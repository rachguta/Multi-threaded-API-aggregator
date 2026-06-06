package org.example.mode;
import org.example.API;
import org.example.CliManager;
import org.example.exception.FileProcessingException;
import org.example.exception.NoDataException;
import org.example.filemanager.FileManager;
import org.example.parallel.ApiPolling;

import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Mode {
    API[] apis;
    String formatName;
    int maxNumOfTasks;
    long apiInterval;
    FileManager fileManager;
    String fileName;
    Lock aggregationLock = new ReentrantLock();

    abstract public void start() throws IllegalArgumentException, NoSuchElementException, NoDataException, FileProcessingException;

    abstract void writeToFile() throws FileProcessingException;

    void startParallelPolling(API[] apis) throws NoSuchElementException{
        try(ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(maxNumOfTasks)){
            System.out.println("Starting the scheduler...");
            for(API api : apis){
                AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();
                ApiPolling task = new ApiPolling(aggregationLock, api,  futureRef);
                ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(task, 0,
                        apiInterval, TimeUnit.MILLISECONDS);
                futureRef.set(future);
            }
            CliManager.readStoppingPolling();
            System.out.println("Stopping the scheduler...");
        }
    }

}
