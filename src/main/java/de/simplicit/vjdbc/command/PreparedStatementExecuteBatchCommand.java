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
import java.util.List;

public class PreparedStatementExecuteBatchCommand implements Command {
    static final long serialVersionUID = 2439854950000135145L;

    private List _batchCommands;

    public PreparedStatementExecuteBatchCommand() {
    }

    public PreparedStatementExecuteBatchCommand(List batches) {
        _batchCommands = batches;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_batchCommands);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _batchCommands = (List)in.readObject();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        PreparedStatement pstmt = (PreparedStatement)target;
        pstmt.clearBatch();

        for(int i = 0, n = _batchCommands.size(); i < n; i++) {
            PreparedStatementParameter[] parms = (PreparedStatementParameter[])_batchCommands.get(i);
            for(int j = 0; j < parms.length; j++) {
                parms[j].setParameter(pstmt, j + 1);
            }
            pstmt.addBatch();
        }

        return pstmt.executeBatch();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("PreparedStatementExecuteBatchCommand\n");

        for(int i = 0; i < _batchCommands.size(); i++) {
            PreparedStatementParameter[] preparedStatementParameters = (PreparedStatementParameter[])_batchCommands.get(i);

            sb.append("Parameter-Set ").append(i).append(":\n");

            for(int j = 0; j < preparedStatementParameters.length; j++) {
                PreparedStatementParameter preparedStatementParameter = preparedStatementParameters[j];
                sb.append("\t[").append(j).append("] = ").append(preparedStatementParameter.toString()).append("\n");
            }
        }

        return sb.toString();
    }
}
