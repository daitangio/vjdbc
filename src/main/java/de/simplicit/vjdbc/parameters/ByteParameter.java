// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ByteParameter implements PreparedStatementParameter {
    static final long serialVersionUID = -6844809323174032034L;

    private byte _value;
    
    public ByteParameter() {
    }

    public ByteParameter(byte value) {
        _value = value;
    }

    public byte getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = in.readByte();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(_value);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setByte(index, _value);
    }

    public String toString() {
        return "byte: " + _value;
    }
}
