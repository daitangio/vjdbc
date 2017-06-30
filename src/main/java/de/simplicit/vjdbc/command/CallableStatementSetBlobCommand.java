// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.StreamSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class CallableStatementSetBlobCommand implements Command {
    static final long serialVersionUID = 4264932633701227941L;

    private int _index;
    private String _parameterName;
    private Blob clob;

    public CallableStatementSetBlobCommand() {
    }

    public CallableStatementSetBlobCommand(int index, Blob clob) throws IOException {
        _index = index;
        this.clob = clob;
    }

    public CallableStatementSetBlobCommand(String paramName, Blob clob) throws IOException {
        _parameterName = paramName;
        this.clob = clob;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_index);
        out.writeUTF(_parameterName);
        out.writeObject(clob);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _index = in.readInt();
        _parameterName = in.readUTF();
        clob = (Blob)in.readObject();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        CallableStatement cstmt = (CallableStatement)target;
        if(_parameterName != null) {
            cstmt.setBlob(_parameterName, clob);
        } else {
            cstmt.setBlob(_index, clob);
        }

        return null;
    }

    public String toString() {
        return "CallableStatementSetBlobCommand";
    }
}
