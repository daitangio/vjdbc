// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import de.simplicit.vjdbc.serial.SerialBlob;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlobParameter implements PreparedStatementParameter {
    static final long serialVersionUID = 7120087686097706094L;

    private SerialBlob _value;

    public BlobParameter() {
    }
    
    public BlobParameter(Blob value) throws SQLException {
        _value = new SerialBlob(value);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = (SerialBlob)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_value);
    }    

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setBlob(index, _value);
    }

    public String toString() {
        return "Blob: " + _value;
    }
}
