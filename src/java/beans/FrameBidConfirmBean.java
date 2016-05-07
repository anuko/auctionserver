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
import utils.UUIDUtil;


/**
 * Holds information about a frame bid being confirmed.
 * A frame bid originates from participating websites, where we know only user email and amount.
 *
 * @author Nik Okuntseff
 */
public class FrameBidConfirmBean {

    private static final Logger Log = LoggerFactory.getLogger(UserConfirmBean.class);

    private String ref_uuid;     // Temporary reference UUID.
    private String uuid;         // UUID for this bid.
    private String item_uuid;    // UUID of the item using is bidding on.
    private String seller_uuid;  // Needed to prohibit bidding on own items.
    private String item_name;
    private String currency;
    private float current_bid;   // Current top bid on the item. Float because we never display it.
    private String amount;       // Amount of this bid, must be greater then current bid.


    public FrameBidConfirmBean() {
    }


    /**
     * Constructs a bean using temporary reference UUID.
     *
     * @param reference reference UUID.
     */
    public FrameBidConfirmBean(String reference) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String bid_uuid = null;

        try {
            // Determine if we have a reference in the table.
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select bid_uuid from as_tmp_refs " +
                "where uuid = ?");
            pstmt.setString(1, reference);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                bid_uuid = rs.getString("bid_uuid");
            }

            if (UUIDUtil.isUUID(bid_uuid)) {
                pstmt = conn.prepareStatement("select b.uuid, b.item_uuid, i.seller_uuid, " +
                    "i.name, i.currency, i.top_bid, b.amount " +
                    "from as_bids b " +
                    "left join as_items i on (i.uuid = b.item_uuid) " +
                    "where b.uuid = ?");
                pstmt.setString(1, bid_uuid);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    ref_uuid = reference;
                    uuid = rs.getString("uuid");
                    item_uuid = rs.getString("item_uuid");
                    seller_uuid = rs.getString("seller_uuid");
                    item_name = rs.getString("name");
                    currency = rs.getString("currency");
                    current_bid = rs.getFloat("top_bid");
                    amount = String.format(I18n.getLocale(), "%.2f", rs.getFloat("amount"));
                }
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


    public String getRefUuid() {
        return ref_uuid;
    }


    public void setRefUuid(String val) {
        ref_uuid = val;
    }


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
