package bajtahack.easysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class PoolDatabase {
    
    public static final PoolDatabase instance = new PoolDatabase();
    private final DataSource dataSource;

    private PoolDatabase() {
        InitialContext ctx = null;
        try {
            ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:jboss/datasources/bajtahack");
        } catch (NamingException e) {
            final String reason = "Failed to retrieve the datasource from the context: " + e.toString();
            throw new IllegalStateException(reason, e);
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw e;
        }
    }

    public String get123() {
        ResultSet rs = null;
        PreparedStatement statement = null;
        Connection connection = null;
        String out = "";
        try {
            final String sql = "SELECT * FROM  bajtatest";
            connection = PoolDatabase.instance.getConnection();
            statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = statement.executeQuery();
            while (rs.next()) { 
                out += "<br>";
                out += rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    ;
                }
                rs = null;
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    ;
                }
                statement = null;
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    ;
                }
                connection = null;
            }
        }
        return out;
    }
    
}