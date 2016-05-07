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
import beans.UserConfirmBean;
import utils.Authenticator;


/**
 * Processes user confirmation requests.
 * These come during a bid confirmation process from participating websites.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "UserConfirmServlet", urlPatterns = {"/user_confirm"})
public class WelcomeServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(WelcomeServlet.class);
    private static final Authenticator auth = ApplicationListener.getAuthenticator();


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

        // Do nothing if we don't have UserConfirmBean. It must be set in the view.
        UserConfirmBean bean = (UserConfirmBean) session.getAttribute("user_confirm_bean");
        if (bean == null) {
            Log.error("UserConfirmBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error.
        session.removeAttribute("welcome_error");

        // Collect parameters.
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String confirm_password = request.getParameter("confirm_password");
        String name = request.getParameter("name");

        // Set parameters in session for reuse in the view.
        bean.setLogin(login);
        bean.setPassword(password);
        bean.setConfirmPassword(confirm_password);
        bean.setName(name);

        // Validate user input.
        if (!UUIDUtil.isUUID(bean.getRefUuid()) || !UUIDUtil.isUUID(bean.getUserUuid())) {
            session.setAttribute("welcome_error", I18n.get("error.invalid_link"));
            response.sendRedirect("welcome.jsp");
            return;
        }
        if (login == null || login.equals("")) {
            session.setAttribute("welcome_error", I18n.get("error.empty", I18n.get("label.login")));
            response.sendRedirect("welcome.jsp");
            return;
        }
        if (password == null || password.equals("")) {
            session.setAttribute("welcome_error", I18n.get("error.empty", I18n.get("label.password")));
            response.sendRedirect("welcome.jsp");
            return;
        }
        if (!password.equals(confirm_password)) {
            session.setAttribute("welcome_error", I18n.get("error.not_equal", I18n.get("label.password"), I18n.get("label.confirm_password")));
            response.sendRedirect("welcome.jsp");
            return;
        }
        if (name == null || name.equals("") || name.equals(I18n.get("label.user"))) {
            session.setAttribute("welcome_error", I18n.get("error.field", I18n.get("label.name")));
            response.sendRedirect("welcome.jsp");
            return;
        }
        // Finished validating user input.

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean userConfirmed = false;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("update as_users " +
                "set login = ?, password = md5(?), name = ?, confirmed = 1 " +
                "where uuid = ?");
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, bean.getUserUuid());
            int rows = pstmt.executeUpdate();
            userConfirmed = (1 == rows);
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        // Login user.
        if (!auth.doLogin(login, password, session)) {
            session.setAttribute("welcome_error", I18n.get("error.db"));
            response.sendRedirect("welcome.jsp");
            return;
        }

        // Remove the bean as we are done.
        session.removeAttribute("user_confirm_bean");

        // Redirect to the frame bid confirm form.
        response.sendRedirect("frame_bid_confirm.jsp?ref=" + bean.getRefUuid());
    }
}
