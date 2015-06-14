package com.logikz.api;


import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


/**
 * Created by Nick on 6/13/2015.
 */
@Path("/nest")
public class NestResources {

    @POST
    @Path("/temperature/{temperature}")
    public Response ping(@PathParam("temperature") int temperature) {
        System.out.println("Temperature: " + temperature);
        return Response.ok().build();
    }
}
