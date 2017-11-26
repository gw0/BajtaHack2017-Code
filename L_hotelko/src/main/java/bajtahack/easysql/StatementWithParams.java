package bajtahack.easysql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;

/** Statement with params wrapper. Used for transactions, where we have to remember
 * several statements (sql + parameters) 
 * @author mitjagustin
 */
public final class StatementWithParams {
	 
     private int position = 1;
	 final String query;
	 private final Map<Integer, SqlQueryParam> params;
	 private volatile String toStringCached;
	 
	 public StatementWithParams(String query) {
        this.query = query;
        this.params = new HashMap<Integer, SqlQueryParam>();
	 }
	 
	 public void addParam(SqlQueryParam param) {
	     params.put(position, param);
	     position++;
	 }
	 
	 public StatementWithParams(String query, Map<Integer, SqlQueryParam> params) {
		this.query = query;
		this.params = params;
		this.toString(); // prepare cached log representation
	 }

	public String getQuery() {
		return query;
	}

	public Map<Integer, SqlQueryParam> getParams() {
		return params;
	}
		
	public static String toStringCreator(String sql, Map<Integer, SqlQueryParam> members) {
	    
        int placeholderCount = StringUtils.countMatches(sql, "?");
        if(placeholderCount == 0 && members == null) return sql; // stavek brez parametra
	    
        int numOfParams = members.entrySet().size();

	    if (placeholderCount != numOfParams) {
	        throw new IllegalStateException(String.format(
	                "Število parametrov %s je drugačno od števila parametrov v sql stavku: %s", numOfParams, sql));
	    }
	    
	       // create it if not present
        final StringBuilder builder = new StringBuilder("SQL Statement [query=");
        
        final String[] paramArr = new String[numOfParams];
        int idxParam = 0;
        final Iterator<Entry<Integer, SqlQueryParam>> it = members.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<Integer, SqlQueryParam> next = it.next();
            paramArr[idxParam] = next.getValue().getDbVal();
        }
        
        final String[] sqlParts = sql.split("\\?");
        try {
            for (int i = 0; i < sqlParts.length; i++) {
                String part = sqlParts[i];
                builder.append(part);
                if (i < paramArr.length)
                    builder.append(paramArr[i]);
            }    
        }catch(Throwable t) {} // we dont care, assuming sql is always ok
        
        builder.append("]");
        return builder.toString();
	    
	}
	
	
    @Override
    public String toString() {
        if (toStringCached!=null) return toStringCached;
        toStringCached = toStringCreator(query, params);
        return toStringCached;
    }
	 
}