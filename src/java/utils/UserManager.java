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
import java.util.Date;
import java.util.UUID;
import java.util.HashMap;
import listeners.ApplicationListener;
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
                uuid = rs.getString("uuid");
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
                email = rs.getString("email");
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
     * Retrieves seller information
     *
     * @param item_uuid item <code>UUID</code>
     * @return a map of seller attributes such as name and email
     */
    public static HashMap<String, String> getSellerInfo(String item_uuid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> map = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select i.seller_uuid, u.name, u.email " +
                "from as_items i " +
                "left join as_users u on (u.uuid = i.seller_uuid) " +
                "where i.uuid = ?");
            pstmt.setString(1, item_uuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                map = new HashMap<String, String>();
                map.put("name", rs.getString("name"));
                map.put("email", rs.getString("email"));
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        return map;
    }


    /**
     * Retrieves bidder email
     *
     * @param bid_uuid bid<code>UUID</code>
     * @return a map of bidder attributes such as name and email
     */
    public static HashMap<String, String> getBidderInfo(String bid_uuid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> map = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select b.user_uuid, u.name, u.email " +
                "from as_bids b " +
                "left join as_users u on (u.uuid = b.user_uuid) " +
                "where b.uuid = ?");
            pstmt.setString(1, bid_uuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                map = new HashMap<String, String>();
                map.put("name", rs.getString("name"));
                map.put("email", rs.getString("email"));
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        return map;
    }

    /**
     * Retrieves bidder information.
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
                email = rs.getString("email");
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


    /**
     * Retrieves first found user UUID for a specified email.
     *
     * @param email user email
     * @return user UUID <code>String</code> or null if not found.
     */
    public static String getUserByEmail(String email) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String uuid = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid from as_users where email = ? and (status = 1 or status = 0)");
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                uuid = rs.getString("uuid");
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
     * Creates an unconfirmed user with email as login and a random password.
     *
     * @param email user email
     * @return user UUID <code>String</code>.
     */
    public static String createUnconfirmedUser(String email) {

        // Prepare data for insertion.
        String user_uuid = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean userCreated = false;
        try {
            // Insert user record.
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("insert into as_users " +
                "set uuid = ?, login = ?,  password = md5(?), " +
                "name = ?, email = ?, status = 1");
            pstmt.setString(1, user_uuid);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, I18n.get("label.user"));
            pstmt.setString(5, email.toLowerCase(I18n.getLocale()));
            int rows = pstmt.executeUpdate();
            userCreated = (1 == rows);
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        if (!userCreated)
            return null;

        return user_uuid;
    }


    /**
     * Creates a reference that can be used to confirm a user.
     *
     * @param user_uuid user UUID.
     * @return reference UUID <code>String</code>.
     */
    public static String createUnconfirmedUserReference(String user_uuid) {

        // Prepare data for insertion.
        UUID random = UUID.randomUUID();
        Date now = new Date();
        String created_timestamp = ApplicationListener.getSimpleDateFormat().format(now);
        String reference = random.toString(); // To create random URL for confirmation.

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean refCreated = false;
        try {
            // Insert user record.
            conn = DatabaseManager.getConnection();

            // Insert reference for user into as_tmp_refs table.
            pstmt = conn.prepareStatement("insert into as_tmp_refs " +
                "set uuid = ?, user_uuid = ?,  created_timestamp = ?");
            pstmt.setString(1, reference);
            pstmt.setString(2, user_uuid);
            pstmt.setString(3, created_timestamp);
            int rows = pstmt.executeUpdate();
            refCreated = (1 == rows);
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        if (!refCreated)
            return null;

        return reference;
    }


    /**
     * Creates a reference that can be used to confirm a bid for user.
     *
     * @param user_uuid user UUID.
     * @param bid_uuid bid UUID.
     * @return reference UUID <code>String</code>.
     */
    public static String createUnconfirmedBidReference(String user_uuid, String bid_uuid) {

        // Prepare data for insertion.
        String reference = UUID.randomUUID().toString();
        Date now = new Date();
        String created_timestamp = ApplicationListener.getSimpleDateFormat().format(now);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean refCreated = false;
        try {
            // Insert user record.
            conn = DatabaseManager.getConnection();

            // Insert reference for user into as_tmp_refs table.
            pstmt = conn.prepareStatement("insert into as_tmp_refs " +
                "set uuid = ?, user_uuid = ?,  bid_uuid = ?, created_timestamp = ?");
            pstmt.setString(1, reference);
            pstmt.setString(2, user_uuid);
            pstmt.setString(3, bid_uuid);
            pstmt.setString(4, created_timestamp);
            int rows = pstmt.executeUpdate();
            refCreated = (1 == rows);
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        if (!refCreated)
            return null;

        return reference;
    }
}
