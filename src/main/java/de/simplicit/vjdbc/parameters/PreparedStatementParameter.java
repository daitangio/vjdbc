// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.Externalizable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Interface for implementors of parameter transport classes.
 */
public interface PreparedStatementParameter extends Externalizable {
    void setParameter(PreparedStatement pstmt, int index) throws SQLException;
}
