/* Copyright Anuko International Ltd. (https://www.anuko.com)

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


package tags;

import java.io.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Obtains the value of user login string either from the session or cookie (name defined in web.xml).
 */
public class LoginTag extends TagSupport {

    public int doStartTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpSession session = pageContext.getSession();
        ServletContext context = pageContext.getServletContext();

        String login = (String) session.getAttribute("login");
        if (login == null || login.isEmpty()) {
            login = "";
            // Determine login from cookie.
            Cookie[] cookies = request.getCookies();
            String cookieName = context.getInitParameter("loginCookieName");
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (cookieName != null && cookieName.equals(cookie.getName()))
                        login = cookie.getValue();
                }
            }
        }
        try {
            JspWriter out = pageContext.getOut();
            out.print(login);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return SKIP_BODY;
    }
}
