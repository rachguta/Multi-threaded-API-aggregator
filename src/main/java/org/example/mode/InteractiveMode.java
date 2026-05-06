package org.example.mode;

import org.example.API;
import org.example.Aggregator;
import org.example.CliManager;
import org.example.exception.FileProcessingException;
import org.example.exception.NoDataException;
import org.example.format.CsvFileManager;
import org.example.format.FileManager;
import org.example.format.JsonFileManager;
import java.util.NoSuchElementException;

public class InteractiveMode implements Mode {

    @Override
    public void start() throws NoSuchElementException, NoDataException, FileProcessingException {
        System.out.println("Interactive Mode");
        API api;
        String formatName;
        FileManager fileManager;
        String fileMode;
        String outputMode;
        api = CliManager.readApi();
        formatName = CliManager.readFormat();

        if(formatName.equals("json")){
            fileManager = new JsonFileManager();
        }
        else{
            fileManager = new CsvFileManager();
        }

        Aggregator.aggregateData(api);

        if(Aggregator.getData().isEmpty()){
            throw new NoDataException("No data was aggregated from the API");
        }

        fileMode = CliManager.readFileMode();
        String fileName = CliManager.readFileName() + "." + formatName;

        if(fileMode.equals("create")){
            fileManager.writeToNewFile(fileName, Aggregator.getData());
        }
        else{
            fileManager.writeToExistingFile(fileName, Aggregator.getData());
        }

        outputMode = CliManager.readOutputMode();

        if(outputMode.equals("fully")){
            fileManager.printAll(fileName);
        }
        else{
            API outputApi = CliManager.readApi();
            fileManager.printByApi(fileName, outputApi);
        }
    }
}
