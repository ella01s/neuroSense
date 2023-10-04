package com.example.neurosense.tracking;


import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogFileManager {

    public static void writeTimestampToFile(long timestamp, String sessionID) {
        File logDir = new File(Environment.getExternalStorageDirectory(), "neuroSense/Training Data/timestamps");
        if (!logDir.exists() && !logDir.mkdirs()) {
            // Failed to create directories
            return;
        }

        String logFileName = sessionID + "_timestamps.log";
        File logFile = new File(logDir, logFileName);

        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(timestamp + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

