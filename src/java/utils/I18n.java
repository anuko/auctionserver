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
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Helper class that encapsulates getting localized strings from the application resource bundle.
 *
 * @author Nik Okuntseff
 */
public class I18n {

    private static final Logger Log = LoggerFactory.getLogger(DatabaseManager.class);
    private static ResourceBundle bundle;

    /**
     * Initializes <code>I18n</code> object with the language from the database.
     */
    public I18n() {

        String language = "en"; // Default language is English.
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // Obtain site language from the database.
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select language from as_site_details");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                language = rs.getString(1);
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        Locale currentLocale = new Locale(language);
        bundle = ResourceBundle.getBundle("i18n.auctionserver", currentLocale);        
    }


    /**
     * Returns a localized string from the application resource bundle.
     *
     * @param key the key of the desired localized string.
     *
     * @return localized <code>String</code> associated with the key.
     */
    public static String get(String key) {
        return bundle.getString(key);
    }


    /**
     * Returns a localized string with parameters from the application resource bundle.
     *
     * @param key the key of the desired localized string.
     * @param param variable number of optional parameters to substitute
     *              in a localized pattern associated with a key.
     *
     * @return localized <code>String</code> associated with the key.
     */
    public static String get(String key, String... param) {
        String pattern = bundle.getString(key);
        return MessageFormat.format(pattern, (Object[]) param);
    }
}
