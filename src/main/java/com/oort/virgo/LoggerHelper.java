package com.oort.virgo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.oort.virgo.constant.LogLevel;
import com.oort.virgo.constant.LogRollingType;
import com.oort.virgo.constant.VirgoConstant;
import com.oort.virgo.model.LogModel;
import com.google.common.base.Strings;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 日志辅助类
 * @author lpf
 */
public class LoggerHelper {

    /**
     * 不使用virgo
     */
    public static final String UNUSE_VIRGO = "ecliptic.unuse.virgo";
    /**
     * 确定不使用virgo
     */
    public static final String YES = "1";

    /**
     * 日志上下文
     */
    private static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    /**
     * virgo日志记录在root中
     */
    private static Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

    /**
     * 找到配置文件的关键字
     */
    private static final String FIND_SOURCE = "Found resource";

    /**
     * 使用virgo日志前清理其他日志系统的残留
     */
    public static void logClear() {
//        List<Logger> loggerList = loggerContext.getLoggerList();
//        for (Logger logger: loggerList) {
//            // 清空所有的appenders
//            logger.detachAndStopAllAppenders();
//            // 不修改继承关系
//            logger.setAdditive(false);
//            // 设置级别
//            logger.setLevel(Level.INFO);
//        }
        ConsoleAppender stdoutAppender = getStdoutAppender();
        rootLogger.addAppender(stdoutAppender);
        // root logger设置为debug
        rootLogger.setLevel(Level.DEBUG);
    }

    /**
     * 增加新的Logger，其中path为默认的路径，additivty默认为true
     * @param name
     * @param pack 包名
     * @param path 日志路径
     */
    public static Logger addLogger(String name, String pack, String path) {

        return addLogger(name, pack, path, false, null, 0, null, false, false);
    }

    /**
     * 增加特殊日志，包括一些常用组件的日志和用户自定义的一些组件的日志
     * @param name 唯一标识
     * @param pack 包名
     */
    public static void addSpecLogger(String name, String pack) {

        addLogger(name, pack, null, true, null, 0, LogLevel.ERROR, true, false);
    }

    /**
     * 增加自定义Logger
     * @param name Logger名称，唯一，通过getLogger(name)获取Logger
     * @param pack 包名
     * @param path 日志保存路径，非必输
     * @param additivty 是否add到root内,默认false
     * @param logRollingType 日志切割类型
     * @param saveCount 保存（天、小时）数
     * @param level 日志级别
     * @param isSpec 是否是特殊的
     */
    public static Logger addLogger(String name, String pack, String path, boolean additivty, LogRollingType logRollingType, int saveCount, LogLevel level, boolean isSpec, boolean curLogHasDate) {

        // 名字必输
        if(Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("name must not null or empty");
        }
        LogModel model = new LogModel();
        // 这里是指logger里的package
        model.setName(pack);
        model.setAdditivity(additivty);
        model.setPath(path);
        model.setCurLogHasDate(curLogHasDate);
        if(logRollingType != null) {
            model.setLogRollingType(logRollingType);
        }
        if(saveCount > 0) {
            model.setSaveCount(saveCount);
        }
        model.setLevel(level);

        Appender appender = null;
        // 特殊的logger，没有appender
        if(!isSpec) {
            appender = getDefaultAppender(model);
        }
        // 创建logger
        Logger logger = createLogger(model, appender);
        VirgoLog.addLogger(name, logger);
        return logger;
    }

    /**
     * 打开控制台日志
     * 给Root和additivity=false的追加控制台日志
     */
    public static void consoleOpen() {

        ConsoleAppender stdoutAppender = getStdoutAppender();
        Logger root = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        root.detachAppender(VirgoConstant.CONSOLE_APPENDER_NAME);
        root.addAppender(stdoutAppender);
        List<Logger> loggerList = loggerContext.getLoggerList();
        for (Logger logger: loggerList) {
            if(!logger.isAdditive()) {
                // 给不输出到root的logger追加appender
                logger.detachAppender(VirgoConstant.CONSOLE_APPENDER_NAME);
                logger.addAppender(stdoutAppender);
            }
        }
    }

    /**
     * 打开控制台日志
     */
    public static void consoleClose() {

        List<Logger> loggerList = loggerContext.getLoggerList();
        for (Logger logger: loggerList) {
            logger.detachAppender(VirgoConstant.CONSOLE_APPENDER_NAME);
            logger.detachAppender(VirgoConstant.CONSOLE_APPENDER_NAME_TWO);
        }
    }

    /**
     * 组件日志开关
     * @param name 参考VirgoConstant.SPEC_*
     * @param level 开启的日志级别
     */
    public static void commonOpen(String name, LogLevel level) {

        if(Strings.isNullOrEmpty(name)) {
            throw new RuntimeException("method commonOpen，param name must not null");
        }
        Logger exists = loggerContext.exists(name);
        // 如果日志已经存在，则不在创建
        if(exists == null) {
            rootLogger.warn("logger exists , not create");
            return;
        }
        Logger logger = loggerContext.getLogger(name);
        // 日志级别
        if(level == null) {
            // 默认debug
            level = LogLevel.DEBUG;
        }
        logger.setLevel(level.getLevel());
    }

    /**
     * 组件日志开关
     */
    public static void changeLevel(String name, LogLevel level) {

        if(Strings.isNullOrEmpty(name)) {
            throw new RuntimeException("method commonOpen，param name must not null");
        }
        Logger logger = loggerContext.getLogger(name);
        // 日志级别
        if(level == null) {
            // 默认debug
            level = LogLevel.DEBUG;
        }
        logger.setLevel(level.getLevel());
    }

    /**
     * 把组件日志打印到目标日志内
     * @param source 组件源
     * @param target 目标(即最基本的日志)
     * @param level 级别
     */
    public static void addCommonToLog(String source, String target, LogLevel level) {

        // 获取源日志
        Logger sourceLogger = loggerContext.getLogger(source);
        // 获取目标日志
        Logger targetLogger = loggerContext.getLogger(target);
        // 设置日志级别
        sourceLogger.setLevel(level.getLevel());
        // 源日志清空appender
        sourceLogger.detachAndStopAllAppenders();
        // 把目标logger的appender赋予源Logger
        sourceLogger.addAppender(targetLogger.getAppender(target));
    }

    /**
     * 获取默认的appender
     * @param model
     * @return
     */
    private static Appender getDefaultAppender(LogModel model) {

        // 创建appender
        RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setContext(loggerContext);
        fileAppender.setName(model.getName());
        String path = model.getPath();
        if(Strings.isNullOrEmpty(path)) {
            // 默认日志路径
            path = model.getName() + VirgoConstant.POINT + VirgoConstant.LOG_EXT;
            rootLogger.info("use default path : {}", path);
        }
        if(!model.isCurLogHasDate()) {
            fileAppender.setFile(path);
        }

        LogRollingType logRollingType = model.getLogRollingType();
        if(logRollingType != LogRollingType.NONE) {
            // 日志切割
            TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
            rollingPolicy.setContext(loggerContext);
            // xxx.log.%d{yyyy-MM-dd(-HH)}.log
            if(path.contains(VirgoConstant.LOG_EXT)) {
                rollingPolicy.setFileNamePattern(path + VirgoConstant.POINT + logRollingType.getFormate());
            } else {
                rollingPolicy.setFileNamePattern(path + VirgoConstant.POINT + logRollingType.getFormate() + VirgoConstant.POINT + VirgoConstant.LOG_EXT);
            }
            rollingPolicy.setParent(fileAppender);
            // 保存（天、小时）数
            rollingPolicy.setMaxHistory(model.getSaveCount());
            rollingPolicy.start();
            fileAppender.setRollingPolicy(rollingPolicy);
        }

        // 日志模板
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(loggerContext);
        patternLayout.setPattern(VirgoConstant.DEFAULT_LAYOUT);
        patternLayout.start();
        fileAppender.setLayout(patternLayout);

        fileAppender.start();
        return fileAppender;
    }

    /**
     * 获取控制台appender
     * @return
     */
    private static ConsoleAppender getStdoutAppender() {

        // 控制台appender
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setContext(loggerContext);
        // 控制台appender的name
        consoleAppender.setName(VirgoConstant.CONSOLE_APPENDER_NAME);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        // 默认的格式
        encoder.setPattern(VirgoConstant.DEFAULT_LAYOUT);
        encoder.start();

        consoleAppender.setEncoder(encoder);
        consoleAppender.start();
        return consoleAppender;
    }

    /**
     * 创建logger
     * @param model
     */
    private static Logger createLogger(LogModel model, Appender appender) {

        rootLogger.info("create new logger begin : {}", model);
        Logger exists = loggerContext.exists(model.getName());
        // 如果日志已经存在，则不在创建 ....错误，如果不创建，之前存在的话，我们创建的就会被覆盖
        if(exists != null) {
            // 如果日志已经存在，则清空所有的配置
            exists.detachAndStopAllAppenders();
        }
        Logger logger = loggerContext.getLogger(model.getName());
        // 日志级别
        LogLevel level = model.getLevel();
        if(level == null) {
            level = LogLevel.INFO;
        }
        logger.setLevel(level.getLevel());
        logger.setAdditive(model.isAdditivity());

        if(appender != null) {
            // logger添加appender
            logger.addAppender(appender);
//            logger.addAppender(appender);
            // 有appender的说明是业务Logger，追加到root里
            Logger root = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
            root.detachAppender(appender.getName());
            root.addAppender(appender);
        }
        return logger;
    }

    /**
     * 判断是否找到logback.xml
     * @return
     */
    protected static boolean findConfigXml() {
//        StatusManager sm = loggerContext.getStatusManager();
//        List<Status> sl = sm.getCopyOfStatusList();
//        if(sl != null && sl.size() > 0) {
//            for (int i = 0; i < sl.size(); i++) {
//                Status status = sl.get(i);
//                String message = status.getMessage();
//                if(!Strings.isNullOrEmpty(message) && message.startsWith(FIND_SOURCE)) {
//                    return true;
//                }
//            }
//        }
        String unuseVrigo = System.getProperty(UNUSE_VIRGO);
        if(YES.equals(unuseVrigo)) {
            return true;
        }
        return false;
    }

    protected static Logger getDefaultLogger(String name) {
        return loggerContext.getLogger(name);
    }
}
