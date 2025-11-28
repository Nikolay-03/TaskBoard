package com.example.kanban.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JsonUtils {
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static <T> T readJson(InputStream is, Class<T> clazz) throws IOException {
        return MAPPER.readValue(is, clazz);
    }

    public static void writeJson(OutputStream os, Object value) throws IOException {
        MAPPER.writeValue(os, value);
    }
}
