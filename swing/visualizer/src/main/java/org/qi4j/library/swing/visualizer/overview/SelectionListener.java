/*  Copyright 2008 Edward Yakop.
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
package org.qi4j.library.swing.visualizer.overview;

import org.qi4j.library.swing.visualizer.overview.descriptor.CompositeDetailDescriptor;
import org.qi4j.library.swing.visualizer.overview.descriptor.EntityDetailDescriptor;
import org.qi4j.spi.structure.ApplicationDescriptor;
import org.qi4j.spi.structure.LayerDescriptor;
import org.qi4j.spi.structure.ModuleDescriptor;
import org.qi4j.spi.object.ObjectDescriptor;
import org.qi4j.service.ServiceDescriptor;

/**
 * @author edward.yakop@gmail.com
 * @since 0.5
 */
public interface SelectionListener
{
    /**
     * Invoked when an application node is selected.
     *
     * @param aDescriptor The selected application descriptor. This argument must not be {@code null}.
     * @since 0.5
     */
    void onApplicationSelected( ApplicationDescriptor aDescriptor );

    /**
     * Invoked when a layer node is selected.
     *
     * @param aDescriptor The selected layer descriptor. This argument must not be {@code null}.
     * @since 0.5
     */
    void onLayerSelected( LayerDescriptor aDescriptor );

    /**
     * Invoked when a module node is selected.
     *
     * @param aDescriptor The selected module descriptor. This argument must not be {@code null}.
     * @since 0.5
     */
    void onModuleSelected( ModuleDescriptor aDescriptor );

    /**
     * Invoked when a composite node is selected.
     *
     * @param aDescriptor The selected composite descriptor. This argument must not be {@code null}.
     * @since 0.5
     */
    void onCompositeSelected( CompositeDetailDescriptor aDescriptor );

    /**
     * Invoked when a composite node is selected.
     *
     * @param aDescriptor The selected entity descriptor. This argument must not be {@code null}.
     * @since 0.5
     */
    void onEntitySelected( EntityDetailDescriptor aDescriptor );

    /**
     * Invoked when a service node is selected.
     *
     * @param aDescriptor The selected service descriptor. This argument must not be {@code null}.
     * @since 0.5
     */
    void onServiceSelected( ServiceDescriptor aDescriptor );

    /**
     * Invoked when an object node is selected.
     *
     * @param aDescriptor The selected object descriptor. This argument must not be {@code null}.
     * @since 0.5
     */
    void onObjectSelected( ObjectDescriptor aDescriptor );
}