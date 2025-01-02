package org.example.server.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.server.Entities.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Optional;

public class RequestUtils {

    public static Optional<User> toUser(Reader reader) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Optional.ofNullable(objectMapper.readValue(reader, User.class));
        } catch (IOException e) {
            System.err.println("Error while mapping to user: " + e.getMessage());
        }
        return Optional.empty();
    }


    public static String[] hashPassword(String password, String saltString) throws Exception {
        byte[] salt = null;

        if (saltString == null) {
            SecureRandom random = new SecureRandom();
            salt = new byte[16];
            random.nextBytes(salt);
        } else  {
            salt = Base64.getDecoder().decode(saltString);
        }


        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();
        return new String[] {Base64.getEncoder().encodeToString(salt), Base64.getEncoder().encodeToString(hash)};
    }

}
