package com.oort.virgo;

import ch.qos.logback.classic.PatternLayout;
import com.oort.virgo.constant.LogLevel;
import com.oort.virgo.constant.LogRollingType;
import com.oort.virgo.constant.VirgoConstant;
import com.oort.virgo.layout.NMessageConverter;
import com.oort.virgo.layout.MsgWithThrowConverter;
import com.google.common.base.Strings;

/**
 * virgo启动器
 * @author lpf
 */
public class VirgoLancher {

    static {
        if(!LoggerHelper.findConfigXml()) {
            // 替换id
            PatternLayout.defaultConverterMap.put(VirgoConstant.LAYOUT_NMSG, NMessageConverter.class.getName());
            // pre 格式化
            PatternLayout.defaultConverterMap.put(VirgoConstant.LAYOUT_EXP, MsgWithThrowConverter.class.getName());
            // 清理其他日志的残留
            LoggerHelper.logClear();
            // 生成特殊的Logger
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_SPRING, VirgoConstant.SPEC_SPRING);
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_COMMONS, VirgoConstant.SPEC_COMMONS);
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_IBATIS, VirgoConstant.SPEC_IBATIS);
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_MYBAITIC, VirgoConstant.SPEC_MYBAITIC);
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_KAFKA, VirgoConstant.SPEC_KAFKA);
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_DRUID, VirgoConstant.SPEC_DRUID);
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_HTTP, VirgoConstant.SPEC_HTTP);
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_LAMBDAWORKS, VirgoConstant.SPEC_LAMBDAWORKS);
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_NETTY, VirgoConstant.SPEC_NETTY);
            // 集成洋仔的调度器
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_SCHEDULE, VirgoConstant.SPEC_SCHEDULE);
            LoggerHelper.commonOpen(VirgoConstant.SPEC_NAME_SCHEDULE, LogLevel.DEBUG);
            // 集成各大控制器默认的Log
            LoggerHelper.addSpecLogger(VirgoConstant.SPEC_NAME_SERVICELOG, VirgoConstant.SPEC_SERVICELOG);
            LoggerHelper.commonOpen(VirgoConstant.SPEC_NAME_SERVICELOG, LogLevel.DEBUG);

            // 关闭控制台
            LoggerHelper.consoleClose();
        }
    }

    /**
     * virgo日志启动，业务系统层，即初始化VirgoLog.getLogger()的日志
     * @param name 日志名
     * @param pack 包名
     * @param path 路径
     */
    public static void start(String name, String pack, String path) {

        if(Strings.isNullOrEmpty(name)) {
            throw new RuntimeException("name is null");
        }
        // 如果pack为空，则使用name作为默认的pack
        if(Strings.isNullOrEmpty(pack)) {
            pack = name;
        }
        // 创建默认的logger
        VirgoLog.init(name, pack, path);
    }

    /**
     * virgo日志启动，业务系统层，即初始化VirgoLog.getLogger()的日志
     * @param name 日志名
     * @param pack 包名
     * @param path 路径
     */
    public static void start(String name, String pack, String path, LogRollingType logRollingType, int saveCount, boolean curLogHasDate) {

        if(Strings.isNullOrEmpty(name)) {
            throw new RuntimeException("name is null");
        }
        // 如果pack为空，则使用name作为默认的pack
        if(Strings.isNullOrEmpty(pack)) {
            pack = name;
        }
        // 创建默认的logger
        VirgoLog.init(name, pack, path, logRollingType, saveCount, curLogHasDate);
    }

    /**
     * 组件层日志启动，即初始化VirgoLog.getLogger(commonName)的日志
     * @param commonName
     */
    public static void commonStart(String commonName, String pack) {

        if(Strings.isNullOrEmpty(commonName)) {
            throw new RuntimeException("commonName is null");
        }
        // 如果pack为空，则使用commonName作为默认的pack
        if(Strings.isNullOrEmpty(pack)) {
            pack = commonName;
        }
        if(!LoggerHelper.findConfigXml()) {
            LoggerHelper.addLogger(commonName, pack, null, true, LogRollingType.DAY, 30, LogLevel.INFO, true, false);
        }
    }

    /**
     * 获取tomcat默认的路径
     * @param name 日志名
     * @return
     */
    public static String getTomcatLogPath(String name) {

        String result = null;
        String property = System.getProperty("catalina.home");
        if(!Strings.isNullOrEmpty(property)) {
            result = property + "/logs/" + name + ".log";
        }
        return result;
    }
}
