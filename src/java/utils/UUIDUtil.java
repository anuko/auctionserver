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


/**
 * Contains utility functions to work with UUID.
 *
 * @author Nik Okuntseff
 */
public class UUIDUtil {

    /**
     * Validates a UUID string.
     *
     * @param uuid UUID string to validate.
     * @return true if a uuid parameter represents a valid UUID.
     */
    public static boolean isUUID(String uuid) {
        if (uuid != null && uuid.matches("[0-9a-f]{8}-[0-9a-f]{4}-[34][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
            return true;
        }
        return false;
    }
}
