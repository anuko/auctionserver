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
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import business.User;
import business.UserHelper;
import javax.servlet.http.Cookie;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Authenticator;
import utils.CookieManager;
import utils.I18n;


/**
 * Processes a profile edit request.
 *
 * @author Nik Okuntseff
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(ProfileServlet.class);
    private static final I18n i18n = ApplicationListener.getI18n();
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

        // Remove previous error from the session.
        session.removeAttribute("error");

        // Collect parameters.
        String login = request.getParameter("login");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        String full_name = request.getParameter("full_name");
        String email = request.getParameter("email");

        // Set parameters in session for reuse in the view.
        session.setAttribute("user_login", login);
        session.setAttribute("user_password1", password1);
        session.setAttribute("user_password2", password2);
        session.setAttribute("user_name", full_name);
        session.setAttribute("user_email", email);

        // Validate parameters.
        if (login == null || login.equals("")) {
            session.setAttribute("error", i18n.get("error.empty", i18n.get("register.label.login")));
            response.sendRedirect("profile.jsp");
            return;
        }
        // New login must be unique.
        if (!login.equals(user.getLogin()) && UserHelper.getUserByLogin(login) != null) {
            session.setAttribute("error", i18n.get("error.user_exists"));
            response.sendRedirect("profile.jsp");
            return;
        }
        if (password1 == null || password1.equals("")) {
            session.setAttribute("error", i18n.get("error.empty", i18n.get("register.label.password")));
            response.sendRedirect("profile.jsp");
            return;
        }
        if (!password1.equals(password2)) {
            session.setAttribute("error", i18n.get("error.not_equal", i18n.get("register.label.password"), i18n.get("register.label.confirm_password")));
            response.sendRedirect("profile.jsp");
            return;
        }
        if (full_name == null || full_name.equals("")) {
            session.setAttribute("error", i18n.get("error.empty", i18n.get("register.label.full_name")));
            response.sendRedirect("profile.jsp");
            return;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            session.setAttribute("error", i18n.get("error.field", i18n.get("register.label.email")));
            response.sendRedirect("profile.jsp");
            return;
        }
        // Finished validating user input.

        // Update user record.
        if (!UserHelper.update(user.getUuid(), login, password1, full_name, email)) {
            session.setAttribute("error", i18n.get("error.db"));
            response.sendRedirect("profile.jsp");
            return;
        }

        // If we are here, we successfully updated user record.
        if (auth.doLogin(login, password1, session)) {

            // Remember user login in cookie.
            CookieManager.setCookie(request.getServletContext().getInitParameter("loginCookieName"), login,
                Integer.parseInt(request.getServletContext().getInitParameter("loginCookieAge")), request, response);

            // TODO: need a better redirect.
            response.sendRedirect("profile.jsp");
        }
    }
}
