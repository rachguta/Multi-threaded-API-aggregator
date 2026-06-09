package org.example.mode;

import org.example.Aggregator;
import org.example.API;
import org.example.CliManager;
import org.example.exception.FileProcessingException;
import org.example.filemanager.CsvFileManager;
import org.example.filemanager.JsonFileManager;
import java.util.List;
import java.util.NoSuchElementException;


public class AutoMode extends Mode{
    String[] args;

    public AutoMode(String[] args) {
        this.args = args;
    }

    @Override
    public void start() throws IllegalArgumentException, NoSuchElementException, FileProcessingException {
        System.out.println("Auto mode");

        List<String> argList = CliManager.removeSeparators(args);
        apis = CliManager.readApisFromArgs(argList);
        formatName = CliManager.readFormatFromArgs(argList);
        maxNumOfTasks = CliManager.readMaxNumOfTasksFromArgs(argList);
        apiInterval = CliManager.readApiIntervalFromArgs(argList);


        if(formatName.equals("json")) {
            fileManager = new JsonFileManager("results");
        }
        else{
            fileManager = new CsvFileManager("results");
        }

        fileManager.createNewFile(fileName + "." + formatName);

        for(API api : apis){
            if(!api.getParameterName().isEmpty()) {
                api.createUrlWithDefaultParameter();
            }
        }

        startParallelPolling(apis);
    }
}
