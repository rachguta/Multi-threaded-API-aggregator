package org.example.format;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.example.API;
import org.example.exceptions.FileProcessingException;

public abstract class FileFormat {
    protected String dirName = "results";

    public abstract void writeToNewFile(String fileName, ArrayNode data);

    public abstract void writeToExistingFile(String fileName, ArrayNode data)  throws FileProcessingException;

    public abstract void printAll(String fileName);

    public abstract void printByApi(String fileName, API api);

}
