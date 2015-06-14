package com.logikz.api;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


/**
 * Created by Nick on 6/13/2015.
 */

@Path("/nest")
public class NestResources {

    @GET
    public Response ping() {
        System.out.println("PING");
        return Response.ok().build();
    }
}
