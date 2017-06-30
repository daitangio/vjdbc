// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.parameters.PreparedStatementParameter;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementExecuteCommand implements Command {
    static final long serialVersionUID = 8987200111317750567L;

    protected PreparedStatementParameter[] _params;

    public PreparedStatementExecuteCommand() {
    }

    public PreparedStatementExecuteCommand(PreparedStatementParameter[] params) {
        _params = params;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_params);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _params = (PreparedStatementParameter[])in.readObject();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        PreparedStatement pstmt = (PreparedStatement)target;
        for(int i = 0; i < _params.length; i++) {
            if(_params[i] != null) {
                _params[i].setParameter(pstmt, i + 1);
            }
        }
        return pstmt.execute() ? Boolean.TRUE : Boolean.FALSE;
    }

    public String toString() {
        return "PreparedStatementExecuteCommand";
    }
}
