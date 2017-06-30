// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

public class TimeParameter implements PreparedStatementParameter {
    static final long serialVersionUID = -3833958578075965947L;

    private Time _value;
    private Calendar _calendar;

    public TimeParameter() {
    }
    
    public TimeParameter(Time value, Calendar cal) {
        _value = value;
        _calendar = cal;
    }
    
    public Time getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = (Time)in.readObject();
        _calendar = (Calendar)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_value);
        out.writeObject(_calendar);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        if(_calendar == null) {
            pstmt.setTime(index, _value);
        } else {
            pstmt.setTime(index, _value, _calendar);
        }
    }

    public String toString() {
        return "Time: " + _value;
    }
}
