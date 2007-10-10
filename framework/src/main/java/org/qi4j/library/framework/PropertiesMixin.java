/*
 * Copyright 2007 Rickard Öberg. All Rights Reserved.
 * Copyright 2007 Alin Dreghiciu. All Rights Reserved.
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
package org.qi4j.library.framework;

import java.beans.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.qi4j.api.annotation.AppliesTo;
import org.qi4j.api.annotation.AppliesToFilter;
import org.qi4j.api.annotation.scope.Property;

/**
 * Generic property mixin. Methods in interface
 * can be of the following types:
 * setFoo = set property named foo
 * getFoo = get property named foo
 * addFoo = add object to list named foo
 * removeFoo = remove object from list named foo
 * fooIterator - return an iterator over the list of Foos
 */
@AppliesTo( PropertiesMixin.AppliesTo.class )
public class PropertiesMixin
    implements InvocationHandler
{
    public static class AppliesTo
        implements AppliesToFilter
    {
        public boolean appliesTo( Method method, Class compositeType, Class mixin )
        {
            String name = method.getName();
            if( name.startsWith( "get" ) ||
                name.startsWith( "set" ) ||
                name.startsWith( "add" ) ||
                name.startsWith( "remove" ) )
            {
                return true;
            }

            return method.getAnnotation( Property.class ) != null;
        }
    }

    // Attributes ----------------------------------------------------
    Map<String, Object> properties = new HashMap<String, Object>();

    // InvocationHandler implementation ------------------------------
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
    {
        String methodName = method.getName();
        if( methodName.startsWith( "set" ) )
        {
            // Setter
            String name = "v:" + Introspector.decapitalize( methodName.substring( 3 ) );
            Object value = args[ 0 ];
            if( value == null )
            {
                properties.remove( name );
            }
            else
            {
                properties.put( name, value );
            }
            return null;
        }
        else if( methodName.startsWith( "get" ) )
        {
            // Getter
            String name = "v:" + Introspector.decapitalize( methodName.substring( 3 ) );
            return properties.get( name );
        }
        else if( methodName.startsWith( "add" ) )
        {
            // Add to list
            String name = "l:" + Introspector.decapitalize( methodName.substring( 3 ) );
            ArrayList<Object> list = (ArrayList<Object>) properties.get( name );
            if( list == null )
            {
                list = new ArrayList<Object>();
                properties.put( name, list );
            }
            list.add( args[ 0 ] );
        }
        else if( methodName.startsWith( "remove" ) )
        {
            // Remove from list
            String name = "l:" + Introspector.decapitalize( methodName.substring( 6 ) );
            ArrayList<Object> list = (ArrayList<Object>) properties.get( name );
            if( list != null )
            {
                list.remove( args[ 0 ] );
                if( list.size() == 0 )
                {
                    properties.remove( name );
                }
            }
        }
        else if( methodName.endsWith( "Iterator" ) || "iterator".equals( methodName ) )
        {
            String name = "l:" + Introspector.decapitalize( methodName.substring( 0, methodName.length() - 8 ) );
            ArrayList<Object> list = (ArrayList<Object>) properties.get( name );
            if( list != null )
            {
                return list.iterator();
            }
        }
        return null;
    }

}