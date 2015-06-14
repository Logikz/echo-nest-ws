package com.logikz.dao;

import com.logikz.utils.PostgresConnection;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Nick on 6/13/2015.
 */
public class PostgresDAO {

    public PostgresDAO() {
    }

    public void setToken(String stateId, String token) throws URISyntaxException, SQLException, ClassNotFoundException {
        try(Connection connection = PostgresConnection.getConnection()){
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS nest (stateId VARCHAR(50), token VARCHAR(500))");
            statement.executeUpdate("INSERT INTO nest VALUES (" + stateId + ", " + token + ")");
        }
    }

    public String getToken(String stateId) throws URISyntaxException, SQLException, ClassNotFoundException {
        try(Connection connection = PostgresConnection.getConnection()){
            Statement statement = connection.createStatement();

            try(ResultSet resultSet = statement.executeQuery("SELECT token FROM nest WHERE stateId=" + stateId)){
                if(resultSet.next()){
                    return resultSet.getString(0);
                }
            }
        }
        return null;
    }
}
