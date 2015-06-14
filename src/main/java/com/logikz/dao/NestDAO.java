package com.logikz.dao;

import com.logikz.model.NestToken;
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


    public Response setTemperature(String token, int temperature) {
        return null;
    }

    public void doNestAuthorization(String stateId) {
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation("https://home.nest.com/login/oauth2")
                    .setClientId(CLIENT_ID)
                    .setState(stateId)
                    .setRedirectURI("https://nest-echo.herokuapp.com/" + stateId + "/auth/callback")
                    .buildQueryMessage();
            String locationUri = request.getLocationUri();
            System.out.println("GET: " + locationUri);

            String entity = ClientBuilder.newClient().target(locationUri).request().get(String.class);

            System.out.println("ENTITY: " + entity);
        } catch (OAuthSystemException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getToken(String code) throws OAuthSystemException {
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation("https://api.home.nest.com/oauth2/access_token")
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
                .setCode(code)
                .buildQueryMessage();

        String locationUri = request.getLocationUri();
        System.out.println("POST: " + locationUri);

        Response response = ClientBuilder.newClient().target(locationUri).request().post(Entity.entity("", MediaType.TEXT_PLAIN_TYPE));

        if(response.getStatus() > 300){
            System.out.println(response.readEntity(String.class));
            return null;
        } else {
            NestToken token = response.readEntity(NestToken.class);

            System.out.println("Token: " + token.getAccess_token());

            return token.getAccess_token();
        }
    }
}
