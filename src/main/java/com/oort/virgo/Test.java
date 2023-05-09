package com.oort.virgo;

import com.oort.virgo.constant.LogRollingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class Test {


    public static void main(String[] args) throws InterruptedException {

        // 启动
        VirgoLancher.start("hahaha", "com.oort.virgo", "D:/test", LogRollingType.HOURE, 30, true);
//        LoggerHelper.commonOpen("hahaha", LogLevel.DEBUG);
//        LoggerHelper.addLogger("tttt1", "tttt1", "D:/test2/tttt1.log");
//        Logger logger1 = LoggerFactory.getLogger("druid");
//        VirgoLancher.commonStart("abc", "com.oort.virgo");
        // 打开控制台
        LoggerHelper.consoleOpen();
        // 设置cid
        VirgoLog.setUniqKey("asdfasfdasfasdf");
        // 设置步骤名和交易名
//        VirgoLog.updateStep("adfa", "saf");
        // 获取Logger
        VirgoLog logger = VirgoLog.getLogger();
//        VirgoLog tttt1 = VirgoLog.getLogger("tttt1");

        // 打开debug级别（只有在开发阶段可以打开）
//        logger.changeLevel(LogLevel.DEBUG);
        // 记录换行
        logger.log("a");

//        tttt1.log("xxxxxx");

//        logger1.info("dddddddddd");
//        logger1.error("dddddddddd");

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
//        logger.begin("处理内容");
//        logger.begin("处理第二个");
//        logger.begin("处理第三个");
//        Thread.sleep(3000L);
//        logger.end();
//        Thread.sleep(1000L);
//        logger.end();
//        VirgoLog.updateStep("saf3");
//        logger.end();
//        // 记录debug日志，一般调试用
//        logger.logDebug("jajajajaja");

        List l = new ArrayList();
        B b = new B();
        try {
            b.b();
        } catch (Exception e) {
            logger.logErr("woqu", e);
        }
    }

    private static class A {
        public void a() throws Exception {
            throw new Exception("错了a");
        }
    }

    private static class B {
        public void b() {
            A a = new A();
            try {
                a.a();
            } catch (Exception e) {
                throw new IllegalArgumentException("错误b", e);
            }
        }
    }

}
