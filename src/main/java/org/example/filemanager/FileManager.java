package org.example.filemanager;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.example.API;
import org.example.exception.FileProcessingException;

public abstract class FileManager {
    protected String dirName = "results";

    public abstract void writeToNewFile(String fileName, ArrayNode data) throws FileProcessingException;

    public abstract void writeToExistingFile(String fileName, ArrayNode data) throws FileProcessingException;

    public abstract void printAll(String fileName) throws FileProcessingException;

    public abstract void printByApi(String fileName, API api) throws FileProcessingException;

}
