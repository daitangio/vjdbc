// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;

public class SerialStruct implements Struct, Externalizable {
    private static final long serialVersionUID = 3256444694312792625L;

    private String _sqlTypeName;
    private Object[] _attributes;

    public SerialStruct() {
    }

    public SerialStruct(String typeName, Object[] attributes) {
        _sqlTypeName = typeName;
        _attributes = attributes;
    }

    public SerialStruct(Struct struct) throws SQLException {
        _sqlTypeName = struct.getSQLTypeName();
        _attributes = struct.getAttributes();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_sqlTypeName);
        out.writeObject(_attributes);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _sqlTypeName = (String)in.readObject();
        _attributes = (Object[])in.readObject();
    }

    public String getSQLTypeName() throws SQLException {
        return _sqlTypeName;
    }

    public Object[] getAttributes() throws SQLException {
        return _attributes;
    }

    public Object[] getAttributes(Map map) throws SQLException {
        throw new UnsupportedOperationException("getAttributes(Map)");
    }
}
