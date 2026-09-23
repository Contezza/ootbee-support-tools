<#--
Copyright (C) 2016 - 2025 Order of the Bee

This file is part of OOTBee Support Tools

OOTBee Support Tools is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

OOTBee Support Tools is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.

Linked to Alfresco
Copyright (C) 2005 - 2025 Alfresco Software Limited.
-->

<#include "../admin-template.ftl" />

<@page title=msg("batch-executer.title") readonly=true>

    <script type="text/javascript">//<![CDATA[
        function cancelBatchJob(jobId) {
            Admin.request({
                method: "DELETE",
                url: '${url.serviceContext?js_string}/ootbee/batch-executer/jobs/' + encodeURIComponent(jobId),
                responseContentType: "text/html",
                fnSuccess: function (response) {
                    var canceled = response.responseStatus === 200;
                    window.location = '${url.service?js_string}?canceledJobId=' + encodeURIComponent(jobId) + '&canceled=' + canceled;
                }
            });
        }
    //]]></script>

    <div class="column-full">
        <p class="intro">${msg("batch-executer.intro-text")?html}</p>

        <#if jobs?size == 0>
            <p>${msg("batch-executer.none")?html}</p>
        <#else>
            <table class="data">
                <thead>
                <tr>
                    <th>${msg("batch-executer.column.name")?html}</th>
                    <th>${msg("batch-executer.column.batchSize")?html}</th>
                    <th>${msg("batch-executer.column.threads")?html}</th>
                    <th>${msg("batch-executer.column.disabledRules")?html}</th>
                    <th>${msg("batch-executer.column.completed")?html}</th>
                    <th>${msg("batch-executer.column.total")?html}</th>
                    <th>${msg("batch-executer.column.nodeFunction")?html}</th>
                    <th>${msg("batch-executer.column.batchFunction")?html}</th>
                    <th>${msg("batch-executer.column.status")?html}</th>
                    <th>${msg("batch-executer.column.action")?html}</th>
                </tr>
                </thead>
                <tbody>
                <#list jobs as job>
                    <tr>
                        <td>${(job.name!"")?html}</td>
                        <td>${job.batchSize?c}</td>
                        <td>${job.threads?c}</td>
                        <td>${(job.disableRules!false)?string}</td>
                        <td>${(job.completed!0)?c}</td>
                        <td><#if (job.totalEstimatedWorkSize!-1) &gt;= 0>${job.totalEstimatedWorkSize?c}<#else>-</#if></td>
                        <td>${(job.onNodeFunction!"")?html}</td>
                        <td>${(job.onBatchFunction!"")?html}</td>
                        <#assign jobStatus = (job.status!"")?string/>
                        <td>${jobStatus?html}</td>
                        <td>
                            <#if jobStatus != "CANCELED" && jobStatus != "FINISHED">
                                <button type="button" onclick="cancelBatchJob('${job.id?js_string}');">${msg("batch-executer.cancel")?html}</button>
                            </#if>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </#if>

        <#if args.canceledJobId??>
            <p>
                ${msg("batch-executer.canceled-prefix")?html} ${args.canceledJobId?html}
                <#if args.canceled?? && args.canceled == "true">
                    ${msg("batch-executer.canceled")?html}
                <#else>
                    ${msg("batch-executer.already-finished")?html}
                </#if>
            </p>
        </#if>
    </div>

</@page>
