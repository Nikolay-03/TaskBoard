package com.example.kanban.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> T readJson(InputStream is, Class<T> clazz) throws IOException {
        return MAPPER.readValue(is, clazz);
    }

    public static void writeJson(OutputStream os, Object value) throws IOException {
        MAPPER.writeValue(os, value);
    }
}
