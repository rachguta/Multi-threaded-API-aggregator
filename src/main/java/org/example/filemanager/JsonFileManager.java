package org.example.filemanager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.example.API;
import org.example.converter.Converter;
import org.example.exception.FileProcessingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class JsonFileManager extends FileManager {

    public JsonFileManager(String dirName){
        super(dirName);
    }
    @Override
    public void writeToExistingFile(String fileName, JsonNode data) throws FileProcessingException {
        Path path = Path.of(dirName, fileName);
        if(!Files.exists(path)){
            createNewFile(fileName);
        }
        try{
            ArrayNode arrayNode;
            if (Files.size(path) > 0) {
                JsonNode existing = Converter.getMapper().readTree(path.toFile());
                if (!existing.isArray()) {
                    throw new IOException();
                }
                arrayNode = (ArrayNode) existing;
            } else {
                arrayNode = Converter.getMapper().createArrayNode();
            }
            arrayNode.add(data);
            Converter.getMapper().writerWithDefaultPrettyPrinter().writeValue(path.toFile(), arrayNode);
        } catch(IOException e){
            throw new FileProcessingException("Error writing file " + path);
        }
    }

    @Override
    public void printAll(String fileName) throws FileProcessingException {
        Path filePath = Path.of(dirName, fileName);
        try {
            JsonNode root = Converter.getMapper().readTree(filePath.toFile());
            if(!root.isArray()){
                throw new FileProcessingException("Invalid JSON format in file " + filePath + ": expected an array of records");
            }
            ArrayNode records = (ArrayNode) root;
            for (JsonNode record : records) {
                System.out.println(Converter.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(record));
            }
        }catch(FileProcessingException e){
            throw e;
        }
        catch (IOException e){
            throw new FileProcessingException("Error reading file " + filePath);
        }
    }

    @Override
    public void printByApi(String fileName, API api) throws FileProcessingException {
        Path filePath = Path.of(dirName, fileName);
        boolean found = false;
        try {
            ArrayNode records = (ArrayNode) Converter.getMapper().readTree(filePath.toFile());
            for (JsonNode record : records) {
                if(record.get("source").asText().equalsIgnoreCase(api.name())){
                    System.out.println(Converter.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(record));
                    found = true;
                }
            }
            if(!found){
                System.err.println("No records found for API " + api.name() + " in file " + filePath);
            }
        }catch (IOException e){
            throw new FileProcessingException("Error reading file " + filePath);
        }
    }
}
