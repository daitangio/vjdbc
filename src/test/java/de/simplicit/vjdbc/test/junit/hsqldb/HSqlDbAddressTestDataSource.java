// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.test.junit.hsqldb;

import de.simplicit.vjdbc.test.junit.VJdbcTest;
import de.simplicit.vjdbc.test.junit.general.AddressTest;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;

public class HSqlDbAddressTestDataSource extends AddressTest {



	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite();

		VJdbcTest.addAllTestMethods(suite, HSqlDbAddressTestDataSource.class);

		TestSetup wrapper = new TestSetup(suite) {
			@Override
			protected void setUp() throws Exception {
				new HSqlDbAddressTestDataSource("").oneTimeSetup();
			}

			@Override
			protected void tearDown() throws Exception {
				new HSqlDbAddressTestDataSource("").oneTimeTearDown();
			}
		};

		return wrapper;
	}

	public HSqlDbAddressTestDataSource(String s) {
		super(s);
	}

	@Override
	protected Connection createNativeDatabaseConnection() throws Exception {
		Class.forName("org.hsqldb.jdbcDriver");
		// GG return DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/HSqlDb", "sa", "");
		return DriverManager.getConnection("jdbc:hsqldb:.", "sa", "");
	}

	@Override
	protected String getVJdbcDatabaseShortcut() {
		return "HSqlDB-DataSource";
	}
}
