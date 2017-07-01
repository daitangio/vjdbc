package com.gioorgi.vjdbc.test;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.simplicit.vjdbc.serial.StreamSerializer;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class SuperSimpleHSqlTest{

	protected Logger logger=Logger.getLogger(getClass());

	private static final int NUMBER_OF_ADDRESSES = 1234;

	protected DateFormat _dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
		Class.forName("de.simplicit.vjdbc.VirtualDriver").newInstance();
		//		Class.forName("org.hsqldb.jdbcDriver");
		//		Connection connVJdbc= DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/HSqlDb", "sa", "");
	}

	Connection connVJdbc;

	@Before
	public void setUp() throws Exception {				
		connVJdbc = DriverManager.getConnection("jdbc:vjdbc:rmi://localhost:2000/VJdbc#testdb");
	}

	@After
	public void tearDown() throws Exception {
		connVJdbc.rollback();
		connVJdbc.close();
	}

	@Test
	public void test1_SimpleCreate() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		logger.info("Connecting to test db....and creating some tables");


		connVJdbc.setAutoCommit(false);
		createTestData(connVJdbc);
		connVJdbc.commit();


	}

	private void createTestData(Connection connVJdbc) throws SQLException {
		Statement stmt = connVJdbc.createStatement();
		dropTables(stmt, new String[] { "Address"});
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
		pstmt.executeBatch();
		logger.info("Batch insert of:"+NUMBER_OF_ADDRESSES+" addresses");
	}

	protected void compareResultSets(ResultSet rs1, ResultSet rs2) throws Exception {
		ResultSetMetaData rsmeta1 = rs1.getMetaData();
		ResultSetMetaData rsmeta2 = rs2.getMetaData();

		int columnCount1 = rsmeta1.getColumnCount();
		int columnCount2 = rsmeta2.getColumnCount();
		assertEquals(columnCount1, columnCount2);

		for(int i = 1; i <= columnCount1; i++) {
			assertEquals(rsmeta1.getColumnName(i), rsmeta2.getColumnName(i));
			assertEquals(rsmeta1.getColumnLabel(i), rsmeta2.getColumnLabel(i));
			if(rsmeta1.getColumnType(i) != Types.STRUCT) {
				assertEquals(rsmeta1.getColumnType(i), rsmeta2.getColumnType(i));
			}
		}

		boolean doLoop = true;

		while(doLoop) {
			boolean nextRow1 = rs1.next();
			boolean nextRow2 = rs2.next();

			assertEquals(nextRow1, nextRow2);

			if(nextRow1) {
				// First pass with column indizes
				for(int i = 1; i <= rsmeta1.getColumnCount(); i++) {
					Object obj1 = rs1.getObject(i);
					Object obj2 = rs2.getObject(i);

					if(obj1 != null || obj2 != null) {
						if(obj1 == null) {
							fail("ResultSets not equal");
						}
						if(obj2 == null) {
							fail("ResultSets not equal");
						}

						int columnType = rsmeta1.getColumnType(i);
						switch(columnType) {
						case Types.BINARY:
							byte[] barr1 = rs1.getBytes(i);
							byte[] barr2 = rs2.getBytes(i);
							assertEquals(barr1.length, barr2.length);
							for (int idx = 0; idx < barr1.length; ++idx) {
								assertEquals(barr1[idx], barr2[idx]);
							}
							break;
						case Types.CHAR:
						case Types.VARCHAR:
						case Types.LONGVARCHAR:
							String str1 = rs1.getString(i);
							String str2 = rs2.getString(i);
							assertEquals(str1, str2);
							assertTrue(Arrays.equals(
									StreamSerializer.toCharArray(rs1.getCharacterStream(i)),
									StreamSerializer.toCharArray(rs2.getCharacterStream(i)))
									);
							break;
						case Types.NCHAR:
						case Types.NVARCHAR:
						case Types.LONGNVARCHAR:
							str1 = rs1.getNString(i);
							str2 = rs2.getNString(i);
							assertEquals(str1, str2);
							assertTrue(Arrays.equals(
									StreamSerializer.toCharArray(rs1.getNCharacterStream(i)),
									StreamSerializer.toCharArray(rs2.getNCharacterStream(i)))
									);
							break;
						case Types.ARRAY:
							Object[] arr1 = (Object[])rs1.getArray(i).getArray();
							Object[] arr2 = (Object[])rs2.getArray(i).getArray();
							if(arr1.length == arr2.length) {
								for(int j = 0; j < arr1.length; j++) {
									// Oracle-specific: skip equality check if the nested
									// element is a SQL-Struct
									if(!Struct.class.isAssignableFrom(arr1[j].getClass())) {
										assertEquals(arr1[j], arr2[j]);
									}
								}
							} else {
								fail("Array-Size not equal !");
							}
							break;
						case Types.NUMERIC:
						case Types.DECIMAL:
							assertEquals(rs1.getBigDecimal(i), rs2.getBigDecimal(i));
							break;
						case Types.DATE:
							Date date1 = getCleanDate(rs1.getDate(i).getTime());
							Date date2 = rs2.getDate(i);
							if(!date1.equals(date2)) {
								String msg = "Date1 " + _dateTimeFormat.format(date1) + " != Date2 " + _dateTimeFormat.format(date2);
								fail(msg);
							}
							break;
						case Types.TIME:
							Time time1 = getCleanTime(rs1.getTime(i).getTime());
							// VJDBC already returns a clear time
							Time time2 = rs2.getTime(i);
							if(!time1.equals(time2)) {
								String msg = "Time1 " + _dateTimeFormat.format(time1) + " != Time2 " + _dateTimeFormat.format(time2);
								msg += "(" + time1.getTime() + " != " + time2.getTime() + ")";
								fail(msg);
							}
							break;
						case Types.TIMESTAMP:
							Timestamp ts1 = rs1.getTimestamp(i);
							Timestamp ts2 = rs2.getTimestamp(i);
							if(!ts1.equals(ts2)) {
								String msg = "Timestamp1 " + _dateTimeFormat.format(ts1) + " != Timestamp2 " + _dateTimeFormat.format(ts2);
								fail(msg);
							}
							break;
						case Types.BLOB:
							Blob blob1 = rs1.getBlob(i);
							Blob blob2 = rs2.getBlob(i);
							assertEquals(blob1.length(), blob2.length());
							assertTrue(Arrays.equals(blob1.getBytes(1, (int)blob1.length()), blob2.getBytes(1, (int)blob2.length())));
							break;
						case Types.CLOB:
							Clob clob1 = rs1.getClob(i);
							Clob clob2 = rs2.getClob(i);
							assertTrue(Arrays.equals(
									StreamSerializer.toCharArray(clob1.getCharacterStream(), (int)clob1.length()),
									StreamSerializer.toCharArray(clob2.getCharacterStream(), (int)clob2.length()))
									);
							assertEquals(clob1.getSubString(1, (int)clob1.length()), clob2.getSubString(1, (int)clob2.length()));
							break;
						case Types.NCLOB:
							NClob nclob1 = rs1.getNClob(i);
							NClob nclob2 = rs2.getNClob(i);
							assertTrue(Arrays.equals(
									StreamSerializer.toCharArray(nclob1.getCharacterStream(), (int)nclob1.length()),
									StreamSerializer.toCharArray(nclob2.getCharacterStream(), (int)nclob2.length()))
									);
							assertEquals(nclob1.getSubString(1, (int)nclob1.length()), nclob2.getSubString(1, (int)nclob2.length()));
							break;
						case Types.SQLXML:
							SQLXML xml1 = rs1.getSQLXML(i);
							SQLXML xml2 = rs2.getSQLXML(i);
							assertEquals(xml1.getString(), xml2.getString());
							break;
						default:
							if(obj1!= null && !obj1.equals(obj2)) {
								fail("ResultSets not equal: " + obj1.toString() + " != " + obj2);
							}else {
								fail("Bho GG");
							}
						}
					}
				}
			} else {
				doLoop = false;
			}
		}

		rs1.close();
		rs2.close();
	}

	protected void dropTables(Statement stmt, String[] tables) {
		for(int i = 0; i < tables.length; i++) {
			System.out.println("... drop table " + tables[i]);
			try {
				stmt.executeUpdate("drop table " + tables[i]);
			} catch(SQLException e) {
				System.out.println("... doesn't exist");
			}
		}
	}

	protected Date getCleanDate(long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}

	protected Time getCleanTime(long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		cal.set(Calendar.YEAR, 1970);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DATE, 1);
		return new Time(cal.getTimeInMillis());
	}

}
