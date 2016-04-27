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

import utils.Authenticator;
import beans.UserBean;
import business.UserHelper;
import utils.I18n;

/**
 *
 * @author nik
 */
public class RegistrationServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(RegistrationServlet.class);
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

        // Do nothing if we don't have UserBean. It must be set in the view.
        HttpSession session = request.getSession();
        UserBean bean = (UserBean) session.getAttribute("register_bean");
        if (bean == null) {
            Log.error("UserBean object is null. We are not supposed to get here.");
            return;
        }

        // Remove previous page error.
        session.removeAttribute("register_error");

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

        if (UserHelper.getUserByLogin(login) != null) {
            session.setAttribute("register_error", I18n.get("error.user_exists"));
            response.sendRedirect("register.jsp");
            return;
        }

        // Insert user record.
        if (!UserHelper.insert(login, password, name, email)) {
            session.setAttribute("register_error", I18n.get("error.db"));
            response.sendRedirect("register.jsp");
            return;
        }

        // If we are here, we successfully created a new user record.

        // Login a new user.
        if (!auth.doLogin(login, password, session)) {
            session.setAttribute("register_error", I18n.get("error.db"));
            return;
        }

        // Remove the bean, which is used to pass form data between the view (register.jsp)
        // and the controller (RegistrationServlet). We no longer need it as we are done.
        session.removeAttribute("register_bean");

        // Everything is good, normal exit by a redirect to my_auctions.jsp page.
        response.sendRedirect("my_auctions.jsp");
    }
}
