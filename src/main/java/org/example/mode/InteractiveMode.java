package org.example.mode;

import org.example.API;
import org.example.Aggregator;
import org.example.CliManager;
import org.example.exception.FileProcessingException;
import org.example.filemanager.CsvFileManager;
import org.example.filemanager.JsonFileManager;


import java.util.NoSuchElementException;

public class InteractiveMode extends Mode {
    String fileMode;
    String outputMode;

    @Override
    public void start() throws NoSuchElementException, FileProcessingException {
        System.out.println("Interactive Mode");
        apis = CliManager.getInstance().readApis();
        formatName = CliManager.getInstance().readFormat();
        maxNumOfTasks = CliManager.getInstance().readMaxNumOfTasks();
        apiInterval = CliManager.getInstance().readApiInterval();
        fileMode = CliManager.getInstance().readFileMode();
        fileName = CliManager.getInstance().readFileName();

        if(formatName.equals("json")) {
            fileManager = new JsonFileManager("results");
        }
        else{
            fileManager = new CsvFileManager("results");
        }

        if(fileMode.equals("create")){
            fileManager.createNewFile(fileName + "." + formatName);
        }

        for(API api : apis){
            if(!api.getParameterName().isEmpty()) {
                String parameter = CliManager.getInstance().readParameter(api);
                api.createUrlWithParameter(parameter);
            }
        }

        CliManager.getInstance().readStartingPolling();

        startParallelPolling(apis);

        writeToConsole();
    }


    void writeToConsole() throws FileProcessingException {
        outputMode = CliManager.getInstance().readOutputMode();

        if(outputMode.equals("fully")){
            fileManager.printAll(fileName + "." + formatName);
        }
        else{
            API outputApi = CliManager.getInstance().readApi();
            fileManager.printByApi(fileName + "." + formatName, outputApi);
        }
    }
}
