package org.example.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoDataExceptionTest {
    @Test
    void shouldReturnValidMessage_whenCreated() {
        String msg = "file error";
        NoDataException ex = new NoDataException(msg);

        assertEquals(msg, ex.getMessage());
    }
}