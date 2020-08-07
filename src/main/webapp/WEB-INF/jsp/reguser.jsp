<!DOCTYPE html>
<%@ page errorPage="errorpage.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title>Watch Dog : User Registration</title>

<jsp:include page="header.jsp"></jsp:include>
<script type="text/javascript" src="/js/jquery.validate.min.js"></script>
</head>
<body>

	<div class="container">

		<div style="padding-left: 250px;">
			<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					<li><a href="reguser"><i class="fa fa-floppy-o" aria-hidden="true"></i> Save User</a></li>
					<li class="breadcrumb-item active" aria-current="page">
					 This credentials will be used into watchdog application for login</li>
				</ol>
			</nav>
			<div class="col-sm-6">
				<div>${msg}</div>

				<form:form name="form" role="form" method="POST" id="configForm"
					action="saveuser" modelAttribute="user">
					<div>
						<div class="form-group">
							<label for="email">Email</label>
							<form:input path="email" type="email" class="form-control"
								id="emailId" placeholder="Enter Email" required="required" />
									  <span id='message' class="error"></span>

						</div>

						<div class="form-group">
							<label for="username">User Name</label>
							<form:input path="username" class="form-control" id="usernameId"
								placeholder="User Name" required="required" />
						</div>
						<div class="form-group">
							<label for="userpass">User Password</label>
							<form:password path="userpass"  class="form-control" id="userpassId"
								placeholder="User password" required="required" />
						</div>
						<div class="form-group">
							<label >Retype password</label>
							<div class="input-group">
								<input type="password" name="confirmPassword" id="confirmPassword" placeholder="Confirm password"  class="form-control pwd"  required="required"> 
								<span class="input-group-btn">
									<button class="btn btn-default reveal" type="button">
										<span class="fa fa-eye"></span>
									</button>
								</span>
								
							</div>
							<label id="confirmPassword-error" class="error" for="confirmPassword"></label>
						</div>

					</div>
					<br />
					<br />
					<button type="submit" id="submitButtonId" class="btn btn-primary">
					<i class="fa fa-floppy-o" aria-hidden="true"></i> Save User</button>
					<button type="button" id="re-loadId" class="btn btn-success" onclick="load()">
					<i class="fa fa-refresh" aria-hidden="true"></i> Reload Page</button>


				</form:form>


			</div>
		</div>
	</div>

	
<script type="text/javascript">
$(document).ready(function(){
	  $("#tab10").addClass("active");
	  $("#tab1").removeClass("active");
});
function load(){
	window.location.href='reguser';
}
$("#emailId" ).change(function() {
	$.ajax({
		type : "GET",
		url : 'emailcheck?email='+$("#emailId").val(),
		cache : false,
		async : true,
		success : function(data) {
			if(data==true){
             $("#message").html("Duplicate email found. try with another email.");
            // $(':input[type="submit"]').prop('disabled', false);
             $(":submit").attr("disabled", true);

			}else{
			    $(":submit").removeAttr("disabled");
	             $("#message").html("");

			}
             
		}
	});
});

jQuery('#configForm').validate({
    rules : {
        password : {
            minlength : 5
        },
        confirmPassword : {
            minlength : 5,
            equalTo : "#userpassId"
        }
    }
});

</script>
<jsp:include page="footer.jsp"></jsp:include>

</body>
</html>
