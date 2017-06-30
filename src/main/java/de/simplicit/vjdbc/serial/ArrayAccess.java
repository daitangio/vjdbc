//VJDBC - Virtual JDBC
//Written by Michael Link
//Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

interface ArrayAccess {
    Object getValue(Object array, int index, boolean[] nullFlags);
}
