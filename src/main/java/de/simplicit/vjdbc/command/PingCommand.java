// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;

public class PingCommand implements Command {
    static final long serialVersionUID = 3340327873423851L;

    private static Log _logger = LogFactory.getLog(PingCommand.class);

    public PingCommand() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        if(_logger.isDebugEnabled()) {
            _logger.debug("Keep alive ping ...");
        }
        return null;
    }

    public String toString() {
        return "PingCommand";
    }
}
