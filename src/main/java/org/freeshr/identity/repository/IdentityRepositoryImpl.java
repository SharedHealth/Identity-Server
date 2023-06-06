package org.freeshr.identity.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.freeshr.identity.model.PatientCreds;
import org.freeshr.identity.model.UserCredentials;
import org.freeshr.identity.model.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Component("identity-impl")
public class IdentityRepositoryImpl extends PropertyReader implements IdentityRepository {

    private String USER_DETAIL_FILE_PATH;

    private String USER_PASSWORD_FILE_PATH;

    private String PATIENT_PORTAL_CLIENT_ID;
    Map<String, String> userPasswords = new HashMap<>();
    Map<String, UserCredentials> sessions = new HashMap<>();
    Map<String, UserInfo> userTokens = new HashMap<>();
    Map<String, String> clients = new HashMap<>();
    private Map<String, String> users = new HashMap<>();

    public IdentityRepositoryImpl(@Value("${USER_DETAIL_FILE_PATH}") String userDetailFilePath,
                                  @Value("${USER_PASSWORD_FILE_PATH}") String userPasswordFilePath,
                                  @Value("${PATIENT_PORTAL_CLIENT_ID}") String patientPortalClientId
    ) throws IOException {
        USER_DETAIL_FILE_PATH = userDetailFilePath;
        USER_PASSWORD_FILE_PATH = userPasswordFilePath;
        PATIENT_PORTAL_CLIENT_ID = patientPortalClientId;
        init();
    }

    private void init() throws IOException {
        loadUserPasswords();
        loadClients();
        loadUserProfiles();
    }

    private void loadClients() {
        Properties properties;
        properties = loadProperties("clients.properties");
        for (String user : properties.stringPropertyNames()) {
            clients.put(user, properties.getProperty(user));
        }
    }

    private void loadUserProfiles() throws IOException {
        Properties properties;
        //properties = loadProperties("userDetail.properties");
        properties = loadPropertiesFromFile(USER_DETAIL_FILE_PATH);
        for (String user : properties.stringPropertyNames()) {
            users.put(user, properties.getProperty(user));
        }
    }

    private void loadUserPasswords() {
        //Properties properties = loadProperties("userPassword.properties");
        Properties properties = loadPropertiesFromFile(USER_PASSWORD_FILE_PATH);
        for (String user : properties.stringPropertyNames()) {
            userPasswords.put(user, properties.getProperty(user));
        }
    }

    @Override
    @Scope
    public String signin(UserCredentials userCredentials) throws IOException {
        return checkUserNameAndPassword(userCredentials) && checkClientIdAndAuthToken(userCredentials) ? getOrCreateSession(userCredentials) : null;
    }

    @Override
    public boolean checkClientIdAndAuthToken(UserCredentials userCredentials) {
        String clientId = userCredentials.getClientId();
        return clients.containsKey(clientId) && clients.get(clientId).equals(userCredentials.getAuthToken());
    }

    private String getOrCreateSession(UserCredentials userCredentials) throws IOException {
        String sessionId = findSessionId(userCredentials);
        if (null != sessionId) {
            return sessionId;
        }
        return createSession(userCredentials);
    }

    private String findSessionId(UserCredentials userCredentials) {
        UserInfo userInfo = userTokens.get(userCredentials.getEmail());
        return userInfo != null ? userInfo.getAccessToken() : null;
    }

    private boolean checkUserNameAndPassword(UserCredentials userCredentials) {
        String name = userCredentials.getEmail();
        return userPasswords.containsKey(name) && userPasswords.get(name).equals(userCredentials.getPassword());
    }

    private String createSession(UserCredentials userCredentials) throws IOException {
        String uuid = UUID.randomUUID().toString();
        sessions.put(uuid, userCredentials);
        String email = userCredentials.getEmail();
        UserInfo userInfo = new ObjectMapper().readValue(users.get(email), UserInfo.class);
        userInfo.setAccessToken(uuid);
        userTokens.put(email, userInfo);
        return uuid;
    }

    @Override
    public UserCredentials getUserByToken(String token) {
        return sessions.get(token);
    }

    @Override
    public UserInfo getUserInfo(String email) {
        return userTokens.get(email);
    }

    @Override
    public void addPatient(PatientCreds patient) {
        final String patientTemplate = """
                {"id": "%s","name": "%s","email": "%s","groups": ["SHR User","MCI User","Patient"],"profiles": [{"name": "patient","id": "%s","catchment": null}]}""";
        String patientData = String.format(patientTemplate, PATIENT_PORTAL_CLIENT_ID, patient.getName(), patient.getHealthId(), patient.getHealthId());
        users.put(patient.getHealthId(), patientData);
        userPasswords.put(patient.getHealthId(), patient.getPassword());
        saveProperties(users, USER_DETAIL_FILE_PATH);
        saveProperties(userPasswords, USER_PASSWORD_FILE_PATH);
    }

    private void saveProperties(Map<String, String> map, String filePath) {
        try (FileWriter fw = new FileWriter(filePath, false)) {
            for (Map.Entry<String, String> keyValue : map.entrySet()) {
                fw.write("%s=%s\n".formatted(keyValue.getKey(), keyValue.getValue()));
            }
        } catch (IOException e) {
            System.out.printf("Error while saving map values into file '%s (%s)", filePath, e.getMessage());
        }
    }
}

