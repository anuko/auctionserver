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
     * @return bid UUID.
     */
    public static String createUnconfirmedBid(String user_uuid, String item_uuid, float bid_amount) {

        String bidUuid = UUID.randomUUID().toString();

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
            pstmt.setString(1, bidUuid);
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
        return bidUuid;
    }


    /**
     * Processes a new bid.
     */
    public static void processNewBid(String bid_uuid) {

        String item_uuid = null;
        float bid_amount = 0.0f;
        String bidder_uuid = null;
        float current_bid = 0.0f;
        String current_bid_uuid = null;
        int bids = 0;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();

            // Update processed flag.
            pstmt = conn.prepareStatement("update as_bids set processed = 1 where uuid = ?");
            pstmt.setString(1, bid_uuid);
            pstmt.executeUpdate();

            // Obtain bid details.
            pstmt = conn.prepareStatement("select item_uuid, amount, user_uuid " +
                "from as_bids where uuid = ?");
            pstmt.setString(1, bid_uuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                item_uuid = rs.getString("item_uuid");
                bid_amount = rs.getFloat("amount");
                bidder_uuid = rs.getString("user_uuid");
            }

            // Obtain current details of the item.
            pstmt = conn.prepareStatement("select top_bid, bids, top_bid_uuid from as_items " +
                "where uuid = ?");
            pstmt.setString(1, item_uuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                current_bid = rs.getFloat("top_bid");
                bids = rs.getInt("bids");
                current_bid_uuid = rs.getString("top_bid_uuid");
            }

            if (bid_amount > current_bid) {
                // Update the item.
                pstmt = conn.prepareStatement("update as_items set top_bid = ?, top_bid_uuid = ?, bids = ? where uuid = ?");
                pstmt.setFloat(1, bid_amount);
                pstmt.setString(2, bid_uuid);
                pstmt.setInt(3, ++bids);
                pstmt.setString(4, item_uuid);
                pstmt.executeUpdate();

                // Notify seller.
                NotificationManager.notifySellerNewBid(item_uuid, bid_amount);

                // Notify losing bidder.
                if (current_bid_uuid != null) {
                    NotificationManager.notifyLosingBidder(current_bid_uuid, item_uuid, bid_amount);
                }

                // Notify currently winning bidder.
                NotificationManager.notifyCurrentTopBidder(bid_uuid, item_uuid, bid_amount);
            } else {
                NotificationManager.notifyLosingBidder(bid_uuid, item_uuid, current_bid);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }
}
