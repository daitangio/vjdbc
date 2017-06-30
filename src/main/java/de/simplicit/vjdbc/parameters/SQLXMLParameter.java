// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.parameters;

import de.simplicit.vjdbc.serial.SerialSQLXML;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.PreparedStatement;
import java.sql.SQLXML;
import java.sql.SQLException;

public class SQLXMLParameter implements PreparedStatementParameter {
    static final long serialVersionUID = 8647675527971168478L;

    private SerialSQLXML _value;

    public SQLXMLParameter() {
    }

    public SQLXMLParameter(SQLXML value) throws SQLException {
        _value = new SerialSQLXML(value);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _value = (SerialSQLXML)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_value);
    }

    public void setParameter(PreparedStatement pstmt, int index) throws SQLException {
        pstmt.setSQLXML(index, _value);
    }

    public String toString() {
        try {
            return "SQLXML: " + _value.getString();
        } catch (SQLException sqle) {
        }
        return "SQLXML: fail";
    }
}
