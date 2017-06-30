//VJDBC - Virtual JDBC
//Written by Michael Link
//Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * This class inherits from the GenericObjectPool and provides a little bit
 * of logging when eviction happens.
 * @author Mike
 */
public class LoggingGenericObjectPool extends GenericObjectPool {
    private static Log _logger = LogFactory.getLog(LoggingGenericObjectPool.class);
    
    private String _idOfConnection;

    public LoggingGenericObjectPool(String nameOfConnection) {
        super(null);
        _idOfConnection = nameOfConnection;
    }
    
    public LoggingGenericObjectPool(String nameOfConnection, GenericObjectPool.Config config) {
        super(null, config);
        _idOfConnection = nameOfConnection;
    }
        
    public synchronized void evict() throws Exception {
        super.evict();
        if(_logger.isDebugEnabled()) {
            _logger.debug("DBCP-Evictor: number of idle connections in '" + _idOfConnection + "' = " + getNumIdle());
        }
    }
}
