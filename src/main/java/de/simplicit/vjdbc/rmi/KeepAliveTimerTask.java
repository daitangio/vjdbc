// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.rmi;

import de.simplicit.vjdbc.command.Command;
import de.simplicit.vjdbc.command.CommandSinkListener;
import de.simplicit.vjdbc.command.DecoratedCommandSink;
import de.simplicit.vjdbc.command.PingCommand;

import java.sql.SQLException;
import java.util.TimerTask;

/**
 * This timer task will periodically notify the server with a dummy command, just to
 * keep the connection alive. This will prevent the RMI-Object to be garbage-collected when
 * there aren't any RMI-Calls for a specific time (lease value).
 */
public class KeepAliveTimerTask extends TimerTask implements CommandSinkListener {
    private static Command _dummyCommand = new PingCommand();
    private DecoratedCommandSink _sink;
    private boolean _ignoreNextPing = false;

    public KeepAliveTimerTask(DecoratedCommandSink sink) {
        _sink = sink;
        _sink.setListener(this);
    }

    public void preExecution(Command cmd) {
        // Next ping can be ignored when there are commands processed
        // to the sink
        _ignoreNextPing = true;
    }

    public void postExecution(Command cmd) {
    }

    public void run() {
        try {
            if(_ignoreNextPing) {
                _ignoreNextPing = false;
            } else {
                _sink.process(null, _dummyCommand);
            }
        } catch(SQLException e) {
            // Ignore it, sink is already closed
        }
    }
}
