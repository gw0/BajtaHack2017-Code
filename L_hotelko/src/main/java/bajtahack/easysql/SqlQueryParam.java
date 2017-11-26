package bajtahack.easysql;

/**
 * Holder for prepared statement parameter. You must specify type and value.
 * Later on, database code iterates through collection of this parameters and inserts them into
 * statement's sql.
 * @author mitjag
 *
 */
public class SqlQueryParam {
	
	public final int dbType;
	public final String dbVal;
	
	public SqlQueryParam(int dbType, String dbVal) {
		super();
		this.dbType = dbType;
		this.dbVal = dbVal;
	}
	public int getDbType() {
		return dbType;
	}
	public String getDbVal() {
		return dbVal;
	}
	
    @Override
    public String toString() {
        return this.dbVal;
    }
	
	
	
}