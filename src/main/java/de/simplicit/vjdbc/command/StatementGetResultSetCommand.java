// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementGetResultSetCommand implements Command {
    static final long serialVersionUID = 972478509574280291L;

    public StatementGetResultSetCommand() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        return ((Statement)target).getResultSet();
    }

    public String toString() {
        return "StatementGetResultSetCommand";
    }
}
