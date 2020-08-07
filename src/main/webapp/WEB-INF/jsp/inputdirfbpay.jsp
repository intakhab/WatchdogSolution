<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : FBPay Folder</title>
<jsp:include page="header.jsp"></jsp:include>
<jsp:include page="model.jsp"></jsp:include>
<script  src="/js/jquery-ui.js"></script>
<link rel="stylesheet" href="/css/jquery-ui.css">
</head>
<body>

	<div class="container">

		<div class="row" style="padding-left: 5px;">
		   <nav aria-label="breadcrumb" style="width: 100%">
				<ol class="breadcrumb">
					    <li><a href="inputdirfbpay"> <i class="fa fa-folder-open" aria-hidden="true"></i> FBPay</a></li>
					    <li class="breadcrumb-item active" aria-current="page"> Directory</li>
				</ol>
			</nav>
			<div class="col-sm-12">
						   <div id='alertId' class='alert alert-success' data-dismiss='alert' aria-label='Close' role='alert'>${param.msg}
			    <span style='float:right;cursor: pointer;'>&times;</span></div>
			
				<table id="example" class="display"
					style="width: 100%">
					<thead>
						<tr>
							<th></th>
							<th>File Name</th>
							<th>Last Modified Date</th>
							<th>#File Type</th>
							<th></th>
							
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${fileList}" var="dt">
							<tr>
								<td>${dt.id}</td>
								<td>${dt.fileName}</td>
								<td>${dt.lastModifiedDate}</td>
								<td>${dt.fileStatus}</td>
								<td>
								<a href="downloadfile/${dt.fileName}/fbpay"  title="Download File.">
								 
								<button type="button"  class="btn_link btn-primary"><i class="fa fa-download" aria-hidden="true"></i></button>
								</a>
                                <a  href="javascript:void(0)"  onclick="deleteFile('${dt.fileName}')">
                             	   <button type="button"  class="btn_link btn-danger"><i class="fa fa-trash" aria-hidden="true"></i></button>
                                 </a>
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
$(document).ready(function(){
	
     $("#tab3").addClass("active");
     $("#tab1").removeClass("active");
     var v='${param.msg}';
		if(v==''){
			$("#alertId").hide();
		}
	});
	
	function deleteFile(val) {	
		var message = "<div class='alert alert-danger'>Are you sure want to delete file? <br/> File will be removed from disk.</div>";
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
					window.location.href = "deletefilefrominput/fbpay/" + val;
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
