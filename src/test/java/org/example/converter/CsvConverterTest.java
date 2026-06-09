package org.example.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Csv Converter Testing")
class CsvConverterTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldReturnEmptyList_whenRecordsAreNull() {
        List<Map<String, String>> result = CsvConverter.convertToRows(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldExtractMetadataAndFlatData_whenRecordIsSimple(){
        String json = "{" +
                "\"id\": 10," +
                "\"source\": \"steam\"," +
                "\"timestamp\": \"2023-10-01\"," +
                "\"data\": {\"game\": \"gta\", \"price\": 30}" +
                "}";
        JsonNode record = assertDoesNotThrow(() -> mapper.readTree(json));

        List<Map<String, String>> result = CsvConverter.convertToRows(record);

        assertEquals(1, result.size());
        Map<String, String> row = result.getFirst();

        assertAll(() -> assertEquals("10", row.get("id")),
                () -> assertEquals("steam", row.get("source")),
                () -> assertEquals("2023-10-01", row.get("timestamp")),
                () -> assertEquals("gta", row.get("steam.game")),
                () -> assertEquals("30", row.get("steam.price"))
        );
    }

    @Test
    void shouldGenerateMultipleRows_whenDataContainsArray() {
        String json = "{" +
                "\"id\": 5," +
                "\"source\": \"test\"," +
                "\"timestamp\": \"T0\"," +
                "\"data\": {\"tags\": [\"red\", \"blue\"]}" +
                "}";
        JsonNode record = assertDoesNotThrow(() -> mapper.readTree(json));

        List<Map<String, String>> result = CsvConverter.convertToRows(record);

        assertEquals(2, result.size());

        assertAll(() -> assertEquals("5", result.getFirst().get("id")),
                () -> assertEquals("test", result.getFirst().get("source")),
                () -> assertEquals("T0", result.getFirst().get("timestamp")),
                () -> assertEquals("red", result.getFirst().get("test.tags")),
                () -> assertEquals("5", result.get(1).get("id")),
                () -> assertEquals("test", result.get(1).get("source")),
                () -> assertEquals("T0", result.get(1).get("timestamp")),
                () -> assertEquals("blue", result.get(1).get("test.tags"))
        );
    }

}