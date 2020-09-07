<!DOCTYPE html>
<%@ page errorPage="errorpage.jsp" %>  
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title>Watch Dog : File Config Page</title>
<jsp:include page="header.jsp"></jsp:include>
</head>
<body>
	<div class="container">
		<div style="padding-left: 150px;">
			<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					    <li><a href="configfile">
					      <i class="fa fa-cog" aria-hidden="true"></i> Settings </a></li>
					    <li class="breadcrumb-item active" aria-current="page">Configuration for Optimization</li>
				</ol>
			</nav>
			<div class="col-sm-9">
			    <div>
			      ${msg}
				</div>
				<ul class="nav nav-tabs" id="navId">
				    <li id="filePathId1" class="active"  onclick="chckTab('filePathId1')"><a data-toggle="tab" href="#filePathId" title="File Optimization Settings">
				    <i class="fa fa-book fa-fw" aria-hidden="true"></i> File</a></li>
				    <li id="batchPathId1" onclick="chckTab('batchPathId1')"><a data-toggle="tab" href="#batchPathId" title="Financial Optimization Settings">
				    <i class="fa fa-book fa-fw" aria-hidden="true"></i> Fin</a></li>
				    <!--  -->
				    <li id="billConfigId1" onclick="chckTab('billConfigId1')"><a data-toggle="tab" href="#billConfigId" title="Freight Bill Pay Optimization Settings">
				    <i class="fa fa-book fa-fw" aria-hidden="true"></i> FBPay</a></li>
				    <!--  -->
				    <li id="planIdTab1"  onclick="chckTab('planIdTab1')"><a data-toggle="tab" href="#planIdTab" title="SO Order Optimization Settings">
				    <i class="fa fa-book fa-fw" aria-hidden="true"></i> SO</a></li>
				    <li id="loadConfigId1" onclick="chckTab('loadConfigId1')"><a data-toggle="tab" href="#loadConfigId" title="NonEdi/Wine Optimization Settings">
				    <i class="fa fa-book fa-fw" aria-hidden="true"></i> NonEdi</a></li>
				    
				    <li id="bulkId1" onclick="chckTab('bulkId1')"><a data-toggle="tab" href="#bulkId" title="Bulk Optimization Settings">
				    <i class="fa fa-book fa-fw" aria-hidden="true"></i> BLK</a></li>
				    
				    <li id="fileConfigId1" onclick="chckTab('fileConfigId1')"><a data-toggle="tab" href="#fileConfigId" title="More/Mail Settings">
				    <i class="fa fa-book fa-fw" aria-hidden="true"></i> More</a></li>
				    
			   </ul>
				<form:form name="form" role="form" method="POST" id="configForm"  action="saveconfiginfo" modelAttribute="infoObj">
				<form:hidden path="dogId" />
				<form:hidden path="tabId" id="tabId" />
				<form:hidden path="cronHit" id="cronHit" />
				<input type="hidden" id="tabIds"  value="${tabId}"/>
				 <div class="tab-content">
				       <br>
				     <!-- Start Tab 1 -->   
				    <div id="filePathId" class="tab-pane active">
						  <div class="form-group">
								<label for="batchFilePath">Batch File Path</label>
								<form:input path="batchFilePath"  class="form-control" id="batchFilePathId" placeholder="Batch File Path"
									required="required" />
							</div>
							<div class="form-group">
								<label for="inputFolderPath">Input Folder Path</label>
								<form:input path="inputFolderPath" class="form-control" id="inputFolderPathId" placeholder="Input Folder Path"
									required="required" />
							</div>
							<div class="form-group">
								<label for="outputFolderPath">Output Folder Path</label>
								<form:input path="outputFolderPath" class="form-control" id="outputFolderPathId" placeholder="Ouptput Folder Path"
									required="required" />
							</div>
							<div class="form-group">
								<label for="archiveFolderPath">Archive Folder Path</label>
								<form:input path="archiveFolderPath" cssClass="form-control" id="archiveFolderPathId" placeholder="Archive Folder Path" required="required" />
							</div>
		                     
		                     <div class="form-group">
								<label for="archiveFolderPath">Failure Folder Path</label>
								<form:input path="failureFolderPath" cssClass="form-control" id="failureFolderPath" placeholder="Failure Folder Path" required="required" />
							</div>
							<div class="form-group">
								<label for="pollingTime">File Support(s)</label>
								<form:input path="fileSupports" class="form-control"
									id="fileSupportsId" placeholder="Enter File Type" required="required" />
								<small id="fileSupportsSmall" class="form-text text-muted">Can
									write file end with comma separator like <strong>cam,dc,so,edi,ssc</strong></small>
		
							</div>
						<div id="mydiv" class="">
						    <div class="form-group">
									<div class="form-group input_fields_wrap">
									<i class="fa fa-info-circle" aria-hidden="true"></i>
										<label class="" for="action_id">Change Entity Name</label>
										<small id="actionIdName" class="form-text text-muted"> 
										You can write like <strong class="impo">cam=changeEntity</strong> 
										  (Don't put whitespace) 15 Maximum input boxes allowed. 
										</small>
										<c:choose>
										<c:when test="${fn:length(infoObj.supportsAPI) eq 0}">
         									  <div class="entry input-group">
												<form:input id="action_id" path="supportsAPI[0]"
													placeholder="Enter like cam=changeEntity"
													class="form-control input-md" required="required" title="${infoObj.supportsAPI[0]}" />

												<span class="input-group-btn">
													<button id="add-more" name="add-more"
														class="btn btn-success add-more" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br>
         								</c:when>
										<c:otherwise>
									    <c:forEach items="${infoObj.supportsAPI}" varStatus="loop">
											<div class="entry input-group">
												<form:input id="action_id" path="supportsAPI[${loop.index}]"
													placeholder="Enter like cam=changeEntity"
													class="form-control input-md" title="${infoObj.supportsAPI[loop.index]}"  />

												<span class="input-group-btn">
													<button id="add-more" name="add-more"
														class="btn btn-success add-more" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>

											</div>
											<br />
											</c:forEach>
										</c:otherwise>
								</c:choose>
								</div>
								</div>
								<br> 
				        	</div>
                        </div>
                         <!-- End Tab 1 -->
                         <!-- Start Tab 2 -->
                        <div  id="batchPathId" class="tab-pane">
		                        	<div class="form-group">
								<label for="optInputFolderPath">Fin Input Folder Path</label>
								<form:input path="optInputFolderPath" class="form-control" id="optInputFolderPathId" placeholder="Input Folder Path"
									required="required" />
							</div>
							<div class="form-group">
								<label for="soaOutputFolderPath">SOA In/Out Folder Path</label>
								<form:input path="soaOutputFolderPath" class="form-control" id="soaOutputFolderPathId" placeholder="SOA In/Out Folder Path"
									required="required" />
							</div>
		                     <div class="form-group">
								<label for="optFileSupports">Fin File Supports</label>
								<form:input path="optFileSupports" class="form-control"
									id="fileSupportsId" placeholder="Enter File Type" required="required" />
								<small id="fileSupportsSmall" class="form-text text-muted">Can
									write file end with comma separator like <strong>csi,cam,payload</strong></small>
		
							</div>
				    	<div id="mydiv2">
						    <div class="form-group">
									<div class="form-group input_fields_wrap1"><i class="fa fa-info-circle" aria-hidden="true"></i>
										<label class="" for="action_id">API Configuration</label>
										<small id="actionIdName" class="form-text text-muted"> 
										ex. <strong class="impo">API@{TAG=VAL|TAG=VAL}</strong> BLANK = empty value, AUTO_NUM = watchdog will add auto num. 
										   processScheduleRunRequest@{ScheduleRunTypeEnumVal=SRT_APVCHR|RequestID=AUTO_NUM}(Don't put whitespace)
										   There are four group G1,G2,G3 and G4 which will run in separate batch timing. 
										</small>
										<c:choose >
										<c:when test="${fn:length(infoObj.optSupportsAPI) eq 0}">
         									  <div class="entry input-group">
												<form:input id="action_id" path="optSupportsAPI[0]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value} if you dont know value then write value=?"
													class="form-control input-md" title="${infoObj.optSupportsAPI[0]}"/>

												<span class="input-group-btn">
													<button id="add-more1" name="add-more"
														class="btn btn-success add-more1" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br>
         								</c:when>
										<c:otherwise>
									    <c:forEach items="${infoObj.optSupportsAPI}" varStatus="loop">
											<div class="entry input-group">
												<form:input id="action_id" path="optSupportsAPI[${loop.index}]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value} if you dont know value then write value=?"
													class="form-control input-md" title="${infoObj.optSupportsAPI[loop.index]}"/>

												<span class="input-group-btn">
													<button id="add-more1" name="add-more"
														class="btn btn-success add-more1" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>

											</div>
											<br />
											</c:forEach>
										</c:otherwise>
								</c:choose>
										
								</div>
								</div>
								<br> 
					       </div>
                     </div>
                    <div  id="loadConfigId" class="tab-pane">
                            <nav aria-label="breadcrumb">
								<ol class="breadcrumb">
					           <li><a href="javascript:void(0)"><i class="fa fa-pencil fa-fw" aria-hidden="true"></i>
					           Non Edi Configuration </a></li>
				             </ol>
			               </nav>
                            <div class="form-group">
								<label for="nonEdiCamFolderPath">Non Edi Folder Path</label>
								<form:input path="nonEdiCamInputFolderPath" class="form-control" id="nonEdiCamInputFolderPathId" placeholder="NonEdi Input Folder Path"
									required="required" />
							</div>
							
		                     <div class="form-group">
								<label for="nonEdiCamFileSupports">File Supports</label>
								<form:input path="nonEdiCamFileSupports" class="form-control"
									id="nonEdiCamFileSupports" placeholder="Enter File Type" required="required" />
								<small id="fileSupportsSmall" class="form-text text-muted">Can
									write file end with comma separator like <strong>nonedi</strong></small>
		
							</div>
				    	<div id="mydiv">
						    <div class="form-group">
									<div class="form-group input_fields_wrap2">
										<label for="action_id"><i class="fa fa-info-circle" aria-hidden="true"></i> API Configuration</label>
										<small id="actionIdName" class="form-text text-muted"> 
										ex. <strong class="impo">API@{TAG=VAL|TAG=VAL}</strong> READ read data, PUT means push data in XML
										  (Don't put whitespace)
										</small>
										<c:choose >
										<c:when test="${fn:length(infoObj.nonEdiCamSupportsAPI) eq 0}">
         									  <div class="entry input-group">
												<form:input id="action_id" path="nonEdiCamSupportsAPI[0]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.nonEdiCamSupportsAPI[0]}"/>

												<span class="input-group-btn">
													<button id="add-more1" name="add-more"
														class="btn btn-success add-more2" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br>
         								</c:when>
										<c:otherwise>
									    <c:forEach items="${infoObj.nonEdiCamSupportsAPI}" varStatus="loop">
											<div class="entry input-group">
												<form:input id="action_id"  path="nonEdiCamSupportsAPI[${loop.index}]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.nonEdiCamSupportsAPI[loop.index]}"/>

												<span class="input-group-btn">
													<button id="add-more1" name="add-more"
														class="btn btn-success add-more2" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br />
											</c:forEach>
										</c:otherwise>
								</c:choose>
								</div>
						 	</div>
							<br> 
					    </div>
					       <!--  CLosed as per requiest
					       
					        <nav aria-label="breadcrumb">
									<ol class="breadcrumb">
										<li><a href="javascript:void()"><i class="fa fa-pencil fa-fw" aria-hidden="true"></i> Wine Configuration </a></li>
									</ol>
					    	</nav> 
							 <div class="form-group">
								<label for="nonEdiCamFolderPath">File Supports</label>
								<form:input path="nonEdiCamWineFileSupports" class="form-control" id="nonEdiCamWineFileSupportsId" placeholder="Wine File Supports"
									required="required" />
							</div>
							
						<div id="mydiv-3">
						    <div class="form-group">
									<div class="form-group input_fields_wrap3">
										<label for="action_id"><i class="fa fa-info-circle" aria-hidden="true"></i> API Configuration</label>
										<small id="actionIdName" class="form-text text-muted"> 
										ex. <strong>API@{TAG=VAL|TAG=VAL}</strong> READ read data, PUT means push data in XML
										  (Don't put whitespace)
										</small>
										<c:choose >
										<c:when test="${fn:length(infoObj.nonEdiCamWineSupportsAPI) eq 0}">
         									  <div class="entry input-group">
												<form:input id="action_id" path="nonEdiCamWineSupportsAPI[0]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.nonEdiCamWineSupportsAPI[0]}"/>

												<span class="input-group-btn">
													<button id="add-more3" name="add-more"
														class="btn btn-success add-more3" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br>
         								</c:when>
										<c:otherwise>
									    <c:forEach items="${infoObj.nonEdiCamWineSupportsAPI}" varStatus="loop">
											<div class="entry input-group">
												<form:input id="action_id"  path="nonEdiCamWineSupportsAPI[${loop.index}]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.nonEdiCamWineSupportsAPI[loop.index]}"/>

												<span class="input-group-btn">
													<button id="add-more3" name="add-more"
														class="btn btn-success add-more3" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br />
											</c:forEach>
										</c:otherwise>
								</c:choose>
								</div>
						 	</div>
							<br> 
					    </div>-->
					<br> 
					<br> 
                  </div>
                  <!-- End Tab 2 -->
                  <!-- Bulk -->
                  <div  id="bulkId" class="tab-pane">
                           
                            <div class="form-group">
								<label for="bulkInputFolderPath">Bulk Folder Path</label>
								<form:input path="bulkInputFolderPath" class="form-control" id="bulkInputFolderPathId" placeholder="Bulk Input Folder Path"
									required="required" />
							</div>
							
		                     <div class="form-group">
								<label for="bulkFileSupports">File Supports</label>
								<form:input path="bulkFileSupports" class="form-control"
									id="bulkFileSupports" placeholder="Enter File Type" required="required" />
								<small id="fileSupportsSmall" class="form-text text-muted">Can
									write file end with comma separator like <strong>bulk</strong></small>
		
							</div>
				    	<div id="mydiv6">
						    <div class="form-group">
									<div class="form-group input_fields_wrap6">
										<label for="action_id"><i class="fa fa-info-circle" aria-hidden="true"></i> API Configuration</label>
										<small id="actionIdName" class="form-text text-muted"> 
										ex. <strong class="impo">API@{TAG=VAL|TAG=VAL}</strong> READ read data, PUT means push data in XML
										  (Don't put whitespace)
										</small>
										<c:choose >
										<c:when test="${fn:length(infoObj.bulkSupportsAPI) eq 0}">
         									  <div class="entry input-group">
												<form:input id="action_id" path="bulkSupportsAPI[0]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.bulkSupportsAPI[0]}"/>

												<span class="input-group-btn">
													<button id="add-more6" name="add-more"
														class="btn btn-success add-more6" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br>
         								</c:when>
										<c:otherwise>
									    <c:forEach items="${infoObj.bulkSupportsAPI}" varStatus="loop">
											<div class="entry input-group">
												<form:input id="action_id"  path="bulkSupportsAPI[${loop.index}]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.bulkSupportsAPI[loop.index]}"/>

												<span class="input-group-btn">
													<button id="add-more6" name="add-more"
														class="btn btn-success add-more6" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br />
											</c:forEach>
										</c:otherwise>
								</c:choose>
								</div>
						 	</div>
							<br> 
					    </div>
						
					<br> 
					<br> 
                  </div>
                  <!-- Start Tab 3 -->
				<div id="fileConfigId" class="tab-pane">
				      <div>
					        <nav aria-label="breadcrumb">
									<ol class="breadcrumb">
										<li><a href="javascript:void()"><i class="fa fa-pencil fa-fw" aria-hidden="true"></i> Auto Pilot</a></li>
									</ol>
					    	</nav>
 					     <form:checkbox path="autoPilot" class="form-check-input" id="autoPilotId"/>
 					     <label class="form-check-label" for="autoPilotIdLbl">Enable Auto Pilot</label>
 					
						 <div id="isAutoPilotDiv">
	  							<small id="dd" class="form-text text-muted"><span style="color:blue">Auto Pilot will automatically Restart WatchDog in given time interval</span>(<span style="color:red"> [second, minute, hour, day, month, weekday ex  00 05 21 * * * </span>)</small>	
								<div class="input-group" >
	    					    <span class="input-group-addon">Auto Pilot&nbsp;</span>
	   						    <form:input id="autoPilotCronId" type="text" class="form-control msgc" path="autoPilotCron" placeholder="Additional Info" required="required" />
	  					    </div>
  					     </div>
					       <br/>
					       <br/>
					 </div>
					 <div>
				            <nav aria-label="breadcrumb">
									<ol class="breadcrumb">
										<li><a href="javascript:void()"><i class="fa fa-pencil fa-fw" aria-hidden="true"></i> Mail Configuration </a></li>
									</ol>
					    	</nav>
					  </div>  	
					<div class="form-group">
					   <input type="hidden" id="checkBoxId" value="${infoObj.enableMail}"/>
						<form:checkbox path="enableMail" value="${enableMail}" class="form-check-input"
							 id="emailCheckedId"/>
						<label class="form-check-label" for="emailCheckedId">Enable Email</label>
						<div id="emailCheckedTextAreaId" >
							<div class="input-group">
								<span class="input-group-addon">@</span>
								<form:input type="email" path="toWhomEmail" class="form-control" 
									id="toWhomEmailId"  placeholder="Send Email ias@hcl.com,foo@hcl.com" 
									required="required" aria-describedby="toWhomEmailIns" multiple="multiple" />
							</div>
							<small id="toWhomEmailIns" title="${toWhomEmail}" class="form-text text-muted">
						     <span class="impo">Write email id with comma separator</span> i.e <strong>ias@hcl.com, foo@hcl.com</strong></small>
						    <div class="form-row row">
								<div class="form-group col-md-4">
									<label for="host">Mail Host</label>
									<form:input path="host" 
										class="form-control" id="hostId" placeholder="Email Host"  />
								</div>	
								<div class="form-group col-md-4">
									<label for="host">Mail Port</label>
									<form:input path="port" 
										class="form-control" id="portId" placeholder="Email Port"  />
								</div>	
								<div class="form-group col-md-4">
									<label for="host">Debug Mail</label>
									<form:select path="debugMail" class="form-control" id="debugMailId">
										<form:option value="true">ON</form:option>
										<form:option value="false">OFF</form:option>
	 
								     </form:select>
								</div>		
						   </div>
						   
						   <div class="form-row row">
								<div class="form-group col-md-4">
									<label for="host">Mail Username</label>
									<form:input path="mailUserName" 
										class="form-control" id="mailUserNameId" placeholder="Mail Username" />
								</div>	
								<div class="form-group col-md-4">
									<label for="host">Mail Password</label>
									<form:input  path="mailPassword" 
										class="form-control" id="mailPasswordId" placeholder="Mail Password"  autocomplete="off" />
								</div>	
								<div class="form-group col-md-4">
									<label for="host">Mail from</label>
									<form:input path="fromMail" 
										class="form-control" id="fromMailId" placeholder="Mail From"  />
								</div>	
									
						   </div>
						   <div class="form-row row">
							<div class="form-group col-md-6">
								<label for="enableStartupEmail">Enable Startup Email</label>
								<form:select path="enableStartupEmail" class="form-control"
									id="enableStartupEmail">
									<form:option value="true">ON</form:option>
									<form:option value="false">OFF</form:option>
								</form:select>
							</div>
							<div class="form-group col-md-6">
								<label for="enableShutdownEmail">Enable Shutdown Email</label>
								<form:select path="enableShutdownEmail" class="form-control"
									id="enableShutdownEmail">
									<form:option value="true">ON</form:option>
									<form:option value="false">OFF</form:option>
	
								</form:select>
							</div>
						</div>
								
						</div>
						
					</div>
					      <nav aria-label="breadcrumb">
									<ol class="breadcrumb">
										<li><a href="javascript:void()"><i class="fa fa-pencil fa-fw" aria-hidden="true"></i> System Configuration </a></li>
									</ol>
					    	</nav>
					<div class="form-row row">
						<div class="form-group col-md-6">
							<label for="responseLog">Disabled TMS Response log</label>
							<form:select path="enableResponseCodeLog" class="form-control"
								id="enableResponseLogId">
								<form:option value="true">ON</form:option>
								<form:option value="false">OFF</form:option>

							</form:select>
						</div>
						<div class="form-group col-md-6">
							<label for="enableArchiveOthersFile">Archive not supported file from Input Folder</label>
							<form:select path="enableArchiveOthersFile" class="form-control"
								id="enableArchiveOthersFileId">
								<form:option value="true">ON</form:option>
								<form:option value="false">OFF</form:option>

							</form:select>
						</div>
					</div>
					

					<div class="form-row row hidden" >
						<div class="form-group col-md-6">
							<label for="fileTypeSeparator">File Separator</label>
							<form:input path="fileTypeSeparator" value="@"
								class="form-control" id="fileTypeSeparatorId"
								placeholder="File Separator"  required="required" readonly="readonly" />
								<small id="fileTypeSeparator" class="form-text text-muted">You can't change, it is read only</small>
						</div>
						<div class="form-group col-md-6">
							<label for="fileExtension">File Extension</label>
							<form:input path="fileExtension" value="xml" class="form-control"
								id="fileExtensionId" placeholder="Enter File Extension" required="required" readonly="readonly" />
							<small id="fileExtensionId" class="form-text text-muted">You can't change, it is read only</small>	
						</div>
					</div>

					<div class="form-row row">
						<div class="form-group col-md-6">
							<label for="limitFilesFolder">Limit files in folder</label>
							<form:input path="limitFilesFolder" 
								class="form-control" id="limitFilesFolderId" placeholder="Limit files in folder" required="required" />
						</div>
						<div class="form-group col-md-6">
							<label for="responseFilePrefix">Out XML File Prefix</label>
							<form:input path="responseFilePrefix" class="form-control" id="responseFilePrefixId"
								placeholder="Enter File Extension" required="required" />
						</div>
					</div>
					<div>
					        <nav aria-label="breadcrumb">
									<ol class="breadcrumb">
										<li><a href="javascript:void()"><i class="fa fa-pencil fa-fw" aria-hidden="true"></i> Batch/Job Configuration Run </a></li>
									</ol>
					    	</nav>
					
					</div>
					<div class="form-row row">
					    <div class="form-group col-md-3">
							<label for="stopFileRun">File Batch</label>
							<form:select path="stopFileRun" class="form-control"
								id="stopFileRunId">
								<form:option value="true">Stop</form:option>
								<form:option value="false">Start</form:option>
							</form:select>
						<small id="tt" class="form-text text-muted">Start/Stop</small>	
						</div>
						<div class="form-group col-md-2">
							<label for="stopSoBatchRun">SO Batch</label>
							<form:select path="stopSoBatchRun" class="form-control"
								id="stopSoBatchRunId">
								<form:option value="true">Stop</form:option>
								<form:option value="false">Start</form:option>
							</form:select>
						  <small id="tt" class="form-text text-muted">Start/Stop</small>	
						</div>
						<div class="form-group col-md-2">
							<label for="stopBatchRun">Fin Batch</label>
							<form:select path="stopBatchRun" class="form-control"
								id="stopBatchRunId">
								<form:option value="true">Stop</form:option>
								<form:option value="false">Start</form:option>
							</form:select>
						<small id="tt" class="form-text text-muted">Start/Stop</small>	
						</div>
						
						<div class="form-group col-md-2">
							<label for="stopNonEdiBatchRun">NonEdi Batch</label>
							<form:select path="stopNonEdiBatchRun" class="form-control"
								id="stopNonEdiBatchRunId">
								<form:option value="true">Stop</form:option>
								<form:option value="false">Start</form:option>
							</form:select>
						<small id="tt" class="form-text text-muted">Start/Stop</small>	
						</div>
						<div class="form-group col-md-3">
							<label for="stopBulkBatchRun">Bulk Batch</label>
							<form:select path="stopBulkBatchRun" class="form-control"
								id="stopBulkBatchId">
								<form:option value="true">Stop</form:option>
								<form:option value="false">Start</form:option>
							</form:select>
						<small id="tt" class="form-text text-muted">Start/Stop</small>	
						</div>
					</div>	
					<div id="fbatch">
						   <p>
	  					  </p>
	  					<small id="tt" class="form-text text-muted"><span style="color:blue">File Dog Polling Time </span>(<span style="color:red">Put polling time in Minute(s)</span>)</small>	
						<div class="input-group" >
	    					<span class="input-group-addon">F1&nbsp;</span>
	   						 <form:input id="msg1" type="text" class="form-control dig" path="filePollingTime" placeholder="Additional Info" required="required" maxlength="3" />
	  					</div>
  					</div>
  					<div id="sobatch">
	  					  <p>
	  					 </p>
	  					<small id="tt" class="form-text text-muted"><span style="color:blue">SO Optimization Cron Time </span> [<span style="color:red">second, minute, hour, day, month, weekday</span> ex 1. [00 05 21 * * *] ex 2. [ * */1 * * * *]]</small>	
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">G1&nbsp;</span>
	   						 <form:input id="msg6" type="text" class="form-control msgc" path="soCronTimeG1" placeholder="00 20 20 * * *" required="required"/>
	   						 
	  					</div>
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">G2&nbsp;</span>
	   						 <form:input id="msg7" type="text" class="form-control msgc" path="soCronTimeG2" placeholder="00 20 21 * * *"  required="required"/>
	  					</div>
  					</div>
  					<div id="finbatch">
	  					   <p>
	  					  </p>
	  					<small id="tt" class="form-text text-muted"><span style="color:blue">Fin Optimization Cron Time</span> [<span style="color:red">second, minute, hour, day, month, weekday</span> ex 1. [00 05 21 * * *], ex 2. [ * */1 * * * *]]</small>	
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">G1&nbsp;</span>
	   						 <form:input id="msg2" type="text" class="form-control msgc" path="finCronTimeG1" placeholder="00 05 21 * * *" required="required"/>
	  					</div>
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">G2&nbsp;</span>
	   						 <form:input id="msg3" type="text" class="form-control msgc" path="finCronTimeG2" placeholder="* */1 * * * *"  required="required"/>
	  					</div>
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">G3&nbsp;</span>
	   						 <form:input id="msg4" type="text" class="form-control msgc" path="finCronTimeG3"  placeholder="*/60 * * * * *"  required="required"/>
	  					</div>
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">G4&nbsp;</span>
	   						 <form:input id="msg5" type="text" class="form-control msgc" path="finCronTimeG4"  placeholder="10 2 20 * * *"  required="required" />
	  					</div>
  					</div>
  					
  					<div id="edibatch">
  					
	  				      <p>
	  					 </p>
	  					<small id="tt" class="form-text text-muted"><span style="color:blue">NonEdi Optimization Polling Time </span>  (<span style="color:red">Put polling time in Minute(s)</span>)</small>
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">G1&nbsp;</span>
	   						 <form:input id="msg8" type="text" class="form-control dig" path="nonEdiPoollingTimeG1" placeholder="10 Mins" required="required" maxlength="4" />
	  					</div>
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">W1</span>
	   						 <form:input id="msg9" type="text" class="form-control dig" path="nonEdiPoollingTimeW1" placeholder="15 Mins" required="required" maxlength="4" />
	   						   
	  					</div>
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">W2</span>
	   						 <form:input id="msg10" type="text" class="form-control dig" path="nonEdiPoollingTimeW2" placeholder="20 Mins" required="required" maxlength="4" />
	  					</div>	
  					</div>
  					
  					<div id="bulkbatch">
  					
	  				      <p>
	  					 </p>
	  					<small id="tt" class="form-text text-muted"><span style="color:blue">Bulk Optimization Cron Time</span> [<span style="color:red">second, minute, hour, day, month, weekday</span> ex 1. [00 05 21 * * *], ex 2. [ * */1 * * * *]]</small>	
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">G1&nbsp;</span>
	   						 <form:input id="msg11" type="text" class="form-control msgc" path="bulkPoollingTimeG1" placeholder="10 2 20 * * *"  required="required" />
	  					</div>
	  					<div class="input-group form-group">
	    					<span class="input-group-addon">G2&nbsp;</span>
	   						 <form:input id="msg12" type="text" class="form-control msgc" path="bulkPoollingTimeG2" placeholder="10 2 20 * * *"  required="required" />
	  					</div>
	  					
  					</div>
  					
  					           <input type="hidden" id="pollTimeId" value="${infoObj.filePollingTime}"/>
	   						   <input type="hidden" id="soCronTimeG1Id" value="${infoObj.soCronTimeG1}"/>
	   						   <input type="hidden" id="soCronTimeG2Id" value="${infoObj.soCronTimeG2}"/>
	   						   
   	   						   <input type="hidden" id="finCronTimeG1Id" value="${infoObj.finCronTimeG1}"/>
   	   						   <input type="hidden" id="finCronTimeG2Id" value="${infoObj.finCronTimeG2}"/>
   	   						   <input type="hidden" id="finCronTimeG3Id" value="${infoObj.finCronTimeG3}"/>
   	   						   <input type="hidden" id="finCronTimeG4Id" value="${infoObj.finCronTimeG4}"/>
   	   						   
   	   						   <input type="hidden" id="nonEdiPoollingTimeG1Id" value="${infoObj.nonEdiPoollingTimeG1}"/>
	   						   <input type="hidden" id="nonEdiPoollingTimeW1Id" value="${infoObj.nonEdiPoollingTimeW1}"/>
	   						   <input type="hidden" id="nonEdiPoollingTimeW2Id" value="${infoObj.nonEdiPoollingTimeW2}"/>
	   						   <input type="hidden" id="bulkPoollingTimeG1Id" value="${infoObj.bulkPoollingTimeG1}"/>
	   						   <input type="hidden" id="bulkPoollingTimeG2Id" value="${infoObj.bulkPoollingTimeG2}"/>
	   						   
	   				           <input type="hidden" id="autoPilotTimeId" value="${infoObj.autoPilotCron}"/>
	   				           
	   				           
	   				           
	  				 <br>
					 <br>
					 <br>
				 </div>
				  <!-- End Tab 3 -->
				  <!-- Tab 4 -->
					 <div  id="planIdTab" class="tab-pane">
                            <div class="form-group">
								<label for="soOrderInputFolder">SO Folder Path</label>
								<form:input path="soOrderInputFolderPath" class="form-control" id="soOrderInputFolderId" placeholder="SO Opt Input Folder Path"
									required="required" />
							</div>
				    	   <div id="mydivPlanId">
						    <div class="form-group">
									<div class="form-group input_fields_wrap4">
										<label for="action_id"><i class="fa fa-info-circle" aria-hidden="true"></i> API Configuration</label>
										<small id="actionIdName" class="form-text text-muted"> 
										ex. <strong class="impo">API@{TAG=VAL|TAG=VAL}</strong> READ read data, PUT means push data in XML
										  (Don't put whitespace)
										</small>
										<c:choose >
										<c:when test="${fn:length(infoObj.soOrderSupportsAPI) eq 0}">
         									  <div class="entry input-group">
												<form:input id="action_id" path="soOrderSupportsAPI[0]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.soOrderSupportsAPI[0]}"/>
												<span class="input-group-btn">
													<button id="add-more4" name="add-more"
														class="btn btn-success add-more4" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br>
         								</c:when>
										<c:otherwise>
									    <c:forEach items="${infoObj.soOrderSupportsAPI}" varStatus="loop">
											<div class="entry input-group">
												<form:input id="action_id"  path="soOrderSupportsAPI[${loop.index}]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.soOrderSupportsAPI[loop.index]}"/>

												<span class="input-group-btn">
													<button id="add-more4" name="add-more"
														class="btn btn-success add-more4" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br />
											</c:forEach>
										</c:otherwise>
								</c:choose>
										
								</div>
								
								
						 	</div>
							<br> 
					     </div>
							<br> 
							<br> 
                   </div>
                   
                   <div  id="billConfigId" class="tab-pane">
                            <div class="form-group">
								<label for="fbPayInputFolderPath">FBPay Folder Path</label>
								<form:input path="fbPayInputFolderPath" class="form-control" id="fbPayInputFolderId" placeholder="FBPay Input Folder Path"
									required="required" />
							</div>
							<div class="form-group">
								<label for="fbPayFileSupports">FBPay Supports File</label>
								<form:input path="fbPayFileSupports" class="form-control" id="fbPayFileSupportsId" placeholder="FBPay File Support"
									required="required" />
							</div>
				    	   <div id="mydivPayId">
						    <div class="form-group">
									<div class="form-group input_fields_wrap5">
										<label for="action_id"><i class="fa fa-info-circle" aria-hidden="true"></i> API Configuration</label>
										<small id="actionIdName" class="form-text text-muted"> 
										ex. <strong class="impo">API@{TAG=VAL|TAG=VAL}</strong> READ read data, PUT means push data in XML
										  (Don't put whitespace)
										</small>
										<c:choose >
										<c:when test="${fn:length(infoObj.fbPaySupportsAPI) eq 0}">
         									  <div class="entry input-group">
												<form:input id="action_id" path="fbPaySupportsAPI[0]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.fbPaySupportsAPI[0]}"/>
												<span class="input-group-btn">
													<button id="add-more5" name="add-more"
														class="btn btn-success add-more5" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br>
         								</c:when>
										<c:otherwise>
									    <c:forEach items="${infoObj.fbPaySupportsAPI}" varStatus="loop">
											<div class="entry input-group">
												<form:input id="action_id"  path="fbPaySupportsAPI[${loop.index}]"
													placeholder="Enter API@{XMLTaG=Value| XMLTag=Value}"
													class="form-control input-md" title="${infoObj.fbPaySupportsAPI[loop.index]}"/>

												<span class="input-group-btn">
													<button id="add-more5" name="add-more"
														class="btn btn-success add-more5" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<br />
											</c:forEach>
										</c:otherwise>
								</c:choose>
								</div>
						 	</div>
							<br> 
					     </div>
					    
							<br> 
							<br> 
                        
                   </div>
					 <!-- Tab FB Pay End -->
					 
					</div>
					<button type="submit" id="submitButtonId" class="btn btn-primary">
					<i class="fa fa-floppy-o" aria-hidden="true"></i> Save Configuration Info</button>
					<button type="button" id="re-loadId" class="btn btn-success">
					<i class="fa fa-refresh" aria-hidden="true"></i> Reload Page</button>
					
					<br />
					<br />
					<br />
					<br />
				
				</form:form>
  			
			</div>
		</div>
	</div>

	<div>
	
</div>
<jsp:include page="footer.jsp"></jsp:include>
</body>
 <jsp:include page="configfile-script.jsp"></jsp:include>
</html>
