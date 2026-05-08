package org.example.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.API;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class JsonConverter extends Converter{
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
}
