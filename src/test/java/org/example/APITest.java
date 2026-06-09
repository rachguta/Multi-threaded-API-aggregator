package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class APITest {

    API apiWithParameter;
    API apiWithoutParameter;

    @Test
    void getters_shouldReturnConstructorValues() {
        apiWithParameter = API.STEAM;
        assertAll(() -> assertEquals("https://store.steampowered.com/api/salepage/?id=", apiWithParameter.getUrl()),
                () -> assertEquals("franchise name", apiWithParameter.getParameterName()),
                () -> assertEquals("(separate words using \"-\")", apiWithParameter.getComment()));
    }

    @Test
    void createUrlWithDefaultParameter_shouldAppendDefaultValueToUrl() {
        apiWithParameter = API.SPACEX;
        String originalUrl = apiWithParameter.getUrl();

        apiWithParameter.createUrlWithDefaultParameter();

        assertEquals(originalUrl + "1", apiWithParameter.getUrl());
    }

    @Test
    void createUrlWithParameter_shouldAppendValueToUrl_whenParameterIsNotNull() {
        apiWithParameter = API.IMDB;
        String originalUrl = apiWithParameter.getUrl();
        String customParam = "inception";

        apiWithParameter.createUrlWithParameter(customParam);

        assertEquals(originalUrl + customParam, apiWithParameter.getUrl());
    }

    @Test
    void createUrlWithParameter_shouldNotChangeUrl_whenParameterIsNull() {
        apiWithoutParameter = API.OPEN_METEO;
        String originalUrl = apiWithoutParameter.getUrl();

        apiWithoutParameter.createUrlWithParameter(null);

        assertEquals(originalUrl, apiWithoutParameter.getUrl());
    }
}