<html>
<head>
   <title>Batch JavaScript Jobs</title>
   <style>
      body {
         font-family: "Lucida Sans Unicode", "Lucida Grande", Sans-Serif;
         background: #fff;
         color: #039;
      }
      h3 {
         font-weight: normal;
      }
      table {
         font-size: 12px;
         margin: 20px;
         width: 95%;
         border-collapse: collapse;
         text-align: left;
      }
      table th {
         font-size: 14px;
         font-weight: normal;
         padding: 10px 8px;
         border-bottom: 2px solid #6678b1;
      }
      table td {
         border-bottom: 1px solid #ccc;
         color: #669;
         padding: 6px 8px;
      }
      table tbody tr:hover td {
         color: #009;
      }
      div.cancel {
         background-image: url(${url.context}/images/icons/error.gif);
         background-repeat: no-repeat;
         background-position: center;
         width: 25px;
         height: 25px;
         cursor: pointer;
      }
      div.canceled {
         font-size: 14px;
      }
   </style>

   <script>
      function cancel(jobId) {
         var xhr = new XMLHttpRequest();
         var url = '${url.service}';
         if (url.endsWith('.html')) {
            url = url.slice(0, -5);
         }
         xhr.open('DELETE', url + '/' + jobId, true);
         xhr.onreadystatechange = function () {
            if (xhr.readyState != 4) return;
            if (xhr.status == 200 || xhr.status == 204) {
               var canceled = xhr.status == 200;
               window.location = '${url.service}?canceledJob=' + jobId + '&canceled=' + canceled;
            }
         }
         xhr.send();
      }
   </script>
</head>
<body>
<h3>Jobs being executed by batch executer</h3>
<#if jobs?size == 0>
   <p>There are no jobs running now</p>
<#else>
   <table class="moduletable">
      <thead>
      <tr>
         <th>Name</th>
         <th>Batch Size</th>
         <th>Threads</th>
         <th>Disabled Rules</th>
         <th>Completed</th>
         <th>Total</th>
         <th>Node Function</th>
         <th>Batch Function</th>
         <th>Status</th>
         <th>Action</th>
      </tr>
      </thead>
      <tbody>
      <#list jobs as job>
         <tr>
            <td>${job.name}</td>
            <td>${job.batchSize?c}</td>
            <td>${job.threads?c}</td>
            <td>${(job.disableRules!false)?string}</td>
            <td>${(job.completed!0)?c}</td>
            <td><#if (job.totalEstimatedWorkSize!-1) &gt;= 0>${job.totalEstimatedWorkSize?c}<#else>-</#if></td>
            <td>${job.onNodeFunction!""}</td>
            <td>${job.onBatchFunction!""}</td>
            <#assign jobStatus = (job.status!"")?string/>
            <td>${jobStatus}</td>
            <td>
               <#if jobStatus != "CANCELED" && jobStatus != "FINISHED">
               <div title="Cancel job" class="cancel" onclick="cancel('${job.id?replace("'", "\\'")}');"></div>
               </#if>
            </td>
         </tr>
      </#list>
      </tbody>
   </table>
</#if>

<#if args.canceledJob??>
<div class="canceled">
   Job by ID ${args.canceledJob}
   <#if args.canceled?? && args.canceled == "true">
   was canceled
   <#else>
   has already finished or was canceled before
   </#if>
</div>
</#if>

</body>
</html>
