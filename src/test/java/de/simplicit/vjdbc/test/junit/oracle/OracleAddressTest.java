// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.test.junit.oracle;

import de.simplicit.vjdbc.test.junit.VJdbcTest;
import de.simplicit.vjdbc.test.junit.general.AddressTest;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.math.BigDecimal;
import java.sql.*;

public class OracleAddressTest extends AddressTest {

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();

        VJdbcTest.addAllTestMethods(suite, OracleAddressTest.class);

        TestSetup wrapper = new TestSetup(suite) {
            protected void setUp() throws Exception {
                new OracleAddressTest("").oneTimeSetup();
            }

            protected void tearDown() throws Exception {
                new OracleAddressTest("").oneTimeTearDown();
            }
        };

        return wrapper;
    }

    public OracleAddressTest(String s) {
        super(s);
    }

    protected Connection createNativeDatabaseConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL", "scott", "tiger");
    }

    protected String getVJdbcPassword() {
        return "vjdbc";
    }

    protected String getVJdbcUser() {
        return "vjdbc";
    }

    protected String getVJdbcDatabaseShortcut() {
        return "OracleDB";
    }

    public String getCreateBlobsTableSql() {
        return "create table SomeBlobs (id int, description raw(100))";
    }

    public void testExceptionStacktrace() throws Exception {
        Statement stmtVJdbc = _connVJdbc.createStatement();
        try {
            stmtVJdbc.executeQuery("select * from nonexistingtable");
        } catch (SQLException e) {
            String msg = e.getMessage().toLowerCase();
            System.out.println("err msg " + msg);
            assertTrue(msg.indexOf("table not found") >= 0 || msg.indexOf("doesn't exist") >= 0 || msg.indexOf("does not exist") >= 0);
        }
        stmtVJdbc.close();
    }

    public void testCancelStatement() throws Exception {
        final Statement stmt = _connVJdbc.createStatement();
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                    stmt.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        ResultSet rs = stmt.executeQuery("select * from Address a, Address b where a.id = b.id");
        rs.close();
        stmt.close();
        t.join();
    }

    public void testConversions() throws Exception {
        Statement stmtVJdbc = _connVJdbc.createStatement();
        Statement stmtNative = _connOther.createStatement();

        ResultSet rsVJdbc = stmtVJdbc.executeQuery("select id, somenumber, stringboolean, integernumber, floatingnumber from Address");
        ResultSet rsNative = stmtNative.executeQuery("select id, somenumber, stringboolean, integernumber, floatingnumber from Address");

        assertTrue(rsNative.next());
        assertTrue(rsVJdbc.next());

        int nId = rsNative.getInt("id");
        int vId = rsVJdbc.getInt("id");
        boolean nIsZero = rsNative.getBoolean("somenumber");
        boolean vIsZero = rsVJdbc.getBoolean("somenumber");
        assertEquals(nIsZero, vIsZero);

        nIsZero = rsNative.getBoolean("stringboolean");
        vIsZero = rsVJdbc.getBoolean("stringboolean");
        assertEquals(nId, vId);
        assertEquals(nIsZero, vIsZero);
        // Check String-to-Integer conversion
        assertEquals(rsNative.getShort("id"), rsNative.getShort("integernumber"));
        assertEquals(rsVJdbc.getShort("id"), rsVJdbc.getShort("integernumber"));
        assertEquals(rsNative.getInt("id"), rsNative.getInt("integernumber"));
        assertEquals(rsVJdbc.getInt("id"), rsVJdbc.getInt("integernumber"));
        assertEquals(rsNative.getLong("id"), rsNative.getLong("integernumber"));
        assertEquals(rsVJdbc.getLong("id"), rsVJdbc.getLong("integernumber"));
        // Check String-to-Float conversion
        assertEquals(rsNative.getBigDecimal("id").add(new BigDecimal("0.5")), rsNative.getBigDecimal("floatingnumber"));
        assertEquals(rsVJdbc.getBigDecimal("id").add(new BigDecimal("0.5")), rsVJdbc.getBigDecimal("floatingnumber"));
        rsNative.close();
        rsVJdbc.close();
        stmtVJdbc.close();
        stmtNative.close();
    }
}
