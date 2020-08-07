<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : Server Status</title>
<jsp:include page="header.jsp"></jsp:include>

</head>
<body>

	<div class="container" id="statusId">
		<div class="row" style="padding-left: 150px;">
			<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
				    <li><i class="fa fa-book fa-fw" aria-hidden="true"></i> <a href="#">Status</a></li>
					<li class="breadcrumb-item active" aria-current="page"> Information</li>
				</ol>
			</nav>
			<div class="col-sm-10">
				<table class="table-striped table" style="width: 80%">
					<tbody>
						<tr>
							<td>WatchDog</td>
							<td><a href="javascript:void(0)" onclick="closeWatchDog('C')"><button class="btn btn-danger"><i class="fa fa-stop-circle-o" aria-hidden="true"></i>
							Stop Me</button> </a></td>
						</tr>
						<tr>
							<td>${statusList[0].serverStatus}</td>
							<td><span class="btn btn-success"><i class="fa fa-check" aria-hidden="true"></i>
							${statusList[1].serverStatus}</span></td>
						</tr>
						<tr>
							<td>Restore Config backup</td>
							<td><a href="javascript:void(0)" onclick="restoreWatchDog()"><span class="btn btn-info"><i class="fa fa-window-restore" aria-hidden="true"></i>
							Restore&nbsp;</span></a></td>
						</tr>
						
						<tr>
							<td>WatchDog</td>
							<td><a href="javascript:void(0)" onclick="closeWatchDog('R')"><span class="btn btn-warning"><i class="fa fa-server" aria-hidden="true"></i>

							Restart&nbsp;</span></a></td>
						</tr>
						
						
						<tr>
							<td>Process ID</td>
							<td><a href="javascript:void(0)" onclick="processId('${statusList[1].pid}')" ><span class="btn btn-primary">
							<i class="fa fa-info" aria-hidden="true"></i>
							&nbsp;${statusList[1].pid}&nbsp;</span></a></td>
						</tr>
						<tr>
							<td>${statusList[0].hostAddress}</td>
							<td>${statusList[1].hostAddress}</td>
						</tr>
						<tr>
							<td>${statusList[0].hostName}</td>
							<td>${statusList[1].hostName}</td>
						</tr>
						<tr>
							<td>${statusList[0].cononicalHostName}</td>
							<td>${statusList[1].cononicalHostName}</td>
						</tr>
						<tr>
							<td>${statusList[0].userName}</td>
							<td>${statusList[1].userName}</td>
						</tr>
						<tr>
							<td>${statusList[0].versionId}</td>
							<td><A href="javascript:alert('${statusList[1].versionId}')">${statusList[1].versionId}</A></td>
						</tr>
						
						<tr>
							<td>${statusList[0].startedTime}</td>
							<td>${statusList[1].startedTime}</td>
						</tr>

					</tbody>

				</table>
			</div>
		</div>
	</div>
<jsp:include page="footer.jsp"></jsp:include>
</body>
<script type="text/javascript">
$(document).ready(function(){
     $("#tab1").addClass("active");
});

function closeWatchDog(val) {
	    var url;
	    var msg;
	    if(val=='C'){
	    	url="shutdownContext";
	    	msg="Close";
	    }else{
	    	url="restartContext";
	    	msg="Restart";

	    }
		var message = "<i class='fa fa-info-circle' aria-hidden='true' style='font-size:35px;color:red'></i>&nbsp;&nbsp;Are you sure want to "+msg+" WatchDog?";
		$('<div></div>').appendTo('body').html(
				'<div><h6>' + message + '</h6></div>').dialog({
			modal : true,
			title : 'Confirmation',
			zIndex : 10000,
			autoOpen : true,
			width : 'auto',
			resizable : false,
			buttons : {
				Yes : function() {
					$(this).dialog("close");
					if(val=='C'){
					   window.location.href = url;
					}else{
						restart();

					}
				},
				No : function() {
					$(this).dialog("close");
				}
			},
			close : function(event, ui) {
				$(this).remove();
			}
		});
	}
	
//width: 600,  height: 500,  modal: true,
function restoreWatchDog() {
	var message = "<i class='fa fa-info-circle' aria-hidden='true' style='font-size:35px;color:red'></i>&nbsp;&nbsp;Are you sure want to Restore Database?";
	$('<div></div>').appendTo('body').html(
			'<div><h6>' + message + '</h6></div>').dialog({
		modal : true,
		title : 'Confirmation',
		zIndex : 10000,
		autoOpen : true,
		width : 'auto',
		resizable : false,
		buttons : {
			Yes : function() {
				$(this).dialog("close");
				window.location.href = "restoreconfig";
			},
			No : function() {
				$(this).dialog("close");
			}
		},
		close : function(event, ui) {
			$(this).remove();
		}
	});
}
	
function processId(val) {
	var message = "<div><i class='fa fa-info-circle' aria-hidden='true' style='font-size:35px;color:red'></i>&nbsp;&nbsp;Are you sure want to Kill Watch Dog Process? <br/>"
		           +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;You can take [ "+val+" ] PID and manually kill with CMD</div>";
	$('<div></div>').appendTo('body').html(
			'<div><h6>' + message + '</h6></div>').dialog({
		modal : true,
		title : 'Confirmation',
		zIndex : 10000,
		autoOpen : true,
		width : 'auto',
		resizable : false,
		buttons : {
			Ok : function() {
				$(this).dialog("close");
				//window.location.href = "restoreconfig";
			}
		},
		close : function(event, ui) {
			$(this).remove();
		}
	});
}

</script>
</html>
