// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

public class DateParameter implements PreparedStatementParameter {
    static final long serialVersionUID = 5153278906714835319L;

    private Date _value;
    private Calendar _calendar;
    
    public DateParameter() {
    }

    public DateParameter(Date value, Calendar cal) {
        _value = value;
        _calendar = cal;
    }

    public Date getValue() {
        return _value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = (Date)in.readObject();
        _calendar = (Calendar)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_value);
        out.writeObject(_calendar);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        if(_calendar == null) {
            pstmt.setDate(index, _value);
        } else {
            pstmt.setDate(index, _value, _calendar);
        }
    }

    public String toString() {
        return "Date: " + _value;
    }
}
