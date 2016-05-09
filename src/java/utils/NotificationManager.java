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
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Sends email notifications to users about various events.
 *
 * @author Nik Okuntseff
 */
public class NotificationManager {

    private static final Logger Log = LoggerFactory.getLogger(NotificationManager.class);


    /**
     * Notifies site admin about a new auction that needs an approval.
     *
     * @param uuid <code>UUID</code> of a new auction.
     */
    public static void notifyAdminNewAuction(String uuid) {

        // Prepare message body.
        String msg_subject = I18n.get("email.new_auction.subject");
        String msg_body = I18n.get("email.new_auction.body");
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session mailSession = (Session) envCtx.lookup("mail/Session");
            String from = "noreply@anuko.com";

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, I18n.get("title")));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(Site.getEmail(), true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }
/*
        // Update processed flag.
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("update as_items set processed = 1 where uuid = ?");
            pstmt.setString(1, uuid);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }*/
    }


    /**
     * Notifies the seller about a new bid.
     *
     * @param item_uuid <code>UUID</code> of the auction item on which a new bid was placed.
     * @param bid_amount current top bid. 
     */
    public static void notifySellerNewBid(String item_uuid, float bid_amount) {

        AuctionItem item = new AuctionItem(item_uuid);

        // Obtain seller email.
        String sellerEmail = UserManager.getSellerEmail(item_uuid);

        // Prepare message body.
        String msg_subject = I18n.get("email.new_bid.subject");
        String localizedBid = item.getCurrency() + " " + String.format(I18n.getLocale(), "%.2f", item.getTopBid());
        String itemUri = Site.getUri() + "/auction.jsp?uuid=" + item.getUuid();
        String msg_body = I18n.get("email.new_bid.body", item.getName(), itemUri, localizedBid);
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session mailSession = (Session) envCtx.lookup("mail/Session");
            String from = "noreply@anuko.com";

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, I18n.get("title")));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sellerEmail, true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }
    }


    /**
     * Notifies a losing bidder about their bid being lost to a higher bid.
     *
     * @param bid_uuid bid <code>UUID</code>.
     * @param item_uuid <code>UUID</code> of the auction item on which a new bid was placed.
     * @param bid_amount current top bid.
     */
    public static void notifyLosingBidder(String bid_uuid, String item_uuid, float bid_amount) {

        AuctionItem item = new AuctionItem(item_uuid);

        // Is losing bidder the same as winning bidder?
        // TODO: consider checking for it.

        String bidderEmail = UserManager.getBidderEmail(bid_uuid);

        // Prepare message body.
        String msg_subject = I18n.get("email.lost_bid.subject");
        String itemUri = Site.getUri() + "/auction.jsp?uuid=" + item.getUuid();
        String localizedBid = item.getCurrency() + " " + String.format(I18n.getLocale(), "%.2f", item.getTopBid());
        String msg_body = I18n.get("email.lost_bid.body", item.getName(), localizedBid, item.getCloseTimestamp(), itemUri);
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session mailSession = (Session) envCtx.lookup("mail/Session");
            String from = "noreply@anuko.com";

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, I18n.get("title")));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(bidderEmail, true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }

        // Set bid status to 0 (0 means "lost bid").
        BidManager.updateStatus(bid_uuid, 0);
    }


    /**
     * Notifies a currently winning bidder about their bid being the highest at the moment.
     *
     * @param bid_uuid bid <code>UUID</code>.
     * @param item_uuid <code>UUID</code> of the auction item on which a new bid was placed.
     * @param bid_amount current top bid.
     */
    public static void notifyCurrentTopBidder(String bid_uuid, String item_uuid, float bid_amount) {

        AuctionItem item = new AuctionItem(item_uuid);
        String bidderEmail = UserManager.getBidderEmail(bid_uuid);

       // Prepare message body.
        String msg_subject = I18n.get("email.top_bid.subject");
        String itemUri = Site.getUri() + "/auction.jsp?uuid=" + item.getUuid();
        String localizedBid = item.getCurrency() + " " + String.format(I18n.getLocale(), "%.2f", item.getTopBid());
        String msg_body = I18n.get("email.top_bid.body", item.getName(), itemUri, localizedBid);
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session mailSession = (Session) envCtx.lookup("mail/Session");
            String from = "noreply@anuko.com";

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, I18n.get("title")));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(bidderEmail, true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }

        // Set bid status to 1 (1 means "top, potentially winning bid").
        BidManager.updateStatus(bid_uuid, 1);
    }


    /**
     * Notifies the seller about a completed auction.
     *
     * @param item_uuid <code>UUID</code> of the auction, which is closed
     */
    public static void notifySellerAuctionClose(String item_uuid) {

        // Obtain seller email.
        String sellerEmail = UserManager.getSellerEmail(item_uuid);

        // Obtain item details.
        AuctionItem item = new AuctionItem(item_uuid);
        boolean sold = (item.getTopBid() > item.getReservePrice());

        // Send a notification to seller.
        String msg_subject = null;
        String msg_body = null;
        if (sold) {
            msg_subject = I18n.get("email.item_sold.subject");
            String localizedBid = item.getCurrency() + " " + String.format(I18n.getLocale(), "%.2f", item.getTopBid());
            msg_body = I18n.get("email.item_sold.body", item.getName(), localizedBid);
        } else {
            msg_subject = I18n.get("email.item_not_sold.subject");
            msg_body = I18n.get("email.item_not_sold.body", item.getName());
        }
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session mailSession = (Session) envCtx.lookup("mail/Session");
            String from = "noreply@anuko.com";

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, I18n.get("title")));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sellerEmail, true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }
    }


    /**
     * Notifies the bidder about a completed auction.
     *
     * @param auction_uuid <code>UUID</code> of the auction, which is closed.
     */
    public static void notifyBidderAuctionClose(String item_uuid) {

        // Obtain item details.
        AuctionItem item = new AuctionItem(item_uuid);

        String bid_uuid = item.getTopBidUuid();
        if (bid_uuid == null) return; // Nothing to do.

        boolean sold = (item.getTopBid() > item.getReservePrice());

        // Obtain bidder email.
        String bidderEmail = UserManager.getBidderEmail(bid_uuid);

        // Prepare message body.
        String msg_subject;
        String msg_body;
        String localizedBid = item.getCurrency() + " " + String.format(I18n.getLocale(), "%.2f", item.getTopBid());
        if (sold) {
            msg_subject = I18n.get("email.item_won.subject");
            msg_body = I18n.get("email.item_won.body", item.getName(), localizedBid);
        } else {
            msg_subject = I18n.get("email.reserve_not_met.subject");
            msg_body = I18n.get("email.reserve_not_met.body", item.getName(), localizedBid);
        }

        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session mailSession = (Session) envCtx.lookup("mail/Session");
            String from = "noreply@anuko.com";

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, I18n.get("title")));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(bidderEmail, true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }
    }

    /**
     * Notifies newly registered user to confirm their registration.
     *
     * @param email user email.
     * @param uri random uri for user to click on.
     */
    public static void notifyRegisteredUser(String email, String uri) {

        // Prepare message body.
        String msg_subject = I18n.get("email.registration_successful.subject");
        String msg_body = I18n.get("email.registration_successful.body", uri);

        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session mailSession = (Session) envCtx.lookup("mail/Session");
            String from = "noreply@anuko.com";

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, I18n.get("title")));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }
    }


    /**
     * Notifies user how to reset their password.
     *
     * @param email user email.
     * @param uri random uri for user to click on.
     */
    public static void notifyUserResetPassword(String email, String uri) {

        // Prepare message body.
        String msg_subject = I18n.get("email.reset_password.subject");
        String msg_body = I18n.get("email.reset_password.body", uri);

        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session mailSession = (Session) envCtx.lookup("mail/Session");
            String from = "noreply@anuko.com";

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, I18n.get("title")));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }
    }


    /**
     * Notifies user to confirm their bid.
     *
     * @param email user email.
     * @param uri random uri for user to click on.
     */
    public static void notifyUserConfirmBid(String email, String item_name, String uri) {

        // Prepare message body.
        String msg_subject = I18n.get("email.confirm_bid.subject");
        String msg_body = I18n.get("email.confirm_bid.body", item_name, uri);

        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session mailSession = (Session) envCtx.lookup("mail/Session");
            String from = "noreply@anuko.com";

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, I18n.get("title")));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }
    }

    /**
     * Notifies user to confirm their bid.
     *
     * @param email user email.
     * @param uri random uri for user to click on.
     */
    public static void notifyBidderClosingReminder(String itemUuid, String email) {

        AuctionItem item = new AuctionItem(itemUuid);

        // Prepare message body.
        String msg_subject = I18n.get("email.closing_reminder.subject");
        String itemUri = Site.getUri() + "/auction.jsp?uuid=" + item.getUuid();
        String localizedBid = item.getCurrency() + " " + String.format(I18n.getLocale(), "%.2f", item.getTopBid());
        String msg_body = I18n.get("email.closing_reminder.body", item.getName(), localizedBid, itemUri);

        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            Session mailSession = (Session) envCtx.lookup("mail/Session");
            String from = "noreply@anuko.com";

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from, I18n.get("title")));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }
    }
}
