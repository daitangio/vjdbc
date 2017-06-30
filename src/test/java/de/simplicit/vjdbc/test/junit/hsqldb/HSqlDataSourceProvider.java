// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.test.junit.hsqldb;

import de.simplicit.vjdbc.server.DataSourceProvider;

import javax.sql.DataSource;
import java.sql.SQLException;

public class HSqlDataSourceProvider implements DataSourceProvider {
    public DataSource getDataSource() throws SQLException {
        return new HSqlDataSource();
    }
}
