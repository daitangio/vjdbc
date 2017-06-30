// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.SerializableTransport;
import de.simplicit.vjdbc.util.SQLExceptionHelper;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class CallableStatementSetObjectCommand implements Command {
    static final long serialVersionUID = -9132697894345849726L;

    private int _index;
    private String _paramName;
    private Integer _targetSqlType;
    private Integer _scale;
    private SerializableTransport _transport;

    public CallableStatementSetObjectCommand() {
    }

    public CallableStatementSetObjectCommand(int index, Integer targetSqlType, Integer scale) {
        _index = index;
        _targetSqlType = targetSqlType;
        _scale = scale;
        _transport = null;
    }

    public CallableStatementSetObjectCommand(String paramName, Integer targetSqlType, Integer scale) {
        _paramName = paramName;
        _targetSqlType = targetSqlType;
        _scale = scale;
        _transport = null;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_index);
        out.writeObject(_paramName);
        out.writeObject(_targetSqlType);
        out.writeObject(_scale);
        out.writeObject(_transport);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _index = in.readInt();
        _paramName = (String)in.readObject();
        _targetSqlType = (Integer)in.readObject();
        _scale = (Integer)in.readObject();
        _transport = (SerializableTransport)in.readObject();
    }

    public void setObject(Object obj) throws SQLException {
        if(obj instanceof Serializable) {
            _transport = new SerializableTransport(obj);
        } else {
            throw new SQLException("Object of type " + obj.getClass().getName() + " is not serializable");
        }
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        CallableStatement cstmt = (CallableStatement)target;

        Object obj;
        try {
            obj = _transport.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }

        if(_paramName != null) {
            if(_targetSqlType != null) {
                if(_scale != null) {
                    cstmt.setObject(_paramName, obj, _targetSqlType.intValue(), _scale.intValue());
                } else {
                    cstmt.setObject(_paramName, obj, _targetSqlType.intValue());
                }
            } else {
                cstmt.setObject(_paramName, obj);
            }
        } else {
            if(_targetSqlType != null) {
                if(_scale != null) {
                    cstmt.setObject(_index, obj, _targetSqlType.intValue(), _scale.intValue());
                } else {
                    cstmt.setObject(_index, obj, _targetSqlType.intValue());
                }
            } else {
                cstmt.setObject(_index, obj);
            }
        }

        return null;
    }

    public String toString() {
        return "CallableStatementSetObjectCommand";
    }
}
