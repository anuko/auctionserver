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


    /**
     * Retrieves user email.
     *
     * @param uuid user <code>UUID</code>
     * @return user email or null if not found.
     */
    public static String getUserEmail(String user_uuid) {

        String email = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select email " +
                    "from as_users where uuid = ?");
            pstmt.setString(1, user_uuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                email = rs.getString(1);
            }
         }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        return email;
    }


    /**
     * Retrieves first found user UUID by email.
     *
     * @param email user email.
     * @return user <code>UUID</code> or null if not found.
     */
    public static String getUserUuid(String email) {

        String uuid = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid " +
                "from as_users where email = ? and status is not null");
            pstmt.setString(1, email);
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
     * Retrieves seller email.
     *
     * @param item_uuid item <code>UUID</code>
     * @return seller email or null if not found.
     */
    public static String getSellerEmail(String item_uuid) {

        String email = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select i.seller_uuid, u.email " +
                "from as_items i " +
                "left join as_users u on (u.uuid = i.seller_uuid) " +
                "where i.uuid = ?");
            pstmt.setString(1, item_uuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                email = rs.getString(2);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        return email;
    }


    /**
     * Retrieves bidder email.
     *
     * @param bid_uuid bid<code>UUID</code>
     * @return bidder email or null if not found.
     */
    public static String getBidderEmail(String bid_uuid) {

        String email = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select b.user_uuid, u.email " +
                "from as_bids b " +
                "left join as_users u on (u.uuid = b.user_uuid) " +
                "where b.uuid = ?");
            pstmt.setString(1, bid_uuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                email = rs.getString(2);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        return email;
    }


    /**
     * Counts number of users with a specified email.
     *
     * @param email user email.
     * @return number of logins with a specified email.
     */
    public static int countUsers(String email) {

        int count = 0;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select count(*) from as_users " +
                "where email = ? and status is not null");
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        return count;
    }
}
