package com.oort.virgo.constant;

/**
 * 日志切割类型
 */
public enum LogRollingType {

    /**
     * 日
     */
    DAY("d", "%d{yyyy-MM-dd}"),
    /**
     * 小时
     */
    HOURE("h", "%d{yyyy-MM-dd-HH}"),
    /**
     * 不切割
     */
    NONE(null, null);

    String name;
    String formate;

    LogRollingType(String name, String formate) {
        this.name = name;
        this.formate = formate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormate() {
        return formate;
    }

    public void setFormate(String formate) {
        this.formate = formate;
    }
}
