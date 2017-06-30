// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NullParameter implements PreparedStatementParameter {
    static final long serialVersionUID = 2061806736191837513L;

    private int _sqlType;
    private String _typeName;
    
    public NullParameter() {
    }

    public NullParameter(int sqltype, String typename) {
        _sqlType = sqltype;
        _typeName = typename;
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _sqlType = in.readInt();
        _typeName = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_sqlType);
        out.writeObject(_typeName);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        if(_typeName == null) {
            pstmt.setNull(index, _sqlType);
        } else {
            pstmt.setNull(index, _sqlType, _typeName);
        }
    }

    public String toString() {
        return "Null, SQL-Type: " + _sqlType;
    }
}
