package com.ceygreen.iot.firebase;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Firebase Realtime Database settings loaded from {@code application.yml}.
 * Credentials file must stay out of git (see {@code iot-service/.gitignore}).
 */
@ConfigurationProperties(prefix = "ceygreen.firebase")
public class FirebaseProperties {

    /**
     * When false, the service uses an in-memory store so local demo works
     * without a Firebase project yet.
     */
    private boolean enabled = false;

    private String projectId = "";
    private String databaseUrl = "";
    private String credentialsPath = "";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getCredentialsPath() {
        return credentialsPath;
    }

    public void setCredentialsPath(String credentialsPath) {
        this.credentialsPath = credentialsPath;
    }
}
