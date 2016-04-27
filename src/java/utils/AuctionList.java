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
import servlets.ApplicationListener;


/**
 * Obtains a list of auctions.
 *
 * @author Nik Okuntseff
 */
public class AuctionList {

    private static final Logger Log = LoggerFactory.getLogger(AuctionList.class);


    /**
     * Obtain a list of auction items.
     */
    public static List<AuctionBean> getAuctions() {

        List<AuctionBean> list = new ArrayList<AuctionBean>();

        Date now_date = new Date();
        String now = ApplicationListener.getSimpleDateFormat().format(now_date);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid, origin, seller_uuid, name, close_timestamp " +
                    "from as_auctions " +
                    "where close_timestamp > ? and approved = 1 " +
                    "order by close_timestamp");
            pstmt.setString(1, now);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                AuctionBean auction = new AuctionBean();
                auction.setUuid(rs.getString(1));
                auction.setOrigin(rs.getString(2));
                auction.setSellerUuid(rs.getString(3));
                auction.setName(rs.getString(4));
                auction.setCloseTimestamp(rs.getString(5));
                list.add(auction);
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
    public static List<AuctionBean> getUserAuctions(String userUuid) {

        List<AuctionBean> list = new ArrayList<AuctionBean>();

        Date now_date = new Date();
        String now = ApplicationListener.getSimpleDateFormat().format(now_date);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid, origin, seller_uuid, name, close_timestamp, approved " +
                    "from as_auctions " +
                    "where seller_uuid = ? and close_timestamp > ? and status is not null " +
                    "order by close_timestamp");
            pstmt.setString(1, userUuid);
            pstmt.setString(2, now);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                AuctionBean auction = new AuctionBean();
                auction.setUuid(rs.getString(1));
                auction.setOrigin(rs.getString(2));
                auction.setSellerUuid(rs.getString(3));
                auction.setName(rs.getString(4));
                auction.setCloseTimestamp(rs.getString(5));
                auction.setApproved(rs.getString(6));
                list.add(auction);
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
