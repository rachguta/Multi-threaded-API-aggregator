package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Converter {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static Optional<JsonNode> convertToJsonNode(String body, int id, API api) {
        try {
            if(body == null) return Optional.empty();
            JsonNode data = mapper.readTree(body);
            ObjectNode nodeWithMetaData = mapper.createObjectNode();
            nodeWithMetaData.put("id", id);
            nodeWithMetaData.put("source", api.name().toLowerCase());
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            String timestamp = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            nodeWithMetaData.put("timestamp", timestamp);
            nodeWithMetaData.set("data", data);
            return Optional.of(nodeWithMetaData);
        }
         catch(JsonProcessingException e){
            System.err.println("Failed to create java object from response body of " + api.name().toLowerCase());
            return Optional.empty();
         }
    }


    public static List<Map<String, String>> convertToRows(ArrayNode records) {
        List<Map<String, String>> allRows = new ArrayList<>();

        if(records == null) {
            System.err.println("Data is null, cannot convert to rows.");
            return allRows;
        }

        for (JsonNode record : records) {
            Map<String, String> metadata = new LinkedHashMap<>();

            String id = record.path("id").asText("");
            String source = record.path("source").asText("");
            String timestamp = record.path("timestamp").asText("");

            metadata.put("id", id);
            metadata.put("source", source);
            metadata.put("timestamp", timestamp);

            JsonNode data = record.path("data");

            List<Map<String, String>> dataRows = expand(data, source);

            for (Map<String, String> dataRow : dataRows) {
                Map<String, String> fullRow = new LinkedHashMap<>(metadata);
                fullRow.putAll(dataRow);
                allRows.add(fullRow);
            }

        }
        return allRows;
    }

    private static List<Map<String, String>> expand(JsonNode node, String prefix) {
        List<Map<String, String>> result = new ArrayList<>();
        result.add(new LinkedHashMap<>());

        if (node.isObject()) {
            for (Map.Entry<String, JsonNode> entry : node.properties()) {
                String key = entry.getKey();
                String newPrefix = prefix.isEmpty() ? key : prefix + "." + key;
                List<Map<String, String>> fieldRows = expand(entry.getValue(), newPrefix);
                crossJoin(result, fieldRows);
            }
        } else if (node.isArray()) {
            if (node.isEmpty()) {
                for (Map<String, String> row : result) {
                    row.put(prefix, "");
                }
            } else {
                List<Map<String, String>> arrayRows = new ArrayList<>();
                for (JsonNode item : node) {
                    arrayRows.addAll(expand(item, prefix));
                }
                crossJoin(result, arrayRows);
            }
        } else {
            String value = node.isNull() ? "" : node.asText();
            for (Map<String, String> row : result) {
                row.put(prefix, value);
            }
        }
        return result;
    }

    private static void crossJoin(List<Map<String, String>> result, List<Map<String, String>> fieldRows) {
        if (result.isEmpty()) return;
        if (fieldRows.isEmpty()) return;

        List<Map<String, String>> joined = new ArrayList<>(result.size() * fieldRows.size());
        for (Map<String, String> l : result) {
            for (Map<String, String> r : fieldRows) {
                Map<String, String> merged = new LinkedHashMap<>(l);
                merged.putAll(r);
                joined.add(merged);
            }
        }
        result.clear();
        result.addAll(joined);
    }

}
