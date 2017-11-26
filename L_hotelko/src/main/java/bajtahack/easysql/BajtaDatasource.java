package bajtahack.easysql;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import bajtahack.database.LoggingFactory;


public class BajtaDatasource implements ConnectionFactory {
	
    public static final Logger log = LoggingFactory.loggerForThisClass();
    
	private DataSource dataSource;
	private final String dsName;

	public BajtaDatasource(final String dsName) {
	    
		this.dsName = dsName;
		if (this.dsName == null || this.dsName.isEmpty())
		    throw new ExceptionInInitializerError("Parameter datasource ni nastavljen");
		
		InitialContext ctx = null;
		try {
			ctx = new InitialContext();
			dataSource = (DataSource) ctx.lookup(this.dsName);
			log.info("Inicializiran nov resource pool :" + this.dsName);
			
			if (dataSource == null) {
			    log.severe("UnitTestDatasource z imenom ne obstaja :" + this.dsName);
			    throw new ExceptionInInitializerError("Datasource ne obstaja");
			}
			
		} catch (NamingException e) {
			Database.log.log(Level.SEVERE, "Cannot find datasource with JNDI: " + dsName, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			// we disable autocommit by default
			final Connection c = dataSource.getConnection();
			c.setAutoCommit(false);
			return c;
		} catch (SQLException e) {
		    Database.log.log(Level.SEVERE, "Could not get connection from datasource: " + dsName, e);
			throw e;
		}
	}
	
}