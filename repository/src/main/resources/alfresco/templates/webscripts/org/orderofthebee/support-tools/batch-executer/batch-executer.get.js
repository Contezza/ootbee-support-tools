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
 */
/*
 * Ported from alfresco-js-batch-executer (com.contezza.batchexecuter).
 */

function formatJob(job)
{
    var props = [ 'id', 'name', 'batchSize', 'threads', 'disableRules', 'onNodeFunction', 'onBatchFunction', 'status' ];
    var output = {};
    for (var p = 0; p < props.length; p++)
    {
        var prop = props[p];
        var value = job[prop];
        if (value)
        {
            output[prop] = value;
        }
    }
    // expose id via getter in case job is a Java object
    if (!output.id && job.getId)
    {
        output.id = job.getId();
    }
    return output;
}

var currentJobs = batchExecuter.getCurrentJobs();
var jobArray = currentJobs.toArray();
var jobObject = [];

for (var i = 0; i < jobArray.length; i++)
{
    var job = jobArray[i];
    // Prefer server-side summary (contains total + completed)
    try
    {
        var summary = batchExecuter.getJobSummary(job.getId());
        if (summary && Object.keys(summary).length > 0)
        {
            jobObject.push(summary);
            continue;
        }
    }
    catch (e)
    {
        // ignore and fall back to client-side formatting
    }
    jobObject.push(formatJob(job));
}

model.jobs = jobObject;
model.stringifiedJobs = JSON.stringify(jobObject);
