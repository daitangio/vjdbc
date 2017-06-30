// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import java.io.*;
import java.sql.Clob;
import java.sql.SQLException;

import de.simplicit.vjdbc.util.SQLExceptionHelper;

public class SerialClob implements Clob, Externalizable {
    private static final long serialVersionUID = 3904682695287452212L;

    protected char[] _data;

    public SerialClob() {
    }

    public SerialClob(Clob other) throws SQLException {
        try {
            StringWriter sw = new StringWriter();
            Reader rd = other.getCharacterStream();
            char[] buff = new char[1024];
            int len;
            while((len = rd.read(buff)) > 0) {
                sw.write(buff, 0, len);
            }
            _data = sw.toString().toCharArray();
            other.free();
        } catch(IOException e) {
            throw new SQLException("Can't retrieve contents of Clob", e.toString());
        }
    }

    public SerialClob(Reader rd) throws SQLException {
        try {
            init(rd);
        } catch(IOException e) {
            throw new SQLException("Can't retrieve contents of Clob", e.toString());
        }
    }

    public SerialClob(Reader rd, long length) throws SQLException {
        try {
            init(rd, length);
        } catch(IOException e) {
            throw new SQLException("Can't retrieve contents of Clob", e.toString());
        }
    }

    public void init(Reader rd) throws IOException {
        StringWriter sw = new StringWriter();
        char[] buff = new char[1024];
        int len;
        while((len = rd.read(buff)) > 0) {
            sw.write(buff, 0, len);
        }
        _data = sw.toString().toCharArray();
    }

    public void init(Reader rd, long length) throws IOException {
        StringWriter sw = new StringWriter();
        char[] buff = new char[1024];
        int len;
        long toRead = length;
        while(toRead > 0 && (len = rd.read(buff, 0, (int)(toRead > 1024 ? 1024 : toRead))) > 0) {
            sw.write(buff, 0, len);
            toRead -= len;
        }
        _data = sw.toString().toCharArray();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_data);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _data = (char[])in.readObject();
    }

    public long length() throws SQLException {
        return _data.length;
    }

    public String getSubString(long pos, int length) throws SQLException {
        if (pos <= Integer.MAX_VALUE) {
            return new String(_data, (int)pos - 1, length);
        }
        // very slow but gets around problems with the pos being represented
        // as long instead of an int in most java.io and other byte copying
        // APIs
        CharArrayWriter writer = new CharArrayWriter(length);
        for (long i = 0; i < length; ++i) {
            writer.write(_data[(int)(pos + i)]);
        }
        return writer.toString();
    }

    public Reader getCharacterStream() throws SQLException {
        return new StringReader(new String(_data));
    }

    public InputStream getAsciiStream() throws SQLException {
        try {
            return new ByteArrayInputStream(new String(_data).getBytes("US-ASCII"));
        } catch(UnsupportedEncodingException e) {
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public long position(String searchstr, long start) throws SQLException {
        throw new UnsupportedOperationException("SerialClob.position");
    }

    public long position(Clob searchstr, long start) throws SQLException {
        throw new UnsupportedOperationException("SerialClob.position");
    }

    public int setString(long pos, String str) throws SQLException {
        throw new UnsupportedOperationException("SerialClob.setString");
    }

    public int setString(long pos, String str, int offset, int len) throws SQLException {
        throw new UnsupportedOperationException("SerialClob.setString");
    }

    public OutputStream setAsciiStream(long pos) throws SQLException {
        throw new UnsupportedOperationException("SerialClob.setAsciiStream");
    }

    public Writer setCharacterStream(long pos) throws SQLException {
        throw new UnsupportedOperationException("SerialClob.setCharacterStream");
    }

    public void truncate(long len) throws SQLException {
        throw new UnsupportedOperationException("SerialClob.truncate");
    }

    /* start JDBC4 support */
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        if (pos <= Integer.MAX_VALUE && length <= Integer.MAX_VALUE) {
            return new CharArrayReader(_data, (int)pos, (int)length);
        }
        // very slow but gets around problems with the pos being represented
        // as long instead of an int in most java.io and other byte copying
        // APIs
        CharArrayWriter writer = new CharArrayWriter((int)length);
        for (long i = 0; i < length; ++i) {
            writer.write(_data[(int)(pos + i)]);
        }
        return new CharArrayReader(writer.toCharArray());
    }

    public void free() throws SQLException {
        _data = null;
    }
    /* end JDBC4 support */
}
