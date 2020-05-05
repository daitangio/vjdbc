/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.simplicit.vjdbc.server.config;

import javax.xml.bind.annotation.*;

/**
<vjdbc-configuration> 
  <rmi registryPort="2000" remotingPort="2001"/>
  <connection 
	id="mydb"
	driver="my.db.DriverClass"
	url="my:jdbc:dbUrl"
	user="myuser"
	password="mypassword"
	ignoreSQLFeatureNotSupportedExceptions="false"
  />
</vjdbc-configuration>
* @author JerrySmith
 */
@XmlRootElement(name = "VJdbcConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class VJdbcConfiguration {
    private static VJdbcConfiguration singleton;
    
    public static VJdbcConfiguration singleton() {
        return singleton;
    }
    
    public static void set(VJdbcConfiguration s) {
        singleton = s;
    }
    
    @XmlElement(name = "occt")
    private OcctConfiguration occt = new OcctConfiguration();
    @XmlElement(name = "connection")
    private ConnectionConfiguration connection = new ConnectionConfiguration();
    @XmlElement(name = "rmi")
    private RmiConfiguration rmi = new RmiConfiguration();
    
    private boolean useCustomResultSetHandling = true;
    
    public OcctConfiguration getOcct() {
        return occt;
    }

    public void setOcct(OcctConfiguration occt) {
        this.occt = occt;
    }

    public ConnectionConfiguration getConnection() {
        return connection;
    }

    public void setConnection(ConnectionConfiguration connection) {
        this.connection = connection;
    }

    public RmiConfiguration getRmi() {
        return rmi;
    }

    public void setRmi(RmiConfiguration rmi) {
        this.rmi = rmi;
    }

    public boolean isUseCustomResultSetHandling() {
        return useCustomResultSetHandling;
    }

    public void setUseCustomResultSetHandling(boolean useCustomResultSetHandling) {
        this.useCustomResultSetHandling = useCustomResultSetHandling;
    }
}
