// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.config;

import de.simplicit.vjdbc.VJdbcException;
import de.simplicit.vjdbc.VJdbcProperties;
import de.simplicit.vjdbc.server.DataSourceProvider;
import de.simplicit.vjdbc.server.LoginHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionConfiguration implements Executor {
	private static Logger _logger = Logger.getLogger(ConnectionConfiguration.class.getName());

	// Basic properties
    @XmlAttribute(name="id")
	protected String _id;
    @XmlAttribute(name="driver")
	protected String _driver;
    @XmlAttribute(name="url")
	protected String _url;
	protected String _dataSourceProvider;
    @XmlAttribute(name="user")
	protected String _user;
    @XmlAttribute(name="password")
	protected String _password;
	// Trace properties
    @XmlTransient
	protected boolean _traceCommandCount = false;
    @XmlTransient
	protected boolean _traceOrphanedObjects = false;
	// Row-Packet size defines the number of rows that is
	// transported in one packet
    @XmlTransient
	protected int _rowPacketSize = 1000;
	// Encoding for strings
    @XmlTransient
	protected String _charset = "ISO-8859-1";
	// Compression
    @XmlTransient
	protected int _compressionMode = Deflater.BEST_SPEED;
    @XmlTransient
	protected long _compressionThreshold = 1000;
	// Fetch the metadata of a resultset immediately after constructing
    @XmlTransient
	protected boolean _prefetchResultSetMetaData = false;
	// Custom login handler
    @XmlTransient
	protected String _loginHandler;
    @XmlTransient
	private LoginHandler _loginHandlerInstance = null;
        // Ignore SQLFeatureNotSupportedExceptions
    @XmlAttribute(name="ignoreSQLFeatureNotSupportedExceptions")
        protected boolean _ignoreSQLFeatureNotSupportedExceptions;

	// Connection pooling support
    @XmlTransient
	private boolean _driverInitialized = false;
    @XmlTransient
	private Boolean _connectionPoolInitialized = Boolean.FALSE;
	// Thread pooling support
    @XmlTransient
	private int _maxThreadPoolSize = 8;
	// _GG_ Replaced with jdk executors
    @XmlTransient
	private ExecutorService _pooledExecutor = Executors.newScheduledThreadPool(_maxThreadPoolSize) ;



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

        public boolean isIgnoreSQLFeatureNotSupportedExceptions() {
            return _ignoreSQLFeatureNotSupportedExceptions;
        }

        public void setIgnoreSQLFeatureNotSupportedExceptions(boolean _ignoreSQLFeatureNotSupportedExceptions) {
            this._ignoreSQLFeatureNotSupportedExceptions = _ignoreSQLFeatureNotSupportedExceptions;
        }

	void validate() throws ConfigurationException {
		if(_url == null && (_dataSourceProvider == null)) {
			String msg = "Connection-Entry " + _id + ": neither URL nor DataSourceProvider is provided";
			_logger.severe(msg);
			throw new ConfigurationException(msg);
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
		_logger.info("  Pre-Fetch ResultSetMetaData  " + (_prefetchResultSetMetaData ? "on" : "off"));
		_logger.info("  Login-Handler .............. " + (_loginHandler != null ? _loginHandler : "none"));
		_logger.info("  Trace Command-Counts ....... " + _traceCommandCount);
		_logger.info("  Trace Orphaned-Objects ..... " + _traceOrphanedObjects);
        _logger.info("  Ignore SQL Feature Errors... " + _ignoreSQLFeatureNotSupportedExceptions);
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

		_logger.fine("Creating DataSourceFactory from class " + _dataSourceProvider);

		try {
			Class clsDataSourceProvider = Class.forName(_dataSourceProvider);
			DataSourceProvider dataSourceProvider = (DataSourceProvider) clsDataSourceProvider.newInstance();
			_logger.fine("DataSourceProvider created");
			DataSource dataSource = dataSourceProvider.getDataSource();
			_logger.fine("Retrieving connection from DataSource");
			if(_user != null) {
				result = dataSource.getConnection(_user, _password);
			} else {
				result = dataSource.getConnection();
			}
			_logger.fine("... Connection successfully retrieved");
		} catch (ClassNotFoundException e) {
			String msg = "DataSourceProvider-Class " + _dataSourceProvider + " not found";
			_logger.severe(msg);
            e.printStackTrace();
			throw new SQLException(msg);
		} catch (InstantiationException e) {
			String msg = "Failed to create DataSourceProvider";
			_logger.severe(msg);
            e.printStackTrace();
			throw new SQLException(msg);
		} catch (IllegalAccessException e) {
			String msg = "Can't access DataSourceProvider";
			_logger.severe(msg);
            e.printStackTrace();
			throw new SQLException(msg);
		}

		return result;
	}

	protected Connection createConnectionViaDriverManager(Properties props) throws SQLException {
		// Try to load the driver
		if(!_driverInitialized && _driver != null) {
			try {
				_logger.fine("Loading driver " + _driver);
				Class.forName(_driver).newInstance();
				_logger.fine("... successful");
			} catch (Exception e) {
				String msg = "Loading of driver " + _driver + " failed";
                _logger.severe(msg);
                e.printStackTrace();
				throw new SQLException(msg);
			}
			_driverInitialized = true;
		}

		// When database login is provided use them for the login instead of the
		// ones provided by the client
		if(_user != null) {
			_logger.fine("Using " + _user + " for database-login");
			props.put("user", _user);
			if(_password != null) {
				props.put("password", _password);
			} else {
				_logger.warning("No password was provided for database-login " + _user);
			}
		}

		String jdbcurl = _url;

		if(jdbcurl.length() > 0) {
			_logger.fine("JDBC-Connection-String: " + jdbcurl);
		} else {
			String msg = "No JDBC-Connection-String available";
			_logger.severe(msg);
			throw new SQLException(msg);
		}

		return DriverManager.getConnection(jdbcurl, props);
	}

	protected void checkLogin(Properties props) throws VJdbcException {
		if(_loginHandler != null) {
			_logger.fine("Trying to login ...");

			if(_loginHandlerInstance == null) {
				try {
					Class loginHandlerClazz = Class.forName(_loginHandler);
					_loginHandlerInstance = (LoginHandler) loginHandlerClazz.newInstance();
				} catch (ClassNotFoundException e) {
					String msg = "Login-Handler class not found";
                    _logger.severe(msg);
                    e.printStackTrace();
					throw new VJdbcException(msg, e);
				} catch (InstantiationException e) {
					String msg = "Error creating instance of Login-Handler class";
                    _logger.severe(msg);
                    e.printStackTrace();
					throw new VJdbcException(msg, e);
				} catch (IllegalAccessException e) {
					String msg = "Error creating instance of Login-Handler class";
                    _logger.severe(msg);
                    e.printStackTrace();
					throw new VJdbcException(msg, e);
				}
			}

			String loginUser = props.getProperty(VJdbcProperties.LOGIN_USER);
			String loginPassword = props.getProperty(VJdbcProperties.LOGIN_PASSWORD);

			if(loginUser == null) {
				_logger.warning("Property vjdbc.login.user is not set, " + "the login-handler might not be satisfied");
			}

			if(loginPassword == null) {
				_logger.warning("Property vjdbc.login.password is not set, " + "the login-handler might not be satisfied");
			}

			_loginHandlerInstance.checkLogin(loginUser, loginPassword);

			_logger.fine("... successful");
		}
	}

	@Override
	public void execute(Runnable command) throws RejectedExecutionException {
		_pooledExecutor.execute(command);
	}
}
