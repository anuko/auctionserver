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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.DatabaseManager;
import utils.I18n;
import utils.User;
import beans.FrameBidConfirmBean;


/**
 * Processes a frame bid confirm request.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "FrameBidConfirmServlet", urlPatterns = {"/frame_bid_confirm"})
public class FrameBidConfirmServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(FrameBidConfirmServlet.class);

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

        // Do nothing if we don't have FrameBidConfirmBean. It must be set in the view.
        HttpSession session = request.getSession();
        FrameBidConfirmBean bean = (FrameBidConfirmBean) session.getAttribute("frame_bid_confirm_bean");
        if (bean == null) {
            Log.error("FrameBidConfirmBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error.
        session.removeAttribute("frame_bid_confirm_error");

        // Collect parameters.
        String uuid = request.getParameter("uuid");
        String amount = request.getParameter("amount");

        // Set parameters in session for reuse in the view.
        bean.setAmount(amount);

        // Validate parameters.
        if (amount == null || amount.equals("")) {
            session.setAttribute("user_confirm_error", I18n.get("error.empty", I18n.get("label.your_bid")));
            response.sendRedirect("frame_bid_confirm.jsp");
            return;
        }
        float bid = 0.0f;
        try {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(I18n.getLocale());
            if (dfs.getDecimalSeparator() == ',') {
                // Replace comma with a dot so that parseFloat below works.
                amount = amount.replace(',','.');
            }
            bid = Float.parseFloat(amount);
        }
        catch (NumberFormatException e) {
            session.setAttribute("user_confirm_error", I18n.get("error.field", I18n.get("label.your_bid")));
            response.sendRedirect("bid_confirm.jsp");
            return;
        }
        float currentBid = bean.getCurrentBid();
        if (bid < 1.01 * currentBid) {
            session.setAttribute("user_confirm_error", I18n.get("error.insufficient_bid"));
            response.sendRedirect("bid_confirm.jsp");
            return;
        }
        // Finished validating user input.

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("update as_bids " +
                "set amount = ?, confirmed = 1, processed = 0 " +
                "where uuid = ?");
            pstmt.setFloat(1, bid);
            pstmt.setString(2, bean.getUuid());
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        // Automatically login user (without password).
        session.removeAttribute("user");
        User user = new User(bean.getUuid());
        session.setAttribute("user", user);

        // Remove the bean, which is used to pass form data between the view (bid_confirm.jsp)
        // and the controller (BidConfirmServlet). We no longer need it as we are done.
        session.removeAttribute("frame_bid_confirm_bean");

        // Everything is good, redirect to success page with a message.
        session.setAttribute("success_message", I18n.get("message.frame_bid_confirmed"));
        response.sendRedirect("success.jsp");
    }
}
