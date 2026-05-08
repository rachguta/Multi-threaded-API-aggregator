package org.example.mode;
import org.example.exception.FileProcessingException;
import org.example.exception.NoDataException;

import java.util.NoSuchElementException;

public interface Mode {
    void start() throws IllegalArgumentException, NoSuchElementException, NoDataException, FileProcessingException;
}
