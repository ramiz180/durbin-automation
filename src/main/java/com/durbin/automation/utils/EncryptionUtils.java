package com.durbin.automation.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for AES-256-GCM encryption of document content.
 * Uses the DOCUMENT_ENCRYPTION_KEY from the .env file.
 */
public class EncryptionUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int IV_LENGTH = 12; // bytes â€” recommended for GCM

    /**
     * Encrypts the given plaintext string using AES-256-GCM.
     *
     * @param plaintext the string to encrypt (e.g., base64-encoded PDF content)
     * @return a String array where [0] = base64-encoded encrypted data, [1] = base64-encoded IV
     * @throws Exception if encryption fails or the key is not configured
     */
    public static String[] encrypt(String plaintext) throws Exception {
        String keyBase64 = EnvReader.get("DOCUMENT_ENCRYPTION_KEY");
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new RuntimeException("DOCUMENT_ENCRYPTION_KEY is not configured in .env");
        }

        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        if (keyBytes.length != 32) {
            throw new RuntimeException("DOCUMENT_ENCRYPTION_KEY must be a base64-encoded 32-byte key for AES-256, got " + keyBytes.length + " bytes");
        }
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

        // Generate a random 12-byte IV for each encryption
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
        String ivBase64 = Base64.getEncoder().encodeToString(iv);

        return new String[]{encryptedBase64, ivBase64};
    }
}

