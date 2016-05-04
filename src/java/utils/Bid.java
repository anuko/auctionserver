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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Holds information about a bid.
 *
 * @author Nik Okuntseff
 */
public class Bid {

    private static final Logger Log = LoggerFactory.getLogger(Bid.class);


    private String uuid;
    private String item_uuid;
    private String item_name;
    private String currency;
    private float amount;
    private int status;


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


    public float getAmount() {
        return amount;
    }


    public void setAmount(float val) {
        amount = val;
    }


    public int getStatus() {
        return status;
    }


    public void setStatus(int val) {
        status = val;
    }
}
