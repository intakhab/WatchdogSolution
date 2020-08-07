<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : Failure Folder</title>
<jsp:include page="header.jsp"></jsp:include>
</head>
<body>
	<div class="container">
		<div class="row" style="padding-left: 50px;">
		<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
				     <li><a href="failuredir"><i class="fa fa-folder-open" aria-hidden="true"></i> Failure</a></li>
					 <li class="breadcrumb-item active" aria-current="page"> Directory</li>
				</ol>
			</nav>
			<div class="col-sm-10">
				  <div id='alertId' class='alert alert-success' data-dismiss='alert' aria-label='Close' role='alert'>${param.msg}
			       <span style='float:right;cursor: pointer;'>&times;</span></div>
			     <div id="tableDivId">
			        <jsp:include page="failuredircontent.jsp"></jsp:include>
			     </div>
			</div>
		</div>
	    <div style="padding-top: 100px;"></div>
	</div>
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
<script type="text/javascript">
$(document).ready(function(){
     $("#tab8").addClass("active");
     $("#tab1").removeClass("active");
     var v='${param.msg}';
		if(v==''){
			$("#alertId").hide();
		}
});

/* $(document).ready(function () {
    // will call refreshPartial every 10 seconds
    setInterval(function(){ 
    	refreshPartial('failuredircontent');
    }, 10000);

}); */

</script>
</html>
