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
 * Contains utility functions to work with auction items.
 *
 * @author Nik Okuntseff
 */
public class AuctionManager {

    private static final Logger Log = LoggerFactory.getLogger(AuctionManager.class);

    /**
     * Updates item status.
     *
     * @param item_uuid item <code>UUID</code>
     * @param status new item status
     */
    public static void updateStatus(String item_uuid, int status) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("update as_items set status = ? " +
                "where uuid = ?");
            pstmt.setInt(1, status);
            pstmt.setString(2, item_uuid);
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
     * Sets reminder_sent flag for an item.
     *
     * @param item_uuid item <code>UUID</code>
     */
    public static void markReminderSent(String item_uuid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("update as_items set reminder_sent = 1 where uuid = ?");
            pstmt.setString(1, item_uuid);
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
