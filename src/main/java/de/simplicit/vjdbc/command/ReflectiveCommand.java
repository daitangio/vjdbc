// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.simplicit.vjdbc.util.SQLExceptionHelper;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class ReflectiveCommand implements Command, Externalizable {
    static final long serialVersionUID = 1573361368678688726L;

    private static Log _logger = LogFactory.getLog(ReflectiveCommand.class);
    private static final Object[] _zeroParameters = new Object[0];

    private int _interfaceType;
    private String _cmd;
    private Object[] _parameters;
    private int _parameterTypes;
    private transient Class _targetClass;

    public ReflectiveCommand() {
    }

    public ReflectiveCommand(int interfaceType, String cmd) {
        _interfaceType = interfaceType;
        _cmd = cmd;
        _parameters = _zeroParameters;
    }

    public ReflectiveCommand(int interfaceType, String cmd, Object[] parms, int parmTypes) {
        _interfaceType = interfaceType;
        _cmd = cmd;
        _parameters = parms;
        _parameterTypes = parmTypes;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_interfaceType);
        out.writeUTF(_cmd);
        out.writeInt(_parameters.length);
        for (int i = 0; i < _parameters.length; i++) {
            out.writeObject(_parameters[i]);
        }
        out.writeInt(_parameterTypes);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _interfaceType = in.readInt();
        _cmd = in.readUTF();
        int len = in.readInt();
        _parameters = new Object[len];
        for (int i = 0; i < _parameters.length; i++) {
            _parameters[i] = in.readObject();
        }
        _parameterTypes = in.readInt();
    }

    public Object execute(Object target, ConnectionContext ctx) throws SQLException {
        try {
            _targetClass = JdbcInterfaceType._interfaces[_interfaceType];
            Method method = _targetClass.getDeclaredMethod(_cmd, ParameterTypeCombinations._typeCombinations[_parameterTypes]);
            return method.invoke(target, _parameters);
        } catch(NoSuchMethodException e) {
            String msg = "No such method '" + _cmd + "' on object " + target + " (Target-Class " + _targetClass.getName() + ")";
            _logger.warn(msg);
            _logger.warn(getParameterTypesAsString());
            throw SQLExceptionHelper.wrap(e);
        } catch(SecurityException e) {
            String msg = "Security exception with '" + _cmd + "' on object " + target;
            _logger.error(msg, e);
            throw SQLExceptionHelper.wrap(e);
        } catch(IllegalAccessException e) {
            String msg = "Illegal access exception with '" + _cmd + "' on object " + target;
            _logger.error(msg, e);
            throw SQLExceptionHelper.wrap(e);
        } catch(IllegalArgumentException e) {
            String msg = "Illegal argument exception with '" + _cmd + "' on object " + target;
            _logger.error(msg, e);
            throw SQLExceptionHelper.wrap(e);
        } catch(InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            String msg = "Unexpected invocation target exception: " + targetException.toString();
            _logger.warn(msg, targetException);
            throw SQLExceptionHelper.wrap(targetException);
        } catch(Exception e) {
            String msg = "Unexpected exception: " + e.toString();
            _logger.error(msg, e);
            throw SQLExceptionHelper.wrap(e);
        }
    }

    public int getInterfaceType() {
        return _interfaceType;
    }

    public void setInterfaceType(int interfaceType) {
        _interfaceType = interfaceType;
    }

    public String getCommand() {
        return _cmd;
    }

    public void setCommand(String cmd) {
        _cmd = cmd;
    }

    public Object[] getParameters() {
        return _parameters;
    }

    public void setParameters(Object[] parameters) {
        _parameters = parameters;
    }

    public int getParameterTypes() {
        return _parameterTypes;
    }

    public void setParameterTypes(int parameterTypes) {
        _parameterTypes = parameterTypes;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ReflectiveCommand '").append(_cmd).append("'");
        if(_targetClass != null) {
            sb.append(" on object of class ").append(_targetClass.getName());
        }
        if(_parameters.length > 0) {
            sb.append(" with ").append(_parameters.length).append(" parameters\n");
            for(int i = 0, n = _parameters.length; i < n; i++) {
                sb.append("\t[").append(i).append("] ");
                if(_parameters[i] != null) {
                    String value = _parameters[i].toString();
                    if(value.length() > 0) {
                        sb.append(value);
                    } else {
                        sb.append("<empty>");
                    }
                } else {
                    sb.append("<null>");
                }
                if(i < n - 1) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    private String getParameterTypesAsString() {
        Class[] parameterTypes = ParameterTypeCombinations._typeCombinations[_parameterTypes];
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < parameterTypes.length; i++) {
            buff.append("Parameter-Type ").append(i).append(": ").append(parameterTypes[i].getName()).append("\n");
        }
        return buff.toString();
    }
}
