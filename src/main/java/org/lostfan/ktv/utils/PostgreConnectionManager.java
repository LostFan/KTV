package org.lostfan.ktv.utils;

import org.lostfan.ktv.ApplicationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreConnectionManager extends ConnectionManager {

    private Connection connection;

    public PostgreConnectionManager() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/KTV", "postgres", "postgres");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            throw new ApplicationException("appErrors.connectionFailed", ex);
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void close() {
//        try {
//            this.connection.createStatement().execute("SHUTDOWN");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}
