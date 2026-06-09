package org.example.parallel;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.API;
import org.example.Aggregator;
import org.example.exception.FileProcessingException;
import org.example.exception.NoDataException;
import org.example.filemanager.FileManager;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

public class ApiPolling implements Runnable {
    Lock locker;
    API api;
    String fileName;
    FileManager fileManager;
    ScheduledFuture<?> future;
    Aggregator aggregator;
    boolean locked = false;
    public ApiPolling(Lock locker, API api, String fileName, FileManager fileManager, Aggregator aggregator) {
        this.locker = locker;
        this.api = api;
        this.fileName = fileName;
        this.fileManager = fileManager;
        this.aggregator = aggregator;
    }

    @Override
    public void run() {
        try {
            Optional<String> stringResponse = aggregator.sendRequest(api);
            Optional<JsonNode> javaResponse = Optional.empty();
            if(stringResponse.isPresent()) {
                locker.lock();
                locked = true;
                javaResponse = aggregator.convertDataToJavaObject(stringResponse.get(), api);
            }
            if(javaResponse.isPresent()) {
                aggregator.saveData(fileName, javaResponse.get(), fileManager);
            }
            else{
                throw new NoDataException("No Data Available");
            }
        }catch(FileProcessingException | NoDataException  e){
            System.err.println(e.getMessage());
        }catch(IOException e){
            System.err.println("Canceling the polling for " + api.name().toLowerCase());
            future.cancel(true);
        }
        finally {
            if(locked){
                locker.unlock();
            }
        }
    }
     public void setScheduledFuture(ScheduledFuture<?> future){
        this.future = future;
     }
}
