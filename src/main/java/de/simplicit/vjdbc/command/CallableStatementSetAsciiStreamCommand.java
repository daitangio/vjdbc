// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.StreamSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class CallableStatementSetAsciiStreamCommand implements Command {
    static final long serialVersionUID = -6772875360380241530L;

    private int _index;
    private int _length;
    private String _parameterName;
    private byte[] _byteArray;

    public CallableStatementSetAsciiStreamCommand() {
    }

    public CallableStatementSetAsciiStreamCommand(int index, InputStream is) throws IOException {
        _index = index;
        _byteArray = StreamSerializer.toByteArray(is);
        _length = _byteArray.length;
    }

    public CallableStatementSetAsciiStreamCommand(String paramName, InputStream is) throws IOException {
        _parameterName = paramName;
        _byteArray = StreamSerializer.toByteArray(is);
        _length = _byteArray.length;
    }

    public CallableStatementSetAsciiStreamCommand(int index, InputStream is, int len) throws IOException {
        _index = index;
        _length = len;
        _byteArray = StreamSerializer.toByteArray(is);
    }

    public CallableStatementSetAsciiStreamCommand(String paramName, InputStream is, int len) throws IOException {
        _parameterName = paramName;
        _length = len;
        _byteArray = StreamSerializer.toByteArray(is);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_index);
        out.writeInt(_length);
        out.writeObject(_parameterName);
        out.writeObject(_byteArray);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _index = in.readInt();
        _length = in.readInt();
        _parameterName = (String)in.readObject();
        _byteArray = (byte[])in.readObject();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        CallableStatement cstmt = (CallableStatement)target;
        InputStream is = StreamSerializer.toInputStream(_byteArray);
        if(_parameterName != null) {
            cstmt.setAsciiStream(_parameterName, is, _length);
        } else {
            cstmt.setAsciiStream(_index, is, _length);
        }

        return null;
    }

    public String toString() {
        return "CallableStatementSetAsciiStreamCommand";
    }
}
