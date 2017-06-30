// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc;

import de.simplicit.vjdbc.command.CommandPool;
import de.simplicit.vjdbc.command.DecoratedCommandSink;
import de.simplicit.vjdbc.command.JdbcInterfaceType;
import de.simplicit.vjdbc.serial.UIDEx;

import java.sql.SQLException;
import java.sql.Savepoint;

public class VirtualSavepoint extends VirtualBase implements Savepoint {
    VirtualSavepoint(UIDEx reg, DecoratedCommandSink sink) {
        super(reg, sink);
    }

    public int getSavepointId() throws SQLException {
        return _sink.processWithIntResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.SAVEPOINT,"getSavepointId"));
    }

    public String getSavepointName() throws SQLException {
        return (String)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.SAVEPOINT,"getSavepointName"));
    }
}
