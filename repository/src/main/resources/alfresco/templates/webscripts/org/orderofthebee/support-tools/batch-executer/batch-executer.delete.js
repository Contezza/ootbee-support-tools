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

function main()
{
    var jobId = url.templateArgs.jobId;
    if (!jobId)
    {
        status.code = 400;
        status.message = "jobId must be specified";
        status.redirect = true;
        return;
    }

    var canceled = batchExecuter.cancelJob(jobId);
    status.code = canceled ? 200 : 204;
    if (!canceled)
    {
        status.message = "Job " + jobId + " already finished or was canceled";
    }

    var location = "" + url.service;
    location = location.replace(/\/jobs.*/, '/jobs') + "?canceledJobId=" + jobId + "&canceled=" + canceled;
    status.location = location;

    model.jobId = jobId;
    model.canceled = canceled;
}

main();
