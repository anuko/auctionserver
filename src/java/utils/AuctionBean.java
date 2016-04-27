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
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlets.ApplicationListener;


/**
 * Holds information about an auction.
 *
 * @author Nik Okuntseff
 */
public class AuctionBean {

    private static final Logger Log = LoggerFactory.getLogger(AuctionBean.class);

    private String uuid;
    private String origin;
    private String seller_uuid;
    private String name;
    private String description;
    private String image_uri;
    private String created_timestamp;
    private String close_timestamp;
    private String currency;
    private String reserve_price;
    private String bids;
    private String current_price;
    private String approved;
    private String status;

    private String duration;

    public AuctionBean() {
    }


    /**
     * Initializes bean from the database.
     *
     * @param auctionUuid auction uuid.
     * @param sellerUuid seller uuid.
     */
    public AuctionBean(String auctionUuid, String sellerUuid) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid, origin, seller_uuid, name, description, image_uri, " +
                    "created_timestamp, close_timestamp, currency, reserve_price, bids, current_price, approved, status " +
                    "from as_auctions where uuid = ? and seller_uuid = ?");
            pstmt.setString(1, auctionUuid);
            pstmt.setString(2, sellerUuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                this.uuid = rs.getString(1);
                this.origin = rs.getString(2);
                this.seller_uuid = rs.getString(3);
                this.name = rs.getString(4);
                this.description = rs.getString(5);
                this.image_uri = rs.getString(6);
                this.created_timestamp = rs.getString(7);
                this.close_timestamp = rs.getString(8);
                this.currency = rs.getString(9);
                this.reserve_price = rs.getString(10);
                this.bids = rs.getString(11);
                this.current_price = rs.getString(12);
                this.approved = rs.getString(13);
                this.status = rs.getString(14);
                // Calculate duration (number of days from created to close timestamp).
                try {
                    Date dateEnd = ApplicationListener.getSimpleDateFormat().parse(this.close_timestamp);
                    Date dateStart = ApplicationListener.getSimpleDateFormat().parse(this.created_timestamp);
                    long diff = dateEnd.getTime() - dateStart.getTime();
                    this.duration = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                }
                catch (ParseException e) {
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


    public String getUuid() {
        return uuid;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getOrigin() {
        return origin;
    }


    public void setOrigin(String origin) {
        this.origin = origin;
    }


    public String getSellerUuid() {
        return seller_uuid;
    }


    public void setSellerUuid(String seller_uuid) {
        this.seller_uuid = seller_uuid;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getImageUri() {
        return image_uri;
    }


    public void setImageUri(String image_uri) {
        this.image_uri = image_uri;
    }


    public String getCreatedTimestamp() {
        return created_timestamp;
    }


    public void setCreatedTimestamp(String created_timestamp) {
        this.created_timestamp = created_timestamp;
    }


    public String getCloseTimestamp() {
        return close_timestamp;
    }


    public void setCloseTimestamp(String close_timestamp) {
        this.close_timestamp = close_timestamp;
    }


    public String getCurrency() {
        return currency;
    }


    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public String getReservePrice() {
        return reserve_price;
    }


    public void setReservePrice(String reserve_price) {
        this.reserve_price = reserve_price;
    }


    public String getBids() {
        return bids;
    }


    public void setBids(String bids) {
        this.bids = bids;
    }


    public String getCurrentPrice() {
        return current_price;
    }


    public void setCurrentPrice(String current_price) {
        this.current_price = current_price;
    }


    public String getApproved() {
        return approved;
    }


    public void setApproved(String approved) {
        this.approved = approved;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getDuration() {
        return duration;
    }


    public void setDuration(String duration) {
        this.duration = duration;
    }
}
