// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringParameter implements PreparedStatementParameter {
    static final long serialVersionUID = -8131525145406357230L;

    private String _value;
    
    public StringParameter() {
    }

    public StringParameter(String value) {
        _value = value;
    }
    
    public String getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_value);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setString(index, _value);
    }

    public String toString() {
        return "String: " + _value;
    }
}
