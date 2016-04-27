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
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
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


/**
 * Processes an auction edit request.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "AuctionEditServlet", urlPatterns = {"/auction_edit"})
public class AuctionEditServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(AuctionEditServlet.class);


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

        // Remove previous error from the session.
        session.removeAttribute("error");

        // Collect parameters.
        String uuid = request.getParameter("uuid");
        String name = request.getParameter("name");
        String currency = request.getParameter("currency");
        String start = request.getParameter("start");
        String duration = request.getParameter("duration");
        String reservePrice = request.getParameter("reserve_price");
        String image_uri = request.getParameter("image_uri");
        String description = request.getParameter("description");

        // Set parameters in session for reuse in the view.
        session.setAttribute("auction_uuid", uuid);
        session.setAttribute("auction_name", name);
        session.setAttribute("auction_currency", currency);
        session.setAttribute("auction_duration", duration);
        session.setAttribute("auction_reserve_price", reservePrice);
        session.setAttribute("auction_image_uri", image_uri);
        session.setAttribute("auction_description", description);

        // Validate parameters.
        if (name == null || name.equals("")) {
            session.setAttribute("error", I18n.get("error.empty", I18n.get("label.name")));
            response.sendRedirect("auction_edit.jsp");
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
                session.setAttribute("error", I18n.get("error.field", I18n.get("label.reserve_price")));
                response.sendRedirect("auction_edit.jsp");
                return;
            }
        }
        // Finished validating user input.

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

Log.error("NIK DEBUG............ uuid is: " + uuid);
Log.error("NIK DEBUG............ start is: " + start);

        Date startDate = new Date();
        try {
            startDate = ApplicationListener.getSimpleDateFormat().parse(start);
        }
        catch (ParseException e) {
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        int days = Integer.parseInt(duration);
        c.add(Calendar.DAY_OF_YEAR, +days);
        Date close_date = new Date(c.getTimeInMillis());
        String close_timestamp = ApplicationListener.getSimpleDateFormat().format(close_date);
Log.error("NIK DEBUG............ close_timestamp is: " + close_timestamp);
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("update as_auctions " +
                "set name = ?, close_timestamp = ?, currency = ?, reserve_price = ? " +
                "where uuid = ?");
            pstmt.setString(1, name);
            pstmt.setString(2, close_timestamp);
            pstmt.setString(3, currency);
            pstmt.setFloat(4, reserve_price);
            pstmt.setString(5, uuid);

            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        // Everything is good, normal exit by a redirect to my_auctions.jsp page.
        response.sendRedirect("my_auctions.jsp");
    }
}
