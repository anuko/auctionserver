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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.jsp.jstl.core.Config;
import utils.DatabaseManager;

import utils.SiteBean;
import utils.I18n;
import utils.Authenticator;


/**
 * Auction Server application listener.
 * Its methods are called during application initialization and destruction.
 * We use the contextInitialized call to initialize the application.
 *
 * @author Nik Okuntseff
 */
public class ApplicationListener implements ServletContextListener {

    private static final Logger Log = LoggerFactory.getLogger(ApplicationListener.class);
    private static ServletContext context;
    private static SimpleDateFormat sdf;
    private static SiteBean site;
    private static I18n i18n;
    private static Authenticator auth;


    /**
     * Initializes the web application as a whole.
     *
     * @param sce the ServletContextEvent containing the ServletContext that is being initialized.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // We get here when the application initializes.
        // We do our application initialization steps here.
        context = sce.getServletContext();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        site = new SiteBean();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();

            // Obtain site info from the database.
            pstmt = conn.prepareStatement("select uuid, name, uri, language, template from as_site_details");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                site.setUuid(rs.getString(1));
                site.setName(rs.getString(2));
                site.setUri(rs.getString(3));
                site.setLanguage(rs.getString(4));
                site.setTemplate(rs.getString(5));
            }

            // Obtain supported currencies.
            List<HashMap<String,String>> currencies = new ArrayList<HashMap<String,String>>();
            pstmt = conn.prepareStatement("select currency from as_currencies order by ord_num");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                HashMap<String, String> map = new HashMap();
                map.put("name", rs.getString(1));
                currencies.add(map);
            }
            // Add curency set to the application context.
            context.setAttribute("currencies", currencies);

            // Obtain supported durations.
            List<HashMap<String,String>> durations = new ArrayList<HashMap<String,String>>();
            pstmt = conn.prepareStatement("select duration from as_durations order by ord_num");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                HashMap<String, String> map = new HashMap();
                map.put("days", rs.getString(1));
                durations.add(map);
            }
            // Add curency set to the application context.
            context.setAttribute("durations", durations);
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        // Initialize I18n.
        i18n = new I18n(site.getLanguage());

        // Set locale and localization context for fmt taglib used in all jsp pages.
        Config.set(ApplicationListener.getServletContext(), Config.FMT_LOCALE, I18n.getLocale());
        Config.set(ApplicationListener.getServletContext(), Config.FMT_LOCALIZATION_CONTEXT, "i18n.auctionserver");
        // The above code is here to eliminate the following from each JSP page.
        /*
        <fmt:setLocale value="en" />
        <fmt:setBundle basename="i18n.auctionserver" />
        */

        // Set template and style to use as application context attributes.
        context.setAttribute("template", "/templates/"+site.getTemplate()+"/index.jsp");
        context.setAttribute("style", context.getContextPath()+"/templates/"+site.getTemplate()+"/style.css");

        // Set context path.
        context.setAttribute("ctx", context.getContextPath());

        // Initialize authenticator.
        auth = new Authenticator();
    }


    /**
     * Does cleanup before the web application exits.
     *
     * @param sce the ServletContextEvent containing the ServletContext that is being destroyed.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        context = null;
        sdf = null;
        site = null;
        i18n = null;
        auth = null;
    }


    /**
     * Returns web application <code>ServletContext</code>.
     * Intended use is for other areas of the application that are not aware of it.
     *
     * @return The <code>ServletContext</code> object of the web application.
     */
    public static ServletContext getServletContext() {
        return context;
    }


    /**
     * Returns "yyyy-MM-dd HH:mm:ss" SimpleDateFormat.
     *
     * @return <code>SimpleDateFormat</code> object to use.
     */
    public static SimpleDateFormat getSimpleDateFormat() {
        return sdf;
    }


    /**
     * Returns a static <code>Authenticator</code> object.
     *
     * @return initialized <code>Authenticator</code> object.
     */
    public static Authenticator getAuthenticator() {
        return auth;
    }


    /**
     * Returns a static I18n object to get localized resource strings.
     *
     * @return initialized <code>I18n</code> object.
     */
    public static I18n getI18n() {
        return i18n;
    }


    /**
     * Returns site bean.
     *
     * @return initialized <code>SiteBean</code> object.
     */
    public static SiteBean getSiteBean() {
        return site;
    }
}
