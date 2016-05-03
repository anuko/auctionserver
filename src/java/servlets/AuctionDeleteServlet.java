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

import utils.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import beans.AuctionBean;
import utils.DatabaseManager;
import utils.I18n;


/**
 * Processes an auction delete request.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "AuctionDeleteServlet", urlPatterns = {"/auction_delete"})
public class AuctionDeleteServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(AuctionDeleteServlet.class);


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
        AuctionBean bean = (AuctionBean) session.getAttribute("auction_delete_bean");
        if (bean == null) {
            Log.error("AuctionBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error.
        user.getErrorBean().setAuctionDeleteError(null);

        // Collect parameters.
        String uuid = request.getParameter("uuid");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("update as_items " +
                "set status = null " +
                "where uuid = ? and seller_uuid = ?");
            pstmt.setString(1, uuid);
            pstmt.setString(2, user.getUuid());
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        // Remove the bean, which is used to pass form data between the view (auction_delete.jsp)
        // and the controller (AuctionDeleteServlet). We no longer need it as we are done.
        session.removeAttribute("auction_delete_bean");

        // Everything is good, normal exit by a redirect to my_auctions.jsp page.
        response.sendRedirect("my_auctions.jsp");

    }
}
