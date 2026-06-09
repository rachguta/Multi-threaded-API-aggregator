package org.example.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitExceptionTest {
    @Test
    void shouldReturnValidMessage_whenCreated() {
        String msg = "file error";
        RateLimitException ex = new RateLimitException(msg);

        assertEquals(msg, ex.getMessage());
    }
}