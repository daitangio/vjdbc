// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ObjectParameter implements PreparedStatementParameter {
    static final long serialVersionUID = -9065375715201787003L;

    private Object _value;
    private Integer _targetSqlType;
    private Integer _scale;
    
    public ObjectParameter() {
    }

    public ObjectParameter(Object value, Integer targetSqlType, Integer scale) {
        _value = value;
        _targetSqlType = targetSqlType;
        _scale = scale;
    }
    
    public Object getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = in.readObject();
        _targetSqlType = (Integer)in.readObject();
        _scale = (Integer)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_value);
        out.writeObject(_targetSqlType);
        out.writeObject(_scale);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        if(_scale == null) {
            if(_targetSqlType == null) {
                pstmt.setObject(index, _value);
            } else {
                pstmt.setObject(index, _value, _targetSqlType.intValue());
            }
        } else {
            pstmt.setObject(index, _value, _targetSqlType.intValue(), _scale.intValue());
        }
    }

    public String toString() {
        return "Object: " + _value;
    }
}
