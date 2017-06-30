// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementGetGeneratedKeysCommand implements Command, ResultSetProducerCommand {
    static final long serialVersionUID = -6529413105195105196L;

    public StatementGetGeneratedKeysCommand() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public int getResultSetType() {
        return ResultSet.TYPE_SCROLL_INSENSITIVE;
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        return ((Statement)target).getGeneratedKeys();
    }

    public String toString() {
        return "StatementGetGeneratedKeysCommand";
    }
}
