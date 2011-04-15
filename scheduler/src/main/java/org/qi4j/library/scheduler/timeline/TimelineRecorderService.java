/*
 * Copyright (c) 2010, Paul Merlin. All Rights Reserved.
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
package org.qi4j.library.scheduler.timeline;

import static org.qi4j.library.scheduler.timeline.TimelineRecordStep.SUCCESS;
import static org.qi4j.library.scheduler.timeline.TimelineRecordStep.FAILURE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;

import org.qi4j.library.scheduler.SchedulerService;
import org.qi4j.library.scheduler.schedule.ScheduleRunner;
import org.qi4j.library.scheduler.task.Task;

/**
 * Used by {@link ScheduleRunner} to record {@link Task} runs.
 */
@Mixins( TimelineRecorderService.Mixin.class )
public interface TimelineRecorderService
        extends ServiceComposite
{

    /**
     * @param task  Successful Task
     * @return      TimelineRecord
     */
    TimelineRecord recordSuccess( Task task );

    /**
     * Record a {@link Task} failure.
     *
     * Use the cause stacktrace as record details.
     *
     * @param task  Failed Task
     * @param cause Failure cause
     * @return      TimelineRecord
     */
    TimelineRecord recordFailure( Task task, Throwable cause );

    abstract class Mixin
            implements TimelineRecorderService
    {

        @Structure
        private UnitOfWorkFactory uowf;
        @Service
        private SchedulerService scheduler;

        public TimelineRecord recordSuccess( Task task )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<TimelineRecordEntity> builder = uow.newEntityBuilder( TimelineRecordEntity.class );
            TimelineRecordEntity record = builder.instance();
            record.schedulerIdentity().set( scheduler.identity().get() );
            record.timestamp().set( System.currentTimeMillis() );
            record.step().set( SUCCESS );
            record.taskName().set( task.name().get() );
            record.taskTags().set( task.tags().get() );
            return builder.newInstance();
        }

        public TimelineRecord recordFailure( Task task, Throwable cause )
        {
            Writer result = new StringWriter();
            cause.printStackTrace( new PrintWriter( result ) );
            return recordFailure( task, result.toString() );
        }

        private TimelineRecord recordFailure( Task task, String details )
        {
            UnitOfWork uow = uowf.currentUnitOfWork();
            EntityBuilder<TimelineRecordEntity> builder = uow.newEntityBuilder( TimelineRecordEntity.class );
            TimelineRecordEntity record = builder.instance();
            record.schedulerIdentity().set( scheduler.identity().get() );
            record.timestamp().set( System.currentTimeMillis() );
            record.step().set( FAILURE );
            record.taskName().set( task.name().get() );
            record.taskTags().set( task.tags().get() );
            record.details().set( details );
            return builder.newInstance();
        }

    }

}
