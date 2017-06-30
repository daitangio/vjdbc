// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.StreamSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLXML;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class CallableStatementSetSQLXMLCommand implements Command {
    static final long serialVersionUID = 7396654168665073844L;

    private int _index;
    private String _parameterName;
    private SQLXML sqlxml;

    public CallableStatementSetSQLXMLCommand() {
    }

    public CallableStatementSetSQLXMLCommand(int index, SQLXML sqlxml) throws IOException {
        _index = index;
        this.sqlxml = sqlxml;
    }

    public CallableStatementSetSQLXMLCommand(String paramName, SQLXML sqlxml) throws IOException {
        _parameterName = paramName;
        this.sqlxml = sqlxml;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_index);
        out.writeUTF(_parameterName);
        out.writeObject(sqlxml);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _index = in.readInt();
        _parameterName = in.readUTF();
        sqlxml = (SQLXML)in.readObject();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        CallableStatement cstmt = (CallableStatement)target;
        if(_parameterName != null) {
            cstmt.setSQLXML(_parameterName, sqlxml);
        } else {
            cstmt.setSQLXML(_index, sqlxml);
        }

        return null;
    }

    public String toString() {
        return "CallableStatementSetSQLXMLCommand";
    }
}
