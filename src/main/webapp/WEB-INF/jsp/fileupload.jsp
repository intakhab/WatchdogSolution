<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : File Upload</title>
<jsp:include page="header.jsp"></jsp:include>
</head>
<body>

	<div class="container">
		<nav aria-label="breadcrumb">
			 <ol class="breadcrumb">
				     <li><a href="fileupload"><i class="fa fa-folder-open" aria-hidden="true"></i> File Upload</a></li>
					 <li class="breadcrumb-item active" aria-current="page"> Directory</li>
				</ol>
		</nav>
		<form method="POST" action="uploadfile" enctype="multipart/form-data">

			<div class="card">
				<br />
				<div style="padding-left: 20px;">
				${msg}
				
				</div>
				<br /> <br />
				<div class="col-sm-10">
				
				<table class="table-striped table" style='width: 50%'>

					<tr>
						<td><input type="file" name="file" /></td>
						<td><input type="submit" class="btn btn-primary"
							value="Upload File to Input Directory" /></td>
					</tr>

				</table>
				</div>

			</div>
		</form>
	</div>
<jsp:include page="footer.jsp"></jsp:include>

</body>
<script type="text/javascript">
$(document).ready(function(){
     $("#tab6").addClass("active");
     $("#tab1").removeClass("active");

});
</script>
</html>
