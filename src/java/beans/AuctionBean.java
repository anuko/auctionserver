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
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlets.ApplicationListener;
import utils.DatabaseManager;


/**
 * Holds information about an auction item being added or edited.
 * An instance of this class is used to pass item data between a view and a controller.
 * All members are strings, as we need to display them in case of invalid input.
 * AuctionBean represents an item only partially, holding only the data required in form.
 *
 * @author Nik Okuntseff
 */
public class AuctionBean {

    private static final Logger Log = LoggerFactory.getLogger(AuctionBean.class);

    private String uuid;
    private String name;
    private String created_timestamp;
    private String duration;
    private String currency;
    private String reserve_price;
    private String image_uri;
    private String description;


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
            pstmt = conn.prepareStatement("select uuid, name, description, image_uri, " +
                "created_timestamp, close_timestamp, currency, reserve_price " +
                "from as_auctions where uuid = ? and seller_uuid = ?");
            pstmt.setString(1, auctionUuid);
            pstmt.setString(2, sellerUuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                this.uuid = rs.getString("uuid");
                this.name = rs.getString("name");
                this.description = rs.getString("description");
                this.image_uri = rs.getString("image_uri");
                this.created_timestamp = rs.getString("created_timestamp");
                this.currency = rs.getString("currency");
                this.reserve_price = rs.getString("reserve_price");
                // Calculate duration (number of days from created to close timestamp).
                try {
                    Date dateEnd = ApplicationListener.getSimpleDateFormat().parse(rs.getString("close_timestamp"));
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


    public void setUuid(String val) {
        uuid = val;
    }


    public String getName() {
        return name;
    }


    public void setName(String val) {
        name = val;
    }


    public String getCreatedTimestamp() {
        return created_timestamp;
    }


    public void setCreatedTimestamp(String val) {
        created_timestamp = val;
    }


    public String getDuration() {
        return duration;
    }


    public void setDuration(String val) {
        duration = val;
    }


    public String getCurrency() {
        return currency;
    }


    public void setCurrency(String val) {
        currency = val;
    }


    public String getReservePrice() {
        return reserve_price;
    }


    public void setReservePrice(String val) {
        reserve_price = val;
    }


    public String getImageUri() {
        return image_uri;
    }


    public void setImageUri(String val) {
        image_uri = val;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String val) {
        description = val;
    }
}
