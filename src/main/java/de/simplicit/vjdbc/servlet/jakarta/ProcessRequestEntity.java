package de.simplicit.vjdbc.servlet.jakarta;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

import de.simplicit.vjdbc.command.Command;
import de.simplicit.vjdbc.serial.CallingContext;

public class ProcessRequestEntity implements RequestEntity {
    private Long _connuid;
    private Long _uid;
    private Command _cmd;
    private CallingContext _ctx;
    
    public ProcessRequestEntity(Long connuid, Long uid, Command cmd, CallingContext ctx) {
        _connuid = connuid;
        _uid = uid;
        _cmd = cmd;
        _ctx = ctx;
    }
    
    public long getContentLength() {
        return -1; // Don't know length in advance
    }

    public String getContentType() {
        return "binary/x-java-serialized";
    }

    public boolean isRepeatable() {
        return true;
    }

    public void writeRequest(OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(_connuid);
        oos.writeObject(_uid);
        oos.writeObject(_cmd);
        oos.writeObject(_ctx);
        oos.flush();
    }
}
