// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.SerializableTransport;
import de.simplicit.vjdbc.serial.StreamSerializer;

import java.io.*;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class CallableStatementGetCharacterStreamCommand implements Command {
    static final long serialVersionUID = 3594832624574651235L;

    private int _index;
    private String _parameterName;

    public CallableStatementGetCharacterStreamCommand() {
    }

    public CallableStatementGetCharacterStreamCommand(int index) {
        _index = index;
    }

    public CallableStatementGetCharacterStreamCommand(String paramName) {
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
            result = cstmt.getCharacterStream(_parameterName);
        } else {
            result = cstmt.getCharacterStream(_index);
        }
        try {
            // read reader and return as a char[]
            return new SerializableTransport(StreamSerializer.toCharArray(result), ctx.getCompressionMode(), ctx.getCompressionThreshold());
        } catch (IOException ioe) {
            throw new SQLException(ioe);
        }
    }

    public String toString() {
        return "CallableStatementGetCharacterStreamCommand";
    }
}
