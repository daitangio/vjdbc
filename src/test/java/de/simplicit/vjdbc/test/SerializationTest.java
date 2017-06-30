package de.simplicit.vjdbc.test;

import de.simplicit.vjdbc.serial.StreamingResultSet;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SerializationTest {
    public static void main(String[] args) throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/HSqlDb", "sa", "");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from Address");
        StreamingResultSet srs = new StreamingResultSet(1000, true, false, "UTF-8");
        srs.populate(rs);
        byte[] serSRS = serializeObject(srs);
        System.out.println("Size SRS: " + serSRS.length);
        conn.close();
        ResultSet rs2 = (ResultSet)deserializeObject(serSRS);
        rs2.close();
    }
    
    private static byte[] serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        return baos.toByteArray();
    }

    private static Object deserializeObject(byte[] b) throws ClassNotFoundException, IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
}
