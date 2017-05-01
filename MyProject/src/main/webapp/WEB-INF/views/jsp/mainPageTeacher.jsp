<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link href="<c:url value="css/mainPageTeacherCss.css" />" rel="stylesheet">
<title>Insert title here</title>
</head>
<style>
</style>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="http://localhost:8080/MyProject/GetMainPageTeacher">Home</a>
					<span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span> </span></li>
			</ul>
		</nav>
	</div><!-- mostRecentlyClosedHomeworksForTeacherMap -->
	<div id="pageWrapper">
		<h4 id = "pageTitle">
			<b><u>Top 10 most recently closed homeworks</u></b>
		</h4>
<div id = "tableAndStudents" >
		<div id="currTable" style = "">
				<div id="divTable">
					<table id="resultTable" border="1"
						class="table table-striped table-bordered table-hover">
						<thead class="wrapword">
							<tr>
								<th>Group</th>
								<th>Homework</th>
							</tr>
						</thead>
						<tbody class="wrapword">
						<c:forEach items="${sessionScope.mostRecentlyClosedHomeworks}"
							var="entry">
							<c:forEach items="${entry.value}" var="item" varStatus="loop">
								<tr>
									<td><c:out value="${entry.key.name}"></c:out></td>
									<td><button type = 'submit' class='btn btn-link' onclick = "chooseStudent('${item.heading}','${item.id}','${entry.key.id}')"><c:out value="${item.heading}"></c:out></button></td>
								</tr>
							</c:forEach>
							<br>
						</c:forEach>
					</tbody>
					</table>
				</div>
			</div>
			
			<div class="list">
		<h id = "listHeading">Students in chosen group:</h>
			<ul id="listOfStudentsOfGroup" class="editable"></ul>
		</div>
			
			
			</div>
			
</div>
</body>
<script>
function chooseStudent(homeworkName, homeworkId, groupId) {
	console.log(09)
	if (!$('#listOfStudentsOfGroup').is(':empty')) {
		$("#listOfStudentsOfGroup").empty();
	}
	if (!$('li#chosenHomeworkName').is(':empty')) {

		$("li#chosenHomeworkName").remove();
	}
	var homeworkName = homeworkName;
	$('.breadcrumb')
			.append(
					'<li id = "chosenHomeworkName">'
							+ homeworkName
							+ '<span class="divider"><span class="accesshide "><span class="arrow_text"></span></span></span></li>');

	$
			.ajax({
				url : './getAllStudentsOfGroupServlet',
				type : 'GET',
				data : {
					"chosenGroupId" : groupId,
					"homeworkId" : homeworkId
				},
				dataType : 'json',
				success : function(response) {
					var div = document
							.getElementById("listOfStudentsOfGroup");
					if (!$.trim(response)) {
						alert("There are no students in this group.");
					}
					for ( var i in response) {
						var hasStudentGivenMinOneTask = response[i].hasStudentGivenMinOneTask;
						if (hasStudentGivenMinOneTask == true) {
							$('#listOfStudentsOfGroup')
									.append(
											"<li type='square'><form action = './homeworkOfStudent'><input type = 'hidden' name = 'id' value = "+homeworkId +"><input type = 'hidden' name = 'studentId' value = "+response[i].id+"><button type = 'submit' class='btn btn-link'>"
													+ response[i].username
													+ "</button></form></li>");

							document
									.getElementById("listOfStudentsOfGroup").style.display = "block";
							document.getElementById('listHeading').style.display = 'block';

						} else {
							$('#listOfStudentsOfGroup')
									.append(
											"<li type='square'><form action = './homeworkOfStudent'><input type = 'hidden' name = 'id' value = "+homeworkId +"><input type = 'hidden' name = 'studentId' value = "+response[i].id+"><button type = 'button' title = 'Student has not uploaded the chosen homework' style= 'color:#620062' class='btn btn-link'>"
													+ response[i].username
													+ "</button></form></li>");
							document
									.getElementById("listOfStudentsOfGroup").style.display = "block";
							document.getElementById('listHeading').style.display = 'block';

						}
					}
				}
			});	
}
		$(document)
				.ready(
						function() {
							var table = $('#resultTable').DataTable({
								"aoColumnDefs" : [ {
									'className' : "wrapword",
									"targets" : [ 0, 1]

								} ],
								
								"dom" : '<"top"lp>rt<"clear">',
								"aoColumns" : [ {
									sWidth : '1%'
								}, {
									sWidth : '12%'
								}],
									"aoColumns" : [ {
										"bSortable" : true
									}, {
										"bSortable" : true
									}],
									"lengthMenu" : [ 5, 10, 15 ],
									"bDestroy" : true,
									"paging" : false
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
			});});	
		</script>
</html>