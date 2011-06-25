/*
 * Copyright 2005-2011 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.library.alarm;

import java.util.List;
import org.junit.Test;
import org.qi4j.api.constraint.ConstraintViolationException;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.value.ValueBuilder;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.core.testsupport.AbstractQi4jTest;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;

import static org.junit.Assert.*;

public class AlarmServiceTest
    extends AbstractQi4jTest
{

    @Override
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.services( TestAlarmModel1.class ).setMetaInfo( new AlarmModelDescriptor("Simple", false) );
        module.services( TestAlarmModel2.class ).setMetaInfo( new AlarmModelDescriptor("Standard", true) );
        module.services( AlarmSystemService.class );
        module.entities( AlarmEntity.class );
        module.values( AlarmStatus.class );
        module.values( AlarmCategory.class );
        module.values( AlarmEvent.class );
        module.services( MemoryEntityStoreService.class );
        module.services( UuidIdentityGeneratorService.class );
    }

    @Mixins( SimpleAlarmModelService.SimpleAlarmModelMixin.class )
    public interface TestAlarmModel1
        extends AlarmModel, ServiceComposite
    {
    }

    @Mixins( StandardAlarmModelService.StandardAlarmModelMixin.class )
    public interface TestAlarmModel2
        extends AlarmModel, ServiceComposite
    {
    }

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        unitOfWorkFactory.newUnitOfWork();
    }

    @Override
    public void tearDown()
        throws Exception
    {
        UnitOfWork uow = unitOfWorkFactory.currentUnitOfWork();
        if( uow != null )
        {
            uow.discard();
        }
        super.tearDown();
    }

    @Test
    public void testGetAlarmModels()
    {
        AlarmSystem alarmService = (AlarmSystem) serviceLocator.findService( AlarmSystem.class ).get();
        List<AlarmModelDescriptor> models = alarmService.alarmModels();
        assertNotNull( models );
        assertEquals( 2, models.size() );
    }

    @Test
    public void testDefaultModel()
        throws Exception
    {
        AlarmSystem alarmService = (AlarmSystem) serviceLocator.findService( AlarmSystem.class ).get();
        List<AlarmModelDescriptor> models = alarmService.alarmModels();
        assertNotNull( models );
        assertEquals( 2, models.size() );

        AlarmModel model = alarmService.defaultAlarmModel();
        assertNotNull( model );
    }

    @Test
    public void testListeners()
        throws Exception
    {
        AlarmSystem alarmService = (AlarmSystem) serviceLocator.findService( AlarmSystem.class ).get();
        Alarm alarm = alarmService.createAlarm( "TestAlarm", createCategory("AlarmServiceTest") );

        CountingListener listener1 = new CountingListener();
        ExceptionThrowingListener listener2 = new ExceptionThrowingListener();
        ErrorThrowingListener listener3 = new ErrorThrowingListener();
        alarmService.addAlarmListener( listener1 );
        alarmService.addAlarmListener( listener3 );
        alarmService.addAlarmListener( listener1 );
        try
        {
            alarm.activate();
            fail( "InternalError was expected to be thrown." );
        }
        catch( InternalError e )
        {
            // Expected. If an Error is thrown it should not be captured anywhere.
        }
        assertEquals( 1, listener1.getCounter() );      // One time, because the second listener would not be called.

        alarmService.removeAlarmListener( listener3 );
        alarmService.removeAlarmListener( listener1 );
        alarmService.addAlarmListener( listener2 );
        alarmService.addAlarmListener( listener1 );   // We should now have, 'counting', 'exception', 'counting' in the list.
        alarm.deactivate();   // No Exception should be thrown. The fireAlarm() should swallow it and ensure that
        // all listeners are called and then return.
        assertEquals( 3, listener1.getCounter() );

        List listeners = alarmService.alarmListeners();
        assertEquals( "Listeners registered.", 3, listeners.size() );

        alarmService.removeAlarmListener( listener1 );
        listeners = alarmService.alarmListeners();
        assertEquals( "Listeners registered.", 2, listeners.size() );

        alarmService.removeAlarmListener( listener1 );
        listeners = alarmService.alarmListeners();
        assertEquals( "Listeners registered.", 1, listeners.size() );

        alarmService.removeAlarmListener( listener1 );
        listeners = alarmService.alarmListeners();
        assertEquals( "Listeners registered.", 1, listeners.size() );

        alarmService.removeAlarmListener( listener2 );
        listeners = alarmService.alarmListeners();
        System.out.println( listeners );
        assertEquals( "Listeners registered.", 0, listeners.size() );

        alarmService.removeAlarmListener( listener2 );
        listeners = alarmService.alarmListeners();
        assertEquals( "Listeners registered.", 0, listeners.size() );

        try
        {
            alarmService.removeAlarmListener( null );
            fail( "Should not be able to call removeAlarmListener with null argument." );
        }
        catch( ConstraintViolationException e )
        {
            // expected
        }
        listeners = alarmService.alarmListeners();
        assertEquals( "Listeners registered.", 0, listeners.size() );
    }

    private AlarmCategory createCategory( String name )
    {
        ValueBuilder<AlarmCategory> builder = valueBuilderFactory.newValueBuilder( AlarmCategory.class );
        builder.prototype().name().set( name );
        return builder.newInstance();
    }



    private class CountingListener
        implements AlarmListener
    {

        private int m_Counter;

        public CountingListener()
        {
            m_Counter = 0;
        }

        public void alarmFired( AlarmEvent event )
        {
            m_Counter = getCounter() + 1;
        }

        public int getCounter()
        {
            return m_Counter;
        }
    }

    private class ExceptionThrowingListener
        implements AlarmListener
    {

        public void alarmFired( AlarmEvent event )
            throws IllegalArgumentException
        {
            throw new IllegalArgumentException( "This is an intentional Exception, and it is not a sign of a problem." );
        }
    }

    private class ErrorThrowingListener
        implements AlarmListener
    {

        public void alarmFired( AlarmEvent event )
        {
            throw new InternalError( "This is an intentional java.lang.Error." );
        }
    }
}
