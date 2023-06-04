package org.freeshr.identity.repository;

import com.fasterxml.jackson.databind.annotation.JsonAppend;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    protected Properties loadProperties(String propFileName) {
        Properties properties = new Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        try {
            if (inputStream != null)
                properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("property file '" + propFileName + "' not found in the classpath");
        }
        return properties;
    }

    protected Properties loadPropertiesFromFile(String propFileFullPath){
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(propFileFullPath))
        {
            properties.load(inputStream);
        }
        catch (IOException exc){
            System.out.println("property file '" + propFileFullPath + "' not found.");
        }
        return properties;
    }
}
