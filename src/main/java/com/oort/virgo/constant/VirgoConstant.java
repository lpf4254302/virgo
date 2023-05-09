package com.oort.virgo.constant;

import java.io.File;

/**
 * 日志常量
 * @author lpf
 */
public class VirgoConstant {

    /**
     * 路径分割/
     */
    public static final String PATH_SPL = File.pathSeparator;

    /**
     * .
     */
    public static final String POINT = ".";

    /**
     *
     */
    public static final String SPACE = " ";

    /**
     *
     */
    public static final String EMPTY = "";

    /**
     * 日志文件扩展名
     */
    public static final String LOG_EXT = "log";

    /**
     * 格式化参数
     */
    public static final String DEFAULT_DATE_FORMATE = "HHmmss.SSS";
    // 和上面的长度对应，为了不去拼空格，这块直接写死
    public static final String DEFAULT_TAB = "            ";
    public static final String DEFAULT_LAYOUT = "%d{"+DEFAULT_DATE_FORMATE+"}||%vmsg %vexp||[%level][%thread]%n";
//    public static final String DEFAULT_LAYOUT = "%d{"+DEFAULT_DATE_FORMATE+"}||%vmsg %vexp||[%level][%thread][%c]%n";
    public static final String LAYOUT_NMSG = "vmsg";
    public static final String LAYOUT_EXP = "vexp";

    /**
     * id-唯一标识
     */
    public static final String ID = "id";

    /**
     * 前缀数组
     */
    public static final String PRE = "pre";

    /**
     * trade-唯一标识
     */
    public static final String TRADE = "trade";

    /**
     * step-唯一标识
     */
    public static final String STEP = "step";

    /**
     * windows系统换行符
     */
    public static final String WIN_EOL = "\r\n";

    /**
     * linux系统换行符
     */
    public static final String LINUX_EOL = "\n";

    /**
     * windows|linux系统换行符
     */
    public static final String WIN_LINUX_EOL_REG = "\r\n|\n|\r";

    /**
     * 主线程id标识
     */
    public static final String SELF_DEFIND_ID="N-";

    /**
     * 子线程id标识
     */
    public static final String CHILD_ID="C-";

    /**
     * 步骤分割
     */
    public static final String PART_SPLIT = "||";

    /**
     * pre格式化常量
     */
    public static final String STAR="★";
    public static final String BRACKETS_START="【";
    public static final String BRACKETS_END="】";
    public static final String END_LINE_MARK="└";
    public static final String HAS_NEXT_LINE_MARK="┌";
    public static final String NEXT_LINE_MARK="├";

    /**
     * 特殊日志
     */
    public static final String SPEC_NAME_SPRING = "springframework";
    public static final String SPEC_SPRING = "org.springframework";
    public static final String SPEC_NAME_COMMONS = "commons";
    public static final String SPEC_COMMONS = "org.apache.commons";
    public static final String SPEC_NAME_MYBAITIC = "mybatis";
    public static final String SPEC_MYBAITIC = "org.mybatis";
    public static final String SPEC_NAME_IBATIS = "ibatis";
    public static final String SPEC_IBATIS = "org.apache.ibatis";
    public static final String SPEC_NAME_KAFKA = "kafkaClients";
    public static final String SPEC_KAFKA = "org.apache.kafka.clients";
    public static final String SPEC_NAME_SCHEDULE = "scheduled";
    public static final String SPEC_SCHEDULE = "scheduled";
    public static final String SPEC_NAME_DRUID = "druid";
    public static final String SPEC_DRUID = "druid";
    public static final String SPEC_NAME_SERVICELOG = "serviceLog";
    public static final String SPEC_SERVICELOG = "serviceLog";
    public static final String SPEC_NAME_HTTP = "http";
    public static final String SPEC_HTTP = "org.apache.http";
    public static final String SPEC_NAME_NETTY = "netty";
    public static final String SPEC_NETTY = "io.netty";
    public static final String SPEC_NAME_LAMBDAWORKS = "lambdaworks";
    public static final String SPEC_LAMBDAWORKS = "com.lambdaworks.redis";

    /**
     * 控制台appender name
     */
    public static final String CONSOLE_APPENDER_NAME = "STDOUT";
    public static final String CONSOLE_APPENDER_NAME_TWO = "console";
}
