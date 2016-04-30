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
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.NotificationManager;


/**
 * Processing thread for auction server.
 * Handles 3 types of workflow:
 *   1) auction creation.
 *   2) bids
 *   3) auction closing
 * This thread runs continuously and does workflow things in sequence (when there
 * is something to do).
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

            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                conn = DatabaseManager.getConnection();

                // Workflow 1: Check if any new auctions got created.
                // If so, send a notification to site admin for manual approval.

                pstmt = conn.prepareStatement("select uuid, name from as_auctions where status is null");
                rs = pstmt.executeQuery();
                while (rs.next()) {
Log.error("new auction detected: " + rs.getString(2));
                    String uuid = rs.getString(1);
                    NotificationManager.notifyAdminNewAuction(uuid);

                    // Exit the thread if we are interrupted.
                    if (Thread.currentThread().isInterrupted()) {
                        Log.info("OutboundThread was interrupted during delivery loop.");
                        return;
                    }
                }
            }
            catch (SQLException e) {
                Log.error(e.getMessage(), e);
            }
            finally {
                DatabaseManager.closeConnection(rs, pstmt, conn);
            }

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
}
