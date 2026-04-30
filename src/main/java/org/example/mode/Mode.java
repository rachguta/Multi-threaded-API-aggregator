package org.example.mode;

import org.example.exceptions.FileProcessingException;

import java.util.NoSuchElementException;

public interface Mode {
    public void start() throws NoSuchElementException, FileProcessingException;
}
