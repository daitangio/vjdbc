// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.config;

import de.simplicit.vjdbc.VJdbcException;
import de.simplicit.vjdbc.VJdbcProperties;
import de.simplicit.vjdbc.server.DataSourceProvider;
import de.simplicit.vjdbc.server.LoginHandler;
import de.simplicit.vjdbc.server.concurrent.Executor;
import de.simplicit.vjdbc.server.concurrent.PooledExecutor;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.zip.Deflater;

public class ConnectionConfiguration implements Executor {
    private static Log _logger = LogFactory.getLog(ConnectionConfiguration.class);
    private static final String DBCP_ID = "jdbc:apache:commons:dbcp:";

    // Basic properties
    protected String _id;
    protected String _driver;
    protected String _url;
    protected String _dataSourceProvider;
    protected String _user;
    protected String _password;
    // Trace properties
    protected boolean _traceCommandCount = false;
    protected boolean _traceOrphanedObjects = false;
    // Row-Packet size defines the number of rows that is
    // transported in one packet
    protected int _rowPacketSize = 200;
    // Encoding for strings
    protected String _charset = "ISO-8859-1";
    // Compression
    protected int _compressionMode = Deflater.BEST_SPEED;
    protected long _compressionThreshold = 1000;
    // Connection pooling
    protected boolean _connectionPooling = true;
    protected ConnectionPoolConfiguration _connectionPoolConfiguration = null;
    // Fetch the metadata of a resultset immediately after constructing
    protected boolean _prefetchResultSetMetaData = false;
    // Custom login handler
    protected String _loginHandler;
    private LoginHandler _loginHandlerInstance = null;
    // Named queries
    protected NamedQueryConfiguration _namedQueries;
    // Query filters
    protected QueryFilterConfiguration _queryFilters;

    // Connection pooling support
    private boolean _driverInitialized = false;
    private Boolean _connectionPoolInitialized = Boolean.FALSE;
    private GenericObjectPool _connectionPool = null;
    // Thread pooling support
    private int _maxThreadPoolSize = 8;
    private PooledExecutor _pooledExecutor = new PooledExecutor(_maxThreadPoolSize);

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getDriver() {
        return _driver;
    }

    public void setDriver(String driver) {
        _driver = driver;
    }

    public String getUrl() {
        return _url;
    }

    public void setUrl(String url) {
        _url = url;
    }

    public String getDataSourceProvider() {
        return _dataSourceProvider;
    }

    public void setDataSourceProvider(String dataSourceProvider) {
        _dataSourceProvider = dataSourceProvider;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public String getUser() {
        return _user;
    }

    public void setUser(String user) {
        _user = user;
    }

    public boolean isTraceCommandCount() {
        return _traceCommandCount;
    }

    public void setTraceCommandCount(boolean traceCommandCount) {
        _traceCommandCount = traceCommandCount;
    }

    public boolean isTraceOrphanedObjects() {
        return _traceOrphanedObjects;
    }

    public void setTraceOrphanedObjects(boolean traceOrphanedObjects) {
        _traceOrphanedObjects = traceOrphanedObjects;
    }

    public int getRowPacketSize() {
        return _rowPacketSize;
    }

    public void setRowPacketSize(int rowPacketSize) {
        _rowPacketSize = rowPacketSize;
    }

    public String getCharset() {
        return _charset;
    }

    public void setCharset(String charset) {
        _charset = charset;
    }

    public int getCompressionModeAsInt() {
        return _compressionMode;
    }

    public void setCompressionModeAsInt(int compressionMode) throws ConfigurationException {
        switch (compressionMode) {
        case Deflater.BEST_SPEED:
        case Deflater.BEST_COMPRESSION:
        case Deflater.NO_COMPRESSION:
            _compressionMode = compressionMode;
        default:
            throw new ConfigurationException("Unknown compression mode");
        }
    }

    public String getCompressionMode() {
        switch (_compressionMode) {
        case Deflater.BEST_SPEED:
            return "bestspeed";
        case Deflater.BEST_COMPRESSION:
            return "bestcompression";
        case Deflater.NO_COMPRESSION:
            return "none";
        default:
            throw new RuntimeException("Unknown compression mode");
        }
    }

    public void setCompressionMode(String compressionMode) throws ConfigurationException {
        if(compressionMode.equalsIgnoreCase("bestspeed")) {
            _compressionMode = Deflater.BEST_SPEED;
        } else if(compressionMode.equalsIgnoreCase("bestcompression")) {
            _compressionMode = Deflater.BEST_COMPRESSION;
        } else if(compressionMode.equalsIgnoreCase("none")) {
            _compressionMode = Deflater.NO_COMPRESSION;
        } else {
            throw new ConfigurationException("Unknown compression mode '" + compressionMode
                    + "', use either bestspeed, bestcompression or none");
        }
    }

    public long getCompressionThreshold() {
        return _compressionThreshold;
    }

    public void setCompressionThreshold(long compressionThreshold) throws ConfigurationException {
        if(_compressionThreshold < 0) {
            throw new ConfigurationException("Compression threshold must be >= 0");
        }
        _compressionThreshold = compressionThreshold;
    }

    public boolean useConnectionPooling() {
        return _connectionPooling;
    }

    public void setConnectionPooling(boolean connectionPooling) {
        _connectionPooling = connectionPooling;
    }

    public ConnectionPoolConfiguration getConnectionPoolConfiguration() {
        return _connectionPoolConfiguration;
    }

    public void setConnectionPoolConfiguration(ConnectionPoolConfiguration connectionPoolConfiguration) {
        _connectionPoolConfiguration = connectionPoolConfiguration;
        _connectionPooling = true;
    }

    public boolean isPrefetchResultSetMetaData() {
        return _prefetchResultSetMetaData;
    }

    public void setPrefetchResultSetMetaData(boolean fetchResultSetMetaData) {
        _prefetchResultSetMetaData = fetchResultSetMetaData;
    }

    public String getLoginHandler() {
        return _loginHandler;
    }

    public void setLoginHandler(String loginHandler) {
        _loginHandler = loginHandler;
    }

    public NamedQueryConfiguration getNamedQueries() {
        return _namedQueries;
    }

    public void setNamedQueries(NamedQueryConfiguration namedQueries) {
        _namedQueries = namedQueries;
    }

    public QueryFilterConfiguration getQueryFilters() {
        return _queryFilters;
    }

    public void setQueryFilters(QueryFilterConfiguration queryFilters) {
        _queryFilters = queryFilters;
    }

    void validate() throws ConfigurationException {
        if(_url == null && (_dataSourceProvider == null)) {
            String msg = "Connection-Entry " + _id + ": neither URL nor DataSourceProvider is provided";
            _logger.error(msg);
            throw new ConfigurationException(msg);
        }

        // When connection pooling is used, the user/password combination must be
        // provided in the configuration as otherwise user-accounts are mixed up
        if(_dataSourceProvider == null) {
            if(_connectionPooling && _user == null) {
                String msg = "Connection-Entry " + _id + ": connection pooling can only be used when a dedicated user is specified for the connection";
                _logger.error(msg);
                throw new ConfigurationException(msg);
            }
        }
    }

    void log() {
        String usedPassword = "provided by client";
        if(_password != null) {
            char[] hiddenPassword = new char[_password.length()];
            for(int i = 0; i < _password.length(); i++) {
                hiddenPassword[i] = '*';
            }
            usedPassword = new String(hiddenPassword);
        }

        _logger.info("Connection-Configuration '" + _id + "'");

        // We must differentiate between the DataSource-API and the older
        // DriverManager-API. When the DataSource-Provider is provided, the
        // driver and URL configurations will be ignored
        if(_dataSourceProvider != null) {
            _logger.info("  DataSource-Provider ........ " + _dataSourceProvider);
        } else {
            if (_driver != null) {
                _logger.info("  Driver ..................... " + _driver);
            }
            _logger.info("  URL ........................ " + _url);
        }
        _logger.info("  User ....................... " + ((_user != null) ? _user : "provided by client"));
        _logger.info("  Password ................... " + usedPassword);
        _logger.info("  Row-Packetsize ............. " + _rowPacketSize);
        _logger.info("  Charset .................... " + _charset);
        _logger.info("  Compression ................ " + getCompressionMode());
        _logger.info("  Compression-Thrs ........... " + _compressionThreshold + " bytes");
        _logger.info("  Connection-Pool ............ " + (_connectionPooling ? "on" : "off"));
        _logger.info("  Pre-Fetch ResultSetMetaData  " + (_prefetchResultSetMetaData ? "on" : "off"));
        _logger.info("  Login-Handler .............. " + (_loginHandler != null ? _loginHandler : "none"));
        _logger.info("  Trace Command-Counts ....... " + _traceCommandCount);
        _logger.info("  Trace Orphaned-Objects ..... " + _traceOrphanedObjects);

        if(_connectionPoolConfiguration != null) {
            _connectionPoolConfiguration.log();
        }

        if(_namedQueries != null) {
            _namedQueries.log();
        }

        if(_queryFilters != null) {
            _queryFilters.log();
        }
    }

    public Connection create(Properties props) throws SQLException, VJdbcException {
        checkLogin(props);

        if(_dataSourceProvider != null) {
            return createConnectionViaDataSource();
        } else {
            return createConnectionViaDriverManager(props);
        }
    }

    protected Connection createConnectionViaDataSource() throws SQLException {
        Connection result;

        _logger.debug("Creating DataSourceFactory from class " + _dataSourceProvider);

        try {
            Class clsDataSourceProvider = Class.forName(_dataSourceProvider);
            DataSourceProvider dataSourceProvider = (DataSourceProvider) clsDataSourceProvider.newInstance();
            _logger.debug("DataSourceProvider created");
            DataSource dataSource = dataSourceProvider.getDataSource();
            _logger.debug("Retrieving connection from DataSource");
            if(_user != null) {
                result = dataSource.getConnection(_user, _password);
            } else {
                result = dataSource.getConnection();
            }
            _logger.debug("... Connection successfully retrieved");
        } catch (ClassNotFoundException e) {
            String msg = "DataSourceProvider-Class " + _dataSourceProvider + " not found";
            _logger.error(msg, e);
            throw new SQLException(msg);
        } catch (InstantiationException e) {
            String msg = "Failed to create DataSourceProvider";
            _logger.error(msg, e);
            throw new SQLException(msg);
        } catch (IllegalAccessException e) {
            String msg = "Can't access DataSourceProvider";
            _logger.error(msg, e);
            throw new SQLException(msg);
        }

        return result;
    }

    protected Connection createConnectionViaDriverManager(Properties props) throws SQLException {
        // Try to load the driver
        if(!_driverInitialized && _driver != null) {
            try {
                _logger.debug("Loading driver " + _driver);
                Class.forName(_driver).newInstance();
                _logger.debug("... successful");
            } catch (Exception e) {
                String msg = "Loading of driver " + _driver + " failed";
                _logger.error(msg, e);
                throw new SQLException(msg);
            }
            _driverInitialized = true;
        }

        // When database login is provided use them for the login instead of the
        // ones provided by the client
        if(_user != null) {
            _logger.debug("Using " + _user + " for database-login");
            props.put("user", _user);
            if(_password != null) {
                props.put("password", _password);
            } else {
                _logger.warn("No password was provided for database-login " + _user);
            }
        }

        String jdbcurl = _url;

        if(jdbcurl.length() > 0) {
            _logger.debug("JDBC-Connection-String: " + jdbcurl);
        } else {
            String msg = "No JDBC-Connection-String available";
            _logger.error(msg);
            throw new SQLException(msg);
        }

        // Connection pooling with DBCP
        if(_connectionPooling && _connectionPoolInitialized != null) {
            String dbcpId = DBCP_ID + _id;

            if(_connectionPool != null) {
                jdbcurl = dbcpId;
            } else {
                try {
                    // Try to load the DBCP-Driver
                    Class.forName("org.apache.commons.dbcp.PoolingDriver");
                    // Populate configuration object
                    if(_connectionPoolConfiguration != null) {
                        GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
                        poolConfig.maxActive = _connectionPoolConfiguration.getMaxActive();
                        poolConfig.maxIdle = _connectionPoolConfiguration.getMaxIdle();
                        poolConfig.maxWait = _connectionPoolConfiguration.getMaxWait();
                        poolConfig.minIdle = _connectionPoolConfiguration.getMinIdle();
                        poolConfig.minEvictableIdleTimeMillis = _connectionPoolConfiguration.getMinEvictableIdleTimeMillis();
                        poolConfig.timeBetweenEvictionRunsMillis = _connectionPoolConfiguration.getTimeBetweenEvictionRunsMillis();
                        _connectionPool = new LoggingGenericObjectPool(_id, poolConfig);
                    }
                    else {
                        _connectionPool = new LoggingGenericObjectPool(_id);
                    }

                    ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(jdbcurl, props);
                    new PoolableConnectionFactory(connectionFactory, _connectionPool, null, null, false, true);
                    PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(DBCP_ID);
                    // Register pool with connection id
                    driver.registerPool(_id, _connectionPool);
                    _connectionPoolInitialized = Boolean.TRUE;
                    jdbcurl = dbcpId;
                    _logger.debug("Connection-Pooling successfully initialized for connection " + _id);
                } catch (ClassNotFoundException e) {
                    _connectionPool = null;
                    _connectionPoolInitialized = null;
                    _logger.error("Jakarta-DBCP-Driver not found, switching it off for connection " + _id, e);
                }
            }
        }

        return DriverManager.getConnection(jdbcurl, props);
    }

    protected void checkLogin(Properties props) throws VJdbcException {
        if(_loginHandler != null) {
            _logger.debug("Trying to login ...");

            if(_loginHandlerInstance == null) {
                try {
                    Class loginHandlerClazz = Class.forName(_loginHandler);
                    _loginHandlerInstance = (LoginHandler) loginHandlerClazz.newInstance();
                } catch (ClassNotFoundException e) {
                    String msg = "Login-Handler class not found";
                    _logger.error(msg, e);
                    throw new VJdbcException(msg, e);
                } catch (InstantiationException e) {
                    String msg = "Error creating instance of Login-Handler class";
                    _logger.error(msg, e);
                    throw new VJdbcException(msg, e);
                } catch (IllegalAccessException e) {
                    String msg = "Error creating instance of Login-Handler class";
                    _logger.error(msg, e);
                    throw new VJdbcException(msg, e);
                }
            }

            String loginUser = props.getProperty(VJdbcProperties.LOGIN_USER);
            String loginPassword = props.getProperty(VJdbcProperties.LOGIN_PASSWORD);

            if(loginUser == null) {
                _logger.warn("Property vjdbc.login.user is not set, " + "the login-handler might not be satisfied");
            }

            if(loginPassword == null) {
                _logger.warn("Property vjdbc.login.password is not set, " + "the login-handler might not be satisfied");
            }

            _loginHandlerInstance.checkLogin(loginUser, loginPassword);

            _logger.debug("... successful");
        }
    }

    public void execute(Runnable command) throws InterruptedException {
        _pooledExecutor.execute(command);
    }
}
