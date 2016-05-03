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


package threads;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import listeners.ApplicationListener;
import utils.AuctionManager;
import utils.DatabaseManager;
import utils.NotificationManager;


/**
 * Processing thread for auction server.
 * Handles 3 types of workflows:
 *   1) auction creation processing
 *   2) bid processing
 *   3) auction closing processing
 * This thread runs continuously and does things in sequence
 * (when there is anything to do).
 *
 * @author Nik Okuntseff
 */
public class ProcessingThread implements Runnable {

    private static final Logger Log = LoggerFactory.getLogger(ProcessingThread.class);


    /**
     * Implements 3 types of workflow processing.
     */
    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {

            // Do each workflow in a separate call.
            processNewAuctions();
            processNewBids();
            closeExpiredAuctions();

            // Wait a minute.
            try {
                Thread.sleep(60000);
            }
            catch (InterruptedException e) {
                Log.info("OutboundThread interrupted while sleeping.");
                return;
            }
        }
    }


    /**
     * Processes new auctions by sending a notification to site admin.
     */
    private void processNewAuctions() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();

            pstmt = conn.prepareStatement("select uuid, name from as_items where approved is null and processed is null");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString(1);
                NotificationManager.notifyAdminNewAuction(uuid);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }


    /**
     * Processes new bids.
     */
    private void processNewBids() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();

            pstmt = conn.prepareStatement("select uuid from as_bids where processed is null");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString(1);
                processNewBid(uuid);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }


    /**
     * Processes a new bid.
     */
    private void processNewBid(String bid_uuid) {

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
                bids = rs.getInt("bid");
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
                NotificationManager.notifySellerNewBid(item_uuid, Float.toString(bid_amount));

                // Notify losing bidder.
                if (current_bid_uuid != null) {
                    NotificationManager.notifyLosingBidder(current_bid_uuid, item_uuid, Float.toString(bid_amount));
                }

                // Notify currently winning bidder. // TODO: rename to notifyCurrentTopBidder
                NotificationManager.notifyCurrentTopBidder(bid_uuid, item_uuid, Float.toString(bid_amount));
            } else {
                NotificationManager.notifyLosingBidder(bid_uuid, item_uuid, Float.toString(current_bid));
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }


    /**
     * Processes expired auctions.
     */
    private void closeExpiredAuctions() {

        Date now_date = new Date();
        String now = ApplicationListener.getSimpleDateFormat().format(now_date);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();

            pstmt = conn.prepareStatement("select uuid from as_items where status = 1 and close_timestamp < ?");
            pstmt.setString(1, now);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString(1);
                closeExpiredAuction(uuid);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }


    /**
     * Processes a single expired auction.
     */
    private void closeExpiredAuction(String item_uuid) {

        AuctionManager.updateStatus(item_uuid, 0);

        // Notify seller.
        NotificationManager.notifySellerAuctionClose(item_uuid);

        // Notify bidder.
        NotificationManager.notifyBidderAuctionClose(item_uuid);
    }
}
