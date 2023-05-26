package com.ivan4oto.app.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

@RestController
public class AuthController {
    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @PostMapping("/api/auth/google")
    public ResponseEntity<?> authenticateGoogleUser(@RequestBody Map<String, String> payload) {
        String idTokenString = payload.get("idTokenString");
        try {
            Boolean userData = verifyGoogleIdToken(idTokenString);
            if (userData == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // Save user data to the database and create a session

            return ResponseEntity.ok("User is valid");
        } catch (IOException e) {
            throw new RuntimeException("Error authenticating Google user", e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private Boolean verifyGoogleIdToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

// (Receive idTokenString by HTTPS POST)

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            // Use or store profile information
            // ...
            return true;
        } else {
            System.out.println("Invalid ID token.");
            return false;
        }

    }
}