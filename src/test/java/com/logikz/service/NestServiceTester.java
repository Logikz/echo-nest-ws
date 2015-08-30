package com.logikz.service;

import com.logikz.dao.NestDAO;
import com.logikz.dao.PostgresDAO;
import com.logikz.utils.PostgresConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by Nick on 8/30/2015.
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( PostgresConnection.class )
public class NestServiceTester {

    @Mock
    PostgresDAO postgresDAO;

    @Mock
    NestDAO nestDAO;

    @Mock
    Connection connection;

    @Test
    public void setTemperatureTest() throws Exception {
        NestService nestService = new NestService( postgresDAO, nestDAO );
        mockStatic( PostgresConnection.class );

        when( PostgresConnection.getConnection() ).thenReturn( connection );
        when( postgresDAO.getToken( "stateid", connection ) ).thenReturn( "valid" );
        when( nestDAO.setTemperature( "valid", 72 ) ).thenReturn( Response.ok().build() );

        Response response = nestService.setTemperature( "stateid", 72 );
        assertEquals( response.getStatus(), 200 );
        verify( postgresDAO ).getToken( "stateid", connection );
        verify( nestDAO ).setTemperature( "valid", 72 );
    }

    @Test
    public void setTemperatureNotFoundTest() throws Exception {
        NestService nestService = new NestService( postgresDAO, nestDAO );
        mockStatic( PostgresConnection.class );

        when( PostgresConnection.getConnection() ).thenReturn( connection );
        when( postgresDAO.getToken( "stateid", connection ) ).thenReturn( null );

        Response response = nestService.setTemperature( "stateid", 72 );
        assertEquals( response.getStatus(), 404 );
        verify( postgresDAO ).getToken( "stateid", connection );
    }

    @Test
    public void setTemperatureExceptionTest() throws Exception {
        NestService nestService = new NestService( postgresDAO, nestDAO );
        mockStatic( PostgresConnection.class );

        when( PostgresConnection.getConnection() ).thenReturn( connection );
        when( postgresDAO.getToken( "stateid", connection ) ).thenThrow( new SQLException( "sql error" ) );

        Response response = nestService.setTemperature( "stateid", 72 );
        assertEquals( response.getStatus(), 500 );
        verify( postgresDAO ).getToken( "stateid", connection );
    }
}
