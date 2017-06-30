// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.server.command.ResultSetHolder;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;

public class NextRowPacketCommand implements Command {
    static final long serialVersionUID = -8463588846424302034L;

    public NextRowPacketCommand() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        ResultSetHolder rsh = (ResultSetHolder) target;
        // Return next serialized RowPacket
        return rsh.nextRowPacket();
    }

    public String toString() {
        return "NextRowPacketCommand";
    }
}
