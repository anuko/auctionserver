/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

/**
 * Used to track website visits with a cookie. A value of the cookie is a timestamp of the first visit.
 * Page visits are stored in the local file system as text lines in the corresponding file.
 *
 * @author Nik
 */
public class VisitorTracker {

    private String cookieName = "tc";    // Cookie name.
    private int cookieAge = 60*60*24*90; // Cookie time to live in seconds. 90 days tracking. Started on May 10, 2013.
    private String recent;   // Local directory where recent visits are stored.
                             // It should be small for efficiency of tracking.
                             // Smallness is achieved by regular move and merge operation from here to archive.
    //private String archive;  // Archive directory. Recent visits are moved and merged into here.
                             // This is our main repository in the file system, which may grow very large.
                             // Therefore, a separate mechanism must be in place to clean it.
    private String[] sites;  // Only local websites listed in here are tracked.
    private String[] pages;  // Only pages ending with strings in here are tracked.

    // Blacklists.
    private String[] bots;   // User agent sub-strings that identify bots. Bots are not tracked.
    private String[] ips;    // Blacklisted IPs. Visits from blacklisted IPs are not tracked.
    private String[] referers; // Blacklisted referers. Visits from blacklisted referers are not tracked.

    //private int moveCounter; // Incremented each time a blacklisted entity visits us.
                             // When a treshold is reached, a move and merge occurs, and the counter is reset.
                             // This occurs during a visit from a blacklisted entity to keep user visits fast.
    //private int moveThreshold; // When moveCounter becomes greater than moveThreshold, we can move and merge the files.

    /*
     * Initializes the VisitorTracker object by reading config parameters from the tracker.conf.
     *
     * @param pathToConf - full path to the configuration file.
     */
    public VisitorTracker(String pathToConf)
    {
        // Read the values from the config file.
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(pathToConf));
            String line = br.readLine();

            while (line != null) {
                if (line.startsWith("CookieName "))
                    cookieName = line.substring(11);
                else if(line.startsWith("CookieAge "))
                    cookieAge = Integer.valueOf(line.substring(10));
                if (line.startsWith("RecentDir "))
                    recent = line.substring(10);
               // else if(line.startsWith("ArchiveDir "))
                 //   archive = line.substring(11);
                else if(line.startsWith("TrackSites "))
                    sites = line.substring(11).split(",");
                else if(line.startsWith("PagesEndWith "))
                    pages = line.substring(13).split(",");
                else if(line.startsWith("Bots "))
                    bots = line.substring(5).split(",");
                //else if(line.startsWith("MoveThreshold "))
                  //  moveThreshold = Integer.valueOf(line.substring(14));
                else if (line.trim().startsWith("DoNotTrack {")) {
                    ArrayList<String> ipList = new ArrayList<String>();
                    line = br.readLine();
                     while (!line.trim().startsWith("}") && line != null) {
                         ipList.add(line.substring(0, line.indexOf(";")).trim());
                         line = br.readLine();
                     }
                    ips = new String[ipList.size()];
                    ips = ipList.toArray(ips);
                }
                else if (line.trim().startsWith("DoNotTrackReferers {")) {
                    ArrayList<String> refererList = new ArrayList<String>();
                    line = br.readLine();
                     while (!line.trim().startsWith("}") && line != null) {
                         refererList.add(line.substring(0, line.indexOf(";")).trim());
                         line = br.readLine();
                     }
                    referers = new String[refererList.size()];
                    referers = refererList.toArray(referers);
                }
                line = br.readLine();
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    // The trackVisit function inserts information about a visit into a file.
    public void trackVisit(HttpServletRequest request, HttpServletResponse response)
    {
        String server = request.getServerName();
        if (server.startsWith("www."))
            server = server.substring(4);

        // Track visits only to listed websites. Requests to other sites are ignored.
        boolean track = false;
        if (sites == null) return;
        for(String s : sites) {
            if (server.equals(s)) { track = true; break; }
        }
        if (!track) return;

        // Track only requests to specific pages.
        String uri = request.getRequestURI();
        track = false;
        if (pages != null) {
            for(String s : pages) {
                if (uri.endsWith(s)) { track = true; break; }
            }
        }
        if (!track) return;

        // Ignore bots.
        String ua = request.getHeader("User-Agent");
        if (ua == null) return;
        if (bots != null) {
            for(String s : bots) {
                if (ua.contains(s)) {
                    //moveCounter++;
                    return; // Ignore a request from bot.
                }
            }
        }

        // Ignore requests from DoNotTrack IP list.
        String ip = request.getRemoteAddr();
        if (ip == null) return;
        if (ips != null) {
            for(String s : ips) {
                if (ip.equals(s)) {
                    //moveCounter++;
                    return; // Ignore a request from DoNotTrack IP list.
                }
            }
        }

        // Ignore requests from DoNotTrackReferers list.
        String ref = request.getHeader("Referer");
        if (ref != null && referers != null) {
            for(String s : referers) {
                if (ref.contains(s)) {
                    //moveCounter++;
                    return; // Ignore a request from DoNotTrackReferers list.
                }
            }
        }

        // Ignore a request to search query without "tracker_param" attribute set (we log it separately).
        String trackerParam = (String) request.getAttribute("tracker_param");
        if (trackerParam == null && uri.endsWith("/root/search.htm"))
            return;

        Cookie[] cookies = request.getCookies();
        String cookieValue = "";
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookieName.equals(cookie.getName()))
                    cookieValue = cookie.getValue();
            }
        }
        if (cookieValue.equals("")) {
            // Create a new timestamp.
            java.util.Date date = new java.util.Date();
            cookieValue = Long.toString(date.getTime());
        }

        // Track this visit in the cookie file.
        try {
            java.io.File file = new java.io.File(recent + server + "/" + cookieValue);
            PrintWriter out = new PrintWriter(new FileWriter(file, true));

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
            java.util.Date date = new java.util.Date();
            String timestamp = sdf.format(date);

            String method = request.getMethod();
            uri = request.getRequestURL().toString();
            String qryString = request.getQueryString();
            if (qryString != null)
                uri += "?" + qryString;
            if (qryString == null && trackerParam != null)
                uri += "?q=" + java.net.URLEncoder.encode(trackerParam, "UTF-8"); // Append search pattern to the URL we log for visitor tracking.
            String rq = "\"" + method + " " + uri + "\"";

            String referer = request.getHeader("Referer");
            if (referer != null)
                referer = referer.replaceAll("(\\r|\\n)", ""); // Neutralize CR and LF injections.
            referer = "\"" + referer + "\"";

            if (ua != null)
                ua = ua.replaceAll("(\\r|\\n)", ""); // Neutralize CR and LF injections.
            ua = "\"" + ua + "\"";

            out.println(timestamp + " " + ip + " " + rq + " " + referer + " " + ua);
            out.close();
        }
        catch(IOException e) {
           System.out.println("Sorry, exception occurred when writing to a cookie file...");
        }

        // Set the cookie in response. This updates the existing cookie expiration date.
        Cookie trackingCookie = new Cookie(cookieName, cookieValue);
        trackingCookie.setMaxAge((cookieAge));
        trackingCookie.setDomain(("." + server));
        trackingCookie.setPath("/");
        response.addCookie(trackingCookie);

        return;
    }

    // The trackVisitor function tracks a visit for a specific customer.
    // This function is used when cookie is not available in the request but is obtained by other means.
    // For example, a request from a payment system will not have user cookie set.
    // However, if we manage to pass the cookie as a paramater we can still track the visit.
    public void trackVisitor(String customerCookie, HttpServletRequest request, HttpServletResponse response)
    {
        String server = request.getServerName();
        if (server.startsWith("www."))
            server = server.substring(4);

        // Track this visit in the cookie file.
        try {
            java.io.File file = new java.io.File(recent + server + "/" + customerCookie);
            PrintWriter out = new PrintWriter(new FileWriter(file, true));

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
            java.util.Date date = new java.util.Date();
            String timestamp = sdf.format(date);

            String method = request.getMethod();
            String uri = request.getRequestURL().toString();
            String qryString = request.getQueryString();
            if (qryString != null)
              uri += "?" + qryString;
            String rq = "\"" + method + " " + uri + "\"";

            String referer = request.getHeader("Referer");
            if (referer != null)
                referer = referer.replaceAll("(\\r|\\n)", ""); // Neutralize CR and LF injections.
            referer = "\"" + referer + "\"";

            String ua = request.getHeader("User-Agent");
            if (ua != null)
                ua = ua.replaceAll("(\\r|\\n)", ""); // Neutralize CR and LF injections.
            ua = "\"" + ua + "\"";

            String ip = request.getRemoteAddr();

            out.println(timestamp + " " + ip + " " + rq + " " + referer + " " + ua);
            out.close();
        }
        catch(IOException e) {
           System.out.println("Sorry, exception occurred when writing to a cookie file...");
        }
    }
}

