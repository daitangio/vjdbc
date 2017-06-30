// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.CallingContext;
import de.simplicit.vjdbc.serial.UIDEx;

import java.sql.SQLException;
import java.util.Properties;

/**
 * Interface which each CommandSink must implement to be used for
 * VJDBC client-server communication.
 */
public interface CommandSink {
    UIDEx connect(String database, Properties props, Properties clientInfo, CallingContext ctx) throws SQLException;

    Object process(Long connuid, Long uid, Command cmd, CallingContext ctx) throws SQLException;
    
    void close();
}
