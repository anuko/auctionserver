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


package business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DatabaseManager;


/**
 * Helper class for operations with users.
 *
 * @author Nik OKuntseff
 */
public class UserHelper {

    private static final Logger Log = LoggerFactory.getLogger(UserHelper.class);

    /**
     * Determines UUID of a user if one exists in the database.
     *
     * @param login user login.
     *
     * @return user UUID or null if not found.
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


    /**
     * Inserts a user record into the database.
     *
     * @param login user login.
     * @param password user password.
     * @param full_name user full name.
     * @param email user email.
     * @return true on success.
     */
    public static boolean insert(String login, String password, String full_name, String email) {

        UUID uuid = UUID.randomUUID();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int insertResult = 0;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("insert into as_users " +
                    "values (?, ?, md5(?), ?, ?, 1)");
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, login);
            pstmt.setString(3, password);
            pstmt.setString(4, full_name);
            pstmt.setString(5, email);
            insertResult = pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        return (1 == insertResult);
  }
}
