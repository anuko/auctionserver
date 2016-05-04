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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import listeners.ApplicationListener;


/**
 * Contains utility functions to work with bids.
 *
 * @author Nik Okuntseff
 */
public class BidManager {

    private static final Logger Log = LoggerFactory.getLogger(BidManager.class);


    /**
     * Updates bid status.
     *
     * @param bid_uuid bid <code>UUID</code>
     * @param status new bid status
     */
    public static void updateStatus(String bid_uuid, int status) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("update as_bids set status = ? " +
                "where uuid = ?");
            pstmt.setInt(1, status);
            pstmt.setString(2, bid_uuid);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }


    /**
     * Creates an unconfirmed bid.
     *
     * @param user_uuid user <code>UUID</code>.
     * @param item_uuid item <code>UUID</code>.
     * @param bid_amount bid amount.
     */
    public static void createUnconfirmedBid(String user_uuid, String item_uuid, float bid_amount) {

        UUID uuid = UUID.randomUUID();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Date now = new Date();
        String created_timestamp = ApplicationListener.getSimpleDateFormat().format(now);

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("insert into as_bids " +
                "set uuid = ?, origin = ?, item_uuid = ?, amount = ?, " +
                "user_uuid = ?, created_timestamp = ?"); // confirmed flag will be NULL by default
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, Site.getUuid());
            pstmt.setString(3, item_uuid);
            pstmt.setFloat(4, bid_amount);
            pstmt.setString(5, user_uuid);
            pstmt.setString(6, created_timestamp);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }
}
