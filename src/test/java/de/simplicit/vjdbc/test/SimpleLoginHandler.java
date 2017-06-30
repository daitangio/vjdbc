// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.test;

import de.simplicit.vjdbc.VJdbcException;
import de.simplicit.vjdbc.server.LoginHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SimpleLoginHandler implements LoginHandler {
    private Properties _properties = new Properties();

    public SimpleLoginHandler() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("de/simplicit/vjdbc/test/user.properties");
        _properties.load(is);
    }

    public void checkLogin(String user, String password) throws VJdbcException {
        if (user != null) {
            String pw = _properties.getProperty(user);

            if (pw != null) {
                if (!pw.equals(password)) {
                    throw new VJdbcException("Password for user " + user + " is wrong");
                }
            } else {
                throw new VJdbcException("Unknown user " + user);
            }
        } else {
            throw new VJdbcException("User is null");
        }
    }
}
