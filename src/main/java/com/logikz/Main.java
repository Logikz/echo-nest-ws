package com.logikz;

import com.logikz.api.NestResources;
import io.swagger.jaxrs.config.BeanConfig;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;


/**
 * Created by Nick on 6/13/2015.
 */
public class Main {
    public static final Logger LOG = LoggerFactory.getLogger( Main.class );

    public static void main( String[] args ) {
        LOG.info( "--- ECHO NEST BRIDGE COMING ALIVE ---" );
        try {
            Server server = new Server( Integer.valueOf( System.getenv( "PORT" ) ) );
            // Workaround for resources from JAR files
            Resource.setDefaultUseCaches( false );

            final HandlerList handlers = new HandlerList();

            handlers.addHandler( getResourceHandler() );
            handlers.addHandler( getSwaggerHandler() );

            server.setHandler( handlers );

            initSwagger();

            LOG.trace( "Server starting" );
            server.start();
            LOG.trace( "Server started" );
            server.join();
            LOG.trace( "Server joined" );
        } catch ( Exception e ) {
            LOG.error( e.getMessage(), e );
        }
    }

    private static Handler getSwaggerHandler() throws URISyntaxException {
        ResourceHandler swaggerResources = new ResourceHandler();
        swaggerResources.setResourceBase( Main.class.getClassLoader().getResource( "webapp" ).toURI()
                                                    .toString() );
        swaggerResources.setWelcomeFiles( new String[]{ "index.html" } );

        ServletContextHandler swagger = new ServletContextHandler( ServletContextHandler.SESSIONS );
        swagger.setContextPath( "/swagger/" );
        swagger.setHandler( swaggerResources );

        return swagger;
    }

    private static void initSwagger() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion( "1.0.0" );
        beanConfig.setSchemes( new String[]{ "http" } );
        beanConfig.setHost( "localhost" );
        beanConfig.setBasePath( "/api/v1/" );
        beanConfig.setResourcePackage( "com.logikz.api" );
        beanConfig.setScan( true );
    }

    private static Handler getResourceHandler() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages( NestResources.class.getPackage().getName(), "io.swagger.jaxrs.listing" );

        ServletContainer container = new ServletContainer( resourceConfig );
        ServletHolder holder = new ServletHolder( container );
        ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );

        context.setContextPath( "/" );
        context.addServlet( holder, "/api/v1/*" );

        return context;
    }
}
