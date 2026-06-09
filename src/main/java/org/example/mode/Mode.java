package org.example.mode;
import org.example.API;
import org.example.Aggregator;
import org.example.exception.FileProcessingException;
import org.example.filemanager.FileManager;
import org.example.parallel.ExecutionManager;

import java.util.NoSuchElementException;

public abstract class Mode {
    API[] apis;
    String formatName;
    int maxNumOfTasks;
    long apiInterval;
    FileManager fileManager;
    String fileName = "result";
    Aggregator aggregator = new Aggregator();
    abstract public void start() throws IllegalArgumentException, NoSuchElementException, FileProcessingException;

    void startParallelPolling(API[] apis) throws NoSuchElementException{
        ExecutionManager executionManager = new ExecutionManager();
        executionManager.startParallelPolling(apis, maxNumOfTasks, apiInterval, fileName + "." + formatName, fileManager, aggregator);
    }

}
