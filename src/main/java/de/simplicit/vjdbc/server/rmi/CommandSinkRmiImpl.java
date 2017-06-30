// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.sql.SQLException;
import java.util.Properties;

import de.simplicit.vjdbc.command.Command;
import de.simplicit.vjdbc.rmi.CommandSinkRmi;
import de.simplicit.vjdbc.serial.CallingContext;
import de.simplicit.vjdbc.serial.UIDEx;
import de.simplicit.vjdbc.server.command.CommandProcessor;

public class CommandSinkRmiImpl extends UnicastRemoteObject implements CommandSinkRmi, Unreferenced {
    private static final long serialVersionUID = 3257566187649840185L;
    
    private CommandProcessor _processor;

    public CommandSinkRmiImpl(int remotingPort) throws RemoteException {
        super(remotingPort);
        _processor = CommandProcessor.getInstance();
    }

    public void unreferenced() {
    }

    public UIDEx connect(String url, Properties props, Properties clientInfo, CallingContext ctx) throws SQLException, RemoteException {
        return _processor.createConnection(url, props, clientInfo, ctx);
    }

    public Object process(Long connuid, Long uid, Command cmd, CallingContext ctx) throws SQLException, RemoteException {
        return _processor.process(connuid, uid, cmd, ctx);
    }
}
