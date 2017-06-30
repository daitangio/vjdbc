// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NStringParameter implements PreparedStatementParameter {
    private static final long serialVersionUID = 3458761646106361472L;
    
    private String _value;
    
    public NStringParameter() {
    }

    public NStringParameter(String value) {
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
        pstmt.setNString(index, _value);
    }

    public String toString() {
        return "NString: " + _value;
    }
}
