package kr.co.proten.llmops.index.provsearch;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProUtils {
	private static String TOKEN_KEY = "PROTEN_ENCPT_KEY";

	/**
	 * 문자열에 값을 비교해서 리턴한다.
	 */
	public static int findArrayValue(String find, String[] arr) {
		int findKey = -1;

		for (int i = 0; i < arr.length; i++) {
			if (find.equals(arr[i])) {
				findKey = i;

				break;
			}
		}

		return findKey;
	}

	/**
	 * 문자열에 값을 비교해서 리턴한다.
	 */
	public static int findArrayValue(String find, String[] arr, String split) {
		int findKey = -1;

		for (int i = 0; i < arr.length; i++) {
			if (arr[i].indexOf(split) != -1)
				arr[i] = arr[i].split(split)[0];

			if (find.equals(arr[i])) {
				findKey = i;

				break;
			}
		}

		return findKey;
	}

	public static String getHighlightTag(String data, String sTag, String eTag) {
		data = ProUtils.replace(data, "<em>", sTag);
		data = ProUtils.replace(data, "</em>", eTag);

		return data;
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

		if (strNum.indexOf(".") > 0) {
			strNum = strNum.substring(0, strNum.indexOf("."));
		}

		try {
			return Integer.parseInt(strNum);
		} catch (Exception e) {
			return def;
		}
	}

	/**
	 * 배열을 split 문자열로 구분자로 한 문자로 생성한다.
	 *
	 * @param src
	 * @return String
	 */
	public static String[] list2array(List<String> src) {
		String[] str = new String[src.size()];

		if (src != null && src.size() > 0) {
			for (int i = 0; i < src.size(); i++) {
				str[i] = src.get(i);
			}
		}
		
		return str;
	}

	public static String list2String(List<String> list, String split) {
		StringBuffer sb = new StringBuffer();
		
		for (int idx = 0; idx < list.size(); idx++) {
			String str = (String) list.get(idx);
			sb.append(str);
		
			if (idx + 1 < list.size()) {
				sb.append(split);
			}
		}
		
		return sb.toString();
	}

	public static String[] split(String splittee, String splitChar) {
		return split(splittee, splitChar, 0);
	}

	/**
	 * 구분자를 가지고 있는 문자열을 구분자를 기준으로 나누어주는 메소드
	 * 
	 * @param splittee  구분자를 가진 문자열
	 * @param splitChar 구분자
	 * @return String[]
	 */
	public static String[] split(String splittee, String splitChar, int limit) {
		String taRetVal[];
		StringTokenizer toTokenizer;
		int tnTokenCnt;

		try {
			toTokenizer = new StringTokenizer(splittee, splitChar);
			tnTokenCnt = toTokenizer.countTokens();
			
			if (limit != 0 && tnTokenCnt > limit) {
				tnTokenCnt = limit;
			}
			
			taRetVal = new String[tnTokenCnt];

			for (int i = 0; i < tnTokenCnt; i++) {
				if (toTokenizer.hasMoreTokens()) {
					taRetVal[i] = toTokenizer.nextToken();
				}
				
				if (limit != 0 && limit == (i + 1)) {
					break;
				}
			}
		} catch (Exception e) {
			taRetVal = new String[0];
		}
		
		return taRetVal;
	}

	/**
	 * public static String list2Array(List<String> list, String split) {
	 * StringBuffer sb = new StringBuffer(); for ( int idx = 0 ; idx < list.size();
	 * idx++) { String str = (String) list.get(idx); sb.append(str); if ( idx +1 <
	 * list.size() ) sb.append(split); } return sb.toString(); }
	 * 
	 * public static String list2String(List<String> list) { StringBuffer sb = new
	 * StringBuffer(); for ( int idx = 0 ; idx < list.size(); idx++) { String str =
	 * (String) list.get(idx); sb.append(str); if ( idx +1 < list.size() )
	 * sb.append(","); } return sb.toString(); }
	 * 
	 * 
	 * public static String nvl(Object value,String def) { String ret = def;
	 * if(value!=null) { ret = value.toString(); } return ret; }
	 * 
	 * public static String nvl(String name, String defaultValue) { if ( name ==
	 * null || name.trim().equals("")) { return defaultValue; } return name; }
	 * 
	 * public static String checkboxTrueFalseString(String [] params, String
	 * trueValue, String falseValue) { if ( params == null || params.length == 0 ) {
	 * return falseValue; } else { return trueValue; } }
	 * 
	 * 
	 * public static String trim(String str) { return str == null ? null :
	 * str.trim(); }
	 * 
	 * 
	 * 
	 * public static final String []
	 * getParameters(javax.servlet.http.HttpServletRequest request, String _param,
	 * int _size) { String [] params = new String[_size]; for ( int idx=0;
	 * idx<_size; idx++) { try { params[idx] = nvl(request.getParameter(_param +
	 * idx),""); } catch ( Exception e) { params[idx] = ""; } } return params; }
	 * 
	 * /** request null체크
	 **/
	public static String getCheckReq(String parameter, String defaultValue) {
		return (parameter != null && !parameter.isEmpty()) ? parameter : defaultValue;
	}

//	/**
//	 * request null체크
//	 **/
//	public static String getCheckReq(javax.servlet.http.HttpServletRequest req, String parameter,
//			String default_value) {
//		String req_value = req.getParameter(parameter) != null && !"".equals(req.getParameter(parameter))
//				? req.getParameter(parameter)
//				: default_value;
//
//		return req_value;
//	}
//
//	/**
//	 * request Array null체크
//	 **/
//	public String[] getCheckReqs(javax.servlet.http.HttpServletRequest req, String parameter, String[] default_value) {
//		String[] req_value = req.getParameterValues(parameter);
//		String[] tmp = null;
//		int c = 0;
//
//		if (req_value != null) {
//			tmp = new String[req_value.length];
//
//			for (int i = 0; i < req_value.length; i++) {
//				tmp[c] = req_value[i];
//
//				c++;
//			}
//		}
//
//		req_value = req.getParameterValues(parameter) != null ? tmp : default_value;
//
//		return req_value;
//	}

	public static boolean isNull(String string) {
		return "".equals(checkNull(string));
	}

	/**
	 * String의 값이 null일 경우 ""로 변환하여 리턴한다.
	 * 
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

	public static String getCurrentDate(String stype) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(stype, Locale.KOREA);
		
		return dateFormat.format(new Date());
	}

	public static String getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
		
		return dateFormat.format(new Date());
	}

	/**
	 *
	 * @param str
	 * @param outFormat
	 * @return
	 */
	public static String numberFormat(String str, String outFormat) {
		return new DecimalFormat(outFormat).format(str);
	}

	/**
	 *
	 * @param num
	 * @return
	 */
	public static String numberFormat(int num) {
		return NumberFormat.getNumberInstance().format(num);

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

		StringBuffer sbBuffer = new StringBuffer();

		int pos = 0;
		int index = inString.indexOf(oldPattern);
		int patLen = oldPattern.length();
		
		while (index >= 0) {
			sbBuffer.append(inString.substring(pos, index));
			sbBuffer.append(newPattern);
		
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		
		sbBuffer.append(inString.substring(pos));

		return sbBuffer.toString();
	}

	public static String convertDateFormat(String strDate, String format, String targetFormat) throws ParseException {
		Date date = new SimpleDateFormat(format).parse(strDate);
		
		return new SimpleDateFormat(targetFormat).format(date);
	}

	public static String nvl(String s) {
		return s == null ? "" : s;
	}

	public static String nvl(Object s) {
		return s == null ? "" : s.toString();
	}

	public static String nvl(String s, String d) {
		return s == null ? d : s;
	}

	public static String nvl(Object s, String d) {
		return s == null ? d : s.toString();
	}

	public static int nvl(String s, int d) {
		if (s == null) {
			return d;
		}
		
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return d;
		}
	}

	public static long nvl(String s, long d) {
		if (s == null) {
			return d;
		}
		
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			return d;
		}
	}

	public static float nvl(String s, float d) {
		if (s == null) {
			return d;
		}
		
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return d;
		}
	}

	public static double nvl(String s, double d) {
		if (s == null) {
			return d;
		}
		
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return d;
		}
	}

	public static int nvlObject(Object s, int d) {
		if (s == null) {
			return d;
		}
		
		try {
			return Integer.parseInt(String.valueOf(s));
		} catch (NumberFormatException e) {
			return d;
		}
	}

	public static int nvl(Object s, int d) {
		if (s == null) {
			return d;
		}
		
		try {
			return Integer.parseInt(s.toString());
		} catch (NumberFormatException e) {
			return d;
		}
	}

	public static int nvl(Integer ii, int i) {
		if (ii == null) {
			return i;
		}
		
		return ii.intValue();
	}

	public static int isZero(Integer ii, int i) {
		if (ii == 0) {
			return i;
		}
		
		return ii.intValue();
	}

	/**
	 * Request 에 배열로 들어오는 내용들을 해쉬테이블에 담는다. index별로 조건을 걸기 위한 기능으로 기본 구분자는 ; 이다.
	 * index구분이 없으면 전체 index에 대해 조건이 부여된다. ex) index1:value;index2:value index 에
	 * TOTAL 일 경우에는 전체 index에 대한 기준으로 판단한다.
	 **/

	public static Hashtable getRequestHash(String str) {
		return getRequestHash(str, ";");
	}

	public static Hashtable getRequestHashFilter(String str) {
		return getRequestHashFilter(str, ";");
	}

	public static Hashtable getRequestHash(String str, String split) {
		Hashtable hs = new Hashtable();
		
		if (!str.equals("")) {
			String[] datas = str.split(split);
			
			for (int idx = 0; idx < datas.length; idx++) {
				String[] exs = datas[idx].split("@");
				
				if (exs.length == 1) {
					if (hs.containsKey("TOTAL")) {
						String hsdata = (String) hs.get("TOTAL");
						hsdata = hsdata + " AND " + exs[0];
				
						hs.remove("TOTAL");
						hs.put("TOTAL", hsdata);
					} else {
						hs.put("TOTAL", exs[0]);
					}
				} else if (exs.length == 2) {
					if (hs.containsKey(exs[0])) {
						String hsdata = (String) hs.get(exs[0]);
						hsdata = hsdata + " AND " + exs[1];
						
						hs.remove(exs[0]);
						hs.put(exs[0], hsdata);
					} else {
						hs.put(exs[0], exs[1]);
					}
				}
			}
		}
		
		return hs;
	}

	public static Hashtable getRequestHash(String[] indexs, String str) {
		return getRequestHash(indexs, str, ";");
	}

	public static Hashtable getRequestHash(String[] indexs, String str, String split) {
		Hashtable hs = new Hashtable();
		
		if (!str.equals("")) {
			String[] datas = str.split(split);
		
			for (int idx = 0; idx < datas.length; idx++) {
				String[] exs = datas[idx].split("@");
			
				if (exs.length == 2) {
					if (exs[0].equals("TOTAL")) {
						for (int kdx = 0; kdx < indexs.length; kdx++) {
							if (hs.containsKey(indexs[kdx])) {
								String hsdata = (String) hs.get(indexs[kdx]);
								hsdata = hsdata + " " + exs[1];
				
								hs.remove(indexs[kdx]);
								hs.put(indexs[kdx], hsdata);
							} else {
								hs.put(indexs[kdx], exs[1]);
							}
						}
					} else {
						if (hs.containsKey(exs[0])) {
							String hsdata = (String) hs.get(exs[0]);
							hsdata = hsdata + " " + exs[1];
							
							hs.remove(exs[0]);
							hs.put(exs[0], hsdata);
						} else {
							hs.put(exs[0], exs[1]);
						}
					}
				} else {
					for (int kdx = 0; kdx < indexs.length; kdx++) {
						if (hs.containsKey(indexs[kdx])) {
							String hsdata = (String) hs.get(indexs[kdx]);
							hsdata = hsdata + " " + exs[0];
							
							hs.remove(indexs[kdx]);
							hs.put(indexs[kdx], hsdata);
						} else {
							hs.put(indexs[kdx], exs[0]);
						}
					}
				}
			}
		}
		
		return hs;
	}

	public static Hashtable getRequestHashFilter(String str, String split) {
		Hashtable hs = new Hashtable();

		if (!str.equals("")) {
			str = replace(str, "'", "\"");
			str = replace(str, "'", "\"");
			String[] datas = str.split(split);
		
			for (int idx = 0; idx < datas.length; idx++) {
				String[] exs = datas[idx].split(":");
			
				if (exs.length == 1) {
					hs.put("TOTAL", exs[0]);
				} else if (exs.length == 2) {
					hs.put(exs[0], exs[1]);
				}
			}
		}
		
		return hs;
	}

//	/**
//	 * request XSS 처리
//	 **/
//	public static String getRequestXSS(javax.servlet.http.HttpServletRequest req, String parameter,
//			String default_value) {
//		String req_value = (req.getParameter(parameter) == null || req.getParameter(parameter).equals(""))
//				? default_value
//				: req.getParameter(parameter);
//		req_value = req_value.replaceAll("</?[a-zA-Z][0-9a-zA-Z가-\uD7A3ㄱ-ㅎ=/\"\'%;:,._()\\-# ]+>", "");
//		req_value = req_value.replaceAll(">", "");
//		req_value = req_value.replaceAll("<", "");
//
//		return req_value;
//	}

	public static String xmlEscape(String originalUnprotectedString) {
		if (originalUnprotectedString == null) {
			return "";
		}
		
		boolean anyCharactersProtected = false;

		StringBuffer stringBuffer = new StringBuffer();
		
		for (int i = 0; i < originalUnprotectedString.length(); i++) {
			char ch = originalUnprotectedString.charAt(i);

			boolean controlCharacter = ch < 32;
			boolean unicodeButNotAscii = ch > 126;
			boolean characterWithSpecialMeaningInXML = ch == '&';

			if (characterWithSpecialMeaningInXML || controlCharacter) {
				stringBuffer.append("&#" + (int) ch + ";");
				anyCharactersProtected = true;
			} else {
				stringBuffer.append(ch);
			}
		}
		
		if (anyCharactersProtected == false) {
			return originalUnprotectedString;
		}

		return stringBuffer.toString();
	}

	public static String jsonEscape(String s) {
		if (s == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
		
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;			
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		} // for
		
		return sb.toString();
	}

	public static String addQueryString(String str, String data, String addStr) {
		String retstring = data;
		
		if (!str.trim().equals("")) {
			retstring = str + addStr + retstring;
		}
		
		return retstring;
	}

	/**
	 * 특수기호 제거 함수
	 * 
	 * @param str
	 * @return
	 */
	public static String removeTrimChar(String str) {
		if (str == null) {
			return "";
		}
		
		String ret = replace(str, " ", "");
		ret = ret.replaceAll("!\"#[$]%&\\(\\)\\{\\}@`[*]:[+];-.<>,\\^~|'\\[\\]", "");

		return ret;
	}

	/**
	 * 사용자 지정 우선 정렬
	 * 
	 * @param map dataset 결과
	 * @return
	 */
	public static LinkedHashMap<String, Object> setCustomSortAsMap(Map<String, Object> map, String[] fieldList) {
		LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
		List<Map.Entry<String, Object>> entries = new LinkedList<>(map.entrySet());

		// 최우선 정렬 필드 대소문자 구분없이 체크 하고 put
		for (String field : fieldList) {
			for (Map.Entry<String, Object> entry : entries) {
				if (field.toLowerCase().equals(entry.getKey()) || field.toUpperCase().equals(entry.getKey())) {
					resultMap.put(entry.getKey(), entry.getValue());
				}
			}
		}

		// 문자 길이로 sorting (오름차순)
		// Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
		// //일부 자바버전은 람다식이 허용되지않음
		Collections.sort(entries, new Comparator<Map.Entry<String, Object>>() {
			@Override
			public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});

		// 나머지는 순서정렬 우선정렬된 필드들은 제외하고 put
		for (Map.Entry<String, Object> entry : entries) {
			if (!Arrays.asList(fieldList).contains(entry.getKey().toLowerCase())
					&& !Arrays.asList(fieldList).contains(entry.getKey().toUpperCase())) {
				resultMap.put(entry.getKey(), entry.getValue());
			}
		}
		
		return resultMap;
	}

//	/**
//	 * request null체크
//	 **/
//	public static String getRequest(javax.servlet.http.HttpServletRequest req, String parameter, String default_value) {
//		String req_value = req.getParameter(parameter) != null && !"".equals(req.getParameter(parameter))
//				? req.getParameter(parameter)
//				: default_value;
//
//		return req_value;
//	}
//
//	/**
//	 * request parameter 전체 가져오기
//	 **/
//	public static String getRequestParamString(javax.servlet.http.HttpServletRequest req) {
//		String params = "";
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		Enumeration<String> enumber = req.getParameterNames();
//
//		while (enumber.hasMoreElements()) {
//			String key = enumber.nextElement().toString();
//			String value = "";
//
//			if (key != null && !"".equals(key)) {
//				value = req.getParameter(key);
//
//				if (!"".equals(params)) {
//					params += "&";
//				}
//
//				params += key + "=" + value;
//			}
//		}
//
//		return params;
//	}
//
//	/**
//	 * request parameter Map으로 가져오기
//	 **/
//	public static Map getRequestParameterMap(javax.servlet.http.HttpServletRequest req) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		Enumeration<String> enumber = req.getParameterNames();
//
//		while (enumber.hasMoreElements()) {
//			String key = enumber.nextElement().toString();
//			String value = "";
//
//			if (key != null && "".equals(key)) {
//				value = req.getParameter(key);
//				map.put(key, value);
//			}
//		}
//
//		return map;
//	}

	/**
	 * 첨부파일 확장자 가져오기
	 **/
	public static String getFileExt(String filename) {
		String ext = "";
		ext = filename.substring(filename.lastIndexOf(".") + 1);

		return ext;
	}

	/*
	 * 브라우저별 고유 ID 생성
	 */
	public static String getSessionID(jakarta.servlet.http.HttpServletRequest req) throws NoSuchAlgorithmException {
		String userAgent = req.getHeader("USER-AGENT");
		String userIP = (String) req.getHeader("X-Forwarded-For");

		if (userIP == null || userIP.length() == 0 || userIP.toLowerCase().equals("unknown")) {
			userIP = (String) req.getRemoteAddr();
		}
		
		return createMD5Hash(userIP + userAgent);
	}

	/*
	 * MD5 생성
	 */
	public static String createMD5Hash(final String input) throws NoSuchAlgorithmException {
		String hashtext = null;
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] messageDigest = md.digest(input.getBytes());

		hashtext = convertToHex(messageDigest);

		return hashtext;
	}

	/*
	 * Hash 코드 생성
	 */
	private static String convertToHex(final byte[] messageDigest) {
		BigInteger bigint = new BigInteger(1, messageDigest);
		String hexText = bigint.toString(16);

		while (hexText.length() < 32) {
			hexText = "0".concat(hexText);
		}

		return hexText;
	}

	/*
	 * Session 시간 체크
	 */
	public static boolean checkSessionTimeOut(String lastAccessedTime, int maxInactiveInterval) {
		Date lastAccessedDate = new Date(Long.parseLong(lastAccessedTime));
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastAccessedDate);
		cal.add(Calendar.SECOND, maxInactiveInterval);
		
		lastAccessedDate = cal.getTime();

		Date toDate = new Date();

		if (lastAccessedDate.getTime() < new Date().getTime()) {
			return true;
		} else {
			return false;
		}
	}

//	/*
//	 * userId기반으로 JWT토큰 생성
//	 * @param username userId
//	 * @return JWT
//	 */
//	public static String generateToken(String username) {
//		return Jwts.builder()
//				.setSubject(username)
//				.setIssuedAt(new Date(System.currentTimeMillis()))
//				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2)) // 2 hours
//				.signWith(SignatureAlgorithm.HS256, TOKEN_KEY)
//				.compact();
//	}

//	/*
//	 *  토큰 유효 체크
//	 *  @param token 생성되어 있는 token
//	 */
//	public static Claims validateToken(String token, HttpServletResponse response) {
//		try {
//			return Jwts.parser()
//					.setSigningKey(TOKEN_KEY)
//					.parseClaimsJws(token)
//					.getBody();
//		} catch (SignatureException | ExpiredJwtException e) {
//            try {
//                response.sendRedirect("http://demo.prosearch.co.kr:18006/demo/error/error401.html");
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//            return null;
//		}
//	}
}