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

import beans.BidBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlets.ApplicationListener;


/**
 * Obtains a list of bids for user.
 *
 * @author Nik Okuntseff
 */
public class BidList {

    private static final Logger Log = LoggerFactory.getLogger(BidList.class);


    /**
     * Obtain a list of bids for a specific user.
     */
    public static List<BidBean> getUserBids(String userUuid) {

        List<BidBean> list = new ArrayList<BidBean>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select b.uuid, b.item_uuid, a.name, a.currency, b.max_price, b.status " +
                    "from as_bids b " +
                    "left join as_auctions a on (a.uuid = b.item_uuid) " +
                    "where b.user_uuid = ? " +
                    "order by b.created_timestamp");
            pstmt.setString(1, userUuid);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                BidBean bid = new BidBean();
                bid.setUuid(rs.getString(1));
                bid.setItemUuid(rs.getString(2));
                bid.setItemName(rs.getString(3));
                bid.setCurrency(rs.getString(4));
                bid.setAmount(rs.getString(5));
                bid.setStatus(rs.getString(6));
                list.add(bid);
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
