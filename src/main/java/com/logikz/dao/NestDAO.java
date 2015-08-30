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
    public static final String CLIENT_ID = System.getenv( "NEST_CLIENT_ID" );
    public static final String CLIENT_SECRET = System.getenv( "NEST_CLIENT_SECRET" );
    public static final String THERMOSTAT = System.getenv( "NEST_THERMOSTAT_ID" );
    private static NestDAO INSTANCE = getInstance();

    public static NestDAO getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new NestDAO();
        }
        return INSTANCE;
    }


    private NestDAO() {
    }


    public Response setTemperature( String token, int temperature ) {
        System.out.println( "Setting the temperature to " + temperature );
        Response response = ClientBuilder.newClient()
                                         .target( "https://developer-api.nest.com" )
                                         .path( "/devices/thermostats/" + THERMOSTAT + "/target_temperature_f" )
                                         .queryParam( "auth", token )
                                         .request()
                                         .accept( MediaType.APPLICATION_JSON )
                                         .put( Entity.entity( temperature, MediaType.TEXT_PLAIN_TYPE ) );

        if ( response.getStatus() != 200 ) {
            System.out.println( "Failed...trying again in 5 seconds" );
            System.out.println( response.readEntity( String.class ) );
        }

        return response;
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

    public int getTargetTemperature( String token ) {
        System.out.println( "Getting the temperature" );
        Response response = ClientBuilder.newClient()
                                         .target( "https://developer-api.nest.com" )
                                         .path( "/devices/thermostats/" + THERMOSTAT + "/target_temperature_f" )
                                         .queryParam( "auth", token )
                                         .request()
                                         .accept( MediaType.APPLICATION_JSON )
                                         .get();

        if ( response.getStatus() != 200 ) {
            System.out.println( "Failed...trying again in 5 seconds" );
            System.out.println( response.readEntity( String.class ) );
        }

        return response.readEntity( Integer.class );
    }

    public int getAmbientTemperature( String token ) {
        System.out.println( "Getting the ambient temperature" );
        Response response = ClientBuilder.newClient()
                                         .target( "https://developer-api.nest.com" )
                                         .path( "/devices/thermostats/" + THERMOSTAT + "/ambient_temperature_f" )
                                         .queryParam( "auth", token )
                                         .request()
                                         .accept( MediaType.APPLICATION_JSON )
                                         .get();

        if ( response.getStatus() != 200 ) {
            System.out.println( "Failed...trying again in 5 seconds" );
            System.out.println( response.readEntity( String.class ) );
        }

        return response.readEntity( Integer.class );
    }

    public void setThermostat( String token, String state ) {
        System.out.println( "Setting the thermostat to: " + state );
        int temperature;
        switch ( state.toLowerCase() ) {
            case "on":
                temperature = getAmbientTemperature( token );
                temperature -= 2;
                setTemperature( token, temperature );
                break;
            case "off":
                temperature = getAmbientTemperature( token );
                temperature += 5;
                setTemperature( token, temperature );
                break;
            case "up":
                temperature = getTargetTemperature( token );
                temperature += 2;
                setTemperature( token, temperature );
                break;
            case "down":
                temperature = getTargetTemperature( token );
                temperature -= 2;
                setTemperature( token, temperature );
                break;
            default:
                throw new IllegalArgumentException( "Unknown state" );
        }
    }
}
