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

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options;

                // 1) Prefer explicit path set by our startup script: FIREBASE_JSON
                String firebaseJsonPath = System.getenv("FIREBASE_JSON");

                // 2) Fallback to GOOGLE_APPLICATION_CREDENTIALS if set
                String googleCreds = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

                if (firebaseJsonPath != null && !firebaseJsonPath.trim().isEmpty()) {
                    log.info("Initializing Firebase using FIREBASE_JSON file: {}", firebaseJsonPath);
                    try (InputStream is = new FileInputStream(firebaseJsonPath)) {
                        options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(is))
                                .build();
                    }
                } else if (googleCreds != null && !googleCreds.trim().isEmpty()) {
                    log.info("Initializing Firebase using GOOGLE_APPLICATION_CREDENTIALS: {}", googleCreds);
                    try (InputStream is = new FileInputStream(googleCreds)) {
                        options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(is))
                                .build();
                    }
                } else {
                    // final fallback: application default creds (works on GCP platforms)
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