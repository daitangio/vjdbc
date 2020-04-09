// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.UIDEx;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DestroyCommand implements Command {
    static final long serialVersionUID = 4457392123395584636L;

	private static Logger _logger = Logger.getLogger(DestroyCommand.class.getName());
    private Long _uid;
    private int _interfaceType;

    public DestroyCommand() {
    }

    public DestroyCommand(UIDEx regentry, int interfaceType) {
        _uid = regentry.getUID();
        _interfaceType = interfaceType;
    }

    public DestroyCommand(Long uid, int interfaceType) {
    	this._uid = uid;
    	this._interfaceType = interfaceType;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(_uid.longValue());
        out.writeInt(_interfaceType);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _uid = new Long(in.readLong());
        _interfaceType = in.readInt();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
    	/*
    	 * if we are trying to close a Connection, we also need to close all the other associated
    	 * JDBC objects, such as ResultSet, Statements, etc.
    	 */
    	if(target instanceof Connection) {
    		if(_logger.isLoggable(Level.FINE)) {
    			_logger.fine("******************************************************");
    			_logger.fine("Destroy command for Connection found!");
    			_logger.fine("destroying and closing all related JDBC objects first.");
    			_logger.fine("******************************************************");
    		}
    		ctx.closeAllRelatedJdbcObjects();
    	}
    	// now we are ready to go on and close this connection
    	
        Object removed = ctx.removeJDBCObject(_uid);

        // Check for identity
        if(removed == target) {
            if(_logger.isLoggable(Level.FINE)) {
                _logger.fine("Removed " + target.getClass().getName() + " with UID " + _uid);
            }
            try {
                Class targetClass = JdbcInterfaceType._interfaces[_interfaceType];
                Method mth = targetClass.getDeclaredMethod("close", new Class[0]);
                mth.invoke(target, (Object[])null);
                if(_logger.isLoggable(Level.FINE)) {
                    _logger.fine("Invoked close() successfully");
                }
            } catch(NoSuchMethodException e) {
                // Object doesn't support close()
            	if(_logger.isLoggable(Level.FINE)) {
            		_logger.fine("close() not supported");
            	}
            } catch(Exception e) {
                _logger.severe("Invocation of close() failed");
                e.printStackTrace();
            }
        } else {
            if(_logger.isLoggable(Level.FINE)) {
                _logger.fine("Target object " + target + " wasn't registered with UID " + _uid);
            }
        }
        return null;
    }

    public String toString() {
        return "DestroyCommand";
    }
}
