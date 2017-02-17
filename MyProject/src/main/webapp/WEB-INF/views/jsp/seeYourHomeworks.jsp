<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
.wrapword {
	white-space: -moz-pre-wrap !important; /* Mozilla, since 1999 */
	white-space: -webkit-pre-wrap; /*Chrome & Safari */
	white-space: -pre-wrap; /* Opera 4-6 */
	white-space: -o-pre-wrap; /* Opera 7 */
	white-space: pre-wrap; /* css-3 */
	word-wrap: break-word; /* Internet Explorer 5.5+ */
	word-break: break-all;
	white-space: normal;
}
#image{
	position:absolute;
	   left: 850px;
	
}
#divTable{
	position:absolute;
	top:150px;
	left:20px;
	width:50%;
}
</style>
<body>
	<%@ include file="navBarStudent.jsp"%>
<div id = "image">
     <img src="images/logo-black.png" class="img-rounded" width="380" height="236"> 
	</div>
	<br>
		<div id = "divTable">
	
	<table border="1"
			class="table table-striped table-bordered table-hover" id = "resultTable">
		<thead class="wrapword">
			<tr>
				<td >Heading</td>
				<td>Days left</td>
			</tr>
		</thead>
			<tbody class="wrapword">
				<c:forEach var="homework"
					items="${currHomeworksOfGroup}">
					<tr>
						<td><form action="./GetHomeworkServlet" method="GET">
							<input type="hidden" name="id" value='${homework.id}'>
								<button type="submit" class="btn btn-link">
									<c:out value="${homework.heading}" />
								</button>
							</form></td>
						<td>
							<c:if test="${homework.daysLeft ge 0}">
								<c:out value="${homework.daysLeft}" />
							</c:if> <c:if test="${homework.daysLeft lt 0}">
								<c:out value="upload time passed" />
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
</div>
</body>
<script>
$(document).ready(function() {
	var table = $('#resultTable');

	var table = $('#resultTable').DataTable({
		"aoColumnDefs" : [ {
			'bSortable' : true,
			'aTargets' : [0,1 ],
			'className': "wrapword", "targets": [ 0,1]
			
		} ],
	      "dom":'<"top"l>rt<"bottom"ip><"clear">',
		 "aoColumns": [
		               { sWidth: '4%' },
		              { sWidth: '4%' },
		              ],
		"lengthMenu" : [5,8],
		"bDestroy" : true
	});
});
$(function () {
    $.ajaxSetup({
      statusCode: {
        401: function () {
          location.href = '/MyProject/index';
        },
        403: function () {
	            location.href = '/MyProject/forbiddenPage';
	      },
	      500: function(){
	    	  location.href = '/MyProject/exceptionPage';
	      }
      }
    });
  });
</script>
</html>