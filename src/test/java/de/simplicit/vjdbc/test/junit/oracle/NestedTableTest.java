// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.test.junit.oracle;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class NestedTableTest extends Oracle9iTest {
    public NestedTableTest(String s) {
        super(s);
    }

    protected void oneTimeSetup() throws Exception {
        super.oneTimeSetup();

        Connection connVJdbc = createVJdbcConnection();

        System.out.println("Creating tables ...");
        Statement stmt = connVJdbc.createStatement();
        dropTables(stmt, new String[]{"product", "supplier_tbl"});
        System.out.println("... create type supplier");
        stmt.executeUpdate("create or replace type supplier as table of char(3)");
        System.out.println("... create table product");
        stmt.executeUpdate("create table product (prodno char(8) primary key, price number(5,2), supplierno supplier) nested table supplierno store as supplier_tbl");
        System.out.println("... insert product data");
        PreparedStatement pstmt = connVJdbc.prepareStatement("insert into product values (?, 23.45, supplier('101', '224'))");
        for(int i = 0; i < 200; i++) {
            pstmt.setString(1, "XY" + i);
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.close();
        connVJdbc.close();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(new NestedTableTest("testConnection"));

        TestSetup wrapper = new TestSetup(suite) {
            protected void setUp() throws Exception {
                new NestedTableTest("").oneTimeSetup();
            }

            protected void tearDown() throws Exception {
                new NestedTableTest("").oneTimeTearDown();
            }
        };

        return wrapper;
    }
}
