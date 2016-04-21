package servlets;

import business.User;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.I18n;
// import utils.DatabaseManager;
/**
 * Processes POSTs from login.jsp. Upon successful login it sets an
 * initialized User object in session.
 */
public class LoginServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(LoginServlet.class);
    private static final I18n i18n = ApplicationListener.getI18n();

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

        // Try to login user.
        User user = new User(login, password);
        if (user.getUuid() != null) {
            // Login successful.

            // Put a cookie with login name in response. This updates the existing cookie expiration date.
            ServletContext context = this.getServletContext();
            Cookie trackingCookie = new Cookie(context.getInitParameter("loginCookieName"), login);
            int age = Integer.parseInt(context.getInitParameter("loginCookieAge"));
            trackingCookie.setMaxAge(age);
            trackingCookie.setPath("/");
            response.addCookie(trackingCookie);

            // Initialize user and redirect to lists.jsp view.
            session.setAttribute("user", user);
            response.sendRedirect("auctions.jsp");
        } else {
            // Login failed. Set error message and redirect to the login.jsp view.
            String error = i18n.get("error.auth");
            session.setAttribute("login", login); // To pass to login.jsp to fill the login field (instead of info from cookie).
            session.setAttribute("error", error);
            response.sendRedirect("login.jsp");
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
    }
}
