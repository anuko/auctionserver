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
 * Holds information about user errors on various pages in the application.
 * The intention is for the User object in session to hold ErrorBean instance,
 * which would remember any errors encountered by user on such pages.
 * The app will only display an error for a page when such error exists.
 *
 * @author Nik Okuntseff
 */
public class ErrorBean {

    private String profile_edit_error;   // Last error on profile.jsp
    private String auction_add_error;    // Last error on auction_add.jsp
    private String auction_edit_error;   // Last error on auction_edit.jsp
    private String auction_delete_error; // Last error on auction_delete.jsp
    private String bid_confirm_error;    // Last error on bid_confirm.jsp
    private String bid_edit_error;       // Last error on bid_edit.jsp

    // Getter and setter functions below.


    public String getProfileEditError() {
        return profile_edit_error;
    }


    public void setProfileEditError(String val) {
        profile_edit_error = val;
    }


    public String getAuctionAddError() {
        return auction_add_error;
    }


    public void setAuctionAddError(String val) {
        auction_add_error = val;
    }


    public String getAuctionEditError() {
        return auction_edit_error;
    }


    public void setAuctionEditError(String val) {
        auction_edit_error = val;
    }


    public String getAuctionDeleteError() {
        return auction_delete_error;
    }


    public void setAuctionDeleteError(String val) {
        auction_delete_error = val;
    }


    public String getBidConfirmError() {
        return bid_confirm_error;
    }


    public void setBidConfirmError(String val) {
        bid_confirm_error = val;
    }


    public String getBidEditError() {
        return bid_edit_error;
    }


    public void setBidEditError(String val) {
        bid_edit_error = val;
    }
}
