package com.logikz;

import com.google.common.io.Resources;
import com.logikz.api.NestResources;
import com.logikz.utils.SwaggerDocsServlet;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.URLResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;


/**
 * Created by Nick on 6/13/2015.
 */
public class Main {
    public static final Logger LOG = LoggerFactory.getLogger( Main.class );

    public static void main( String[] args ) {
        LOG.info( "--- ECHO NEST BRIDGE COMING ALIVE ---" );
        try {
            // Workaround for resources from JAR files
            Resource.setDefaultUseCaches( false );

            Server server = new Server( Integer.valueOf( System.getenv( "PORT" ) ) );
            final HandlerList handlers = new HandlerList();

            handlers.addHandler( getRestHandler() );
            handlers.addHandler( getSwaggerHandler() );

            server.setHandler( handlers );

            initSwagger();

            LOG.trace( "Server starting" );
            server.start();
            LOG.trace( "Server started" );
        } catch ( Exception e ) {
            LOG.error( e.getMessage(), e );
        }
    }

    private static Handler getSwaggerHandler() throws URISyntaxException {
        // Create servlet for the UI
        URL swaggerUi = Resources.getResource( Main.class, "webapp" );
        Resource urlResource = URLResource.newResource( swaggerUi );

        ResourceHandler swaggerUIHandler = new ResourceHandler();
        swaggerUIHandler.setDirectoriesListed( true );
        swaggerUIHandler.setBaseResource( urlResource );
        swaggerUIHandler.setWelcomeFiles( new String[]{ "index.html" } );

        ContextHandler context = new ContextHandler("/swagger");
        context.setHandler( swaggerUIHandler );


        return context;
    }

    private static Handler getRestHandler() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages( NestResources.class.getPackage().getName(), ApiListingResource.class.getPackage()
                                                                                                     .getName() );

        ServletContainer container = new ServletContainer( resourceConfig );
        ServletHolder holder = new ServletHolder( container );
        ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );

        context.setContextPath( "/" );
        context.addServlet( holder, "/api/v1/*" );

        // Open CORS filter
        FilterHolder filter = new FilterHolder();
        filter.setInitParameter("allowedOrigins", "*");
        filter.setInitParameter("allowedMethods", "POST,GET,OPTIONS,PUT,DELETE,HEAD");
        filter.setInitParameter("allowedHeaders", "*");
        filter.setInitParameter("allowCredentials", "true");
        filter.setFilter( new CrossOriginFilter() );
        context.addFilter( filter, "/*", EnumSet.of( DispatcherType.ASYNC, DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.INCLUDE ) );

        //auto load swagger ui
        ServletHolder pathHolder = new ServletHolder(new SwaggerDocsServlet("/api/v1"));
        context.addServlet( pathHolder, SwaggerDocsServlet.PATH_SERVLET_ENDPOINT );

        return context;
    }

    private static void initSwagger() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion( "1.0.0" );
        beanConfig.setSchemes( new String[]{ "http" } );
        beanConfig.setHost( System.getenv( "SWAGGER_HOST" ) );
        beanConfig.setBasePath( "/api/v1/" );
        beanConfig.setResourcePackage( "com.logikz.api" );
        beanConfig.setScan( true );
    }
}

