// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DoubleParameter implements PreparedStatementParameter {
    static final long serialVersionUID = -8304299062026994797L;

    private double _value;
    
    public DoubleParameter() {
    }

    public DoubleParameter(double value) {
        _value = value;
    }
    
    public double getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = in.readDouble();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(_value);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setDouble(index, _value);
    }

    public String toString() {
        return "double: " + _value;
    }
}
