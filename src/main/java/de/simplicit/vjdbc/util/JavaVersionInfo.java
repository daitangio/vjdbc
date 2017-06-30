// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.util;

/**
 * Helper class which provides information about the used Java version
 */
public class JavaVersionInfo {
    public static final boolean use14Api = System.getProperty("java.specification.version").compareTo("1.4") >= 0;
}
