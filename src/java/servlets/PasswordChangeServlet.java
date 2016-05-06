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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.PasswordBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import listeners.ApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DatabaseManager;
import utils.I18n;
import utils.UUIDUtil;


/**
 * Processes a password change request.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "PasswordChangeServlet", urlPatterns = {"/password_change"})
public class PasswordChangeServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(PasswordChangeServlet.class);


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

        // Do nothing if we don't have a PasswordBean. It must be set in the view.
        HttpSession session = request.getSession();
        PasswordBean bean = (PasswordBean) session.getAttribute("password_change_bean");
        if (bean == null || bean.getUuid() == null || !UUIDUtil.isUUID(bean.getUuid())) {
            Log.error("PasswordBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error.
        session.removeAttribute("password_change_error");

        // Collect parameters.
        String ref_uuid = request.getParameter("uuid");
        String password = request.getParameter("password");
        String confirm_password = request.getParameter("confirm_password");

        // Set parameters in session for reuse in the view.
        bean.setPassword(password);
        bean.setConfirmPassword(confirm_password);

        // Validate parameters.
        if (password == null || password.equals("")) {
            session.setAttribute("register_error", I18n.get("error.empty", I18n.get("label.password")));
            response.sendRedirect("password_change.jsp");
            return;
        }
        if (!password.equals(confirm_password)) {
            session.setAttribute("register_error", I18n.get("error.not_equal", I18n.get("label.password"), I18n.get("label.confirm_password")));
            response.sendRedirect("password_change.jsp");
            return;
        }
        // Finished validating user input.

        // Determine cutoff_timestamp for cleanup call.
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = new Date(c.getTimeInMillis());
        String cutoff_timestamp = ApplicationListener.getSimpleDateFormat().format(yesterday);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String user_uuid = null;
        boolean passwordChanged = false;

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
            pstmt.setString(1, ref_uuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                user_uuid = rs.getString("user_uuid");
            }

            if (user_uuid != null) {
                pstmt = conn.prepareStatement("update as_users set password = md5(?) " +
                    "where uuid = ?");
                pstmt.setString(1, password);
                pstmt.setString(2, user_uuid);
                int rows = pstmt.executeUpdate();
                passwordChanged = (1 == rows);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        // If we confirmed a user, set an attribute to display as a hint on the login page.
        if (passwordChanged) {
            // Everything is good. Set login_hint attribute.
            session.setAttribute("login_hint", I18n.get("message.password_changed"));
        }

        // Redirect to the login page,
        response.sendRedirect("login.jsp");
    }
}
