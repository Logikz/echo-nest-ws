package com.logikz.dao;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * Created by Nick on 6/13/2015.
 */
public class NestDAO {
    public Response setTemperature(String token, int temperature) {
        return null;
    }

    public void doNestAuthorization(String stateId) {
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation("https://home.nest.com/login/oauth2")
                    .setClientId("2f2612c7-6d36-4c5c-8c19-2903bb7be430")
                    .setState(stateId)
                            //.setRedirectURI("https://nest-echo.herokuapp.com/" + stateId + "/auth/callback")
                    .buildQueryMessage();
            String locationUri = request.getLocationUri();
            System.out.println("GET: " + locationUri);

            Client client = ClientBuilder.newClient();
            String entity = client.target(locationUri).request().get(String.class);

            System.out.println("ENTITY: " + entity);
        } catch (OAuthSystemException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getToken(String body) {
        return null;
    }
}
