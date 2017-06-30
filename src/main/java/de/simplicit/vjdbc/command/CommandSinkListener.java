// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

/**
 * Interface for objects which are interested what is happening in the command sink.
 */
public interface CommandSinkListener {
    void preExecution(Command cmd);

    void postExecution(Command cmd);
}
