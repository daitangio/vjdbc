// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FloatParameter implements PreparedStatementParameter {
    static final long serialVersionUID = -2273786408954216402L;

    private float _value;

    public FloatParameter() {
    }
    
    public FloatParameter(float value) {
        _value = value;
    }
    
    public float getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = in.readFloat();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(_value);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setFloat(index, _value);
    }

    public String toString() {
        return "float: " + _value;
    }
}
