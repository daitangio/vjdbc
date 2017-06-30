// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.servlet.jakarta;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;

import de.simplicit.vjdbc.command.Command;
import de.simplicit.vjdbc.serial.CallingContext;
import de.simplicit.vjdbc.serial.UIDEx;
import de.simplicit.vjdbc.servlet.AbstractServletCommandSinkClient;
import de.simplicit.vjdbc.servlet.RequestEnhancer;
import de.simplicit.vjdbc.servlet.ServletCommandSinkIdentifier;
import de.simplicit.vjdbc.util.SQLExceptionHelper;
import de.simplicit.vjdbc.util.StreamCloser;

/**
 * ServletCommandSinkClient implementation which uses Jakarta-HttpClient to communicate with the
 * web server.
 * @author Mike
 */
public class ServletCommandSinkJakartaHttpClient extends AbstractServletCommandSinkClient {
    private String _urlExternalForm;
    private HttpClient _httpClient;
    private MultiThreadedHttpConnectionManager _multiThreadedHttpConnectionManager;

    public ServletCommandSinkJakartaHttpClient(String url, RequestEnhancer requestEnhancer) throws SQLException {
        super(url, requestEnhancer);
        _urlExternalForm = _url.toExternalForm();
        _multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
        _httpClient = new HttpClient(_multiThreadedHttpConnectionManager);
        
        _httpClient.getParams().setBooleanParameter("http.connection.stalecheck", false);
    }
    
    public void close() {
        super.close();
        _multiThreadedHttpConnectionManager.shutdown();
    }

    public UIDEx connect(String database, Properties props, Properties clientInfo, CallingContext ctx) throws SQLException {
        PostMethod post = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try {
            // Open connection and adjust the Input/Output
            post = new PostMethod(_urlExternalForm);
            post.setDoAuthentication(false);
            post.setFollowRedirects(false);
            post.setRequestHeader("Content-type", "binary/x-java-serialized");
            post.setRequestHeader(ServletCommandSinkIdentifier.METHOD_IDENTIFIER, ServletCommandSinkIdentifier.CONNECT_COMMAND);
            // Finally let the optional Request-Enhancer set request headers
            if(_requestEnhancer != null) {
                _requestEnhancer.enhanceConnectRequest(new RequestModifierJakartaHttpClient(post));
            }
            // Write the parameter objects using a ConnectRequestEntity
            post.setRequestEntity(new ConnectRequestEntity(database, props, clientInfo, ctx));

            // Call ...
            _httpClient.executeMethod(post);

            // Check the HTTP status.
            if(post.getStatusCode() != HttpStatus.SC_OK) {
                throw SQLExceptionHelper.wrap(new HttpClientError(post.getStatusLine().toString()));
            } else {
                // Read the result object from the InputStream
                ois = new ObjectInputStream(new BufferedInputStream(post.getResponseBodyAsStream()));
                Object result = ois.readObject();
                // This might be a SQLException which must be rethrown
                if(result instanceof SQLException) {
                    throw (SQLException) result;
                } else {
                    return (UIDEx) result;
                }
            }

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw SQLExceptionHelper.wrap(e);
        } finally {
            // Cleanup resources
            StreamCloser.close(oos);
            StreamCloser.close(ois);
            
            if(post != null) {
                post.releaseConnection();
            }
        }
    }

    public Object process(Long connuid, Long uid, Command cmd, CallingContext ctx) throws SQLException {
        PostMethod post = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try {
            post = new PostMethod(_urlExternalForm);
            post.setDoAuthentication(false);
            post.setFollowRedirects(false);
            post.setContentChunked(false);
            post.setRequestHeader(ServletCommandSinkIdentifier.METHOD_IDENTIFIER, ServletCommandSinkIdentifier.PROCESS_COMMAND);
            // Finally let the optional Request-Enhancer set request properties
            if(_requestEnhancer != null) {
                _requestEnhancer.enhanceProcessRequest(new RequestModifierJakartaHttpClient(post));
            }
            // Write the parameter objects using a ProcessRequestEntity
            post.setRequestEntity(new ProcessRequestEntity(connuid, uid, cmd, ctx));

            // Call ...
            _httpClient.executeMethod(post);

            if(post.getStatusCode() != HttpStatus.SC_OK) {
                throw SQLExceptionHelper.wrap(new HttpClientError(post.getStatusLine().toString()));
            } else {
                ois = new ObjectInputStream(new BufferedInputStream(post.getResponseBodyAsStream()));
                Object result = ois.readObject();
                if(result instanceof SQLException) {
                    throw (SQLException) result;
                } else {
                    return result;
                }
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw SQLExceptionHelper.wrap(e);
        } finally {
            // Cleanup resources
            StreamCloser.close(oos);
            StreamCloser.close(ois);
            
            if(post != null) {
                post.releaseConnection();
            }
        }
    }
}
