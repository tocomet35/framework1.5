/* 
 * @(#)RDUtil.java
 */
package framework.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import framework.db.ColumnNotFoundException;
import framework.db.RecordSet;

/**
 * RD(Report Designer)를 이용하여 개발할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class RDUtil {

	/**
	 * 디폴트 열 구분자
	 */
	public static final String DEFAULT_COLSEP = "##";

	/**
	 * 디폴트 행 구분자
	 */
	public static final String DEFAULT_LINESEP = "\n";

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet 이용

	/**
	 * RecordSet을 RD 파일 형식으로 출력한다.
	 * 행, 열 구분자로 디폴트 구분자를 사용한다.
	 * <br>
	 * ex) response로 rs를 RD 파일 형식으로 출력하는 경우 : RDUtil.setRecordSet(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RD 파일 형식으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * RecordSet을 RD 파일 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 RD 파일 형식으로 출력하는 경우 : RDUtil.setRecordSet(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RD 파일 형식으로 변환할 RecordSet 객체
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @param lineSep 행 구분자로 쓰일 문자열
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs, String colSep, String lineSep) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(lineSep);
			}
			pw.print(rdRowStr(rs, colNms, colSep));
		}
		return rowCount;
	}

	/**
	 * RecordSet을 RD 파일 형식으로 변환한다.
	 * 행, 열 구분자로 디폴트 구분자를 사용한다.
	 * <br>
	 * ex) rs를 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.format(rs)
	 * 
	 * @param rs 변환할 RecordSet 객체
	 * 
	 * @return RD 파일 형식으로 변환된 문자열
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs) throws ColumnNotFoundException {
		return format(rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * RecordSet을 RD 파일 형식으로 변환한다.
	 * <br>
	 * ex) rs를 열구분자 ##, 행구분자 !! 인 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.format(rs, "##", "!!")
	 * 
	 * @param rs 변환할 RecordSet 객체
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @param lineSep 행 구분자로 쓰일 문자열
	 * 
	 * @return RD 파일 형식으로 변환된 문자열
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs, String colSep, String lineSep) throws ColumnNotFoundException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(lineSep);
			}
			buffer.append(rdRowStr(rs, colNms, colSep));
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet 이용

	/**
	 * ResultSet을 RD 파일 형식으로 출력한다.
	 * 행, 열 구분자로 디폴트 구분자를 사용한다.
	 * <br>
	 * ex) response로 rs를 RD 파일 형식으로 출력하는 경우 : RDUtil.setResultSet(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RD 파일 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @return 처리건수 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs) throws SQLException, IOException {
		return setResultSet(response, rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * ResultSet을 RD 파일 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 RD 파일 형식으로 출력하는 경우 : RDUtil.setResultSet(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RD 파일 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @param lineSep 행 구분자로 쓰일 문자열
	 * @return 처리건수
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs, String colSep, String lineSep) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			String[] colInfo = new String[count];
			int[] colSize = new int[count];
			int[] colSizeReal = new int[count];
			int[] colScale = new int[count];
			// byte[] 데이터 처리를 위해서 추가
			int[] columnsType = new int[count];
			for (int i = 1; i <= count; i++) {
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				columnsType[i - 1] = rsmd.getColumnType(i);
				//Fiels 의 정보 및 Size 추가
				colSize[i - 1] = rsmd.getColumnDisplaySize(i);
				colSizeReal[i - 1] = rsmd.getPrecision(i);
				colScale[i - 1] = rsmd.getScale(i);
				colInfo[i - 1] = rsmd.getColumnTypeName(i);
			}
			int rowCount = 0;
			while (rs.next()) {
				if (rowCount++ > 0) {
					pw.print(lineSep);
				}
				// 현재 Row 저장 객체
				Map<String, Object> columns = new LinkedHashMap<String, Object>(count);
				for (int i = 1; i <= count; i++) {
					if (colInfo[i - 1].equals("LONG") || colInfo[i - 1].equals("LONG RAW") || colInfo[i - 1].equals("INTEGER") || colInfo[i - 1].equals("FLOAT") || colInfo[i - 1].equals("DOUBLE") || colInfo[i - 1].equals("NUMBER")) {
						columns.put(colNms[i - 1], rs.getObject(colNms[i - 1]));
					} else {
						columns.put(colNms[i - 1], rs.getString(colNms[i - 1]));
					}
				}
				pw.print(rdRowStr(columns, colSep));
			}
			return rowCount;
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * ResultSet을 RD 파일 형식으로 변환한다.
	 * 행, 열 구분자로 디폴트 구분자를 사용한다.
	 * <br>
	 * ex) rs를 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.format(rs)
	 * 
	 * @param rs 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * 
	 * @return RD 파일 형식으로 변환된 문자열
	 * @throws SQLException 
	 */
	public static String format(ResultSet rs) throws SQLException {
		return format(rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * ResultSet을 RD 파일 형식으로 변환한다.
	 * <br>
	 * ex) rs를 열구분자 ##, 행구분자 !! 인 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.format(rs, "##", "!!")
	 * 
	 * @param rs 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @param lineSep 행 구분자로 쓰일 문자열
	 * 
	 * @return RD 파일 형식으로 변환된 문자열
	 * @throws SQLException 
	 */
	public static String format(ResultSet rs, String colSep, String lineSep) throws SQLException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			String[] colInfo = new String[count];
			int[] colSize = new int[count];
			int[] colSizeReal = new int[count];
			int[] colScale = new int[count];
			// byte[] 데이터 처리를 위해서 추가
			int[] columnsType = new int[count];
			for (int i = 1; i <= count; i++) {
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				columnsType[i - 1] = rsmd.getColumnType(i);
				//Fiels 의 정보 및 Size 추가
				colSize[i - 1] = rsmd.getColumnDisplaySize(i);
				colSizeReal[i - 1] = rsmd.getPrecision(i);
				colScale[i - 1] = rsmd.getScale(i);
				colInfo[i - 1] = rsmd.getColumnTypeName(i);
			}
			int rowCount = 0;
			while (rs.next()) {
				if (rowCount++ > 0) {
					buffer.append(lineSep);
				}
				// 현재 Row 저장 객체
				Map<String, Object> columns = new LinkedHashMap<String, Object>(count);
				for (int i = 1; i <= count; i++) {
					if (colInfo[i - 1].equals("LONG") || colInfo[i - 1].equals("LONG RAW") || colInfo[i - 1].equals("INTEGER") || colInfo[i - 1].equals("FLOAT") || colInfo[i - 1].equals("DOUBLE") || colInfo[i - 1].equals("NUMBER")) {
						columns.put(colNms[i - 1], rs.getObject(colNms[i - 1]));
					} else {
						columns.put(colNms[i - 1], rs.getString(colNms[i - 1]));
					}
				}
				buffer.append(rdRowStr(columns, colSep));
			}
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// 기타 Collection 이용

	/**
	 * Map객체를 RD 파일 형식으로 변환한다.
	 * 열 구분자로 디폴트 구분자를 사용한다.
	 * <br>
	 * ex) map을 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.format(map)
	 *
	 * @param map 변환할 Map객체
	 *
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String format(Map<String, Object> map) {
		return format(map, DEFAULT_COLSEP);
	}

	/**
	 * Map객체를 RD 파일 형식으로 변환한다.
	 * <br>
	 * ex) map을 열구분자 ## 인 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.format(map, "##")
	 *
	 * @param map 변환할 Map객체
	 * @param colSep 열 구분자로 쓰일 문자열
	 *
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String format(Map<String, Object> map, String colSep) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(rdRowStr(map, colSep));
		return buffer.toString();
	}

	/**
	 * List객체를 RD 파일 형식으로 변환한다.
	 * 행, 열 구분자로 디폴트 구분자를 사용한다.
	 * <br>
	 * ex1) mapList를 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.format(mapList)
	 *
	 * @param mapList 변환할 List객체
	 *
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String format(List<Map<String, Object>> mapList) {
		return format(mapList, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * List객체를 RD 파일 형식으로 변환한다.
	 * <br>
	 * ex1) mapList를 열구분자 ##, 행구분자 !! 인 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.format(mapList, "##", "!!")
	 *
	 * @param mapList 변환할 List객체
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @param lineSep 행 구분자로 쓰일 문자열
	 *
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String format(List<Map<String, Object>> mapList, String colSep, String lineSep) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		if (mapList.size() > 0) {
			for (Map<String, Object> map : mapList) {
				buffer.append(rdRowStr(map, colSep));
				buffer.append(lineSep);
			}
			buffer.delete(buffer.length() - lineSep.length(), buffer.length());
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private 메소드
	/**
	 * RD(리포트디자이너) 용 Row 문자열 생성
	 */
	private static String rdRowStr(Map<String, Object> map, String colSep) {
		StringBuilder buffer = new StringBuilder();
		for (Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value != null) {
				buffer.append(value.toString());
			}
			buffer.append(colSep);
		}
		return buffer.toString();
	}

	/**
	 * RD(리포트디자이너) 용 Row 문자열 생성
	 * @throws ColumnNotFoundException 
	 */
	private static String rdRowStr(RecordSet rs, String[] colNms, String colSep) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		for (int c = 0; c < colNms.length; c++) {
			if (rs.get(colNms[c]) != null) {
				buffer.append(rs.get(colNms[c]));
			}
			buffer.append(colSep);
		}
		return buffer.toString();
	}
}
