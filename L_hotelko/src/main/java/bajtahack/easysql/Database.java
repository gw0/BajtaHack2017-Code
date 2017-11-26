package bajtahack.easysql;


import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import bajtahack.database.LoggingFactory;
import bajtahack.json.DatabaseJson;
import bajtahack.json.DatabaseJson.JsonRenderType;
import net.sf.json.JSONObject;
import static bajtahack.json.DatabaseJson.*;

/**
 * Database subsystem. Works with connection from connection pool running inside application server, or
 * establishes its own connections via JDBC DriverManager. 
 * 
 * Contains background worker thread for asynchronous background queries. You dump a query into queue list
 * and workers picks it later on and processes it. 
 * 
 * @author mitjag
 *
 */
public class Database {
    
    public static final boolean DEBUG_TRACE = true;
    public static final boolean VERBOSE_TRACE = false;
	
    public static final Logger log = LoggingFactory.loggerForThisClass();
    
    public static final Database instance = new Database();
	
	private ConnectionFactory impl;

	public void setConnectionFactory(final ConnectionFactory connProvider) {
		impl = connProvider;
	}
	
	public Connection getConnection() throws SQLException{
		return impl.getConnection();
	}
	
	private static void reportError(String query, Map<Integer, SqlQueryParam> params) {
	    
	    final StringBuilder sb = new StringBuilder();
        sb.append("================================ DATABASE ERROR "  + LoggingFactory.LOG_LINE_SEPARATOR);
        sb.append(LoggingFactory.LOG_LINE_SEPARATOR);
        sb.append(StatementWithParams.toStringCreator(query, params) + LoggingFactory.LOG_LINE_SEPARATOR);
        sb.append(LoggingFactory.LOG_LINE_SEPARATOR);
        sb.append("================================ ERROR END");
        log.log(Level.SEVERE, sb.toString());
	}
	
	// *****************************    Public methods
	
	/** The main method for retrieving json from datatabse. You specify query, json output type and pass in some other arguments.
	 * @param query SQL prepared statement query with positional parameters <code>SELECT * FROM1 WEHERE id = ?</code>
	 * @param params map with all named parameters 
	 * @param maxRows  how many row if multiple found. Defult is 100 usually
	 * @param rType what should method render [object | array | array with layout]
	 * @param echoNum property used by datatables to identify request / response if multiple requests are sent
	 * @param mainSql TODO not used
	 * @param limitCellLength used by datatables. Max cell length limit (cut if necessary)
	 * @param countSql datatables - used for calculating summary
	 * @param dateTimeOnly display only date part of datetime. If false, the timestamp will be displayed
	 * @return string builder with already valid json
	 */
	public StringBuilder getJson(String query, Map<Integer, SqlQueryParam> params, int maxRows, JsonRenderType rType, int echoNum, String mainSql, boolean limitCellLength, String countSql, boolean dateTimeOnly){
    	final long start = System.currentTimeMillis();

    	if(DEBUG_TRACE)log.fine("Executing query: " + query);
		StringBuilder jsonResp = new StringBuilder();
		
		boolean isError = false; // signal sql query error
		Throwable cause = null;
		
		try(final Connection connection = this.getConnection())  {
			try {
				
				try (final PreparedStatement statement  = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
					statement.setMaxRows(maxRows);
		            if (params != null) setParams(statement, params);
		            
		            if(DEBUG_TRACE)log.fine(StatementWithParams.toStringCreator(query, params));  // members logging
		            
		            boolean isRS = statement.execute();
		            
		            displayResults(jsonResp, statement, isRS, params, rType, echoNum, limitCellLength, countSql, dateTimeOnly);
		            
		            if(VERBOSE_TRACE)log.finest(jsonResp.toString()); // json response logging
				}
				
			} catch (SQLException e) {
			    
			    reportError(query, params);
				log.log(Level.SEVERE, e.toString(), e);
				
				isError = true;
				cause = e;
			} finally {
				if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
			}
		} catch (SQLException e1) {
			log.severe("MOPED - unable to get connection from pool.");
			log.log(Level.SEVERE, "Error db", e1);
		}
		
		if (isError) { // zawrapamo vse v runtime exception, da ne rabimo popravljat vseh klicočih metod. 
		    
		    throw new java.lang.IllegalStateException("Napaka pri delu z bazo. Ne morem nadaljevati!", cause);
		    
		} 
		
		return jsonResp;
	}
	
	public JSONObject getPojo(String query, Map<Integer, SqlQueryParam> params)  {
        final long start = System.currentTimeMillis();
        if (DEBUG_TRACE) log.fine("Executing query: " + query);
        
        boolean isError = false; // signal sql query error
        Throwable cause = null;
        
        try(final Connection connection = this.getConnection())  {
            try {
                try (final PreparedStatement statement  = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                    if (params != null) setParams(statement, params);
                    boolean isRS = statement.execute();
                    if (isRS){
                        ResultSet rs = statement.getResultSet();
                        return DatabaseJson.getPojo(rs);
                    }
                }
            } catch (SQLException e) {
                
                reportError(query, params);
                log.log(Level.SEVERE, e.toString(), e);
                
                isError = true;
                cause = e;
            } finally {
                if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch (SQLException e1) {
            log.severe("MOPED - unable to get connection from pool.");
            log.log(Level.SEVERE, "Error db", e1);
        }   
        if (isError) throw new java.lang.IllegalStateException("Napaka pri delu z bazo. Ne morem nadaljevati!", cause);
        return null;
    }
	
	public Object getScalar(String query, Map<Integer, SqlQueryParam> params)  {
    	final long start = System.currentTimeMillis();
    	if (DEBUG_TRACE) log.fine("Executing query: " + query);
		Object f = null;
		boolean isError = false; // signal sql query error
	    Throwable cause = null;
		try(final Connection connection = this.getConnection())  {
			try {
				try (final PreparedStatement statement  = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
					if (params != null) setParams(statement, params);
		            boolean isRS = statement.execute();
		            if (isRS){
		            	ResultSet rs = statement.getResultSet();
		            	ResultSetMetaData rsmd = rs.getMetaData();
		            	while (rs.next()) {
		        			f = getData(rsmd, rs, 1, true);
		        		}
		            }
		            if(DEBUG_TRACE)log.finest(StatementWithParams.toStringCreator(query, params));  // members logging
					return f;
				}
				
			} catch (SQLException e) {
			    reportError(query, params);
                log.log(Level.SEVERE, e.toString(), e);
				isError = true;
                cause = e;
			} finally {
				if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
			}
		} catch (SQLException e1) {
			log.severe("MOPED - unable to get connection from pool.");
			log.log(Level.SEVERE, "Error db", e1);
		}	
		if (isError) throw new java.lang.IllegalStateException("Napaka pri delu z bazo. Ne morem nadaljevati!", cause);
		return null;
        
    }
	
	public String getPureString(String scalarQuery, Map<Integer, SqlQueryParam> params)  {
        final long start = System.currentTimeMillis();
        if (DEBUG_TRACE) log.fine("Executing query: " + scalarQuery);
        String f = null;
        
        boolean isError = false; // signal sql query error
        Throwable cause = null;
        
        try(final Connection connection = this.getConnection())  {
            try {
                try (final PreparedStatement statement  = connection.prepareStatement(scalarQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                    if (params != null) setParams(statement, params);
                    boolean isRS = statement.execute();
                    if (isRS){
                        ResultSet rs = statement.getResultSet();
                        while (rs.next()) {
                            f = rs.getString(1);
                        }
                    }
                    if(DEBUG_TRACE)log.finest(StatementWithParams.toStringCreator(scalarQuery, params));  // members logging
                    return f;
                }
                
            } catch (SQLException e) {
                reportError(scalarQuery, params);
                log.log(Level.SEVERE, e.toString(), e);
                isError = true;
                cause = e;
            } finally {
                if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch (SQLException e1) {
            log.severe("MOPED - unable to get connection from pool.");
            log.log(Level.SEVERE, "Error db", e1);
        }   
        if (isError) throw new java.lang.IllegalStateException("Napaka pri delu z bazo. Ne morem nadaljevati!", cause);
        return null;
        
    }
	
	public List<Map<String, Object>> getArrayList(String query, Map<Integer, SqlQueryParam> params) {
	    final long start = System.currentTimeMillis();
        if (DEBUG_TRACE) log.finest("Executing query: " + query);
        final List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        
        boolean isError = false; // signal sql query error
        Throwable cause = null;
        
        try(final Connection connection = this.getConnection())  {
        	try{
        		try (final PreparedStatement statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
        			if (params != null) setParams(statement, params);
		            try (final ResultSet rs = statement.executeQuery()){
		                
		                if(DEBUG_TRACE)log.finest(StatementWithParams.toStringCreator(query, params)); // members logging
		                
		            	final ResultSetMetaData metaData = rs.getMetaData();
			            final int numColumns = metaData.getColumnCount();
			            
			            if (!rs.isBeforeFirst()) { // check if there is any data in result set
			                return null;
			            }
			            
			            while (rs.next()) {
			                final Map<String, Object> row = new HashMap<String, Object>();
			                for(int i=0; i<numColumns; i++){
			                    final String column = metaData.getColumnName(i+1);
			                    Object value = rs.getObject(i+1);
			                    
			                    row.put(column, value);
			                }
			                rows.add(row);
			            }
			            return rows;
		            }
        		}
        	} catch (SQLException e) {
        	    reportError(query, params);
                log.log(Level.SEVERE, e.toString(), e);
	            isError = true;
                cause = e;
	            
	        } finally {
	        	if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
	        }
        	
        } catch (SQLException e) {
            log.log(Level.SEVERE, " -- POZOR -- Napaka v komunikaciji z bazo", e);
            isError = true;
            cause = e;
        } 
        
        if (isError) throw new java.lang.IllegalStateException("Napaka pri delu z bazo. Ne morem nadaljevati!", cause);
        return null;
        
	}
	
	public String execUpdateQuery(StatementWithParams st) throws SQLException {
	    return execUpdateQuery(st.getQuery(), st.getParams());
    }
	
	public String execUpdateQuery(String query, Map<Integer, SqlQueryParam> params) throws SQLException {
	    final Integer executeUpdate = execUpdateQueryInternal(query, params);
	    if (executeUpdate != null && executeUpdate > 0){
	        String resp = displayUpdateCount(executeUpdate);
            return resp;
	    }
	    return null;
	}
	
	public Integer execUpdateQueryInternal(String query, Map<Integer, SqlQueryParam> params) throws SQLException {
        final long start = System.currentTimeMillis();
        if (DEBUG_TRACE) log.finest("Executing query: " + query);
        try(final Connection connection = this.getConnection())  {
            try{
                try (final PreparedStatement statement = connection.prepareStatement(query)) {
                    if (params != null) setParams(statement, params);
                    int executeUpdate = statement.executeUpdate();
                    connection.commit();
                    if (executeUpdate > 0) {
                        
                        if(DEBUG_TRACE) log.finest(StatementWithParams.toStringCreator(query, params));  // members logging
                        
                        return  executeUpdate;
                        
                    }
                    
                }
            }catch (SQLException e) {
                reportError(query, params);
                log.log(Level.SEVERE, e.toString(), e);
                throw e;
            }finally {
                if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch(SQLException ex){
            log.log(Level.SEVERE, "SQL napaka", ex);
            throw ex;
        } 
        
        return null;
        
    }
	
	/** Izvede insert in vrne kolumne novo vstavljene vrstice. <br>
	 * Ime vrstice  podaš s parametrom v sledeči obliki
	 * <code>
	 * new String[]{"ID"}
	 * </code>
	 * @param query
	 * @param params
	 * @param returnColumns
	 * @param jsonizedObjectTmpl npr: <code>"%s"</code>
	 * @return
	 * @throws SQLException
	 */
	public String execUpdateQueryReturnInsertId(String query, Map<Integer, SqlQueryParam> params, String returnColumns[], String jsonizedObjectTmpl) throws SQLException {
        final long start = System.currentTimeMillis();
        if (DEBUG_TRACE) log.finest("Executing query: " + query);
        try(final Connection connection = this.getConnection())  {
            try{
                // WARN: oracle ne podpira opcije /*Statement.RETURN_GENERATED_KEYS*/, oz. le ta vrne rowid, in ga moraš naprej queryat.
                // Če pa mu podaš imena stolpca ki ga vrne insert stavek (ID), potem je vse v redu
                //String generatedColumns[] = {"ID"};
                try (final PreparedStatement statement = connection.prepareStatement(query, returnColumns/*Statement.RETURN_GENERATED_KEYS*/)) {
                    if (params != null) setParams(statement, params);
                    if(statement.executeUpdate() > 0){
                        
                        if(DEBUG_TRACE)log.finest(StatementWithParams.toStringCreator(query, params));  // members logging
                        final ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys!=null && generatedKeys.next()) {
                            final Long id = generatedKeys.getLong(1);
                            if (DEBUG_TRACE) log.fine("Insertid " + id);
                            if (jsonizedObjectTmpl == null)
                                return  "{\"insertId\" : " + id.toString() + "}";
                            
                            return String.format(jsonizedObjectTmpl, id.toString());
                        }
                            
                        log.severe("Napaka pri pridobivanju generiranega IDja");
                    }
                }finally{
                    connection.commit();
                }
            }catch (SQLException e) {
                reportError(query, params);
                log.log(Level.SEVERE, e.toString(), e);
                throw e;
            }finally {
                if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch(SQLException ex){
            log.log(Level.SEVERE, "SQL napaka", ex);
            throw ex;
        } 
        
        // če smo tu, je napaka
        log.severe("Napaka pri dodajanju recorda v bazo. Nobena vrstica ni bila dodana");
        log.severe(StatementWithParams.toStringCreator(query, params));  // members logging
        return null;
        
    }
	
	public String execUpdateQueryReturnInsertId(StatementWithParams st, String returnColumns[], String jsonizedObjectTmpl) throws SQLException {
	    return execUpdateQueryReturnInsertId(st.getQuery(), st.getParams(), returnColumns, jsonizedObjectTmpl);
	}
	
	public String saveBlob(String query, String idObjekt, String idLokacija, String fileName, InputStream fileContent, String mimeType, String lokacija, String spremenil, int fileSize) throws SQLException {
        final long start = System.currentTimeMillis();
        if (DEBUG_TRACE) log.finest("Executing query: " + query);
    
        try(final Connection connection = this.getConnection())  {
            try{
                try (final PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, idObjekt );
                    statement.setString(2, idLokacija);
                    statement.setString(3, fileName);
                    statement.setBlob(4, fileContent);
                    statement.setString(5, mimeType);
                    statement.setString(6, lokacija);
                    statement.setString(7, spremenil);
                    statement.setInt(8, fileSize);
                    
                    int executeUpdate = statement.executeUpdate();
                    connection.commit();
                    
                    if (executeUpdate > 0) {
                        String resp = "Number of inserted files: "+Integer.toString(executeUpdate);
                        if (DEBUG_TRACE) log.fine(resp);
                        return resp;
                    }
                    log.severe("Executing : "+query);  // members logging
                }
            }catch (SQLException e) {
                
                log.log(Level.SEVERE, e.toString(), e);
                throw e;
            }finally {
                if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch(SQLException ex){
            log.log(Level.SEVERE, "Napaka pri shranjevanju blob datoteke", ex);
            throw ex;
        }         
        return null;
    }
	
	public void readBlob(String lokacija, String imeDatoteka, String idObjekt, HttpServletResponse response, String mimeType) throws SQLException, IOException{	    
	    final long start = System.currentTimeMillis();
        if (DEBUG_TRACE) log.finest("Executing read data: " + imeDatoteka + " lokacija: "+ lokacija+ " idObjekt: " +idObjekt);
        Blob blob = null;
        ResultSet rs = null;
        String fileName="";
        
        String query = "SELECT DATOTEKA, IME_DATOTEKA, MIME_TYPE FROM FILES WHERE IME_DATOTEKA = ? AND LOKACIJA = ? AND ID_OBJEKT = ?";
        final Map<Integer, SqlQueryParam> params = new TreeMap<Integer, SqlQueryParam>();
        params.put(1, new SqlQueryParam(Types.VARCHAR, imeDatoteka));
        params.put(2, new SqlQueryParam(Types.VARCHAR, lokacija));
        params.put(3, new SqlQueryParam(Types.VARCHAR, idObjekt));

        ServletOutputStream out = response.getOutputStream();
        
        try(final Connection connection = this.getConnection())  {
            try{
                try (final PreparedStatement statement = connection.prepareStatement(query)) {
                      statement.setString(1, imeDatoteka);
                      statement.setString(2, lokacija);
                      statement.setString(3, idObjekt);
                      rs = statement.executeQuery();
                      
                      if(rs.next()) {
                          blob = rs.getBlob(1);
                      }
                      else {
                          response.setContentType("text/html");
                          out.println("<html><head><title>Datoteka ne obstaja</title></head>");
                          out.println("<body><h1>Datoteka: " + imeDatoteka + " ne obstaja.");
                          out.println("<br>");
                          out.println("Lokacija: "+ lokacija+ " idObjekt: " +idObjekt+" </h1></body></html>");
                          return;
                      }
                      //get data from database
                      fileName=rs.getString(2);
                                           
                      response.setContentType(mimeType);
                      
                      String headerKey = "Content-Disposition";
                      String headerValue = String.format("attachment; filename=\"%s\"", fileName);
                      response.setHeader(headerKey, headerValue);
                      
                      InputStream in = blob.getBinaryStream();
                      int length;
                      Integer fileSize = (int) blob.length();
                      response.setHeader("Content-Length", fileSize.toString());
                      
                      int bufferSize = 1024;
                      byte[] buffer = new byte[bufferSize];
                      
                      while ((length = in.read(buffer)) != -1) {
                          out.write(buffer, 0, length);
                      }
                      
                      in.close();
                      out.flush();
                }
            } catch(SQLException e){
                reportError(query, params);
                log.log(Level.SEVERE, e.getMessage(), e);
                return;
            } finally {
                try {
                    if(rs!=null)rs.close();
                } catch (Exception e) {}
                if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
        }
	}

    public void executeTransaction(List<StatementWithParams> statements) throws SQLException {
        StatementWithParams currStatement = null;
        final StringBuilder logBuff = new StringBuilder();
    	try(final Connection con = getConnection())  {
    	    try {
    	        log.info("==== TransactionStart");
    	    	for (final Iterator<StatementWithParams> iterator = statements.iterator(); iterator.hasNext();) {
    				currStatement = iterator.next();
    				logBuff.append("Transaction member " + currStatement.getQuery());
    				logBuff.append(currStatement.toString());
    				final Map<Integer, SqlQueryParam> params = currStatement.getParams();
    				final String query = currStatement.getQuery();
    				final PreparedStatement pst = con.prepareStatement(query);
    				//pst.setQueryTimeout(10);
    				if (params != null) setParams(pst, params);
    				log.info("==== member se komita v bazo " + currStatement.toString());
    				pst.executeUpdate();
    				//con.commit();
                    log.info("==== member je komitan!" + currStatement.toString());
    			}
    	        con.commit();
    	        log.info("=== TransactionEnd ");
    	    }
    	    catch (SQLException e) {
    	        log.log(Level.SEVERE, " ==== Transaction Rollback !!! ==============)", e);
    	        if (currStatement!=null){
                    log.log(Level.SEVERE, " ===== Transation member : " + currStatement.toString() , e);
                    log.log(Level.SEVERE, logBuff.toString());
    	        }
				con.rollback();
				throw e;
    		}
    	}
	}
    
    public int executeBatch(List<StatementWithParams> statements) throws SQLException {
        
        try(final Connection con = getConnection())  {
            try {
                con.setAutoCommit(false); // start of transaction
                PreparedStatement pst = null;
                
                for (final Iterator<StatementWithParams> iterator = statements.iterator(); iterator.hasNext();) {
                    final StatementWithParams statementWithParams = iterator.next();
                    final Map<Integer, SqlQueryParam> params = statementWithParams.getParams();

                    // PAZI: query na batch statemetu nastaviš samo enkrat
                    final String query = statementWithParams.getQuery();
                    if(pst == null)
                        pst = con.prepareStatement(query);
                    
                    if (params != null) setParams(pst, params);
                    if(pst != null) 
                        pst.addBatch();
                }                
                if (pst==null) throw new RuntimeException("==== NAPAKA");
                
                int[] count = pst.executeBatch();
                log.info("Nad bazo se je izvršilo : "+ count.length + " SQL ukazov");
                con.commit();
                return count.length;
            }
            catch (SQLException e) {  
                log.log(Level.SEVERE, e.toString(), e);
                con.rollback();
                return 0;
            }
        }
        
    }
    
    
    /* STATIC METHODS - za upravljanje s transakcijo je odgovoren klicatelj */
    
    public static String execUpdateQueryReturnInsertId(final Connection connection, String query, Map<Integer, SqlQueryParam> params, String returnColumns[], String jsonizedObjectTmpl) throws SQLException {
        final long start = System.currentTimeMillis();//TODO dodaj timeaware log stavek
        if (DEBUG_TRACE) log.finest("Executing query: " + query);
        
        // WARN: oracle ne podpira opcije /*Statement.RETURN_GENERATED_KEYS*/, oz. le ta vrne rowid, in ga moraš naprej queryat.
        // Če pa mu podaš imena stolpca ki ga vrne insert stavek (ID), potem je vse v redu
        //String generatedColumns[] = {"ID"};
        try (final PreparedStatement statement = connection.prepareStatement(query, returnColumns/*Statement.RETURN_GENERATED_KEYS*/)) {
            if (params != null) setParams(statement, params);
            if(statement.executeUpdate() > 0){
                
                if(DEBUG_TRACE)log.finest(StatementWithParams.toStringCreator(query, params));  // members logging
                final ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys!=null && generatedKeys.next()) {
                    final Long id = generatedKeys.getLong(1);
                    if (DEBUG_TRACE) log.fine("Insertid " + id);
                    if (jsonizedObjectTmpl == null) {
                        if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
                        return  "{\"insertId\" : " + id.toString() + "}";
                    }
                    
                    return String.format(jsonizedObjectTmpl, id.toString());
                    
                }
                    
                log.severe("Napaka pri pridobivanju generiranega IDja");
            }
        }catch(SQLException e) {
            reportError(query, params);
            log.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
        
        return null;
    }
    
    public static Integer execUpdateQueryWithConn(final Connection connection, String query, Map<Integer, SqlQueryParam> params) throws SQLException {
        if (DEBUG_TRACE) log.finest("Executing query: " + query);
        
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            if (params != null) setParams(statement, params);
            
            return statement.executeUpdate();
        }
       
    }
    
    public static void executeTransaction(Connection con, List<StatementWithParams> statements, boolean issueCommit) throws SQLException {
        StatementWithParams currStatement = null;
        final StringBuilder logBuff = new StringBuilder();
        
        try {
            log.info("==== Start executing statements in transaction");
            for (final Iterator<StatementWithParams> iterator = statements.iterator(); iterator.hasNext();) {
                
                currStatement = iterator.next();
                logBuff.append("Transaction member " + currStatement.getQuery());
                logBuff.append(currStatement.toString());
                final Map<Integer, SqlQueryParam> params = currStatement.getParams();
                final String query = currStatement.getQuery();
                final PreparedStatement pst = con.prepareStatement(query);
                pst.setQueryTimeout(60);
                if (params != null) setParams(pst, params);
                log.info("= stavek transakcije se izvaja v bazi " + currStatement.toString());
                pst.executeUpdate();
                log.info("= je izveden!" + currStatement.toString());
            }
            
            if (issueCommit) {
                con.commit();
                log.info("=== TransactionEnd ");    
            }
            
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, " ==== Transaction Rollback !!! ==============)", e);
            if (currStatement!=null){
                log.log(Level.SEVERE, " ===== Transation member : " + currStatement.toString() , e);
                log.log(Level.SEVERE, logBuff.toString());
            }
            con.rollback();
            throw e;
        }
    
    }
    public static void executeTransactionWithouthCommit(Connection con, List<StatementWithParams> statements, boolean issueCommit) throws SQLException {
        StatementWithParams currStatement = null;
        final StringBuilder logBuff = new StringBuilder();
        
        try {
            log.info("==== Start executing statements in transaction");
            for (final Iterator<StatementWithParams> iterator = statements.iterator(); iterator.hasNext();) {
                
                currStatement = iterator.next();
                logBuff.append("Transaction member " + currStatement.getQuery());
                logBuff.append(currStatement.toString());
                final Map<Integer, SqlQueryParam> params = currStatement.getParams();
                final String query = currStatement.getQuery();
                final PreparedStatement pst = con.prepareStatement(query);
                pst.setQueryTimeout(60);
                if (params != null) setParams(pst, params);
                log.info("= stavek transakcije se izvaja v bazi " + currStatement.toString());
                pst.executeUpdate();
                log.info("= je izveden!" + currStatement.toString());
            }
            
            if (issueCommit) {
                log.info("=== TransactionEnd ");    
            }
            
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, " ==== Transaction Rollback !!! ==============)", e);
            if (currStatement!=null){
                log.log(Level.SEVERE, " ===== Transation member : " + currStatement.toString() , e);
                log.log(Level.SEVERE, logBuff.toString());
            }
            con.rollback();
            throw e;
        }
    
    }
    
    public static Object getScalar(Connection connection, String query, Map<Integer, SqlQueryParam> params)  {
        final long start = System.currentTimeMillis();
        if (DEBUG_TRACE) log.fine("Executing query: " + query);
        Object f = null;
        boolean isError = false; // signal sql query error
        Throwable cause = null;
        try {
            try (final PreparedStatement statement  = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                if (params != null) setParams(statement, params);
                boolean isRS = statement.execute();
                if (isRS){
                    ResultSet rs = statement.getResultSet();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while (rs.next()) {
                        f = getData(rsmd, rs, 1, true);
                    }
                }
                if(DEBUG_TRACE)log.finest(StatementWithParams.toStringCreator(query, params));  // members logging
                return f;
            }
            
        } catch (SQLException e) {
            reportError(query, params);
            log.log(Level.SEVERE, e.toString(), e);
            isError = true;
            cause = e;
        } finally {
            if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
        }
           
        if (isError) throw new java.lang.IllegalStateException("Napaka pri delu z bazo. Ne morem nadaljevati!", cause);
        return null;
        
    }
	
	public String deleteFile(String lokacija, String imeDatoteka, String idObjekt,  String changedBy) throws SQLException {
        if (DEBUG_TRACE) log.finest("Executing delete file: " + imeDatoteka + " lokacija: "+lokacija+" id objekta: "+idObjekt);
        
        String tableName="FILES";
        String query = "IME_DATOTEKA = '"+imeDatoteka+"' AND LOKACIJA= '"+lokacija+"' AND ID_OBJEKT = '"+idObjekt+"'";
        
        int countDeleteRevision =  deleteAndRevision(tableName, query, changedBy);
        
        if (countDeleteRevision == 0) {
            return null;
        }
         
        return displayUpdateCount(countDeleteRevision);
    }

    public String getFiles(String lokacija, String idObjekt, String idLokacija) throws SQLException {
        //TODO
        if (DEBUG_TRACE) log.finest("Pridobi podatke o : ");
        
        Map<Integer, SqlQueryParam> params = new TreeMap<Integer, SqlQueryParam>();
        params.put(1, new SqlQueryParam(Types.VARCHAR, lokacija));
        params.put(2, new SqlQueryParam(Types.VARCHAR, idObjekt));

        String query;
        //V PRIMERU, DA NI PODAN ID_LOKACIJE SE IZ BAZE POBERE VREDNOSTI, KI IMAJO ID_LOKACIJA IS NULL ?
       if(idLokacija=="0")
            query = "SELECT IME_DATOTEKA, FILE_SIZE FROM FILES WHERE LOKACIJA = ? AND ID_OBJEKT = ? AND ID_LOKACIJA = 0";
        else {    
            query = "SELECT IME_DATOTEKA, FILE_SIZE FROM FILES WHERE LOKACIJA = ? AND ID_OBJEKT = ? AND ID_LOKACIJA=?";    
            params.put(3, new SqlQueryParam(Types.VARCHAR, idLokacija));
        }

        final StringBuilder jsonResponse =getJson(query, params, 100, JsonRenderType.ARRAY_WITHOUT_LAYOUT, 0, null, true, null, true);
        return jsonResponse.toString();
    }
    
    /**
     * metoda, ki izbriše podatke in le te zapiše v revizijsko tabelo.
     * za vse skupaj poskrbi bazna procedura DELETE_ROWS_P
     * @param connection
     * @param tableName (procedura na podlagi tega parametra pridobi vse podatke o vseh imenih vrstci, ki jih je potrebno zapisati v revizijsko tabelo
     * @param query (na podlagi query procedura zgradi delete stavke. Vso kodo za izbris pred WHERE stavkom zgradi procedura)
     * @param changedBy
     * @return število izbrisanih zapisov
     * @throws SQLException
     */
    public static int deleteAndRevisionConnection(final Connection connection, String tableName, String query, String changedBy) throws SQLException {
        CallableStatement callableStatement;
        String deleteRows = "{call DELETE_ROWS_P(?,?,?,?)}";
        callableStatement = connection.prepareCall(deleteRows);
        callableStatement.setString(1, tableName);
        callableStatement.setString(2, " "+query+" ");
        callableStatement.setString(3, changedBy);
        callableStatement.registerOutParameter(4, java.sql.Types.INTEGER);
        
        callableStatement.executeUpdate();
        
        int countDeleteRevision = callableStatement.getInt(4);
        return countDeleteRevision; 
    }
    
    public int deleteAndRevision(String tableName, String query, String changedBy) throws SQLException{
        final long start = System.currentTimeMillis();
        if(DEBUG_TRACE) log.fine("Executing query: " + query);
        int countDeleteRevision=0;
        
        try(final Connection connection = this.getConnection())  {
            try {
                  CallableStatement callableStatement;
                  String deleteRows = "{call DELETE_ROWS_P(?,?,?,?)}";
                  callableStatement = connection.prepareCall(deleteRows);
                  callableStatement.setString(1, tableName);
                  callableStatement.setString(2, " "+query+" ");
                  callableStatement.setString(3, changedBy);
                  callableStatement.registerOutParameter(4, java.sql.Types.INTEGER);
                  callableStatement.executeUpdate();
                  countDeleteRevision = callableStatement.getInt(4);
                  connection.commit();
             }
             catch(SQLException ex){
                log.log(Level.SEVERE, "SQL napaka", ex);
                connection.rollback();
                return 0;
            } finally {
                if(DEBUG_TRACE)log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
       }
      
       return countDeleteRevision;
   }
}
