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
import java.text.ParseException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import listeners.ApplicationListener;


/**
 * Holds information about a single auction item.
 *
 * @author Nik Okuntseff
 */
public class AuctionItem {

    private static final Logger Log = LoggerFactory.getLogger(AuctionItem.class);

    private String uuid;
    private String origin;
    private String seller_uuid;
    private String name;
    private String description;
    private String image_uri;
    private String created_timestamp;
    private String close_timestamp;
    private String currency;
    private float reserve_price;
    private int bids;
    private float top_bid;
    private String top_bid_uuid;
    private String top_bidder_uuid;
    private String top_bidder_name;
    private int approved;
    private int processed;
    private int status;
    private String item_uri;

    public AuctionItem() {
    }

    /**
     * Initializes an item from the database.
     *
     * @param auctionUuid auction uuid.
     */
    public AuctionItem(String auctionUuid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select i.uuid, i.origin, i.seller_uuid, i.name, i.description, i.image_uri, " +
                "i.created_timestamp, i.close_timestamp, i.currency, i.reserve_price, i.bids, i.top_bid, " +
                "i.top_bid_uuid, i.approved, i.processed, i.status, u.name as top_bidder_name, u.uuid as tob_bidder_uuid " +
                "from as_items i " +
                "left join as_bids b on (b.uuid = i.top_bid_uuid) " +
                "left join as_users u on (u.uuid = b.user_uuid) " +
                "where i.uuid = ?");
            pstmt.setString(1, auctionUuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                uuid = rs.getString("uuid");
                origin = rs.getString("origin");
                seller_uuid = rs.getString("seller_uuid");
                name = rs.getString("name");
                description = rs.getString("description");
                image_uri = rs.getString("image_uri");
                created_timestamp = rs.getString("created_timestamp");
                close_timestamp = rs.getString("close_timestamp");
                currency = rs.getString("currency");
                reserve_price = rs.getFloat("reserve_price");
                bids = rs.getInt("bids");
                top_bid = rs.getFloat("top_bid");
                top_bid_uuid = rs.getString("top_bid_uuid");
                top_bidder_name = rs.getString("top_bidder_name");
                top_bidder_uuid = rs.getString("tob_bidder_uuid");
                approved = rs.getInt("approved");
                processed = rs.getInt("processed");
                status = rs.getInt("status");
                item_uri = Site.getUri() + "/auction.jsp?uuid=" + uuid;
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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String val) {
        origin = val;
    }

    public String getSellerUuid() {
        return seller_uuid;
    }

    public void setSellerUuid(String val) {
        seller_uuid = val;
    }

    public String getName() {
        return name;
    }

    public void setName(String val) {
        name = val;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String val) {
        description = val;
    }

    public String getImageUri() {
        return image_uri;
    }

    public void setImageUri(String val) {
        image_uri = val;
    }

    public String getCreatedTimestamp() {
        return created_timestamp;
    }

    public void setCreatedTimestamp(String val) {
        created_timestamp = val;
    }

    public String getCloseTimestamp() {
        return close_timestamp;
    }

    public void setCloseTimestamp(String val) {
        close_timestamp = val;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String val) {
        currency = val;
    }

    public float getReservePrice() {
        return reserve_price;
    }

    public void setReservePrice(float val) {
        reserve_price = val;
    }

    public int getBids() {
        return bids;
    }

    public void setBids(int val) {
        bids = val;
    }

    public float getTopBid() {
        return top_bid;
    }

    public void setTopBid(float val) {
        top_bid = val;
    }

    public String getTopBidUuid() {
        return top_bid_uuid;
    }

    public void setTopBidUuid(String val) {
        top_bid_uuid = val;
    }

    public String getTopBidderName() {
        return top_bidder_name;
    }

    public void setTopBidderName(String val) {
        top_bidder_name = val;
    }

    public String getTopBidderUuid() {
        return top_bidder_uuid;
    }

    public void setTopBidderUuid(String val) {
        top_bidder_uuid = val;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int val) {
        approved = val;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int val) {
        processed = val;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int val) {
        status = val;
    }

    public String getItemUri() {
        return item_uri;
    }

    public void setItemUri(String val) {
        item_uri = val;
    }

    /**
     * Returns localized top bid with currency.
     */
    public String getTopBidString() {
        return currency + " " + String.format(I18n.getLocale(), "%.2f", top_bid);
    }

    /**
     * Returns localized top bid with bidder info.
     */
    public String getTopBidWithBidder() {
        String currentBidString = getTopBidString();
        if (bids > 0)
            currentBidString += " " + I18n.get("label.from") + " " + getTopBidderObfuscatedUuid();
        return currentBidString;
    }

    /**
     * Returns obfuscated bidder UUID.
     */
    public String getTopBidderObfuscatedUuid() {
        return top_bidder_uuid.charAt(0) + "***" + top_bidder_uuid.charAt(35);
    }

    /**
     * Returns obfuscated item UUID.
     */
    public String getObfuscatedUuid() {
        return uuid.charAt(0) + "***" + uuid.charAt(35);
    }

    /**
     * Returns localized remaining time in "N days hh:mm" format.
     */
    public String getTimeRemaining() {

        Date now = new Date();
        Date close = now;
        try { close = ApplicationListener.getSimpleDateFormat().parse(close_timestamp); }
        catch (ParseException e) { }

        long diff = close.getTime() - now.getTime(); // Milliseconds.
        if (diff < 0) diff = 0;

        // long diffSeconds = diff / 1000 % 60;
	long diffMinutes = diff / (60 * 1000) % 60;
	long diffHours = diff / (60 * 60 * 1000) % 24;
	long diffDays = diff / (24 * 60 * 60 * 1000);

        String timeRemaining = diffDays + " " + I18n.get("remaining.days");
        timeRemaining += " " + String.format("%02d:%02d", diffHours, diffMinutes);
        return timeRemaining;
    }

    /**
     * Returns state of the item.
     */
    public String getState() {
        if (processed == 0 && approved == 0)
            return I18n.get("state.auction.pending");
        if (processed == 1 && approved == 0)
            return I18n.get("state.auction.disapproved");
        if (status == 1)
            return I18n.get("state.auction.active");
        if (status == 0)
            return I18n.get("state.auction.closed");

        return I18n.get("state.auction.unknown");
    }

    /**
     * Returns checkout email for the item.
     */
    public String getCheckoutEmail() {

        String checkoutEmail = null;
        List<HashMap<String,String>> currencies = (List<HashMap<String,String>>) ApplicationListener.getServletContext().getAttribute("currencies");

        for (HashMap<String,String> currency : currencies) {
            if (currency.get("name").equals(this.currency)) {
                checkoutEmail = currency.get("checkout_email");
                break;
            }
        }
        return checkoutEmail;
    }
}
