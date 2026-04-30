package org.example.exceptions;

import java.io.IOException;

public class FileProcessingException extends IOException {
    public FileProcessingException(String message) {
        super(message);
    }
}
