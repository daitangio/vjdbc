// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc;

import java.sql.SQLException;

import de.simplicit.vjdbc.serial.UIDEx;

/**
 * A factory for turning proxy network objects back into their full JDBC
 * form on the client.
 */
public interface ProxyFactory {

    public Object makeJdbcObject(Object proxy) throws SQLException;
}
