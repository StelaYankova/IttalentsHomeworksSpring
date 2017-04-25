<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
<link href="<c:url value="css/seeYourHomeworksCss.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">

</head>
<body>
	<%@ include file="navBarStudent.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="./GetMainPageStudent">Home</a><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li><c:out
						value="${sessionScope.chosenGroupName}"></c:out><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
			</ul>
		</nav>
	</div>
	<div id="pageWrapper">
			<h4 id = "pageTitle"><b><u>Your homeworks</u></b></h4>
	
		<div id="divTable">
			<table border="1"
				class="table table-striped table-bordered table-hover"
				id="resultTable">
				<thead class="wrapword">
					<tr>
						<th>Heading</th>
						<th>Days left</th>
					</tr>
				</thead>
				<tbody class="wrapword">
					<c:forEach var="homework"
						items="${sessionScope.currHomeworksOfGroup}">
						<tr>
							<td><form action="./GetHomeworkServlet" method="GET">
									<input type="hidden" name="id" value='${homework.id}'>
									<button type="submit" class="btn btn-link">
										<c:out value="${homework.heading}" />
									</button>
								</form></td>
							<td><c:set var="passesOn" value="${fn:replace(homework.closingTime,'T', ' ')}" />
							<c:if test="${homework.daysLeft ge 0}">
									<c:out value="${homework.daysLeft} (until ${passesOn})" />
								</c:if> <c:if test="${homework.daysLeft lt 0}">
								
									<c:out value="upload time passed on ${passesOn}" />
								</c:if></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	
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
				sWidth : '1%'
			}, {
				sWidth : '3%'
			}, ],
			"lengthMenu" : [ 5, 8 ],
			"bDestroy" : true,
			"bPaginate" : false,
			"ordering": false,
			"bInfo": false
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
				},404 : function(){
					location.href = '/MyProject/pageNotFoundPage';
				},
				500 : function() {
					location.href = '/MyProject/exceptionPage';
				}
			}
		});
	});
</script>
</html>