//VJDBC - Virtual JDBC
//Written by Michael Link
//Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.serial;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

class ArrayAccessors {
    private static Map _arrayAccessors = new HashMap();
    private static ArrayAccess _objectArrayFiller = new ArrayAccessObject();

    static {
        _arrayAccessors.put(Boolean.TYPE, new ArrayAccessBoolean());
        _arrayAccessors.put(Byte.TYPE, new ArrayAccessByte());
        _arrayAccessors.put(Character.TYPE, new ArrayAccessCharacter());
        _arrayAccessors.put(Short.TYPE, new ArrayAccessShort());
        _arrayAccessors.put(Integer.TYPE, new ArrayAccessInteger());
        _arrayAccessors.put(Long.TYPE, new ArrayAccessLong());
        _arrayAccessors.put(Float.TYPE, new ArrayAccessFloat());
        _arrayAccessors.put(Double.TYPE, new ArrayAccessDouble());
    }

    static ArrayAccess getArrayAccessorForPrimitiveType(Class primitiveType) {
        return (ArrayAccess) _arrayAccessors.get(primitiveType);
    }

    static ArrayAccess getObjectArrayAccessor() {
        return _objectArrayFiller;
    }

    private static class ArrayAccessBoolean implements ArrayAccess {
        public Object getValue(Object array, int index, boolean[] nullFlags) {
            if(nullFlags[index]) {
                return null;
            } else {
                return Boolean.valueOf(Array.getBoolean(array, index));
            }
        }
    }

    private static class ArrayAccessByte implements ArrayAccess {
        public Object getValue(Object array, int index, boolean[] nullFlags) {
            if(nullFlags[index]) {
                return null;
            } else {
                return new Byte(Array.getByte(array, index));
            }
        }
    }

    private static class ArrayAccessCharacter implements ArrayAccess {
        public Object getValue(Object array, int index, boolean[] nullFlags) {
            if(nullFlags[index]) {
                return null;
            } else {
                return new Character(Array.getChar(array, index));
            }
        }
    }

    private static class ArrayAccessShort implements ArrayAccess {
        public Object getValue(Object array, int index, boolean[] nullFlags) {
            if(nullFlags[index]) {
                return null;
            } else {
                return new Short(Array.getShort(array, index));
            }
        }
    }

    private static class ArrayAccessInteger implements ArrayAccess {
        public Object getValue(Object array, int index, boolean[] nullFlags) {
            if(nullFlags[index]) {
                return null;
            } else {
                return new Integer(Array.getInt(array, index));
            }
        }
    }

    private static class ArrayAccessLong implements ArrayAccess {
        public Object getValue(Object array, int index, boolean[] nullFlags) {
            if(nullFlags[index]) {
                return null;
            } else {
                return new Long(Array.getLong(array, index));
            }
        }
    }

    private static class ArrayAccessFloat implements ArrayAccess {
        public Object getValue(Object array, int index, boolean[] nullFlags) {
            if(nullFlags[index]) {
                return null;
            } else {
                return new Float(Array.getFloat(array, index));
            }
        }
    }

    private static class ArrayAccessDouble implements ArrayAccess {
        public Object getValue(Object array, int index, boolean[] nullFlags) {
            if(nullFlags[index]) {
                return null;
            } else {
                return new Double(Array.getDouble(array, index));
            }
        }
    }

    private static class ArrayAccessObject implements ArrayAccess {
        public Object getValue(Object array, int index, boolean[] nullFlags) {
            return Array.get(array, index);
        }
    }
}