package org.freeshr.identity.repository;

import org.freeshr.identity.model.PatientCreds;
import org.freeshr.identity.model.UserCredentials;
import org.freeshr.identity.model.UserInfo;

import java.io.IOException;

public interface IdentityRepository
{
    String signin(UserCredentials userCredentials) throws IOException;

    boolean checkClientIdAndAuthToken(UserCredentials userCredentials);

    UserCredentials getUserByToken(String token);

    UserInfo getUserInfo(String email);

    void addPatient(PatientCreds patientCreds);
}
