package com.oort.virgo.layout;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.oort.virgo.constant.VirgoConstant;
import com.google.common.base.Strings;
import org.slf4j.MDC;

/**
 * 前缀消息内容格式转换
 * @Author: lpf
 */
public class PreConverter extends ClassicConverter {

    /**
     * 转换方法
     * @param event
     * @return
     */
	@Override
	public String convert(ILoggingEvent event) {
//		String message = super.convert(event);
//		if (message == null) {
//			return "";
//		}
//		boolean endWithEnter = message.endsWith(VirgoConstant.LINUX_EOL);
//		String[] stringLines = message.split(VirgoConstant.WIN_LINUX_EOL_REG);

		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < stringLines.length; i++) {
			// 格式为 a1★a2★a3★a4★a5
			String pres = MDC.get(VirgoConstant.PRE);
			// 格式化pre   【a1★a2★a3★a4★a5】
			String preStr = VirgoConstant.EMPTY;
			if(!Strings.isNullOrEmpty(pres)) {
				preStr = VirgoConstant.BRACKETS_START + pres + VirgoConstant.BRACKETS_END;
			}
			sb.append(preStr);
//					.append(stringLines[i]);
//			if (i != (stringLines.length - 1)) {
//				sb.append(VirgoConstant.WIN_EOL);
//			} else {
//				if (endWithEnter) {
//					sb.append(VirgoConstant.WIN_EOL);
//				}
//			}
//		}
		return sb.toString();
	}
}
