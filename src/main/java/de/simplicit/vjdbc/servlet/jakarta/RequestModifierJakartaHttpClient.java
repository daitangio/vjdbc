// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.servlet.jakarta;

import org.apache.commons.httpclient.methods.PostMethod;

import de.simplicit.vjdbc.servlet.RequestModifier;

/**
 * The RequestModifierHttpClient lets an external entity partly change the Http-Request
 * that is made by VJDBC in Servlet-Mode. To prevent abuse actually only one method
 * is delegated to the URLConnection.
 * @author Mike
 *
 */
final class RequestModifierJakartaHttpClient implements RequestModifier {
    private final PostMethod _postMethod;
    
    /**
     * Package visibility, doesn't make sense for other packages.
     * @param urlConnection Wrapped URLConnection
     */
    RequestModifierJakartaHttpClient(PostMethod postMethod) {
        _postMethod = postMethod;
    }
    
    /* (non-Javadoc)
     * @see de.simplicit.vjdbc.servlet.RequestModifier#addRequestProperty(java.lang.String, java.lang.String)
     */
    public void addRequestHeader(String key, String value) {
        _postMethod.addRequestHeader(key, value);
    }
}
