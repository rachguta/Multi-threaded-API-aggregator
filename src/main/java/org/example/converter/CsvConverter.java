package org.example.converter;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvConverter extends Converter{
    public static List<Map<String, String>> convertToRows(JsonNode record) {
        List<Map<String, String>> allRows = new ArrayList<>();

        if(record == null) {
            System.err.println("Data is null, cannot convert to rows.");
            return allRows;
        }

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
