<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : Logs pages</title>
<jsp:include page="header.jsp"></jsp:include>
</head>
<body>
	<div class="container">
		<div class="row" style="padding-left: 150px;">
		<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
				    <li><a href="logspage"><i class="fa fa-folder-open" aria-hidden="true"></i> Logs </a></li>
					 <li class="breadcrumb-item active" aria-current="page"> Page</li>
				</ol>
			</nav>
			<div class="col-sm-10">
				<div id="tableDivId">
				  <jsp:include page="logspagecontent.jsp"></jsp:include>
				</div>
			</div>
		</div>
		<div style="padding-top: 100px;"></div>
	</div>
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
<script type="text/javascript">
$(document).ready(function(){
	  $("#tab9").addClass("active");
	  $("#tab1").removeClass("active");
});

/** $(document).ready(function () {
    // will call refreshPartial every 10 seconds
    setInterval(function(){ 
    	refreshPartial('logspagecontent');
    }, 10000);

}); */
</script>
</html>
