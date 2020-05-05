// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc;

import java.rmi.Naming;
import java.rmi.server.RMISocketFactory;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;


import de.simplicit.vjdbc.command.CallingContextFactory;
import de.simplicit.vjdbc.command.CommandSink;
import de.simplicit.vjdbc.command.DecoratedCommandSink;
import de.simplicit.vjdbc.command.NullCallingContextFactory;
import de.simplicit.vjdbc.command.StandardCallingContextFactory;
import de.simplicit.vjdbc.rmi.CommandSinkRmi;
import de.simplicit.vjdbc.rmi.CommandSinkRmiProxy;
import de.simplicit.vjdbc.rmi.ConnectionBrokerRmi;
import de.simplicit.vjdbc.rmi.SecureSocketFactory;
import de.simplicit.vjdbc.serial.CallingContext;
import de.simplicit.vjdbc.serial.UIDEx;
import de.simplicit.vjdbc.util.ClientInfo;
import de.simplicit.vjdbc.util.SQLExceptionHelper;

public final class VirtualDriver implements Driver {
	private static Logger _logger = Logger.getLogger(VirtualDriver.class.getName());

    private static final String VJDBC_IDENTIFIER = "jdbc:vjdbc:";
    private static final String EJB_IDENTIFIER = "ejb:";
    private static final String RMI_IDENTIFIER = "rmi:";
    private static final String SERVLET_IDENTIFIER = "servlet:";
    private static SecureSocketFactory _sslSocketFactory;
    private static boolean _cacheEnabled = false;

    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 7;

    static {
        try {
            DriverManager.registerDriver(new VirtualDriver());
            _logger.info("Virtual JDBC-Driver successfully registered");
            try {
                Class.forName("org.hsqldb.jdbcDriver").newInstance();
                _logger.info("HSQL-Driver loaded, caching activated");
                _cacheEnabled = true;
            } catch(ClassNotFoundException e) {
                _logger.info("Couldn't load HSQL-Driver, caching deactivated");
                _cacheEnabled = false;
            } catch(Exception e) {
                _logger.severe("Unexpected exception occured on loading the HSQL-Driver");
                _cacheEnabled = false;
            }
        } catch(Exception e) {
            _logger.severe("Couldn't register Virtual-JDBC-Driver !");
            throw new RuntimeException("Couldn't register Virtual-JDBC-Driver !", e);
        }
    }

    public VirtualDriver() {
    }

    public Connection connect(String urlstr, Properties props) throws SQLException {
        Connection result = null;

        if(acceptsURL(urlstr)) {
            String realUrl = urlstr.substring(VJDBC_IDENTIFIER.length());

            _logger.info("VJdbc-URL: " + realUrl);

            try {
                CommandSink sink = null;

                String[] urlparts;

                // RMI-Connection
                if(realUrl.startsWith(RMI_IDENTIFIER)) {
                    urlparts = split(realUrl.substring(RMI_IDENTIFIER.length()));
                    _logger.info("VJdbc in RMI-Mode, using object " + urlparts[0]);
                    // Examine SSL property
                    boolean useSSL = false;
                    String propSSL = props.getProperty(VJdbcProperties.RMI_SSL);
                    useSSL = (propSSL != null && propSSL.equalsIgnoreCase("true"));
                    if(useSSL) {
                        _logger.info("Using Secure Socket Layer (SSL)");
                    }
                    sink = createRmiCommandSink(urlparts[0], useSSL);
                    // Servlet-Connection
                } else {
                    throw new SQLException("Unknown protocol identifier " + realUrl);
                }

                if(urlparts[1].length() > 0) {
                    _logger.info("Connecting to datasource " + urlparts[1]);
                } else {
                    _logger.info("Using default datasource");
                }

                // Connect with the sink
                UIDEx reg = sink.connect(
                        urlparts[1],
                        props,
                        ClientInfo.getProperties(props.getProperty(VJdbcProperties.CLIENTINFO_PROPERTIES)),
                        new CallingContext());

                CallingContextFactory ctxFactory;
                // The value 1 signals that every remote call shall provide a calling context. This should only
                // be used for debugging purposes as sending of these objects is quite expensive.
                if(reg.getValue1() == 1) {
                    ctxFactory = new StandardCallingContextFactory();
                }
                else {
                    ctxFactory = new NullCallingContextFactory();
                }
                // Decorate the sink
                DecoratedCommandSink decosink = new DecoratedCommandSink(reg, sink, ctxFactory);
                // return the new connection
                result = new VirtualConnection(reg, decosink, props, _cacheEnabled);
            } catch(Exception e) {
                _logger.severe("Other error connecting");
                e.printStackTrace();
                throw SQLExceptionHelper.wrap(e);
            }
        }

        return result;
    }

    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(VJDBC_IDENTIFIER);
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    public boolean jdbcCompliant() {
        return true;
    }

    private CommandSink createRmiCommandSink(String rminame, boolean useSSL) throws Exception {
        if(useSSL) {
            if(_sslSocketFactory == null) {
                _sslSocketFactory = new SecureSocketFactory();
                RMISocketFactory.setSocketFactory(_sslSocketFactory);
            }
        }
        ConnectionBrokerRmi broker = (ConnectionBrokerRmi)Naming.lookup(rminame);
        CommandSinkRmi rmiSink = broker.createCommandSink();
        CommandSink proxy = new CommandSinkRmiProxy(rmiSink);
        return proxy;
    }

    // Helper method (can't use the 1.4-Method because support for 1.3 is desired)
    private String[] split(String url) {
        char[] splitChars = { ',', ';', '#', '$' };

        for(int i = 0; i < splitChars.length; i++) {
            int charindex = url.indexOf(splitChars[i]);

            if(charindex >= 0) {
                return new String[] { url.substring(0, charindex), url.substring(charindex + 1) };
            }
        }

        return new String[] { url, "" };
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger");
    }
}
