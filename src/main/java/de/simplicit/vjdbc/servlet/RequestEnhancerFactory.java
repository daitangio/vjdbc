// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.servlet;

/**
 * Interface that must be implemented by the classes that register themselves with the
 * VJdbcProperties.SERVLET_REQUEST_ENHANCER_FACTORY.
 * @author Mike
 */
public interface RequestEnhancerFactory {
    /**
     * Factory method to create a RequestEnhancer object
     * @return Created RequestEnhancer
     */
    RequestEnhancer create();
}
