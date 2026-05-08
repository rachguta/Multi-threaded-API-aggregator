package org.example;
import org.example.exception.FileProcessingException;
import org.example.exception.NoDataException;
import org.example.mode.AutoMode;
import org.example.mode.InteractiveMode;
import org.example.mode.Mode;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Mode mode;
        try {
            if(args.length == 0) {
                mode = new InteractiveMode();
            }
            else{
                mode = new AutoMode(args);
            }
            mode.start();
        }catch (FileProcessingException | IllegalArgumentException | NoDataException e) {
            System.err.println(e.getMessage());
        }
        catch (NoSuchElementException e) {
            System.err.println("Input stream has closed too early");
        }
    }
}