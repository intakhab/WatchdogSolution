<script type="text/javascript">
function confirmMsg(msg){
	
	var message="<i class='fa fa-warning aria-hidden='true' style='font-size:35px;color:red'></i>&nbsp;&nbsp;<span class='error'>You have changed "+msg+" timing !!! <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; WatchDog will be re-started automcatically to get the latest value!<span>";
	   $('<div></div>').appendTo('body').html('<div><h6>'+message+'</h6></div>')
	    .dialog({
	        modal: true, 
	        title: 'Confirmation',
	        zIndex: -1, 
	        autoOpen: true,
	        width: 'auto', 
	        resizable: false,
	        buttons: {
	            Yes: function () {
	            	$("#cronHit").val(true);
	                document.getElementById("configForm").submit();
	            },
	            No: function (){
	                $(this).dialog("close");
	            }
	        }
	    });
	
}
/****
 * This will fire cron error
 */
function cronError(){
	var message="<i class='fa fa-info-circle' aria-hidden='true' style='font-size:35px;color:red'></i>&nbsp;&nbsp;Input Cron Value is incorrect Please put one sapce in between characters like.. * * * * * *  ";
	   $('<div></div>').appendTo('body').html('<div><h6>'+message+'</h6></div>')
	    .dialog({
	        modal: true, 
	        title: 'Information ',
	        zIndex: -1,
	        width: 'auto', 
			cache : false,
	        autoOpen: true,
	        buttons: { 
	           OK: function () {
                $(this).dialog("close");

               }
	        }
	    });
	
}
/***
 * filebatch hide and show
 */
function fbatch(){
	var fr=$("#stopFileRunId").val();
	 if(fr=="true"){
		 $('#fbatch').hide();
	 }else{
		 $('#fbatch').show();
	 }
}
/***
 * Fin batch hide and show
 */
function finbatch(){
	var fr=$("#stopBatchRunId").val();
	 if(fr=="true"){
		 $('#finbatch').hide();
	 }else{
		 $('#finbatch').show();
	 }
}

function bulkbatch(){
	var fr=$("#stopBulkBatchId").val();
	 if(fr=="true"){
		 $('#bulkbatch').hide();
	 }else{
		 $('#bulkbatch').show();
	 }
}

function sobatch(){
	var fr=$("#stopSoBatchRunId").val();
	 if(fr=="true"){
		 $('#sobatch').hide();
	 }else{
		 $('#sobatch').show();
	 }
}
/***
 * Edi batch hide and show
 */
function edibatch(){
	var fr=$("#stopNonEdiBatchRunId").val();
	 if(fr=="true"){
		 $('#edibatch').hide();
	 }else{
		 $('#edibatch').show();
	 }
}
/***
 * Only consider number
 */
function validateNumber(event) {
    var key = window.event ? event.keyCode : event.which;
    if (event.keyCode === 8 || event.keyCode === 46) {
        return true;
    } else if ( key < 48 || key > 57 ) {
        return false;
    } else {
    	return true;
    }
};		
function chckTab(id){
	$("#filePathId1").removeClass("active");
	$("#batchPathId1").removeClass("active");
	$("#billConfigId1").removeClass("active");
	$("#fileConfigId1").removeClass("active");
	$("#planIdTab1").removeClass("active");
	$("#loadConfigId1").removeClass("active");
	$("#bulkId1").removeClass("active");
	$("#"+id).addClass("active");
	$("#tabId").val(id);
 };

$(document).ready(function() {
    $('.dig').keypress(validateNumber);//Number consider
    //Cron validation fire
	$(".msgc" ).change(function() {
        var idx=$(this).attr("id");
		$.ajax({
			type : "GET",
			url : 'cronvalidator?param='+$(this).val(),
			cache : false,
			async : true,
			success : function(data) {
				if(data==false){
					$("#"+idx).css("background-color", "red");
					 cronError();
	                 $(":submit").attr("disabled", true);
				}else{
				    $(":submit").removeAttr("disabled");
					$("#"+idx).css("background-color", "white");
				}
	             
			}
		});
	});

	$("#stopFileRunId").change(function(e) {
		fbatch();
	});
	$("#stopNonEdiBatchRunId").change(function(e) {
		edibatch();
	});
	$("#stopSoBatchRunId").change(function(e) {
		sobatch();
	});
	$("#stopBatchRunId").change(function(e) {
		finbatch();
	});
	$("#stopBulkBatchId").change(function(e) {
		bulkbatch();
	});
	
	$("#tab2").addClass("active");
	$("#tab1").removeClass("active");
	$("#re-loadId").click(function(e) {
		window.location.href="configfile";

	});
	 
    $("#emailCheckedId").click(function(e) {
		if ($('#emailCheckedId').prop('checked')) {
			$('#emailCheckedTextAreaId').show();
			$('#toWhomEmailId').prop('required',true);
		} else {
			$('#emailCheckedTextAreaId').hide();
			$('#toWhomEmailId').val("");
		    $('#toWhomEmailId').prop('required',false);
		}
	});
    
    $("#autoPilotId").click(function(e) {
		if ($('#autoPilotId').prop('checked')) {
			$('#isAutoPilotDiv').show();
			$('#autoPilotCronId').prop('required',true);
		} else {
			$('#isAutoPilotDiv').hide();
		    $('#autoPilotCronId').prop('required',false);
		}
	});
    
    $("#reportsId").click(function(e) {
		if ($('#reportsId').prop('checked')) {
			$('#isReportsDiv').show();
			$('#reportsCronId').prop('required',true);
		} else {
			$('#isReportsDiv').hide();
		    $('#reportsCronId').prop('required',false);
		}
	});
    
  
    
    $('#submitButtonId').click(function () {
        $('input:invalid').each(function () {
            // Find the tab-pane that this element is inside, and get the id
            var $closest = $(this).closest('.tab-pane');
            var id = $closest.attr('id');
            // Find the link that corresponds to the pane and have it show
            $('.nav a[href="#' + id + '"]').tab('show');
			$("#filePathId1").removeClass("active");
			$("#batchPathId1").removeClass("active");
			$("#billConfigId1").removeClass("active");
			$("#fileConfigId1").removeClass("active");
			$("#loadConfigId1").removeClass("active");
			$("#planIdTab1").removeClass("active");
			$("#bulkId1").removeClass("active");
			$("#"+id+"1").addClass("active");
				// Only want to do it once
				return false;
			});
		});
});//End

$(document).ready(function() {
	    var max_fields      = 15; //maximum input boxes allowed
	    var wrapper         = $(".input_fields_wrap"); //Fields wrapper
	    var add_button      = $(".add-more"); //Add button ID

	    var x = 1; //initlal text box count
	    $(add_button).click(function(e){ //on add input button click
	        e.preventDefault();
	        if(x < max_fields){ //max input box allowed
	            x++; //text box increment
	            $(wrapper).append('<div class="form-group"><div class="entry input-group"><label for="action_id"></label><input name="supportsAPI['+x+']" id="supportsAPI['+x+']" type="text" class="form-control input-md" required="required"/><span class="input-group-btn"><button class="btn btn-danger remove_field" type="button"><span class="glyphicon glyphicon-minus"></span></button></span></div></div>'); //add input box
	           // legnth++;
	        }
	    });
	    
	    $(wrapper).on("click",".remove_field", function(e){ //user click on remove text
	           e.preventDefault(); 
	           $(this).parent('span').parent('div').remove();x--;
	    })
	    
	   
});//end
		
$(document).ready(function() {
		    var max_fields      = 15; //maximum input boxes allowed
		    var wrapper         = $(".input_fields_wrap1"); //Fields wrapper
		    var add_button      = $(".add-more1"); //Add button ID
		    var x = 1; //initlal text box count
		    $(add_button).click(function(e){ //on add input button click
		        e.preventDefault();
		        if(x < max_fields){ //max input box allowed
		            x++; //text box increment
		            $(wrapper).append('<div class="form-group"><div class="entry input-group"><label for="action_id"></label><input name="optSupportsAPI['+x+']" id="optSupportsAPI['+x+']" type="text" class="form-control input-md" required="required"/><span class="input-group-btn"><button class="btn btn-danger remove_field1" type="button"><span class="glyphicon glyphicon-minus"></span></button></span></div></div>'); //add input box
		           // legnth++;
		        }
		    });
		    
		    $(wrapper).on("click",".remove_field1", function(e){ //user click on remove text
		           e.preventDefault(); 
		           $(this).parent('span').parent('div').remove();x--;
		    })
 
		    window.setTimeout(function() {
		        $(".alert").fadeTo(500, 0).slideUp(500, function(){
		            $(this).remove(); 
		          //  window.location.href='configfile?tabId='+$("#tabIds").val();
		        });
		    }, 1000);
		
		
  }); //End
		//load
$(document).ready(function() {
		    var max_fields      = 15; //maximum input boxes allowed
		    var wrapper         = $(".input_fields_wrap2"); //Fields wrapper
		    var add_button      = $(".add-more2"); //Add button ID
		    var x = 1; //initlal text box count
		    $(add_button).click(function(e){ //on add input button click
		        e.preventDefault();
		        if(x < max_fields){ //max input box allowed
		            x++; //text box increment
		            $(wrapper).append('<div class="form-group"><div class="entry input-group"><label for="action_id"></label><input name="nonEdiCamSupportsAPI['+x+']" id="nonEdiCamSupportsAPI['+x+']" type="text" class="form-control input-md" required="required"/><span class="input-group-btn"><button class="btn btn-danger remove_field2" type="button"><span class="glyphicon glyphicon-minus"></span></button></span></div></div>'); //add input box
		           // legnth++;
		        }
		    });
		    
		    $(wrapper).on("click",".remove_field2", function(e){ //user click on remove text
		           e.preventDefault(); 
		           $(this).parent('span').parent('div').remove();x--;
		          // $(this).parent('div').remove(); 
		    })
		    
		
});
		
$(document).ready(function() {
		    var max_fields      = 15; //maximum input boxes allowed
		    var wrapper         = $(".input_fields_wrap3"); //Fields wrapper
		    var add_button      = $(".add-more3"); //Add button ID
		    var x = 1; //initlal text box count
		    $(add_button).click(function(e){ //on add input button click
		        e.preventDefault();
		        if(x < max_fields){ //max input box allowed
		            x++; //text box increment
		            $(wrapper).append('<div class="form-group"><div class="entry input-group"><label for="action_id"></label><input name="nonEdiCamWineSupportsAPI['+x+']" id="nonEdiCamWineSupportsAPI['+x+']" type="text" class="form-control input-md" required="required"/><span class="input-group-btn"><button class="btn btn-danger remove_field3" type="button"><span class="glyphicon glyphicon-minus"></span></button></span></div></div>'); //add input box
		           // legnth++;
		        }
		    });
		    
		    $(wrapper).on("click",".remove_field3", function(e){ //user click on remove text
		           e.preventDefault(); 
		           $(this).parent('span').parent('div').remove();x--;
		    })
		    
		
});

$(document).ready(function() {
    var max_fields      = 15; //maximum input boxes allowed
    var wrapper         = $(".input_fields_wrap4"); //Fields wrapper
    var add_button      = $(".add-more4"); //Add button ID
    var x = 1; //initlal text box count
    $(add_button).click(function(e){ //on add input button click
        e.preventDefault();
        if(x < max_fields){ //max input box allowed
            x++; //text box increment
            $(wrapper).append('<div class="form-group"><div class="entry input-group"><label for="action_id"></label><input name="soOrderSupportsAPI['+x+']" id="soOrderSupportsAPI['+x+']" type="text" class="form-control input-md" required="required"/><span class="input-group-btn"><button class="btn btn-danger remove_field4" type="button"><span class="glyphicon glyphicon-minus"></span></button></span></div></div>'); //add input box
           // legnth++;
        }
    });
    
    $(wrapper).on("click",".remove_field4", function(e){ //user click on remove text
           e.preventDefault(); 
           $(this).parent('span').parent('div').remove();x--;
    })
    

});

$(document).ready(function() {
    var max_fields      = 15; //maximum input boxes allowed
    var wrapper         = $(".input_fields_wrap5"); //Fields wrapper
    var add_button      = $(".add-more5"); //Add button ID
    var x = 1; //initlal text box count
    $(add_button).click(function(e){ //on add input button click
        e.preventDefault();
        if(x < max_fields){ //max input box allowed
            x++; //text box increment
            $(wrapper).append('<div class="form-group"><div class="entry input-group"><label for="action_id"></label><input name="fbPaySupportsAPI['+x+']" id="fbPaySupportsAPI['+x+']" type="text" class="form-control input-md" required="required"/><span class="input-group-btn"><button class="btn btn-danger remove_field5" type="button"><span class="glyphicon glyphicon-minus"></span></button></span></div></div>'); //add input box
        }
    });
    $(wrapper).on("click",".remove_field5", function(e){ //user click on remove text
           e.preventDefault(); 
           $(this).parent('span').parent('div').remove();x--;
    })
    
});

$(document).ready(function() {
    var max_fields      = 15; //maximum input boxes allowed
    var wrapper         = $(".input_fields_wrap6"); //Fields wrapper
    var add_button      = $(".add-more6"); //Add button ID
    var x = 1; //initlal text box count
    $(add_button).click(function(e){ //on add input button click
        e.preventDefault();
        if(x < max_fields){ //max input box allowed
            x++; //text box increment
            $(wrapper).append('<div class="form-group"><div class="entry input-group"><label for="action_id"></label><input name="bulkSupportsAPI['+x+']" id="bulkSupportsAPI['+x+']" type="text" class="form-control input-md" required="required"/><span class="input-group-btn"><button class="btn btn-danger remove_field6" type="button"><span class="glyphicon glyphicon-minus"></span></button></span></div></div>'); //add input box
        }
    });
    $(wrapper).on("click",".remove_field6", function(e){ //user click on remove text
           e.preventDefault(); 
           $(this).parent('span').parent('div').remove();x--;
    })
    
});
//
function isCronChange(){
	var changeFound=false;
	if($("#pollTimeId").val()!=$("#msg1").val()){
		confirmMsg(" File Dog F1 ");
		changeFound=true; 
	}
	if($("#finCronTimeG1Id").val()!=$("#msg2").val()){
		confirmMsg(" Fin G1 ");
		changeFound=true; 
	}
	if($("#finCronTimeG2Id").val()!=$("#msg3").val()){
		confirmMsg(" Fin G2 ")
		changeFound=true; 
	}
	if($("#finCronTimeG3Id").val()!=$("#msg4").val()){
		changeFound=true; 
		confirmMsg(" Fin G3 ")
	}
	if($("#finCronTimeG4Id").val()!=$("#msg5").val()){
		changeFound=true; 
		confirmMsg(" Fin G4 ")
	}
	if($("#soCronTimeG1Id").val()!=$("#msg6").val()){
		changeFound=true; 
		confirmMsg(" SO G1 ")
	}
	if($("#soCronTimeG2Id").val()!=$("#msg7").val()){
		changeFound=true; 
		confirmMsg(" SO G2 ")
	}
	if($("#nonEdiPoollingTimeG1Id").val()!=$("#msg8").val()){
		changeFound=true; 
		confirmMsg(" NonEdi G1 ")
	}
	if($("#nonEdiPoollingTimeW1Id").val()!=$("#msg9").val()){
		changeFound=true; 
		confirmMsg(" NonEdi W1 ")
	}
	if($("#nonEdiPoollingTimeW2Id").val()!=$("#msg10").val()){
		changeFound=true; 
		confirmMsg(" NonEdi W2 ")
	}
	if($("#bulkPoollingTimeG1Id").val()!=$("#msg11").val()){
		changeFound=true; 
		confirmMsg(" Bulk G1 ")
	}
	if($("#bulkPoollingTimeG2Id").val()!=$("#msg12").val()){
		changeFound=true; 
		confirmMsg(" Bulk G2 ")
	}
	
	if($("#autoPilotTimeId").val()!=$("#autoPilotCronId").val()){
		changeFound=true; 
		confirmMsg(" Auto Pilot ")
	}
	if($("#reportsTimeId").val()!=$("#reportsCronId").val()){
		changeFound=true; 
		confirmMsg(" Reports ")
	}
	
   return changeFound;
}
/***
 * Submit 
 */
$( "#configForm" ).submit(function( event ) {
	   event.preventDefault();
	   if(isCronChange()){
		   return;
	   }
   var message="<i class='fa fa-info' aria-hidden='true' style='font-size:35px;color:red'></i>&nbsp;&nbsp;Are you sure want to save the Settings Configuration?";
	     $('<div></div>').appendTo('body').html('<div><h6>'+message+'</h6></div>')
    .dialog({
        modal: true, title: 'Confirmation', zIndex: 10000, autoOpen: true,
        width: 'auto', 
        resizable: false,
        buttons: {
            Yes: function () {
                $(this).dialog("close");
            	$("#cronHit").val(false);
                document.getElementById("configForm").submit();

            },
            No: function () {                           		                      
                $(this).dialog("close");
            }
        },
        close: function (event, ui) {
            $(this).remove();
        }
    });
  });
  
  
$(window).bind("load", function() {
	if($("#cronHit").val()=="true"){
		restart();
	    //window.location.href='restartContext';
	}
	var tt='${param.tabId}';
	if(tt!=""){
		var tabsId= tt.substring(0, tt.length - 1);
        $("a[href='#"+tabsId+"']").trigger("click");
	}
	//
	if($("#checkBoxId").val().trim()==true || $("#checkBoxId").val().trim()=="true"){
	     $('#emailCheckedTextAreaId').show();
	     $('#emailCheckedId').prop('checked', true);
		 $('#toWhomEmailId').prop('required',true);

	}else{
   	     $('#emailCheckedTextAreaId').hide();
   	     $('#emailCheckedId').prop('checked' ,false)
		 $('#toWhomEmailId').prop('required',false);
	}
	
	if($("#autoPilotId").val()=="true"){
		if ($('#autoPilotId').prop('checked')) {
			$('#isAutoPilotDiv').show();
			$('#autoPilotCronId').prop('required',true);
		} else {
			$('#isAutoPilotDiv').hide();
		    $('#autoPilotCronId').prop('required',false);
		}
	}
	if($("#reportsId").val()=="true"){
		if ($('#reportsId').prop('checked')) {
			$('#isReportsDiv').show();
			$('#reportsCronId').prop('required',true);
		} else {
			$('#isReportsDiv').hide();
		    $('#reportsCronId').prop('required',false);
		}
	}
	
	//Validation
	fbatch();
	edibatch();
	sobatch();
	finbatch();
	bulkbatch();
	
}); 
</script>