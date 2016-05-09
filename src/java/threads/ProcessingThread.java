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
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import listeners.ApplicationListener;
import utils.AuctionManager;
import utils.DatabaseManager;
import utils.NotificationManager;


/**
 * Processing thread for auction server.
 * Closes expired auctions, also sends closing reminders for items.
 *
 * @author Nik Okuntseff
 */
public class ProcessingThread implements Runnable {

    private static final Logger Log = LoggerFactory.getLogger(ProcessingThread.class);


    /**
     * Closes expired auctions and sends closing reminders for items.
     */
    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {

            // Do each workflow in a separate call.
            closeExpiredAuctions();
            sendClosingReminders();

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

    /**
     * Sends 24-hour closing reminders to losing bidders.
     */
    private void sendClosingReminders() {

        // Determine cutoff_timestamp.
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, +1);
        Date tomorrow = new Date(c.getTimeInMillis());
        String cutoff_timestamp = ApplicationListener.getSimpleDateFormat().format(tomorrow);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid from as_items where status = 1 and reminder_sent = 0 and close_timestamp < ?");
            pstmt.setString(1, cutoff_timestamp);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String itemUuid = rs.getString(1);
                AuctionManager.markReminderSent(itemUuid);
                sendClosingReminder(itemUuid);
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
     * Sends a 24-hour closing reminder to all losing bidders for a single item.
     */
    private void sendClosingReminder(String itemUuid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select distinct u.email from as_bids b " +
                "left join as_users u on (u.uuid = b.user_uuid) " +
                "where b.item_uuid = ? and b.status = 0");
            pstmt.setString(1, itemUuid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String email = rs.getString("email");
                NotificationManager.notifyBidderClosingReminder(itemUuid, email);
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
