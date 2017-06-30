// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.CallingContext;

/**
 * This class produces standard Calling-Contexts which contain the callstack of the
 * executing command. 
 */
public class StandardCallingContextFactory implements CallingContextFactory {
    public CallingContext create() {
        return new CallingContext();
    }
}
