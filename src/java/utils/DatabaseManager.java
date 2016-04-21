/*
Copyright Anuko International Ltd. (https://www.anuko.com)

LIBERAL FREEWARE LICENSE: This source code document may be used
by anyone for any purpose, and freely redistributed alone or in
combination with other software, provided that the license is obeyed.

There are only two ways to violate the license:

1. To redistribute this code in source form, with the copyright notice or
   license removed or altered. (Distributing in compiled forms without
   embedded copyright notices is permitted).

2. To redistribute modified versions of this code in *any* form
   that bears insufficient indications that the modifications are
   not the work of the original author(s).

This license applies to this document only, not any other software that it
may be combined with.
*/


package utils;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manages connections to the database and provides utility functions to close
 * result sets and statements.
 *
 * @author Nik Okuntseff
 */
public class DatabaseManager {

    public static String dataSourceName = "java:comp/env/jdbc/auctionserver";

    private static final Logger Log = LoggerFactory.getLogger(DatabaseManager.class);

    public static Connection getConnection() throws SQLException {
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup(dataSourceName);
            if (ds == null)
                throw new SQLException("No datasource " + dataSourceName);
            return ds.getConnection();

        } catch (NamingException e) {
            throw new SQLException("Cannot get datasource " + dataSourceName, e);
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
               conn.close();
            }
            catch (Exception e) {
                Log.error(e.getMessage(), e);
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                    rs.close();
                }
            catch (SQLException e) {
                Log.error(e.getMessage(), e);
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (Exception e) {
                Log.error(e.getMessage(), e);
            }
        }
    }

    public static void closeStatement(ResultSet rs, Statement stmt) {
        closeResultSet(rs);
        closeStatement(stmt);
    }

    public static void closeConnection(ResultSet rs, Statement stmt, Connection conn) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn);
    }

    public static void closeConnection(Statement stmt, Connection con) {
        closeStatement(stmt);
        closeConnection(con);
    }
}
