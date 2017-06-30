// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SerialResultSetMetaData implements ResultSetMetaData, Externalizable {
    static final long serialVersionUID = 9034215340975782405L;

    private int _columnCount;

    private String[] _catalogName;
    private String[] _schemaName;
    private String[] _tableName;
    private String[] _columnClassName;
    private String[] _columnLabel;
    private String[] _columnName;
    private String[] _columnTypeName;

    private Integer[] _columnType;
    private Integer[] _columnDisplaySize;
    private Integer[] _precision;
    private Integer[] _scale;
    private Integer[] _nullable;

    private Boolean[] _autoIncrement;
    private Boolean[] _caseSensitive;
    private Boolean[] _currency;
    private Boolean[] _readOnly;
    private Boolean[] _searchable;
    private Boolean[] _signed;
    private Boolean[] _writable;
    private Boolean[] _definitivelyWritable;

    public SerialResultSetMetaData() {
    }

    public SerialResultSetMetaData(ResultSetMetaData rsmd) throws SQLException {
        _columnCount = rsmd.getColumnCount();

        allocateArrays();
        fillArrays(rsmd);
    }

    public String[] readStringArr(ObjectInput in) throws IOException
    {
        int numElems = in.readShort();
        if (numElems != -1) {
            String ret[] = new String[numElems];
            for (int i = 0; i < numElems; i++) {
                byte notNull = in.readByte();
                if (1 == notNull) {
                    ret[i] = in.readUTF();
                } else {
                    ret[i] = null;
                }
            }
            return ret;
        }
        return null;
    }

    public void writeStringArr(String arr[], ObjectOutput out)
        throws IOException
    {
        if (arr != null) {
            out.writeShort(arr.length);
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != null) {
                    out.writeByte(1);
                    out.writeUTF(arr[i]);
                } else {
                    out.writeByte(0);
                }
            }
        } else {
            out.writeShort(-1);
        }
    }

    public Integer[] readIntArr(ObjectInput in) throws IOException
    {
        int numElems = in.readShort();
        if (numElems != -1) {
            Integer ret[] = new Integer[numElems];
            for (int i = 0; i < numElems; i++) {
                ret[i] = new Integer(in.readInt());
            }
            return ret;
        }
        return null;
    }

    public void writeIntArr(Integer arr[], ObjectOutput out)
        throws IOException
    {
        if (arr != null) {
            out.writeShort(arr.length);
            for (int i = 0; i < arr.length; i++) {
                out.writeInt(arr[i].intValue());
            }
        } else {
            out.writeShort(-1);
        }
    }

    public Boolean[] readBooleanArr(ObjectInput in) throws IOException
    {
        int numElems = in.readShort();
        if (numElems != -1) {
            Boolean ret[] = new Boolean[numElems];
            for (int i = 0; i < numElems; i++) {
                ret[i] = new Boolean(in.readBoolean());
            }
            return ret;
        }
        return null;
    }

    public void writeBooleanArr(Boolean arr[], ObjectOutput out)
        throws IOException
    {
        if (arr != null) {
            out.writeShort(arr.length);
            for (int i = 0; i < arr.length; i++) {
                out.writeBoolean(arr[i].booleanValue());
            }
        } else {
            out.writeShort(-1);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _columnCount = in.readInt();

        _catalogName = readStringArr(in);
        _schemaName = readStringArr(in);
        _tableName = readStringArr(in);
        _columnClassName = readStringArr(in);
        _columnLabel = readStringArr(in);
        _columnName = readStringArr(in);
        _columnTypeName = readStringArr(in);

        _columnType = readIntArr(in);
        _columnDisplaySize = readIntArr(in);
        _precision = readIntArr(in);
        _scale = readIntArr(in);
        _nullable = readIntArr(in);

        _autoIncrement = readBooleanArr(in);
        _caseSensitive = readBooleanArr(in);
        _currency = readBooleanArr(in);
        _readOnly = readBooleanArr(in);
        _searchable = readBooleanArr(in);
        _signed = readBooleanArr(in);
        _writable = readBooleanArr(in);
        _definitivelyWritable = readBooleanArr(in);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_columnCount);

        writeStringArr(_catalogName, out);
        writeStringArr(_schemaName, out);
        writeStringArr(_tableName, out);
        writeStringArr(_columnClassName, out);
        writeStringArr(_columnLabel, out);
        writeStringArr(_columnName, out);
        writeStringArr(_columnTypeName, out);

        writeIntArr(_columnType, out);
        writeIntArr(_columnDisplaySize, out);
        writeIntArr(_precision, out);
        writeIntArr(_scale, out);
        writeIntArr(_nullable, out);

        writeBooleanArr(_autoIncrement, out);
        writeBooleanArr(_caseSensitive, out);
        writeBooleanArr(_currency, out);
        writeBooleanArr(_readOnly, out);
        writeBooleanArr(_searchable, out);
        writeBooleanArr(_signed, out);
        writeBooleanArr(_writable, out);
        writeBooleanArr(_definitivelyWritable, out);
    }

    private void allocateArrays() {
        _catalogName = new String[_columnCount];
        _schemaName = new String[_columnCount];
        _tableName = new String[_columnCount];
        _columnClassName = new String[_columnCount];
        _columnLabel = new String[_columnCount];
        _columnName = new String[_columnCount];
        _columnTypeName = new String[_columnCount];

        _columnDisplaySize = new Integer[_columnCount];
        _columnType = new Integer[_columnCount];
        _precision = new Integer[_columnCount];
        _scale = new Integer[_columnCount];
        _nullable = new Integer[_columnCount];

        _autoIncrement = new Boolean[_columnCount];
        _caseSensitive = new Boolean[_columnCount];
        _currency = new Boolean[_columnCount];
        _readOnly = new Boolean[_columnCount];
        _searchable = new Boolean[_columnCount];
        _signed = new Boolean[_columnCount];
        _writable = new Boolean[_columnCount];
        _definitivelyWritable = new Boolean[_columnCount];
    }

    private void fillArrays(ResultSetMetaData rsmd) {
        for(int i = 0; i < _columnCount; i++) {
            int col = i + 1;

            try {
                _catalogName[i] = rsmd.getCatalogName(col);
            } catch(Exception e) {
                _catalogName[i] = null;
            }

            try {
                _schemaName[i] = rsmd.getSchemaName(col);
            } catch(Exception e1) {
                _schemaName[i] = null;
            }

            try {
                _tableName[i] = rsmd.getTableName(col);
            } catch(Exception e2) {
                _tableName[i] = null;
            }

            try {
                _columnLabel[i] = rsmd.getColumnLabel(col);
            } catch(Exception e3) {
                _columnLabel[i] = null;
            }

            try {
                _columnName[i] = rsmd.getColumnName(col);
            } catch(Exception e4) {
                _columnName[i] = null;
            }

            try {
                _columnClassName[i] = rsmd.getColumnClassName(col);
            } catch(Exception e5) {
                _columnClassName[i] = null;
            }

            try {
                _columnTypeName[i] = rsmd.getColumnTypeName(col);
            } catch(Exception e6) {
                _columnTypeName[i] = null;
            }

            try {
                _columnDisplaySize[i] = new Integer(rsmd.getColumnDisplaySize(col));
            } catch(Exception e7) {
                _columnDisplaySize[i] = null;
            }

            try {
                _columnType[i] = new Integer(rsmd.getColumnType(col));
            } catch(Exception e8) {
                _columnType[i] = null;
            }

            try {
                _precision[i] = new Integer(rsmd.getPrecision(col));
            } catch(Exception e9) {
                _precision[i] = null;
            }

            try {
                _scale[i] = new Integer(rsmd.getScale(col));
            } catch(Exception e10) {
                _scale[i] = null;
            }

            try {
                _nullable[i] = new Integer(rsmd.isNullable(col));
            } catch(Exception e11) {
                _nullable[i] = null;
            }

            try {
                _autoIncrement[i] = rsmd.isAutoIncrement(col) ? Boolean.TRUE : Boolean.FALSE;
            } catch(Exception e12) {
                _autoIncrement[i] = null;
            }

            try {
                _caseSensitive[i] = rsmd.isCaseSensitive(col) ? Boolean.TRUE : Boolean.FALSE;
            } catch(Exception e13) {
                _caseSensitive[i] = null;
            }

            try {
                _currency[i] = rsmd.isCurrency(col) ? Boolean.TRUE : Boolean.FALSE;
            } catch(Exception e14) {
                _currency[i] = null;
            }

            try {
                _readOnly[i] = rsmd.isReadOnly(col) ? Boolean.TRUE : Boolean.FALSE;
            } catch(Exception e15) {
                _readOnly[i] = null;
            }

            try {
                _searchable[i] = rsmd.isSearchable(col) ? Boolean.TRUE : Boolean.FALSE;
            } catch(Exception e16) {
                _searchable[i] = null;
            }

            try {
                _signed[i] = rsmd.isSigned(col) ? Boolean.TRUE : Boolean.FALSE;
            } catch(Exception e17) {
                _signed[i] = null;
            }

            try {
                _writable[i] = rsmd.isWritable(col) ? Boolean.TRUE : Boolean.FALSE;
            } catch(Exception e18) {
                _writable[i] = null;
            }

            try {
                _definitivelyWritable[i] = rsmd.isDefinitelyWritable(col) ? Boolean.TRUE : Boolean.FALSE;
            } catch(Exception e18) {
                _definitivelyWritable[i] = null;
            }
        }
    }

    public int getColumnCount() throws SQLException {
        return _columnCount;
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_autoIncrement[column - 1]);
        return _autoIncrement[column - 1].booleanValue();
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_caseSensitive[column - 1]);
        return _caseSensitive[column - 1].booleanValue();
    }

    public boolean isSearchable(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_searchable[column - 1]);
        return _searchable[column - 1].booleanValue();
    }

    public boolean isCurrency(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_currency[column - 1]);
        return _currency[column - 1].booleanValue();
    }

    public int isNullable(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_nullable[column - 1]);
        return _nullable[column - 1].intValue();
    }

    public boolean isSigned(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_signed[column - 1]);
        return _signed[column - 1].booleanValue();
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_columnDisplaySize[column - 1]);
        return _columnDisplaySize[column - 1].intValue();
    }

    public String getColumnLabel(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_columnLabel[column - 1]);
        return _columnLabel[column - 1];
    }

    public String getColumnName(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_columnName[column - 1]);
        return _columnName[column - 1];
    }

    public String getSchemaName(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_schemaName[column - 1]);
        return _schemaName[column - 1];
    }

    public int getPrecision(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_precision[column - 1]);
        return _precision[column - 1].intValue();
    }

    public int getScale(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_scale[column - 1]);
        return _scale[column - 1].intValue();
    }

    public String getTableName(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_tableName[column - 1]);
        return _tableName[column - 1];
    }

    public String getCatalogName(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_catalogName[column - 1]);
        return _catalogName[column - 1];
    }

    public int getColumnType(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_columnType[column - 1]);
        return _columnType[column - 1].intValue();
    }

    public String getColumnTypeName(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_columnTypeName[column - 1]);
        return _columnTypeName[column - 1];
    }

    public boolean isReadOnly(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_readOnly[column - 1]);
        return _readOnly[column - 1].booleanValue();
    }

    public boolean isWritable(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_writable[column - 1]);
        return _writable[column - 1].booleanValue();
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_definitivelyWritable[column - 1]);
        return _definitivelyWritable[column - 1].booleanValue();
    }

    public String getColumnClassName(int column) throws SQLException {
        checkColumnIndex(column);
        throwIfNull(_columnClassName[column - 1]);
        return _columnClassName[column - 1];
    }

    public void setColumnCount(int columnCount) throws SQLException {
        if (columnCount < 0) {
            throw new SQLException("invalid number of columns " + columnCount);
        }
        _columnCount = columnCount;
        allocateArrays();
    }

    public void setAutoIncrement(int columnIndex, boolean property)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _autoIncrement[columnIndex - 1] = new Boolean(property);
    }

    public void setCaseSensitive(int columnIndex, boolean property)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _caseSensitive[columnIndex - 1] = new Boolean(property);
    }

    public void setCatalogName(int columnIndex, String catalogName)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _catalogName[columnIndex - 1] = catalogName;
    }

    public void setColumnDisplaySize(int columnIndex, int size)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _columnDisplaySize[columnIndex - 1] = new Integer(size);
    }

    public void setColumnLabel(int columnIndex, String label)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _columnLabel[columnIndex - 1] = label;
    }

    public void setColumnName(int columnIndex, String columnName)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _columnName[columnIndex - 1] = columnName;
    }

    public void setColumnType(int columnIndex, int SQLType)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _columnType[columnIndex - 1] = new Integer(SQLType);
    }

    public void setColumnTypeName(int columnIndex, String typeName)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _columnTypeName[columnIndex - 1] = typeName;
    }

    public void setCurrency(int columnIndex, boolean property)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _currency[columnIndex - 1] = new Boolean(property);
    }

    public void setNullable(int columnIndex, int property)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _nullable[columnIndex - 1] = new Integer(property);
    }

    public void setPrecision(int columnIndex, int precision)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _precision[columnIndex - 1] = new Integer(precision);
    }

    public void setScale(int columnIndex, int scale)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _scale[columnIndex - 1] = new Integer(scale);
    }

    public void setSchemaName(int columnIndex, String schemaName)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _schemaName[columnIndex - 1] = schemaName;
    }

    public void setSearchable(int columnIndex, boolean property)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _searchable[columnIndex - 1] = new Boolean(property);
    }

    public void setSigned(int columnIndex, boolean property)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _signed[columnIndex - 1] = new Boolean(property);
    }

    public void setTableName(int columnIndex, String tableName)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _tableName[columnIndex - 1] = tableName;
    }

    public void setReadOnly(int columnIndex, boolean readOnly)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _readOnly[columnIndex - 1] = new Boolean(readOnly);
    }

    public void setWritable(int columnIndex, boolean writable)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _writable[columnIndex - 1] = new Boolean(writable);
    }

    public void setDefinitelyWritable(int columnIndex, boolean writable)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _definitivelyWritable[columnIndex - 1] = new Boolean(writable);
    }

    public void setColumnClassName(int columnIndex, String columnClassName)
        throws SQLException {
        checkColumnIndex(columnIndex);
        _columnClassName[columnIndex - 1] = columnClassName;
    }

    private void throwIfNull(Object obj) throws SQLException {
        if(obj == null) {
            throw new SQLException("Method not supported");
        }
    }

    private void checkColumnIndex(int columnIndex) throws SQLException {
        if (columnIndex < 1 || columnIndex > _columnCount) {
            throw new SQLException("invalid column index " + columnIndex);
        }
    }

    /* start JDBC4 support */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(SerialResultSetMetaData.class);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T)this;
    }
    /* end JDBC4 support */
}

