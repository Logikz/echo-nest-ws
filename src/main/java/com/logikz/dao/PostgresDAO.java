package com.logikz.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.sql.*;


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


        connection.prepareStatement( "DROP TABLE auth" ).executeQuery();
        connection.prepareStatement( "CREATE TABLE auth(stateid VARCHAR(50), token VARCHAR(500))" ).executeQuery();
        PreparedStatement statement = connection.prepareStatement( "INSERT INTO auth VALUES (?, ?" );
        statement.setString( 1, stateId );
        statement.setString( 2, token );
        statement.executeUpdate();

    }

    public String getToken( String stateId, Connection connection ) throws URISyntaxException, SQLException, ClassNotFoundException {
        LOG.trace( "Get auth token" );
        //dump();

        PreparedStatement statement = connection.prepareStatement( "SELECT token FROM auth WHERE stateid=?" );
        statement.setString( 1, stateId );

        try ( ResultSet resultSet = statement.executeQuery() ) {
            if ( resultSet.next() ) {
                LOG.trace( "Auth token: " + resultSet.getString( 1 ) );
                return resultSet.getString( 1 );
            }
        }

        return null;
    }

    private void dump( Connection connection ) throws ClassNotFoundException, SQLException, URISyntaxException {
        PreparedStatement statement = connection.prepareStatement( "SELECT * FROM auth" )
        try ( ResultSet resultSet = statement.executeQuery() ) {
            while ( resultSet.next() ) {
                LOG.trace( "----ROW----" );
                LOG.trace( resultSet.getString( 0 ) );
                LOG.trace( resultSet.getString( 1 ) );
            }
        }
    }
}
