// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.servlet;

/**
 * Interface for manipulating the request headers of VJDBC-Http-Requests.
 * @author Mike
 *
 */
public interface RequestModifier {
    /**
     * Adds a Request-Property with the same semantics like URLConnection.addRequestProperty
     * @param key Key of the Request-Property
     * @param value Value of the Request-Property
     */
    public abstract void addRequestHeader(String key, String value);
}