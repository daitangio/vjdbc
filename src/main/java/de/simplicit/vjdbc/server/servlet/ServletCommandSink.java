// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.simplicit.vjdbc.command.Command;
import de.simplicit.vjdbc.serial.CallingContext;
import de.simplicit.vjdbc.server.command.CommandProcessor;
import de.simplicit.vjdbc.server.config.ConfigurationException;
import de.simplicit.vjdbc.server.config.ConnectionConfiguration;
import de.simplicit.vjdbc.server.config.VJdbcConfiguration;
import de.simplicit.vjdbc.servlet.ServletCommandSinkIdentifier;
import de.simplicit.vjdbc.util.SQLExceptionHelper;
import de.simplicit.vjdbc.util.StreamCloser;
import javax.servlet.ServletContext;

public class ServletCommandSink extends HttpServlet {
    private static final String INIT_PARAMETER_CONFIG_RESOURCE = "config-resource";
    private static final String INIT_PARAMETER_CONFIG_VARIABLES = "config-variables";
    private static final String DEFAULT_CONFIG_RESOURCE = "/WEB-INF/vjdbc-config.xml";
    private static final long serialVersionUID = 3257570624301249846L;
    private static Log _logger = LogFactory.getLog(ServletCommandSink.class);

    private CommandProcessor _processor;

    public ServletCommandSink() {
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        String configResource = servletConfig.getInitParameter(INIT_PARAMETER_CONFIG_RESOURCE);

        // Use default location when nothing is configured
        if(configResource == null) {
            configResource = DEFAULT_CONFIG_RESOURCE;
        }

        ServletContext ctx = servletConfig.getServletContext();

        _logger.info("Trying to get config resource " + configResource + "...");
        InputStream configResourceInputStream = ctx.getResourceAsStream(configResource);
        if(null == configResourceInputStream) {
            try {
                configResourceInputStream =
                    new FileInputStream(ctx.getRealPath(configResource));
            } catch (FileNotFoundException fnfe) {
            }
        }

        if(configResourceInputStream == null) {
            String msg = "VJDBC-Configuration " + configResource + " not found !";
            _logger.error(msg);
            throw new ServletException(msg);
        }

        // Are config variables specifiec ?
        String configVariables = servletConfig.getInitParameter(INIT_PARAMETER_CONFIG_VARIABLES);
        Properties configVariablesProps = null;

        if(configVariables != null) {
            _logger.info("... using variables specified in " + configVariables);

            InputStream configVariablesInputStream = null;

            try {
                configVariablesInputStream = ctx.getResourceAsStream(configVariables);
                if(null == configVariablesInputStream) {
                    configVariablesInputStream =
                        new FileInputStream(ctx.getRealPath(configVariables));
                }

                if(configVariablesInputStream == null) {
                    String msg = "Configuration-Variables " + configVariables + " not found !";
                    _logger.error(msg);
                    throw new ServletException(msg);
                }

                configVariablesProps = new Properties();
                configVariablesProps.load(configVariablesInputStream);
            } catch (IOException e) {
                String msg = "Reading of configuration variables failed";
                _logger.error(msg, e);
                throw new ServletException(msg, e);
            } finally {
                if(configVariablesInputStream != null) {
                    try {
                        configVariablesInputStream.close();
                    } catch (IOException e) {}
                }
            }
        }

        try {
            _logger.info("Initialize VJDBC-Configuration");
            VJdbcConfiguration.init(configResourceInputStream, configVariablesProps);
            _processor = CommandProcessor.getInstance();
        } catch (ConfigurationException e) {
            _logger.error("Initialization failed", e);
            throw new ServletException("VJDBC-Initialization failed", e);
        } finally {
                StreamCloser.close(configResourceInputStream);
        }
    }

    public void destroy() {
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException {
        handleRequest(httpServletRequest, httpServletResponse);
    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException {
        handleRequest(httpServletRequest, httpServletResponse);
    }

    private void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;

        try {
            // Get the method to execute
            String method = httpServletRequest.getHeader(ServletCommandSinkIdentifier.METHOD_IDENTIFIER);

            if(method != null) {
                ois = new ObjectInputStream(httpServletRequest.getInputStream());
                // And initialize the output
                OutputStream os = httpServletResponse.getOutputStream();
                oos = new ObjectOutputStream(os);
                Object objectToReturn = null;

                try {
                    // Some command to process ?
                    if(method.equals(ServletCommandSinkIdentifier.PROCESS_COMMAND)) {
                        // Read parameter objects
                        Long connuid = (Long) ois.readObject();
                        Long uid = (Long) ois.readObject();
                        Command cmd = (Command) ois.readObject();
                        CallingContext ctx = (CallingContext) ois.readObject();
                        // Delegate execution to the CommandProcessor
                        objectToReturn = _processor.process(connuid, uid, cmd, ctx);
                    } else if(method.equals(ServletCommandSinkIdentifier.CONNECT_COMMAND)) {
                        String url = ois.readUTF();
                        Properties props = (Properties) ois.readObject();
                        Properties clientInfo = (Properties) ois.readObject();
                        CallingContext ctx = (CallingContext) ois.readObject();

                        ConnectionConfiguration connectionConfiguration = VJdbcConfiguration.singleton().getConnection(url);

                        if(connectionConfiguration != null) {
                            Connection conn = connectionConfiguration.create(props);
                            objectToReturn = _processor.registerConnection(conn, connectionConfiguration, clientInfo, ctx);
                        } else {
                            objectToReturn = new SQLException("VJDBC-Connection " + url + " not found");
                        }
                    }
                } catch (Throwable t) {
                    // Wrap any exception so that it can be transported back to
                    // the client
                    objectToReturn = SQLExceptionHelper.wrap(t);
                }

                // Write the result in the response buffer
                oos.writeObject(objectToReturn);
                oos.flush();

                httpServletResponse.flushBuffer();
            } else {
                // No VJDBC-Method ? Then we redirect the stupid browser user to
                // some information page :-)
                httpServletResponse.sendRedirect("index.html");
            }
        } catch (Exception e) {
            _logger.error("Unexpected Exception", e);
            throw new ServletException(e);
        } finally {
            StreamCloser.close(ois);
            StreamCloser.close(oos);
        }
    }
}
