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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.validator.routines.EmailValidator;

import listeners.ApplicationListener;
import beans.UserBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import utils.DatabaseManager;
import utils.UserManager;
import utils.I18n;
import utils.NotificationManager;
import utils.Site;


/**
 * Processes registration request.
 *
 * @author Nik Okuntseff
 */
public class RegistrationServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(RegistrationServlet.class);


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

        // Do nothing if we don't have UserBean. It must be set in the view.
        HttpSession session = request.getSession();
        UserBean bean = (UserBean) session.getAttribute("register_bean");
        if (bean == null) {
            Log.error("UserBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error and success.
        session.removeAttribute("register_error");
        session.removeAttribute("registration_successful");

        // Collect parameters.
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String confirm_password = request.getParameter("confirm_password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        // Set parameters in session for reuse in the view.
        bean.setLogin(login);
        bean.setPassword(password);
        bean.setConfirmPassword(confirm_password);
        bean.setName(name);
        bean.setEmail(email);

        // Validate parameters.
        if (login == null || login.equals("")) {
            session.setAttribute("register_error", I18n.get("error.empty", I18n.get("label.login")));
            response.sendRedirect("register.jsp");
            return;
        }
        if (password == null || password.equals("")) {
            session.setAttribute("register_error", I18n.get("error.empty", I18n.get("label.password")));
            response.sendRedirect("register.jsp");
            return;
        }
        if (!password.equals(confirm_password)) {
            session.setAttribute("register_error", I18n.get("error.not_equal", I18n.get("label.password"), I18n.get("label.confirm_password")));
            response.sendRedirect("register.jsp");
            return;
        }
        if (name == null || name.equals("")) {
            session.setAttribute("register_error", I18n.get("error.empty", I18n.get("label.name")));
            response.sendRedirect("register.jsp");
            return;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            session.setAttribute("register_error", I18n.get("error.field", I18n.get("label.email")));
            response.sendRedirect("register.jsp");
            return;
        }
        // Finished validating user input.

        if (UserManager.getUserByLogin(login) != null) {
            session.setAttribute("register_error", I18n.get("error.user_exists"));
            response.sendRedirect("register.jsp");
            return;
        }

        // Prepare data for insertion.
        UUID user_uuid = UUID.randomUUID();
        UUID random = UUID.randomUUID();
        Date now = new Date();
        String created_timestamp = ApplicationListener.getSimpleDateFormat().format(now);
        String userUuid = user_uuid.toString();
        String reference = random.toString(); // To create random URL for confirmation.

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Insert user record.
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("insert into as_users " +
                "set uuid = ?, login = ?,  password = md5(?), " +
                "name = ?, email = ?, status = 1");
            pstmt.setString(1, userUuid);
            pstmt.setString(2, login);
            pstmt.setString(3, password);
            pstmt.setString(4, name);
            pstmt.setString(5, email.toLowerCase(I18n.getLocale()));
            pstmt.executeUpdate();

            // Insert reference for user into as_tmp_refs table.
            pstmt = conn.prepareStatement("insert into as_tmp_refs " +
                "set uuid = ?, user_uuid = ?,  created_timestamp = ?");
            pstmt.setString(1, reference);
            pstmt.setString(2, userUuid);
            pstmt.setString(3, created_timestamp);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
        // If we are here, we successfully created a new user record and a reference.

        // Send a notification to user.
        String uri = Site.getUri() + "/reg_confirm?ref=" + reference;
        NotificationManager.notifyRegisteredUser(email, uri);

        // Remove the bean, which is used to pass form data between the view (register.jsp)
        // and the controller (RegistrationServlet). We no longer need it as we are done.
        session.removeAttribute("register_bean");

        // Everything is good. Set registration_successful attribute and redirect to sucess page.
        session.setAttribute("success_message", I18n.get("message.registration_successful"));
        response.sendRedirect("success.jsp");
    }
}
