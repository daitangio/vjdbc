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

public class PreparedStatementQueryCommand implements Command, ResultSetProducerCommand {
    static final long serialVersionUID = -7028150330288724130L;

    protected PreparedStatementParameter[] _params;
    protected int _resultSetType;

    public PreparedStatementQueryCommand() {
    }

    public PreparedStatementQueryCommand(PreparedStatementParameter[] params, int resultSetType) {
        _params = params;
        _resultSetType = resultSetType;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_resultSetType);
        out.writeObject(_params);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _resultSetType = in.readInt();
        _params = (PreparedStatementParameter[])in.readObject();
    }

    public int getResultSetType() {
        return _resultSetType;
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        PreparedStatement pstmt = (PreparedStatement)target;
        for(int i = 0; i < _params.length; i++) {
            _params[i].setParameter(pstmt, i + 1);
        }
        return pstmt.executeQuery();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PreparedStatementQueryCommand");
        if(_params != null && _params.length > 0) {
            sb.append(" with parameters\n");
            for(int i = 0, n = _params.length; i < n; i++) {
                sb.append("\t[").append(i + 1).append("] = ").append(_params[i]);
                if(i < n - 1) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
}
