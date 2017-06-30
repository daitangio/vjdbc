// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import de.simplicit.vjdbc.serial.SerialClob;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClobParameter implements PreparedStatementParameter {
    static final long serialVersionUID = -8231456859022053216L;

    private SerialClob _value;

    public ClobParameter() {
    }
    
    public ClobParameter(Clob value) throws SQLException {
        _value = new SerialClob(value);
    }
    
    public SerialClob getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = (SerialClob)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_value);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setClob(index, _value);
    }

    public String toString() {
        return "Clob: " + _value;
    }
}
