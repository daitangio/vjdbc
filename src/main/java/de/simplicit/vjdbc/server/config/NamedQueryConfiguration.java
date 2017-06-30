// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NamedQueryConfiguration {
    private static Log _logger = LogFactory.getLog(NamedQueryConfiguration.class);
    private Map _queryMap = new HashMap();

    public Map getQueryMap() {
        return _queryMap;
    }

    public void addEntry(String id, String sql) {
        _queryMap.put(id, sql);
    }

    public String getSqlForId(String id) throws SQLException {
        String result = (String)_queryMap.get(id);
        if(result != null) {
            return result;
        }
        else {
            String msg = "Named-Query for key '" + id + "' not found";
            _logger.error(msg);
            throw new SQLException(msg);
        }
    }

    void log() {
        _logger.info("  Named Query-Configuration:");

        for (Iterator it = _queryMap.keySet().iterator(); it.hasNext();) {
            String id = (String) it.next();
            _logger.info("    [" + id + "] = [" + _queryMap.get(id) + "]");
        }
    }
}
