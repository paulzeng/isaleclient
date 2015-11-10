package com.zjrc.isale.client.util.xml;

public class XmlValueUtil {
	/**
	 * 替换一个字符串中的某些指定字符
	 * 
	 * @param strData
	 *            String 原始字符串
	 * @param regex
	 *            String 要替换的字符串
	 * @param replacement
	 *            String 替代字符串
	 * @return String 替换后的字符串
	 */
	public static String replaceString(String strData, String regex,
			String replacement) {
		if (strData == null) {
			return null;
		}
		int index;
		index = strData.indexOf(regex);
		String strNew = "";
		if (index >= 0) {
			while (index >= 0) {
				strNew += strData.substring(0, index) + replacement;
				strData = strData.substring(index + regex.length());
				index = strData.indexOf(regex);
			}
			strNew += strData;
			return strNew;
		}
		return strData;
	}

	/**
	 * 替换字符串中特殊字符
	 */
	public static String encodeString(String strData) {
		if (strData == null) {
			return "";
		}
		
		strData = replaceString(strData, "\"", "&quot;");
		strData = replaceString(strData, "'", "&apos;");
		strData = replaceString(strData, "&", "&amp;");
		strData = replaceString(strData, "<", "&lt;");
		strData = replaceString(strData, ">", "&gt;");
		
		/*strData = replaceString(strData, "\"", "&#34;");
		strData = replaceString(strData, "'", "&#39;");
		strData = replaceString(strData, "&", "&#38;");
		strData = replaceString(strData, "<", "&#60;");
		strData = replaceString(strData, ">", "&#62;");*/
		return strData;
	}
	/**
	 * 特殊字符处理 for dom4j
	 */
	public static String decorationString(String strData) {
		if (strData == null) {
			return "";
		}
		
		strData = "<![CDATA["+strData+"]]>";
		
		
		/*strData = replaceString(strData, "\"", "&#34;");
		strData = replaceString(strData, "'", "&#39;");
		strData = replaceString(strData, "&", "&#38;");
		strData = replaceString(strData, "<", "&#60;");
		strData = replaceString(strData, ">", "&#62;");*/
		return strData;
	}
	/**
	 * 还原字符串中特殊字符
	 */
	public static String decodeString(String strData) {
		/*strData = replaceString(strData, "&quot;", "\"");
		strData = replaceString(strData, "&apos;", "'");
		strData = replaceString(strData, "&amp;", "&");
		strData = replaceString(strData, "&lt;", "<");
		strData = replaceString(strData, "&gt;", ">");*/
		return strData;
	}
}
