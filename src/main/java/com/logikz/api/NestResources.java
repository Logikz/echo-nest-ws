package com.logikz.api;


import com.logikz.dao.NestDAO;
import com.logikz.dao.PostgresDAO;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.sql.SQLException;


/**
 * Created by Nick on 6/13/2015.
 */
@Path("/nest")
public class NestResources {

    @POST
    @Path("/{stateId}/temperature/{temperature}")
    public Response setTemperature(@PathParam("stateId") String stateId, @PathParam("temperature") int temperature) {
        System.out.println("Temperature: " + temperature);
        PostgresDAO dao = new PostgresDAO();
        NestDAO nestDAO = new NestDAO();
        try {
            String token = dao.getToken(stateId);
            if (token != null) {
                return nestDAO.setTemperature(token, temperature);
            }
        } catch (SQLException | URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        nestDAO.doNestAuthorization(stateId);

        return Response.ok().build();
    }

    @GET
    @Path("/{stateId}/auth")
    public Response authorize(@PathParam("stateId") String stateId) {
        NestDAO nestDAO = new NestDAO();
        nestDAO.doNestAuthorization(stateId);

        return Response.ok().build();
    }

    @POST
    @Path("{stateId}/auth/callback")
    public Response callback(@PathParam("stateId") String stateId, String body) {
        NestDAO nestDAO = new NestDAO();
        PostgresDAO postgresDAO = new PostgresDAO();

        try {
            postgresDAO.setToken(stateId, nestDAO.getToken(body));
        } catch (URISyntaxException | SQLException e) {
            System.out.println(e.getMessage());
        }

        return Response.ok().build();
    }
}
