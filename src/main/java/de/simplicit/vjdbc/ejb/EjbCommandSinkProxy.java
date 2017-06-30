// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.ejb;

import de.simplicit.vjdbc.command.Command;
import de.simplicit.vjdbc.command.CommandSink;
import de.simplicit.vjdbc.serial.CallingContext;
import de.simplicit.vjdbc.serial.UIDEx;
import de.simplicit.vjdbc.util.SQLExceptionHelper;

import java.sql.SQLException;
import java.util.Properties;
import javax.ejb.Remote;

@Remote
public interface EjbCommandSinkProxy extends CommandSink {

    public UIDEx connect(String url, Properties props, Properties clientInfo,
                         CallingContext ctx)
        throws SQLException;

    public Object process(Long connuid, Long uid, Command cmd,
                          CallingContext ctx)
        throws SQLException;

    public void close();
}
