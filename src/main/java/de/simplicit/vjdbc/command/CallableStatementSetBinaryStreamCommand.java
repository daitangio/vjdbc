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

public class CallableStatementSetBinaryStreamCommand implements Command {
    static final long serialVersionUID = 4264932633701227941L;

    private int _index;
    private int _length;
    private String _parameterName;
    private byte[] _byteArray;

    public CallableStatementSetBinaryStreamCommand() {
    }

    public CallableStatementSetBinaryStreamCommand(int index, InputStream is) throws IOException {
        _index = index;
        _byteArray = StreamSerializer.toByteArray(is);
        _length = _byteArray.length;
    }

    public CallableStatementSetBinaryStreamCommand(String paramName, InputStream is) throws IOException {
        _parameterName = paramName;
        _byteArray = StreamSerializer.toByteArray(is);
        _length = _byteArray.length;
    }

    public CallableStatementSetBinaryStreamCommand(int index, InputStream is, int len) throws IOException {
        _index = index;
        _byteArray = StreamSerializer.toByteArray(is);
        _length = len;
    }

    public CallableStatementSetBinaryStreamCommand(String paramName, InputStream is, int len) throws IOException {
        _parameterName = paramName;
        _byteArray = StreamSerializer.toByteArray(is);
        _length = len;
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
            cstmt.setBinaryStream(_parameterName, is, _length);
        } else {
            cstmt.setBinaryStream(_index, is, _length);
        }

        return null;
    }

    public String toString() {
        return "CallableStatementSetBinaryStreamCommand";
    }
}
