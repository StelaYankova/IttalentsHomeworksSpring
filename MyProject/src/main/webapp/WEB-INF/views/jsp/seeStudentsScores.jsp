<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
<link href="<c:url value="css/seeStudentsScoresCss.css" />"
	rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link rel="icon" type="image/png" href="./images/favIcon.png">

</head>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<c:if test="${sessionScope.isTeacher == false}">
		<div class="navPath">
			<nav class="breadcrumb-nav">
				<ul class="breadcrumb" id="navPathList">
					<li><a href="./mainPageStudent">Home</a><span class="divider"><span
							class="accesshide "><span class="arrow_text"></span></span> </span></li>
					<li><a href="./studentsScoresByTeacher">Your scores</a><span
						class="divider"><span class="accesshide "><span
								class="arrow_text"></span></span> </span></li>
				</ul>
			</nav>
		</div>
	</c:if>
	<c:if test="${sessionScope.isTeacher == true}">
		<div class="navPath">
			<nav class="breadcrumb-nav">
				<ul class="breadcrumb">
					<li><a href="./mainPageTeacher">Home</a><span class="divider"><span
							class="accesshide "><span class="arrow_text"></span></span> </span></li>
					<li>See student's scores<span class="divider"><span
							class="accesshide "><span class="arrow_text"></span></span> </span></li>
				</ul>
			</nav>
		</div>
	</c:if>
	<div id="pageWrapper">
		<h4 id="pageTitle">
			<b><u>Scores of students</u></b>
		</h4>
		<div id="select">
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
		</div>
		<div id="tableAndStudents">
			<div id="currTable">
				<div id="divTable">

					<table id="resultTable" border="1"
						class="table table-striped table-bordered table-hover dataTables_wrapper form-inline dt-bootstrap">
						<thead class="wrapword">
							<tr>
								<th>Heading</th>
								<th>System score</th>
								<th>Teacher score</th>
								<th>Teacher comment</th>
							</tr>
						</thead>
						<tbody class="wrapword">
						</tbody>
					</table>
					<div id="studentAverageScore">
						<strong><u>Average teacher score: <span id="score"></span>/100
						</u></strong>
					</div>
					<div id="studentAverageSystemScore">
						<strong><u>Average system score: <span
								id="scoreSystem"></span>%
						</u></strong>
					</div>
				</div>
			</div>

			<div class="list">
				<h id="listHeading">Students in chosen group:</h>
				<ul id="listOfStudentsOfGroup" class="editable"></ul>
			</div>
		</div>
	</div>
</body>
<script>
		var table = $('#resultTable').DataTable({
			"aoColumnDefs" : [ {
				'className' : "wrapword",
				"targets" : [ 0, 1, 2 , 3 ]

			} ],
			"dom" : '<"top"lp>rt<"clear">',
			"aoColumns": [
{ "bSortable": true },
{ "bSortable": false },
{ "bSortable": false },{ "bSortable": false }
],
			"lengthMenu" : [5, 10, 15 ],
			"bDestroy" : true
		});
		function seeHomeworks(groupId, studentId, studentUsername) {
			document.getElementById('studentAverageScore').style.display = 'none';

			if (!$('#resultTable tbody').is(':empty')) {
				$("#resultTable tbody").empty();
			}
			var groupId = groupId;
			var studentId = studentId;
			var studentUsername = studentUsername;
			if (!$('li#chosenStudentUsername')
					.is(':empty')) {

				$("li#chosenStudentUsername")
						.remove();
					}
			 $('.breadcrumb')
				.append(
						'<li id = "chosenStudentUsername">'
								+studentUsername+'<span class="divider"><span class="accesshide "><span class="arrow_text"></span></span></span></li>');
			$
					.ajax({
						url : './seeHomeworksOfStudentByGroupForScoresByTeacher',
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
							if (!$.trim(response)) {
										alert("There are no homeworks in this group.");
							} 
							var averageScore = 0;
							var averageSystemScore = 0;
							var numberHomeworks = 0;
							for ( var i in response) {
									var hasStudentGivenMinOneTask = response[i].hasStudentGivenMinOneTask;
									averageScore += response[i].teacherScore;
									averageSystemScore += response[i].systemScore;
									numberHomeworks += 1;
									if (hasStudentGivenMinOneTask === true) {
										var rowNode = table.row
												.add(
														[
																"<form action = './getHomeworkOfStudentByTeacher' method = 'GET'><input type = 'hidden' name = 'homeworkId' value = " + response[i].id+ "><input type = 'hidden' name = 'studentId' value = "+studentId+"><button type = 'submit' class = 'wrapword btn btn-link'>"
																		+ response[i].heading
																		+ "</button></form>",response[i].systemScore +"%",
																response[i].teacherScore
																		+ "/100",
																response[i].teacherComment ])
												.draw().node();
									} else {
										var rowNode = table.row
												.add(
														[
																"<form action = './getHomeworkOfStudentByTeacher' method = 'GET'><input type = 'hidden' name = 'homeworkId' value = " + response[i].id+ "><input type = 'hidden' name = 'studentId' value = "+studentId+"><button title = 'Homework is not uploaded' style= 'color:#620062' type = 'button' class = 'wrapword btn btn-link'>"
																		+ response[i].heading
																		+ "</button></form>",response[i].systemScore +"%",
																response[i].teacherScore
																		+ "/100",
																response[i].teacherComment ])
												.draw().node();
									}
								}
								document.getElementById('divTable').style.display = 'block';
							var answer = (averageScore/numberHomeworks).toFixed(1);
							var answerSystemScore = (averageSystemScore/numberHomeworks).toFixed(1);
		if (!$('#score').is(':empty')) {
								$("#score").empty();
							}
							if(answer === 'NaN'){
								document.getElementById("score").append(0);
							}else{
								document.getElementById("score").append(answer);
							}
							
							if (!$('#scoreSystem').is(':empty')) {
								$("#scoreSystem").empty();
							}
							if(answerSystemScore === 'NaN'){
								document.getElementById("scoreSystem").append(0);
							}else{
								document.getElementById("scoreSystem").append(answerSystemScore);
							}
							document.getElementById("studentAverageScore").style.display = "block";
						}
					});
		}
		$('#chosenGroup')
				.change(
						function(event) {
							document.getElementById('divTable').style.display = 'none';
							document.getElementById('studentAverageScore').style.display = 'none';
							document.getElementById('')
							if (!$('#score').is(':empty')) {
								$("#score").empty();
							}
							if (!$('#scoreSystem').is(':empty')) {
								$("#scoreSystem").empty();
							}
							if (!$('#resultTable tbody').is(':empty')) {
								$("#resultTable tbody")
										.html(
												'<tr><td colspan="5" style = "padding-left:16em ">No data available in table</td></tr>');
							}
							if (!$('#listOfStudentsOfGroup').is(':empty')) {
								$("#listOfStudentsOfGroup").empty();
							}
							if (!$('li#chosenStudentUsername').is(':empty')) {

								$("li#chosenStudentUsername").remove();
							}
							var groupId = $(this).find(":selected").val();
							if (groupId != 'null') {
								if (!$('li#chosenGroupName').is(':empty')) {
									$("li#chosenGroupName").remove();
									var groupName = $(this).find(":selected")
											.text();
									$('.breadcrumb')
											.append(
													'<li id = "chosenGroupName">'
															+groupName+'<span class="divider"><span class="accesshide "><span class="arrow_text"></span></span></span></li>');
								}
							} else {
								if (!$('li#chosenGroupName').is(':empty')) {
									$("li#chosenGroupName").remove();
								}
							}
							document.getElementById('listOfStudentsOfGroup').style.display = 'none';
							document.getElementById('listHeading').style.display = 'none';
							$
									.ajax({
										url : './getAllStudentsOfGroupServlet',
										type : 'GET',
										data : {
											"chosenGroupId" : groupId
										},
										dataType : 'json',
										success : function(response) {
											for ( var i in response) {
												$('#listOfStudentsOfGroup')
														.append(
																"<li type='square'><button class = 'wrapword btn btn-link' onclick = 'seeHomeworks("
																		+ groupId
																		+ ","
																		+ response[i].id
																		+ ","
																		+ "\""
																		+ response[i].username
																		+ "\""
																		+ ")'>"
																		+ response[i].username
																		+ "</button></li>");
											}
											if ($.trim(response)) {
												document
														.getElementById('listOfStudentsOfGroup').style.display = 'block';
												document.getElementById('listHeading').style.display = 'block';
											} else {
												alert("There are no students in this group.");
												document
														.getElementById('listOfStudentsOfGroup').style.display = 'none';
												document.getElementById('listHeading').style.display = 'none';
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