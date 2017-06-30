// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.simplicit.vjdbc.command.CommandPool;
import de.simplicit.vjdbc.command.DecoratedCommandSink;
import de.simplicit.vjdbc.command.JdbcInterfaceType;
import de.simplicit.vjdbc.command.PreparedStatementExecuteBatchCommand;
import de.simplicit.vjdbc.command.PreparedStatementExecuteCommand;
import de.simplicit.vjdbc.command.PreparedStatementQueryCommand;
import de.simplicit.vjdbc.command.PreparedStatementUpdateCommand;
import de.simplicit.vjdbc.parameters.*;
import de.simplicit.vjdbc.serial.*;
import de.simplicit.vjdbc.util.SQLExceptionHelper;

public class VirtualPreparedStatement extends VirtualStatement implements PreparedStatement {
    private static PreparedStatementParameter[] _emptyParameters = new PreparedStatementParameter[0];

    protected PreparedStatementParameter[] _paramList = new PreparedStatementParameter[10];
    protected int _maxIndex = 0;

    public VirtualPreparedStatement(UIDEx reg, Connection connection, String sql, DecoratedCommandSink sink, int resultSetType) {
        super(reg, connection, sink, resultSetType);
    }

    public ResultSet executeQuery() throws SQLException {
        StreamingResultSet result = null;

        try {
            reduceParam();

            SerializableTransport st = (SerializableTransport) _sink.process(_objectUid,
                    new PreparedStatementQueryCommand(_paramList, _resultSetType), true);
            result = (StreamingResultSet) st.getTransportee();
            result.setStatement(this);
            result.setCommandSink(_sink);
        } catch (Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }

        return result;
    }

    public int executeUpdate() throws SQLException {
        reduceParam();
        return _sink.processWithIntResult(_objectUid, new PreparedStatementUpdateCommand(_paramList));
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        setParam(parameterIndex, new NullParameter(sqlType, null));
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        setParam(parameterIndex, new BooleanParameter(x));
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        setParam(parameterIndex, new ByteParameter(x));
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        setParam(parameterIndex, new ShortParameter(x));
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        setParam(parameterIndex, new IntegerParameter(x));
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        setParam(parameterIndex, new LongParameter(x));
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        setParam(parameterIndex, new FloatParameter(x));
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        setParam(parameterIndex, new DoubleParameter(x));
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        setParam(parameterIndex, new BigDecimalParameter(x));
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        setParam(parameterIndex, new StringParameter(x));
    }

    public void setBytes(int parameterIndex, byte x[]) throws SQLException {
        setParam(parameterIndex, new ByteArrayParameter(x));
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        setParam(parameterIndex, new DateParameter(x, null));
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        setParam(parameterIndex, new TimeParameter(x, null));
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        setParam(parameterIndex, new TimestampParameter(x, null));
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParam(parameterIndex, new ByteStreamParameter(ByteStreamParameter.TYPE_ASCII, x, length));
    }

    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParam(parameterIndex, new ByteStreamParameter(ByteStreamParameter.TYPE_UNICODE, x, length));
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        setParam(parameterIndex, new ByteStreamParameter(ByteStreamParameter.TYPE_BINARY, x, length));
    }

    public void clearParameters() throws SQLException {
        for (int i = 0; i < _paramList.length; ++i) {
            _paramList[i] = null;
        }
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        setParam(parameterIndex, new ObjectParameter(x, new Integer(targetSqlType), new Integer(scale)));
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        setParam(parameterIndex, new ObjectParameter(x, new Integer(targetSqlType), null));
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        setParam(parameterIndex, new ObjectParameter(x, null, null));
    }

    public boolean execute() throws SQLException {
        reduceParam();
        return _sink.processWithBooleanResult(_objectUid, new PreparedStatementExecuteCommand(_paramList));
    }

    public void addBatch() throws SQLException {
        reduceParam();
        PreparedStatementParameter[] paramListClone = new PreparedStatementParameter[_paramList.length];
        System.arraycopy(_paramList, 0, paramListClone, 0, _paramList.length);
        _batchCollector.add(paramListClone);
        clearParameters();
    }

    public int[] executeBatch() throws SQLException {
        try {
            return (int[]) _sink.process(_objectUid, new PreparedStatementExecuteBatchCommand(_batchCollector));
        } finally {
            _batchCollector.clear();
        }
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        setParam(parameterIndex, new CharStreamParameter(reader, length));
    }

    public void setRef(int i, Ref x) throws SQLException {
        setParam(i, new RefParameter(x));
    }

    public void setBlob(int i, Blob x) throws SQLException {
        setParam(i, new BlobParameter(x));
    }

    public void setClob(int i, Clob x) throws SQLException {
        setParam(i, new ClobParameter(x));
    }

    public void setArray(int i, Array x) throws SQLException {
        setParam(i, new ArrayParameter(x));
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            SerializableTransport st = (SerializableTransport) _sink.process(_objectUid, CommandPool
                    .getReflectiveCommand(JdbcInterfaceType.PREPAREDSTATEMENT, "getMetaData"));
            return (SerialResultSetMetaData) st.getTransportee();
        } catch (Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        setParam(parameterIndex, new DateParameter(x, cal));
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        setParam(parameterIndex, new TimeParameter(x, cal));
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        setParam(parameterIndex, new TimestampParameter(x, cal));
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        setParam(parameterIndex, new NullParameter(sqlType, typeName));
    }

    public void setURL(int parameterIndex, URL x) throws SQLException {
        setParam(parameterIndex, new URLParameter(x));
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new UnsupportedOperationException("getParameterMetaData");
    }

    protected void setParam(int index, PreparedStatementParameter parm) {
        if(_paramList.length < index) {
            List tmp = Arrays.asList(_paramList);
            PreparedStatementParameter[] newArray = new PreparedStatementParameter[index * 2];
            _paramList = (PreparedStatementParameter[]) tmp.toArray(newArray);
        }

        if(_maxIndex < index) {
            _maxIndex = index;
        }

        _paramList[index - 1] = parm;
    }

    protected void reduceParam() {
        if(_maxIndex > 0) {
            PreparedStatementParameter[] tmpArray = new PreparedStatementParameter[_maxIndex];
            System.arraycopy(_paramList, 0, tmpArray, 0, _maxIndex);
            _paramList = tmpArray;
        } else {
            _paramList = _emptyParameters;
        }
    }

    /* start JDBC4 support */
    public void setRowId(int parameterIndex, RowId rowId) throws SQLException {
        setParam(parameterIndex, new RowIdParameter(rowId));
    }

    public void setNString(int parameterIndex, String value) throws SQLException {
        setParam(parameterIndex, new StringParameter(value));
    }

    public void setNCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        setParam(parameterIndex, new CharStreamParameter(reader, length));
    }

    public void setNClob(int i, NClob x) throws SQLException {
        setParam(i, new ClobParameter(x));
    }

    public void setClob(int i, Reader reader, long length) throws SQLException {
        setParam(i, new ClobParameter(new SerialClob(reader, length)));
    }

    public void setBlob(int i, InputStream inputStream, long length) throws SQLException {
        setParam(i, new BlobParameter(new SerialBlob(inputStream, length)));
    }

    public void setNClob(int i, Reader reader, long length) throws SQLException {
        setParam(i, new ClobParameter(new SerialNClob(reader, length)));
    }

    public void setSQLXML(int i, SQLXML xmlObject) throws SQLException {
        setParam(i, new SQLXMLParameter(new SerialSQLXML(xmlObject)));
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        setAsciiStream(parameterIndex, x, -1);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setParam(parameterIndex, new ByteStreamParameter(ByteStreamParameter.TYPE_ASCII, x, length));
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        setBinaryStream(parameterIndex, x, -1);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setParam(parameterIndex, new ByteStreamParameter(ByteStreamParameter.TYPE_BINARY, x, length));
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        setParam(parameterIndex, new CharStreamParameter(reader));
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        setParam(parameterIndex, new CharStreamParameter(reader, length));
    }

    public void setNCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        setParam(parameterIndex, new CharStreamParameter(reader));
    }

    public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        setParam(parameterIndex, new CharStreamParameter(reader, length));
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        setParam(parameterIndex, new ClobParameter(new SerialClob(reader)));
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        setParam(parameterIndex, new BlobParameter(new SerialBlob(inputStream)));
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        setParam(parameterIndex, new ClobParameter(new SerialNClob(reader)));
    }
    /* end JDBC4 support */
}
