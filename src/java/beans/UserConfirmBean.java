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


package beans;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.DatabaseManager;


/**
 * Holds information about a user being confirmed during bid confirmation process.
 *
 * @author Nik Okuntseff
 */
public class UserConfirmBean {

    private static final Logger Log = LoggerFactory.getLogger(UserConfirmBean.class);

    private String ref_uuid;
    private String user_uuid;
    private String login;
    private String password;
    private String confirm_password;
    private String name;
    private String email;


    public UserConfirmBean() {
    }


    /**
     * Constructs a bean using temporary reference UUID.
     *
     * @param reference reference UUID.
     */
    public UserConfirmBean(String reference) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String user_uuid = null;

        try {
            // Determine if we have a reference in the table.
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select user_uuid from as_tmp_refs " +
                "where uuid = ?");
            pstmt.setString(1, reference);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                user_uuid = rs.getString("user_uuid");
            }

            if (user_uuid != null) {
                pstmt = conn.prepareStatement("select uuid, login, email from as_users " +
                    "where uuid = ?");
                pstmt.setString(1, user_uuid);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    this.ref_uuid = reference;
                    this.user_uuid = rs.getString("uuid");
                    this.login = rs.getString("login");
                    this.email = rs.getString("email");
                }
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }


    // Getter and setter functions below.


    public String getRefUuid() {
        return ref_uuid;
    }


    public void setRefUuid(String val) {
        ref_uuid = val;
    }


    public String getUserUuid() {
        return user_uuid;
    }


    public void setUserUuid(String val) {
        user_uuid = val;
    }


    public String getLogin() {
        return login;
    }


    public void setLogin(String val) {
        login = val;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String val) {
        password = val;
    }


    public String getConfirmPassword() {
        return confirm_password;
    }


    public void setConfirmPassword(String val) {
        confirm_password = val;
    }


    public String getName() {
        return name;
    }


    public void setName(String val) {
        name = val;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String val) {
        email = val;
    }
}
