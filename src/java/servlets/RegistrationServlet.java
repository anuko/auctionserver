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
import business.UserHelper;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.validator.routines.EmailValidator;
import utils.Authenticator;

import utils.I18n;

/**
 *
 * @author nik
 */
public class RegistrationServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(RegistrationServlet.class);
    private static final I18n i18n = ApplicationListener.getI18n();
    private static final Authenticator auth = ApplicationListener.getAuthenticator();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RegistrationServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegistrationServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        processRequest(request, response);
    }

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

        // Remove previous error from the session.
        HttpSession session = request.getSession();
        session.removeAttribute("error");

        // Collect parameters.
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String confirm_password = request.getParameter("confirm_password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        // Set parameters in session for reuse in the view.
        session.setAttribute("user_login", login);
        session.setAttribute("user_password", password);
        session.setAttribute("user_confirm_password", confirm_password);
        session.setAttribute("user_name", name);
        session.setAttribute("user_email", email);

        // Validate parameters.
        if (login == null || login.equals("")) {
            session.setAttribute("error", i18n.get("error.empty", i18n.get("register.label.login")));
            response.sendRedirect("register.jsp");
            return;
        }
        if (password == null || password.equals("")) {
            session.setAttribute("error", i18n.get("error.empty", i18n.get("register.label.password")));
            response.sendRedirect("register.jsp");
            return;
        }
        if (!password.equals(confirm_password)) {
            session.setAttribute("error", i18n.get("error.not_equal", i18n.get("register.label.password"), i18n.get("register.label.confirm_password")));
            response.sendRedirect("register.jsp");
            return;
        }
        if (name == null || name.equals("")) {
            session.setAttribute("error", i18n.get("error.empty", i18n.get("register.label.name")));
            response.sendRedirect("register.jsp");
            return;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            session.setAttribute("error", i18n.get("error.field", i18n.get("register.label.email")));
            response.sendRedirect("register.jsp");
            return;
        }
        // Finished validating user input.

        if (UserHelper.getUserByLogin(login) != null) {
            session.setAttribute("error", i18n.get("error.user_exists"));
            response.sendRedirect("register.jsp");
            return;
        }

        // Insert user record.
        if (!UserHelper.insert(login, password, name, email)) {
            session.setAttribute("error", i18n.get("error.db"));
            response.sendRedirect("register.jsp");
            return;
        }

        // If we are here, we successfully created a new user record.

        // Remove no longer needed attributes.
        session.removeAttribute("user_password");
        session.removeAttribute("user_confirm_password");

        if (auth.doLogin(login, password, session)) {
            // TODO: need a better redirect.
            response.sendRedirect("auctions.jsp");
            return;
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
