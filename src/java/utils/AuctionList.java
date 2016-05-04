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
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import listeners.ApplicationListener;


/**
 * Obtains a list of auction items.
 *
 * @author Nik Okuntseff
 */
public class AuctionList {

    private static final Logger Log = LoggerFactory.getLogger(AuctionList.class);


    /**
     * Obtain a list of auction items.
     */
    public static List<AuctionItem> getAuctions() {

        List<AuctionItem> list = new ArrayList<AuctionItem>();

        Date now_date = new Date();
        String now = ApplicationListener.getSimpleDateFormat().format(now_date);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid, origin, seller_uuid, name, " +
                    "top_bid, bids, close_timestamp " +
                    "from as_items " +
                    "where close_timestamp > ? and approved = 1 " +
                    "order by close_timestamp");
            pstmt.setString(1, now);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                AuctionItem item = new AuctionItem();
                item.setUuid(rs.getString("uuid"));
                item.setOrigin(rs.getString("origin"));
                item.setSellerUuid(rs.getString("seller_uuid"));
                item.setName(rs.getString("name"));
                item.setTopBid(rs.getFloat("top_bid"));
                item.setBids(rs.getInt("bids"));
                item.setCloseTimestamp(rs.getString("close_timestamp"));
                list.add(item);
                // Limit output to 50 rows to keep things simple for now.
                if (++count >= 50)
                    break;
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        return list;
    }


    /**
     * Obtain a list of auction items for a specific user.
     */
    public static List<AuctionItem> getUserAuctions(String user_uuid) {

        List<AuctionItem> list = new ArrayList<AuctionItem>();

        Date now_date = new Date();
        String now = ApplicationListener.getSimpleDateFormat().format(now_date);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid, origin, seller_uuid, name, " +
                    "top_bid, bids, close_timestamp, approved " +
                    "from as_items " +
                    "where seller_uuid = ? and close_timestamp > ? " +
                    "order by close_timestamp");
            pstmt.setString(1, user_uuid);
            pstmt.setString(2, now);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                AuctionItem item = new AuctionItem();
                item.setUuid(rs.getString("uuid"));
                item.setOrigin(rs.getString("origin"));
                item.setSellerUuid(rs.getString("seller_uuid"));
                item.setName(rs.getString("name"));
                item.setTopBid(rs.getFloat("top_bid"));
                item.setBids(rs.getInt("bids"));
                item.setCloseTimestamp(rs.getString("close_timestamp"));
                item.setApproved(rs.getInt("approved"));
                list.add(item);
                // Limit output to 50 rows to keep things simple for now.
                if (++count >= 50)
                    break;
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        return list;
    }
}
