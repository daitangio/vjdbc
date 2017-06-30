// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementCancelCommand implements Command {  
    private static final long serialVersionUID = 5602747945115861740L;

    public StatementCancelCommand() {
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        ((Statement)target).cancel();
        return null;
    }

    public String toString() {
        return "StatementCancelCommand";
    }
}
