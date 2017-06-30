// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.*;
import java.util.Map;

public class SerialArray implements Array, Externalizable {
    private static final long serialVersionUID = 3256722892212418873L;

    private int _baseType;
    private String _baseTypeName;
    private Object _array;

    public SerialArray() {
    }

    public SerialArray(int baseType, String typeName, Object[] elements) {
        _baseType = baseType;
        _baseTypeName = typeName;
        _array = elements;
    }

    public SerialArray(String typeName, Object[] elements) {

        if ("array".equalsIgnoreCase(typeName)) {
            _baseType = Types.ARRAY;
        } else if ("bigint".equalsIgnoreCase(typeName)) {
            _baseType = Types.BIGINT;
        } else if ("binary".equalsIgnoreCase(typeName)) {
            _baseType = Types.BINARY;
        } else if ("bit".equalsIgnoreCase(typeName)) {
            _baseType = Types.BIT;
        } else if ("blob".equalsIgnoreCase(typeName)) {
            _baseType = Types.BLOB;
        } else if ("boolean".equalsIgnoreCase(typeName)) {
            _baseType = Types.BOOLEAN;
        } else if ("char".equalsIgnoreCase(typeName)) {
            _baseType = Types.CHAR;
        } else if ("clob".equalsIgnoreCase(typeName)) {
            _baseType = Types.CLOB;
        } else if ("datalink".equalsIgnoreCase(typeName)) {
            _baseType = Types.DATALINK;
        } else if ("date".equalsIgnoreCase(typeName)) {
            _baseType = Types.DATE;
        } else if ("decimal".equalsIgnoreCase(typeName)) {
            _baseType = Types.DECIMAL;
        } else if ("distinct".equalsIgnoreCase(typeName)) {
            _baseType = Types.DISTINCT;
        } else if ("double".equalsIgnoreCase(typeName)) {
            _baseType = Types.DOUBLE;
        } else if ("float".equalsIgnoreCase(typeName)) {
            _baseType = Types.FLOAT;
        } else if ("integer".equalsIgnoreCase(typeName)) {
            _baseType = Types.INTEGER;
        } else if ("java_object".equalsIgnoreCase(typeName)) {
            _baseType = Types.JAVA_OBJECT;
        } else if ("longnvarchar".equalsIgnoreCase(typeName)) {
            _baseType = Types.LONGNVARCHAR;
        } else if ("longvarbinary".equalsIgnoreCase(typeName)) {
            _baseType = Types.LONGVARBINARY;
        } else if ("longvarchar".equalsIgnoreCase(typeName)) {
            _baseType = Types.LONGVARCHAR;
        } else if ("nchar".equalsIgnoreCase(typeName)) {
            _baseType = Types.NCHAR;
        } else if ("nclob".equalsIgnoreCase(typeName)) {
            _baseType = Types.NCLOB;
        } else if ("null".equalsIgnoreCase(typeName)) {
            _baseType = Types.NULL;
        } else if ("numeric".equalsIgnoreCase(typeName)) {
            _baseType = Types.NUMERIC;
        } else if ("nvarchar".equalsIgnoreCase(typeName)) {
            _baseType = Types.NVARCHAR;
        } else if ("other".equalsIgnoreCase(typeName)) {
            _baseType = Types.OTHER;
        } else if ("real".equalsIgnoreCase(typeName)) {
            _baseType = Types.REAL;
        } else if ("ref".equalsIgnoreCase(typeName)) {
            _baseType = Types.REF;
        } else if ("rowid".equalsIgnoreCase(typeName)) {
            _baseType = Types.ROWID;
        } else if ("smallint".equalsIgnoreCase(typeName)) {
            _baseType = Types.SMALLINT;
        } else if ("sqlxml".equalsIgnoreCase(typeName)) {
            _baseType = Types.SQLXML;
        } else if ("struct".equalsIgnoreCase(typeName)) {
            _baseType = Types.STRUCT;
        } else if ("time".equalsIgnoreCase(typeName)) {
            _baseType = Types.TIME;
        } else if ("timestamp".equalsIgnoreCase(typeName)) {
            _baseType = Types.TIMESTAMP;
        } else if ("tinyint".equalsIgnoreCase(typeName)) {
            _baseType = Types.TINYINT;
        } else if ("varbinary".equalsIgnoreCase(typeName)) {
            _baseType = Types.VARBINARY;
        } else if ("varchar".equalsIgnoreCase(typeName)) {
            _baseType = Types.VARCHAR;
        }
        _baseTypeName = typeName;
        _array = (Object)elements;
    }

    public SerialArray(Array arr) throws SQLException {
        _baseType = arr.getBaseType();
        _baseTypeName = arr.getBaseTypeName();
        _array = arr.getArray();

        if(_baseType == Types.STRUCT) {
            Object[] orig = (Object[])_array;
            Struct[] cpy = new SerialStruct[orig.length];
            for(int i = 0; i < orig.length; i++) {
                cpy[i] = new SerialStruct((Struct)orig[i]);
            }
            _array = cpy;
        }
        arr.free();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_baseType);
        out.writeObject(_baseTypeName);
        out.writeObject(_array);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _baseType = in.readInt();
        _baseTypeName = (String)in.readObject();
        _array = in.readObject();
    }

    /* start JDBC4 support */
    public void free() throws SQLException {
        _array = null;
    }
    /* end JDBC4 support */

    public String getBaseTypeName() throws SQLException {
        return _baseTypeName;
    }

    public int getBaseType() throws SQLException {
        return _baseType;
    }

    public Object getArray() throws SQLException {
        return _array;
    }

    public Object getArray(Map map) throws SQLException {
        throw new UnsupportedOperationException("getArray(Map) not supported");
    }

    public Object getArray(long index, int count) throws SQLException {
        throw new UnsupportedOperationException("getArray(index, count) not supported");
    }

    public Object getArray(long index, int count, Map map) throws SQLException {
        throw new UnsupportedOperationException("getArray(index, count, Map) not supported");
    }

    public ResultSet getResultSet() throws SQLException {
        throw new UnsupportedOperationException("getResultSet() not supported");
    }

    public ResultSet getResultSet(Map map) throws SQLException {
        throw new UnsupportedOperationException("getResultSet(Map) not supported");
    }

    public ResultSet getResultSet(long index, int count) throws SQLException {
        throw new UnsupportedOperationException("getResultSet(index, count) not supported");
    }

    public ResultSet getResultSet(long index, int count, Map map) throws SQLException {
        throw new UnsupportedOperationException("getResultSet(index, count, Map) not supported");
    }
}
