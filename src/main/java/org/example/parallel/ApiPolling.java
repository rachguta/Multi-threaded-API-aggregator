package org.example.parallel;

import org.example.API;
import org.example.Aggregator;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

public class ApiPolling implements Runnable {
    Lock locker;
    API api;
    AtomicReference<ScheduledFuture<?>> futureRef;
    boolean locked = false;
    public ApiPolling(Lock locker, API api, AtomicReference<ScheduledFuture<?>> futureRef) {
        this.locker = locker;
        this.api = api;
        this.futureRef = futureRef;
    }

    @Override
    public void run() {
        try {
            Optional<String> response = Aggregator.sendRequest(api);
            locker.lock();
            locked = true;
            response.ifPresent(s -> Aggregator.saveData(s, api));
        }catch (IOException e){
            System.err.println("Canceling the polling for " + api.name().toLowerCase());
            futureRef.get().cancel(true);
        }
        finally {
            if(locked){
                locker.unlock();
            }
        }
    }
}
