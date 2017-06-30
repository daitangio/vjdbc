// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import java.io.Externalizable;
import java.sql.SQLException;

/**
 * Interface for all commands which shall be executed by the server
 * CommandProcessor.
 */
public interface Command extends Externalizable {
    Object execute(Object target, ConnectionContext ctx) throws SQLException;
}
