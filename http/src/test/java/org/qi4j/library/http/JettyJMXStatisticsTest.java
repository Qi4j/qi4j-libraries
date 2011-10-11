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

import org.junit.Test;

import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.test.AbstractQi4jTest;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.library.jmx.JMXAssembler;
import static org.qi4j.library.http.Servlets.*;

public class JettyJMXStatisticsTest
        extends AbstractQi4jTest
{

    public void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        module.services( MemoryEntityStoreService.class );
        new JettyServiceAssembler().assemble( module );
        new JMXAssembler().assemble( module );

        JettyConfiguration config = module.forMixin( JettyConfiguration.class ).declareDefaults();
        config.port().set( 8441 );
        config.statistics().set( Boolean.TRUE );

        // Hello world servlet related assembly
        addServlets( serve( "/hello" ).with( HelloWorldServletService.class ) ).to( module );
    }

    /**
     * Run this test with -Djmxtest make it to not return so you can connect to the JVM using a JMX client.
     */
    @Test
    public void dummy()
            throws InterruptedException
    {
        if ( !"false".equals( System.getProperty( "jmxtest", "false" ) ) ) {
            Thread.sleep( Long.MAX_VALUE );
        }
    }

}
