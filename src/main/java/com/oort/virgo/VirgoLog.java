package com.oort.virgo;

import com.oort.virgo.constant.LogLevel;
import com.oort.virgo.constant.LogRollingType;
import com.oort.virgo.constant.VirgoConstant;
import com.oort.virgo.util.MyMessageFormatter;
import com.oort.virgo.util.StrUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志主类
 * @author lpf
 */
public class VirgoLog {

    /**
     * logger集合
     */
    private static final Map<String, VirgoLog> LOGGERS = new ConcurrentHashMap();

    private static VirgoLog dfLogger = new VirgoLog();

    /**
     * 默认的日志
     */
    private Logger logger = null;

    /**
     * 是否找到xml
     */
    private static boolean isFindXml = true;

    /**
     * 计时器，本地变量
     * 按栈方式存储，来记录耗时
     */
    private static ThreadLocal<Stack<Long>> timers = new ThreadLocal<Stack<Long>>() {
        @Override
        protected Stack<Long> initialValue() {
            return new Stack();
        }
    };
    private static ThreadLocal<Stack<String>> msgs = new ThreadLocal<Stack<String>>() {
        @Override
        protected Stack<String> initialValue() {
            return new Stack();
        }
    };

    private VirgoLog() {
    }

    /**
     * 初始化默认的日志
     * @param pack 包名
     */
    static synchronized void init(String name, String pack, String path) {

        init(name, pack, path, null, 0, false);
    }

    /**
     * 初始化默认的日志
     * @param pack 包名
     */
    static synchronized void init(String name, String pack, String path, LogRollingType logRollingType, int saveCount, boolean curLogHasDate) {

        // 可以初始化多次，为static加载顺序准备
//        if(dfLogger != null) {
//            throw new RuntimeException("只能初始化一次!");
//        }
        isFindXml = LoggerHelper.findConfigXml();
        if(isFindXml) {
            return;
        }
        // 初始化默认的Logger
        dfLogger.logger = LoggerHelper.addLogger(name, pack, path, false, logRollingType, saveCount, null, false, curLogHasDate);
    }

    /**
     * 新增logger，纳入virgo管理
     * @param name
     * @param logger
     */
    static void addLogger(String name, Logger logger) {

        if(Strings.isNullOrEmpty(name) || logger == null) {
            throw new RuntimeException("param can't be null");
        }
        VirgoLog virgoLog = LOGGERS.get(name);
        if(virgoLog == null) {
            virgoLog = new VirgoLog();
            LOGGERS.put(name, virgoLog);
        }
        virgoLog.logger = logger;
    }

    /**
     * 使用默认的日志记录
     * @return
     */
    public static VirgoLog getLogger() {

        return dfLogger;
    }

    /**
     * 通过日志或组件名获取日志
     * @param name
     * @return
     */
    public static VirgoLog getLogger(String name) {

        // 找到配置文件的默认处理，不使用virgo了
        isFindXml = LoggerHelper.findConfigXml();
        if(isFindXml) {
            addLogger(name, LoggerHelper.getDefaultLogger(name));
        }
        VirgoLog virgoLog = LOGGERS.get(name);
        if(virgoLog == null) {
            // 没设置就使用默认的Logger
            virgoLog = dfLogger;
        }
        if(virgoLog == null) {
            // 如果不存在，则先暂时默认初始化一个，等到真正调用初始化后才正常使用
            LoggerHelper.addLogger(name, name, null, true, LogRollingType.DAY, 30, LogLevel.INFO, true, false);
            // 再拿一次
            virgoLog = LOGGERS.get(name);
            if(virgoLog == null) {
                throw new RuntimeException("no logger " + name);
            }
        }
        return virgoLog;
    }

    /**
     * 通过日志或组件名获取日志
     * @param name
     * @return
     */
    public static VirgoLog getLogger(String name, String useClassName) {

        // 找到配置文件的默认处理，不使用virgo了
        isFindXml = LoggerHelper.findConfigXml();
        if(isFindXml) {
            addLogger(name, LoggerHelper.getDefaultLogger(name));
        }
        VirgoLog virgoLog = LOGGERS.get(name);
        if(virgoLog == null) {
            // 没设置就使用默认的Logger
            virgoLog = dfLogger;
        }
        if(virgoLog == null) {
            // 如果不存在，则先暂时默认初始化一个，等到真正调用初始化后才正常使用
            LoggerHelper.addLogger(name, name, null, true, LogRollingType.DAY, 30, LogLevel.INFO, true, false);
            // 再拿一次
            virgoLog = LOGGERS.get(name);
            if(virgoLog == null) {
                throw new RuntimeException("no logger " + name);
            }
        }
        return virgoLog;
    }

    /**
     * 拦截器或者新线程启动时设置id号
     * 如果id为空,则新建一个"N-"开头的id
     */
    public static void setUniqKey(String id) {

        if (Strings.isNullOrEmpty(id)) {
            id = VirgoConstant.SELF_DEFIND_ID + StrUtil.generateShortUuid();
        }
        MDC.put(VirgoConstant.ID, id);
    }

    /**
     * 更新当前线程 交易名和步骤名
     * @param trade
     * @param step
     */
    public static void updateStep(String trade, String step) {

        if(trade == null) {
            trade = VirgoConstant.EMPTY;
        }
        if(step == null) {
            step = VirgoConstant.EMPTY;
        }
        // 以空格为间隔
        String pre = trade + VirgoConstant.STAR + step;
        MDC.put(VirgoConstant.PRE, pre);
    }

    /**
     * 仅更新当前线程 步骤名
     * @param step
     */
    public static void updateStep(String step) {

        String trade = VirgoConstant.EMPTY;
        String oldPre = (String) MDC.get(VirgoConstant.PRE);
        if(!Strings.isNullOrEmpty(oldPre) && oldPre.contains(VirgoConstant.STAR)) {
            trade = oldPre.substring(0, oldPre.indexOf(VirgoConstant.STAR));
        }
        if(step == null) {
            step = VirgoConstant.EMPTY;
        }
        // 以空格为间隔
        String pre = trade + VirgoConstant.STAR + step;
        MDC.put(VirgoConstant.PRE, pre);
    }

    /**
     * 获取当前线程的UniqKey
     * @return 当前线程的uniqKey
     */
    public static String getUniqKey() {

        Object uniqKey = MDC.get(VirgoConstant.ID);
        String resultKey = null;
        if(uniqKey != null) {
            if(uniqKey instanceof String) {
                resultKey = (String) uniqKey;
            } else {
                resultKey = uniqKey.toString();
            }
        }
        return resultKey;
    }

    /**
     * 生成子线程的uniqKey(在主线程生成，传入子线程)
     * @return 新的UniqKey
     */
    public static String genChildUniqKey() {

        // 获取当前线程的uniqKey
        String oldUniqKey = getUniqKey();
        if(Strings.isNullOrEmpty(oldUniqKey)) {
            oldUniqKey = StrUtil.generateShortUuid();
        }
        return VirgoConstant.CHILD_ID + oldUniqKey;
    }

    /**
     * 记录开始时间
     * begin=b end=e
     * 支持下面这种形式的调用
     * b e
     * b1 b2 e2 e1
     * b1 b2 b3 e3 e2 e1
     * b(n次) e(n次)
     * 不支持乱序 b1 b2 b3 e2 e3 e1
     *
     * 1、调用begin，必须要调用end，否则会出现计算错误
     * 2、仅支持同线程内计算
     * 3、仅支持成对的计算
     */
    public void begin(String msg) {
        begin(null, msg);
    }

    /**
     * 记录结束，打印日志
     * 和begin成对调用
     * @return
     */
    public long end() {
        return end(null);
    }

    /**
     * 记录开始时间
     * begin=b end=e
     * 支持下面这种形式的调用
     * b e
     * b1 b2 e2 e1
     * b1 b2 b3 e3 e2 e1
     * b(n次) e(n次)
     * 不支持乱序 b1 b2 b3 e2 e3 e1
     *
     * 1、调用begin，必须要调用end，否则会出现计算错误
     * 2、仅支持同线程内计算
     * 3、仅支持成对的计算
     */
    public void begin(String step, String msg) {
        if(!Strings.isNullOrEmpty(step)) {
            updateStep(step);
        }
        Stack<Long> stack = timers.get();
        Stack<String> smsgs = msgs.get();
        stack.push(System.currentTimeMillis());
        if(Strings.isNullOrEmpty(msg)) {
            msg = VirgoConstant.EMPTY;
        }
        smsgs.push(msg);
        log("------ {} begin ------", msg);
    }

    /**
     * 记录结束，打印日志
     * 和begin成对调用
     * @return
     */
    public long end(String step) {
        if(!Strings.isNullOrEmpty(step)) {
            updateStep(step);
        }
        Stack<Long> stack = timers.get();
        Stack<String> smsgs = msgs.get();
        Long begin = stack.pop();
        String msg = smsgs.pop();
        long end = System.currentTimeMillis();
        if (begin == null) {
            begin = 0L;
            logErr("not call begin yet!");
            return begin - end;
        }
        if(msg == null) {
            msg = VirgoConstant.EMPTY;
        }
        long elapsed = end - begin;
        log("------ {} end, elapse:{}ms ------", msg, elapsed);
        return elapsed;
    }

    /**
     * 记录info级别日志
     * @param msg
     */
    public void log(String msg) {
        logStep(null, msg);
    }

    /**
     * 格式化记录info级别日志
     * @param format
     * @param args
     */
    public void log(String format, Object... args) {
        logStep(null, format, args);
    }

    /**
     * 记录info级别日志
     * @param msg
     */
    public void logStep(String step, String msg) {
        if(!Strings.isNullOrEmpty(step)) {
            updateStep(step);
        }
        getCurLogger().info(msg);
    }

    /**
     * 格式化记录info级别日志
     * @param format
     * @param args
     */
    public void logStep(String step, String format, Object... args) {
        if(!Strings.isNullOrEmpty(step)) {
            updateStep(step);
        }
        getCurLogger().info(MyMessageFormatter.arrayFormat(format, args).getMessage());
    }

    /**
     * 把对象转化为json输出
     * @param obj
     */
    public void logJson(Object obj) {
        logJson(obj, false);
    }

    /**
     * 把对象转化为json输出
     * @param obj
     * @param pretty 是否格式化
     */
    public void logJson(Object obj, boolean pretty) {
        logObjJson(obj, pretty);
    }

    /**
     * 把map转换为json
     * @param map
     */
    public void logMap(Map map) {
        logMap(map, false);
    }

    /**
     * 把map转换为json，格式化输出
     * @param map
     * @param pretty
     */
    public void logMap(Map map, boolean pretty) {
        logObjJson(map, pretty);
    }

    /**
     * 把集合装换为json，格式化输出
     * @param collection
     * @param pretty
     */
    public void logCollection(Collection collection, boolean pretty) {
        logObjJson(collection, pretty);
    }

    /**
     * 把集合装换为json
     * @param collection
     */
    public void logCollection(Collection collection) {
        logCollection(collection, false);
    }

    /**
     * 把对象转换为json输出日志
     * @param obj
     * @param pretty 格式化
     */
    private void logObjJson(Object obj, boolean pretty) {
        if(obj == null) {
            getCurLogger().info(null);
            return;
        }
        try {
            if(pretty) {
                log(MyMessageFormatter.DELIM_JSON_PRETTY_STR, obj);
            } else {
                log(MyMessageFormatter.DELIM_JSON_STR, obj);
            }
        } catch (Exception e) {
            logErr("json转换异常", e);
        }
    }

    /**
     * 记录错误日志
     * @param msg
     */
    public void logErr(String msg) {

        logErrStep(null, msg);
    }

    /**
     * 记录错误日志
     * @param format
     */
    public void logErr(String format, Object... args) {

        logErrStep(null, format, args);
    }

    /**
     * 记录错误日志
     * @param msg
     * @param t
     */
    public void logErr(String msg, Throwable t) {

        logErrStep(null, msg, t);
    }

    /**
     * 格式化记录异常日志
     * @param format
     * @param t
     * @param args
     */
    public void logErr(String format, Throwable t, Object... args) {

        logErrStep(null, format, t, args);
    }

    /**
     * 记录错误日志
     * @param msg
     */
    public void logErrStep(String step, String msg) {
        if(!Strings.isNullOrEmpty(step)) {
            updateStep(step);
        }
        getCurLogger().error(msg);
    }

    /**
     * 记录错误日志
     * @param format
     */
    public void logErrStep(String step, String format, Object... args) {
        if(!Strings.isNullOrEmpty(step)) {
            updateStep(step);
        }
        getCurLogger().error(MyMessageFormatter.arrayFormat(format, args).getMessage());
    }

    /**
     * 记录错误日志
     * @param msg
     * @param t
     */
    public void logErrStep(String step, String msg, Throwable t) {
        if(!Strings.isNullOrEmpty(step)) {
            updateStep(step);
        }
        getCurLogger().error(msg, t);
    }

    /**
     * 格式化记录异常日志
     * @param format
     * @param t
     * @param args
     */
    public void logErrStep(String step, String format, Throwable t, Object... args) {
        if(!Strings.isNullOrEmpty(step)) {
            updateStep(step);
        }
        String message = MyMessageFormatter.arrayFormat(format, args).getMessage();
        getCurLogger().error(message, t);
    }

    /**
     * 记录debug日志，一般只有组件才会使用
     * @param format
     * @param args
     */
    public void logDebug(String format, Object... args) {

        getCurLogger().debug(MyMessageFormatter.arrayFormat(format, args).getMessage());
    }

    /**
     * 记录debug日志，一般只有组件才会使用
     * @param msg
     */
    public void logDebug(String msg) {

        getCurLogger().debug(msg);
    }

    /**
     * 改变日志级别
     * @param level
     */
    public void changeLevel(LogLevel level) {
        if(level != null) {
            ch.qos.logback.classic.Logger curLogger = (ch.qos.logback.classic.Logger) this.logger;
            curLogger.setLevel(level.getLevel());
        }
    }

    /**
     * 对获取本类内的logger做个封装
     * @return
     */
    private Logger getCurLogger() {

        if(logger == null) {
            // 如果拿不到log，使用stdout
            logger = LoggerFactory.getLogger(VirgoConstant.CONSOLE_APPENDER_NAME);
        }
        return logger;
    }

    public static void main(String[] args) throws InterruptedException {

        // 启动
        VirgoLancher.start("hahaha", "com.cdc.virgo", "D:/test");
        LoggerHelper.commonOpen("hahaha", LogLevel.DEBUG);
        Logger logger1 = LoggerFactory.getLogger("druid");
//        VirgoLancher.commonStart("abc", "com.cdc.virgo");
        // 打开控制台
        LoggerHelper.consoleOpen();
        // 设置cid
        VirgoLog.setUniqKey("asdfasfdasfasdf");
        // 设置步骤名和交易名
//        VirgoLog.updateStep("adfa", "saf");
        // 获取Logger
        VirgoLog logger = VirgoLog.getLogger();

        // 打开debug级别（只有在开发阶段可以打开）
//        logger.changeLevel(LogLevel.DEBUG);
        // 记录换行
        logger.log("a");

        logger.logStep("换了", "aaaaa");

        logger1.info("dddddddddd");
        logger1.error("dddddddddd");
        logger.logErrStep("换了2", "adasfas");

//        logger1.info("sfdasfaf" +
//                "\nafafdasfd" +
//                "\nasfdasf");
        logger.log("sfdasfaf" +
                "\nafafdasfd" +
                "\nasfdasf");

//        logger1.info("b");
        // 正常日志
//        logger.log("我只有一行");
        Map<String, String> map = new HashMap();
        map.put("asdf", "1");
        map.put("asdf2", "2");
        map.put("asdf3", "13");
        map.put("asdf4", "14");
        map.put("asdf5", "15");
        map.put("asdf6", "16");
//        // 异常日志也支持格式化
//        logger.logErr("我错了:{},你没错：~~", new Exception("asdfsaflk"), "啊", map);
//        logger.log("----------------------------------------------");
//        // {}替换普通对象，调用toString()  ~~把对象转换为json并且格式化输出 ``把对象转换为json不格式化输出
        logger.log("你好{},你是谁~~``,sd~xx {}", map, map, map, "tttt");
        VirgoLog.updateStep("saf2");
//        // 把对象转换为json输出
//        logger.logJson(map, false);
//        // 更新步骤名和交易名
//        VirgoLog.updateStep("bbbbb", "ccccc");
//        // 耗时日志打印
        logger.begin("处理内容");
        logger.begin("处理第二个");
        logger.begin("处理第三个");
        Thread.sleep(3000L);
        logger.end();
        Thread.sleep(1000L);
        logger.end();
        VirgoLog.updateStep("saf3");
        logger.end();
//        // 记录debug日志，一般调试用
//        logger.logDebug("jajajajaja");

//        List l = new ArrayList();
//        B b = new B();
//        try {
//            b.b();
//        } catch (Exception e) {
//            logger.logErr("woqu", e);
//        }
}

//    private static class A {
//        public void a() throws Exception {
//            throw new Exception("错了a");
//        }
//    }
//
//    private static class B {
//        public void b() {
//            A a = new A();
//            try {
//                a.a();
//            } catch (Exception e) {
//                throw new IllegalArgumentException("错误b", e);
//            }
//        }
//    }

}
