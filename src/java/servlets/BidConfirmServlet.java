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

import beans.AuctionBean;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
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
import beans.BidBean;
import java.util.UUID;


/**
 * Processes a bid confirm request.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "BidConfirmServlet", urlPatterns = {"/bid_confirm"})
public class BidConfirmServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(BidConfirmServlet.class);


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

        // Do nothing if we don't have a logged in user.
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            Log.error("User object is null. We are not supposed to get here.");
            return;
        }

        // Do nothing if we don't have BidBean. It must be set in the view.
        BidBean bean = (BidBean) session.getAttribute("bid_confirm_bean");
        if (bean == null) {
            Log.error("BidBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error.
        user.getErrorBean().setBidConfirmError(null);

        // Collect parameters.
        String uuid = request.getParameter("uuid");
        String amount = request.getParameter("amount");

        // Set parameters in session for reuse in the view.
        bean.setAmount(amount);

        // Validate parameters.
        if (amount == null || amount.equals("")) {
            user.getErrorBean().setBidConfirmError(I18n.get("error.empty", I18n.get("label.your_bid")));
            response.sendRedirect("bid_confirm.jsp");
            return;
        }
        float bid = 0.0f;
        try {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(ApplicationListener.getI18n().getLocale());
            if (dfs.getDecimalSeparator() == ',') {
                // Replace comma with a dot so that parseFloat below works.
                amount = amount.replace(',','.');
            }
            bid = Float.parseFloat(amount);
        }
        catch (NumberFormatException e) {
            user.getErrorBean().setBidConfirmError(I18n.get("error.field", I18n.get("label.your_bid")));
            response.sendRedirect("bid_confirm.jsp");
            return;
        }
        if (bean.getSellerUuid().equals(user.getUuid())) {
            user.getErrorBean().setBidConfirmError(I18n.get("error.own_item"));
            response.sendRedirect("bid_confirm.jsp");
            return;
        }
        // Finished validating user input.

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Date now = new Date();
        String created_timestamp = ApplicationListener.getSimpleDateFormat().format(now);

        int insertResult = 0;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("insert into as_bids " +
                "set uuid = ?, origin = ?, item_uuid = ?, max_price = ?, " +
                "user_uuid = ?, created_timestamp = ?");
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, ApplicationListener.getSiteBean().getUuid());
            pstmt.setString(3, bean.getItemUuid());
            pstmt.setFloat(4, bid);
            pstmt.setString(5, user.getUuid());
            pstmt.setString(6, created_timestamp);
            insertResult = pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        if (1 != insertResult) {
           user.getErrorBean().setBidConfirmError(I18n.get("error.db"));
           response.sendRedirect("bid_confirm.jsp");
           return;
        }

        // Remove the bean, which is used to pass form data between the view (bid_confirm.jsp)
        // and the controller (BidConfirmServlet). We no longer need it as we are done.
        session.removeAttribute("bid_confirm_bean");

        // Everything is good, normal exit by a redirect to my_auctions.jsp page.
        response.sendRedirect("my_bids.jsp");
    }
}
