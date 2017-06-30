// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import de.simplicit.vjdbc.serial.SerialArray;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArrayParameter implements PreparedStatementParameter {
    static final long serialVersionUID = 82417815012404533L;

    private SerialArray _value;

    public ArrayParameter() {
    }
    
    public ArrayParameter(Array value) throws SQLException {
        _value = new SerialArray(value);
    }
    
    public SerialArray getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = (SerialArray)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_value);
    }    

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setArray(index, _value);
    }

    public String toString() {
        return "Array: " + _value;
    }
}
