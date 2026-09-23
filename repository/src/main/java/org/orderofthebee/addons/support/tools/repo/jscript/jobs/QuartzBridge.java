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
 */
package org.orderofthebee.addons.support.tools.repo.jscript.jobs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Quartz 1.x (ACS 5) and Quartz 2.x (ACS 6+) calls without a compile-time dependency on either API.
 */
final class QuartzBridge
{

    private QuartzBridge()
    {
    }

    static final class ListedJob
    {
        final String jobName;
        final String groupName;
        final Date previousFireTime;
        final Date nextFireTime;
        final String calendarName;
        final String triggerName;
        final String triggerGroup;
        final String cronExpression;

        ListedJob(final String jobName, final String groupName, final Date previousFireTime, final Date nextFireTime,
                final String calendarName, final String triggerName, final String triggerGroup, final String cronExpression)
        {
            this.jobName = jobName;
            this.groupName = groupName;
            this.previousFireTime = previousFireTime;
            this.nextFireTime = nextFireTime;
            this.calendarName = calendarName;
            this.triggerName = triggerName;
            this.triggerGroup = triggerGroup;
            this.cronExpression = cronExpression;
        }
    }

    static boolean isQuartz2()
    {
        try
        {
            Class.forName("org.quartz.JobKey");
            return true;
        }
        catch (final ClassNotFoundException ex)
        {
            return false;
        }
    }

    static List<ListedJob> listJobs(final Object scheduler)
    {
        final List<ListedJob> jobs = new ArrayList<>();
        for (final Object group : asIterable(invoke(scheduler, "getJobGroupNames")))
        {
            final String groupName = String.valueOf(group);
            if (isQuartz2())
            {
                final Object matcher = invokeStatic("org.quartz.impl.matchers.GroupMatcher", "jobGroupEquals",
                        new Class<?>[] { String.class }, new Object[] { groupName });
                final Object jobKeys = invoke(scheduler, "getJobKeys", new Class<?>[] { matcher.getClass() }, new Object[] { matcher });
                for (final Object jobKey : asIterable(jobKeys))
                {
                    final String jobName = (String) invoke(jobKey, "getName");
                    addFirstTrigger(jobs, jobName, (String) invoke(jobKey, "getGroup"),
                            invoke(scheduler, "getTriggersOfJob", new Class<?>[] { jobKey.getClass() }, new Object[] { jobKey }));
                }
            }
            else
            {
                for (final Object jobNameObj : asIterable(invoke(scheduler, "getJobNames", new Class<?>[] { String.class },
                        new Object[] { groupName })))
                {
                    final String jobName = String.valueOf(jobNameObj);
                    addFirstTrigger(jobs, jobName, groupName, invoke(scheduler, "getTriggersOfJob",
                            new Class<?>[] { String.class, String.class }, new Object[] { jobName, groupName }));
                }
            }
        }
        return jobs;
    }

    static void triggerJob(final Object scheduler, final String jobName, final String groupName)
    {
        invokeJobKeyOrNames(scheduler, "triggerJob", jobName, groupName);
    }

    static void pauseJob(final Object scheduler, final String jobName, final String groupName)
    {
        invokeJobKeyOrNames(scheduler, "pauseJob", jobName, groupName);
    }

    static void resumeJob(final Object scheduler, final String jobName, final String groupName)
    {
        invokeJobKeyOrNames(scheduler, "resumeJob", jobName, groupName);
    }

    static void deleteJob(final Object scheduler, final String jobName, final String groupName)
    {
        invokeJobKeyOrNames(scheduler, "deleteJob", jobName, groupName);
    }

    static void unscheduleJob(final Object scheduler, final String triggerName, final String triggerGroup)
    {
        if (isQuartz2())
        {
            final Object key = invokeStatic("org.quartz.TriggerKey", "triggerKey", new Class<?>[] { String.class, String.class },
                    new Object[] { triggerName, triggerGroup });
            invoke(scheduler, "unscheduleJob", new Class<?>[] { key.getClass() }, new Object[] { key });
        }
        else
        {
            invoke(scheduler, "unscheduleJob", new Class<?>[] { String.class, String.class },
                    new Object[] { triggerName, triggerGroup });
        }
    }

    static boolean isRunning(final Object scheduler, final String jobName, final String groupName)
    {
        for (final Object context : asIterable(invoke(scheduler, "getCurrentlyExecutingJobs")))
        {
            final Object detail = invoke(context, "getJobDetail");
            final String runningName;
            final String runningGroup;
            if (isQuartz2())
            {
                final Object key = invoke(detail, "getKey");
                runningName = (String) invoke(key, "getName");
                runningGroup = (String) invoke(key, "getGroup");
            }
            else
            {
                runningName = (String) invoke(detail, "getName");
                runningGroup = (String) invoke(detail, "getGroup");
            }
            if (jobName.equals(runningName) && groupName.equals(runningGroup))
            {
                return true;
            }
        }
        return false;
    }

    static void scheduleCronJob(final Object scheduler, final String jobName, final String jobGroup, final String triggerName,
            final String triggerGroup, final String cronExpression, final Map<String, Object> jobData, final Class<?> jobClass)
    {
        final Object dataMap = newJobDataMap(jobData);
        if (isQuartz2())
        {
            Object jobBuilder = invokeStatic("org.quartz.JobBuilder", "newJob", new Class<?>[] { Class.class }, new Object[] { jobClass });
            jobBuilder = invoke(jobBuilder, "withIdentity", new Class<?>[] { String.class, String.class },
                    new Object[] { jobName, jobGroup });
            jobBuilder = invoke(jobBuilder, "usingJobData", new Class<?>[] { dataMap.getClass() }, new Object[] { dataMap });
            final Object jobDetail = invoke(jobBuilder, "build");

            final Object schedule = invokeStatic("org.quartz.CronScheduleBuilder", "cronSchedule", new Class<?>[] { String.class },
                    new Object[] { cronExpression });
            Object triggerBuilder = invokeStatic("org.quartz.TriggerBuilder", "newTrigger", new Class<?>[0], new Object[0]);
            triggerBuilder = invoke(triggerBuilder, "withIdentity", new Class<?>[] { String.class, String.class },
                    new Object[] { triggerName, triggerGroup });
            triggerBuilder = invoke(triggerBuilder, "withSchedule", new Class<?>[] { schedule.getClass() }, new Object[] { schedule });
            triggerBuilder = invoke(triggerBuilder, "forJob", new Class<?>[] { String.class, String.class },
                    new Object[] { jobName, jobGroup });
            final Object trigger = invoke(triggerBuilder, "build");
            invoke(scheduler, "scheduleJob", new Class<?>[] { jobDetail.getClass(), trigger.getClass() },
                    new Object[] { jobDetail, trigger });
        }
        else
        {
            final Object jobDetail;
            try
            {
                jobDetail = Class.forName("org.quartz.JobDetail").getConstructor(String.class, String.class, Class.class)
                        .newInstance(jobName, jobGroup, jobClass);
            }
            catch (final ReflectiveOperationException ex)
            {
                throw new AlfrescoRuntimeException("Cannot build Quartz 1 job " + jobName, ex);
            }
            invoke(jobDetail, "setJobDataMap", new Class<?>[] { dataMap.getClass() }, new Object[] { dataMap });
            final Object trigger;
            try
            {
                trigger = Class.forName("org.quartz.CronTrigger")
                        .getConstructor(String.class, String.class, String.class, String.class, String.class)
                        .newInstance(triggerName, triggerGroup, jobName, jobGroup, cronExpression);
            }
            catch (final ReflectiveOperationException ex)
            {
                throw new AlfrescoRuntimeException("Cannot build Quartz 1 trigger for " + jobName, ex);
            }
            invoke(scheduler, "scheduleJob", new Class<?>[] { jobDetail.getClass(), trigger.getClass() },
                    new Object[] { jobDetail, trigger });
        }
    }

    private static void addFirstTrigger(final List<ListedJob> jobs, final String jobName, final String groupName,
            final Object triggers)
    {
        for (final Object trigger : asIterable(triggers))
        {
            final String triggerName;
            final String triggerGroup;
            if (isQuartz2())
            {
                final Object key = invoke(trigger, "getKey");
                triggerName = (String) invoke(key, "getName");
                triggerGroup = (String) invoke(key, "getGroup");
            }
            else
            {
                triggerName = (String) invoke(trigger, "getName");
                triggerGroup = (String) invoke(trigger, "getGroup");
            }
            String cron = null;
            if (trigger.getClass().getName().endsWith("CronTrigger") || hasMethod(trigger, "getCronExpression"))
            {
                cron = (String) invoke(trigger, "getCronExpression");
            }
            jobs.add(new ListedJob(jobName, groupName, (Date) invoke(trigger, "getPreviousFireTime"),
                    (Date) invoke(trigger, "getNextFireTime"), (String) invoke(trigger, "getCalendarName"), triggerName, triggerGroup, cron));
            return;
        }
    }

    private static void invokeJobKeyOrNames(final Object scheduler, final String methodName, final String name, final String group)
    {
        if (isQuartz2())
        {
            final Object key = invokeStatic("org.quartz.JobKey", "jobKey", new Class<?>[] { String.class, String.class },
                    new Object[] { name, group });
            invoke(scheduler, methodName, new Class<?>[] { key.getClass() }, new Object[] { key });
        }
        else
        {
            invoke(scheduler, methodName, new Class<?>[] { String.class, String.class }, new Object[] { name, group });
        }
    }

    private static Object newJobDataMap(final Map<String, Object> jobData)
    {
        final Object dataMap;
        try
        {
            dataMap = Class.forName("org.quartz.JobDataMap").getDeclaredConstructor().newInstance();
        }
        catch (final ReflectiveOperationException ex)
        {
            throw new AlfrescoRuntimeException("Cannot create Quartz job data", ex);
        }
        for (final Map.Entry<String, Object> entry : jobData.entrySet())
        {
            invoke(dataMap, "put", new Class<?>[] { Object.class, Object.class }, new Object[] { entry.getKey(), entry.getValue() });
        }
        return dataMap;
    }

    private static boolean hasMethod(final Object target, final String name)
    {
        for (final Method method : target.getClass().getMethods())
        {
            if (method.getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    private static Iterable<Object> asIterable(final Object value)
    {
        if (value == null)
        {
            return Collections.emptyList();
        }
        if (value instanceof Object[])
        {
            return Arrays.asList((Object[]) value);
        }
        if (value instanceof Collection)
        {
            @SuppressWarnings("unchecked")
            final Collection<Object> collection = (Collection<Object>) value;
            return collection;
        }
        return Collections.singletonList(value);
    }

    private static Object invokeStatic(final String className, final String methodName, final Class<?>[] types, final Object[] args)
    {
        try
        {
            final Class<?> cls = Class.forName(className);
            return cls.getMethod(methodName, types).invoke(null, args);
        }
        catch (final InvocationTargetException ex)
        {
            final Throwable cause = ex.getCause() == null ? ex : ex.getCause();
            if (cause instanceof RuntimeException)
            {
                throw (RuntimeException) cause;
            }
            throw new AlfrescoRuntimeException("Cannot call " + className + "." + methodName, cause);
        }
        catch (final ReflectiveOperationException ex)
        {
            throw new AlfrescoRuntimeException("Cannot call " + className + "." + methodName, ex);
        }
    }

    private static Object invoke(final Object target, final String methodName)
    {
        return invoke(target, methodName, new Class<?>[0], new Object[0]);
    }

    private static Object invoke(final Object target, final String methodName, final Class<?>[] types, final Object[] args)
    {
        try
        {
            final Method method = findMethod(target.getClass(), methodName, types);
            return method.invoke(target, args);
        }
        catch (final InvocationTargetException ex)
        {
            final Throwable cause = ex.getCause() == null ? ex : ex.getCause();
            if (cause instanceof RuntimeException)
            {
                throw (RuntimeException) cause;
            }
            throw new AlfrescoRuntimeException("Cannot call " + target.getClass().getName() + "." + methodName, cause);
        }
        catch (final ReflectiveOperationException ex)
        {
            throw new AlfrescoRuntimeException("Cannot call " + target.getClass().getName() + "." + methodName, ex);
        }
    }

    private static Method findMethod(final Class<?> cls, final String methodName, final Class<?>[] types)
            throws NoSuchMethodException
    {
        try
        {
            return cls.getMethod(methodName, types);
        }
        catch (final NoSuchMethodException ex)
        {
            for (final Method method : cls.getMethods())
            {
                if (!method.getName().equals(methodName) || method.getParameterTypes().length != types.length)
                {
                    continue;
                }
                boolean match = true;
                for (int i = 0; i < types.length; i++)
                {
                    if (!method.getParameterTypes()[i].isAssignableFrom(types[i]))
                    {
                        match = false;
                        break;
                    }
                }
                if (match)
                {
                    return method;
                }
            }
            throw ex;
        }
    }
}
