package com.logikz.dao;

import com.logikz.model.NestError;
import com.logikz.model.NestToken;
import com.owlike.genson.Genson;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Nick on 6/13/2015.
 */
public class NestDAO {
    public static final String CLIENT_ID = "2f2612c7-6d36-4c5c-8c19-2903bb7be430";
    public static final String CLIENT_SECRET = "m6RnIa8q3LmWqiszwfyVCUkJ4";
    public static final String THERMOSTAT = "LULEEUtZ_cQQs-LF12lyuhOizG3Nxo_B";

    public Response setTemperature( String token, int temperature ) {
        System.out.println("Setting the temperature to " + temperature);
        Response response = ClientBuilder.newClient()
                                         .target( "https://developer-api.nest.com" )
                                         .path( "/devices/thermostats/" + THERMOSTAT + "/target_temperature_f" )
                                         .queryParam( "auth", token )
                                         .request()
                                         .accept( MediaType.APPLICATION_JSON )
                                         .post( Entity.entity( temperature, MediaType.TEXT_PLAIN_TYPE ) );
        if ( response.getStatus() != 200 ) {
            System.out.println("Failed...trying again in 5 seconds");
            System.out.println(response.readEntity( String.class ));
//            try {
//                for(int i=0; i < 10; ++i){
//                    Thread.sleep( 1000 * 5 );
//                    setTemperature( token, temperature );
//                }
//
//            } catch ( InterruptedException e ) {
//                return Response.ok().build();
//            }
        }

        return response;
    }

    public void doNestAuthorization( String stateId ) {
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation( "https://home.nest.com/login/oauth2" )
                    .setClientId( CLIENT_ID )
                    .setState( stateId )
                    .setRedirectURI( "https://nest-echo.herokuapp.com/" + stateId + "/auth/callback" )
                    .buildQueryMessage();
            String locationUri = request.getLocationUri();
            System.out.println( "GET: " + locationUri );

            String entity = ClientBuilder.newClient().target( locationUri ).request().get( String.class );

            System.out.println( "ENTITY: " + entity );
        } catch ( OAuthSystemException e ) {
            System.out.println( e.getMessage() );
        }
    }

    public String getToken( String code ) throws OAuthSystemException {
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation( "https://api.home.nest.com/oauth2/access_token" )
                .setGrantType( GrantType.AUTHORIZATION_CODE )
                .setClientId( CLIENT_ID )
                .setClientSecret( CLIENT_SECRET )
                .setCode( code )
                .buildQueryMessage();

        String locationUri = request.getLocationUri();
        System.out.println( "POST: " + locationUri );

        Response response = ClientBuilder.newClient()
                                         .target( locationUri )
                                         .request()
                                         .accept( MediaType.APPLICATION_JSON )
                                         .post( Entity.entity( "", MediaType.TEXT_PLAIN_TYPE ) );
        System.out.println( "POST COMPLETE" );
        try {
            if ( response.getStatus() == 400 ) {
                String error = response.readEntity( String.class );
                System.out.println( "response: " + error );
                Genson genson = new Genson();
                NestError nestError = genson.deserialize( error, NestError.class );
                System.out.println( "ERROR:" + nestError.getError_description() );

                return null;
            } else {
                String success = response.readEntity( String.class );
                System.out.println( "response: " + success );
                Genson genson = new Genson();
                NestToken token = genson.deserialize( success, NestToken.class );

                System.out.println( "Token: " + token.getAccess_token() );

                return token.getAccess_token();
            }
        } catch ( Exception e ) {
            System.out.println( e.getMessage() );
            return null;
        }

    }
}
