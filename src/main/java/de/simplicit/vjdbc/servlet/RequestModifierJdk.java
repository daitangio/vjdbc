// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.servlet;

import java.net.URLConnection;


/**
 * The HttpRequestModifier lets an external entity partly change the Http-Request
 * that is made by VJDBC in Servlet-Mode. To prevent abuse actually only one method
 * is delegated to the URLConnection.
 * @author Mike
 *
 */
final class RequestModifierJdk implements RequestModifier {
    private final URLConnection _urlConnection;
    
    /**
     * Package visibility, doesn't make sense for other packages.
     * @param urlConnection Wrapped URLConnection
     */
    RequestModifierJdk(URLConnection urlConnection) {
        _urlConnection = urlConnection;
    }
    
    /* (non-Javadoc)
     * @see de.simplicit.vjdbc.servlet.RequestModifier#addRequestProperty(java.lang.String, java.lang.String)
     */
    public void addRequestHeader(String key, String value) {
        _urlConnection.addRequestProperty(key, value);
    }
}
