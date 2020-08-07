<style type="text/css">

.pt-3-half {
    padding-top: 1.4rem;
}

</style>
<!-- Editable table -->
<div class="card">
    <h3 class="card-header text-center font-weight-bold text-uppercase py-4">Cron Table</h3>
    
    <div class="card-body">
        <div id="table" class="table-editable">
            <span class="table-add float-right mb-3 mr-2"><a href="#!" class="text-success"><i class="fa fa-plus fa-2x" aria-hidden="true">+</i></a></span>
            <table class="table table-bordered table-responsive-md table-striped text-center">
                <tr>
                    <th class="text-center">Cron Name</th>
                    <th class="text-center">Cron Expression</th>
                   
                    <th class="text-center">Remove</th>
                </tr>
                <tr>
                    <td class="pt-3-half" contenteditable="true">File Dog</td>
                    <td class="pt-3-half" contenteditable="true">60000</td>
                   
                    <td>
                        <span class="table-remove"><button type="button" class="btn btn-danger btn-rounded btn-sm my-0">Remove</button></span>
                    </td>
                </tr>
                <tr>
                    <td class="pt-3-half" contenteditable="true">NonEdi CaM</td>
                    <td class="pt-3-half" contenteditable="true">60000</td>
                   
                    <td>
                        <span class="table-remove"><button type="button" class="btn btn-danger btn-rounded btn-sm my-0">Remove</button></span>
                    </td>
                </tr>
                <tr>
                    <td class="pt-3-half" contenteditable="true">Gin G1</td>
                    <td class="pt-3-half" contenteditable="true">60000</td>
                   
                    <td>
                        <span class="table-remove"><button type="button" class="btn btn-danger btn-rounded btn-sm my-0">Remove</button></span>
                    </td>
                </tr>
                <tr>
                    <td class="pt-3-half" contenteditable="true">Fin G2</td>
                    <td class="pt-3-half" contenteditable="true">60000</td>
                   
                    <td>
                        <span class="table-remove"><button type="button" class="btn btn-danger btn-rounded btn-sm my-0">Remove</button></span>
                    </td>
                </tr>
                
                 <tr>
                    <td class="pt-3-half" contenteditable="true">Fin G3</td>
                    <td class="pt-3-half" contenteditable="true">60000</td>
                   
                    <td>
                        <span class="table-remove"><button type="button" class="btn btn-danger btn-rounded btn-sm my-0">Remove</button></span>
                    </td>
                </tr>
                
                <!-- This is our clonable table line -->
                <!-- <tr>
                    <td class="pt-3-half" contenteditable="true">Guerra Cortez</td>
                    <td class="pt-3-half" contenteditable="true">45</td>
                    <td class="pt-3-half" contenteditable="true">Insectus</td>
                    <td class="pt-3-half" contenteditable="true">USA</td>
                    <td class="pt-3-half" contenteditable="true">San Francisco</td>
                    <td class="pt-3-half">
                        <span class="table-up"><a href="#!" class="indigo-text"><i class="fa fa-long-arrow-up" aria-hidden="true"></i></a></span>
                        <span class="table-down"><a href="#!" class="indigo-text"><i class="fa fa-long-arrow-down" aria-hidden="true"></i></a></span>
                    </td>
                    <td>
                        <span class="table-remove"><button type="button" class="btn btn-danger btn-rounded btn-sm my-0">Remove</button></span>
                    </td>
                </tr> -->
               
               
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
var $TABLE = $('#table');
var $BTN = $('#export-btn');
var $EXPORT = $('#export');

$('.table-add').click(function () {
  var $clone = $TABLE.find('tr.hide').clone(true).removeClass('hide table-line');
  $TABLE.find('table').append($clone);
});

$('.table-remove').click(function () {
  $(this).parents('tr').detach();
});

$('.table-up').click(function () {
  var $row = $(this).parents('tr');
  if ($row.index() === 1) return; // Don't go above the header
  $row.prev().before($row.get(0));
});

$('.table-down').click(function () {
  var $row = $(this).parents('tr');
  $row.next().after($row.get(0));
});

// A few jQuery helpers for exporting only
jQuery.fn.pop = [].pop;
jQuery.fn.shift = [].shift;

$BTN.click(function () {
  var $rows = $TABLE.find('tr:not(:hidden)');
  var headers = [];
  var data = [];

  // Get the headers (add special header logic here)
  $($rows.shift()).find('th:not(:empty)').each(function () {
    headers.push($(this).text().toLowerCase());
  });

  // Turn all existing rows into a loopable array
  $rows.each(function () {
    var $td = $(this).find('td');
    var h = {};

    // Use the headers from earlier to name our hash keys
    headers.forEach(function (header, i) {
      h[header] = $td.eq(i).text();
    });

    data.push(h);
  });

  // Output the result
  $EXPORT.text(JSON.stringify(data));
});

</script>