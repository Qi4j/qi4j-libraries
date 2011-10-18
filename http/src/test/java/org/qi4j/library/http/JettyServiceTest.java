/**
 * Copyright (c) 2008, Edward Yakop. All Rights Reserved.
 * Copyright (c) 2011, Paul Merlin. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.library.http;

import java.util.Iterator;
import static javax.servlet.DispatcherType.REQUEST;
import org.apache.http.client.methods.HttpGet;
import static org.junit.Assert.*;
import org.junit.Test;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import static org.qi4j.library.http.Servlets.*;

public final class JettyServiceTest
        extends AbstractJettyTest
{

    public final void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        module.services( MemoryEntityStoreService.class );
        new JettyServiceAssembler().assemble( module );

        module.forMixin( JettyConfiguration.class ).declareDefaults().port().set( HTTP_PORT );

        // Hello world servlet related assembly
        addServlets( serve( "/helloWorld" ).with( HelloWorldServletService.class ) ).to( module );
        addFilters( filter( "/*" ).through( UnitOfWorkFilterService.class ).on( REQUEST ) ).to( module );
    }

    @Test
    public final void testInstantiation()
            throws Throwable
    {
        Iterable<ServiceReference<JettyService>> services = module.findServices( JettyService.class );
        assertNotNull( services );

        Iterator<ServiceReference<JettyService>> iterator = services.iterator();
        assertTrue( iterator.hasNext() );

        ServiceReference<JettyService> serviceRef = iterator.next();
        assertNotNull( serviceRef );

        JettyService jettyService = serviceRef.get();
        assertNotNull( jettyService );

        String output = defaultHttpClient.execute( new HttpGet( "http://localhost:8041/helloWorld" ), stringResponseHandler );
        assertEquals( "Hello World", output );
    }

}
