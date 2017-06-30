// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.zip.Deflater;

public class SerializableTransport implements Externalizable {
    static final long serialVersionUID = -5634734498572640609L;

    private boolean _isCompressed;
    private Object _transportee;
    private transient Object _original;

    public SerializableTransport() {
    }
    
    public SerializableTransport(Object transportee, int compressionMode, long minimumSize) {
        deflate(transportee, compressionMode, minimumSize);
    }

    public SerializableTransport(Object transportee) {
        this(transportee, Deflater.BEST_SPEED, 2000);
    }

    public Object getTransportee() throws IOException, ClassNotFoundException {
        if(_original == null) {
            if(_isCompressed) {
                inflate();
            } else {
                _original = _transportee;
            }
        }

        return _original;
    }

    private void deflate(Object crs, int compressionMode, long minimumSize) {
        if(compressionMode != Deflater.NO_COMPRESSION) {
            try {
                byte[] serializedObject = serializeObject(crs);
                if(serializedObject.length >= minimumSize) {
                    _transportee = Zipper.zip(serializedObject, compressionMode);
                    _isCompressed = true;
                } else {
                    _transportee = crs;
                    _isCompressed = false;
                }
            } catch(IOException e) {
                _transportee = crs;
                _isCompressed = false;
            }
        } else {
            _transportee = crs;
            _isCompressed = false;
        }
    }

    private void inflate() throws IOException, ClassNotFoundException {
        byte[] unzipped = Zipper.unzip((byte[])_transportee);
        _original = deserializeObject(unzipped);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _isCompressed = in.readBoolean();
        _transportee = in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(_isCompressed);
        out.writeObject(_transportee);
    }

    private static byte[] serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        return baos.toByteArray();
    }

    private static Object deserializeObject(byte[] b) throws ClassNotFoundException, IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
}
