package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AggregatorTest {

    @Test
    void shouldReturnNodeAndIncrementId_whenBodyIsValid() {
        Aggregator aggregator = new Aggregator();
        String body = "{\"field\": \"value\"}";

        Optional<JsonNode> result = assertDoesNotThrow(() -> aggregator.convertDataToJavaObject(body, API.STEAM));

        assertTrue(result.isPresent());
        assertEquals(1, result.get().get("id").asInt());
    }

    @Test
    void shouldReturnEmptyAndKeepId_whenBodyIsInvalid() {
        Aggregator aggregator = new Aggregator();
        String invalidBody = "not json";
        String validBody = "{\"field\": \"value\"}";

        Optional<JsonNode> firstResult = assertDoesNotThrow(() -> aggregator.convertDataToJavaObject(invalidBody, API.STEAM));
        assertTrue(firstResult.isEmpty());

        Optional<JsonNode> secondResult = assertDoesNotThrow(() -> aggregator.convertDataToJavaObject(validBody, API.STEAM));
        assertTrue(secondResult.isPresent());
        assertEquals(1, secondResult.get().get("id").asInt());
    }
}