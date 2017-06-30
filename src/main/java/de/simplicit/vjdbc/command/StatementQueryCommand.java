// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementQueryCommand implements Command, ResultSetProducerCommand {
    static final long serialVersionUID = -8463588846424302034L;

    private int _resultSetType;
    private String _sql;

    public StatementQueryCommand() {
    }

    public StatementQueryCommand(String sql, int resultSetType) {
        _sql = sql;
        _resultSetType = resultSetType;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_resultSetType);
        out.writeUTF(_sql);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _resultSetType = in.readInt();
        _sql = in.readUTF();
    }

    public int getResultSetType() {
        return _resultSetType;
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        return ((Statement) target).executeQuery(ctx.resolveOrCheckQuery(_sql));
    }

    public String toString() {
        return "StatementQueryCommand: " + _sql;
    }
}
