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

    public void setToken( String stateId, String token ) throws URISyntaxException, SQLException, ClassNotFoundException {
        System.out.println( "Set auth token" );
        try ( Connection connection = PostgresConnection.getConnection() ) {
            Statement statement = connection.createStatement();
            statement.executeQuery( "DROP TABLE auth" );
            statement.executeQuery( "CREATE TABLE auth(stateid VARCHAR(50), token VARCHAR(500))" );
            statement.executeUpdate( "INSERT INTO auth VALUES ('" + stateId + "', '" + token + "')" );
        }
    }

    public String getToken( String stateId ) throws URISyntaxException, SQLException, ClassNotFoundException {
        System.out.println( "Get auth token" );
        //dump();
        try ( Connection connection = PostgresConnection.getConnection() ) {
            Statement statement = connection.createStatement();
            String sql = "SELECT token FROM auth WHERE stateid='" + stateId + "'";
            System.out.println( sql );
            try ( ResultSet resultSet = statement.executeQuery( sql ) ) {
                if ( resultSet.next() ) {
                    System.out.println( "Auth token: " + resultSet.getString( 1 ) );
                    return resultSet.getString( 1 );
                }
            }
        }
        return null;
    }

    private void dump() throws ClassNotFoundException, SQLException, URISyntaxException {
        try ( Connection connection = PostgresConnection.getConnection() ) {
            Statement statement = connection.createStatement();
            try ( ResultSet resultSet = statement.executeQuery( "SELECT * FROM auth" ) ) {
                while ( resultSet.next() ) {
                    System.out.println( "----ROW----" );
                    System.out.println( resultSet.getString( 0 ) );
                    System.out.println( resultSet.getString( 1 ) );
                }
            }
        }
    }
}
