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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.jsp.jstl.core.Config;
import listeners.ApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Holds information about a site.
 *
 * @author Nik Okuntseff
 */
public class Site {

    private static final Logger Log = LoggerFactory.getLogger(Site.class);

    private static String uuid;
    private static String name;
    private static String uri;
    private static String email;
    private static String language;
    private static String template;
    private static String tracker_conf;


    /**
     * Initializes <code>Site</code> object with the details from the database.
     */
    public Site() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();

            // Obtain site info from the database.
            pstmt = conn.prepareStatement("select uuid, name, uri, email, language, template, tracker_conf from as_site_details");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                uuid = rs.getString("uuid");
                name = rs.getString("name");
                uri = rs.getString("uri");
                email = rs.getString("email");
                language = rs.getString("language");
                template = rs.getString("template");
                tracker_conf = rs.getString("tracker_conf");
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }
    }


    // Getter methods below.


    public static String getUuid() {
        return uuid;
    }


    public static String getName() {
        return name;
    }


    public static String getUri() {
        return uri;
    }


    public static String getEmail() {
        return email;
    }


    public static String getLanguage() {
        return language;
    }


    public static String getTemplate() {
        return template;
    }


    public static String getTrackerConf() {
        return tracker_conf;
    }
}
