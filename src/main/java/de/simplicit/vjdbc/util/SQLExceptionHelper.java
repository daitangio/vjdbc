// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * SQLExceptionHelper wraps driver-specific exceptions in a generic SQLException.
 */
public class SQLExceptionHelper {

    public static SQLException wrap(Throwable t) {

        if (isExceptionGeneric(t)) {
            if (t instanceof SQLException) {
                return (SQLException)t;
            }
            return new SQLException(t.getMessage(), t);
        }

        return wrapThrowable(t);
    }

    public static SQLException wrap(SQLException ex) {

        if (isSQLExceptionGeneric(ex)) {
            // yes a bit misleading but since this exception is already OK
            // for transport, its much simplier just to return it
            return ex;
        }
        else {
            return wrapSQLException(ex);
        }
    }

    private static boolean isExceptionGeneric(Throwable ex) {

        boolean exceptionIsGeneric = true;
        Throwable loop = ex;

        while (loop != null && exceptionIsGeneric) {

            exceptionIsGeneric =
                java.io.Serializable.class.isAssignableFrom(loop.getClass()) ||
                java.io.Externalizable.class.isAssignableFrom(loop.getClass());
            loop = loop.getCause();
        }

        return exceptionIsGeneric;
    }

    private static boolean isSQLExceptionGeneric(SQLException ex) {

        boolean exceptionIsGeneric = true;
        Iterator<Throwable> it = ex.iterator();

        while (it.hasNext() && exceptionIsGeneric) {

            Throwable t = it.next();
            exceptionIsGeneric =
                java.io.Serializable.class.isAssignableFrom(t.getClass()) ||
                java.io.Externalizable.class.isAssignableFrom(t.getClass());
        }

        return exceptionIsGeneric;
    }

    private static SQLException wrapSQLException(SQLException ex) {

        SQLException ex2 =
            new SQLException(ex.getMessage(), ex.getSQLState(),
                             ex.getErrorCode(), wrap(ex.getCause()));

        if (ex.getNextException() != null) {
            ex2.setNextException(wrap(ex.getNextException()));
        }
        return ex2;
    }

    private static SQLException wrapThrowable(Throwable t) {

        SQLException wrapped = null;
        if (t instanceof SQLException) {
            wrapped = wrapSQLException((SQLException)t);
        } else {
            wrapped = new SQLException(t.getMessage(), wrap(t.getCause()));
        }
        // REVIEW: doing some evil hackeration here, but only because I believe
        // that those that change stack traces deserve a special place in hell
        // If your code can be hacked by stack trace info, it deserves to
        // be hacked and will be cracked anyway
        wrapped.setStackTrace(wrapped.getStackTrace());

        return wrapped;
    }
}
