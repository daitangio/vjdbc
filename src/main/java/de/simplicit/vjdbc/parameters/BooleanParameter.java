// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BooleanParameter implements PreparedStatementParameter {
    static final long serialVersionUID = 1915488329736405680L;

    private boolean _value;
    
    public BooleanParameter() {
    }

    public BooleanParameter(boolean value) {
        _value = value;
    }
    
    public boolean getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(_value);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setBoolean(index, _value);
    }

    public String toString() {
        return "boolean: " + (_value ? "true" : "false");
    }
}
