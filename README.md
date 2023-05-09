# 背景
大量项目在使用logback记日志，有部分项目使用日志混乱，格式不统一，多数人搞不懂配置文件，导致配置错误，现在需要开发一套统一的、少配置的日志组件，使用方便

# 设计思路
1. 尽量采用0配置，无logback.xml

2. 日志格式统一，方便后续日志分析系统

3. 只有两个日志级别，一个是正常日志，一个是异常日志

4. 提供log4j、jcl、logback、commons-log等桥接方案及版本兼容方案

5. 提子线程、json格式化输出、map格式化、数组格式化、请求响应参数（供耗时）等便捷日志输出方法

6. 支持redis、db、http自动开关配置****

# 输出路径
约定固定将日志输出到，相对路径log/xxx.yyyy-MM-dd-HH.log，其中xxx为logger的name

日志格式
格式固定：
MMddHHmmss.SSS||id||【交易名★子步骤】||context ||[level][线程号]
固定格式的核心代码，拦截到日志请求，按照格式拼装，主要方法为继承ThrowableProxyConverter和MessageConverter来实现对日志的拦截，并修改为想要的格式，其中使用的例如id等放到本地变量内，核心是对MDC的使用

# 基础logger
所有日志都默认输出到这里 logger name:service 系统初始化时，定义这个Logger和appender，即这个Logger为root log

# 自定义的logger
提供addLogger方法，参数 packageName 包名，例如：com.test 必输参数 如果name未设置时，name默认为包名最后一个.后面的字符 name 名字，决定日志文件的名字 非必输 path 日志路径 非必输 additivity 是否输出到root log内

# 特殊的log
提供特殊组件的log配置，例如： redis 默认ERROR http 默认ERROR db连接池 默认ERROR kafka 默认ERROR schedul 默认ERROR spring 默认ERROR

# 异常、换行日志处理
提供exception异常栈格式打印 提供带换行的格式化打印 代码思路：继承ThrowableProxyConverter，获取异常栈，在每行的前面插入固定格式文本
