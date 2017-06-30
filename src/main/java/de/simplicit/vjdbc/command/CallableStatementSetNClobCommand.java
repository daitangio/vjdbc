// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.StreamSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.NClob;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class CallableStatementSetNClobCommand implements Command {
    static final long serialVersionUID = 4264932633701227941L;

    private int _index;
    private String _parameterName;
    private NClob clob;

    public CallableStatementSetNClobCommand() {
    }

    public CallableStatementSetNClobCommand(int index, NClob clob) throws IOException {
        _index = index;
        this.clob = clob;
    }

    public CallableStatementSetNClobCommand(String paramName, NClob clob) throws IOException {
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
        clob = (NClob)in.readObject();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        CallableStatement cstmt = (CallableStatement)target;
        if(_parameterName != null) {
            cstmt.setNClob(_parameterName, clob);
        } else {
            cstmt.setNClob(_index, clob);
        }

        return null;
    }

    public String toString() {
        return "CallableStatementSetNClobCommand";
    }
}
