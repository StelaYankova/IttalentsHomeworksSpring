<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
#divTable {
	position: absolute;
	top: 150px;
	left: 20px;
	width: 50%;
}
</style>
<body>
	<%@ include file="navBarStudent.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="http://localhost:8080/MyProject/GetMainPageStudent">Home</a>
					<span class="divider"> <span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li><a
					href="http://localhost:8080/MyProject/GetHomeworksOfGroupsServlet">Homeworks
						of chosen group</a> <span class="divider"> <span
						class="accesshide "><span class="arrow_text"></span>&nbsp;</span>
				</span></li>
			</ul>
		</nav>
	</div>
	<div id="pageWrapper">
		<br>
		<div id="divTable">
			<table border="1"
				class="table table-striped table-bordered table-hover"
				id="resultTable">
				<thead class="wrapword">
					<tr>
						<td>Heading</td>
						<td>Days left</td>
					</tr>
				</thead>
				<tbody class="wrapword">
					<c:forEach var="homework"
						items="${sessionScope.currHomeworksOfGroup}">
						<tr>
							<td><form action="http://localhost:8080/MyProject/GetHomeworkServlet" method="GET">
									<input type="hidden" name="id" value='${homework.id}'>
									<button type="submit" class="btn btn-link">
										<c:out value="${homework.heading}" />
									</button>
								</form></td>
							<td><c:if test="${homework.daysLeft ge 0}">
									<c:out value="${homework.daysLeft}" />
								</c:if> <c:if test="${homework.daysLeft lt 0}">
									<c:out value="upload time passed" />
								</c:if></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
						<%@ include file="footer.jsp"%>
	
</body>
<script>
	$(document).ready(function() {
		var table = $('#resultTable').DataTable({
			"aoColumnDefs" : [ {
				'bSortable' : true,
				'aTargets' : [ 0, 1 ],
				'className' : "wrapword",
				"targets" : [ 0, 1 ]
			} ],
			"dom" : '<"top"l>rt<"bottom"ip><"clear">',
			"aoColumns" : [ {
				sWidth : '4%'
			}, {
				sWidth : '4%'
			}, ],
			"lengthMenu" : [ 5, 8 ],
			"bDestroy" : true
		});
	});
	$(function() {
		$.ajaxSetup({
			statusCode : {
				401 : function() {
					location.href = '/MyProject/index';
				},
				403 : function() {
					location.href = '/MyProject/forbiddenPage';
				},
				500 : function() {
					location.href = '/MyProject/exceptionPage';
				}
			}
		});
	});
</script>
</html>