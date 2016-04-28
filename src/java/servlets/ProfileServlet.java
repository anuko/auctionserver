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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utils.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Authenticator;
import utils.CookieManager;
import utils.DatabaseManager;
import beans.UserBean;
import utils.UserManager;
import utils.I18n;


/**
 * Processes a profile edit request.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(ProfileServlet.class);
    private static final Authenticator auth = ApplicationListener.getAuthenticator();


    /**
     * Processes a profile edit <code>POST</code> request.
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

        // Do nothing if we don't have UserBean. It must be set in the view.
        UserBean bean = (UserBean) session.getAttribute("user_edit_bean");
        if (bean == null) {
            Log.error("UserBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error.
        user.getErrorBean().setProfileEditError(null);

        // Collect parameters.
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String confirm_password = request.getParameter("confirm_password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        // Set parameters in session for reuse in the view.
        bean.setLogin(login);
        bean.setPassword(password);;
        bean.setConfirmPassword(confirm_password);
        bean.setName(name);
        bean.setEmail(email);

        // Validate parameters.
        if (login == null || login.equals("")) {
            user.getErrorBean().setProfileEditError(I18n.get("error.empty", I18n.get("label.login")));
            response.sendRedirect("profile.jsp");
            return;
        }
        // New login must be unique.
        if (!login.equals(user.getLogin()) && UserManager.getUserByLogin(login) != null) {
            user.getErrorBean().setProfileEditError(I18n.get("error.user_exists"));
            response.sendRedirect("profile.jsp");
            return;
        }
        if (password == null || password.equals("")) {
            user.getErrorBean().setProfileEditError(I18n.get("error.empty", I18n.get("label.password")));
            response.sendRedirect("profile.jsp");
            return;
        }
        if (!password.equals(confirm_password)) {
            user.getErrorBean().setProfileEditError(I18n.get("error.not_equal", I18n.get("label.password"), I18n.get("label.confirm_password")));
            response.sendRedirect("profile.jsp");
            return;
        }
        if (name == null || name.equals("")) {
            user.getErrorBean().setProfileEditError(I18n.get("error.empty", I18n.get("label.name")));
            response.sendRedirect("profile.jsp");
            return;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            user.getErrorBean().setProfileEditError(I18n.get("error.field", I18n.get("label.email")));
            response.sendRedirect("profile.jsp");
            return;
        }
        // Finished validating user input.

        // Update user record.
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int updateResult = 0;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("update as_users " +
                "set login = ?, password = md5(?), name = ?, email = ? " +
                "where uuid = ?");
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, email);
            pstmt.setString(5, user.getUuid());
            updateResult = pstmt.executeUpdate();
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        if (1 != updateResult) {
            user.getErrorBean().setProfileEditError(I18n.get("error.db"));
            response.sendRedirect("profile.jsp");
            return;
        }

        // If we are here, we successfully updated user record.

        // Login user.
        if (!auth.doLogin(login, password, session)) {
            user.getErrorBean().setProfileEditError(I18n.get("error.db"));
            response.sendRedirect("profile.jsp");
            return;
        }

        // Remember user login in cookie.
        CookieManager.setCookie(request.getServletContext().getInitParameter("loginCookieName"), login,
            Integer.parseInt(request.getServletContext().getInitParameter("loginCookieAge")), request, response);

        // Remove the bean, which is used to pass form data between the view (profile.jsp)
        // and the controller (ProfileServlet). We no longer need it as we are done.
        session.removeAttribute("user_edit_bean");

        // Everything is good, normal exit by a redirect to my_auctions.jsp page.
        response.sendRedirect("my_auctions.jsp");
    }
}
