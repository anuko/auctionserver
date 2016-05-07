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


package servlets;


import java.io.IOException;
import java.text.DecimalFormatSymbols;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.I18n;
import utils.Site;
import utils.UserManager;
import utils.BidManager;
import utils.AuctionItem;
import utils.NotificationManager;


/**
 * Processes bids from iframes in participating partner websites.
 * Each such frame is a simple one-item presentation with a bid form.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "FrameServlet", urlPatterns = {"/frame"})
public class FrameServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(FrameServlet.class);


    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Remove previous page error.
        session.removeAttribute("frame_error");
        session.removeAttribute("frame_message");

        // Collect parameters.
        String uuid = request.getParameter("uuid");
        String amount = request.getParameter("amount");
        String email = request.getParameter("email");

        // Redirect page in case of error.
        String redirect = "frame.jsp?uuid=" + uuid;

        // Set parameters in session for reuse in the view.
        session.setAttribute("frame_amount", amount);
        session.setAttribute("frame_email", email);

        AuctionItem item = new AuctionItem(uuid);

        // Validate parameters.
        if (amount == null || amount.equals("")) {
            session.setAttribute("frame_error", I18n.get("error.empty", I18n.get("label.your_bid")));
            response.sendRedirect(redirect);
            return;
        }
        float bid = 0.0f;
        try {
            if (amount.startsWith(item.getCurrency())) {
                // Strip currency.
                amount = amount.substring(item.getCurrency().length());
            }
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(I18n.getLocale());
            if (dfs.getDecimalSeparator() == ',') {
                // Replace comma with a dot so that parseFloat below works.
                amount = amount.replace(',','.');
            }
            bid = Float.parseFloat(amount);
        }
        catch (NumberFormatException e) {
            session.setAttribute("frame_error", I18n.get("error.field", I18n.get("label.your_bid")));
            response.sendRedirect(redirect);
            return;
        }
        if (bid < 1.01 * item.getTopBid()) {
            session.setAttribute("frame_error", I18n.get("error.insufficient_bid"));
            response.sendRedirect(redirect);
            return;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            session.setAttribute("frame_error", I18n.get("error.field", I18n.get("label.email")));
            response.sendRedirect(redirect);
            return;
        }
        // Finished validating parameters.

        // Determine number of users with provided email.
        int userCount = UserManager.countUsers(email);
        // TODO: what to do if some of users are unconfirmed?

        // Do things differently depending on how many logins we have.
        if (userCount > 1) {
            // We have multiple registered users for a single email.
            // Display message in the view telling user to login and manage bids on the server.
            String login_uri = Site.getUri() + "/login.jsp";
            String message = I18n.get("message.multiple_logins", email, login_uri);
            session.setAttribute("frame_message", message);
            response.sendRedirect("frame_success.jsp");
            return;
        }
        if (userCount == 1) {
            // Single login found. Create an unconfirmed bid and ask user to confirm it.
            String user_uuid = UserManager.getUserByEmail(email);
            String bid_uuid = BidManager.createUnconfirmedBid(user_uuid, uuid, bid);
            String reference = UserManager.createUnconfirmedBidReference(user_uuid, bid_uuid);

            // Send a notification to user.
            String uri = Site.getUri() + "/frame_bid_confirm.jsp?ref=" + reference;
            NotificationManager.notifyUserConfirmBid(email, item.getName(), uri);

            // Display message.
            String message = I18n.get("message.check_mailbox");
            session.setAttribute("frame_message", message);
            response.sendRedirect("frame_success.jsp");
            return;
        }
        if (userCount == 0) {
            // User does not exist. Create one with an aunconfirmed bid, and explain how to confirm.
            String user_uuid = UserManager.createUnconfirmedUser(email);
            String bid_uuid = BidManager.createUnconfirmedBid(user_uuid, uuid, bid);
            String reference = UserManager.createUnconfirmedBidReference(user_uuid, bid_uuid);

            // Send a notification to user.
            String uri = Site.getUri() + "/welcome.jsp?ref=" + reference;
            NotificationManager.notifyUserConfirmBid(email, item.getName(), uri);

            // Display message.
            String message = I18n.get("message.check_mailbox");
            session.setAttribute("frame_message", message);
            response.sendRedirect("frame_success.jsp");
            return;
        }
    }
}
