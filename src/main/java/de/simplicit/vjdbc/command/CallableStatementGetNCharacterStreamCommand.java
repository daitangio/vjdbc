// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.SerializableTransport;
import de.simplicit.vjdbc.serial.StreamSerializer;

import java.io.*;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class CallableStatementGetNCharacterStreamCommand implements Command {
    static final long serialVersionUID = -8218845136435435097L;

    private int _index;
    private String _parameterName;

    public CallableStatementGetNCharacterStreamCommand() {
    }

    public CallableStatementGetNCharacterStreamCommand(int index) {
        _index = index;
    }

    public CallableStatementGetNCharacterStreamCommand(String paramName) {
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
        Reader result;
        if(_parameterName != null) {
            result = cstmt.getNCharacterStream(_parameterName);
        } else {
            result = cstmt.getNCharacterStream(_index);
        }
        try {
            // read reader and return as a char[]
            return new SerializableTransport(StreamSerializer.toCharArray(result), ctx.getCompressionMode(), ctx.getCompressionThreshold());
        } catch (IOException ioe) {
            throw new SQLException(ioe);
        }
    }

    public String toString() {
        return "CallableStatementGetNCharacterStreamCommand";
    }
}
