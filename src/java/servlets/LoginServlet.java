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

import business.User;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.I18n;
import utils.Authenticator;


/**
 * Processes POSTs from login.jsp. Upon successful login it sets an
 * initialized User object in session.
 */
public class LoginServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(LoginServlet.class);
    private static final I18n i18n = ApplicationListener.getI18n();
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

        // Collect parameters.
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        HttpSession session = request.getSession();
        session.removeAttribute("user");
        session.removeAttribute("login");
        session.removeAttribute("error");

        if (!auth.doLogin(login, password, session)) {
            session.setAttribute("user_login", login); // To pass to login.jsp to fill the login field (instead of info from cookie).
            session.setAttribute("error", I18n.get("error.auth"));
            response.sendRedirect("login.jsp");
            return;
        }

        // Remember user login in cookie.
        Cookie loginCookie = new Cookie(request.getServletContext().getInitParameter("loginCookieName"), login);
        loginCookie.setMaxAge(Integer.parseInt(request.getServletContext().getInitParameter("loginCookieAge")));
        String server = request.getServerName();
        if (server.startsWith("www."))
            server = server.substring(4);
        loginCookie.setDomain(("." + server));
        loginCookie.setPath("/");
        response.addCookie(loginCookie);

        // TODO: decide where to redirect better.
        response.sendRedirect("profile.jsp");
        return;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
