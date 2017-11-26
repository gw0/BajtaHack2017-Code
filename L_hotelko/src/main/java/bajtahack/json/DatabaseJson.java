package bajtahack.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Logger;
import bajtahack.common.Utils;
import bajtahack.database.LoggingFactory;
import bajtahack.easysql.SqlQueryParam;
import net.sf.json.JSONObject;

/**
 * @author mitjag
 *
 */
public class DatabaseJson {
	
	private static final int MAX_CELL_LENGTH = 150;
	
	/**
	 * Kako se preslika JDBC ResultSet v JSON 
	 * @author mitjag
	 *
	 */
	public static enum JsonRenderType {
	    /**
	     * Posebni format ki ga pričakuje datatable.
	     */
	    DATATABLE,
		/**
		 * Primeren za rezultate v tabelarni obliki. Rezultati vsebujejo layout (ime fielda iz baze, ime fielda v rezultatih) in podatke, ki so mapirani glede na layout.  
		 */
		ARRAY,
		/**
		 * Najbolj naraven tip.  Primeren za rezultate v tabelarni obliki. Vrne array objektov, kjer je IME_DB_STOLPCA : VREDNOST
		 */
		ARRAY_ASOCIATIVE,
		/**
		 * Enovrstična tabelarna oblika. Primeren za querje, kjer se vrne vrednost več stolpcev za ENO ! vrstico
		 */
		OBJECT, 
		/**
		 * Isto kot ARRAY, le da je brez layouta
		 */
		ARRAY_WITHOUT_LAYOUT
	}
	
	public static final Logger log = LoggingFactory.loggerForThisClass();
	
    /** Retrieves data from ResultSet. Correct type is coersed.
     * @param rsm
     * @param rs
     * @param colIdx
     * @param dateTimeOnly TODO
     * @return
     * @throws SQLException
     */
    public static Object getData(ResultSetMetaData rsm, ResultSet rs, int colIdx, boolean dateTimeOnly) throws SQLException{
		Object dbVal = null;
        switch (rsm.getColumnType(colIdx)) {
            case Types.BIGINT: {
                dbVal = rs.getLong(colIdx);
                break;
            }
            case Types.INTEGER: {
                dbVal = rs.getInt(colIdx);
                break;
            }
            case Types.DATE:
            case Types.TIMESTAMP:
            case Types.TIME:
            {
                if(dateTimeOnly) {
                    Date tmp = rs.getDate(colIdx);
                    if (tmp!=null) {
                        if(dateTimeOnly) dbVal = JsonUtils.formatJsonDateonly(tmp);
                        break;
                    } 
                    
                }else {
                    Timestamp timestamp = rs.getTimestamp(colIdx);
                    if (timestamp != null){
                        dbVal = JsonUtils.formatJsonTimestamp(timestamp);
                    }
                        
                }
              
                break;
            }
            case Types.FLOAT: {
                dbVal = rs.getFloat(colIdx);
                break;
            }
            case Types.DOUBLE: {
                dbVal = rs.getDouble(colIdx);
                break;
            }
            case Types.BOOLEAN: {
                dbVal = rs.getBoolean(colIdx);
                break;
            }
            default: {
            	dbVal = JsonUtils.jsonizeString2(rs.getString(colIdx));
            }
        }
        
        return dbVal;
		
	}
    
	/** Gets styped value from string
	 * @param type you want to output {@link java.sql.Types} 
	 * @param s plain string value
	 * @return object that you cast later
	 * @throws SQLException
	 */
	public static Object strToDbValue(int type, String s) throws SQLException {
        if (s == null || s.isEmpty()) {
            return null;
        } else if (type == Types.BIGINT) {
            return new BigInteger(s);
        } else if (type == Types.BIT) {
            return s.getBytes()[0];
        } else if (type == Types.BOOLEAN || type == Types.BINARY) {
            return Boolean.valueOf(s);
        } else if (type == Types.CHAR) {
            return s.charAt(0);
        } else if (type == Types.DATE) {
            try {
                return new java.sql.Date(new SimpleDateFormat("dd.MM.yyyy").parse(s).getTime());
            } catch (ParseException e) {
                throw new SQLException("Error parsing the date (dd.MM.yyyy): " + s);
            }
        } else if (type == Types.DECIMAL || type == Types.NUMERIC) {
            return new BigDecimal(s);
        } else if (type == Types.DOUBLE) {
            return Double.parseDouble(s);
        } else if (type == Types.FLOAT || type == Types.REAL) {
            return Float.parseFloat(s);
        } else if (type == Types.INTEGER) {
            return Integer.parseInt(s);
        } else if (type == Types.LONGVARBINARY || type == Types.VARBINARY) {
            return s.getBytes();
        } else if (type == Types.LONGVARCHAR || type == Types.VARCHAR) {
            return s;
        } else if (type == Types.SMALLINT) {
            return Short.parseShort(s);
        } else if (type == Types.TIME) {
            try {
                return new Time(new SimpleDateFormat("yyyy-mm-dd hh:MM:ss").parse(s).getTime());
            } catch (ParseException e) {
                throw new SQLException("Error parsing the date (yyyy-mm-dd hh:MM:ss): " + s);
            }
        } else if (type == Types.TIMESTAMP) {
            try {
                return new Timestamp(new SimpleDateFormat("yyyy-mm-dd hh:MM:ss").parse(s).getTime());
            } catch (ParseException e) {
                throw new SQLException("Error parsing the date (yyyy-mm-dd hh:MM:ss): " + s);
            }
        } else if (type == Types.TINYINT) {
            return Byte.parseByte(s);
        } else {
            return null;
        }
    }
	
	/** Displays result as plain flat JSON object with key value pairs
	 * @param pw
	 * @param rs
	 * @throws SQLException
	 */
	private static void displayObject(StringBuilder pw, ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();
        String[] colNames = new String[numColumns + 1];

        for (int i = 1; i < (numColumns + 1); i++)
            colNames[i] = rsmd.getColumnLabel(i);
        
        if (rs.next()) {
            pw.append("{");
            
            for (int colIdx=1; colIdx <= numColumns; colIdx++) {
                Object f = getData(rsmd, rs, colIdx, true);
                pw.append("\""+ Utils.nvl(colNames[colIdx]) + "\"");
                pw.append(":");
                pw.append("\""+ Utils.nvl(f) + "\"");
                if (colIdx < numColumns)
                    pw.append(",");
            }
            
            pw.append("}");
        }
        
    }
	
    public static JSONObject getPojo(ResultSet rs) throws SQLException {
        
        final JSONObject obj = new JSONObject();
        final ResultSetMetaData rsmd = rs.getMetaData();
        if(rsmd == null) throw new IllegalStateException("Empty recordSet MetaData");
        final int numColumns = rsmd.getColumnCount();
        final String[] colNames = new String[numColumns + 1];

        for (int i = 1; i < (numColumns + 1); i++)
            colNames[i] = rsmd.getColumnLabel(i);
        
        if (rs.next()) {
            for (int colIdx=1; colIdx <= numColumns; colIdx++) {
                final Object f = getData(rsmd, rs, colIdx, true);
                obj.put(Utils.nvl(colNames[colIdx]), Utils.nvl(f));
            }
        }
        
        return obj;
    }
	
	/** Displays data as a array with layout strcuture. In this way, the long property names does not fill the
	 * bandwidth.
	 * @param pw
	 * @param rs
	 * @param maxRows
	 * @param withoutLayout
	 * @throws SQLException
	 */
	private static void displayResultSet(StringBuilder pw, ResultSet rs, int maxRows, boolean withoutLayout, boolean limitCellLength) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        
        if(!withoutLayout){
            pw.append("\"layout\": [[");
            for (int i=1; i <= colCount; i++) {
                pw.append("{ \"name\": \"" + rsmd.getColumnLabel(i) + "\", \"field\": \"c" + i + "\" }");
                if (i<colCount) pw.append(",");
            }
            pw.append("]], "); // end layout
        }
        
        pw.append("\"data\": [");
        for (int j=0; j < maxRows && rs.next(); j++) {
            pw.append("{");
            for (int i=1; i <= colCount; i++) {
                Object obj = getData(rsmd, rs, i, true); // rs.getObject(i);
                if (obj == null || obj.toString().trim().equals("")) {
                  obj = "";
                } else {
                    String str = obj.toString();
                    if (limitCellLength && str.length() > MAX_CELL_LENGTH) {
                        str = str.substring(0, MAX_CELL_LENGTH) + "...";
                    }
                    obj = str;
                }
                pw.append("\"c" + i + "\" : \""+ Utils.nvl(obj)+"\"");
                if (i<colCount) pw.append(",");
            }
            pw.append("}"); // end row
            if (!rs.isLast())
                pw.append(",");
        }
        pw.append("]"); // end data
        
        rs.close();
    }
	
    public static String displayUpdateCount(int uc) {
        return String.format("{ \"ucnt\" : \"%s\" }", uc);
    }
	
	// public methods
    
	/** Sets the parameter in {@link java.sql.PreparedStatement}
	 * @param stmt
	 * @param params
	 * @throws SQLException
	 */
	public static void setParams(PreparedStatement stmt, Map<Integer, SqlQueryParam> params) throws SQLException {
        for (Map.Entry<Integer, SqlQueryParam> entry : params.entrySet()) {
            int ndx = entry.getKey();
            SqlQueryParam param = entry.getValue();
            int type = param.getDbType();
            //if (DEBUG_TRACE) log.fine(param.toString());
            Object value = strToDbValue(type, param.getDbVal());
            //if(value != null)
                stmt.setObject(ndx, value);
            //else
            //    stmt.setNull(ndx, type);
        }
    }
	
	
	/** General render method. Render types calles specific render function.
	 * @param pw
	 * @param stmt
	 * @param isRS
	 * @param params
	 * @param rType selectes specific render type
	 * @param echoNum Expected by the DataTables rendered
	 * @param countSql TODO
	 * @param dateTimeOnly TODO
	 * @throws SQLException
	 */
	public static void displayResults(StringBuilder pw, Statement stmt, boolean isRS, Map<Integer, SqlQueryParam> params, JsonRenderType rType, int echoNum, boolean limitCellLength, String countSql, boolean dateTimeOnly) throws SQLException {
		if (isRS) {
			ResultSet rs = stmt.getResultSet();
	
			switch (rType) {
    			case ARRAY:
    				displayResultSet(pw, rs, stmt.getMaxRows(), false, limitCellLength);
    				break;
    			case ARRAY_WITHOUT_LAYOUT:
                    displayResultSet(pw, rs, stmt.getMaxRows(), true, limitCellLength);
                    break;
    			case OBJECT:
    			    displayObject(pw, rs);
    			    break;
    			default:
    				break;
			}
			
			if (rs != null) {try {rs.close(); } catch (SQLException e) {} rs = null; }
			
		} 
	}
	
}
