package com.logikz.api;


import com.logikz.dao.NestDAO;
import com.logikz.dao.PostgresDAO;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.sql.SQLException;


/**
 * Created by Nick on 6/13/2015.
 */
@Path( "/nest" )
public class NestResources {

    @PUT
    @Path( "/{stateId}/temperature/" )
    @Consumes( MediaType.TEXT_PLAIN )
    public Response setTemperature( @PathParam( "stateId" ) String stateId, int temperature ) {
        System.out.println( "Temperature: " + temperature );
        PostgresDAO dao = new PostgresDAO();
        NestDAO nestDAO = new NestDAO();
        try {
            String token = dao.getToken( stateId );
            nestDAO.setTemperature( token, temperature );
        } catch ( SQLException | URISyntaxException | ClassNotFoundException e ) {
            System.out.println( e.getMessage() );
        }
        //nestDAO.doNestAuthorization(stateId);

        return Response.ok().build();
    }

    @PUT
    @Path( "/{stateId}/thermostat" )
    public Response setTemperatureToState( @PathParam( "stateId" ) String stateId, String state ) {
        PostgresDAO dao = new PostgresDAO();
        NestDAO nestDAO = new NestDAO();

        try {
            String token = dao.getToken( stateId );
            nestDAO.setThermostat( token, state );
            return Response.ok().build();
        } catch ( ClassNotFoundException | SQLException | URISyntaxException e ) {
            e.printStackTrace();
        }

        return Response.status( Response.Status.BAD_REQUEST ).build();
    }

    @GET
    @Path( "/{stateId}/temperature" )
    @Produces( MediaType.TEXT_PLAIN )
    public Response getAmbientTemperature( @PathParam( "stateId" ) String stateId ) {
        PostgresDAO dao = new PostgresDAO();
        NestDAO nestDAO = new NestDAO();
        try {
            String token = dao.getToken( stateId );
            int temperature = nestDAO.getAmbientTemperature( token );
            return Response.ok( temperature ).build();
        } catch ( ClassNotFoundException | SQLException | URISyntaxException e ) {
            e.printStackTrace();
        }

        return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
    }

    @GET
    @Path( "{stateId}/auth/callback" )
    public Response callback( @PathParam( "stateId" ) String stateId, @QueryParam( "code" ) String code ) {
        NestDAO nestDAO = new NestDAO();
        PostgresDAO postgresDAO = new PostgresDAO();
        System.out.println( "StateID: " + stateId );
        System.out.println( "code: " + code );
        try {
            String token = nestDAO.getToken( code );
            postgresDAO.setToken( stateId, token );
        } catch ( URISyntaxException | SQLException | ClassNotFoundException | OAuthSystemException e ) {
            System.out.println( e.getMessage() );
        }

        return Response.ok().build();
    }

    @GET
    @Path( "ping" )
    public Response ping() {
        return Response.ok().build();
    }
}
