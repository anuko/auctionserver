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
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
    public static List<Bid> getUserBids(String user_uuid) {

        List<Bid> list = new ArrayList<Bid>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select b.uuid, b.item_uuid, i.name, i.currency, " +
                "b.amount, b.confirmed, b.processed, b.status " +
                "from as_bids b " +
                "left join as_items i on (i.uuid = b.item_uuid) " +
                "where b.user_uuid = ? " +
                "order by b.created_timestamp");
            pstmt.setString(1, user_uuid);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                Bid bid = new Bid();
                bid.setUuid(rs.getString("uuid"));
                bid.setItemUuid(rs.getString("item_uuid"));
                bid.setItemName(rs.getString("name"));
                bid.setCurrency(rs.getString("currency"));
                bid.setAmount(rs.getFloat("amount"));
                bid.setConfirmed(rs.getInt("confirmed"));
                bid.setProcessed(rs.getInt("processed"));
                bid.setStatus(rs.getInt("status"));
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
