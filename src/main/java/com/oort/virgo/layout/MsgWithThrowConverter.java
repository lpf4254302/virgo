package com.oort.virgo.layout;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import com.oort.virgo.constant.VirgoConstant;

/**
 * 异常信息格式转换
 */
public class MsgWithThrowConverter extends ThrowableProxyConverter {

	@Override
	protected String throwableProxyToString(IThrowableProxy tp) {
		StringBuilder sb = new StringBuilder(BUILDER_CAPACITY);

		recursiveAppend(sb, null, ThrowableProxyUtil.REGULAR_EXCEPTION_INDENT, tp, true);

		return sb.toString();
	}

	/**
	 * 递归拼装
	 * @param sb
	 * @param prefix
	 * @param indent
	 * @param tp
	 */
	private void recursiveAppend(StringBuilder sb, String prefix, int indent, IThrowableProxy tp, boolean isFirst) {
		if (tp == null) {
			return;
		}
		if(!isFirst) {
			sb.append(VirgoConstant.DEFAULT_TAB).append(VirgoConstant.NEXT_LINE_MARK);
		}
		subjoinFirstLine(sb, prefix, indent, tp, isFirst);
		sb.append(CoreConstants.LINE_SEPARATOR);
		subjoinSTEPArray(sb, indent, tp);
		IThrowableProxy[] suppressed = tp.getSuppressed();
		if (suppressed != null) {
			for (IThrowableProxy current : suppressed) {
				recursiveAppend(sb, CoreConstants.SUPPRESSED, indent + ThrowableProxyUtil.SUPPRESSED_EXCEPTION_INDENT, current, isFirst);
			}
		}
		recursiveAppend(sb, CoreConstants.CAUSED_BY, indent, tp.getCause(), false);
	}

	private void subjoinFirstLine(StringBuilder buf, String prefix, int indent, IThrowableProxy tp, boolean isFirst) {
		ThrowableProxyUtil.indent(buf, indent - 1);


		// id
		String id = NMessageConverter.getId();
		// 格式化pre   【a1★a2★a3★a4★a5】
		String preStr = NMessageConverter.getPreStr();
		if(!isFirst) {
			buf.append(id).append(VirgoConstant.PART_SPLIT).append(preStr).append(VirgoConstant.PART_SPLIT);
		}
		if (prefix != null) {
			buf.append(prefix);
		}
		subjoinExceptionMessage(buf, tp, isFirst);
	}

	private void subjoinExceptionMessage(StringBuilder buf, IThrowableProxy tp, boolean isFirst) {

		buf.append(tp.getClassName()).append(": ").append(tp.getMessage());
	}

	@Override
	public void subjoinSTEPArray(StringBuilder buf, int indent, IThrowableProxy tp) {

		// id
		String id = NMessageConverter.getId();
		// 格式化pre   【a1★a2★a3★a4★a5】
		String preStr = NMessageConverter.getPreStr();
		StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
		int commonFrames = tp.getCommonFrames();

		boolean unrestrictedPrinting = Integer.MAX_VALUE > stepArray.length;

		int maxIndex = (unrestrictedPrinting) ? stepArray.length : Integer.MAX_VALUE;
		if ((commonFrames > 0) && unrestrictedPrinting) {
			maxIndex -= commonFrames;
		}

		for (int i = 0; i < maxIndex; i++) {
			buf.append(VirgoConstant.DEFAULT_TAB);
			boolean isFrames = (commonFrames > 0) && unrestrictedPrinting;
			if (!isFrames && tp.getCause() == null && (i == maxIndex - 1)) {
				buf.append(VirgoConstant.END_LINE_MARK);
			} else {
				buf.append(VirgoConstant.NEXT_LINE_MARK);
			}
			buf.append(id).append(VirgoConstant.PART_SPLIT).append(preStr).append(VirgoConstant.PART_SPLIT);
			ThrowableProxyUtil.indent(buf, indent);
			buf.append(stepArray[i]);
			// allow other data to be added
			extraData(buf, stepArray[i]);

			if (isFrames || tp.getCause() != null || i != maxIndex - 1) {
				buf.append(CoreConstants.LINE_SEPARATOR);
			}
		}

		if ((commonFrames > 0) && unrestrictedPrinting) {
			buf.append(VirgoConstant.DEFAULT_TAB);
			if(tp.getCause() == null) {
				buf.append(VirgoConstant.END_LINE_MARK);
			} else {
				buf.append(VirgoConstant.NEXT_LINE_MARK);
			}
			buf.append(id).append(VirgoConstant.PART_SPLIT).append(preStr).append(VirgoConstant.PART_SPLIT);
			ThrowableProxyUtil.indent(buf, indent);
			buf.append("... ").append(tp.getCommonFrames()).append(" common frames omitted");
			if(tp.getCause() != null) {
				buf.append(CoreConstants.LINE_SEPARATOR);
			}
		}
	}

}
