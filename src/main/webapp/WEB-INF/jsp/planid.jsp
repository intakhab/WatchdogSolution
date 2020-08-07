<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>
<title>Watch Dog : PlanID List</title>
<jsp:include page="header.jsp"></jsp:include>
<script type="text/javascript">
$(document).ready(function(){
	 
		  $("#tab11").addClass("active");
		  $("#tab1").removeClass("active");
});
</script>
</head>
<body>
	<div class="container">
		<div class="row" style="padding-left: 50px;">
		<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					<li class="breadcrumb-item active"  aria-current="planid"><i class="fa fa-info" aria-hidden="true"></i>
					&nbsp;&nbsp; Incoming PlanID from SO File will persist here till next batch cycle. After successfully running API.This PlanID will be disappeared</li>
				</ol>
			</nav>
			<div class="col-sm-10">
				  
				<table id="example" class="display nowrap" style="width: 100%">
					<thead>
						<tr>
							<th>#</th>
							<th>PlanID</th>
							<th>PlanDescription</th>
							<th>LogisticsGroupCode</th>
							<th>DivisionCode</th>
							<th>Capture Date</th>
							<th>Run Status</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${planList}" var="dt" varStatus="count">
							<tr>
								<td>${count.count}</td>
								<td>${dt.id}</td>
								<td>${dt.planDescription}</td>
								<td>${dt.logisticsGroupCode}</td>
								<td>${dt.divisionCode}</td>
								<td>${dt.createdDate}</td>
								<td>${dt.isRun}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	 <div style="padding-top: 100px;"></div>
		
	</div>
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>

<script type="text/javascript">


  function deleteUser(userid){
	  //alert(userid);
	  var message = "Are you sure want to delete user?";
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
					window.location.href = "deleteuser?id="+userid;
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
 
</script>
</html>
