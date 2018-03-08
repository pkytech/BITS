/*
 * Copyright (c) 2018.
 * BITS Dissertation Proof Concept. Not related to any organization
 */
package edu.bits.mtech.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service for converting Object to JSON and vise-a-versa.
 *
 * @author Tushar Phadke
 */
public class JsonConverterImpl implements JsonConverter {

    private ObjectMapper objectMapper = new ObjectMapper();

    public <T> T deserialize(Class<T> type, String message)  {

        try {
            return objectMapper.readValue(message, type);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to convert JSON to OBject", e);
        }
    }

    /**
     * Serializer to convert object to string.
     *
     * @param data data which needs to be Serializer
     * @return
     */
    public String serialize(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to convert Object to JSON", e);
        }
    }
}
