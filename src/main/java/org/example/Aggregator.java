package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.example.converter.Converter;
import org.example.converter.JsonConverter;
import org.example.exception.RateLimitException;

import java.io.IOException;
import java.util.*;

public class Aggregator {
    private static final ArrayNode data = Converter.getMapper().createArrayNode();
    private static int currentId = 0;

    public static Optional<String> sendRequest(API api) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(api.getUrl()).build();
            return httpClient.execute(httpGet, response -> {
                System.out.println(api.name().toLowerCase() + " " + response.getCode() + " " + response.getReasonPhrase());
                if (response.getCode() >= 200 && response.getCode() < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? Optional.of(EntityUtils.toString(entity)) : Optional.empty();
                }
                else if(response.getCode() == 429) {
                    throw new RateLimitException("Too many requests sent to " + api.name().toLowerCase());
                }
                return Optional.empty();
            });
        }catch(RateLimitException e){
            System.err.println(e.getMessage());
            throw e;
        } catch(IOException e){
            System.err.println("Connection error while sending request to " + api.name().toLowerCase());
            throw e;
        }
    }

     public static void saveData(String body, API api) {
        int newId = currentId + 1;
        Optional<JsonNode> node = JsonConverter.convertToJsonNode(body, newId, api);
        if(node.isPresent()){
            currentId = newId;
            data.add(node.get());
        }
     }

     public static ArrayNode getData() {
        return data;
     }

}
