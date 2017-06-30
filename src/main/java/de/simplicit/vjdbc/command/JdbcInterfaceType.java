// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import java.sql.*;

import de.simplicit.vjdbc.server.command.ResultSetHolder;

public class JdbcInterfaceType {
    public static final Class[] _interfaces = new Class[] {
        null,
        CallableStatement.class,
        Connection.class,
        DatabaseMetaData.class,
        PreparedStatement.class,
        Savepoint.class,
        Statement.class,
        ResultSetHolder.class
    };

    public static final int CALLABLESTATEMENT = 1;
    public static final int CONNECTION = 2;
    public static final int DATABASEMETADATA = 3;
    public static final int PREPAREDSTATEMENT = 4;
    public static final int SAVEPOINT = 5;
    public static final int STATEMENT = 6;
    public static final int RESULTSETHOLDER = 7;
}
