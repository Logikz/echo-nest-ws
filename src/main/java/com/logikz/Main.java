package com.logikz;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by Nick on 6/13/2015.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("--- ECHO NEST BRIDGE COMING ALIVE ---");
        Server server = new Server(80);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "com.logikz.api");

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
