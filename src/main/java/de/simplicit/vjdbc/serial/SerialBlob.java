// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;

public class SerialBlob implements Blob, Externalizable {
    private static final long serialVersionUID = 3258134639489857079L;

    private byte[] _data;

    public SerialBlob() {
    }

    public SerialBlob(Blob other) throws SQLException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = other.getBinaryStream();
            byte[] buff = new byte[1024];
            int len;
            while((len = is.read(buff)) > 0) {
                baos.write(buff, 0, len);
            }
            _data = baos.toByteArray();
            other.free();
        } catch(IOException e) {
            throw new SQLException("Can't retrieve contents of Blob", e.toString());
        }
    }

    public SerialBlob(InputStream is) throws SQLException {
        try {
            init(is);
        } catch(IOException e) {
            throw new SQLException("Can't retrieve contents of Clob", e.toString());
        }
    }

    public SerialBlob(InputStream is, long length) throws SQLException {
        try {
            init(is, length);
        } catch(IOException e) {
            throw new SQLException("Can't retrieve contents of Clob", e.toString());
        }
    }

    public void init(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len;
        while((len = is.read(buff)) > 0) {
            baos.write(buff, 0, len);
        }
        _data = baos.toByteArray();
    }

    public void init(InputStream is, long length) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len;
        long toRead = length;
        while (toRead > 0 && (len = is.read(buff, 0, (int)(toRead > 1024 ? 1024 : toRead))) > 0) {
            baos.write(buff, 0, len);
            toRead -= len;
        }
        _data = baos.toByteArray();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_data);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _data = (byte[])in.readObject();
    }

    public long length() throws SQLException {
        return _data.length;
    }

    public byte[] getBytes(long pos, int length) throws SQLException {
        if (pos <= Integer.MAX_VALUE) {
            byte[] result = new byte[length];
            System.arraycopy(_data, (int)pos - 1, result, 0, length);
            return result;
        }

        // very slow but gets around problems with the pos being represented
        // as long instead of an int in most java.io and other byte copying
        // APIs
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (long i = 0; i < length; ++i) {
            baos.write(_data[(int)(pos + i)]);
        }
        return baos.toByteArray();
    }

    public InputStream getBinaryStream() throws SQLException {
        return new ByteArrayInputStream(_data);
    }

    public long position(byte pattern[], long start) throws SQLException {
        throw new UnsupportedOperationException("Blob.position");
    }

    public long position(Blob pattern, long start) throws SQLException {
        throw new UnsupportedOperationException("Blob.position");
    }

    public int setBytes(long pos, byte[] bytes) throws SQLException {
        throw new UnsupportedOperationException("Blob.setBytes");
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        throw new UnsupportedOperationException("Blob.setBytes");
    }

    public OutputStream setBinaryStream(long pos) throws SQLException {
        throw new UnsupportedOperationException("Blob.setBinaryStream");
    }

    public void truncate(long len) throws SQLException {
        throw new UnsupportedOperationException("Blob.truncate");
    }

    /* start JDBC4 support */
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        if (pos <= Integer.MAX_VALUE && length <= Integer.MAX_VALUE) {
            return new ByteArrayInputStream(_data, (int)pos, (int)length);
        }

        // very slow but gets around problems with the pos being represented
        // as long instead of an int in most java.io and other byte copying
        // APIs
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (long i = 0; i < length; ++i) {
            baos.write(_data[(int)(i + pos)]);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public void free() throws SQLException {
        _data = null;
    }
    /* end JDBC4 support */
}
