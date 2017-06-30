// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.rmi;

import de.simplicit.vjdbc.command.Command;
import de.simplicit.vjdbc.serial.CallingContext;
import de.simplicit.vjdbc.serial.UIDEx;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Properties;

public interface CommandSinkRmi extends Remote {
    UIDEx connect(String url, Properties props, Properties clientInfo, CallingContext ctx) throws SQLException, RemoteException;

    Object process(Long connuid, Long uid, Command cmd, CallingContext ctx) throws SQLException, RemoteException;
}
