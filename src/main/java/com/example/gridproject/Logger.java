package com.example.gridproject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {
    private final List<String> logs;

    public Logger() {
        this.logs = new ArrayList<>();
        addLog("Log Started at: " + new Date());
    }

    public void addLog(String message) {
        this.logs.add(message);
    }

    public String getLog() {
        return String.join("\n", this.logs);
    }
}
