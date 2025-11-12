package com.Shinigami_Coderz.ExchangeXP.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.service-account-file:}")
    private String serviceAccountFile;

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options;
                if (serviceAccountFile != null && !serviceAccountFile.trim().isEmpty()) {
                    log.info("Initializing Firebase using service account file: {}", serviceAccountFile);
                    try (InputStream is = new FileInputStream(serviceAccountFile)) {
                        options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(is))
                                .build();
                    }
                } else {
                    // fallback to Application Default Credentials (GOOGLE_APPLICATION_CREDENTIALS env var or cloud environment)
                    log.info("Initializing Firebase using Application Default Credentials");
                    options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.getApplicationDefault())
                            .build();
                }
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp has been initialized");
            }
        } catch (Exception e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}