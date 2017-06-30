// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.test.junit.jdbcodbc;

import de.simplicit.vjdbc.test.junit.VJdbcTest;
import de.simplicit.vjdbc.test.junit.general.AddressTest;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcOdbcAddressTest extends AddressTest {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();
        
        VJdbcTest.addAllTestMethods(suite, JdbcOdbcAddressTest.class);

        TestSetup wrapper = new TestSetup(suite) {
            protected void setUp() throws Exception {
                new JdbcOdbcAddressTest("").oneTimeSetup();
            }

            protected void tearDown() throws Exception {
                new JdbcOdbcAddressTest("").oneTimeTearDown();
            }
        };

        return wrapper;
    }

    public JdbcOdbcAddressTest(String s) {
        super(s);
    }

    protected Connection createNativeDatabaseConnection() throws Exception {
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        return DriverManager.getConnection("jdbc:odbc:testdb");
    }

    protected String getVJdbcDatabaseShortcut() {
        return "OdbcDB";
    }
}
