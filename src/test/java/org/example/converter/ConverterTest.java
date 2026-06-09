package org.example.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTest {
    @Test
    void getMapper_shouldReturnSameInstance() {
        ObjectMapper mapper1 = Converter.getMapper();
        ObjectMapper mapper2 = Converter.getMapper();

        assertAll(() -> assertNotNull(mapper1),
                () -> assertNotNull(mapper2),
                () -> assertSame(mapper1, mapper2));
    }

}