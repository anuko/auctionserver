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

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import listeners.ApplicationListener;
import utils.I18n;
import utils.Authenticator;
import utils.CookieManager;


/**
 * Processes POSTs from login.jsp. Upon successful login it sets an
 * initialized User object in session.
 */
public class LoginServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(LoginServlet.class);
    private static final Authenticator auth = ApplicationListener.getAuthenticator();

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

        // We use MVC pattern. This servlet is a controller.
        // LoginServlet only processes POSTs from login.jsp.
        // Get requests should go directly to the login form.
        response.sendRedirect("login.jsp");
    }

    /**
     * Handles the HTTP <code>POST</code> that comes from login.jsp with
     * user login and password. This is our login processor. If login and
     * password match, we initialize and set a User object in session, and
     * redirect to lists.jsp view (checklist selector for user).
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
        session.removeAttribute("user");

        // Remove previous page error.
        session.removeAttribute("login_error");

        // Collect parameters.
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        // Set parameters in session for reuse in the view.
        session.setAttribute("login_login", login);
        session.setAttribute("login_password", password);

        // Try to login user.
        if (!auth.doLogin(login, password, session)) {
            session.setAttribute("login_error", I18n.get("error.auth"));
            response.sendRedirect("login.jsp");
            return;
        }

        // Remember user login in cookie.
        CookieManager.setCookie(request.getServletContext().getInitParameter("loginCookieName"), login,
            Integer.parseInt(request.getServletContext().getInitParameter("loginCookieAge")), request, response);

        // Remove no lomnger needed session attributes, as we are done.
        session.removeAttribute("login_login");
        session.removeAttribute("login_password");

        // TODO: decide where to redirect better.
        response.sendRedirect("auctions.jsp");
    }
}
