package org.example.mode;

import org.example.API;
import org.example.Aggregator;
import org.example.CliManager;
import org.example.exception.FileProcessingException;
import org.example.exception.NoDataException;
import org.example.filemanager.CsvFileManager;
import org.example.filemanager.FileManager;
import org.example.filemanager.JsonFileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class InteractiveMode extends Mode {
    String fileMode;
    String outputMode;

    @Override
    public void start() throws NoSuchElementException, NoDataException, FileProcessingException {
        System.out.println("Interactive Mode");
        apis = CliManager.readApis();
        formatName = CliManager.readFormat();
        maxNumOfTasks = CliManager.readMaxNumOfTasks();
        apiInterval = CliManager.readApiInterval();
        for(API api : apis){
            if(!api.getParameterName().isEmpty()) {
                String parameter = CliManager.readParameter(api);
                api.createUrlWithParameter(parameter);
            }
        }

        CliManager.readStartingPolling();

        startParallelPolling(apis);

        if(Aggregator.getData().isEmpty()){
            throw new NoDataException("No data was aggregated from the APIs");
        }

        writeToFile();

        writeToConsole();
    }

    @Override
    void writeToFile() throws FileProcessingException {
        if(formatName.equals("json")){
            fileManager = new JsonFileManager();
        }
        else{
            fileManager = new CsvFileManager();
        }

        fileMode = CliManager.readFileMode();

        fileName = CliManager.readFileName() + "."  + formatName;

        if(fileMode.equals("create")){
            fileManager.writeToNewFile(fileName, Aggregator.getData());
        }
        else{
            fileManager.writeToExistingFile(fileName, Aggregator.getData());
        }
    }

    void writeToConsole() throws FileProcessingException {
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
