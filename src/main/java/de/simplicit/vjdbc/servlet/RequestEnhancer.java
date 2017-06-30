// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.servlet;

/**
 * The RequestEnhancer interface must be implemented by classes that want to enhance
 * the Http-Requests that VJDBC sends to the VJDBC-Servlet. This way it's possible to
 * send connection specific data like authentication cookies.
 * @author Mike
 */
public interface RequestEnhancer {
    /**
     * Called before the initial connect request of VJDBC is sent.
     * @param requestModifier
     */
    void enhanceConnectRequest(RequestModifier requestModifier);
    
    /**
     * Called before each processing request of VJDBC.
     * @param requestModifier
     */
    void enhanceProcessRequest(RequestModifier requestModifier);
}
