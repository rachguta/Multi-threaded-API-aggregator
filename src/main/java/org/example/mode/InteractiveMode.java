package org.example.mode;

import org.example.API;
import org.example.Aggregator;
import org.example.CliManager;
import org.example.exceptions.FileProcessingException;
import org.example.format.CsvFormat;
import org.example.format.FileFormat;
import org.example.format.JsonFormat;

import java.nio.file.Path;
import java.util.NoSuchElementException;

public class InteractiveMode implements Mode {

    @Override
    public void start() throws NoSuchElementException, FileProcessingException {
        System.out.println("Interactive Mode");
        API api;
        String formatName;
        FileFormat fileFormat;
        java.lang.String fileMode;
        java.lang.String outputMode;
        api = CliManager.readApi();
        formatName = CliManager.readFormat();
        if(formatName.equals("json")){
            fileFormat = new JsonFormat();
        }
        else{
            fileFormat = new CsvFormat();
        }
        Aggregator.aggregateData(api);
        fileMode = CliManager.readFileMode();
        String fileName = CliManager.readFileName() + "." + formatName;
        if(fileMode.equals("create")){
            fileFormat.writeToNewFile(fileName, Aggregator.getData());
        }
        else{
            fileFormat.writeToExistingFile(fileName, Aggregator.getData());
        }
        outputMode = CliManager.readOutputMode();
        if(outputMode.equals("fully")){
            fileFormat.printAll(fileName);
        }
        else{
            API outputApi = CliManager.readApi();
            fileFormat.printByApi(fileName, outputApi);
        }

    }
}
