// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementUpdateCommand implements Command {
    private static final long serialVersionUID = 3689069560279937335L;

    private String _sql;

    public StatementUpdateCommand() {
    }

    public StatementUpdateCommand(String sql) {
        _sql = sql;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(_sql);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _sql = in.readUTF();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        return new Integer(((Statement) target).executeUpdate(ctx.resolveOrCheckQuery(_sql)));
    }

    public String toString() {
        return "StatementUpdateCommand: " + _sql;
    }
}
