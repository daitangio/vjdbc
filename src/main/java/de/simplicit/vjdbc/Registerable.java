// VJDBC - Virtual JDBC
// Written by Hunter Payne
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc;

import de.simplicit.vjdbc.serial.UIDEx;

/**
 * Indicates that an object knows the id it wants to be registered under
 */
public interface Registerable {

    public UIDEx getReg();
}
