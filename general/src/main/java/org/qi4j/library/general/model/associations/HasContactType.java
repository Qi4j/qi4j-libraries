/*
 * Copyright (c) 2007, Sianny Halim. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.library.general.model.associations;

import java.io.Serializable;
import org.qi4j.api.annotation.ImplementedBy;
import org.qi4j.library.general.model.ContactType;
import org.qi4j.library.general.model.mixins.HasContactTypeMixin;

/**
 * Interface to describe association with {@link org.qi4j.library.general.model.ContactType}
 */
@ImplementedBy( { HasContactTypeMixin.class } )
public interface HasContactType<T extends ContactType> extends Serializable
{
    void setContactType( T aContactType );

    T getContactType();
}