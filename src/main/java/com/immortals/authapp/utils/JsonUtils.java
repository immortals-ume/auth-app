package com.immortals.authapp.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.immortals.authapp.service.exception.AuthException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Exclude nulls
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignore unknown
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // ISO format
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print (can be turned off)

        // Register modules for Java 8 compatibility
        OBJECT_MAPPER.registerModule(new Jdk8Module());
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }

    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new AuthException("Serialization failed", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new AuthException("Deserialization failed", e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return OBJECT_MAPPER.readValue(json, typeRef);
        } catch (IOException e) {
            throw new AuthException("Deserialization failed", e);
        }
    }
}
