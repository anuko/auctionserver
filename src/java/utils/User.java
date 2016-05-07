package utils;

import java.sql.*;
import beans.ErrorBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.DatabaseManager;

/**
 * User object represents a user of Anuko Auction Server.
 * It maps to a single row in the "as_users" table in the database.
 */
public class User
{
    private static final Logger Log = LoggerFactory.getLogger(DatabaseManager.class);

    private String uuid;                // User UUID.
    private String login;               // User login.
    private String name;                // User name.
    private String email;               // User email.

    private ErrorBean error_bean;       // User errors.

    /**
     * Default constructor.
     */
    public User()
    {
        error_bean = new ErrorBean();
    }

    /**
     * Checks user login against password provided. In other words, this is
     * a login function. If a user with a matching password is found, the id and
     * login members are set accordingly.
     *
     * @param login User login.
     * @param password User password.
     */
    public User(String login, String password)
    {
        // Set id and login to default values.
        this.uuid = null;
        this.login = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select uuid, name, email from as_users where login = ? and password = md5(?)");
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                this.uuid = rs.getString("uuid");
                this.login = login;
                this.name = rs.getString("name");
                this.email = rs.getString("email");
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        error_bean = new ErrorBean();
    }


    /**
     * Creates a User object from user bid.
     * This constructor is used to auto-login a user who is confirming a frame bid.
     *
     * @param bid_uuid bid uuid.
     */
    public User(String bid_uuid)
    {
        String user_uuid = null;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("select user_uuid from as_bids where uuid = ?");
            pstmt.setString(1, bid_uuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                user_uuid = rs.getString("user_uuid");
            }
            pstmt = conn.prepareStatement("select uuid, login, name, email from as_users where uuid = ?");
            pstmt.setString(1, user_uuid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                this.uuid = rs.getString("uuid");
                this.login = rs.getString("login");
                this.name = rs.getString("name");
                this.email = rs.getString("email");
            }
        }
        catch (SQLException e) {
            Log.error(e.getMessage(), e);
        }
        finally {
            DatabaseManager.closeConnection(rs, pstmt, conn);
        }

        error_bean = new ErrorBean();
    }

    public String getUuid()
    {
        return uuid;
    }

    public String getLogin()
    {
        return login;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }


    public ErrorBean getErrorBean()
    {
        return error_bean;
    }
}
