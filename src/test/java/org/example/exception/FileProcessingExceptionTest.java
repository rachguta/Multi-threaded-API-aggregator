// java
package org.example.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessingExceptionTest {

    @Test
    void shouldReturnValidMessage_whenCreated() {
        String msg = "file error";
        FileProcessingException ex = new FileProcessingException(msg);

        assertEquals(msg, ex.getMessage());
    }
}
