// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import java.io.*;
import java.sql.NClob;
import java.sql.SQLException;

import de.simplicit.vjdbc.util.SQLExceptionHelper;

public class SerialNClob extends SerialClob implements NClob {
    static final long serialVersionUID = -869122661664868443L;

    public SerialNClob() {
    }

    public SerialNClob(NClob other) throws SQLException {
        super(other);
    }

    public SerialNClob(Reader rd) throws SQLException {
        super(rd);
    }

    public SerialNClob(Reader rd, long length) throws SQLException {
        super(rd, length);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_data);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _data = (char[])in.readObject();
    }
}
