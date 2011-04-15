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

package org.qi4j.library.rest;

import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.index.rdf.RdfIndexingEngineService;
import org.qi4j.library.rdf.repository.MemoryRepositoryService;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;

import static org.qi4j.api.common.Visibility.*;

/**
 * JAVADOC
 */
public class DomainAssembler
    implements Assembler
{
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.services(
            MemoryEntityStoreService.class,
            UuidIdentityGeneratorService.class,
            RdfIndexingEngineService.class
        ).visibleIn( application );
        module.services( MemoryRepositoryService.class ).identifiedBy( "rdf-indexing" ).visibleIn( layer );

        module.entities( TestEntity.class, TestRole.class, TestEntity2.class ).visibleIn( application );
        module.values( TestValue.class ).visibleIn( application );
        module.services( DummyDataService.class ).instantiateOnStartup();
    }
}
