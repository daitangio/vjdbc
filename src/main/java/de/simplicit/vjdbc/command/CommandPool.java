// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

/**
 * Potential pool implementation for creating ReflectiveCommand-Objects. Yet
 * only a dummy implementation.
 */
public class CommandPool {
    public static Command getReflectiveCommand(int interfaceType, String cmdstr) {
        return new ReflectiveCommand(interfaceType, cmdstr);
    }

    public static Command getReflectiveCommand(int interfaceType, String cmdstr, Object[] parms, int types) {
        return new ReflectiveCommand(interfaceType, cmdstr, parms, types);
    }
}
