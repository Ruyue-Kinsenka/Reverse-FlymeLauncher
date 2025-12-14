package com.android.launcher3;

import java.util.Optional;

public class StatsLogManager {
    public Logger logger() {
        return new Logger();
    }

    public static class Logger {
        public Logger withContainerInfo(Object containerInfo) {
            return this;
        }

        public void log(Object event) {
        }
    }
}