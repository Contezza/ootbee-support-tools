/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 *
 * This file is part of code forked from the alfresco-jscript-extensions project
 * by Jens Goldhammer, which was licensed under the Apache License, Version 2.0.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
package org.orderofthebee.addons.support.tools.repo.jscript.jobs;

import org.quartz.Scheduler;

import java.util.Date;

/**
 * Class representing a job which can be used to trigger a new run, check if the
 * job is running and
 * cancel a running job (when the job class supports it).
 *
 * Works with Quartz 1.x (ACS 5) and Quartz 2.x (ACS 6+) via {@link QuartzBridge}.
 *
 * @author jgoldhammer
 * @author Order of the Bee
 */
public class ScriptJob
{

    public final String jobName;
    public final String groupName;
    public final Scheduler scheduler;
    public final Date previousFireTime;
    public final Date nextFireTime;
    public final String calendarName;
    public final String triggerName;
    public final String triggerGroup;
    public String cronExpression;

    public ScriptJob(String jobName, String jobGroupName, Scheduler scheduler, Date previousFireTime,
                     Date nextFireTime, String calendarName, String triggerName, String triggerGroup)
    {
        this.jobName = jobName;
        this.groupName = jobGroupName;
        this.scheduler = scheduler;
        this.previousFireTime = previousFireTime;
        this.nextFireTime = nextFireTime;
        this.calendarName = calendarName;
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
    }

    /**
     * Starts to trigger the job via quartz runtime which will start the job.
     */
    public void runNow()
    {
        QuartzBridge.triggerJob(scheduler, this.jobName, this.groupName);
    }

    /**
     * Checks if the current job is running.
     *
     * @return true if running, false if not.
     */
    public boolean isRunning()
    {
        return QuartzBridge.isRunning(scheduler, this.jobName, this.groupName);
    }

    @Override
    public String toString()
    {
        return "ScriptJob{" +
               "jobName='" + jobName + '\'' +
               ", groupName='" + groupName + '\'' +
               ", previousFireTime=" + previousFireTime +
               ", nextFireTime=" + nextFireTime +
               ", calendarName='" + calendarName + '\'' +
               ", triggerName='" + triggerName + '\'' +
               ", triggerGroup='" + triggerGroup + '\'' +
               ", cronExpression='" + cronExpression + '\'' +
               '}';
    }

    /**
     * Unschedule/cancel the job trigger.
     */
    public void cancelRun()
    {
        QuartzBridge.unscheduleJob(scheduler, this.triggerName, this.triggerGroup);
    }

    /**
     * Pause the job.
     */
    public void pauseJob()
    {
        QuartzBridge.pauseJob(scheduler, this.jobName, this.groupName);
    }

    /**
     * Resume a paused job.
     */
    public void resumeJob()
    {
        QuartzBridge.resumeJob(scheduler, this.jobName, this.groupName);
    }

    /**
     * Delete the job from the scheduler.
     */
    public void deleteJob()
    {
        QuartzBridge.deleteJob(scheduler, this.jobName, this.groupName);
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }
}
