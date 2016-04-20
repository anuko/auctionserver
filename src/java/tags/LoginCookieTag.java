package tags;

import java.io.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Obtains the value of user login string either from the session or cookie (name defined in web.xml).
 */
public class LoginCookieTag extends TagSupport {
    
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
