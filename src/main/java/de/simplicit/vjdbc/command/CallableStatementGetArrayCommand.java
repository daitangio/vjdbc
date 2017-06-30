// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.SerialArray;
import de.simplicit.vjdbc.serial.SerializableTransport;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class CallableStatementGetArrayCommand implements Command {
    static final long serialVersionUID = 4247967467888689853L;

    private int _index;
    private String _parameterName;

    // No-Arg constructor for deserialization
    public CallableStatementGetArrayCommand() {
    }

    public CallableStatementGetArrayCommand(int index) {
        _index = index;
    }

    public CallableStatementGetArrayCommand(String paramName) {
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
        Array result;
        if(_parameterName != null) {
            result = cstmt.getArray(_parameterName);
        } else {
            result = cstmt.getArray(_index);
        }
        return new SerializableTransport(new SerialArray(result), ctx.getCompressionMode(), ctx.getCompressionThreshold());
    }

    public String toString() {
        return "CallableStatementGetArrayCommand";
    }
}
