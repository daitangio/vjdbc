// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.StreamSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.RowId;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class CallableStatementSetRowIdCommand implements Command {
    static final long serialVersionUID = -2847792562974087927L;

    private int _index;
    private String _parameterName;
    private RowId rowId;

    public CallableStatementSetRowIdCommand() {
    }

    public CallableStatementSetRowIdCommand(int index, RowId rowId) throws IOException {
        _index = index;
        this.rowId = rowId;
    }

    public CallableStatementSetRowIdCommand(String paramName, RowId rowId) throws IOException {
        _parameterName = paramName;
        this.rowId = rowId;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_index);
        out.writeUTF(_parameterName);
        out.writeObject(rowId);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _index = in.readInt();
        _parameterName = in.readUTF();
        rowId = (RowId)in.readObject();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        CallableStatement cstmt = (CallableStatement)target;
        if(_parameterName != null) {
            cstmt.setRowId(_parameterName, rowId);
        } else {
            cstmt.setRowId(_index, rowId);
        }

        return null;
    }

    public String toString() {
        return "CallableStatementSetRowIdCommand";
    }
}
