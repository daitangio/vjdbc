// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PingCommand implements Command {
    static final long serialVersionUID = 3340327873423851L;

    private static Logger _logger = Logger.getLogger(PingCommand.class.getName());

    public PingCommand() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        if(_logger.isLoggable(Level.FINE)) {
            _logger.fine("Keep alive ping ...");
        }
        return null;
    }

    public String toString() {
        return "PingCommand";
    }
}
