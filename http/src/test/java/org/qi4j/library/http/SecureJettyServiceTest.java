/*
 * Copyright (c) 2011, Paul Merlin. All Rights Reserved.
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

import java.io.IOException;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.methods.HttpGet;

import static org.junit.Assert.*;
import org.junit.Test;

import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import static org.qi4j.library.http.Servlets.*;

public class SecureJettyServiceTest
        extends AbstractSecureJettyTest
{

    public void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        module.services( MemoryEntityStoreService.class );
        new SecureJettyServiceAssembler().assemble( module );

        SecureJettyConfiguration config = module.forMixin( SecureJettyConfiguration.class ).declareDefaults();
        config.port().set( HTTPS_PORT );
        config.keystorePath().set( "src/test/resources/org/qi4j/library/http/qi4j-lib-http-unittests-server-cert.p12" );
        config.keystoreType().set( "PKCS12" );
        config.keystorePassword().set( "changeit" );

        addServlets( serve( "/hello" ).with( HelloWorldServletService.class ) ).to( module );
    }

    @Test
    // This test exists for demonstration purpose only, it do not test usefull things but it's on purpose
    public void testNoSSL()
            throws IOException
    {
        try {
            HttpGet get = new HttpGet( "http://localhost:8441/hello" );
            defaultHttpClient.execute( get );
            fail( "We could reach the HTTPS connector using a HTTP url, that's no good" );
        } catch ( NoHttpResponseException ex ) {
            // Expected
        }
    }

    @Test
    // This test exists for demonstration purpose only, it do not test usefull things but it's on purpose
    public void testNoTruststore()
            throws IOException
    {
        try {
            defaultHttpClient.execute( new HttpGet( "https://localhost:8441/hello" ) );
            fail( "We could reach the HTTPS connector without proper truststore, this should not happen" );
        } catch ( SSLPeerUnverifiedException ex ) {
            // Expected
        }
    }

    @Test
    public void testTrust()
            throws IOException, InterruptedException
    {
        String output = trustHttpClient.execute( new HttpGet( "https://localhost:8441/hello" ), stringResponseHandler );
        assertEquals( "Hello World", output );
    }

}
