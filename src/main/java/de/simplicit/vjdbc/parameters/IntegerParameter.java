// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntegerParameter implements PreparedStatementParameter {
    static final long serialVersionUID = 7906650418670821329L;

    private int _value;
    
    public IntegerParameter() {
    }

    public IntegerParameter(int value) {
        _value = value;
    }
    
    public int getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_value);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setInt(index, _value);
    }

    public String toString() {
        return "int: " + _value;
    }
}
