package de.simplicit.vjdbc.test.junit.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import de.simplicit.vjdbc.test.junit.VJdbcTest;

public class HSqlDbDynamicConfiguration extends VJdbcTest {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();

        VJdbcTest.addAllTestMethods(suite, HSqlDbDynamicConfiguration.class);

        TestSetup wrapper = new TestSetup(suite) {
            protected void setUp() throws Exception {
                new HSqlDbDynamicConfiguration("").oneTimeSetup();
            }

            protected void tearDown() throws Exception {
                new HSqlDbDynamicConfiguration("").oneTimeTearDown();
            }
        };

        return wrapper;
    }
    
    public HSqlDbDynamicConfiguration(String s) {
        super(s);
    }

    protected Connection createNativeDatabaseConnection() throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        return DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/HSqlDb", "sa", "");
    }

    protected String getVJdbcDatabaseShortcut() {
        return "HSqlDB2";
    }
    
    public void testDynamicConfiguration() throws Exception
    {
        Connection conn = null;
        
        try {
            conn = createVJdbcConnection();
            assertTrue(true);
        } catch (RuntimeException e) {
            fail("Couldn't open dynamic configuration");
        } finally {
            if(conn != null) {
                conn.close();
            }
        }
    }
}
