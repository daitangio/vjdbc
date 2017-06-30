// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.command;

import de.simplicit.vjdbc.ProxiedObject;
import de.simplicit.vjdbc.command.Command;
import de.simplicit.vjdbc.command.ConnectionContext;
import de.simplicit.vjdbc.command.DestroyCommand;
import de.simplicit.vjdbc.command.JdbcInterfaceType;
import de.simplicit.vjdbc.command.StatementCancelCommand;
import de.simplicit.vjdbc.command.ResultSetProducerCommand;
import de.simplicit.vjdbc.serial.CallingContext;
import de.simplicit.vjdbc.serial.SerialResultSetMetaData;
import de.simplicit.vjdbc.serial.SerializableTransport;
import de.simplicit.vjdbc.serial.StreamingResultSet;
import de.simplicit.vjdbc.serial.UIDEx;
import de.simplicit.vjdbc.server.config.ConnectionConfiguration;
import de.simplicit.vjdbc.server.config.VJdbcConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.*;

class ConnectionEntry implements ConnectionContext {
    private static Log _logger = LogFactory.getLog(ConnectionEntry.class);

    // Unique identifier for the ConnectionEntry
    private Long _uid;
    // The real JDBC-Connection
    private Connection _connection;
    // Configuration information
    private ConnectionConfiguration _connectionConfiguration;
    // Properties delivered from the client
    private Properties _clientInfo;
    // Flag that signals the activity of this connection
    private boolean _active = false;

    // Statistics
    private long _lastAccessTimestamp = System.currentTimeMillis();
    private long _numberOfProcessedCommands = 0;

    // Map containing all JDBC-Objects which are created by this Connection
    // entry
    private Map<Long, JdbcObjectHolder> _jdbcObjects =
        Collections.synchronizedMap(new HashMap<Long, JdbcObjectHolder>());
    // Map for counting commands
    private Map<String, Integer> _commandCountMap =
        Collections.synchronizedMap(new HashMap<String, Integer>());

    ConnectionEntry(Long connuid, Connection conn, ConnectionConfiguration config, Properties clientInfo, CallingContext ctx) {
        _connection = conn;
        _connectionConfiguration = config;
        _clientInfo = clientInfo;
        _uid = connuid;
        // Put the connection into the JDBC-Object map
        _jdbcObjects.put(connuid, new JdbcObjectHolder(conn, ctx, JdbcInterfaceType.CONNECTION));
    }

    void close() {
        try {
            if(!_connection.isClosed()) {
                _connection.close();

                if(_logger.isDebugEnabled()) {
                    _logger.debug("Closed connection " + _uid);
                }
            }

            traceConnectionStatistics();
        } catch (SQLException e) {
            _logger.error("Exception during closing connection", e);
        }
    }

    public void closeAllRelatedJdbcObjects() throws SQLException {
    	Set<Long> keys = null;
    	synchronized(_jdbcObjects) {
    		keys = new HashSet<Long>(_jdbcObjects.keySet());
    	}
    	if(keys != null) {
	    	for(Long key: keys) {
	    		JdbcObjectHolder jdbcObject = _jdbcObjects.get(key);
	    		// don't act on the Connection itself - this will be done elsewhere
	    		if(jdbcObject.getJdbcInterfaceType() == JdbcInterfaceType.CONNECTION)
	    			continue;
	    		// create a DestroyCommand and act on it
	    		Command destroy = new DestroyCommand(key, jdbcObject.getJdbcInterfaceType());
	    		destroy.execute(jdbcObject.getJdbcObject(), this);
	    	}
    	}
    }
    
    boolean hasJdbcObjects() {
        return !_jdbcObjects.isEmpty();
    }

    public Properties getClientInfo() {
        return _clientInfo;
    }

    public boolean isActive() {
        return _active;
    }

    public long getLastAccess() {
        return _lastAccessTimestamp;
    }

    public long getNumberOfProcessedCommands() {
        return _numberOfProcessedCommands;
    }

    public Object getJDBCObject(Long key) {
        JdbcObjectHolder jdbcObjectHolder = _jdbcObjects.get(key);
        if(jdbcObjectHolder != null) {
            return jdbcObjectHolder.getJdbcObject();
        } else {
            return null;
        }
    }

    public void addJDBCObject(Long key, Object partner) {
    	int _jdbcInterfaceType = getJdbcInterfaceTypeFromObject(partner);
        _jdbcObjects.put(key, new JdbcObjectHolder(partner, null, _jdbcInterfaceType));
    }

    public Object removeJDBCObject(Long key) {
        JdbcObjectHolder jdbcObjectHolder = _jdbcObjects.remove(key);
        if(jdbcObjectHolder != null) {
            return jdbcObjectHolder.getJdbcObject();
        } else {
            return null;
        }
    }

    public int getCompressionMode() {
        return _connectionConfiguration.getCompressionModeAsInt();
    }

    public long getCompressionThreshold() {
        return _connectionConfiguration.getCompressionThreshold();
    }

    public int getRowPacketSize() {
        return _connectionConfiguration.getRowPacketSize();
    }

    public String getCharset() {
        return _connectionConfiguration.getCharset();
    }

    public String resolveOrCheckQuery(String sql) throws SQLException
    {
        if (sql.startsWith("$")) {
            return getNamedQuery(sql.substring(1));
        }
        else {
            checkAgainstQueryFilters(sql);
            return sql;
        }
    }

    public synchronized Object executeCommand(Long uid, Command cmd, CallingContext ctx) throws SQLException {
        try {
            _active = true;
            _lastAccessTimestamp = System.currentTimeMillis();

            Object result = null;

            // Some target object ?
            if(uid != null) {
                // ... get it
                JdbcObjectHolder target = _jdbcObjects.get(uid);

                if(target != null) {
                    if(_logger.isDebugEnabled()) {
                        _logger.debug("Target for UID " + uid + " found");
                    }
                    // Execute the command on the target object
                    result = cmd.execute(target.getJdbcObject(), this);
                    // Check if the result must be remembered on the server side with a UID
                    UIDEx uidResult = ReturnedObjectGuard.checkResult(result);

                    if(uidResult != null) {
                        // put it in the JDBC-Object-Table
                    	int _jdbcInterfaceType = getJdbcInterfaceTypeFromObject(result);
                        _jdbcObjects.put(uidResult.getUID(), new JdbcObjectHolder(result, ctx, _jdbcInterfaceType));
                        if(_logger.isDebugEnabled()) {
                            _logger.debug("Registered " + result.getClass().getName() + " with UID " + uidResult);
                        }
                        if (result instanceof ProxiedObject) {
                            return ((ProxiedObject)result).getProxy();
                        }
                        return uidResult;
                    } else {
                        // When the result is of type ResultSet then handle it specially
                        if(result != null &&
                           VJdbcConfiguration.getUseCustomResultSetHandling()) {
                            if(result instanceof ResultSet) {
                                boolean forwardOnly = false;
                                if(cmd instanceof ResultSetProducerCommand) {
                                    forwardOnly = ((ResultSetProducerCommand) cmd).getResultSetType() == ResultSet.TYPE_FORWARD_ONLY;
                                } else {
                                    _logger.debug("Command " + cmd.toString() + " doesn't implement "
                                            + "ResultSetProducer-Interface, assuming ResultSet is scroll insensitive");
                                }
                                result = handleResultSet((ResultSet) result, forwardOnly, ctx);
                            } else if(result instanceof ResultSetMetaData) {
                                result = handleResultSetMetaData((ResultSetMetaData) result);
                            } else {
                                if(_logger.isDebugEnabled()) {
                                    _logger.debug("... returned " + result);
                                }
                            }
                        }
                    }
                } else {
                    _logger.warn("JDBC-Object for UID " + uid + " and command " + cmd + " is null !");
                }
            } else {
                result = cmd.execute(null, this);
            }

            if(_connectionConfiguration.isTraceCommandCount()) {
                String cmdString = cmd.toString();
                Integer oldval = _commandCountMap.get(cmdString);
                if(oldval == null) {
                    _commandCountMap.put(cmdString, new Integer(1));
                } else {
                    _commandCountMap.put(cmdString, new Integer(oldval.intValue() + 1));
                }
            }

            _numberOfProcessedCommands++;

            return result;
        } finally {
            _active = false;
            _lastAccessTimestamp = System.currentTimeMillis();
        }
    }

    public void cancelCurrentStatementExecution(
        Long connuid, Long uid, StatementCancelCommand cmd)
        throws SQLException {
        // Get the Statement object
        JdbcObjectHolder target = _jdbcObjects.get(uid);

        if (target != null) {
            try {
                Statement stmt = (Statement)target.getJdbcObject();
                if (stmt != null) {
                    cmd.execute(stmt, this);
                } else {
                    _logger.info("no statement with id " + uid + " to cancel");
                }
            } catch (Exception e) {
                _logger.info(e.getMessage(), e);
            }
        } else {
            _logger.info("no statement with id " + uid + " to cancel");
        }
    }

    public void traceConnectionStatistics() {
        _logger.info("  Connection ........... " + _connectionConfiguration.getId());
        _logger.info("  IP address ........... " + _clientInfo.getProperty("vjdbc-client.address", "n.a."));
        _logger.info("  Host name ............ " + _clientInfo.getProperty("vjdbc-client.name", "n.a."));
        dumpClientInfoProperties();
        _logger.info("  Last time of access .. " + new Date(_lastAccessTimestamp));
        _logger.info("  Processed commands ... " + _numberOfProcessedCommands);

        if(_jdbcObjects.size() > 0) {
            _logger.info("  Remaining objects .... " + _jdbcObjects.size());
            for(Iterator<JdbcObjectHolder> it = _jdbcObjects.values().iterator(); it.hasNext();) {
                JdbcObjectHolder jdbcObjectHolder = it.next();
                _logger.info("  - " + jdbcObjectHolder.getJdbcObject().getClass().getName());
                if(_connectionConfiguration.isTraceOrphanedObjects()) {
                    if(jdbcObjectHolder.getCallingContext() != null) {
                        _logger.info(jdbcObjectHolder.getCallingContext().getStackTrace());
                    }
                }
            }
        }

        if(!_commandCountMap.isEmpty()) {
            _logger.info("  Command-Counts:");

            ArrayList entries = new ArrayList(_commandCountMap.entrySet());
            Collections.sort(entries, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Map.Entry e1 = (Map.Entry) o1;
                    Map.Entry e2 = (Map.Entry) o2;

                    Integer v1 = (Integer) e1.getValue();
                    Integer v2 = (Integer) e2.getValue();

                    // Descending sort
                    return -v1.compareTo(v2);
                }
            });

            for(Iterator it = entries.iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                String cmd = (String) entry.getKey();
                Integer count = (Integer) entry.getValue();
                _logger.info("     " + count + " : " + cmd);
            }
        }
    }

    private Object handleResultSet(ResultSet result, boolean forwardOnly, CallingContext ctx) throws SQLException {
        // Populate a StreamingResultSet
        StreamingResultSet srs = new StreamingResultSet(
                _connectionConfiguration.getRowPacketSize(),
                forwardOnly,
                _connectionConfiguration.isPrefetchResultSetMetaData(),
                _connectionConfiguration.getCharset());
        // Populate it
        boolean lastPartReached = srs.populate(result);
        // Remember the ResultSet and put the UID in the StreamingResultSet
        UIDEx uid = new UIDEx();
        srs.setRemainingResultSetUID(uid);
        _jdbcObjects.put(uid.getUID(), new JdbcObjectHolder(new ResultSetHolder(result, _connectionConfiguration, lastPartReached), ctx, JdbcInterfaceType.RESULTSETHOLDER));
        if(_logger.isDebugEnabled()) {
            _logger.debug("Registered ResultSet with UID " + uid.getUID());
        }
        return new SerializableTransport(srs, getCompressionMode(), getCompressionThreshold());
    }

    private Object handleResultSetMetaData(ResultSetMetaData result) throws SQLException {
        return new SerializableTransport(new SerialResultSetMetaData(result), getCompressionMode(), getCompressionThreshold());
    }

    private void dumpClientInfoProperties() {
        if(_logger.isInfoEnabled() && !_clientInfo.isEmpty()) {
            boolean printedHeader = false;

            for(Enumeration it = _clientInfo.keys(); it.hasMoreElements();) {
                String key = (String) it.nextElement();
                if(!key.startsWith("vjdbc")) {
                    if(!printedHeader) {
                        printedHeader = true;
                        _logger.info("  Client-Properties ...");
                    }
                    _logger.info("    " + key + " => " + _clientInfo.getProperty(key));
                }
            }
        }
    }

    private String getNamedQuery(String id) throws SQLException {
        if(_connectionConfiguration.getNamedQueries() != null) {
            return _connectionConfiguration.getNamedQueries().getSqlForId(id);
        } else {
            String msg = "No named-queries are associated with this connection";
            _logger.error(msg);
            throw new SQLException(msg);
        }
    }

    private void checkAgainstQueryFilters(String sql) throws SQLException {
        if(_connectionConfiguration.getQueryFilters() != null) {
            _connectionConfiguration.getQueryFilters().checkAgainstFilters(sql);
        }
    }
    
    private int getJdbcInterfaceTypeFromObject(Object jdbcObject) {
    	int _jdbcInterfaceType = 0;
    	if(jdbcObject == null) {
    		return _jdbcInterfaceType;
    	}
    	if(jdbcObject instanceof CallableStatement) {
    		_jdbcInterfaceType = JdbcInterfaceType.CALLABLESTATEMENT;
    	} else if(jdbcObject instanceof Connection) {
    		_jdbcInterfaceType = JdbcInterfaceType.CONNECTION;    		
    	} else if(jdbcObject instanceof DatabaseMetaData) {
    		_jdbcInterfaceType = JdbcInterfaceType.DATABASEMETADATA;
    	} else if(jdbcObject instanceof PreparedStatement) {
    		_jdbcInterfaceType = JdbcInterfaceType.PREPAREDSTATEMENT;
    	} else if(jdbcObject instanceof Savepoint) {
    		_jdbcInterfaceType = JdbcInterfaceType.SAVEPOINT;
    	} else if(jdbcObject instanceof Statement) {
    		_jdbcInterfaceType = JdbcInterfaceType.STATEMENT;
    	} else if(jdbcObject instanceof ResultSetHolder) {
    		_jdbcInterfaceType = JdbcInterfaceType.RESULTSETHOLDER;
    	}
    	return _jdbcInterfaceType;
    }
}
