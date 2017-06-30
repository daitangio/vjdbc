// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc;

import de.simplicit.vjdbc.command.*;
import de.simplicit.vjdbc.serial.*;
import de.simplicit.vjdbc.util.SQLExceptionHelper;

import java.io.InputStream;
import java.io.Reader;
import java.io.CharArrayReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

public class VirtualCallableStatement extends VirtualPreparedStatement implements CallableStatement {
    VirtualCallableStatement(UIDEx reg, Connection connection, String sql, DecoratedCommandSink sink, int resultSetType) {
        super(reg, connection, sql, sink, resultSetType);
    }

    protected void finalize() throws Throwable {
        close();
    }

    public void registerOutParameter(int parameterIndex, int sqlType)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT,
                "registerOutParameter",
                new Object[]{new Integer(parameterIndex), new Integer(sqlType)},
                ParameterTypeCombinations.INTINT));
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT,
                "registerOutParameter",
                new Object[]{new Integer(parameterIndex),
                             new Integer(sqlType),
                             new Integer(scale)},
                ParameterTypeCombinations.INTINTINT));
    }

    public boolean wasNull() throws SQLException {
        return _sink.processWithBooleanResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "wasNull"));
    }

    public String getString(int parameterIndex) throws SQLException {
        return (String)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getString",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        return _sink.processWithBooleanResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getBoolean",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public byte getByte(int parameterIndex) throws SQLException {
        return _sink.processWithByteResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getByte",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public short getShort(int parameterIndex) throws SQLException {
        return _sink.processWithShortResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getShort",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public int getInt(int parameterIndex) throws SQLException {
        return _sink.processWithIntResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getInt",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public long getLong(int parameterIndex) throws SQLException {
        return _sink.processWithLongResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getLong",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public float getFloat(int parameterIndex) throws SQLException {
        return _sink.processWithFloatResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getFloat",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public double getDouble(int parameterIndex) throws SQLException {
        return _sink.processWithDoubleResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getDouble",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public BigDecimal getBigDecimal(int parameterIndex, int scale)
            throws SQLException {
        return (BigDecimal)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getBigDecimal",
                new Object[]{new Integer(parameterIndex), new Integer(scale)},
                ParameterTypeCombinations.INTINT));
    }

    public byte[] getBytes(int parameterIndex) throws SQLException {
        return (byte[])_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getBytes",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public Date getDate(int parameterIndex) throws SQLException {
        return (Date)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getDate",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public Time getTime(int parameterIndex) throws SQLException {
        return (Time)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getTime",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public Timestamp getTimestamp(int parameterIndex)
            throws SQLException {
        return (Timestamp)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getTimestamp",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public Object getObject(int parameterIndex) throws SQLException {
        try {
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, new CallableStatementGetObjectCommand(parameterIndex));
            Object transportee = st.getTransportee();
            checkTransporteeForStreamingResultSet(transportee);
            return transportee;
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return (BigDecimal)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getBigDecimal",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public Object getObject(int i, Map map) throws SQLException {
        try {
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, new CallableStatementGetObjectCommand(i, map));
            Object transportee = st.getTransportee();
            checkTransporteeForStreamingResultSet(transportee);
            return transportee;
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }

    }

    public Ref getRef(int i) throws SQLException {
        try {
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, new CallableStatementGetRefCommand(i));
            return (Ref)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }

    }

    public Blob getBlob(int i) throws SQLException {
        try {
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, new CallableStatementGetBlobCommand(i));
            return (Blob)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Clob getClob(int i) throws SQLException {
        try {
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, new CallableStatementGetClobCommand(i));
            return (Clob)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Array getArray(int i) throws SQLException {
        try {
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, new CallableStatementGetArrayCommand(i));
            return (Array)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Date getDate(int parameterIndex, Calendar cal)
            throws SQLException {
        return (Date)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getDate",
                new Object[]{new Integer(parameterIndex), cal},
                ParameterTypeCombinations.INTCAL));
    }

    public Time getTime(int parameterIndex, Calendar cal)
            throws SQLException {
        return (Time)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getTime",
                new Object[]{new Integer(parameterIndex), cal},
                ParameterTypeCombinations.INTCAL));
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal)
            throws SQLException {
        return (Timestamp)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getTimestamp",
                new Object[]{new Integer(parameterIndex), cal},
                ParameterTypeCombinations.INTCAL));
    }

    public void registerOutParameter(int paramIndex, int sqlType, String typeName)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "registerOutParameter",
                new Object[]{new Integer(paramIndex), new Integer(sqlType), typeName},
                ParameterTypeCombinations.INTINTSTR));
    }

    public void registerOutParameter(String parameterName, int sqlType)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "registerOutParameter",
                new Object[]{parameterName, new Integer(sqlType)},
                ParameterTypeCombinations.STRINT));
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "registerOutParameter",
                new Object[]{parameterName, new Integer(sqlType), new Integer(scale)},
                ParameterTypeCombinations.STRINTINT));
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "registerOutParameter",
                new Object[]{parameterName, new Integer(sqlType), typeName},
                ParameterTypeCombinations.STRINTSTR));
    }

    public URL getURL(int parameterIndex) throws SQLException {
        return (URL)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getURL",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public void setURL(String parameterName, URL val) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setURL",
                new Object[]{parameterName, val},
                ParameterTypeCombinations.STRURL));
    }

    public void setNull(String parameterName, int sqlType) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setNull",
                new Object[]{parameterName, new Integer(sqlType)},
                ParameterTypeCombinations.STRINT));
    }

    public void setBoolean(String parameterName, boolean x) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setBoolean",
                new Object[]{parameterName, x ? Boolean.TRUE : Boolean.FALSE},
                ParameterTypeCombinations.STRBOL));
    }

    public void setByte(String parameterName, byte x) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setByte",
                new Object[]{parameterName, new Byte(x)},
                ParameterTypeCombinations.STRBYT));
    }

    public void setShort(String parameterName, short x) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setShort",
                new Object[]{parameterName, new Short(x)},
                ParameterTypeCombinations.STRSHT));
    }

    public void setInt(String parameterName, int x) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setInt",
                new Object[]{parameterName, new Integer(x)},
                ParameterTypeCombinations.STRINT));
    }

    public void setLong(String parameterName, long x) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setLong",
                new Object[]{parameterName, new Long(x)},
                ParameterTypeCombinations.STRLNG));
    }

    public void setFloat(String parameterName, float x) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setFloat",
                new Object[]{parameterName, new Float(x)},
                ParameterTypeCombinations.STRFLT));
    }

    public void setDouble(String parameterName, double x) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setDouble",
                new Object[]{parameterName, new Double(x)},
                ParameterTypeCombinations.STRDBL));
    }

    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setBigDecimal",
                new Object[]{parameterName, x},
                ParameterTypeCombinations.STRBID));
    }

    public void setString(String parameterName, String x) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setString",
                new Object[]{parameterName, x},
                ParameterTypeCombinations.STRSTR));
    }

    public void setBytes(String parameterName, byte x[]) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setBytes",
                new Object[]{parameterName, x},
                ParameterTypeCombinations.STRBYTA));
    }

    public void setDate(String parameterName, Date x)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setDate",
                new Object[]{parameterName, x},
                ParameterTypeCombinations.STRDAT));
    }

    public void setTime(String parameterName, Time x)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setTime",
                new Object[]{parameterName, x},
                ParameterTypeCombinations.STRTIM));
    }

    public void setTimestamp(String parameterName, Timestamp x)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setTimestamp",
                new Object[]{parameterName, x},
                ParameterTypeCombinations.STRTMS));
    }

    public void setAsciiStream(String parameterName, InputStream x, int length)
            throws SQLException {
        try {
            _sink.process(_objectUid, new CallableStatementSetAsciiStreamCommand(parameterName, x, length));
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setBinaryStream(String parameterName, InputStream x,
                                int length) throws SQLException {
        try {
            _sink.process(_objectUid, new CallableStatementSetBinaryStreamCommand(parameterName, x, length));
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale)
            throws SQLException {
        CallableStatementSetObjectCommand cmd = new CallableStatementSetObjectCommand(parameterName,
                new Integer(targetSqlType),
                new Integer(scale));
        cmd.setObject(x);
        _sink.process(_objectUid, cmd);
    }

    public void setObject(String parameterName, Object x, int targetSqlType)
            throws SQLException {
        CallableStatementSetObjectCommand cmd = new CallableStatementSetObjectCommand(parameterName,
                new Integer(targetSqlType),
                null);
        cmd.setObject(x);
        _sink.process(_objectUid, cmd);
    }

    public void setObject(String parameterName, Object x) throws SQLException {
        CallableStatementSetObjectCommand cmd = new CallableStatementSetObjectCommand(parameterName,
                null,
                null);
        cmd.setObject(x);
        _sink.process(_objectUid, cmd);
    }

    public void setCharacterStream(String parameterName,
                                   Reader reader,
                                   int length) throws SQLException {
        try {
            CallableStatementSetCharacterStreamCommand cmd = new CallableStatementSetCharacterStreamCommand(parameterName, reader, length);
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setDate(String parameterName, Date x, Calendar cal)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setDate",
                new Object[]{parameterName, x, cal},
                ParameterTypeCombinations.STRDATCAL));
    }

    public void setTime(String parameterName, Time x, Calendar cal)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setTime",
                new Object[]{parameterName, x, cal},
                ParameterTypeCombinations.STRTIMCAL));
    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setTimestamp",
                new Object[]{parameterName, x, cal},
                ParameterTypeCombinations.STRTMSCAL));
    }

    public void setNull(String parameterName, int sqlType, String typeName)
            throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setNull",
                new Object[]{parameterName, new Integer(sqlType), typeName},
                ParameterTypeCombinations.STRINTSTR));
    }

    public String getString(String parameterName) throws SQLException {
        return (String)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getString",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public boolean getBoolean(String parameterName) throws SQLException {
        return _sink.processWithBooleanResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getBoolean",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public byte getByte(String parameterName) throws SQLException {
        return _sink.processWithByteResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getByte",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public short getShort(String parameterName) throws SQLException {
        return _sink.processWithShortResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getShort",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public int getInt(String parameterName) throws SQLException {
        return _sink.processWithIntResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getInt",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public long getLong(String parameterName) throws SQLException {
        return _sink.processWithLongResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getLong",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public float getFloat(String parameterName) throws SQLException {
        return _sink.processWithFloatResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getFloat",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public double getDouble(String parameterName) throws SQLException {
        return _sink.processWithDoubleResult(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getDouble",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        return (byte[])_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getBytes",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public Date getDate(String parameterName) throws SQLException {
        return (Date)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getDate",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public Time getTime(String parameterName) throws SQLException {
        return (Time)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getTime",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        return (Timestamp)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getTimestamp",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public Object getObject(String parameterName) throws SQLException {
        try {
            CallableStatementGetObjectCommand cmd = new CallableStatementGetObjectCommand(parameterName);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            Object transportee = st.getTransportee();
            checkTransporteeForStreamingResultSet(transportee);
            return transportee;
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return (BigDecimal)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getBigDecimal",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public Object getObject(String parameterName, Map map) throws SQLException {
        try {
            CallableStatementGetObjectCommand cmd = new CallableStatementGetObjectCommand(parameterName, map);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            Object transportee = st.getTransportee();
            checkTransporteeForStreamingResultSet(transportee);
            return transportee;
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Ref getRef(String parameterName) throws SQLException {
        try {
            CallableStatementGetRefCommand cmd = new CallableStatementGetRefCommand(parameterName);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return (SerialRef)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Blob getBlob(String parameterName) throws SQLException {
        try {
            CallableStatementGetBlobCommand cmd = new CallableStatementGetBlobCommand(parameterName);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return (SerialBlob)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Clob getClob(String parameterName) throws SQLException {
        try {
            CallableStatementGetClobCommand cmd = new CallableStatementGetClobCommand(parameterName);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return (SerialClob)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Array getArray(String parameterName) throws SQLException {
        try {
            CallableStatementGetArrayCommand cmd = new CallableStatementGetArrayCommand(parameterName);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return (SerialArray)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Date getDate(String parameterName, Calendar cal)
            throws SQLException {
        return (Date)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getDate",
                new Object[]{parameterName, cal},
                ParameterTypeCombinations.STRCAL));
    }

    public Time getTime(String parameterName, Calendar cal)
            throws SQLException {
        return (Time)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getTime",
                new Object[]{parameterName, cal},
                ParameterTypeCombinations.STRCAL));
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal)
            throws SQLException {
        return (Timestamp)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getTimestamp",
                new Object[]{parameterName, cal},
                ParameterTypeCombinations.STRCAL));
    }

    public URL getURL(String parameterName) throws SQLException {
        return (URL)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getURL",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    private void checkTransporteeForStreamingResultSet(Object transportee) {
        // The transportee might be a StreamingResultSet (i.e. Oracle can return database cursors). Thus
        // we must check the transportee and set some references correspondingly when it is a ResultSet.
        if(transportee instanceof StreamingResultSet) {
            StreamingResultSet srs = (StreamingResultSet)transportee;
            srs.setStatement(this);
            srs.setCommandSink(_sink);
        }
    }

    /* start JDBC4 support */
    public RowId getRowId(int parameterIndex) throws SQLException {
        return (RowId)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getRowId",
                new Object[]{new Integer(parameterIndex)},
                ParameterTypeCombinations.INT));
    }

    public RowId getRowId(String parameterName) throws SQLException {
        return (RowId)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getRowId",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public void setRowId(String parameterName, RowId x) throws SQLException {
        try {
            CallableStatementSetRowIdCommand cmd =
                new CallableStatementSetRowIdCommand(parameterName, new SerialRowId(x));
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setNString(String parameterName, String x) throws SQLException {
        _sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "setNString",
                new Object[]{parameterName, x},
                ParameterTypeCombinations.STRSTR));
    }

    public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        try {
            CallableStatementSetNCharacterStreamCommand cmd = new CallableStatementSetNCharacterStreamCommand(parameterName, reader, (int)length);
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setNClob(String parameterName, NClob value) throws SQLException {
        try {
            CallableStatementSetNClobCommand cmd = new CallableStatementSetNClobCommand(parameterName, value);
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        try {
            CallableStatementSetClobCommand cmd =
                new CallableStatementSetClobCommand(parameterName, new SerialClob(reader, length));
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        try {
            CallableStatementSetBlobCommand cmd =
                new CallableStatementSetBlobCommand(parameterName, new SerialBlob(inputStream, length));
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        try {
            CallableStatementSetNClobCommand cmd =
                new CallableStatementSetNClobCommand(parameterName, new SerialNClob(reader, length));
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public NClob getNClob(int parameterIndex) throws SQLException {
        try {
            CallableStatementGetNClobCommand cmd = new CallableStatementGetNClobCommand(parameterIndex);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return (SerialNClob)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public NClob getNClob(String parameterName) throws SQLException {
        try {
            CallableStatementGetNClobCommand cmd = new CallableStatementGetNClobCommand(parameterName);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return (SerialNClob)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        try {
            CallableStatementSetSQLXMLCommand cmd =
                new CallableStatementSetSQLXMLCommand(parameterName, new SerialSQLXML(xmlObject));
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        try {
            CallableStatementGetSQLXMLCommand cmd = new CallableStatementGetSQLXMLCommand(parameterIndex);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return (SerialSQLXML)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public SQLXML getSQLXML(String parameterName) throws SQLException {
        try {
            CallableStatementGetSQLXMLCommand cmd = new CallableStatementGetSQLXMLCommand(parameterName);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return (SerialSQLXML)st.getTransportee();
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public String getNString(int parameterIndex) throws SQLException {
        return (String)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getNString",
                new Object[]{parameterIndex},
                ParameterTypeCombinations.INT));
    }

    public String getNString(String parameterName) throws SQLException {
        return (String)_sink.process(_objectUid, CommandPool.getReflectiveCommand(JdbcInterfaceType.CALLABLESTATEMENT, "getNString",
                new Object[]{parameterName},
                ParameterTypeCombinations.STR));
    }

    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        try {
            CallableStatementGetNCharacterStreamCommand cmd = new CallableStatementGetNCharacterStreamCommand(parameterIndex);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return new CharArrayReader((char[])st.getTransportee());
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Reader getNCharacterStream(String parameterName) throws SQLException {
        try {
            CallableStatementGetNCharacterStreamCommand cmd = new CallableStatementGetNCharacterStreamCommand(parameterName);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return new CharArrayReader((char[])st.getTransportee());
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        try {
            CallableStatementGetCharacterStreamCommand cmd = new CallableStatementGetCharacterStreamCommand(parameterIndex);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return new CharArrayReader((char[])st.getTransportee());
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Reader getCharacterStream(String parameterName) throws SQLException {
        try {
            CallableStatementGetCharacterStreamCommand cmd = new CallableStatementGetCharacterStreamCommand(parameterName);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            return new CharArrayReader((char[])st.getTransportee());
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setClob(String parameterName, Clob clob) throws SQLException {
        try {
            CallableStatementSetClobCommand cmd =
                new CallableStatementSetClobCommand(parameterName, clob);
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setBlob(String parameterName, Blob blob) throws SQLException {
        try {
            CallableStatementSetBlobCommand cmd =
                new CallableStatementSetBlobCommand(parameterName, blob);
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        try {
            _sink.process(_objectUid, new CallableStatementSetAsciiStreamCommand(parameterName, x, (int)length));
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        try {
            _sink.process(_objectUid, new CallableStatementSetBinaryStreamCommand(parameterName, x, (int)length));
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        try {
            CallableStatementSetCharacterStreamCommand cmd = new CallableStatementSetCharacterStreamCommand(parameterName, reader, (int)length);
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        try {
            _sink.process(_objectUid, new CallableStatementSetAsciiStreamCommand(parameterName, x));
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        try {
            _sink.process(_objectUid, new CallableStatementSetBinaryStreamCommand(parameterName, x));
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        try {
            CallableStatementSetCharacterStreamCommand cmd = new CallableStatementSetCharacterStreamCommand(parameterName, reader);
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setNCharacterStream(String parameterName, Reader reader) throws SQLException {
        try {
            CallableStatementSetNCharacterStreamCommand cmd = new CallableStatementSetNCharacterStreamCommand(parameterName, reader);
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setClob(String parameterName, Reader reader) throws SQLException {
        try {
            CallableStatementSetClobCommand cmd =
                new CallableStatementSetClobCommand(parameterName, new SerialClob(reader));
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        try {
            CallableStatementSetBlobCommand cmd =
                new CallableStatementSetBlobCommand(parameterName, new SerialBlob(inputStream));
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public void setNClob(String parameterName, Reader reader) throws SQLException {
        try {
            CallableStatementSetNClobCommand cmd =
                new CallableStatementSetNClobCommand(parameterName, new SerialNClob(reader));
            _sink.process(_objectUid, cmd);
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    /* end JDBC4 support */

    /* start JDK7 support */
    public <T> T getObject(int parameterIndex, Class<T> clazz)
        throws SQLException {
        try {
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, new CallableStatementGetObjectCommand(parameterIndex, clazz));
            Object transportee = st.getTransportee();
            checkTransporteeForStreamingResultSet(transportee);
            return (T)transportee;
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public <T> T getObject(String parameterName, Class<T> clazz)
        throws SQLException {
        try {
            CallableStatementGetObjectCommand cmd = new CallableStatementGetObjectCommand(parameterName, clazz);
            SerializableTransport st = (SerializableTransport)_sink.process(_objectUid, cmd);
            Object transportee = st.getTransportee();
            checkTransporteeForStreamingResultSet(transportee);
            return (T)transportee;
        } catch(Exception e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }
    /* end JDK7 support */
}
