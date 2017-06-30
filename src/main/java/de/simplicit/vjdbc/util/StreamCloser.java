// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for closing stream securely.
 * @author Mike
 *
 */
public final class StreamCloser {
    private StreamCloser() {
    }
    
    /**
     * Closes an InputStream.
     * @param is InputStream to close
     */
    public static void close(InputStream is) {
        if(is != null) {
            try {
                is.close();
            }
            catch(IOException e) {
            }
        }
    }
    
    /**
     * Closes an OutputStream
     * @param os OutputStream to close
     */
    public static void close(OutputStream os) {
        if(os != null) {
            try {
                os.close();
            }
            catch(IOException e) {
            }
        }
    }
}
