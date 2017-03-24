<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<c:url value="css/homeworkOfStudentCss.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">

<title>Insert title here</title>
</head>
<body>
	<%@ include file="navBarTeacher.jsp"%>
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
						class="accesshide "><span class="arrow_text"></span></span>
				</span></li>
				<li><c:out value = "${sessionScope.chosenGroupName}"></c:out><span class="divider"> <span
						class="accesshide "><span class="arrow_text"></span>&nbsp;</span>
				</span></li> 
				<li><c:out value = "${sessionScope.currHomework.homeworkDetails.heading}"></c:out><span class="divider"> <span
						class="accesshide "><span class="arrow_text"></span>&nbsp;</span>
				</span></li>
				<li><c:out value = "${sessionScope.currStudentUsername}"></c:out><span class="divider"> <span
						class="accesshide "><span class="arrow_text"></span>&nbsp;</span>
				</span></li>
			</ul>
		</nav>
	</div>
	<c:if test="${not empty invalidFields}">
			<c:if test="${not invalidFields}">
				<div class="alert alertAllPages alert-success">
					<strong>Success!</strong> Group has been added successfully
				</div>
			</c:if>
		</c:if>
	<div id="pageWrapper">
		<div id="currHomework">
			<br>
			<div id="downloadHomeworkForm">
				<form action="./ReadHomeworkServlet" method="GET">
					<input type='hidden'
						value='${sessionScope.currHomework.homeworkDetails.tasksFile}'
						name='fileName'> <strong>You can download tasks
						<button class='btn btn-link btn-xs' type='submit'>
							<b>here</b>
						</button>
					</strong>
				</form>
			</div>
		<div id="divTable">
				<table id="tasksTable" 
					class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<td>Tasks</td>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="i" begin="1"
							end="${sessionScope.currHomework.homeworkDetails.numberOfTasks}">
							<tr>
								<td>
									<button id="seeTaskSolutionButton"
										class="btn btn-primary btn-sm" type="submit"
										onclick="seeTaskSolution('${i}')">
										<c:out value="Task ${i}" />
									</button>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<c:if test="${not empty sessionScope.invalidFields}">
				<c:if test="${sessionScope.invalidFields}">
					<p class="input-invalid-or-empty">You have invalid fields</p>
				</c:if>
			</c:if>
				<form action="./UpdateTeacherGradeAndCommentServlet" method="POST"
				id="UpdateTeacherGradeAndCommentForm" accept-charset="UTF-8">
				<div class="block">
					<label><b>Teacher grade:</b></label> <span class="col-sm-4">
						<input type="number" class="form-control" min=0 max=100 id="grade"
						value="${sessionScope.currHomework.teacherGrade}" name="grade" />
					</span>
				</div>
				<c:if test="${not empty sessionScope.GradeTooLong}">
					<c:if test="${sessionScope.GradeTooLong}">
						<p id="gradeMsg" class="invalidData">Grade - between 1 and 100</p>
					</c:if>
				</c:if>
				<c:if test="${not empty sessionScope.validGrade}">
					<c:if test="${not sessionScope.validGrade}">
						<p id="gradeMsg" class="invalidData">Grade - between 1 and 100</p>
					</c:if>
				</c:if>
				<p id="gradeMsg" class="invalidData"></p>
				<div class="block">
					<label><b>Teacher comment:</b></label>&nbsp; <br>
					<textarea class="form-control" id="textareaComment" maxlength="250"
						name="comment"><c:out
							value="${sessionScope.currHomework.teacherComment}"></c:out></textarea>
					<c:if test="${not empty sessionScope.validComment}">

						<c:if test="${not sessionScope.validComment}">
							<p id="textareaCommentMsg" class="invalidData">Invalid
								comment</p>
						</c:if>
					</c:if>
					<p id="textareaCommentMsg" class="invalidData"></p>
				</div>
				<div class="">
					<input id="saveButton" type="submit" class="btn btn-default"
						value="Save">
				</div>
			</form>
		</div>

		<div id="solution">
		<div id="taskUpload"></div>
			<textarea id="currTaskSolution" disabled="disabled"
				class="form-control" cols="30" rows="27">
	</textarea>
		</div>
	</div>
	<c:if test="${not empty sessionScope.invalidFields}">
			<c:remove var="invalidFields" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.GradeTooLong}">
			<c:remove var="GradeTooLong" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.validGrade}">
			<c:remove var="validGrade" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.validComment}">
			<c:remove var="validComment" scope="session" />
		</c:if>
	<script>
		$('#UpdateTeacherGradeAndCommentForm')
				.submit(
						function(e) {
							e.preventDefault();
							var grade = document.forms["UpdateTeacherGradeAndCommentForm"]["grade"].value;
							var textareaComment = document.forms["UpdateTeacherGradeAndCommentForm"]["textareaComment"].value;
							var isGradeValid = true;
							var isCommentValid = true;
							if (!$('#gradeMsg').is(':empty')) {
								$("#gradeMsg").empty();
							}
							if (!$('#textareaCommentMsg').is(':empty')) {
								$("#textareaCommentMsg").empty();
							}
							if (grade == "") {
								document.getElementById("grade").value = 0;
							} else {
								if ((grade < 0) || (grade > 100)) {
									document.getElementById("gradeMsg").append(
											"Grade - between 0 and 100");
									isGradeValid = false;
								}
							}
							if (textareaComment.length > 250) {
								document
										.getElementById("textareaCommentMsg")
										.append("Comment - maximal 250 symbols");
								isCommentValid = false;
							}
							if (isGradeValid === true
									&& isCommentValid === true) {
								document.getElementById(
										"UpdateTeacherGradeAndCommentForm")
										.submit();
							} else {
								return false;
							}
						});
		$(document).ready(function() {
			var table = $('#tasksTable').DataTable({
				"autoWidth" : false,

				"aoColumnDefs" : [ {
					'bSortable' : false,
					'aTargets' : [ 0 ],
					'className' : "wrapword",

					"targets" : [ 0 ]
				} ],
				"dom" : '<"top"l>rt<"bottom"ip><"clear">',
				"aoColumns" : [ {
					'sWidth' : '140%'
				} ],

				// "lengthMenu" : [ 5 ], 
				"scrollY" : '36vh',
				"scrollCollapse" : true,

				"bDestroy" : true,
				"bPaginate" : false,
				"bInfo" : false
			});
		});
		function seeTaskSolution(taskNum) {
			$
					.ajax({
						url : 'http://localhost:8080/MyProject/ReadJavaFileServlet',
						data : {
							"taskNum" : taskNum,
						},
						type : 'GET',
						dataType : 'json',
						success : function(response) {
							var uploaded = response.uploadedOn;
							var uploadedRep = uploaded.replace("T", " ");
							$("#taskUpload").html(
									"Task " + taskNum + " uploaded on: "
											+ uploadedRep);
							$("#currTaskSolution").html(response.solution);
							document.getElementById("taskUpload").style.visibility = "visible";
							document.getElementById("currTaskSolution").style.visibility = "visible";
						}
					});
		}
		$(function() {
			$.ajaxSetup({
				statusCode : {
					401 : function() {
						location.href = '/MyProject/index';
					},
					403 : function() {
						console.log(1)
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
</body>
</html>