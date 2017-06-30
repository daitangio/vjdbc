// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc;

import de.simplicit.vjdbc.serial.UIDEx;

/**
 * An interface for a JDBC object that can be reconstructed by the client from
 * a network proxy.  The client must implement a ProxyFactory that can take
 * the proxied object and turn it back into the proper client side JDBC object.
 */
public interface ProxiedObject extends Registerable {

    /**
     * The object to be serialized and transported via the command sink.
     * The returned value must implement Serializable or Externalizable
     */
    public Object getProxy();
}
