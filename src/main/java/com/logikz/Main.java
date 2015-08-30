package com.logikz;

import com.logikz.api.NestResources;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by Nick on 6/13/2015.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("--- ECHO NEST BRIDGE COMING ALIVE ---");
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        System.out.println("Context Path Created");

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/api/v1/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                NestResources.class.getCanonicalName());

        try {
            System.out.println("Server starting");
            server.start();
            System.out.println("Server started");
            server.join();
            System.out.println("Server joined");
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
