// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import de.simplicit.vjdbc.command.*;
import de.simplicit.vjdbc.util.JavaVersionInfo;
import de.simplicit.vjdbc.util.SQLExceptionHelper;
import de.simplicit.vjdbc.VirtualStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

public class StreamingResultSet implements ResultSet, Externalizable {
    static final long serialVersionUID = 8291019975153433161L;

    private static Log _logger = LogFactory.getLog(StreamingResultSet.class);

    private int[] _columnTypes;
    private String[] _columnNames;
    private String[] _columnLabels;
    private RowPacket _rows;
    private int _rowPacketSize;
    private boolean _forwardOnly;
    private String _charset;
    private boolean _lastPartReached = true;
    private UIDEx _remainingResultSet = null;
    private SerialResultSetMetaData _metaData = null;

    private transient DecoratedCommandSink _commandSink = null;
    private transient int _cursor = -1;
    private transient int _lastReadColumn = 0;
    private transient Object[] _actualRow;
    private transient int _fetchDirection;
    private transient boolean _prefetchMetaData;
    private transient Statement _statement;

    protected void finalize() throws Throwable {
        super.finalize();
        if(_remainingResultSet != null) {
            close();
        }
    }

    public StreamingResultSet() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_columnTypes);
        out.writeObject(_columnNames);
        out.writeObject(_columnLabels);
        out.writeObject(_rows);
        out.writeInt(_rowPacketSize);
        out.writeBoolean(_forwardOnly);
        out.writeUTF(_charset);
        out.writeBoolean(_lastPartReached);
        out.writeObject(_remainingResultSet);
        out.writeObject(_metaData);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _columnTypes = (int[])in.readObject();
        _columnNames = (String[])in.readObject();
        _columnLabels = (String[])in.readObject();
        _rows = (RowPacket)in.readObject();
        _rowPacketSize = in.readInt();
        _forwardOnly = in.readBoolean();
        _charset = in.readUTF();
        _lastPartReached = in.readBoolean();
        _remainingResultSet = (UIDEx)in.readObject();
        _metaData = (SerialResultSetMetaData)in.readObject();

        _cursor = -1;
    }

    public StreamingResultSet(int rowPacketSize, boolean forwardOnly, boolean prefetchMetaData, String charset) {
        _rowPacketSize = rowPacketSize;
        _forwardOnly = forwardOnly;
        _prefetchMetaData = prefetchMetaData;
        _charset = charset;
    }

    public void setStatement(Statement stmt) {
        _statement = stmt;
    }

    public void setCommandSink(DecoratedCommandSink sink) {
        _commandSink = sink;
    }

    public void setRemainingResultSetUID(UIDEx reg) {
        _remainingResultSet = reg;
    }

    public boolean populate(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Fetch the meta data immediately if required. Succeeding getMetaData() calls
        // on the ResultSet won't require an additional remote call
        if(_prefetchMetaData) {
            _logger.debug("Fetching MetaData of ResultSet");
            _metaData = new SerialResultSetMetaData(metaData);
        }

        int columnCount = metaData.getColumnCount();
        _columnTypes = new int[columnCount];
        _columnNames = new String[columnCount];
        _columnLabels = new String[columnCount];

        for(int i = 1; i <= columnCount; i++) {
            _columnTypes[i-1] = metaData.getColumnType(i);
            _columnNames[i-1] = metaData.getColumnName(i).toLowerCase();
            _columnLabels[i-1] = metaData.getColumnLabel(i).toLowerCase();
        }

        // Create first ResultSet-Part
        _rows = new RowPacket(_rowPacketSize, _forwardOnly);
        // Populate it
        _rows.populate(rs);

        _lastPartReached = _rows.isLastPart();

        return _lastPartReached;
    }

    public boolean next() throws SQLException {
        boolean result = false;

        if(++_cursor < _rows.size()) {
            _actualRow = _rows.get(_cursor);
            result = true;
        } else {
            if(!_lastPartReached) {
                try {
                    SerializableTransport st = (SerializableTransport)_commandSink.process(_remainingResultSet, new NextRowPacketCommand());
                    RowPacket rsp = (RowPacket)st.getTransportee();

                    if(rsp.isLastPart()) {
                        _lastPartReached = true;
                    }

                    if(rsp.size() > 0) {
                        _rows.merge(rsp);
                        _actualRow = _rows.get(_cursor);
                        result = true;
                    }
                } catch(Exception e) {
                    throw SQLExceptionHelper.wrap(e);
                }
            }
        }

        return result;
    }

    public void close() throws SQLException {
        _cursor = -1;
        if(_remainingResultSet != null) {
            // The server-side created StreamingResultSet is garbage-collected after it was send over the wire. Thus
            // we have to check here if it is such a server object because in this case we don't have to try the remote
            // call which indeed causes a NPE.
            if(_commandSink != null) {
                _commandSink.process(_remainingResultSet, new DestroyCommand(_remainingResultSet, JdbcInterfaceType.RESULTSETHOLDER));
            }
            _remainingResultSet = null;
        }
        if (((VirtualStatement)_statement).isCloseOnCompletion()) {
            _statement.close();
        }
    }

    public boolean wasNull() throws SQLException {
        return _actualRow[_lastReadColumn] == null;
    }

    public String getString(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return _actualRow[columnIndex].toString();
        } else {
            return null;
        }
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            Object value = _actualRow[columnIndex];

            switch(_columnTypes[columnIndex]) {
                case Types.BIT:
                    // Boolean
                    return ((Boolean)value).booleanValue();
                case Types.TINYINT:
                    // Byte
                    return ((Byte)value).byteValue() != 0;
                case Types.SMALLINT:
                    // Short
                    return ((Short)value).shortValue() != 0;
                case Types.INTEGER:
                    // Integer
                    return ((Integer)value).intValue() != 0;
                case Types.BIGINT:
                    // Long
                    return ((Long)value).longValue() != 0;
                case Types.REAL:
                    // Float
                    return ((Float)value).floatValue() != 0.0f;
                case Types.FLOAT:
                case Types.DOUBLE:
                    // Double
                    return ((Double)value).doubleValue() != 0.0f;
                case Types.NUMERIC:
                case Types.DECIMAL:
                    // BigDecimal
                    return ((BigDecimal)value).intValue() != 0;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    // String
                    try {
                        return Integer.parseInt((String)value) != 0;
                    } catch (NumberFormatException e) {
                        throw new SQLException("Can't convert String value '" + value + "' to boolean, must be an integer");
                    }
                default:
                    if(JavaVersionInfo.use14Api) {
                        if(_columnTypes[columnIndex] == Types.BOOLEAN) {
                            // Boolean
                            return ((Boolean)value).booleanValue();
                        }
                    }
                    break;
            }

            throw new SQLException("Can't convert type to boolean: " + value.getClass());
        }

        return false;
    }

    public byte getByte(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            Object value = _actualRow[columnIndex];

            switch(_columnTypes[columnIndex]) {
                case Types.BIT:
                    // Boolean
                    return ((Boolean)value).booleanValue() ? (byte)1 : (byte)0;
                case Types.TINYINT:
                    // Byte
                    return ((Byte)value).byteValue();
                case Types.SMALLINT:
                    // Short
                    return ((Short)value).byteValue();
                case Types.INTEGER:
                    // Integer
                    return ((Integer)value).byteValue();
                case Types.BIGINT:
                    // Long
                    return ((Long)value).byteValue();
                case Types.REAL:
                    // Float
                    return ((Float)value).byteValue();
                case Types.FLOAT:
                case Types.DOUBLE:
                    // Double
                    return ((Double)value).byteValue();
                case Types.NUMERIC:
                case Types.DECIMAL:
                    // BigDecimal
                    return ((BigDecimal)value).byteValue();
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    // String
                    try {
                        return Byte.parseByte((String)value);
                    } catch (NumberFormatException e) {
                        throw new SQLException("Can't convert String value '" + value + "' to byte");
                    }
                default:
                    if(JavaVersionInfo.use14Api) {
                        if(_columnTypes[columnIndex] == Types.BOOLEAN) {
                            // Boolean
                            return ((Boolean)value).booleanValue() ? (byte)1 : (byte)0;
                        }
                    }
                    break;
            }

            throw new SQLException("Can't convert type to byte: " + value.getClass());
        }

        return 0;
    }

    public short getShort(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            Object value = _actualRow[columnIndex];

            switch(_columnTypes[columnIndex]) {
                case Types.BIT:
                    // Boolean
                    return ((Boolean)value).booleanValue() ? (short)1 : (short)0;
                case Types.TINYINT:
                    // Byte
                    return ((Byte)value).shortValue();
                case Types.SMALLINT:
                    // Short
                    return ((Short)value).shortValue();
                case Types.INTEGER:
                    // Integer
                    return ((Integer)value).shortValue();
                case Types.BIGINT:
                    // Long
                    return ((Long)value).shortValue();
                case Types.REAL:
                    // Float
                    return ((Float)value).shortValue();
                case Types.FLOAT:
                case Types.DOUBLE:
                    // Double
                    return ((Double)value).shortValue();
                case Types.NUMERIC:
                case Types.DECIMAL:
                    // BigDecimal
                    return ((BigDecimal)value).shortValue();
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    // String
                    try {
                        return Short.parseShort((String)value);
                    } catch (NumberFormatException e) {
                        throw new SQLException("Can't convert String value '" + value + "' to short");
                    }
                default:
                    if(JavaVersionInfo.use14Api) {
                        if(_columnTypes[columnIndex] == Types.BOOLEAN) {
                            // Boolean
                            return ((Boolean)value).booleanValue() ? (short)1 : (short)0;
                        }
                    }
                    break;
            }

            throw new SQLException("Can't convert type to short: " + value.getClass());
        }

        return 0;
    }

    public int getInt(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            Object value = _actualRow[columnIndex];

            switch(_columnTypes[columnIndex]) {
                case Types.BIT:
                    // Boolean
                    return ((Boolean)value).booleanValue() ? (int)1 : (int)0;
                case Types.TINYINT:
                    // Byte
                    return ((Byte)value).intValue();
                case Types.SMALLINT:
                    // Short
                    return ((Short)value).intValue();
                case Types.INTEGER:
                    // Integer
                    return ((Integer)value).intValue();
                case Types.BIGINT:
                    // Long
                    return ((Long)value).intValue();
                case Types.REAL:
                    // Float
                    return ((Float)value).intValue();
                case Types.FLOAT:
                case Types.DOUBLE:
                    // Double
                    return ((Double)value).intValue();
                case Types.NUMERIC:
                case Types.DECIMAL:
                    // BigDecimal
                    return ((BigDecimal)value).intValue();
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    // String
                    try {
                        return Integer.parseInt((String)value);
                    } catch (NumberFormatException e) {
                        throw new SQLException("Can't convert String value '" + value + "' to integer");
                    }
                default:
                    if(JavaVersionInfo.use14Api) {
                        if(_columnTypes[columnIndex] == Types.BOOLEAN) {
                            // Boolean
                            return ((Boolean)value).booleanValue() ? 1 : 0;
                        }
                    }
                    break;
            }

            throw new SQLException("Can't convert type to integer: " + value.getClass());
        }

        return 0;
    }

    public long getLong(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            Object value = _actualRow[columnIndex];

            switch(_columnTypes[columnIndex]) {
                case Types.BIT:
                    // Boolean
                    return ((Boolean)value).booleanValue() ? 1 : 0;
                case Types.TINYINT:
                    // Byte
                    return ((Byte)value).longValue();
                case Types.SMALLINT:
                    // Short
                    return ((Short)value).longValue();
                case Types.INTEGER:
                    // Integer
                    return ((Integer)value).longValue();
                case Types.BIGINT:
                    // Long
                    return ((Long)value).longValue();
                case Types.REAL:
                    // Float
                    return ((Float)value).longValue();
                case Types.FLOAT:
                case Types.DOUBLE:
                    // Double
                    return ((Double)value).longValue();
                case Types.NUMERIC:
                case Types.DECIMAL:
                    // BigDecimal
                    return ((BigDecimal)value).longValue();
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    // String
                    try {
                        return Long.parseLong((String)value);
                    } catch (NumberFormatException e) {
                        throw new SQLException("Can't convert String value '" + value + "' to long");
                    }
                default:
                    if(JavaVersionInfo.use14Api) {
                        if(_columnTypes[columnIndex] == Types.BOOLEAN) {
                            // Boolean
                            return ((Boolean)value).booleanValue() ? 1 : 0;
                        }
                    }
                    break;
            }

            throw new SQLException("Can't convert type to long: " + value.getClass());
        }

        return 0;
    }

    public float getFloat(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            Object value = _actualRow[columnIndex];

            switch(_columnTypes[columnIndex]) {
                case Types.BIT:
                    // Boolean
                    return ((Boolean)value).booleanValue() ? 1.0f : 0.0f;
                case Types.TINYINT:
                    // Byte
                    return ((Byte)value).floatValue();
                case Types.SMALLINT:
                    // Short
                    return ((Short)value).floatValue();
                case Types.INTEGER:
                    // Integer
                    return ((Integer)value).floatValue();
                case Types.BIGINT:
                    // Long
                    return ((Long)value).floatValue();
                case Types.REAL:
                    // Float
                    return ((Float)value).floatValue();
                case Types.FLOAT:
                case Types.DOUBLE:
                    // Double
                    return ((Double)value).floatValue();
                case Types.NUMERIC:
                case Types.DECIMAL:
                    // BigDecimal
                    return ((BigDecimal)value).floatValue();
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    // String
                    try {
                        return Float.parseFloat((String)value);
                    } catch (NumberFormatException e) {
                        throw new SQLException("Can't convert String value '" + value + "' to float");
                    }
                default:
                    if(JavaVersionInfo.use14Api) {
                        if(_columnTypes[columnIndex] == Types.BOOLEAN) {
                            // Boolean
                            return ((Boolean)value).booleanValue() ? 1 : 0;
                        }
                    }
                    break;
            }

            throw new SQLException("Can't convert type to float: " + value.getClass());
        }

        return 0.0f;
    }

    public double getDouble(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            Object value = _actualRow[columnIndex];

            switch(_columnTypes[columnIndex]) {
                case Types.BIT:
                    // Boolean
                    return ((Boolean)value).booleanValue() ? 1.0 : 0.0;
                case Types.TINYINT:
                    // Byte
                    return ((Byte)value).doubleValue();
                case Types.SMALLINT:
                    // Short
                    return ((Short)value).doubleValue();
                case Types.INTEGER:
                    // Integer
                    return ((Integer)value).doubleValue();
                case Types.BIGINT:
                    // Long
                    return ((Long)value).doubleValue();
                case Types.REAL:
                    // Float
                    return ((Float)value).doubleValue();
                case Types.FLOAT:
                case Types.DOUBLE:
                    // Double
                    return ((Double)value).doubleValue();
                case Types.NUMERIC:
                case Types.DECIMAL:
                    // BigDecimal
                    return ((BigDecimal)value).doubleValue();
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    // String
                    try {
                        return Double.parseDouble((String)value);
                    } catch (NumberFormatException e) {
                        throw new SQLException("Can't convert String value '" + value + "' to double");
                    }
                default:
                    if(JavaVersionInfo.use14Api) {
                        if(_columnTypes[columnIndex] == Types.BOOLEAN) {
                            // Boolean
                            return ((Boolean)value).booleanValue() ? 1 : 0;
                        }
                    }
                    break;
            }

            throw new SQLException("Can't convert type to double: " + value.getClass());
        }

        return 0.0;
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return internalGetBigDecimal(_actualRow[columnIndex], _columnTypes[columnIndex], scale);
        } else {
            return null;
        }
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return (byte[])_actualRow[columnIndex];
        } else {
            return null;
        }
    }

    public Date getDate(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            switch(_columnTypes[columnIndex]) {
                case Types.DATE:
                    return (Date)_actualRow[columnIndex];
                case Types.TIME:
                    return getCleanDate((((Time)_actualRow[columnIndex]).getTime()));
                case Types.TIMESTAMP:
                    return getCleanDate(((Timestamp)_actualRow[columnIndex]).getTime());
            }

            throw new SQLException("Can't convert type to Date: " + _actualRow[columnIndex].getClass());
        } else {
            return null;
        }
    }

    public Time getTime(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            switch(_columnTypes[columnIndex]) {
                case Types.TIME:
                    return (Time)_actualRow[columnIndex];
                case Types.DATE:
                    Date date = ((Date)_actualRow[columnIndex]);
                    return getCleanTime(date.getTime());
                case Types.TIMESTAMP:
                    Timestamp timestamp = ((Timestamp)_actualRow[columnIndex]);
                    return getCleanTime(timestamp.getTime());
            }

            throw new SQLException("Can't convert type to Time: " + _actualRow[columnIndex].getClass());
        } else {
            return null;
        }
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            switch(_columnTypes[columnIndex]) {
                case Types.TIME:
                    return new Timestamp(((Time)_actualRow[columnIndex]).getTime());
                case Types.DATE:
                    return new Timestamp(((Date)_actualRow[columnIndex]).getTime());
                case Types.TIMESTAMP:
                    return ((Timestamp)_actualRow[columnIndex]);
            }

            throw new SQLException("Can't convert type to Timestamp: " + _actualRow[columnIndex].getClass());
        } else {
            return null;
        }
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("getAsciiStream");
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("getUnicodeStream");
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            Object obj = _actualRow[columnIndex];

            byte[] bytes;

            if(obj instanceof byte[]) {
                bytes = (byte[])obj;
            } else if(obj instanceof String) {
                try {
                    bytes = ((String)obj).getBytes(_charset);
                } catch(UnsupportedEncodingException e) {
                    throw SQLExceptionHelper.wrap(e);
                }
            } else {
                String msg = "StreamingResultSet.getBinaryStream(): Can't convert object of type '" + obj.getClass() + "' to InputStream";
                throw new SQLException(msg);
            }

            return new ByteArrayInputStream(bytes);
        } else {
            return null;
        }
    }

    public String getString(String columnName) throws SQLException {
        return getString(getIndexForName(columnName));
    }

    public boolean getBoolean(String columnName) throws SQLException {
        return getBoolean(getIndexForName(columnName));
    }

    public byte getByte(String columnName) throws SQLException {
        return getByte(getIndexForName(columnName));
    }

    public short getShort(String columnName) throws SQLException {
        return getShort(getIndexForName(columnName));
    }

    public int getInt(String columnName) throws SQLException {
        return getInt(getIndexForName(columnName));
    }

    public long getLong(String columnName) throws SQLException {
        return getLong(getIndexForName(columnName));
    }

    public float getFloat(String columnName) throws SQLException {
        return getFloat(getIndexForName(columnName));
    }

    public double getDouble(String columnName) throws SQLException {
        return getDouble(getIndexForName(columnName));
    }

    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        return getBigDecimal(getIndexForName(columnName), scale);
    }

    public byte[] getBytes(String columnName) throws SQLException {
        return getBytes(getIndexForName(columnName));
    }

    public Date getDate(String columnName) throws SQLException {
        return getDate(getIndexForName(columnName));
    }

    public Time getTime(String columnName) throws SQLException {
        return getTime(getIndexForName(columnName));
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
        return getTimestamp(getIndexForName(columnName));
    }

    public InputStream getAsciiStream(String columnName) throws SQLException {
        throw new UnsupportedOperationException("getAsciiStream");
    }

    public InputStream getUnicodeStream(String columnName) throws SQLException {
        throw new UnsupportedOperationException("getUnicodeStream");
    }

    public InputStream getBinaryStream(String columnName) throws SQLException {
        return getBinaryStream(getIndexForName(columnName));
    }

    public SQLWarning getWarnings() throws SQLException {
        if(_cursor < 0) {
            throw new SQLException("ResultSet already closed");
        } else {
            return null;
        }
    }

    public void clearWarnings() throws SQLException {
    }

    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException("getCursorName");
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        if(_metaData == null) {
            SerializableTransport st = (SerializableTransport)_commandSink.process(_remainingResultSet, new ResultSetGetMetaDataCommand());
            if(st != null) {
                try {
                    _metaData = (SerialResultSetMetaData)st.getTransportee();
                } catch(Exception e) {
                    throw new SQLException("Can't get ResultSetMetaData, reason: " + e.toString());
                }
            } else {
                throw new SQLException("Can't get ResultSetMetaData");
            }
        }

        return _metaData;
    }

    public Object getObject(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return _actualRow[columnIndex];
        }
        else {
            return null;
        }
    }

    public Object getObject(String columnName) throws SQLException {
        return getObject(getIndexForName(columnName));
    }

    public int findColumn(String columnName) throws SQLException {
        return getIndexForName(columnName);
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return new StringReader((String)_actualRow[columnIndex]);
        }
        else {
            return null;
        }
    }

    public Reader getCharacterStream(String columnName) throws SQLException {
        return getCharacterStream(getIndexForName(columnName));
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return internalGetBigDecimal(_actualRow[columnIndex], _columnTypes[columnIndex], -1);
        }
        else {
            return null;
        }
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        return getBigDecimal(getIndexForName(columnName));
    }

    private BigDecimal internalGetBigDecimal(Object value, int columnType, int scale) throws SQLException {
        BigDecimal result = null;

        if(value != null) {
            switch(columnType) {
                case Types.BIT:
                    // Boolean
                    result = new BigDecimal(((Boolean)value).booleanValue() ? 1.0 : 0.0);
                    break;
                case Types.TINYINT:
                    // Byte
                    result = new BigDecimal(((Byte)value).doubleValue());
                    break;
                case Types.SMALLINT:
                    // Short
                    result = new BigDecimal(((Short)value).doubleValue());
                    break;
                case Types.INTEGER:
                    // Integer
                    result = new BigDecimal(((Integer)value).doubleValue());
                    break;
                case Types.BIGINT:
                    // Long
                    result = new BigDecimal(((Long)value).doubleValue());
                    break;
                case Types.REAL:
                    // Float
                    result = new BigDecimal(((Float)value).doubleValue());
                    break;
                case Types.FLOAT:
                case Types.DOUBLE:
                    // Double
                    result = new BigDecimal(((Double)value).doubleValue());
                    break;
                case Types.NUMERIC:
                case Types.DECIMAL:
                    // BigDecimal
                    result = (BigDecimal)value;
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    // String
                    try {
                        result = new BigDecimal(Double.parseDouble((String)value));
                    } catch (NumberFormatException e) {
                        throw new SQLException("Can't convert String value '" + value + "' to double");
                    }
                default:
                    if(JavaVersionInfo.use14Api) {
                        if(columnType == Types.BOOLEAN) {
                            // Boolean
                            result = new BigDecimal(((Boolean)value).booleanValue() ? 1.0 : 0.0);
                        }
                    }
                    break;
            }

            // Set scale if necessary
            if(result != null) {
                if(scale >= 0) {
                    result = result.setScale(scale);
                }
            }
            else {
                throw new SQLException("Can't convert type to BigDecimal: " + value.getClass());
            }
        }

        return result;
    }

    public boolean isBeforeFirst() throws SQLException {
        return _cursor < 0;
    }

    public boolean isAfterLast() throws SQLException {
        return _rows.isLastPart() && (_cursor == _rows.size());
    }

    public boolean isFirst() throws SQLException {
        return _cursor == 0;
    }

    public boolean isLast() throws SQLException {
        return _rows.isLastPart() && (_cursor == (_rows.size() - 1));
    }

    public void beforeFirst() throws SQLException {
        _cursor = -1;
        _actualRow = null;
    }

    public void afterLast() throws SQLException {
        // Request all remaining Row-Packets
        while(requestNextRowPacket()) ;
        _cursor = _rows.size();
        _actualRow = null;
    }

    public boolean first() throws SQLException {
        try {
            _cursor = 0;
            _actualRow = _rows.get(_cursor);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean last() throws SQLException {
        try {
            // Request all remaining Row-Packets
            while(requestNextRowPacket()) ;
            _cursor = _rows.size() - 1;
            _actualRow = _rows.get(_cursor);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public int getRow() throws SQLException {
        return _cursor + 1;
    }

    public boolean absolute(int row) throws SQLException {
        return setCursor(row - 1);
    }

    public boolean relative(int step) throws SQLException {
        return setCursor(_cursor + step);
    }

    public boolean previous() throws SQLException {
        if(_forwardOnly) {
            throw new SQLException("previous() not possible on Forward-Only-ResultSet");
        } else {
            if(_cursor > 0) {
                _actualRow = _rows.get(--_cursor);
                return true;
            } else {
                return false;
            }
        }
    }

    public void setFetchDirection(int direction) throws SQLException {
        _fetchDirection = direction;
    }

    public int getFetchDirection() throws SQLException {
        return _fetchDirection;
    }

    public void setFetchSize(int rows) throws SQLException {
    }

    public int getFetchSize() throws SQLException {
        return 0;
    }

    public int getType() throws SQLException {
        return _forwardOnly ? ResultSet.TYPE_FORWARD_ONLY : ResultSet.TYPE_SCROLL_INSENSITIVE;
    }

    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    public boolean rowUpdated() throws SQLException {
        return false;
    }

    public boolean rowInserted() throws SQLException {
        return false;
    }

    public boolean rowDeleted() throws SQLException {
        return false;
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("updateNull");
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new UnsupportedOperationException("updateBoolean");
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new UnsupportedOperationException("updateByte");
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new UnsupportedOperationException("updateShort");
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new UnsupportedOperationException("updateInt");
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new UnsupportedOperationException("updateLong");
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new UnsupportedOperationException("updateFloat");
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new UnsupportedOperationException("updateDouble");
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException("updateBigDecimal");
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new UnsupportedOperationException("updateString");
    }

    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
        throw new UnsupportedOperationException("updateBytes");
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new UnsupportedOperationException("updateDate");
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new UnsupportedOperationException("updateTime");
    }

    public void updateTimestamp(int columnIndex, Timestamp x)
            throws SQLException {
        throw new UnsupportedOperationException("updateTimestamp");
    }

    public void updateAsciiStream(int columnIndex,
                                  InputStream x,
                                  int length) throws SQLException {
        throw new UnsupportedOperationException("updateAsciiStream");
    }

    public void updateBinaryStream(int columnIndex,
                                   InputStream x,
                                   int length) throws SQLException {
        throw new UnsupportedOperationException("updateBinaryStream");
    }

    public void updateCharacterStream(int columnIndex,
                                      Reader x,
                                      int length) throws SQLException {
        throw new UnsupportedOperationException("updateCharacterStream");
    }

    public void updateObject(int columnIndex, Object x, int scale)
            throws SQLException {
        throw new UnsupportedOperationException("updateObject");
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new UnsupportedOperationException("updateObject");
    }

    public void updateNull(String columnName) throws SQLException {
        throw new UnsupportedOperationException("updateNull");
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
        throw new UnsupportedOperationException("updateBoolean");
    }

    public void updateByte(String columnName, byte x) throws SQLException {
        throw new UnsupportedOperationException("updateByte");
    }

    public void updateShort(String columnName, short x) throws SQLException {
        throw new UnsupportedOperationException("updateShort");
    }

    public void updateInt(String columnName, int x) throws SQLException {
        throw new UnsupportedOperationException("updateInt");
    }

    public void updateLong(String columnName, long x) throws SQLException {
        throw new UnsupportedOperationException("updateLong");
    }

    public void updateFloat(String columnName, float x) throws SQLException {
        throw new UnsupportedOperationException("updateFloat");
    }

    public void updateDouble(String columnName, double x) throws SQLException {
        throw new UnsupportedOperationException("updateDouble");
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException("updateBigDecimal");
    }

    public void updateString(String columnName, String x) throws SQLException {
        throw new UnsupportedOperationException("updateString");
    }

    public void updateBytes(String columnName, byte x[]) throws SQLException {
        throw new UnsupportedOperationException("updateBytes");
    }

    public void updateDate(String columnName, Date x) throws SQLException {
        throw new UnsupportedOperationException("updateDate");
    }

    public void updateTime(String columnName, Time x) throws SQLException {
        throw new UnsupportedOperationException("updateTime");
    }

    public void updateTimestamp(String columnName, Timestamp x)
            throws SQLException {
        throw new UnsupportedOperationException("updateTimestamp");
    }

    public void updateAsciiStream(String columnName,
                                  InputStream x,
                                  int length) throws SQLException {
        throw new UnsupportedOperationException("updateAsciiStream");
    }

    public void updateBinaryStream(String columnName,
                                   InputStream x,
                                   int length) throws SQLException {
        throw new UnsupportedOperationException("updateBinaryStream");
    }

    public void updateCharacterStream(String columnName,
                                      Reader reader,
                                      int length) throws SQLException {
        throw new UnsupportedOperationException("updateCharacterStream");
    }

    public void updateObject(String columnName, Object x, int scale)
            throws SQLException {
        throw new UnsupportedOperationException("updateObject");
    }

    public void updateObject(String columnName, Object x) throws SQLException {
        throw new UnsupportedOperationException("updateObject");
    }

    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException("insertRow");
    }

    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException("updateRow");
    }

    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException("deleteRow");
    }

    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException("refreshRow");
    }

    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException("cancelRowUpdates");
    }

    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException("moveToInsertRow");
    }

    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException("moveToCurrentRow");
    }

    public Statement getStatement() throws SQLException {
        return _statement;
    }

    public Object getObject(int i, Map map) throws SQLException {
        throw new UnsupportedOperationException("getObject");
    }

    public Ref getRef(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return (Ref)_actualRow[columnIndex];
        }
        else {
            return null;
        }
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return (Blob)_actualRow[columnIndex];
        }
        else {
            return null;
        }
    }

    public Clob getClob(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return (Clob)_actualRow[columnIndex];
        }
        else {
            return null;
        }
    }

    public Array getArray(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return (Array)_actualRow[columnIndex];
        }
        else {
            return null;
        }
    }

    public Object getObject(String colName, Map map) throws SQLException {
        throw new UnsupportedOperationException("getObject");
    }

    public <T> T getObject(String columnName, Class<T> clazz) {
        throw new UnsupportedOperationException("getObject(String, Class)");
    }

    public <T> T getObject(int columnIndex, Class<T> clazz) {
        throw new UnsupportedOperationException("getObject(int, Class)");
    }

    public Ref getRef(String colName) throws SQLException {
        return getRef(getIndexForName(colName));
    }

    public Blob getBlob(String colName) throws SQLException {
        return getBlob(getIndexForName(colName));
    }

    public Clob getClob(String colName) throws SQLException {
        return getClob(getIndexForName(colName));
    }

    public Array getArray(String colName) throws SQLException {
        return getArray(getIndexForName(colName));
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            cal.setTime(getDate(columnIndex));
            return (Date)cal.getTime();
        }
        else {
            return null;
        }
    }

    public Date getDate(String columnName, Calendar cal) throws SQLException {
        return getDate(getIndexForName(columnName), cal);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            Time time = (Time)_actualRow[columnIndex];
            cal.setTime(time);
            return (Time)cal.getTime();
        }
        else {
            return null;
        }
    }

    public Time getTime(String columnName, Calendar cal) throws SQLException {
        return getTime(getIndexForName(columnName), cal);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        Timestamp timestamp = getTimestamp(columnIndex);
        if(timestamp != null) {
            cal.setTime(timestamp);
            return (Timestamp)cal.getTime();
        }
        else {
            return null;
        }
    }

    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        return getTimestamp(getIndexForName(columnName), cal);
    }

    public URL getURL(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return (URL)_actualRow[columnIndex];
        }
        else {
            return null;
        }
    }

    public URL getURL(String columnName) throws SQLException {
        return getURL(getIndexForName(columnName));
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new UnsupportedOperationException("updateRef");
    }

    public void updateRef(String columnName, Ref x) throws SQLException {
        throw new UnsupportedOperationException("updateRef");
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new UnsupportedOperationException("updateBlob");
    }

    public void updateBlob(String columnName, Blob x) throws SQLException {
        throw new UnsupportedOperationException("updateBlob");
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new UnsupportedOperationException("updateClob");
    }

    public void updateClob(String columnName, Clob x) throws SQLException {
        throw new UnsupportedOperationException("updateClob");
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new UnsupportedOperationException("updateArray");
    }

    public void updateArray(String columnName, Array x) throws SQLException {
        throw new UnsupportedOperationException("updateArray");
    }

    private int getIndexForName(String name) throws SQLException {
        int result = -1;
        String nameLowercase = name.toLowerCase();
        // first search in the columns names (hit is very likely)
        for(int i = 0; i < _columnNames.length; ++i) {
            if(_columnNames[i].equals(nameLowercase)) {
                result = i;
                break;
            }
        }
        // not found ? then search in the labels
        if(result < 0) {
                for(int i = 0; i < _columnLabels.length; ++i) {
                    if(_columnLabels[i].equals(nameLowercase)) {
                        result = i;
                        break;
                    }
                }
        }
        if(result < 0) {
            throw new SQLException("Unknown column " + name);
        }
        else {
            _lastReadColumn = result;
        }

        return result + 1;
    }

    private boolean preGetCheckNull(int index) {
        _lastReadColumn = index;
        boolean wasNull = _actualRow[_lastReadColumn] == null;
        return !wasNull;
    }

    private boolean requestNextRowPacket() throws SQLException {
        if(!_lastPartReached) {
            try {
                SerializableTransport st = (SerializableTransport)_commandSink.process(_remainingResultSet, new NextRowPacketCommand());
                RowPacket rsp = (RowPacket)st.getTransportee();
                if(rsp.isLastPart()) {
                    _lastPartReached = true;
                }
                if(rsp.size() > 0) {
                    _rows.merge(rsp);
                    return true;
                } else {
                    return false;
                }
            } catch(Exception e) {
                throw SQLExceptionHelper.wrap(e);
            }
        } else {
            return false;
        }
    }

    private boolean setCursor(int row) throws SQLException {
        if(row >= 0) {
            if(row < _rows.size()) {
                _cursor = row;
                _actualRow = _rows.get(_cursor);
                return true;
            } else {
                // If new row is not in the range of the actually available
                // rows then try to load the next row packets successively
                while(requestNextRowPacket()) {
                    if(row < _rows.size()) {
                        _cursor = row;
                        _actualRow = _rows.get(_cursor);
                        return true;
                    }
                }
                return false;
            }
        } else {
            return false;
        }
    }

    private Date getCleanDate(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTimeInMillis());
    }

    private Time getCleanTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.MILLISECOND, 0);
        return new Time(cal.getTimeInMillis());
    }

    /* start JDBC4 support */
    public RowId getRowId(int parameterIndex) throws SQLException {
        return (RowId)_commandSink.process(_remainingResultSet, CommandPool.getReflectiveCommand(JdbcInterfaceType.RESULTSETHOLDER, "getRowId",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public RowId getRowId(String parameterName) throws SQLException {
        return (RowId)_commandSink.process(_remainingResultSet, CommandPool.getReflectiveCommand(JdbcInterfaceType.RESULTSETHOLDER, "getRowId",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public void setRowId(String parameterName, RowId x) throws SQLException {
        throw new UnsupportedOperationException("setRowId");
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new UnsupportedOperationException("updateRowId");
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new UnsupportedOperationException("updateRowId");
    }

    public int getHoldability() throws SQLException {
        return _commandSink.processWithIntResult(_remainingResultSet, CommandPool.getReflectiveCommand(JdbcInterfaceType.RESULTSETHOLDER, "getHoldability"));
    }

    public boolean isClosed() throws SQLException {
        return (_cursor < 0);
    }

    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new UnsupportedOperationException("updateNString");
    }

    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new UnsupportedOperationException("updateNString");
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException("updateNClob");
    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException("updateNClob");
    }

    public NClob getNClob(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return (NClob)_actualRow[columnIndex];
        }
        else {
            return null;
        }
    }

    public NClob getNClob(String columnLabel) throws SQLException {
        return getNClob(getIndexForName(columnLabel));
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return (SQLXML)_actualRow[columnIndex];
        }
        else {
            return null;
        }
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return getSQLXML(getIndexForName(columnLabel));
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException("updateSQLXML");
    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException("updateSQLXML");
    }

    public String getNString(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return _actualRow[columnIndex].toString();
        } else {
            return null;
        }
    }

    public String getNString(String columnLabel) throws SQLException {
        return getNString(getIndexForName(columnLabel));
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        columnIndex--;
        if(preGetCheckNull(columnIndex)) {
            return new StringReader((String)_actualRow[columnIndex]);
        }
        else {
            return null;
        }
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getNCharacterStream(getIndexForName(columnLabel));
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException("updateNCharacterStream");
    }

    public void updateNCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException("updateNCharacterStream");
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("updateAsciiStream");
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("updateBinaryStream");
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException("updateCharacterStream");
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("updateAsciiStream");
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("updateBinaryStream");
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("updateCharacterStream");
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException("updateBlob");
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("updateClob");
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("updateClob");
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("updateNClob");
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("updateNClob");
    }

    public void updateNCharacterStream(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("updateNCharacterStream");
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("updateNCharacterStream");
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("updateAsciiStream");
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("updateBinaryStream");
    }

    public void updateCharacterStream(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("updateCharacterStream");
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("updateCharacterStream");
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("updateAsciiStream");
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("updateBinaryStream");
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException("updateBlob");
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException("updateBlob");
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("updateClob");
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("updateClob");
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("updateNClob");
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("updateNClob");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(StreamingResultSet.class);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T)this;
    }
    /* end JDBC4 support */
}
