package org.example.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.example.API;
import org.example.Converter;
import org.example.exceptions.FileProcessingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class JsonFormat extends FileFormat {

    @Override
    public void writeToNewFile(String fileName, ArrayNode data) {
        Path path = Path.of(dirName, fileName);
        try{
            Converter.getMapper().writerWithDefaultPrettyPrinter()
                    .writeValue(path.toFile(), data);
        } catch (IOException e){
            System.err.println("Error writing file " + path.toString());
        }
    }

    @Override
    public void writeToExistingFile(String fileName, ArrayNode data) throws FileProcessingException {
        Path path = Path.of(dirName, fileName);
        if(!Files.exists(path)){
            throw new FileProcessingException("File " + path.toString() + " does not exist.");
        }
        try{
            ArrayNode arrayNode;
            if (Files.size(path) > 0) {
                JsonNode existing = Converter.getMapper().readTree(path.toFile());
                if (!existing.isArray()) {
                    System.err.println("File " + path.toString() + " is not a JSON array.");
                    return;
                }
                arrayNode = (ArrayNode) existing;
            } else {
                arrayNode = Converter.getMapper().createArrayNode();
            }
            arrayNode.addAll(data);
            Converter.getMapper().writerWithDefaultPrettyPrinter().writeValue(path.toFile(), arrayNode);
        } catch(IOException e){
            System.err.println("Error writing file " + path.toString());
        }
    }

    @Override
    public void printAll(String fileName) {
        Path filePath = Path.of(dirName, fileName);
        try {
            ArrayNode records = (ArrayNode) Converter.getMapper().readTree(filePath.toFile());
            for (JsonNode record : records) {
                System.out.println(Converter.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(record));
            }
        } catch (IOException e){
            System.err.println("Error reading file " + filePath);
        }
    }

    @Override
    public void printByApi(String fileName, API api) {
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
                System.out.println("️ There is no records for "+ api.name());
            }
        } catch (IOException e){
            System.err.println("Error reading file " + filePath);
        }
    }
}
