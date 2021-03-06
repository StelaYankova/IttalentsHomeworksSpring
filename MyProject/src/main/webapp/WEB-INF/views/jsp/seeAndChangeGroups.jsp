<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link href="<c:url value="css/seeAndChangeGroupsCss.css" />"
	rel="stylesheet">
<link rel="icon" type="image/png" href="./images/favIcon.png">

</head>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a href="./mainPageTeacher">Home</a><span class="divider"><span
						class="accesshide "><span class="arrow_text"></span></span> </span></li>
				<li>See groups<span class="divider"><span
						class="accesshide "><span class="arrow_text"></span></span> </span></li>
			</ul>
		</nav>
	</div>
	<c:if test="${not empty invalidFields}">
		<c:if test="${not invalidFields}">
			<div class="alert alertAllPages alert-success">
				<strong>Success!</strong> Group has been removed successfully
			</div>
		</c:if>
	</c:if>
	<div id="pageWrapper">
		<h4 id="pageTitle">
			<b><u>All groups</u></b>
		</h4>

		<br>
		<div id="divTable">
			<a href="./createGroup" id="createButtonPosition"
				class="glyphicon glyphicon-plus btn-primary btn " type="submit"><b><font
					size="3"> Create group</font></b></a>
			<table id="resultTable" border="1"
				class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th>Name</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="group" items="${applicationScope.allGroups}">
						<tr>
							<td><div id="tdContent">
									<form action="./updateGroup" method="GET" id="updateGroupForm">
										<input type="hidden" name="groupId" value="${group.id}">
										<button class="btn btn-link" type="submit">
											<c:out value="${group.name}"></c:out>
										</button>
									</form>
									<form action="./removeGroup" method="POST" id="removeGroupForm">
										<input type="hidden" name="groupId" value="${group.id}">
										<button type="submit"
											class="glyphicon glyphicon-remove btn btn-default btn-xs"
											onclick="javascript:return confirm('Are you sure you want to remove this group permanently?')"></button>
									</form>
								</div></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<c:if test="${not empty invalidFields}">
		<c:remove var="invalidFields" scope="session" />
	</c:if>
</body>
<script>
	$(document).ready(function() {
		var table = $('#resultTable').DataTable({
			"aoColumnDefs" : [ {
				'bSortable' : false,
				'aTargets' : [ 0, 1 ],
				'className' : "wrapword",
				"targets" : [ 0 ],
			} ],
			"dom" : '<"top"l>rt<"bottom"ip><"clear">',
			"aoColumns" : [ {
				sWidth : '2%'
			}],
			"lengthMenu" : [ 5 ],
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