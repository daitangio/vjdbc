// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class holds configuration information for the OCCT.
 */
public class OcctConfiguration {
    private static Log _logger = LogFactory.getLog(OcctConfiguration.class);

    private long _checkingPeriod = 30000;
    private long _timeout = 120000;

    public OcctConfiguration() {
    }

    public long getCheckingPeriodInMillis() {
        return _checkingPeriod;
    }

    public void setCheckingPeriodInMillis(long checkingPeriod) {
        if(checkingPeriod != 0 && checkingPeriod <= 1000) {
            _logger.error("Checking-Period must be greater than 1 second");
        }
        else {
            _checkingPeriod = checkingPeriod;
        }
    }

    public long getTimeoutInMillis() {
        return _timeout;
    }

    public void setTimeoutInMillis(long timeout) {
        if(timeout > 0 && timeout <= 1000) {
            _logger.error("Timeout must be greater than 1 second " + timeout);
        }
        else {
            _timeout = timeout;
        }
    }

    void log() {
        if(_checkingPeriod > 0) {
            _logger.info("OrphanedConnectionCollectorTask-Configuration (OCCT)");
            _logger.info("  Checking-Period........... " + ConfigurationUtil.getStringFromMillis(_checkingPeriod));
            _logger.info("  Timeout................... " + ConfigurationUtil.getStringFromMillis(_timeout));
        }
        else {
            _logger.info("OrphanedConnectionCollectorTask-Configuration (OCCT): off");
        }
    }
}
