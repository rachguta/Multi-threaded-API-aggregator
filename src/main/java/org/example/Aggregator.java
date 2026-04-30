package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.*;

public class Aggregator {
    private static final ArrayNode data = Converter.createArrayNode();
    private static int currentId = 0;
    private static final String directory = "results";

    public static void aggregateData(API api) {
        String parameter= CliManager.readParameter(api);
        String url = api.getUrl() + URLEncoder.encode(parameter, StandardCharsets.UTF_8);
        Optional<String> response = sendRequest(api, url);
        response.ifPresent(s -> saveData(s, api));
    }

    public static Optional<String> sendRequest(API api, String url) {
        RequestConfig config = RequestConfig.custom()
                .setResponseTimeout(Timeout.ofSeconds(10))
                .build();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                        .setDefaultRequestConfig(config).build() ) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url).build();
            return httpClient.execute(httpGet, response -> {
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                if (response.getCode() >= 200 && response.getCode() < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? Optional.of(EntityUtils.toString(entity)) : Optional.empty();
                }
                return Optional.empty();
            });
        } catch (SocketTimeoutException e) {
            System.err.println("️Timeout when accessing the к " + api.name().toLowerCase());
            return Optional.empty();
        }catch (IOException e) {
            System.err.println("Connection error with " + api.name().toLowerCase());
            return Optional.empty();
        } catch(IllegalArgumentException e) {
            System.err.println("Illegal URL syntax for " + api.name().toLowerCase());
            return Optional.empty();
        }
    }

     private static void saveData(String body, API api) {
        Optional<JsonNode> node = Converter.convertToJsonNode(body, ++currentId, api);
        node.ifPresent(data::add);
     }

     public static ArrayNode getData() {
        return data;
     }




}
