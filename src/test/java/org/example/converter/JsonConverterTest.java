package org.example.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.API;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonConverterTest {
    @Test
    void shouldReturnEmpty_whenBodyIsNull(){
        Optional<JsonNode> res = JsonConverter.convertToJsonNode(null, 1, API.STEAM);
        assertTrue(res.isPresent());
    }



}