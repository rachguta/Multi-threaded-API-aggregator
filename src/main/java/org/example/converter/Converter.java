package org.example.converter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Converter {
    protected static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getMapper() {
        return mapper;
    }
}
