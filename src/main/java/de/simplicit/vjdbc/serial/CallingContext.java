// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import java.io.*;

/**
 * This class encapsulates the context in which a remote command was called.
 * It can be used to find the location of objects that weren't disposed
 * correctly.
 */
public class CallingContext implements Externalizable {
    private static final long serialVersionUID = 3906934495134101813L;
    
    private String _stackTrace;
        
    public CallingContext() {
        Throwable t = new Exception();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("--- The orphaned object was created within the following calling context ---");
        t.printStackTrace(pw);
        pw.println("----------------------- End of calling context -----------------------------");
        _stackTrace = sw.toString();
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _stackTrace = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_stackTrace);
    }
    
    public String getStackTrace() {
        return _stackTrace;
    }
}
