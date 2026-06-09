// java
package org.example.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.API;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonConverterTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final API api = API.STEAM;

    @Test
    void shouldReturnEmpty_WhenBodyIsNull() {
        Optional<JsonNode> res = JsonConverter.convertToJsonNode(null, 1, api);
        assertTrue(res.isEmpty());
    }

    @Test
    void shouldReturnEmpty_whenBodyIsInvalid() {
        Optional<JsonNode> res = JsonConverter.convertToJsonNode("not a json", 1, api);
        assertTrue(res.isEmpty());
    }

    @Test
    void shouldWrapWithMetadata_WhenBodyIsValidJsonObject(){
        String body = "{\"field_1\":\"bar\",\"field_2\":5}";
        int id = 1;

        Optional<JsonNode> res = JsonConverter.convertToJsonNode(body, id, api);

        assertTrue(res.isPresent());
        JsonNode root = res.get();
        assertAll(() -> assertEquals(id, root.get("id").asInt()),
                () -> assertEquals(api.name().toLowerCase(), root.get("source").asText()),
                () -> assertTrue(root.has("timestamp")),
                () -> assertTrue(root.has("data")),
                () -> assertDoesNotThrow(() -> mapper.readTree(body)),
                () -> assertEquals(mapper.readTree(body), root.get("data"))
        );
    }

    @Test
    void shouldWrapWithMetadata_WhenBodyIsValidJsonArray() {
        String body = "[{\"field_1\":1}, {\"field_1\":2}]";
        int id = 7;

        Optional<JsonNode> result = JsonConverter.convertToJsonNode(body, id, api);

        assertTrue(result.isPresent());
        JsonNode root = result.get();

        assertAll(() -> assertEquals(id, root.get("id").asInt()),
                () -> assertTrue(root.get("data").isArray()),
                () -> assertEquals(2, root.get("data").size())
        );
    }
}
