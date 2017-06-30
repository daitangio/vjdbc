// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ByteArrayParameter implements PreparedStatementParameter {
    static final long serialVersionUID = -850921577178865335L;

    private byte[] _value;
    
    public ByteArrayParameter() {
    }

    public ByteArrayParameter(byte[] value) {
        _value = value;
    }
    
    public byte[] getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = (byte[])in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_value);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setBytes(index, _value);
    }

    public String toString() {
        return "byte[]: " + _value.length + " bytes";
    }
}
