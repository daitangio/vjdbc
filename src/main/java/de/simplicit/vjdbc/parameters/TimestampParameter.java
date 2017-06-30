// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class TimestampParameter implements PreparedStatementParameter {
    static final long serialVersionUID = -3786979212713144035L;

    private Timestamp _value;
    private Calendar _calendar;
    
    public TimestampParameter() {
    }

    public TimestampParameter(Timestamp value, Calendar cal) {
        _value = value;
        _calendar = cal;
    }
    
    public Timestamp getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = (Timestamp)in.readObject();
        _calendar = (Calendar)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_value);
        out.writeObject(_calendar);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        if(_calendar == null) {
            pstmt.setTimestamp(index, _value);
        } else {
            pstmt.setTimestamp(index, _value, _calendar);
        }
    }

    public String toString() {
        return "Timestamp: " + _value;
    }
}
