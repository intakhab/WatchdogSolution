<meta http-equiv="refresh" content="${user.sessionTime}; url=logout">
<link rel="shortcut icon" href="/images/wdog.ico">
<link rel="stylesheet" href="/css/main.css">
<link rel="stylesheet" href="/css/bootstrap.min.css">
<link rel="stylesheet" href="/css/jquery.dataTables.min.css">
<link rel="stylesheet" href="/css/select.dataTables.min.css">
<link rel="stylesheet" href="/css/jquery-ui.css">
<link rel="stylesheet" href="/font-awesome/css/font-awesome.css">
<script type="text/javascript" src="/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript" src="/js/popper.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/js/jquery.dataTables.min.js"></script>  
<script type="text/javascript" src="/js/jquery-ui.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<style type="text/css">
.focus {
	border: 2px solid #AA88FF;
	background-color: #FFEEAA;
}
</style>
<nav class="navbar navbar-inverse navbar-fixed-top">
<!-- navbar-fixed-top -->
<div>
 <!-- class= navbar-header -->
			<a class="navbar-brand" href="javascript:void(0)">
				 <strong><span style="color:red;">\\/\/</span>
				 <span style="color:blue;">a</span>
				 <span style="color:lightgreen">t</span>
				 <span style="color:yellow">c</span>
				 <span style="color:cyan">h</span>
				 <span style="color: #FFFFCC"> [|)og</span></strong>
				 <span style="flaot: left; padding-left: 0px; padding-top: -10px;">
					<img height="45px" 	src="/images/wdog.jpg">
				</span>
			</a>
		</div>
	<div>
		<div id="navbar" class="collapse navbar-collapse mfont"  role ="navigation">
			<ul class="nav navbar-nav">
				<li id="tab1"  class="active"><a href="status"  title="Check Running and Others Status">
				<i class="fa fa-home" aria-hidden="true"></i> Status</a></li>
				<li id="tab2"><a href="configfile" title="Setting configuation for application run">
				<i class="fa fa-cog" aria-hidden="true"></i> Settings </a></li>
				<li class="dropdown" id="tab3">
			        <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)" title="Show Input Folder">
			        <i class="fa fa-expand" aria-hidden="true"></i> Input
			        <span class="caret"></span></a>
			        <ul class="dropdown-menu mfont">
			          <li><a href="inputdir"><i class="fa fa-folder-open-o" aria-hidden="true"></i>&nbsp; File </a></li>
			          <li><a href="inputdirfin"><i class="fa fa-folder-open-o" aria-hidden="true"></i>&nbsp; Fin </a></li>
			          <li><a href="inputdirfbpay"><i class="fa fa-folder-open-o" aria-hidden="true"></i>&nbsp; FBPay </a></li>
			    	  <li><a href="inputdirso"><i class="fa fa-folder-open-o" aria-hidden="true"></i>&nbsp; SO  </a></li>
			          <li><a href="inputdirnonedi"><i class="fa fa-folder-open-o" aria-hidden="true"></i>&nbsp; NEdi</a></li>
			          <li><a href="inputdirbulk"><i class="fa fa-folder-open-o" aria-hidden="true"></i>&nbsp; BLK</a></li>
			          
			        </ul>
			     </li>
				<li id="tab4"><a href="responseout" title="Show all files in Output Folder">
				<i class="fa fa-folder-open" aria-hidden="true"></i>  Output</a></li>
				<li id="tab5"><a href="archiveout" title="Show all files in Archive Folder">
				<i class="fa fa-folder-open" aria-hidden="true"></i> Archive</a></li>
				<li id="tab8"><a href="failuredir" title="Show all Failure files in Failure Folder">
				<i class="fa fa-folder-open" aria-hidden="true"></i> Failure</a></li>
				<li id="tab6"><a href="fileupload" title="Can upload valid file to Input Folder">
				<i class="fa fa-upload" aria-hidden="true"></i> Upload File</a></li>
			    <li id="tab7"><a href="reportspage" title="Check Reports of Failure and Success Files">
			    <i class="fa fa-list" aria-hidden="true"></i>  Reports</a></li>
				<li id="tab9"><a href="logspage" title="Check Error File Logs and Application Logs">
				<i class="fa fa-history" aria-hidden="true"></i> Logs</a></li>
			    <li id="tab11"><a href="planid" title="Can see all plan id in coming run cycle of so batch job">
			    <i class="fa fa-briefcase" aria-hidden="true"></i>&nbsp; PlanID</a></li>
			</ul>
			<!-- UI -->
			<ul class="nav navbar-nav navbar-right" style="padding-right: 30px;">
					<li><a href="#" title="Username"><i class="fa fa-user-o" aria-hidden="true" style="color: yellow"> </i>
					  &nbsp;Hello: ${user.username}</a></li>
					<li class="dropdown" id="tab10">
				        <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)" title="Logout & User registration">
				        <span class="fa fa-address-book"></span></a>
				        <ul class="dropdown-menu mfont" id="tab10" >
				          <li><a href="logout"  title="Logout"><i class="fa fa-sign-out" aria-hidden="true" style="color: red;font-size:15px;"></i>&nbsp; Logout&nbsp;&nbsp;</a></li>
				          <li><a href="reguser" title="Registration of user"><i class="fa fa-user-plus" aria-hidden="true"></i>&nbsp;&nbsp; Add User</a></li>
				          <li><a href="showusers" title="All users"><i class="fa fa-users" aria-hidden="true"></i>&nbsp;&nbsp; Show User</a></li>
				          <li><a href="javascript:void(0)" onclick="restoreUsers()" title="Restore users"><i class="fa fa-window-restore" aria-hidden="true"></i>&nbsp;&nbsp; Restore User</a></li>
				        </ul>
			      </li>
			</ul>
		</div>
	</div>
</nav>
<div style="padding-top: 80px;">
	<jsp:include page="model.jsp"></jsp:include>
</div>
