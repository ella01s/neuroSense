package com.example.neurosense.tracking;

public class TimeTracking {
    private long startTime;
    private long endTime;

    public synchronized void start() {
        startTime = System.currentTimeMillis();
    }

    public synchronized void stop() {
        endTime = System.currentTimeMillis();
        //getElapsedTime();
    }

    public synchronized long getElapsedTime() {
        return endTime - startTime;
    }

    public void timeThreshold(){

    }

}


