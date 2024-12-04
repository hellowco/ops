/*
 * @(#)StringUtil.java 3.8.1 2009/03/11
 */
package kr.co.proten.llmops.global.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.*;
import java.util.*;

public class StringUtil {
	private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);
	public final static String newLine = System.getProperty("line.separator");
	private static long SERIAL = -1;

	/**
	 * Create unique ID method
	 * @return String
	 */
	public static synchronized String getTimeBasedUniqueID() {
		if (SERIAL < 0) {
			SERIAL = System.currentTimeMillis();
		}
		String tmp = Long.toHexString(SERIAL);
		SERIAL++;
		int len = tmp.length();
		return tmp.substring(len - 8, len).toUpperCase();
	}

	/**
	 * Check string method
	 * @param str
	 *            StringUtil.hasLength(null) = false
	 *            StringUtil.hasLength("") = false
	 *            StringUtil.hasLength(" ") = true
	 *            StringUtil.hasLength("Hello") = true
	 * @return empty String return false
	 */
	public static boolean hasLength(String str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Check string method
	 * @param str
	 *            StringUtil.hasText(null) = false
	 *            StringUtil.hasText("") = false
	 *            StringUtil.hasText(" ") = false
	 *            StringUtil.hasText("12345") = true
	 *            StringUtil.hasText(" 12345 ") = true
	 * @return boolean
	 */
	public static boolean hasText(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return false;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param s
	 * @param count
	 * @return String
	 */
	public static String repeat(String s, int count) {
		if (s == null || count < 0)
			return null;
		else if (s.length() == 0 || count == 0)
			return "";

		StringBuffer result = new StringBuffer(s.length() * count);

		for (int j = 0; j < count; ++j)
			result.append(s);

		return result.toString();
	}

	/**
	 * 
	 * @param c
	 * @param count
	 * @return String
	 */
	public static String repeat(char c, int count) {
		if (count < 0)
			return null;
		else if (count == 0)
			return "";

		StringBuffer result = new StringBuffer(count);

		for (int j = 0; j < count; ++j)
			result.append(c);

		return result.toString();
	}

	/**
	 * 
	 * @param s
	 * @param finalLength
	 * @return String
	 */
	public static String padLeft(String s, int finalLength) {
		return padLeft(s, ' ', finalLength);
	}

	/**
	 * 
	 * @param value
	 * @param finalLength
	 * @return String
	 */
	public static String padLeft(int value, int finalLength) {
		return padLeft("" + value, ' ', finalLength);
	}

	/**
	 * 
	 * @param value
	 * @param padChar
	 * @param finalLength
	 * @return String
	 */
	public static String padLeft(int value, char padChar, int finalLength) {
		return padLeft("" + value, padChar, finalLength);
	}

	/**
	 * 
	 * @param s
	 * @param padChar
	 * @param finalLength
	 * @return String
	 */
	public static String padLeft(String s, char padChar, int finalLength) {
		if (s == null)
			return null;
		else if (s.length() >= finalLength)
			return s;

		return repeat(padChar, finalLength - s.length()) + s;
	}

	/**
	 * 
	 * @param s
	 * @param finalLength
	 * @return String
	 */
	public static String padRight(String s, int finalLength) {
		return padLeft(s, ' ', finalLength);
	}

	/**
	 * 
	 * @param value
	 * @param finalLength
	 * @return String
	 */
	public static String padRight(int value, int finalLength) {
		return padLeft("" + value, ' ', finalLength);
	}

	/**
	 * 
	 * @param value
	 * @param padChar
	 * @param finalLength
	 * @return String
	 */
	public static String padRight(int value, char padChar, int finalLength) {
		return padLeft("" + value, padChar, finalLength);
	}

	/**
	 * 
	 * @param s
	 * @param padChar
	 * @param finalLength
	 * @return String
	 */
	public static String padRight(String s, char padChar, int finalLength) {
		if (s == null)
			return null;
		else if (s.length() >= finalLength)
			return s;

		return s + repeat(padChar, finalLength - s.length());
	}

	/**
	 * 
	 * @param s
	 * @param length
	 * @return String
	 */
	public static String right(String s, int length) {
		if (s == null)
			return null;
		else if (length < 0 && s.length() <= -length)
			return "";
		else if (s.length() <= length)
			return s;

		if (length < 0)
			return s.substring(0, s.length() + length);
		else
			return s.substring(s.length() - length);
	}

	/**
	 * 
	 * @param s
	 * @return String
	 */
	public static String toMixedCase(String s) {
		StringBuffer result = new StringBuffer();
		char ch;
		boolean lastWasUpper = false;
		boolean isUpper;

		for (int j = 0; j < s.length(); ++j) {
			ch = s.charAt(j);
			isUpper = Character.isUpperCase(ch);
			if (lastWasUpper && isUpper)
				result.append(Character.toLowerCase(ch));
			else
				result.append(ch);
			lastWasUpper = isUpper;
		}

		return result.toString();
	}

	/**
	 * 
	 * @param base
	 * @param newItem
	 * @param delimiter
	 * @return String
	 */
	public static String extendDelimited(String base, String newItem, String delimiter) {
		if (base == null || base.equals(""))
			return newItem;
		else
			return base + delimiter + newItem;
	}

	/**
	 * Trim Leading Whitespace method
	 * @param str
	 * @return String
	 */
	public static String trimLeadingWhitespace(String str) {
		if (str.length() == 0) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
			buf.deleteCharAt(0);
		}
		return buf.toString();
	}

	/**
	 * 
	 * @param s
	 * @return String
	 */
	public static String trimDuplecateSpace(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (i < s.length() - 1) {
				if (c == ' ' && s.charAt(i + 1) == ' ') {
					continue;
				}
			}
			sb.append(c);
		}
		return sb.toString().trim();
	}

	/**
	 * 
	 * @param strNum
	 * @param def
	 * @return int
	 */

	public static int parseInt(String strNum, int def) {
		if (strNum == null)
			return def;
		if (strNum.indexOf('.') > 0) {
			strNum = strNum.substring(0, strNum.indexOf('.'));
		}
		try {
			return Integer.parseInt(strNum);
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * Check Null method
	 * @param temp
	 * @return String
	 */
	public static String checkNull(String temp) {
		if (temp != null) {
			temp = temp.trim();
		} else {
			temp = "";
		}
		return temp;
	}
	
	/**
	 * Check Null method
	 * @param temp
	 * @return String
	 */
	public static String checkNull(Object temp) {
		String ret ="";
		if (temp != null) {
			ret = temp.toString().trim();
		} else {
			temp = "";
		}
		return ret;
	}

	/**
	 * Check Null method
	 * @param temp
	 * @return String[]
	 */
	public static String[] checkNull(String[] temp) {
		for (int i = 0; i < temp.length; i++) {
			temp[i] = checkNull(temp[i]);
		}
		return temp;
	}

	/**
	 * Check Null method
	 * @param temp
	 * @return String[][]
	 */
	public static String[][] checkNull(String[][] temp) {
		for (int i = 0; i < temp.length; i++) {
			temp[i][0] = checkNull(temp[i][0]);
			temp[i][1] = checkNull(temp[i][1]);
		}
		return temp;
	}

	/**
	 * String format convert method
	 * convertFormat("1", "00") return "01"
	 * @param inputStr
	 * @param format
	 * @return String
	 */
	public static String convertFormat(String inputStr, String format) {
		long _input = Long.parseLong(inputStr);
		StringBuffer result = new StringBuffer();
		DecimalFormat df = new DecimalFormat(format);
		df.format(_input, result, new FieldPosition(1));
		return result.toString();
	}

	/**
	 * 
	 * @param input
	 * @param maxLen
	 * @return String
	 */
	public static String convertInteger(String input, int maxLen) {
		int output = 0;
		int idx = maxLen;
		if (input.length() < maxLen) {
			idx = input.length();
		}
		try {
			output = Integer.parseInt(input.substring(0, idx));
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage(), e);
		}
		return Integer.toString(output);
	}

	/**
	 * 
	 * @param inputStr
	 * @return String
	 */
	public static String spaceReplace(String inputStr) {
		String Temp[] = split(inputStr, " ");
		inputStr = Temp[0];
		for (int i = 1; i < Temp.length; i++) {
			inputStr = inputStr + Temp[i];
		}
		return inputStr;
	}

	public static String[] split(String splittee, String splitChar) {
		return split(splittee, splitChar, 0);
	}

	/**
	 * String split method
	 * @param splittee
	 *            input string
	 * @param splitChar
	 *            split text
	 * @param limit
	 *            split limit number
	 * @return String[]
	 */
	public static String[] split(String splittee, String splitChar, int limit) {
		String taRetVal[];
		StringTokenizer toTokenizer;
		int tnTokenCnt;

		try {
			toTokenizer = new StringTokenizer(splittee, splitChar);
			tnTokenCnt = toTokenizer.countTokens();
			if (limit != 0 && tnTokenCnt > limit)
				tnTokenCnt = limit;
			taRetVal = new String[tnTokenCnt];

			for (int i = 0; i < tnTokenCnt; i++) {
				if (toTokenizer.hasMoreTokens()) {
					taRetVal[i] = toTokenizer.nextToken();
				}
				if (limit != 0 && limit == (i + 1))
					break;
			}
		} catch (Exception e) {
			taRetVal = new String[0];
		}
		return taRetVal;
	}

	public static String[] split(String value, String string, boolean trim, boolean ignoreBlank) {
		if (isNull(value)) {
			return new String[0];
		} else {

			String[] result = value.split(string);

			ArrayList tmp = new ArrayList();
			for (int i = 0; i < result.length; i++) {
				if (trim) {
					result[i] = result[i].trim();
				}
				if (ignoreBlank) {
					if (!result[i].equals("")) {
						tmp.add(result[i]);
					}
				} else {
					tmp.add(result[i]);
				}
			}

			return (String[]) tmp.toArray(new String[tmp.size()]);
		}

	}

	 
	public static boolean isNull(String string) {

		return "".equals(checkNull(string));
	}

	/**
	 * String sort method.
	 * @param source
	 *            the source array
	 * @return the sorted array (never null)
	 */
	public static String[] sortStringArray(String[] source) {
		if (source == null) {
			return new String[0];
		}
		Arrays.sort(source);
		return source;
	}

	/**
	 * Erase Duplicated method
	 * @param srcArr
	 * @return String[]
	 */
	public static String[] eraseDuplicatedValue(String[] srcArr) {
		List tempVector = new ArrayList();
		int loopCount = 0;

		for (loopCount = 0; loopCount < srcArr.length; loopCount++) {
			tempVector.add(srcArr[loopCount]);
		}

		Collections.sort(tempVector);

		for (loopCount = 0; loopCount < srcArr.length; loopCount++) {
			srcArr[loopCount] = (String) (tempVector.get(loopCount));
		}

		tempVector.clear();

		tempVector.add(srcArr[0]);

		for (loopCount = 1; loopCount < srcArr.length; loopCount++) {
			if (!srcArr[loopCount].equals(srcArr[loopCount - 1])) {
				tempVector.add(srcArr[loopCount]);
			}
		}

		String[] resultStrArr = new String[tempVector.size()];

		for (loopCount = 0; loopCount < resultStrArr.length; loopCount++) {
			resultStrArr[loopCount] = (String) (tempVector.get(loopCount));
		}
		return resultStrArr;
	}

	/**
	 * 
	 * @param str
	 * @return String
	 */
	public static String trimTrailingWhitespace(String str) {
		if (str.length() == 0) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	/**
	 * 
	 * @param str
	 * @param prefix
	 * @return boolean
	 */
	public static boolean startsWithIgnoreCase(String str, String prefix) {
		if (str == null || prefix == null) {
			return false;
		}
		if (str.startsWith(prefix)) {
			return true;
		}
		if (str.length() < prefix.length()) {
			return false;
		}
		String lcStr = str.substring(0, prefix.length()).toLowerCase();
		String lcPrefix = prefix.toLowerCase();
		return lcStr.equals(lcPrefix);
	}

	/**
	 * 
	 * @param str
	 * @param sub
	 * @return int
	 */
	public static int countOccurrencesOf(String str, String sub) {
		if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
			return 0;
		}
		int count = 0, pos = 0, idx = 0;
		while ((idx = str.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
		}
		return count;
	}

	/**
	 * 
	 * @param inString
	 * @param oldPattern
	 * @param newPattern
	 * @return String
	 */
	public static String replace(String inString, String oldPattern, String newPattern) {
		if (inString == null) {
			return null;
		}
		if (oldPattern == null || newPattern == null) {
			return inString;
		}

		StringBuffer sbuf = new StringBuffer();

		int pos = 0;
		int index = inString.indexOf(oldPattern);

		int patLen = oldPattern.length();
		while (index >= 0) {
			sbuf.append(inString.substring(pos, index));
			sbuf.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sbuf.append(inString.substring(pos));

		return sbuf.toString();
	}

	/**
	 * 
	 * @param inString
	 * @param pattern
	 * @return String
	 */
	public static String delete(String inString, String pattern) {
		return replace(inString, pattern, " ");
	}

	/**
	 * 
	 * @param inString
	 * @param charsToDelete
	 * @return String
	 */
	public static String deleteAny(String inString, String charsToDelete) {
		if (inString == null || charsToDelete == null) {
			return inString;
		}
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				out.append(c);
			}
		}
		return out.toString();
	}

	/**
	 * 
	 * @param qualifiedName
	 * @return String
	 */
	public static String unqualify(String qualifiedName) {
		return unqualify(qualifiedName, '.');
	}

	/**
	 * 
	 * @param qualifiedName
	 * @param separator
	 * @return String
	 */
	public static String unqualify(String qualifiedName, char separator) {
		return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
	}

	/**
	 * 
	 * @param str
	 * @return String
	 */
	public static String capitalize(String str) {
		return changeFirstCharacterCase(str, true);
	}

	/**
	 * 
	 * @param str
	 * @return String
	 */
	public static String uncapitalize(String str) {
		return changeFirstCharacterCase(str, false);
	}

	/**
	 * 
	 * @param str
	 * @param capitalize
	 * @return String
	 */
	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (str == null || str.length() == 0) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str.length());
		if (capitalize) {
			buf.append(Character.toUpperCase(str.charAt(0)));
		} else {
			buf.append(Character.toLowerCase(str.charAt(0)));
		}
		buf.append(str.substring(1));
		return buf.toString();
	}

	/**
	 * 
	 * @param localeString
	 * @return Locale
	 */
	public static Locale parseLocaleString(String localeString) {
		String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
		String language = parts.length > 0 ? parts[0] : "";
		String country = parts.length > 1 ? parts[1] : "";
		String variant = parts.length > 2 ? parts[2] : "";
		return (language.length() > 0 ? new Locale(language, country, variant) : null);
	}

	/**
	 * 
	 * @param arr
	 * @param str
	 * @return String[]
	 */
	public static String[] addStringToArray(String[] arr, String str) {
		String[] newArr = new String[arr.length + 1];
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		newArr[arr.length] = str;
		return newArr;
	}

	/**
	 * 
	 * @param array
	 * @param delimiter
	 * @return Properties
	 */
	public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
		return splitArrayElementsIntoProperties(array, delimiter, null);
	}

	/**
	 * 
	 * @param array
	 * @param delimiter
	 * @param charsToDelete
	 * @return Properties
	 */
	public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter, String charsToDelete) {

		if (array == null || array.length == 0) {
			return null;
		}

		Properties result = new Properties();
		for (int i = 0; i < array.length; i++) {
			String element = array[i];
			if (charsToDelete != null) {
				element = deleteAny(array[i], charsToDelete);
			}
			String[] splittedElement = split(element, delimiter);
			if (splittedElement == null) {
				continue;
			}
			result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
		}
		return result;
	}

	/**
	 * 
	 * @param str
	 * @param delimiters
	 * @return String[]
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	/**
	 * 
	 * @param str
	 * @param delimiters
	 * @param trimTokens
	 * @param ignoreEmptyTokens
	 * @return String[]
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
			boolean ignoreEmptyTokens) {

		StringTokenizer st = new StringTokenizer(str, delimiters);
		List tokens = new ArrayList();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return (String[]) tokens.toArray(new String[tokens.size()]);
	}

	/**
	 * 
	 * @param str
	 * @param delimiter
	 * @return String[]
	 */
	public static String[] delimitedListToStringArray(String str, String delimiter) {
		if (str == null) {
			return new String[0];
		}
		if (delimiter == null) {
			return new String[] { str };
		}

		List result = new ArrayList();
		int pos = 0;
		int delPos = 0;
		while ((delPos = str.indexOf(delimiter, pos)) != -1) {
			result.add(str.substring(pos, delPos));
			pos = delPos + delimiter.length();
		}
		if (str.length() > 0 && pos <= str.length()) {
			result.add(str.substring(pos));
		}

		return (String[]) result.toArray(new String[result.size()]);
	}

	/**
	 * 
	 * @param str
	 * @return String[]
	 */
	public static String[] commaDelimitedListToStringArray(String str) {
		return delimitedListToStringArray(str, ",");
	}

	/**
	 * 
	 * @param str
	 * @return Set
	 */
	public static Set commaDelimitedListToSet(String str) {
		Set set = new TreeSet();
		String[] tokens = commaDelimitedListToStringArray(str);
		for (int i = 0; i < tokens.length; i++) {
			set.add(tokens[i]);
		}
		return set;
	}

	/**
	 * 
	 * @param arr
	 * @param delim
	 * @return String
	 */
	public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (arr == null) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param coll
	 * @param delim
	 * @param prefix
	 * @param suffix
	 * @return String
	 */
	public static String collectionToDelimitedString(Collection coll, String delim, String prefix, String suffix) {
		if (coll == null) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		Iterator it = coll.iterator();
		int i = 0;
		while (it.hasNext()) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(prefix).append(it.next()).append(suffix);
			i++;
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param coll
	 * @param delim
	 * @return String
	 */
	public static String collectionToDelimitedString(Collection coll, String delim) {
		return collectionToDelimitedString(coll, delim, "", "");
	}

	/**
	 * 
	 * @param arr
	 * @return String
	 */
	public static String arrayToCommaDelimitedString(Object[] arr) {
		return arrayToDelimitedString(arr, ",");
	}

	public synchronized static String convert(String input, String encode) {
		int idx = encode.indexOf(",");
		int len = encode.length();
		if (idx > 0 && len > idx + 1) {
			String srcEncode = encode.substring(0, idx);
			String targetEncode = encode.substring(idx + 1, encode.length());
			try {
				input = convert(input, srcEncode, targetEncode);
			} catch (UnsupportedEncodingException e) {
				return input;
			}
		}
		return input;
	}

	/**
	 * 
	 * @param input
	 * @param srcEncode
	 * @param targetEncode
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public synchronized static String convert(String input, String srcEncode, String targetEncode)
			throws UnsupportedEncodingException {
		input = new String(input.getBytes(srcEncode), targetEncode);
		return input;
	}

	/**
	 * Check string method
	 * @param prmData
	 * @return
	 */
	public static boolean chkChar(String prmData) {
		String tsValidChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_!@@#$%^&*()-+=|~`<>?/.,[]{}:;\"' ";
		try {
			char chData[] = prmData.toCharArray();
			for (int i = 0; i < prmData.trim().length(); i++) {
				if (tsValidChars.indexOf("" + chData[i]) == -1) {
					if (chData[i] >= 0x0020 && chData[i] <= 0x007E) {
						return false;
					}
					if (chData[i] >= 0xFF61 && chData[i] <= 0xFF9F) {
						return false;
					}
					if (Character.isDefined(chData[i]) == false) {
						return false;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Check hangul
	 * @param c
	 * @return boolean
	 */
	public static boolean isHangul(char c) {
		Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);
		return unicodeBlock == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
				|| unicodeBlock == Character.UnicodeBlock.HANGUL_JAMO
				|| unicodeBlock == Character.UnicodeBlock.HANGUL_SYLLABLES;
	}

	public static boolean isInteger(String strVal) {
		int tempVal = -1;
		try {
			tempVal = Integer.parseInt(strVal);
			if (tempVal < 0) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isLong(String strVal) {
		long tempVal = -1;
		try {
			tempVal = Long.parseLong(strVal);
			if (tempVal < 0) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Erase SpecialChar method
	 * @param prmData
	 * @return string
	 */
	public static String chkSpecialChar(String prmData) {
		String tsValidChars = "!@#$%^&*()-+=|~`<>?[]{}\"'";
		StringBuffer buffer = new StringBuffer(256);
		try {
			char chData[] = prmData.toCharArray();
			for (int i = 0; i < prmData.trim().length(); i++) {
				if (tsValidChars.indexOf("" + chData[i]) > -1) {
					chData[i] = ' ';
				}
				buffer.append(chData[i]);
			}
		} catch (Exception e) {
			return "";
		}
		return buffer.toString();
	}

	/**
	 * strToFormatedNumber
	 * @param str
	 * @return String
	 */
	public static String strToFormatedNumber(String str) {
		long value = Long.parseLong(str);
		NumberFormat FORMAT = NumberFormat.getInstance();
		FORMAT.setGroupingUsed(true);
		return FORMAT.format(value);
	}

	public static List stringListSort(List list) {
		class ComparatorThis implements Comparator {
			public int compare(Object o1, Object o2) {
				String s1 = (String) o1;
				String s2 = (String) o2;
				if (s1 == null || s2 == null) {
					return 0;
				}
				return s1.compareTo(s2);
			}
		}
		;

		Comparator comparator = new ComparatorThis();
		Collections.sort(list, comparator);
		return list;
	}

	public static boolean isNullOrEmpty(String str) {
		if (str == null || str.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * str ��start tag ��end tag �ъ씠���곗씠�곕� 諛섑솚
	 * ex) str��<test>abcde</test> �닿퀬, start媛�<test>, end媛�</test> �대㈃ abcde 瑜�諛섑솚.
	 * @param str
	 * @param start
	 * @param end
	 * @return
	 */
	public static String getTagValue(String str, String start, String end) {
		int startIdx = str.indexOf(start);
		int endIdx = str.indexOf(end);

		String title = "";
		if (startIdx > -1 && endIdx > -1 && startIdx < endIdx) {
			title = str.substring(startIdx + start.length(), endIdx);
		}

		return title;
	}

	public static boolean replaceYn(String bool) throws IllegalArgumentException {
		if (bool.equalsIgnoreCase("y") || bool.equalsIgnoreCase("yes")) {
			return true;
		} else if (bool.equalsIgnoreCase("n") || bool.equalsIgnoreCase("no")) {
			return false;
		} else {
			throw new IllegalArgumentException("\"y\", \"yes\", \"n\" ,\"no\"");
		}
	}

	/**
	 * boolean 媛믪쓣 y �먮뒗 n 臾몄옄�대줈 諛섑솚
	 * @param bool
	 * @return
	 */
	public static String replaceYn(boolean bool) {
		String ret = "n";
		if (bool)
			ret = "y";
		return ret;
	}

	/**
	 * array ��checkStr ���덈뒗吏�
	 * @param strArr
	 * @param checkStr
	 * @return
	 */
	public static boolean isExistArray(String[] strArr, String checkStr) {
		for (int i = 0; i < strArr.length; i++) {

			if (strArr[i] != null && strArr[i].equals(checkStr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isExistArray(ArrayList strArr, String checkStr) {

		String[] arr = (String[]) strArr.toArray(new String[strArr.size()]);
		boolean result = isExistArray(arr, checkStr);
		return result;
	}

	public static String join(Object[] array, String separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	public static String join(Object[] array, String separator, int startIndex, int endIndex) {
		if (array == null) {
			return null;
		}
		if (separator == null) {
			separator = "";
		}

		// endIndex - startIndex > 0: Len = NofStrings *(len(firstString) + len(separator))
		// (Assuming that all Strings are roughly equally long)
		int bufSize = (endIndex - startIndex);
		if (bufSize <= 0) {
			return "";
		}

		bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + separator.length());

		StringBuffer buf = new StringBuffer(bufSize);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	public static String replaceArrayToString(int[] data, String delim) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			sb.append(String.valueOf(data[i])).append(delim);
		}
		String result = sb.toString();
		if (data.length > 0) {
			sb.substring(0, result.length() - delim.length());
		}
		return result;
	}

	// chzin
	public static String applySecurity(String str) {
		
		//str = str.replaceAll("(01[016789])[-~.[\\s]][0-9]{3,4}[-~.[\\s]][0-9]{4}", "$1-****-****");		// �꾪솕踰덊샇
		str = str.replaceAll("([01][0-9]{5})[,~-]+[1-4][0-9]{6}|([2-9][0-9]{5})[[\\s],~-]+[1-2][0-9]{6}","$1$2-*******");	// 二쇰�踰덊샇
		str = str.replaceAll("([0-9]{2})[-~.[\\s]][0-9]{6}[-~.[\\s]][0-9]{2}", "$1-******-**");		// �댁쟾硫댄뿀踰덊샇
		str = str.replaceAll("[a-zA-Z]{2}[-~.[\\s]][0-9]{7}", "**-*******");		// �ш텒踰덊샇
		str = str.replaceAll("([34569][0-9]{3})[-~.[\\s]]([0-9]{4})[-~.[\\s]]([0-9]{4})[-~.[\\s]]([0-9]{4})", "$1-****-****-****");		// �좎슜移대뱶踰덊샇
		str = str.replaceAll("([1257])[-~.[\\s]][0-9]{10}", "$1-**********");		// 嫄닿컯蹂댄뿕踰덊샇
		str = str.replaceAll("([0-9]{2}[-~.[\\s]][0-9]{2}[-~.[\\s]][0-9]{6}"
				+ "|[0-9]{3}[-~.[\\s]]([0-9]{5,6}[-~.[\\s]][0-9]{3}"
				+ "|[0-9]{6}[-~.[\\s]][0-9]{5}"
				+ "|[0-9]{2,3}[-~.[\\s]][0-9]{6}"
				+ "|[0-9]{2}[-~.[\\s]][0-9]{7}"
				+ "|[0-9]{2}[-~.[\\s]][0-9]{4,6}[-~.[\\s]][0-9]"
				+ "|[0-9]{5}[-~.[\\s]][0-9]{3}[-~.[\\s]][0-9]{2}"
				+ "|[0-9]{2}[-~.[\\s]][0-9]{5}[-~.[\\s]][0-9]{3}"
				+ "|[0-9]{4}[-~.[\\s]][0-9]{4}[-~.[\\s]][0-9]{3}"
				+ "|[0-9]{6}[-~.[\\s]][0-9]{2}[-~.[\\s]][0-9]{3}"
				+ "|[0-9]{2}[-~.[\\s]][0-9]{2}[-~.[\\s]][0-9]{7})"
				+ "|[0-9]{4}[-~.[\\s]]([0-9]{3}[-~.[\\s]][0-9]{6}"
				+ "|[0-9]{2}[-~.[\\s]][0-9]{6}[-~.[\\s]][0-9])"
				+ "|[0-9]{5}[-~.[\\s]][0-9]{2}[-~.[\\s]][0-9]{6}"
				+ "|[0-9]{6}[-~.[\\s]][0-9]{2}[-~.][0-9]{5,6})","**********");		// 怨꾩쥖踰덊샇
		return str;
	}
	
	public static String getWhiteSpace(String str) {
		str = StringUtil.replace(str, "<br>", "\n");
		str = StringUtil.replace(str, "<BR>", "\n");
		str = StringUtil.replace(str, "<bR>", "\n");
		str = StringUtil.replace(str, "<Br>", "\n");
		str = StringUtil.replace(str, "\r\n", "\n");
		str = StringUtil.replace(str, "\n\n", "\n");
		
		String spl[] = StringUtil.delimitedListToStringArray(str, "\n");
		StringBuffer ret = new StringBuffer();
		int iLen = 0;
		String temp = "";
		
		for(int idx = 0; idx < spl.length; idx++) {
			spl[idx] = spl[idx].trim();
			iLen = spl[idx].indexOf(")");
			
			if(iLen > -1 && iLen < 10 && spl[idx].substring(0, iLen).trim().indexOf("-") > -1) {
				temp = StringUtil.replace(spl[idx].substring(0, iLen).trim(), "-", "");
				
				iLen = StringUtil.parseInt(temp, -1);
				if(iLen > -1) {
					ret.append("  " + spl[idx].trim());
				}
			} else {
				ret.append(spl[idx]);
			}
			ret.append("\n");
		}

		return ret.toString();
	}
	
	public static String splitDataString(String str) {
		String delimiter = "\t\"";
		
		str = StringUtil.replace(str,  "\"\"", "§");
		StringBuffer result = new StringBuffer();
		int pos = 0;
		int delPos = 0;
		int subpos = 0;
		
		if(str != null) {
			while((delPos = str.indexOf(delimiter, pos)) !=1) {
				result.append(str.substring(pos, delPos));
				result.append(delimiter);
				pos = delPos + delimiter.length();
				
				subpos = str.indexOf("\"", pos);
				if(subpos > -1) {
					result.append(StringUtil.replace(str.substring(pos, subpos + 1), "\t", "∬"));
					pos = subpos + 1;
				}				
			}
			if(str.length() > 0 && pos <= str.length()) {
				result.append(str.substring(pos));
			}
		}
		
		return replace(StringUtil.replace(result.toString(), "\"", " "), "§", "\"");
	}
	
	public static String removeChar(String str) {
		String ret = str;
		ret = ret.replaceAll("!\"#[$]%&\\(\\)\\{\\}@`[*]:[+];-.<>,\\^~|'\\[\\]", "");
		
		
		return ret;
	}
	
	public static String removeTrimChar(String str) {
		if(str==null) {
			return "";
		}
		String ret = replace(str," ","");
		ret = ret.replaceAll("!\"#[$]%&\\(\\)\\{\\}@`[*]:[+];-.<>,\\^~|'\\[\\]", "");
		
		
		return ret;
	}
	
	
	public static boolean checkEngDictionary(String str,String sep){
		StringBuilder sb = new StringBuilder();
		
		
		int sLen = str.length();
		int wlen = sep.length();
		
		if(wlen>0){
			int totalCnt = 0;
			for (int i = 0; i < wlen; i++) {
				//alert(sepValue.charAt(0));
				String oneChar = sep.charAt(i)+"";
				totalCnt+=Integer.parseInt(oneChar); 
			}
			if(totalCnt!=0){
				 
				//alert(totalCnt+"//"+wordLen);
				if(totalCnt!=sLen){
					System.out.println("분리정보의 숫자가 맞지 않습니다. [ "+str+ " : "+sep + " ]");
					return false;
				}
			}

				 
		}
		
		return true;
	}
	
	
	public static String getEngDictionary(String str,String sep){
		StringBuilder sb = new StringBuilder();
		
		
		int sLen = str.length();
		int wlen = sep.length();
		//sb.append(str);
		//sb.append("\t");
		if(wlen>0){
			int offset = 0;
			for (int i = 0; i < wlen; i++) {
				//alert(sepValue.charAt(0));
				String oneChar = sep.charAt(i)+"";
				int oLen=Integer.parseInt(oneChar); 
				if(offset+oLen <= sLen){
					oneChar = str.substring(offset,oLen);
					if(i!=0){
						sb.append("+");
					}
					sb.append(oneChar+"/NC");
				}
			}
			sb.append("@"+str+"/NC");
				 
		}
		
		return sb.toString();
	}



	public static String splitDictionary(String str,String sep){
		StringBuilder sb = new StringBuilder();
		int sLen = str.length();
		int wlen = sep.length();

		if(wlen>0){

			int offset = 0;
			for (int i = 0; i < wlen; i++) {
				String oneChar = sep.charAt(i)+"";
				int oLen=Integer.parseInt(oneChar);
				if( offset + oLen <= sLen){
					String cutString = str.substring(offset, offset + oLen);
					sb.append(cutString);
				}

				offset = offset + oLen  ;

				if ( i + 1 < wlen) {
					sb.append(",");
				}
			}

		}

		return sb.toString();
	}


	public static HashMap<String,String> transeMap(HashMap<String,Object> map){
		HashMap<String,String> ret = new HashMap<String,String>();
		// 방법2
        for( Map.Entry<String, Object> elem : map.entrySet() ){
        	if(elem.getValue()==null){
        		ret.put(elem.getKey(), "");
        	}else{
        		ret.put(elem.getKey(), elem.getValue().toString());
        	}
        }
        return ret;

 
	}
	
	public static Map<String,String> transeMap(Map<String,Object> map){
		HashMap<String,String> ret = new HashMap<String,String>();
		// 방법2
        for( Map.Entry<String,Object> elem : map.entrySet() ){
        	if(elem.getValue()==null){
        		ret.put(elem.getKey(), "");
        	}else{
        		ret.put(elem.getKey(), elem.getValue().toString());
        	}
        }
        return ret;
	}
	public static Boolean nvl(Boolean s){return s==null?false:s;}
	public static String nvl(String s){return s==null?"":s;}
	public static String nvl(Object s){return s==null?"":s.toString();}
	public static String nvl(String s, String d){return s==null?d:s;}
	public static String nvl(Object s, String d){return s==null  ? d : s.toString();}
	public static int    nvl(String s, int    d){if (s==null) return d;try{return Integer.parseInt(s) ;}catch(NumberFormatException e){return d;}}
	public static long   nvl(String s, long   d){if (s==null) return d;try{return Long.parseLong(s)   ;}catch(NumberFormatException e){return d;}}
	public static float  nvl(String s, float  d){if (s==null) return d;try{return Float.parseFloat(s) ;}catch(NumberFormatException e){return d;}}
	public static double nvl(String s, double d){if (s==null) return d;try{return Double.parseDouble(s);}catch(NumberFormatException e){return d;}}
	public static int    nvlObject(Object s, int    d){if (s==null) return d;try{return Integer.parseInt(String.valueOf(s)) ;}catch(NumberFormatException e){return d;}}
	public static int    nvl(Object s, int    d){if (s==null) return d;try{return Integer.parseInt(s.toString()) ;}catch(NumberFormatException e){return d;}}

	public static int    nvl(Integer ii, int    i){if (ii==null) return i;return ii.intValue() ;}
	public static int    isZero(Integer ii, int    i){if (ii==0) return i;return ii.intValue() ;}

	public static int    nvlNum2Integer(Object s, int    d){
		if (s==null) return d;

		try
		{
			int num = Integer.parseInt(s.toString());
			return num;
		} catch(NumberFormatException e) {}


		try
		{
			int num = ((Double) s).intValue() ;
			return num;
		} catch(NumberFormatException e) {}

		try
		{
			int num = ((Long) s).intValue() ;
			return num;
		} catch(NumberFormatException e) {}

		try
		{
			int num = ((int) s) ;
			return num;
		} catch(NumberFormatException e) {}

		try
		{
			int num = ((Float) s).intValue() ;
			return num;
		} catch(NumberFormatException e) {

		}


		return d;
	}



	public static int currentPage(String page){
		int currentPage=0;
		if(page == null || page.trim().isEmpty() || page.equals("0")) {
			currentPage = 1;
		} else {
			currentPage = Integer.parseInt(page);
		}
		
		return currentPage;
	}
	
	 
	public static String isNull(String str,String ch_str){
		if(str==null){
			return ch_str;
		}
		else if(str.equals("null") || str.equals("")){
			return ch_str;
		}
		else{
			return str;
		}
		
	}
	
	 
	 public static String encodingUTF8(String str){
		 
		 if(str != null){
		 
			 String encode_str = "";
			try {
				encode_str = new String(str.getBytes("8859_1"),"utf-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("UnsupportedEncodingException " + e.getMessage(), e);
			}
			 return encode_str;
		 }
		 else return str;
			 
	 }
	
	/**
	 * 'yyyyMMdd'형식으로 리턴
	 * @return
	 */
	public static String getCurrDate(){
		
		String currDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		
		return currDate;
	}
	
	/**
	 * 형식에 맞는 날짜를 리턴한다.
	 * @param dateType
	 * @return
	 */
	public static String getCurrDate(String dateType){
		
		String currDate = "";
		
		if(dateType != null){
			currDate = new SimpleDateFormat(dateType).format(new Date());
		}
		
		return currDate;
	}
	
	/**
	  * 이전달을 'yyyyMMdd'형식으로 리턴
	 * @param yearMonth
	 * @return
	 */
	public static String getBeforeYearMonthByYM(String yearMonth) {
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		 Calendar cal = Calendar.getInstance();
		 int year = Integer.parseInt(yearMonth.substring(0, 4));
		 int month = Integer.parseInt(yearMonth.substring(4, 6));
		 cal.set(year, month - 1, 0);
		 String beforeYear = dateFormat.format(cal.getTime()).substring(0, 4);
		 String beforeMonth = dateFormat.format(cal.getTime()).substring(4, 6);
		 String result = beforeYear + beforeMonth;
		 return result;
	 }
	
	/**
	  * 다음달을 'yyyyMMdd'형식으로 리턴
	 * @param yearMonth
	 * @return
	 */
	public static String getAfterYearMonthByYM(String yearMonth) {
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		 Calendar cal = Calendar.getInstance();
		 int year = Integer.parseInt(yearMonth.substring(0, 4));
		 int month = Integer.parseInt(yearMonth.substring(4, 6));
		 cal.set(year, month + 1, 0);
		 String beforeYear = dateFormat.format(cal.getTime()).substring(0, 4);
		 String beforeMonth = dateFormat.format(cal.getTime()).substring(4, 6);
		 String result = beforeYear + beforeMonth;
		 return result;
	 }
	
	/**
	 * date1을 date2와 비교하여 작으면-1, 같으면 0, 크다면 1을 리턴한다 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareDate(String date1, String date2){
			
		int result = 0;
		
		try {
			Date startDt1 = new SimpleDateFormat("yyyyMMdd").parse(date1);
			Date startDt2 = new SimpleDateFormat("yyyyMMdd").parse(date2);
			
			result = startDt1.compareTo(startDt2);
			
		} catch (ParseException e) {
			logger.error("ParseException " + e.getMessage(), e);
		}	
		
		return result;
	}
	
	
	/**
	 * 파일 확장명을 추출해서 리턴한다. 
     * 
	 * @param filename
	 * @return
	 */	
	public static String getFileExtension(String filename)
	{
		int idx = filename.lastIndexOf(".");
		if (idx > 0) return filename.substring(idx + 1);
		return "";
	}
	
	/**
	 * 파일 확장명을 제거해서 파일명을 리턴한다. 
     * 
	 * @param filename
	 * @return
	 */	
	public static String getFileFilename(String filename)
	{
		int idx = filename.lastIndexOf(".");
		if (idx > 0) return filename.substring(0, idx);
		return "";
	}
	
	/**
     * setDecimalFormat : String 숫자를  Format형태로
     * form = ,##0.0##
     * 
	 * @param str
	 * @param form
	 * @return 포맷된 숫자
	 */
	public static String setDecimalFormat( String str, String form ) {
    	String rtnVal = "";
    	if ( str == null ) str = "";
    	if ( isStringDouble(str) ) 
    		rtnVal = new DecimalFormat(form).format(Double.parseDouble(str));
    	return rtnVal;
    }
    
	/**
	 * isStringDouble : 숫자여부 확인
	 * @param s
	 * @return 숫자여부(true or false)
	 */
    public static boolean isStringDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    
//
//	/**
//     * XstreamAlias를 정의한 객체로 JSON 문자열로 생성시킨다.
//     * 
//     * @param xstreamAliasObj
//     * @return jsonString
//     */	
//	public static String object2json(Object xstreamAliasObj){
//		String jsonStr = "";
//        //JSON 결과출력
//		
//		XStream xs = new XStream(new JsonHierarchicalStreamDriver() {
//			public HierarchicalStreamWriter createWriter(Writer writer) {
//		        return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
//		    }
//		});
//		
//		xs.autodetectAnnotations(true); // XML에서 셋팅해주는게 안먹음.
//		xs.setMode(XStream.NO_REFERENCES);
//		
//		jsonStr = xs.toXML(xstreamAliasObj);     
//		 
//		
//		return jsonStr;
//	}
	
	
	/**
     * 배열을 split 문자열로 구분자로 한 문자로 생성한다.
     * 
     * @param src
     * @param split : split 위치
     * @return String
     */
	public static String arr2string(String[] src, String split){
		
		StringBuffer sb = new StringBuffer();
		if(src != null && src.length > 0){							
			for(int i=0; i<src.length; i++){
				sb.append(src[i]);
				if (1+1 < sb.length() ){
					sb.append(split);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 배열을 split 문자열로 구분자로 한 문자로 생성한다.
	 *
	 * @param src
	 * @return String
	 */
	public static String[] list2array(List<String> src){

		String [] str = new String[src.size()];

		if(src != null && src.size() > 0){
			for(int i=0 ; i < src.size(); i++){
				str[i] = src.get(i);
			}
		}
		return str;
	}

	/**
	 * 배열을 split 문자열로 구분자로 한 문자로 생성한다.
	 *
	 * @param src
	 * @return String
	 */
	public static String list2splitString(List<String> src, String split){

		if ( split == null ) split = ",";

		StringBuffer sb = new StringBuffer();

		if(src != null && src.size() > 0){
			for(int i=0 ; i < src.size(); i++){
				sb.append(src.get(i));
				if ( i + 1 < src.size() )
					sb.append(split);
			}
		}
		return sb.toString();
	}

    /*
     * 어제 일자 조회 yyyymmdd
     */
	public static String getYesterdayYMD() {
		final Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		DateFormat dt = new SimpleDateFormat("yyyyMMdd");
		return dt.format(yesterday.getTime());
	}
	
	 
	
	public static List<String> getIntervalDate(String s1, String s2){
		List<String> result = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			Date d1 = df.parse(s1);
			Date d2 = df.parse(s2);
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(d1);
			c2.setTime(d2);
			while(c1.compareTo(c2) !=1 ){
				result.add(sdf.format(c1.getTime()).toString());
				c1.add(Calendar.DATE, 1);
			}
		} catch (ParseException e) {
			logger.error("ParseException " + e.getMessage(), e);
		}
		return result;
	}
	
	public static List<String> getIntervalMonth(String s1, String s2){
		List<String> result = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			Date d1 = df.parse(s1);
			Date d2 = df.parse(s2);
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(d1);
			c2.setTime(d2);
			String yyyymm = "";
			while(c1.compareTo(c2) !=1 ){
				String temp = sdf.format(c1.getTime()).toString().substring(0, 6);
				if(! yyyymm.equals(temp) ){
					yyyymm = temp;
					result.add(yyyymm);
//					System.out.println(yyyymm);
				}
				
				c1.add(Calendar.DATE, 1);
			}
		} catch (ParseException e) {
			logger.error("ParseException " + e.getMessage(), e);
		}
		return result;
	}
	
	/**
    *
    * @param s   date string you want to check.
    * @param format string representation of the date format. For example, "yyyy-MM-dd".
    * @return  date Date
    * @throws ParseException error info
    */
   public static Date check(String s, String format) throws ParseException {
       if ( s == null )
           throw new ParseException("date string to check is null", 0);
       if ( format == null )
           throw new ParseException("format string to check date is null", 0);

       SimpleDateFormat formatter =
               new SimpleDateFormat (format, Locale.KOREA);
       Date date = null;
       try {
           date = formatter.parse(s);
       } catch(ParseException e) {
           throw new ParseException(" wrong date:\"" + s + "\" with format \"" + format + "\"", 0);
       }

       if ( ! formatter.format(date).equals(s) )
           throw new ParseException(
                   "Out of bound date:\"" + s + "\" with format \"" + format + "\"",
                   0
           );
       return date;
   }
	
   public static int whichDay(String s, String format) throws ParseException {
        SimpleDateFormat formatter =
                new SimpleDateFormat (format, Locale.KOREA);
        Date date = check(s, format);

        Calendar calendar = formatter.getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
   
   /**
    * @return String
    */
   public static String[] getScheduleTime(){
       Date date = new Date();
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(date);
       SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd/HH/mm", Locale.KOREA);
       String[] retDate = StringUtil.split(sf.format(date),"/");
       String[] newDate = new String[retDate.length+1];
       System.arraycopy(retDate, 0, newDate, 1, retDate.length);
       newDate[0]=""+calendar.get(Calendar.DAY_OF_WEEK);
      
       return newDate;
   }
	
   public static String toString(Object value,String def) {
		String ret = def;
		try {
			if(value!=null) {
				ret = value.toString();
			}
		}catch(Exception e) {
			logger.error("Exception " + e.getMessage(), e);
		}
		return ret;
	}
   
	public static String toCamelCase(String target) {
		StringBuffer buffer = new StringBuffer();
		for (String token : target.toLowerCase().split("_")){
			buffer.append(StringUtils.capitalize(token));
		}
		return StringUtils.uncapitalize(buffer.toString());
	}
	
	public static List<Map<String, Object>> mapComparator(List<Map<String, Object>> resultList, String field) {
		Collections.sort(resultList, new Comparator<Map<String, Object>>(){
			@Override
			public int compare(Map<String, Object> first, Map<String, Object> second) {
				int cnt1 = Integer.valueOf((String)(first.get(field)+""));
				int cnt2 = Integer.valueOf((String)(second.get(field)+""));
				return cnt1 < cnt2 ? -1 : cnt1> cnt2 ? 1 : 0; //오름차순
				//return cnt1 > cnt2 ? -1 : cnt1< cnt2 ? 1 : 0; //내림차순
			}
		});
		return resultList;
	}
	
	public static String  getStandardKeyword(String text) {
	  	StringBuilder keyword = new StringBuilder();
		if(text==null) {
			return "";
		}
		char[] chList = text.toCharArray();
		boolean isAppend = false;
		for(char ch:chList) {
			//System.out.print( ch +"/"+isAppend );
			//System.out.print(  ":"+Character.isLetter(ch) );
			//System.out.print( ":"+Character.isWhitespace(ch) );
			//System.out.print( ":"+Character.isDigit(ch) );
			//System.out.println(); 
			
			if(Character.isLetter(ch) || Character.isDigit(ch)  ) {
				if(!(ch=='ˇ' || ch=='ː')) {
					keyword.append(ch);
					isAppend = true;
					continue;
				}
			}else if( isAppend && (ch=='.' || ch=='-' )) { //|| ch=='+'|| ch=='@'|| ch=='#'|| ch=='$'|| ch=='%'|| ch=='^'|| ch=='*'|| ch=='='
				keyword.append(ch);
				isAppend = true;
				continue;
			} 
			isAppend = false;
		};
		String ret = removeTrimChar(keyword.toString());
		keyword.setLength(0);
		return ret;
	}
	
	public static String jsonValidator(String json ) {

        json = StringUtil.replace(json,"&lt;","<");
        json = StringUtil.replace(json,"&gt;",">");
        json = StringUtil.replace(json,"&quot;","\"");
        json = StringUtil.replace(json,"&#39;","'");

        return json ;
    }
	
  	public static String queryCombination(String query) {
  		StringJoiner sj = new StringJoiner("||");
  		String[] array = query.split(" ");
      
  		if (array.length > 1) {
  			for (int i=0; i<array.length-1; i++) {
  	  			sj.add("(" + array[i] + " " + array[i + 1] + ")");
  	  		}
  	      
  	  		return sj.toString();	
  		} else {
  			return query;
  		}
  	} 
	/*
	public static void main(String [] args) {

   		String str = "나라말사미";
   		String seperator = "311";

   		String ret = splitDictionary(str,seperator);
   		System.out.println(ret);


	}
	*/
}
