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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Authenticates a user.
 *
 * @author Nik Okuntseff
 */
public class Authenticator {

    private static final Logger Log = LoggerFactory.getLogger(Authenticator.class);


    /**
     * Authenticates a user.
     *
     * @param login user login.
     * @param password user password.
     * @return true if user exists and password matches.
     */
    public String authenticate(String login, String password) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid from as_users where login = ? and password = md5(?) and status = 1");
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        return null;
    }


    /**
     * Performs a login procedure.
     *
     * @param login user login.
     * @param password user password.
     * @param session user <code>HttpSession</code>.
     * @return true if a user is authenticated.
     */
    public boolean doLogin(String login, String password, HttpSession session) {

        String uuid = authenticate(login, password);
        if (uuid == null) return false;

        session.setAttribute("authenticated", true);
        session.setAttribute("authenticated_user_uuid", uuid);
        session.setAttribute("login", login);
        return true;
    }


    /**
     * Performs a logout procedure.
     *
     * @param session user <code>HttpSession</code>.
     */
    public void doLogout(HttpSession session) {

        session.removeAttribute("authenticated");
        session.removeAttribute("authenticated_user_uuid");
        session.removeAttribute("login");
  }
}
