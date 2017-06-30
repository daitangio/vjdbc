// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.server.command.ResultSetHolder;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;

public class ResultSetGetMetaDataCommand implements Command {
    private static final long serialVersionUID = 3258411737794558008L;

    public ResultSetGetMetaDataCommand() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        ResultSetHolder rsh = (ResultSetHolder)target;
        return rsh.getMetaData();
    }

    public String toString() {
        return "ResultSetGetMetaDataCommand";
    }
}
