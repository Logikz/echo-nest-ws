package com.logikz.api;


import com.logikz.dao.NestDAO;
import com.logikz.dao.PostgresDAO;
import com.logikz.service.NestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Created by Nick on 6/13/2015.
 */
@Path( "/nest" )
@Api( value = "Nest WS APIs." )
public class NestResources {

    private static final Logger LOG = LoggerFactory.getLogger( NestResources.class );

    @PUT
    @Path( "/{stateId}/temperature/" )
    @Consumes( MediaType.TEXT_PLAIN )
    @ApiOperation( value = "Sets the temperature of the Nest thermostat" )
    @ApiResponses( { @ApiResponse( code = 200, message = "Successful" ), @ApiResponse( code = 404, message = "State ID not " +
            "found" ) } )
    public Response setTemperature( @PathParam( "stateId" ) String stateId, int temperature ) {
        LOG.trace( "Temperature: " + temperature );

        PostgresDAO postgresDAO = PostgresDAO.getInstance();
        NestDAO nestDAO = NestDAO.getInstance();

        NestService nestService = new NestService(postgresDAO, nestDAO);

        return nestService.setTemperature( stateId, temperature );
    }

    @PUT
    @Path( "/{stateId}/thermostat" )
    @ApiOperation( value = "Sets the temperature to a certain state." )
    @ApiResponses( { @ApiResponse( code = 200, message = "Successful" ), @ApiResponse( code = 404, message = "Token " +
            "not found" ) } )
    public Response setThermostatToState( @PathParam( "stateId" ) String stateId, String state ) {
        LOG.trace( "State: " + state );
        PostgresDAO postgresDAO = PostgresDAO.getInstance();
        NestDAO nestDAO = NestDAO.getInstance();

        NestService nestService = new NestService( postgresDAO, nestDAO );

        return nestService.setThermostatToState(stateId, state);
    }

    @GET
    @Path( "/{stateId}/temperature" )
    @Produces( MediaType.TEXT_PLAIN )
    @ApiOperation( value = "Gets the ambient temperature" )
    @ApiResponses( { @ApiResponse( code = 200, message = "Successful" ), @ApiResponse( code = 404, message = "Token " +
            "not found" ) } )
    public Response getAmbientTemperature( @PathParam( "stateId" ) String stateId ) {
        LOG.trace( "Get ambient temperature" );
        PostgresDAO postgresDAO = PostgresDAO.getInstance();
        NestDAO nestDAO = NestDAO.getInstance();

        NestService nestService = new NestService( postgresDAO, nestDAO );

        return nestService.getAmbientTemperature(stateId);
    }

    @GET
    @Path( "{stateId}/auth/callback" )
    public Response callback( @PathParam( "stateId" ) String stateId, @QueryParam( "code" ) String code ) {
        LOG.trace( "Add new API token" );
        PostgresDAO postgresDAO = PostgresDAO.getInstance();

        NestService nestService = new NestService( postgresDAO, null);

        return nestService.setApiToken(stateId, code);
    }

    @GET
    @Path( "ping" )
    @ApiOperation( value = "test the service is alive" )
    public Response ping() {
        LOG.trace( "pong" );
        return Response.ok().build();
    }
}
