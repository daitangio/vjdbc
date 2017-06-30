// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.CallingContext;

/**
 * Dummy class which is doesn't create CallingContexts but returns null.
 */
public class NullCallingContextFactory implements CallingContextFactory {
    public CallingContext create() {
        return null;
    }
}
