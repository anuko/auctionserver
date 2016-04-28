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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides helper methods to work with users.
 *
 * @author Nik Okuntseff
 */
public class UserManager {

    private static final Logger Log = LoggerFactory.getLogger(DatabaseManager.class);


    /**
     * Determines whether a user with a specific login already exists.
     *
     * @param login user login
     * @return user UUID <code>String</code> or null if not found.
     */
    public static String getUserByLogin(String login) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String uuid = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid from as_users where login = ? and (status = 1 or status = 0)");
            pstmt.setString(1, login);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                uuid = rs.getString(1);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        return uuid;
    }
}
