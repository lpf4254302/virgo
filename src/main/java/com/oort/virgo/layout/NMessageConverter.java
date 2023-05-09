package com.oort.virgo.layout;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.oort.virgo.VirgoLog;
import com.oort.virgo.constant.VirgoConstant;
import com.google.common.base.Strings;
import org.slf4j.MDC;

/**
 * 消息内容格式转换
 * @ClassName:      NMessageConverter
 */
public class NMessageConverter extends MessageConverter {

	private static final String VIRGO_CLASS = VirgoLog.class.getName();

    /**
     * 转换方法
     * @param event
     * @return
     */
	@Override
	public String convert(ILoggingEvent event) {
		IThrowableProxy throwableProxy = event.getThrowableProxy();
		String message = super.convert(event);
		if (message == null) {
			return "";
		}
		boolean endWithEnter = message.endsWith(VirgoConstant.LINUX_EOL);
		String[] stringLines = message.split(VirgoConstant.WIN_LINUX_EOL_REG);

		// 格式化pre   【a1★a2★a3★a4★a5】
		String preStr = getPreStr();
		String id = getId();

		StringBuilder sb = new StringBuilder();
		if(stringLines == null || stringLines.length == 0) {
			return sb.toString();
		}
		// 第一行处理，有换行或者有异常
		if(stringLines.length > 1 || throwableProxy != null) {
			// 拥有换行的才会有这个字符，可以以id和这个字符为结尾为依据，判断是否有换行
			sb.append(VirgoConstant.HAS_NEXT_LINE_MARK);
		}
		sb.append(id).append(VirgoConstant.PART_SPLIT).append(preStr).append(VirgoConstant.PART_SPLIT).append(stringLines[0]);
		if(stringLines.length > 1) {
			sb.append(VirgoConstant.WIN_EOL);
		}
		for (int i = 1; i < stringLines.length; i++) {
			sb.append(VirgoConstant.DEFAULT_TAB);
			if (i != (stringLines.length - 1)) {
				sb.append(VirgoConstant.NEXT_LINE_MARK);
			} else {
				sb.append(VirgoConstant.END_LINE_MARK);
			}
			// 从第二行开始，增加标识
			sb.append(id).append(VirgoConstant.PART_SPLIT).append(preStr).append(VirgoConstant.PART_SPLIT).append(stringLines[i]);
			if (i != (stringLines.length - 1)) {
				sb.append(VirgoConstant.WIN_EOL);
			} else {
				if (endWithEnter) {
					sb.append(VirgoConstant.WIN_EOL);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 获取id
	 * @return
     */
	protected static String getId() {
		String id = (String) MDC.get(VirgoConstant.ID);
		if(id == null) {
			VirgoLog.setUniqKey(null);
			id = (String) MDC.get(VirgoConstant.ID);
			if(id == null) {
				id = "";
			}
		}
		return id;
	}

	/**
	 * 封装获取前置字符串
	 * @return
     */
	protected static String getPreStr() {
		String pres = (String) MDC.get(VirgoConstant.PRE);
		// 格式化pre   【a1★a2★a3★a4★a5】
		String preStr = VirgoConstant.EMPTY;
		if(!Strings.isNullOrEmpty(pres)) {
			if(pres.indexOf(VirgoConstant.STAR) != 0) {
				preStr = VirgoConstant.BRACKETS_START + pres + VirgoConstant.BRACKETS_END;
			} else {
				// STAR标志在第一位表示自动设置类名为第一位
				String className = getCallClassName().className;
				preStr = VirgoConstant.BRACKETS_START + className + pres + VirgoConstant.BRACKETS_END;
			}
		} else {
			CM cm = getCallClassName();
			if(!Strings.isNullOrEmpty(cm.className) || !Strings.isNullOrEmpty(cm.method)) {
				// 都为空则设置为空
				preStr = VirgoConstant.BRACKETS_START + cm.className + VirgoConstant.STAR + cm.method + VirgoConstant.BRACKETS_END;
			}
		}
		return preStr;
	}

	/**
	 * 获取调用的className
	 * @return
     */
	protected static CM getCallClassName() {
		boolean mark = false;
		CM cm = new CM();
		String className = VirgoConstant.EMPTY;
		String method = VirgoConstant.EMPTY;
		StackTraceElement[] sts = new Exception().getStackTrace();
		for (StackTraceElement st: sts) {
			boolean isVrigo = VIRGO_CLASS.equals(st.getClassName());
			if(mark && !isVrigo) {
				className = st.getClassName();
				int i = className.lastIndexOf(".") + 1;
				className = className.substring(i);
				method = st.getMethodName();
				break;
			}
			if(VIRGO_CLASS.equals(st.getClassName())) {
				// 发现virgo，标记
				mark = true;
			}
		}
		cm.className = className;
		cm.method = method;
		return cm;
	}

	protected static class CM {
		protected String className;
		protected String method;
	}
}
