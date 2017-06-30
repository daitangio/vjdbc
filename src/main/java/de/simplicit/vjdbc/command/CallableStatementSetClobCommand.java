// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.StreamSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Clob;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class CallableStatementSetClobCommand implements Command {
    static final long serialVersionUID = 4264932633701227941L;

    private int _index;
    private String _parameterName;
    private Clob clob;

    public CallableStatementSetClobCommand() {
    }

    public CallableStatementSetClobCommand(int index, Clob clob) throws IOException {
        _index = index;
        this.clob = clob;
    }

    public CallableStatementSetClobCommand(String paramName, Clob clob) throws IOException {
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
        clob = (Clob)in.readObject();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        CallableStatement cstmt = (CallableStatement)target;
        if(_parameterName != null) {
            cstmt.setClob(_parameterName, clob);
        } else {
            cstmt.setClob(_index, clob);
        }

        return null;
    }

    public String toString() {
        return "CallableStatementSetClobCommand";
    }
}
