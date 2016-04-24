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


package utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides helper functions for operations with cookies.
 *
 * @author Nik Okuntseff
 */
public class CookieManager {


    /**
     * Sets a cookie in HTTP response.
     *
     * @param name cookie name.
     * @param value cookie value.
     * @param age cookie age in seconds.
     * @param request <code>HttpServletRequest</code> object.
     * @param response <code>HttpServletResponse</code> object.
     */
    public static void setCookie(String name, String value, int age, HttpServletRequest request, HttpServletResponse response) {

        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(age);
        String server = request.getServerName();
        if (server.startsWith("www."))
            server = server.substring(4);
        cookie.setDomain(("." + server));
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
