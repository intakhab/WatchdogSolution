<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<table id="example" class="display" style="width: 100%">
	<thead>
		<tr>
			<th></th>
			<th>File Name</th>
			<th>Last Modified Date</th>
			<th></th>

		</tr>
	</thead>
	<tbody>
		<c:forEach items="${fileList}" var="dt">
			<tr>
				<td>${dt.id}</td>
				<td>${dt.fileName}</td>
				<td>${dt.lastModifiedDate}</td>

				<td>

					<div class="btn-group btn-group-sm" role="group"
						aria-label="Basic example">
						<A href="downloadfile/${dt.fileName}/temp" title="Download File"
							title='Download File'>
							<button type="button" class="btn_link btn-primary"><i class="fa fa-download" aria-hidden="true"></i></button>
						</A>
					</div>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
