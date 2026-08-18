package org.example.filemanager;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.API;
import org.example.exception.FileProcessingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class FileManager {
    protected String dirName;

    protected FileManager(String dirName){
        this.dirName = dirName;
    }
    public abstract void writeToExistingFile(String fileName, JsonNode data) throws FileProcessingException;

    public abstract void printAll(String fileName) throws FileProcessingException;

    public abstract void printByApi(String fileName, API api) throws FileProcessingException;

    public void createNewFile(String fileName) throws FileProcessingException {
        try{
            Path path = Path.of(dirName, fileName);
            Files.newOutputStream(
                    path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            ).close();
        }catch(InvalidPathException e){
            throw new FileProcessingException("Invalid file name: " + fileName);
        }
        catch (IOException e){
            throw new FileProcessingException("Error creating file " + fileName);
        }
    }

}
