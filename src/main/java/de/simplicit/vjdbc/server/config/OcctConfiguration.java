// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.config;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * This class holds configuration information for the OCCT.
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class OcctConfiguration {
    private static Logger _logger = Logger.getLogger(OcctConfiguration.class.getName());

    @XmlAttribute(name = "checkingPeriod")
    private long _checkingPeriod = 30000;
    @XmlAttribute(name = "timeout")
    private long _timeout = 120000;

    public OcctConfiguration() {
    }

    public long getCheckingPeriodInMillis() {
        return _checkingPeriod;
    }

    public void setCheckingPeriodInMillis(long checkingPeriod) {
        _checkingPeriod = checkingPeriod;
    }

    public long getTimeoutInMillis() {
        return _timeout;
    }

    public void setTimeoutInMillis(long timeout) {
        _timeout = timeout;
    }
    
    public void validate() throws Exception {
        if(_checkingPeriod != 0 && _checkingPeriod <= 1000) {
            throw new Exception("Checking-Period must be greater than 1 second");
        }
        if(_timeout > 0 && _timeout <= 1000) {
            throw new Exception("Timeout must be greater than 1 second " + _timeout);
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
