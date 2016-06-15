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

import beans.FrameBidConfirmBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormatSymbols;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.BidManager;
import utils.DatabaseManager;
import utils.I18n;
import utils.User;
import beans.FrameBidBean;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.NotificationManager;
import utils.Site;
import utils.UserManager;

/**
 * Processes a frame bid creation request.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "BidServlet", urlPatterns = {"/bid"})
public class BidServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(BidServlet.class);

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

        // Do nothing if we don't have a FrameBidBean. It must be set in the view.
        HttpSession session = request.getSession();
        FrameBidBean bean = (FrameBidBean) session.getAttribute("frame_bid_bean");
        if (bean == null) {
            Log.error("FrameBidBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error.
        session.removeAttribute("frame_bid_error");

        // Collect parameters.
        String amount = request.getParameter("amount");
        String bidderEmail = request.getParameter("email");

        // Set parameters in session for reuse in the view.
        bean.setAmount(amount);
        bean.setBidderEmail(bidderEmail);

        // Validate parameters.
        if (amount == null || amount.equals("")) {
            session.setAttribute("frame_bid_error", I18n.get("error.empty", I18n.get("label.your_bid")));
            response.sendRedirect("bid.jsp");
            return;
        }
        float bidAmount = 0.0f;
        try {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(I18n.getLocale());
            if (dfs.getDecimalSeparator() == ',') {
                // Replace comma with a dot so that parseFloat below works.
                amount = amount.replace(',','.');
            }
            bidAmount = Float.parseFloat(amount);
        }
        catch (NumberFormatException e) {
            session.setAttribute("frame_bid_error", I18n.get("error.field", I18n.get("label.your_bid")));
            response.sendRedirect("bid.jsp");
            return;
        }
        float currentBid = bean.getCurrentBid();
        if (bidAmount < 1.01 * currentBid) {
            session.setAttribute("frame_bid_error", I18n.get("error.insufficient_bid"));
            response.sendRedirect("bid.jsp");
            return;
        }
        if (!EmailValidator.getInstance().isValid(bidderEmail)) {
            session.setAttribute("frame_bid_error", I18n.get("error.field", I18n.get("label.email")));
            response.sendRedirect("bid.jsp");
            return;
        }
        // Finished validating user input.
        
        // Determine number of users with provided email.
        int userCount = UserManager.countUsers(bidderEmail);
        // TODO: what to do if some of users are unconfirmed?

        // Do things differently depending on how many logins we have.
        if (userCount > 1) {
            // We have multiple registered users for a single email.
            // There is nothing much we can do, but ask user to login.
            response.sendRedirect("login.jsp");
            return;
        }
        if (userCount == 1) {
            // Single login found. Create an unconfirmed bid and ask user to confirm it.
            String userUuid = UserManager.getUserByEmail(bidderEmail);
            String bidUuid = BidManager.createUnconfirmedBid(userUuid, bean.getItemUuid(), bidAmount);
            String reference = UserManager.createUnconfirmedBidReference(userUuid, bidUuid);

            // Send a notification to user.
            String uri = Site.getUri() + "/frame_bid_confirm.jsp?ref=" + reference;
            NotificationManager.notifyUserConfirmBid(bidderEmail, bean.getItemName(), uri);

            // Display message.
            String message = I18n.get("message.check_mailbox");
            session.setAttribute("success_message", message);
            session.removeAttribute("frame_bid_bean");
            response.sendRedirect("success.jsp");
            return;
        }
        if (userCount == 0) {
            // User does not exist. Create one with an aunconfirmed bid, and explain how to confirm.
            String userUuid = UserManager.createUnconfirmedUser(bidderEmail);
            String bidUuid = BidManager.createUnconfirmedBid(userUuid, bean.getItemUuid(), bidAmount);
            String reference = UserManager.createUnconfirmedBidReference(userUuid, bidUuid);

            // Send a notification to user.
            String uri = Site.getUri() + "/welcome.jsp?ref=" + reference;
            NotificationManager.notifyUserConfirmBid(bidderEmail, bean.getItemName(), uri);

            // Display message.
            String message = I18n.get("message.check_mailbox");
            session.setAttribute("success_message", message);
            session.removeAttribute("frame_bid_bean");
            response.sendRedirect("success.jsp");
            return;
        }
    }
}
