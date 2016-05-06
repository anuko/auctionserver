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
import java.util.Date;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import listeners.ApplicationListener;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DatabaseManager;
import utils.I18n;
import utils.NotificationManager;
import utils.Site;
import utils.UserManager;


/**
 * Processes a password reset request.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "PasswordResetServlet", urlPatterns = {"/password_reset"})
public class PasswordResetServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(PasswordResetServlet.class);


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

        // Remove previous page error and message.
        session.removeAttribute("password_reset_error");
        session.removeAttribute("password_reset_message");

        // Collect parameters.
        String login = request.getParameter("login");

        // Validate parameters.
        if (login == null || login.equals("")) {
            session.setAttribute("password_reset_error", I18n.get("error.empty", I18n.get("label.login")));
            response.sendRedirect("password_reset.jsp");
            return;
        }
        // Finished validating user input.

        String user_uuid = UserManager.getUserByLogin(login);
        if (user_uuid == null) {

            // Could not find by login. See if the string looks like email.
            if (!EmailValidator.getInstance().isValid(login)) {
                session.setAttribute("password_reset_error", I18n.get("error.user_not_found"));
                response.sendRedirect("password_reset.jsp");
                return;
            }

            // If we are here, we have a valid email.
            int userCount = UserManager.countUsers(login);
            if (userCount == 0) {
                session.setAttribute("password_reset_error", I18n.get("error.user_not_found"));
                response.sendRedirect("password_reset.jsp");
                return;
            }
            if (userCount > 1) {
                session.setAttribute("password_reset_error", I18n.get("error.multiple_logins", login));
                response.sendRedirect("password_reset.jsp");
                return;
            }

            // If we are here, only a single login exists for the provided email.
            user_uuid = UserManager.getUserByEmail(login);
        }

        // User UUID found, do our things.

        // Prepare data for insertion.
        UUID random = UUID.randomUUID();
        Date now = new Date();
        String created_timestamp = ApplicationListener.getSimpleDateFormat().format(now);
        String reference = random.toString(); // To create random URL for confirmation.

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Insert user record.
            conn = DatabaseManager.getConnection();

            // Insert reference for user into as_tmp_refs table.
            pstmt = conn.prepareStatement("insert into as_tmp_refs " +
                "set uuid = ?, user_uuid = ?,  created_timestamp = ?");
            pstmt.setString(1, reference);
            pstmt.setString(2, user_uuid);
            pstmt.setString(3, created_timestamp);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        // If we are here, we successfully created a new reference.

        // Send a notification to user.
        String email = UserManager.getUserEmail(user_uuid);
        String uri = Site.getUri() + "/password_change.jsp?ref=" + reference;
        NotificationManager.notifyUserResetPassword(email, uri);

        // Everything is good. Set registration_successful attribute and redirect back to the view.
        session.setAttribute("password_reset_message", I18n.get("message.password_reset_request_sent"));
        response.sendRedirect("password_reset.jsp");
    }
}
