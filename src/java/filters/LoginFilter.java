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


package filters;

import utils.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Login filter for the application.
 * We use the MVC pattern. Visitor is exposed to views only, which are .jsp files.
 * LoginFilter intercepts traffic to .jsp files and determines if a User object
 * is set in session. This is an indication of successful login. If no such
 * object exists, we redirect to login.jsp.
 *
 * @author Nik Okuntseff
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = {"/profile.jsp"})
public class LoginFilter implements Filter {

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured.
    private FilterConfig filterConfig = null;


    /**
     * Init method for this filter.
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }


    /**
     * Determines if User object exists in session and redirects to login.jsp if not.
     *
     * @param request The servlet request we are processing.
     * @param response The servlet response we are creating.
     * @param chain The filter chain we are processing.
     *
     * @exception IOException if an input/output error occurs.
     * @exception ServletException if a servlet error occurs.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession();
        //session.removeAttribute("error"); // May have been set previously.
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // No User object found in session. Redirect to login.jsp.
            httpResponse.sendRedirect("login.jsp");
        } else {
            // We have a logged in user.

            // Normal processing.
            chain.doFilter(request, response);
        }
    }


    /**
     * Destroy method for this filter.
     */
    public void destroy() {
        filterConfig = null;
    }
}
