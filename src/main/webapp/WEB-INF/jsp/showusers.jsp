<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html lang="en">
<head>
<title>Watch Dog : Users List</title>
<jsp:include page="header.jsp"></jsp:include>
<script type="text/javascript">
$(document).ready(function(){
	  var v='${param.msg}';
		if(v==''){
			$("#alertId").hide();
		}
		  $("#tab10").addClass("active");
		  $("#tab1").removeClass("active");
});
</script>
</head>
<body>
	<div class="container">
		<div class="row" style="padding-left: 50px;">
		<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					<li class="breadcrumb-item active" aria-current="showusers"><i class="fa fa-users" aria-hidden="true"></i>
					 Registered User list</li>
				</ol>
			</nav>
			<div class="col-sm-10">
				  <div id='alertId' class='alert alert-success' data-dismiss='alert' aria-label='Close' role='alert'>${param.msg}
			    <span style='float:right;cursor: pointer;'>&times;</span></div>		
				<table id="example" class="display nowrap" style="width: 100%">
					<thead>
						<tr>
							<th>#</th>
							<th>Email</th>
							<th>User Name</th>
							<th>Created Date</th>
							<th>Status</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${users}" var="dt" varStatus="count">
							<tr>
								<td>${count.count}</td>
								<td>${dt.email}</td>
								<td>${dt.username}</td>
								<td>${dt.createdate}</td>
								
								<td>
								<c:choose>
								<c:when test="${dt.active eq 'true'}">
						              <button type="button" class="btn_link btn-success"><i class="fa fa-check-square" aria-hidden="true"></i>
</button> 
						            </c:when>
								<c:otherwise>
								     <button type="button" class="btn_link btn-danger"><i class="fa fa-ban" aria-hidden="true"></i>
								     </button>
								</c:otherwise>
								</c:choose>
								
								</td>
							    <td>
							      <a href="edituser?id=${dt.id}"> <button type="button" title="Edit user" class="btn_link btn-primary"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>
							      </button> </a>
							    </td>
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
