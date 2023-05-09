package com.oort.virgo.model;

import com.oort.virgo.constant.LogLevel;
import com.oort.virgo.constant.LogRollingType;

import java.io.Serializable;

/**
 * 日志配置模板
 */
public class LogModel implements Serializable {

    /**
     * 日志的名字，决定日志文件的名字
     */
    private String name;

    /**
     * 保存的路径
     */
    private String path;

    /**
     * 是否输出到root log内
     */
    private boolean additivity = false;

    /**
     * 默认是按天切
     */
    private LogRollingType logRollingType = LogRollingType.HOURE;

    /**
     * 保存（天、小时）数
     */
    private int saveCount = 30;

    /**
     * 日志级别
     */
    private LogLevel level = LogLevel.INFO;

    /**
     * 当前小时的日志名是否带日期
     */
    private boolean curLogHasDate = false;

    public LogModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAdditivity() {
        return additivity;
    }

    public void setAdditivity(boolean additivity) {
        this.additivity = additivity;
    }

    @Override
    public String toString() {
        return "LogModel{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", additivity=" + additivity +
                ", logRollingType=" + logRollingType +
                ", saveCount=" + saveCount +
                ", level=" + level +
                ", curLogHasDate=" + curLogHasDate +
                '}';
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public LogRollingType getLogRollingType() {
        return logRollingType;
    }

    public void setLogRollingType(LogRollingType logRollingType) {
        this.logRollingType = logRollingType;
    }

    public int getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(int saveCount) {
        this.saveCount = saveCount;
    }

    public boolean isCurLogHasDate() {
        return curLogHasDate;
    }

    public void setCurLogHasDate(boolean curLogHasDate) {
        this.curLogHasDate = curLogHasDate;
    }
}
