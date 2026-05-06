package org.example;
import org.example.exception.FileProcessingException;
import org.example.exception.NoDataException;
import org.example.mode.Mode;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Mode mode = CliManager.readMode();
            mode.start();
        }catch (FileProcessingException | NoDataException e) {
            System.err.println(e.getMessage());
        }
        catch (NoSuchElementException e) {
            System.err.println("Input stream has closed too early");
        }
    }
}