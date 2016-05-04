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


import beans.UserBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import listeners.ApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DatabaseManager;
import utils.I18n;
import utils.UUIDUtil;


/**
 * Processes registration confirmation GET requests.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "RegistrationConfirmServlet", urlPatterns = {"/reg_confirm"})
public class RegistrationConfirmServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(RegistrationConfirmServlet.class);


    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        session.removeAttribute("registration_confirmed");

        // Collect parameters.
        String reference = request.getParameter("ref");

        // Do nothing if we don't have a valid reference.
        if (reference == null || reference.equals("") || !UUIDUtil.isUUID(reference)) {
            return;
        }

        // Determine cutoff_timestamp for cleanup call.
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = new Date(c.getTimeInMillis());
        String cutoff_timestamp = ApplicationListener.getSimpleDateFormat().format(yesterday);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String user_uuid = null;
        boolean userConfirmed = false;

        try {
            // Do some cleanup by removing old references from the table.
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("delete from as_tmp_refs " +
                "where created_timestamp < ?");
            pstmt.setString(1, cutoff_timestamp);
            pstmt.executeUpdate();

            // Determine if we have a reference in the table.
            pstmt = conn.prepareStatement("select user_uuid from as_tmp_refs " +
                "where uuid = ?");
            pstmt.setString(1, reference);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                user_uuid = rs.getString("user_uuid");
            }

            if (user_uuid != null) {
                pstmt = conn.prepareStatement("update as_users set confirmed = 1 " +
                    "where uuid = ?");
                pstmt.setString(1, user_uuid);
                int rows = pstmt.executeUpdate();
                userConfirmed = (1 == rows);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        // If we confirme a user, set an attribute to display as a hint on the login page.
        if (userConfirmed) {
            // Everything is good. Set registration_confirmed attribute.
            session.setAttribute("registration_confirmed", I18n.get("message.registration_confirmed"));
        }

        // Redirect to the login page,
        response.sendRedirect("login.jsp");
    }
}
