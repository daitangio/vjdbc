// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.command;

import de.simplicit.vjdbc.Registerable;
import de.simplicit.vjdbc.serial.UIDEx;
import de.simplicit.vjdbc.util.JavaVersionInfo;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

/**
 * This guard object checks if the given object shall be passed to the client or if it shall
 * be put into the object pool with a UID.
 */
class ReturnedObjectGuard {

    public static UIDEx checkResult(Object obj) {
        if (obj instanceof Registerable) {
            return ((Registerable)obj).getReg();
        } else if(obj instanceof Statement) {
            try {
                Statement stmt = (Statement)obj;
                return new UIDEx(stmt.getQueryTimeout(), stmt.getMaxRows());
            } catch(SQLException e) {
                return new UIDEx();
            }
        } else if(obj instanceof DatabaseMetaData) {
            return new UIDEx();
        } else if(JavaVersionInfo.use14Api && obj instanceof Savepoint) {
            return new UIDEx();
        } else {
            return null;
        }
    }
}
