package com.logikz.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.logikz.dao.NestDAO;
import com.logikz.dao.PostgresDAO;
import com.logikz.utils.PostgresConnection;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Nick on 8/30/2015.
 */
public class NestService {
    private static final Logger LOG = LoggerFactory.getLogger( NestService.class );
    private static final Response RESPONSE_NOT_FOUND = Response.status( Response.Status.NOT_FOUND ).build();
    private static final Response RESPONSE_INTERNAL_SERVER_ERROR = Response.status( Response.Status
            .INTERNAL_SERVER_ERROR ).build();

    private PostgresDAO postgresDAO;
    private NestDAO nestDAO;

    public NestService( PostgresDAO postgresDAO, NestDAO nestDAO ) {
        Preconditions.checkNotNull( postgresDAO );
        this.postgresDAO = postgresDAO;

        //it's ok for nestDAO to be null in some service calls don't hit the Nest WS
        this.nestDAO = nestDAO;
    }

    public Response setTemperature( String stateId, int temperature ) {
        Preconditions.checkNotNull( nestDAO );
        try {
            try ( Connection connection = PostgresConnection.getConnection() ) {
                String token = postgresDAO.getToken( stateId, connection );
                if ( Strings.isNullOrEmpty( token ) ) {
                    LOG.warn( "Token not found" );
                    return RESPONSE_NOT_FOUND;
                }

                nestDAO.setTemperature( token, temperature );
            }
        } catch ( URISyntaxException | SQLException | ClassNotFoundException e ) {
            LOG.error( e.getMessage(), e );
            return RESPONSE_INTERNAL_SERVER_ERROR;
        }

        return Response.ok().build();
    }

    public Response setThermostatToState( String stateId, String state ) {
        Preconditions.checkNotNull( nestDAO );
        try {
            try ( Connection connection = PostgresConnection.getConnection() ) {
                String token = postgresDAO.getToken( stateId, connection );
                if ( Strings.isNullOrEmpty( token ) ) {
                    LOG.warn( "Token not found" );
                    return RESPONSE_NOT_FOUND;
                }

                nestDAO.setThermostat( token, state );
            }
        } catch ( URISyntaxException | SQLException | ClassNotFoundException e ) {
            LOG.error( e.getMessage(), e );
            return RESPONSE_INTERNAL_SERVER_ERROR;
        }

        return Response.ok().build();
    }

    public Response getAmbientTemperature( String stateId ) {
        Preconditions.checkNotNull( nestDAO );
        try {
            try ( Connection connection = PostgresConnection.getConnection() ) {
                String token = postgresDAO.getToken( stateId, connection );
                if ( Strings.isNullOrEmpty( token ) ) {
                    LOG.warn( "Token not found" );
                    return RESPONSE_NOT_FOUND;
                }

                int temperature = nestDAO.getAmbientTemperature( token );
                LOG.trace( "ambient temperature: " + temperature );

                return Response.ok( temperature ).build();
            }
        } catch ( URISyntaxException | SQLException | ClassNotFoundException e ) {
            LOG.error( e.getMessage(), e );
            return RESPONSE_INTERNAL_SERVER_ERROR;
        }
    }

    public Response setApiToken( String stateId, String code ) {
        LOG.trace( "StateID: " + stateId );
        LOG.trace( "code: " + code );
        try {
            try ( Connection connection = PostgresConnection.getConnection() ) {
                String token = nestDAO.getToken( code );
                postgresDAO.setToken( stateId, token, connection );
            }
        } catch ( URISyntaxException | SQLException | ClassNotFoundException | OAuthSystemException e ) {
            LOG.error( e.getMessage(), e );
        }

        return Response.ok().build();
    }
}
