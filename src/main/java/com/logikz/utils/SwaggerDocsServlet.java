package com.logikz.utils;

import com.google.common.base.Strings;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ncuneo on 8/31/2015.
 */
public class SwaggerDocsServlet extends HttpServlet
{

    private final String path;
    public static final String PATH_SERVLET_ENDPOINT = "/get-endpoint";

    public SwaggerDocsServlet( String path )
    {
        this.path = Strings.nullToEmpty(path);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(path);
    }
}
