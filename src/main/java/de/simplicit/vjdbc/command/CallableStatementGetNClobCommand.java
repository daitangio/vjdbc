// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.SerialNClob;
import de.simplicit.vjdbc.serial.SerializableTransport;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.CallableStatement;
import java.sql.NClob;
import java.sql.SQLException;

public class CallableStatementGetNClobCommand implements Command {
    static final long serialVersionUID = 8230491873823084213L;

    private int _index;
    private String _parameterName;

    public CallableStatementGetNClobCommand() {
    }

    public CallableStatementGetNClobCommand(int index) {
        _index = index;
    }

    public CallableStatementGetNClobCommand(String paramName) {
        _parameterName = paramName;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_index);
        out.writeObject(_parameterName);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _index = in.readInt();
        _parameterName = (String)in.readObject();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        CallableStatement cstmt = (CallableStatement)target;
        NClob result;
        if(_parameterName != null) {
            result = cstmt.getNClob(_parameterName);
        } else {
            result = cstmt.getNClob(_index);
        }
        return new SerializableTransport(new SerialNClob(result), ctx.getCompressionMode(), ctx.getCompressionThreshold());
    }

    public String toString() {
        return "CallableStatementGetNClobCommand";
    }
}
