<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : Response Folder</title>
<jsp:include page="header.jsp"></jsp:include>
</head>
<body>
	<div class="container">

		<div class="row" style="padding-left: 150px;">
		<nav aria-label="breadcrumb" style="width: 85%">
				<ol class="breadcrumb">
					<li><a href="responseout"><i class="fa fa-folder-open" aria-hidden="true"></i> Response Out</a></li>
					 <li class="breadcrumb-item active" aria-current="page"> Directory</li>
				</ol>
			</nav>
			<div class="col-sm-10">
			   <div id="tableDivId">
			    <jsp:include page="responseoutcontent.jsp"></jsp:include>
			   </div>
				
			</div>
		</div>
		<div style="padding-top: 100px;"></div>
		
	</div>
<jsp:include page="footer.jsp"></jsp:include>

</body>
<script type="text/javascript">
$(document).ready(function(){
     $("#tab1").removeClass("active");
     $("#tab4").addClass("active");
     var v='${param.msg}';
		if(v==''){
			$("#alertId").hide();
		}
});

/*$(document).ready(function () {
    // will call refreshPartial every 10 seconds
    setInterval(function(){ 
    	refreshPartial('responseoutcontent');
    }, 10000);
});*/

</script>
</html>
