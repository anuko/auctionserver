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


package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.DatabaseManager;
import utils.I18n;


/**
 * Holds information about a bid being added by user.
 * An instance of this class is used to pass bid data between a view and a controller.
 * All members are strings, as we need to display them in case of invalid input.
 *
 * @author Nik Okuntseff
 */
public class BidBean {

    private static final Logger Log = LoggerFactory.getLogger(BidBean.class);

    private String uuid;         // UUID for this bid.
    private String item_uuid;    // UUID of the item using is bidding on.
    private String seller_uuid;  // Needed to prohibit bidding on own items.
    private String item_name;
    private String currency;
    private float current_bid;   // Current top bid on the item. Float because we never display it.
    private String amount;       // Amount of this bid, must be greater then current bid.


    public BidBean() {
    }


   /**
     * Initializes bean from the database.
     *
     * @param bidUuid bid uuid.
     * @param bidderUuid bidder uuid.
     */
    public BidBean(String bidUuid, String bidderUuid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select b.uuid, b.item_uuid, i.seller_uuid, i.name, " +
                "i.currency, i.top_bid, b.amount " +
                "from as_bids b " +
                "left join as_items i on (b.item_uuid = i.uuid) " +
                "where b.uuid = ? and b.user_uuid = ?");
            pstmt.setString(1, bidUuid);
            pstmt.setString(2, bidderUuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                uuid = rs.getString("uuid");
                item_uuid = rs.getString("item_uuid");
                seller_uuid = rs.getString("seller_uuid");
                item_name = rs.getString("name");
                currency = rs.getString("currency");
                current_bid = rs.getFloat("top_bid");
                amount = String.format(I18n.getLocale(), "%.2f", rs.getFloat("amount"));
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }


    // Getter and setter functions below.


    public String getUuid() {
        return uuid;
    }


    public void setUuid(String val) {
        uuid = val;
    }


    public String getItemUuid() {
        return item_uuid;
    }


    public void setItemUuid(String val) {
        item_uuid = val;
    }


    public String getSellerUuid() {
        return seller_uuid;
    }


    public void setSellerUuid(String val) {
        seller_uuid = val;
    }


    public String getItemName() {
        return item_name;
    }


    public void setItemName(String val) {
        item_name = val;
    }


    public String getCurrency() {
        return currency;
    }


    public void setCurrency(String val) {
        currency = val;
    }


    public float getCurrentBid() {
        return current_bid;
    }


    public void setCurrentBid(float val) {
        current_bid = val;
    }


    public String getAmount() {
        return amount;
    }


    public void setAmount(String val) {
        amount = val;
    }
}
