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

import business.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import utils.AuctionBean;
import utils.DatabaseManager;

import utils.I18n;

/**
 * Processes an auction add request.
 *
 * @author Nik Okuntseff
 */
public class AuctionAddServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(AuctionAddServlet.class);

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

        // Do nothing if we don't have AuctionBean. It must be set in the view.
        AuctionBean bean = (AuctionBean) session.getAttribute("auction_add_bean");
        if (bean == null) {
            Log.error("AuctionBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error.
        user.getErrorBean().setAuctionAddError(null);

        // Collect parameters.
        String name = request.getParameter("name");
        String duration = request.getParameter("duration");
        String currency = request.getParameter("currency");
        String reservePrice = request.getParameter("reserve_price");
        String image_uri = request.getParameter("image_uri");
        String description = request.getParameter("description");

        // Set parameters in session for reuse in the view.
        bean.setName(name);
        bean.setDuration(duration);
        bean.setCurrency(currency);
        bean.setReservePrice(reservePrice);
        bean.setImageUri(image_uri);
        bean.setDescription(description);

        // Validate parameters.
        if (name == null || name.equals("")) {
            user.getErrorBean().setAuctionAddError(I18n.get("error.empty", I18n.get("label.name")));
            response.sendRedirect("auction_add.jsp");
            return;
        }
        float reserve_price = 0.0f;
        if (reservePrice != null && !reservePrice.equals("")) {
            try {
                DecimalFormatSymbols dfs = new DecimalFormatSymbols(ApplicationListener.getI18n().getLocale());
                if (dfs.getDecimalSeparator() == ',') {
                    // Replace comma with a dot so that parseFloat below works.
                    reservePrice = reservePrice.replace(',','.');
                }
                reserve_price = Float.parseFloat(reservePrice);
            }
            catch (NumberFormatException e) {
                user.getErrorBean().setAuctionAddError(I18n.get("error.field", I18n.get("label.reserve_price")));
                response.sendRedirect("auction_add.jsp");
                return;
            }
        }
        // Finished validating user input.

        UUID uuid = UUID.randomUUID();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Date now = new Date();
        String created_timestamp = ApplicationListener.getSimpleDateFormat().format(now);
        Calendar c = Calendar.getInstance();
        int days = Integer.parseInt(duration);
        c.add(Calendar.DAY_OF_YEAR, +days);
        Date close_date = new Date(c.getTimeInMillis());
        String close_timestamp = ApplicationListener.getSimpleDateFormat().format(close_date);

        int insertResult = 0;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("insert into as_auctions " +
                "set uuid = ?, origin = ?, seller_uuid = ?, name = ?, description = ?, " +
                "image_uri = ?, created_timestamp = ?, close_timestamp = ?, currency = ?, " +
                "reserve_price = ?, status = 1");
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, ApplicationListener.getSiteBean().getUuid());
            pstmt.setString(3, user.getUuid());
            pstmt.setString(4, name);
            pstmt.setString(5, description);
            pstmt.setString(6, image_uri);
            pstmt.setString(7, created_timestamp);
            pstmt.setString(8, close_timestamp);
            pstmt.setString(9, currency);
            pstmt.setFloat(10, reserve_price);
            insertResult = pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        if (1 != insertResult) {
           user.getErrorBean().setAuctionAddError(I18n.get("error.db"));
           response.sendRedirect("auction_add.jsp");
           return;
        }

        // Remove the bean, which is used to pass form data between the view (auction_add.jsp)
        // and the controller (AuctionAddServlet). We no longer need it as we are done.
        session.removeAttribute("auction_add_bean");

        // Everything is good, normal exit by a redirect to my_auctions.jsp page.
        response.sendRedirect("my_auctions.jsp");
    }
}
