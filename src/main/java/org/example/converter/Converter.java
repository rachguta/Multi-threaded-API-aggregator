package org.example.converter;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Converter {
    static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getMapper() {
        return mapper;
    }
}
