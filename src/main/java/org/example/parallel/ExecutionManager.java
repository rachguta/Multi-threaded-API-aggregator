package org.example.parallel;

import org.example.API;
import org.example.Aggregator;
import org.example.CliManager;
import org.example.filemanager.FileManager;

import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExecutionManager {
    private final Lock aggregationLock = new ReentrantLock();

    public void startParallelPolling(API[] apis, int maxNumOfTasks, long apiInterval, String fileName,
                                     FileManager fileManager, Aggregator aggregator) throws NoSuchElementException {
        try(ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(maxNumOfTasks)){
            System.out.println("Starting the scheduler...");
            for(API api : apis){
                ApiPolling task = new ApiPolling(aggregationLock, api, fileName, fileManager, aggregator);
                task.setScheduledFuture(scheduler.scheduleWithFixedDelay(task, 0,
                        apiInterval, TimeUnit.MILLISECONDS));
            }
            CliManager.getInstance().readStoppingPolling();
            System.out.println("Stopping the scheduler...");
        }
    }



}
