package com.example.neurosense;

import android.os.Build;

import java.util.Locale;

public class DeviceDetector {
    private String device;

    public DeviceDetector() {
        this.device = detectDevice();
    }

    private String detectDevice() {
        if (isEmulator()) {
            return "Android Emulator";
        } else if (isBuddyRobot()) {
            return "BUDDY Robot";
        } else {
            return "Unknown";
        }
    }

    private boolean isEmulator() {
        return "google_sdk".equals(Build.PRODUCT);
    }

    private boolean isBuddyRobot() {
        // Add specific logic to detect the BUDDY Robot
        // Example: Check for a unique identifier or specific system properties of the BUDDY Robot
        return false; // Replace with your BUDDY Robot detection logic
    }

    public String getDevice() {
        return device;
    }

    public static void main(String[] args) {
        DeviceDetector detector = new DeviceDetector();
        String deviceType = detector.getDevice();
        System.out.println("Detected device: " + deviceType);
    }
}
