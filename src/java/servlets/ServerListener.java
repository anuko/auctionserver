package servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;


/**
 * Auction Server application listener.
 * Its methods are called during application initialization and destruction.
 * We use the contextInitialized call to initialize the application.
 *
 * @author Nik Okuntseff
 */
public class ServerListener implements ServletContextListener {

    private static final Logger Log = LoggerFactory.getLogger(ServerListener.class);
    private static ServletContext context;
    private static SimpleDateFormat sdf;
    private static ResourceBundle bundle;

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

        Locale currentLocale = new Locale("en"); // TODO: obtain it from the database.
        bundle = ResourceBundle.getBundle("i18n.auctionserver", currentLocale);
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
        bundle = null;
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
     * Returns a localized string from the application resource bundle.
     *
     * @param key the key of the desired localized string.
     *
     * @return localized <code>String</code> associated with the key.
     */
    public static String getString(String key) {
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
    public static String getString(String key, String... param) {
        String pattern = bundle.getString(key);
        return MessageFormat.format(pattern, param);
    }
}
