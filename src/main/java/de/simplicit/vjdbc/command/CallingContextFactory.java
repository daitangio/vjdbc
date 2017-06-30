// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import de.simplicit.vjdbc.serial.CallingContext;

/**
 * A CallingContextFactory creates CallingContext objects. 
 * @author Mike
 */
public interface CallingContextFactory {
    CallingContext create();
}
