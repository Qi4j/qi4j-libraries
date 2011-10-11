/*
 * Copyright (c) 2008, Rickard Öberg. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.library.http;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.injection.scope.Uses;
import org.qi4j.api.service.Activatable;
import org.qi4j.api.service.ServiceDescriptor;
import org.qi4j.api.service.ServiceReference;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * JAVADOC
 */
public class JettyMixin
    implements Activatable, HttpService
{
    private static final Integer DEFAULT_PORT = 8080;
    private Configuration<JettyConfiguration> configuration;

    @Service
    private Iterable<ServiceReference<Servlet>> servlets;

    @Service
    private Iterable<ServiceReference<Filter>> filters;

    private Server server;
    private ServletContextHandler root;

    public JettyMixin( @Uses ServiceDescriptor descriptor, @This Configuration<JettyConfiguration> configuration )
    {
        this.configuration = configuration;
        // Create a server given the host port
        Integer port = configuration.configuration().port().get();
        if( port == null )
        {
            port = DEFAULT_PORT;
        }
        server = new Server( port );

        // Sets the context root
        root = new ServletContextHandler( server, "/", true, false );

        // Sets the default servlet for default context
        root.addServlet( DefaultServlet.class, "/" );
    }

    private void addServlets( ServletContextHandler root, Iterable<ServiceReference<Servlet>> servlets )
    {
        // Iterate the available servlets and add it to the server
        for( ServiceReference<Servlet> servlet : servlets )
        {
            ServletInfo servletInfo = servlet.metaInfo( ServletInfo.class );
            String servletPath = servletInfo.getPath();

            Servlet servletInstance = servlet.get();
            ServletHolder holder = new ServletHolder( servletInstance );
            holder.setInitParameters( servletInfo.initParams() );
            root.addServlet( holder, servletPath );
        }
    }

    private void addFilters( ServletContextHandler root, Iterable<ServiceReference<Filter>> filters )
    {
        // Iterate the available filters and add it to the server
        for( ServiceReference<Filter> filter : filters )
        {
            FilterInfo filterInfo = filter.metaInfo( FilterInfo.class );
            String filterPath = filterInfo.getPath();

            Filter filterInstance = filter.get();
            FilterHolder holder = new FilterHolder( filterInstance );
            holder.setInitParameters( filterInfo.initParameters() );
            root.addFilter( holder, filterPath, filterInfo.dispatchers() );
        }
    }

    private String rootResourceBase( String resourcePath )
    {
        if( resourcePath.length() == 0 )
        {
            ProtectionDomain domain = getClass().getProtectionDomain();
            CodeSource source = domain.getCodeSource();
            URL location = source.getLocation();
            String basePath = location.getPath();
            File base = new File( basePath );
            return base.getAbsolutePath();
        }
        else
        {
            return resourcePath;
        }
    }

    public final void activate()
        throws Exception
    {
        // Sets the resource
        root.setResourceBase( rootResourceBase( configuration.configuration().resourcePath().get() ) );

        addServlets( root, servlets );
        addFilters( root, filters );

        Connector[] connectors = server.getConnectors();
        Connector connector = connectors[ 0 ];

        int hostPort = configuration.configuration().port().get();
        connector.setPort( hostPort );
        server.start();
    }

    public final void passivate()
        throws Exception
    {
        server.stop();
        for( Connector connector : server.getConnectors() )
        {
            connector.close();
        }
    }

    public Interface[] interfacesServed()
    {
        Connector[] connectors = server.getConnectors();
        Interface[] result = new Interface[connectors.length];
        int index = 0;
        for( Connector connector : connectors )
        {
            String host = this.configuration.configuration().hostName().get();
            if( host == null )
            {
                host = connector.getHost();
                if( host == null )  // If serving all interfaces.
                {
                    try
                    {
                        host = InetAddress.getLocalHost().getHostAddress();
                    }
                    catch( UnknownHostException e )
                    {
                        InternalError error = new InternalError( "UnknownHost for local interface." );
                        error.initCause( e );
                        throw error;
                    }
                }
            }
            result[ index++ ] = new InterfaceImpl( host, connector.getPort(), Interface.Protocol.http );

        }
        return result;
    }

    private static class InterfaceImpl
        implements Interface
    {
        private String host;
        private int port;
        private Protocol protocol;

        public InterfaceImpl( String host, int port, Protocol protocol )
        {
            this.host = host;
            this.port = port;
            //To change body of created methods use File | Settings | File Templates.
            this.protocol = protocol;
        }

        public String hostName()
        {
            return host;
        }

        public int port()
        {
            return port;
        }

        public Protocol protocol()
        {
            return protocol;
        }
    }
}