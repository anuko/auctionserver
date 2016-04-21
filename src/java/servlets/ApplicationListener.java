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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;

import utils.I18n;


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
    private static I18n i18n;


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

        // Initialize I18n.
        i18n = new I18n();
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
        i18n = null;
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
     * Returns a static I18n object to get localized resource strings.
     *
     * @return initialized <code>I18n</code> object.
     */
    public static I18n getI18n() {
        return i18n;
    }
}
