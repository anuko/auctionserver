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
import servlets.ApplicationListener;


/**
 * Sends out workflow notifications to users about various events.
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

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ApplicationListener.getSiteBean().getEmail(), true));
            msg.setSubject(msg_subject, "UTF-8");
            msg.setText(msg_body, "UTF-8");
            Transport.send(msg);
        }
        catch (Exception e) {
            // Do nothing, this is not expected.
            System.out.println("Exception when sending notification email... " + e.getMessage());
        }

        // Update auction status in the table to mark it accordingly.
        // Status -1 means "notification to admin was sent".
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
Log.error("notifyAdminNewAuction ... updating status...");
            pstmt = conn.prepareStatement("update as_auctions set status = -1 where uuid = ?");
            pstmt.setString(1, uuid);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }
}
