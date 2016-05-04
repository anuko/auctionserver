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


/**
 * Holds information about a bid being added by user.
 * An instance of this class is used to pass bid data between a view and a controller.
 * All members are strings, as we need to display them in case of invalid input.
 *
 * @author Nik Okuntseff
 */
public class BidBean {

    private String uuid;         // UUID for this bid.
    private String item_uuid;    // UUID of the item using is bidding on.
    private String seller_uuid;  // Needed to prohibit bidding on own items.
    private String item_name;
    private String currency;
    private float current_bid;   // Current top bid on the item. Float because we never display it.
    private String amount;       // Amount of this bid, must be greater then current bid.


    public BidBean() {
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
