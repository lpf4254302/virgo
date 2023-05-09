package com.oort.virgo.constant;

import ch.qos.logback.classic.Level;

/**
 * 日志级别
 * @author lpf
 */
public enum LogLevel {

    DEBUG(Level.DEBUG), INFO(Level.INFO), ERROR(Level.ERROR);

    private Level level;

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    LogLevel(Level level) {

        this.level = level;
    }
}
