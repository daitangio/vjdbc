// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.test.junit.general;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;

import de.simplicit.vjdbc.test.junit.VJdbcTest;

public abstract class AddressTest extends VJdbcTest {
    private static final int NUMBER_OF_ADDRESSES = 1234;

    public AddressTest(String s) {
        super(s);
    }

    public String getCreateBlobsTableSql() {
        return "create table SomeBlobs (id int, description binary(100))";
    }

    protected void oneTimeSetup() throws Exception {
        super.oneTimeSetup();

        Connection connVJdbc = createVJdbcConnection();
        Statement stmt = connVJdbc.createStatement();
        dropTables(stmt, new String[] { "Address", "SomeBlobs" });
        stmt.executeUpdate("create table Address (" + "id int, " + "lastname varchar(100), " + "firstname char(50), " + "street varchar(200), "
                + "somenumber int, " + "birthday date, " + "currtime timestamp, " + "amount decimal(10,2), " + "stringboolean char(1), "
                + "integernumber varchar(20), " + "floatingnumber varchar(20)" + ")");
        PreparedStatement pstmt = connVJdbc.prepareStatement("insert into Address values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for(int i = 0; i < NUMBER_OF_ADDRESSES; i++) {
            pstmt.setInt(1, i);
            pstmt.setString(2, "Mike" + i);
            pstmt.setString(3, "Link" + i);
            pstmt.setString(4, "Anystreet");
            pstmt.setInt(5, (i % 100));
            pstmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
            String amount = "" + i + ".34";
            pstmt.setBigDecimal(8, new BigDecimal(amount));
            pstmt.setString(9, ((i & 1) != 0) ? "1" : "0");
            pstmt.setString(10, Integer.toString(i));
            pstmt.setString(11, Double.toString(i + 0.5));
            pstmt.addBatch();
        }
        int[] updates = pstmt.executeBatch();
        assertEquals(updates.length, NUMBER_OF_ADDRESSES);
        pstmt.close();

        stmt.executeUpdate(getCreateBlobsTableSql());
        stmt.close();

        pstmt = connVJdbc.prepareStatement("insert into SomeBlobs values (?, ?)");
        // Write with setBytes
        pstmt.setInt(1, 1);
        pstmt.setBytes(2, "Blob1".getBytes());
        pstmt.addBatch();
        // Write with setInputStream
        pstmt.setInt(1, 2);
        pstmt.setBinaryStream(2, new ByteArrayInputStream("Blob2".getBytes()), -1);
        pstmt.addBatch();

        updates = pstmt.executeBatch();
        pstmt.close();

        connVJdbc.close();
    }

    public void testSimple() throws Exception {
        PreparedStatement pstmt = _connVJdbc.prepareStatement("select * from Address order by id");
        ResultSet rs = pstmt.executeQuery();
        int i = 0;
        while (rs.next()) {
            assertEquals(i++, rs.getInt(1));
        }
        assertEquals(NUMBER_OF_ADDRESSES, i);
        rs.close();
        pstmt.close();
    }

    public void testEmptyResultSet() throws Exception {
        PreparedStatement pstmt = _connVJdbc.prepareStatement("select * from Address where id > 1000000 order by id");
        ResultSet rs = pstmt.executeQuery();
        assertFalse(rs.first());
        assertFalse(rs.last());
        rs.close();
        pstmt.close();
    }

    public void testGettingConnectionFromStatement() throws Exception {
        PreparedStatement pstmt = _connVJdbc.prepareStatement("select * from Address");
        Connection connOther = pstmt.getConnection();
        assertEquals(_connVJdbc, connOther);
        pstmt.close();
    }

    public void testNamedQuery() throws Exception {
        PreparedStatement pstmt1 = _connVJdbc.prepareStatement("select * from Address");
        PreparedStatement pstmt2 = _connVJdbc.prepareStatement("$selectAllAddresses");
        ResultSet rs1 = pstmt1.executeQuery();
        ResultSet rs2 = pstmt2.executeQuery();
        // Results must be equal
        compareResultSets(rs1, rs2);
        rs1.close();
        rs2.close();
        pstmt1.close();
        pstmt2.close();
    }

    public void testNamedQueryWithPreparedStatement() throws Exception {
        PreparedStatement pstmt1 = _connVJdbc.prepareStatement("select * from Address where Id = ?");
        pstmt1.setInt(1, 10);
        PreparedStatement pstmt2 = _connVJdbc.prepareStatement("$selectAddress");
        pstmt2.setInt(1, 10);
        ResultSet rs1 = pstmt1.executeQuery();
        ResultSet rs2 = pstmt2.executeQuery();
        // Results must be equal
        compareResultSets(rs1, rs2);
        rs1.close();
        rs2.close();
        pstmt1.close();
        pstmt2.close();
    }

    public void testNamedQueryInBatches() throws Exception {
        Statement stmt1 = _connVJdbc.createStatement();
        stmt1.addBatch("update Address set lastname = 'Balla' where lastname = 'Billi'");
        stmt1.addBatch("$updateAllAddresses");
        stmt1.executeBatch();
        stmt1.close();
    }

    public void testQueryFilters() throws Exception {
        Statement stmt = _connVJdbc.createStatement();
        stmt.executeQuery("select * from Address").close();
        stmt.executeUpdate("  update Address set lastname = 'Nix'");
        stmt.close();
    }

    public void testExecute() throws Exception {
        Statement stmt1 = _connVJdbc.createStatement();
        Statement stmt2 = _connVJdbc.createStatement();

        String query = "select * from Address where id > 100 and id < 200";

        ResultSet rs1 = stmt1.executeQuery(query);
        assertTrue(stmt2.execute(query));
        ResultSet rs2 = stmt2.getResultSet();
        compareResultSets(rs1, rs2);

        stmt1.close();
        stmt2.close();
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
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("select * from Address a, Address b where a.id = b.id");
            assertTrue(false);
        } catch (SQLException sqle) {
        } finally {
            if (rs != null) {
                rs.close();
            }
            stmt.close();
            t.join();
        }
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
        assertEquals(rsNative.getByte("id"), rsNative.getByte("integernumber"));
        assertEquals(rsVJdbc.getByte("id"), rsVJdbc.getByte("integernumber"));
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

    public void testExceptionStacktrace() throws Exception {
        Statement stmtVJdbc = _connVJdbc.createStatement();
        try {
            stmtVJdbc.executeQuery("select * from nonexistingtable");
        } catch (SQLException e) {
            String msg = e.getMessage().toLowerCase();
            System.out.println("err msg " + msg);
            assertTrue(msg.indexOf("table not found") >= 0 || msg.indexOf("doesn't exist") >= 0 || msg.indexOf("does not exist") >= 0);
            assertTrue(msg.indexOf("nonexistingtable") >= 0);
        }
        stmtVJdbc.close();
    }

    public void testParallelPreparedStatements() throws Exception {
        PreparedStatement stmtVjdbc1 = _connVJdbc.prepareStatement("select id, lastname from Address where somenumber = ?");
        stmtVjdbc1.setInt(1, 34);
        PreparedStatement stmtNative1 = _connOther.prepareStatement("select id, lastname from Address where somenumber = ?");
        stmtNative1.setInt(1, 34);
        compareResultSets(stmtVjdbc1.executeQuery(), stmtNative1.executeQuery());

        PreparedStatement stmtVjdbc2 = _connVJdbc.prepareStatement("select id, lastname from Address where id = ?");
        stmtVjdbc2.setInt(1, 45);
        PreparedStatement stmtNative2 = _connOther.prepareStatement("select id, lastname from Address where id = ?");
        stmtNative2.setInt(1, 45);
        compareResultSets(stmtVjdbc2.executeQuery(), stmtNative2.executeQuery());

        stmtVjdbc1.setInt(1, 44);
        stmtNative1.setInt(1, 44);
        compareResultSets(stmtVjdbc1.executeQuery(), stmtNative1.executeQuery());

        stmtVjdbc1.close();
        stmtNative1.close();
        stmtVjdbc2.close();
        stmtNative2.close();
    }

    public void testRepeatedStatementExecution() throws Exception {
        Statement stmt = _connVJdbc.createStatement();
        stmt.execute("select count(*) from Address");
        ResultSet rs = stmt.getResultSet();
        assertTrue(rs.next());
        int count1 = rs.getInt(1);
        assertEquals(count1, NUMBER_OF_ADDRESSES);
        rs.close();
        stmt.execute("select count(*) from Address");
        rs = stmt.getResultSet();
        assertTrue(rs.next());
        int count2 = rs.getInt(1);
        assertEquals(count2, NUMBER_OF_ADDRESSES);
        rs.close();
        stmt.close();
    }

    public void testReadBlobs() throws Exception {
        Statement stmt = _connVJdbc.createStatement();
        ResultSet rs = stmt.executeQuery("select * from SomeBlobs");
        assertTrue(rs.next());
        assertEquals("Blob1", new String(rs.getBytes(2)).trim());
        assertTrue(rs.next());
        InputStream is = rs.getBinaryStream(2);
        byte[] buff = new byte[10];
        int bytesRead = is.read(buff, 0, buff.length);
        // could be ASCII or UNICODE
        assertTrue(10 == bytesRead || 5 == bytesRead);
        String blobStr = new String(buff, 0, bytesRead);
        assertEquals("Blob2", blobStr.trim());
    }

    /*
    public void testCustomResultSetQuery() throws Exception {
        PreparedStatement pstmt = _connVJdbc.prepareStatement("#getAddress");
        pstmt.setInt(1, 10);
        ResultSet rs = pstmt.executeQuery();
        for(int i = 0; i < 10; i++) {
            assertTrue(rs.next());
            assertEquals("Foo" + i, rs.getString(1));
            assertEquals("Bar" + i, rs.getString(2));
        }
        assertFalse(rs.next());
    }
    */

    /*
    public void testPerformance() throws Exception {
        long perfNative = measurePerformanceSingleThread(_connOther);
        long perfVJdbc = measurePerformanceSingleThread(_connVJdbc);
        System.out.println("Single-Thread (Native): " + perfNative);
        System.out.println("Single-Thread (VJdbc): " + perfVJdbc);
        perfNative = measurePerformanceMultipleThreads(_connOther);
        perfVJdbc = measurePerformanceMultipleThreads(_connVJdbc);
        System.out.println("Multi-Thread (Native): " + perfNative);
        System.out.println("Multi-Thread (VJdbc): " + perfVJdbc);
    }
    */

    private long measurePerformanceSingleThread(final Connection conn) throws Exception {
        long start = System.currentTimeMillis();

        Statement stmt = conn.createStatement();
        for(int i = 0; i < 3000; i++) {
            ResultSet rs = stmt.executeQuery("select somenumber from Address where id = " + i);
            if(rs != null) {
                rs.close();
            }
        }
        stmt.close();
        // Return time difference
        return System.currentTimeMillis() - start;
    }

    private long measurePerformanceMultipleThreads(final Connection conn) throws Exception {
        final int THREAD_COUNT = 30;

        long start = System.currentTimeMillis();
        Thread[] th = new Thread[THREAD_COUNT];
        for(int i = 0; i < THREAD_COUNT; i++) {
            th[i] = new Thread(new Runnable() {
                public void run() {
                    try {
                        Statement stmt = conn.createStatement();
                        for(int j = 0; j < 100; j++) {
                            ResultSet rs = stmt.executeQuery("select somenumber from Address where id = " + j);
                            if(rs != null) {
                                rs.close();
                            }
                        }
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
            th[i].start();
        }
        // Threads are started, now wait for all to end
        for(int i = 0; i < THREAD_COUNT; i++) {
            th[i].join();
        }
        // Return time difference
        return System.currentTimeMillis() - start;
    }

    /*
     * public void testExecuteMultipleResultSets() throws Exception { Connection
     * connVJdbc = null; try { connVJdbc = createVJdbcConnection(); Statement
     * stmt1 = connVJdbc.createStatement(); Statement stmt2 =
     * connVJdbc.createStatement();
     *
     * String query1 = "select * from Address where id > 100 and id < 200";
     * String query2 = "select * from Address where id between 400 and 500";
     *
     * ResultSet rs1a = stmt1.executeQuery(query1);
     * assertTrue(stmt2.execute(query1 + ";" + query2)); ResultSet rs2a =
     * stmt2.getResultSet(); compareResultSets(rs1a, rs2a);
     *
     * ResultSet rs1b = stmt1.executeQuery(query2);
     * assertTrue(stmt2.getMoreResults()); ResultSet rs2b =
     * stmt2.getResultSet(); compareResultSets(rs1b, rs2b);
     *
     * stmt1.close(); stmt2.close(); } finally { connVJdbc.close(); } }
     */
}
