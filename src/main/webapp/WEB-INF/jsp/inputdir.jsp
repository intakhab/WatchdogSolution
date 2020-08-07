<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Watch Dog : Input Folder</title>
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
					    <li><a href="inputdir"><i class="fa fa-folder-open" aria-hidden="true"></i> File Dog</a></li>
					    <li class="breadcrumb-item active" aria-current="page"> Directory</li>
				</ol>
			</nav>
			<div class="col-sm-12">
				<div id='alertId' class='alert alert-success' data-dismiss='alert'
					aria-label='Close' role='alert'>${param.msg}
					<span style='float: right; cursor: pointer;'>&times;</span>
				</div>
				
				<div id="tableDivId">
				  <jsp:include page="inputdircontent.jsp"></jsp:include>
				</div>
				
			</div>
		</div>
		<div style="padding-top: 100px;"></div>
	</div>
<jsp:include page="footer.jsp"></jsp:include>
</body>
<script type="text/javascript">
$(document).ready(function(){
	$("#renameId").click(function(){
    	    var fileEndWith= $("#fileEndWithId").val();
    	    var fileNameId=$("#fileNameId").val();
    	    if(fileEndWith==''){
    		    var message = "File is not supporting, Please rename it <br/> Select File Type dropdown to rename file";
	    		$('<div></div>').appendTo('body').html(
	    				'<div><h6>' + message + '</h6></div>').dialog({
		    			modal : true,
		    			title : 'Information',
		    			zIndex : 10000,
		    			autoOpen : true,
		    			width : 'auto',
		    			resizable : false,
		    			buttons:{
			    			No : function() {
								$(this).dialog("close");
							}
		    			},
		    			close : function(event, ui) {
		    				$(this).remove();
		    	    	    $("#fileEndWithId").focus()
		
		    			}
	    		});
    	    }else{
    	    	window.location.href="renameinvalidfile/"+fileNameId+"/"+fileEndWith;
    	    }
	});
	
     $("#tab3").addClass("active");
     $("#tab1").removeClass("active");
     var v='${param.msg}';
		if(v==''){
			$("#alertId").hide();
		}
	});
	
	/****
	 *This method will delete file
	 **/
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
					window.location.href = "deletefilefrominput/in/" + val;
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
	
	
	/****
	 **/
	$(document).ready(function () {
	    // will call refreshPartial every 10 seconds
	    var time_interval=10000;
	    var listSize='${fileList.size()}';
	    if(listSize==1){
		    var time_interval=1000;
	    }
	    setInterval(function(){ 
	    	refreshPartial('inputdircontent');
	    }, time_interval);

	});

</script>
</html>
