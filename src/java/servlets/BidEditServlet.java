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


import beans.BidBean;
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


/**
 * Processes a bid edit request.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "BidEditServlet", urlPatterns = {"/bid_edit"})
public class BidEditServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(BidEditServlet.class);



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
Log.error("NIK DEBUG, we are in doPost of BidEditServlet...");

        // Do nothing if we don't have a logged in user.
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            Log.error("User object is null. We are not supposed to get here.");
            return;
        }

        // Do nothing if we don't have BidBean. It must be set in the view.
        BidBean bean = (BidBean) session.getAttribute("bid_edit_bean");
        if (bean == null) {
            Log.error("BidBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error.
        user.getErrorBean().setBidEditError(null);

        // Collect parameters.
        String amount = request.getParameter("amount");

        // Set parameters in session for reuse in the view.
        bean.setAmount(amount);

        // Validate parameters.
        if (amount == null || amount.equals("")) {
            user.getErrorBean().setBidConfirmError(I18n.get("error.empty", I18n.get("label.your_bid")));
            response.sendRedirect("bid_edit.jsp");
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
            user.getErrorBean().setBidConfirmError(I18n.get("error.field", I18n.get("label.your_bid")));
            response.sendRedirect("bid_edit.jsp");
            return;
        }
        float currentBid = bean.getCurrentBid();
        if (bid < 1.01 * currentBid) {
            user.getErrorBean().setBidConfirmError(I18n.get("error.insufficient_bid"));
            response.sendRedirect("bid_edit.jsp");
            return;
        }
        if (bean.getSellerUuid().equals(user.getUuid())) {
            user.getErrorBean().setBidConfirmError(I18n.get("error.own_item"));
            response.sendRedirect("bid_edit.jsp");
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

        // Remove the bean, which is used to pass form data between the view (bid_edit.jsp)
        // and the controller (BidEditServlet). We no longer need it as we are done.
        session.removeAttribute("bid_edit_bean");

        // Everything is good, normal exit by a redirect to my_unconfirmed_bids.jsp page.
        response.sendRedirect("my_unconfirmed_bids.jsp");
    }
}
