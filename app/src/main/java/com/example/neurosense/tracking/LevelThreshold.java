package com.example.neurosense.tracking;

import java.util.ArrayList;
import java.util.List;

public class LevelThreshold {
    private int level;
    private long thresholdMillis;
    public LevelThreshold(int level, long thresholdMillis) {
        this.level = level;
        this.thresholdMillis = thresholdMillis;
    }

    public int getLevel() {
        return level;
    }

    public long getThresholdMillis() {
        return thresholdMillis;
    }

    //initialize the level thresholds
    private static List<LevelThreshold> levelThresholds = new ArrayList<>();
    static {
        levelThresholds.add(new LevelThreshold(1, 13830));
        levelThresholds.add(new LevelThreshold(2, 14288));
        levelThresholds.add(new LevelThreshold(3, 16000));
        levelThresholds.add(new LevelThreshold(4, 18000));
        levelThresholds.add(new LevelThreshold(5, 20000));
        levelThresholds.add(new LevelThreshold(6, 22000));
        levelThresholds.add(new LevelThreshold(7, 24000));
        levelThresholds.add(new LevelThreshold(8, 26000));
        levelThresholds.add(new LevelThreshold(9, 28000));
        levelThresholds.add(new LevelThreshold(10, 30000)); //30 seconds


        // to modify the threshold values
        // levelThresholds.get(index).setThresholdMillis(newThresholdMillis);
    }

    public static long getThresholdMillisForLevel(int level) {
        for (LevelThreshold levelThreshold : levelThresholds) {
            if (levelThreshold.getLevel() == level) {
                return levelThreshold.getThresholdMillis();
            }
        }
        // Return default threshold if level is not found
        return 60000; // 1 minute (default threshold for other levels)
    }
}

