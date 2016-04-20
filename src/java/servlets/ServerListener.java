package servlets;

//import com.anuko.utils.DatabaseManager;
//import com.anuko.utils.SQLUtil;
//import com.anuko.utils.OutboundThread;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashMap;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.Date;


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

    /**
     * Initializes Auction Server application as a whole.
     *
     * @param sce the ServletContextEvent containing the ServletContext that is being initialized.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // We get here when the application initializes.
        // We do our application initialization steps here.
        context = sce.getServletContext();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Does cleanup before application exit.
     *
     * @param sce the ServletContextEvent containing the ServletContext that is being destroyed.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        context = null;
        sdf = null;
    }

    /**
     * Returns application servlet context.
     * Intended use is for other areas of the application that are not aware of it.
     */
    public static ServletContext getServletContext() {
        return context;
    }
    
    /**
     * Returns "yyyy-MM-dd HH:mm:ss" SimpleDateFormat.
     */
    public static SimpleDateFormat getSimpleDateFormat() {
        return sdf;
    }
}
