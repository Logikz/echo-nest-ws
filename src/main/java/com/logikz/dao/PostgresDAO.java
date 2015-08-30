package com.logikz.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by Nick on 6/13/2015.
 */
public class PostgresDAO {
    private static final Logger LOG = LoggerFactory.getLogger( PostgresDAO.class );

    private static PostgresDAO INSTANCE = getInstance();

    public static PostgresDAO getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new PostgresDAO();
        }

        return INSTANCE;
    }

    private PostgresDAO() {
    }

    public void setToken( String stateId, String token, Connection connection ) throws URISyntaxException, SQLException, ClassNotFoundException {
        LOG.trace( "Set auth token" );

        Statement statement = connection.createStatement();
        statement.executeQuery( "DROP TABLE auth" );
        statement.executeQuery( "CREATE TABLE auth(stateid VARCHAR(50), token VARCHAR(500))" );
        statement.executeUpdate( "INSERT INTO auth VALUES ('" + stateId + "', '" + token + "')" );

    }

    public String getToken( String stateId, Connection connection ) throws URISyntaxException, SQLException, ClassNotFoundException {
        LOG.trace( "Get auth token" );
        //dump();

        Statement statement = connection.createStatement();
        String sql = "SELECT token FROM auth WHERE stateid='" + stateId + "'";
        LOG.trace( sql );
        try ( ResultSet resultSet = statement.executeQuery( sql ) ) {
            if ( resultSet.next() ) {
                LOG.trace( "Auth token: " + resultSet.getString( 1 ) );
                return resultSet.getString( 1 );
            }
        }

        return null;
    }

    private void dump( Connection connection ) throws ClassNotFoundException, SQLException, URISyntaxException {
        Statement statement = connection.createStatement();
        try ( ResultSet resultSet = statement.executeQuery( "SELECT * FROM auth" ) ) {
            while ( resultSet.next() ) {
                LOG.trace( "----ROW----" );
                LOG.trace( resultSet.getString( 0 ) );
                LOG.trace( resultSet.getString( 1 ) );
            }
        }
    }
}
