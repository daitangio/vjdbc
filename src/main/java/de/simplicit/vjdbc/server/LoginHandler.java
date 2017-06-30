// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server;

import de.simplicit.vjdbc.VJdbcException;

public interface LoginHandler {
    void checkLogin(String user, String password) throws VJdbcException;
}
