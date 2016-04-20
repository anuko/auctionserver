package business;

import java.sql.*;
import java.util.*;

/**
 * User object represents a single user of Anuko Applications (Apps).
 * It maps to a single row in the "users" table in the apps database.
 */
public class User
{
    private int id;                     // User id from the database.
    private String login;               // User login from the database.
    
    /**
     * Default constructor.
     */
    public User()
    {
        id = 0;
        login = "";
    }
    
    /**
     * Checks user login against password provided. In other words, this is
     * a login function. If a user with matching password is found, the id and
     * login members are set accordingly. Not zero id means we have a successful
     * login. In this case, the internal lists member is populated with user
     * checklists from the database. The Checklist objects in there do not have
     * any items.
     * @param login User login.
     * @param password User password.
     */
    public User(String login, String password)
    {
        // Set id and login to default values.
        this.id = 0;
        this.login = "";
    }
    
    public void setUserId(int user_id)
    {
        this.id = user_id;
    }

    public int getUserId()
    { 
        return id; 
    }
    
    public String getLogin()
    { 
        return login; 
    }
    
    public void setLogin(String login)
    { 
        this.login = login; 
    }
    
}