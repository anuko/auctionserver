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
import utils.BidManager;


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
            // processNewAuctions();
            // processNewBids();
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
    /*
    private void processNewAuctions() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();

            pstmt = conn.prepareStatement("select uuid, name from as_items where approved is null and processed = 0");
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
    }*/


    /**
     * Processes new bids.
     */
    /*
    private void processNewBids() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();

            pstmt = conn.prepareStatement("select uuid from as_bids where confirmed = 1 and processed = 0");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString(1);
                BidManager.processNewBid(uuid);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }*/


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
