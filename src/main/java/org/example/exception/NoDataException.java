package org.example.exception;

import java.io.IOException;

public class NoDataException extends IOException {
    public NoDataException(String message) {
        super(message);
    }
}
