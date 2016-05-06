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


/**
 * Holds information about a password and confirm password fields being edited.
 *
 * @author Nik Okuntseff
 */
public class PasswordBean {

    private String uuid;             // Random reference UUID.
    private String password;
    private String confirm_password;


    public PasswordBean() {
    }


    // Getter and setter methods below.


    public String getUuid() {
        return uuid;
    }


    public void setUuid(String val) {
        uuid = val;
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
}
