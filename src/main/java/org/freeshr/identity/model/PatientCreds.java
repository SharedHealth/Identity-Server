package org.freeshr.identity.model;

public class PatientCreds
{
    private String healthId;
    private String password;
    private String name;

    public PatientCreds(String name, String password, String healthId) {
        this.healthId = healthId;
        this.password = password;
        this.name = name;
    }

    public String getHealthId()
    {
        return healthId;
    }

    public void setHealthId(String healthId)
    {
        this.healthId = healthId;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
