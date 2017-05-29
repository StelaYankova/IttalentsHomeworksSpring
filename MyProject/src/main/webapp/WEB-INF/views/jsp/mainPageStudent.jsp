<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="en"
	class=" js flexbox canvas canvastext webgl no-touch geolocation postmessage websqldatabase indexeddb hashchange history draganddrop websockets rgba hsla multiplebgs backgroundsize borderimage borderradius boxshadow textshadow opacity cssanimations csscolumns cssgradients cssreflections csstransforms csstransforms3d csstransitions fontface generatedcontent video audio localstorage sessionstorage webworkers applicationcache svg inlinesvg smil svgclippaths">

<head>
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<meta charset="utf-8">
<title>IT Talents</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link href="<c:url value="css/mainPageStudentCss.css" />"
	rel="stylesheet">
<link rel="icon" type="image/png" href="./images/favIcon.png">

</head>

<body>
	<%@ include file="navBarStudent.jsp"%>

	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a href="./mainPageStudent">Home</a> <span class="divider"><span
						class="accesshide "><span class="arrow_text"></span></span> </span></li>
			</ul>
		</nav>
	</div>
	<div id="pageWrapper">
		<h4 id="pageTitle">
			<b><u>Available homeworks for uploading</u></b>
		</h4>
		<div id="divTable">
			<table id="resultTable" border="1"
				class="table table-striped table-bordered table-hover">
				<thead class="wrapword">
					<tr>
						<th>Homework</th>
						<th>Days left</th>
					</tr>
				</thead>
				<tbody class="wrapword">
					<c:forEach items="${sessionScope.activeHomeworksOfStudent}"
						var="homework">
						<c:set var="passesOn"
							value="${fn:replace(homework.closingTime,'T', ' ')}" />
						<c:set var="passesOnTime"
							value="${fn:substring(homework.closingTime, 11, 16)}" />
						<tr>
							<td><form action="./seeChosenHomeworkPageOfStudentByStudent"
									method="GET">
									<input type="hidden" name="homeworkId" value='${homework.id}'>
									<button type="submit" class="wrapword btn btn-link">
										<c:out value="${homework.heading}" />
									</button>
								</form></td>
							<c:if test="${homework.daysLeft == 0}">
								<td><c:out
										value="${homework.daysLeft} days left (homework passes at ${passesOnTime})"></c:out></td>
							</c:if>
							<c:if test="${homework.daysLeft gt 0}">
								<td><c:out
										value="${homework.daysLeft} days left (until ${passesOn})"></c:out></td>
							</c:if>
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
			"bDestroy" : true,
			"bPaginate" : false,
			"ordering" : false,
			"bInfo" : false
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
				404 : function() {
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