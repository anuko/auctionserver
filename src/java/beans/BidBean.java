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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Holds information about a bid.
 *
 * @author Nik Okuntseff
 */
public class BidBean {

    private static final Logger Log = LoggerFactory.getLogger(BidBean.class);


    private String uuid;
    private String item_uuid;
    private String item_name;
    private String seller_uuid;
    private String currency;
    private String amount;
    private String status;


    public BidBean() {
    }


    public String getUuid() {
        return uuid;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getItemName() {
        return item_name;
    }


    public void setItemName(String name) {
        this.item_name = name;
    }


    public String getItemUuid() {
        return item_uuid;
    }


    public void setItemUuid(String item_uuid) {
        this.item_uuid = item_uuid;
    }


    public String getSellerUuid() {
        return seller_uuid;
    }


    public void setSellerUuid(String seller_uuid) {
        this.seller_uuid = seller_uuid;
    }


    public String getCurrency() {
        return currency;
    }


    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public String getAmount() {
        return amount;
    }


    public void setAmount(String amount) {
        this.amount = amount;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }
}
