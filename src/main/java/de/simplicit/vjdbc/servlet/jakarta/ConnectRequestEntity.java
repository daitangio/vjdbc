// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.servlet.jakarta;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.httpclient.methods.RequestEntity;

import de.simplicit.vjdbc.serial.CallingContext;

/**
 * RequestEntity implementation which streams all of the parameters necessary for
 * a connect request. 
 * @author Mike
 */
class ConnectRequestEntity implements RequestEntity {
    private String _database;
    private Properties _props;
    private Properties _clientInfo;
    private CallingContext _ctx;
    
    public ConnectRequestEntity(String database, Properties props, Properties clientInfo, CallingContext ctx) {
        _database = database;
        _props = props;
        _clientInfo = clientInfo;
        _ctx = ctx;
    }
    
    public long getContentLength() {
        return -1; // Don't know the length in advance
    }

    public String getContentType() {
        return "binary/x-java-serialized";
    }

    public boolean isRepeatable() {
        return true;
    }

    public void writeRequest(OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeUTF(_database);
        oos.writeObject(_props);
        oos.writeObject(_clientInfo);
        oos.writeObject(_ctx);
        oos.flush();
    }
}
