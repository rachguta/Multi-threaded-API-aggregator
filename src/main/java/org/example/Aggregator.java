package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.example.converter.JsonConverter;
import org.example.exception.FileProcessingException;
import org.example.exception.RateLimitException;
import org.example.filemanager.FileManager;

import java.io.IOException;
import java.util.*;

public class Aggregator {
    private int currentId = 0;

    public Optional<String> sendRequest(API api) throws IOException {
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

     public Optional<JsonNode> convertDataToJavaObject(String body, API api) {
        int newId = currentId + 1;
        Optional<JsonNode> convertedData = JsonConverter.convertToJsonNode(body, newId, api);
        if(convertedData.isPresent()){
            currentId = newId;
        }
        return convertedData;
     }

     public void saveData(String fileName, JsonNode data, FileManager fileManager) throws FileProcessingException {
        fileManager.writeToExistingFile(fileName, data);
     }
}
