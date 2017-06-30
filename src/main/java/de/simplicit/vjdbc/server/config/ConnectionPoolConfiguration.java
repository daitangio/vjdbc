// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectionPoolConfiguration {
    private static Log _logger = LogFactory.getLog(ConnectionPoolConfiguration.class);

    protected int _maxActive = 8;
    protected int _maxIdle = 8;
    protected int _minIdle = 0;
    protected long _maxWait = -1;
    protected int _timeBetweenEvictionRunsMillis = -1;
    protected int _minEvictableIdleTimeMillis = 1000 * 60 * 30;

    public ConnectionPoolConfiguration() {
    }

    public int getMaxActive() {
        return _maxActive;
    }

    public void setMaxActive(int maxActive) {
        _maxActive = maxActive;
    }

    public int getMaxIdle() {
        return _maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        _maxIdle = maxIdle;
    }

    public long getMaxWait() {
        return _maxWait;
    }

    public void setMaxWait(long maxWait) {
        _maxWait = maxWait;
    }

    public int getMinEvictableIdleTimeMillis() {
        return _minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        _minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public int getMinIdle() {
        return _minIdle;
    }

    public void setMinIdle(int minIdle) {
        _minIdle = minIdle;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return _timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        _timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    void log() {
        _logger.info("  ConnectionPool-Configuration");
        _logger.info("    Max. active connections .............. " + _maxActive);
        _logger.info("    Max. number of idle connections ...... " + _maxIdle);
        _logger.info("    Min. number of idle connections ...... " + _minIdle);
        _logger.info("    Max. waiting time for connections .... " + ConfigurationUtil.getStringFromMillis(_maxWait));
        _logger.info("    Time between eviction runs ........... " + ConfigurationUtil.getStringFromMillis(_timeBetweenEvictionRunsMillis));
        _logger.info("    Min. idle time before eviction ....... " + ConfigurationUtil.getStringFromMillis(_minEvictableIdleTimeMillis));
    }
}
