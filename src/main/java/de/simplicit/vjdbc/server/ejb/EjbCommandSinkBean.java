// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.ejb;

import de.simplicit.vjdbc.command.Command;
import de.simplicit.vjdbc.serial.CallingContext;
import de.simplicit.vjdbc.serial.UIDEx;
import de.simplicit.vjdbc.server.command.CommandProcessor;
import de.simplicit.vjdbc.ejb.*;
import de.simplicit.vjdbc.util.SQLExceptionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ejb.*;
import java.sql.SQLException;
import java.util.Properties;

@Stateless
@Remote
public class EjbCommandSinkBean
    implements EjbCommandSink, EjbCommandSinkProxy {

    private static Log _logger = LogFactory.getLog(EjbCommandSinkBean.class);

    private transient CommandProcessor _processor;

    public EjbCommandSinkBean() {
        _processor = CommandProcessor.getInstance();
    }

    public UIDEx connect(String url, Properties props, Properties clientInfo,
                         CallingContext ctx)
        throws SQLException {
        try {
            UIDEx reg =
                _processor.createConnection(url, props, clientInfo, ctx);
            return reg;
        } catch (Exception e) {
            _logger.error(url, e);
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public Object process(Long connuid, Long uid, Command cmd,
                          CallingContext ctx)
        throws SQLException {

        return _processor.process(connuid, uid, cmd, ctx);
    }

    public void close() {
        _processor = null;
    }
}
