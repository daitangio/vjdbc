// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class ConnectionRollbackWithSavepointCommand implements Command {
    static final long serialVersionUID = -5189425307111618293L;

    private Long _uidOfSavepoint;

    public ConnectionRollbackWithSavepointCommand() {
    }

    public ConnectionRollbackWithSavepointCommand(Long uidOfSavepoint) {
        _uidOfSavepoint = uidOfSavepoint;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(_uidOfSavepoint.longValue());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _uidOfSavepoint = new Long(in.readLong());
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        Savepoint sp = (Savepoint)ctx.getJDBCObject(_uidOfSavepoint);
        ((Connection)target).rollback(sp);
        return null;
    }

    public String toString() {
        return "ConnectionRollbackWithSavepointCommand";
    }
}
