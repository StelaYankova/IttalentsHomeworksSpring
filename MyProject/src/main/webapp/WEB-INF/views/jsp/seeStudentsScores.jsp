<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
#listOfStudentsOfGroup {
	position: absolute;
	top: 300px;
	right: 40px;
}

#divTable {
	position: absolute;
	top: 150px;
	left: 0px;
	width: 80%
}
</style>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<c:if test="${sessionScope.isTeacher == false}">
		<div class="navPath">
			<nav class="breadcrumb-nav">
				<ul class="breadcrumb">
					<li><a
						href="http://localhost:8080/MyProject/GetMainPageStudent">Home</a>
						<span class="divider"> <span class="accesshide "><span
								class="arrow_text"></span></span>
					</span></li>
					<li><a
						href="http://localhost:8080/MyProject/GetStudentsScoresServlet">Your
							scores</a> <span class="divider"> <span class="accesshide "><span
								class="arrow_text"></span>&nbsp;</span>
					</span></li>
				</ul>
			</nav>
		</div>
	</c:if>
	<c:if test="${sessionScope.isTeacher == true}">
		<div class="navPath">
			<nav class="breadcrumb-nav">
				<ul class="breadcrumb">
					<li><a
						href="http://localhost:8080/MyProject/GetMainPageTeacher">Home</a>
						<span class="divider"> <span class="accesshide "><span
								class="arrow_text"></span></span>
					</span></li>
					<li><a
						href="http://localhost:8080/MyProject/GetStudentsScoresServlet">See
							student's scores</a> <span class="divider"> <span
							class="accesshide "><span class="arrow_text"></span>&nbsp;</span>
					</span></li>
				</ul>
			</nav>
		</div>
	</c:if>
	<div id="pageWrapper">
		Choose group: <select id="chosenGroup" class="selectpicker">
			<option value="null">-</option>
			<c:if test="${sessionScope.isTeacher == false}">
				<c:forEach var="group" items="${user.groups}">
					<option value="${group.id}"><c:out value="${group.name}"></c:out></option>
				</c:forEach>
			</c:if>
			<c:if test="${sessionScope.isTeacher == true}">
				<c:forEach var="group" items="${applicationScope.allGroups}">
					<option value="${group.id}"><c:out value="${group.name}"></c:out></option>
				</c:forEach>
			</c:if>
		</select>
		<div id="divTable">
			<table id="resultTable" border="1"
				class="table table-striped table-bordered table-hover dataTables_wrapper form-inline dt-bootstrap"
				style="width: 60%">
				<thead class="wrapword">
					<tr>
						<td>Heading</td>
						<td>Opens</td>
						<td>Closes</td>
						<td>Teacher score</td>
						<td>Teacher comment</td>
					</tr>
				</thead>
				<tbody class="wrapword">
				</tbody>
			</table>
		</div>
		<ul id="listOfStudentsOfGroup" class="editable wrapwordLink"
			style="visibility: hidden; z-index: 1; height: 300px; width: 18%; overflow: hidden; overflow-y: scroll; overflow-x: scroll;"></ul>
	</div>
	<script>
		var table = $('#resultTable').DataTable({
			"aoColumnDefs" : [ {
				'bSortable' : true,
				'aTargets' : [ 0, 1, 2 ],
				'className' : "wrapword",
				"targets" : [ 0, 1, 2, 3 ]

			} ],
			"dom" : '<"top"l>rt<"bottom"ip><"clear">',
			"aoColumns" : [ {
				sWidth : '14%'
			}, {
				sWidth : '14%'
			}, {
				sWidth : '14%'
			}, {
				sWidth : '18%'
			}, {
				sWidth : '22%'
			} ],
			"lengthMenu" : [ 5, 10 ],
			"bDestroy" : true
		});
		function seeHomeworks(e, e1) {
			if (!$('#resultTable tbody').is(':empty')) {
				$("#resultTable tbody").empty();
			}
			var groupId = e;
			var studentId = e1;
			$
					.ajax({
						url : 'http://localhost:8080/MyProject/SeeAllHomeworksOfStudentByGroupServlet',
						type : 'GET',
						data : {
							"groupId" : groupId,
							"studentId" : studentId
						},
						dataType : 'json',
						success : function(response) {
							if ($(table).find("#tbody").html() !== 0) {
								$('#resultTable').DataTable().clear().draw();
							}
							for ( var i in response) {
								if (response === 'null') {
									$('#resultTable tbody').html(
											'no data available in table');
								} else {
									var opens = response[i].opens;
									var opensRep = opens.replace("T", " ");
									var closes = response[i].closes;
									var closesRep = closes.replace("T", " ");
									var hasStudentGivenMinOneTask = response[i].hasStudentGivenMinOneTask;

									if (hasStudentGivenMinOneTask === true) {
										var rowNode = table.row
												.add(

														[
																"<form action = 'http://localhost:8080/MyProject/GetHomeworkOfStudentServlet' method = 'GET'><input type = 'hidden' name = 'id' value = " + response[i].id+ "><input type = 'hidden' name = 'studentId' value = "+studentId+"><button type = 'submit' class = 'btn btn-link'>"
																		+ response[i].heading
																		+ "</button></form>",
																opensRep,
																closesRep,
																response[i].teacherScore
																		+ "/100",
																response[i].teacherComment ])
												.draw().node();
									} else {
										var rowNode = table.row
												.add(
														[
																"<form action = 'http://localhost:8080/MyProject/GetHomeworkOfStudentServlet' method = 'GET'><input type = 'hidden' name = 'id' value = " + response[i].id+ "><input type = 'hidden' name = 'studentId' value = "+studentId+"><button style= 'color:#620062' type = 'button' class = 'btn btn-link'>"
																		+ response[i].heading
																		+ "</button></form>",
																opensRep,
																closesRep,
																response[i].teacherScore
																		+ "/100",
																response[i].teacherComment ])
												.draw().node();
									}
								}
							}
						}
					});
		}
		$('#chosenGroup')
				.change(
						function(event) {
							if (!$('#resultTable tbody').is(':empty')) {
								$("#resultTable tbody")
										.html(
												'<tr><td colspan="5" style = "padding-left:16em ">No data available in table</td></tr>');
							}
							if (!$('#listOfStudentsOfGroup').is(':empty')) {
								$("#listOfStudentsOfGroup").empty();
							}
							var groupId = $(this).find(":selected").val();
							$
									.ajax({
										url : 'http://localhost:8080/MyProject/getAllStudentsOfGroupServlet',
										type : 'GET',
										data : {
											"chosenGroupId" : groupId
										},
										dataType : 'json',
										success : function(response) {
											for ( var i in response) {
												$('#listOfStudentsOfGroup')
														.append(
																"<li><button class = 'btn btn-link' onclick = 'seeHomeworks("
																		+ groupId
																		+ ","
																		+ response[i].id
																		+ ")'>"
																		+ response[i].username
																		+ "</button></li>");
												document
														.getElementById('listOfStudentsOfGroup').style.visibility = 'visible';
											}
										}
									});
						});
		function selectOption(index) {
			document.getElementById("chosenGroup").options.selectedIndex = index;
		}
		$(document).ready(function(e) {
			selectOption(0);
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
</body>
</html>