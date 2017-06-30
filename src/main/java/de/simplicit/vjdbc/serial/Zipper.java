// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Zipper {
    private static Log _logger = LogFactory.getLog(Zipper.class);

    public static byte[] zip(byte[] b, int compressionMode) throws IOException {
        Deflater deflater = new Deflater(compressionMode);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        deflater.setInput(b);
        deflater.finish();

        while (!deflater.finished()) {
            int count = deflater.deflate(buf);
            bos.write(buf, 0, count);
        }

        bos.close();

        byte[] zipped = bos.toByteArray();

        if(_logger.isDebugEnabled()) {
            _logger.debug("Deflated " + b.length + " to " + zipped.length);
        }

        return zipped;
    }

    public static byte[] unzip(byte[] b) throws IOException {
        Inflater inflater = new Inflater();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        inflater.setInput(b);

        while (!inflater.finished()) {
            try {
                int count = inflater.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
                throw new IOException(e.toString());
            }
        }

        bos.close();

        byte[] unzipped = bos.toByteArray();

        if(_logger.isDebugEnabled()) {
            _logger.debug("Inflated " + b.length + " to " + unzipped.length);
        }

        return unzipped;
    }
}
