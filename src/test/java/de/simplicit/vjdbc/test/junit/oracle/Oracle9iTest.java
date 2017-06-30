// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.test.junit.oracle;

import de.simplicit.vjdbc.test.junit.VJdbcTest;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Oracle9iTest extends VJdbcTest {
    public Oracle9iTest(String s) {
        super(s);
    }

    protected Connection createNativeDatabaseConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL", "scott", "tiger");
    }

    protected String getVJdbcDatabaseShortcut() {
        return "OracleDB";
    }

        protected String getVJdbcPassword() {
                return "vjdbc";
        }

        protected String getVJdbcUser() {
                return "vjdbc";
        }

        protected void oneTimeSetup() throws Exception {
        super.oneTimeSetup();

        Class.forName("oracle.jdbc.OracleDriver");
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();

        suite.addTest(new Oracle9iTest("testConnection"));
        suite.addTest(new Oracle9iTest("testDatabaseMetaData"));
        suite.addTest(OracleAddressTest.suite());
        suite.addTest(NestedTableTest.suite());
        suite.addTest(ObjectTest.suite());
        suite.addTest(BlobClobTest.suite());

        TestSetup wrapper = new TestSetup(suite) {
            protected void setUp() throws Exception {
                new Oracle9iTest("").oneTimeSetup();
            }

            protected void tearDown() throws Exception {
                new Oracle9iTest("").oneTimeTearDown();
            }
        };

        return wrapper;
    }

    protected void dropTypes(Statement stmt, String[] types) {
        for(int i = 0; i < types.length; i++) {
            System.out.println("... drop type " + types[i]);
            try {
                stmt.executeUpdate("drop type " + types[i]);
            } catch(SQLException e) {
                System.out.println("... doesn't exist");
            }
        }
    }
}
