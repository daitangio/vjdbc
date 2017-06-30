// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.command;

import de.simplicit.vjdbc.serial.CallingContext;

class JdbcObjectHolder {
    private Object _jdbcObject;
    private CallingContext _callingContext;
    private int _jdbcInterfaceType;
    
    JdbcObjectHolder(Object jdbcObject, CallingContext ctx, int _jdbcInterfaceType) {
        this._jdbcObject = jdbcObject;
        this._callingContext = ctx;
        this._jdbcInterfaceType = _jdbcInterfaceType;
    }
    
    CallingContext getCallingContext() {
        return this._callingContext;
    }

    Object getJdbcObject() {
        return this._jdbcObject;
    }
    
    int getJdbcInterfaceType() {
    	return this._jdbcInterfaceType;
    }
}
