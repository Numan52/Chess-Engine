package org.example.server.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.server.Entities.User;

import java.io.IOException;
import java.io.Reader;
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

}
