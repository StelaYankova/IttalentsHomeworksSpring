<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link href="<c:url value="css/addOrRemoveStudentByGroupCss.css" />"
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
				<li>Add or remove student<span class="divider"><span
						class="accesshide "><span class="arrow_text"></span></span> </span></li>
			</ul>
		</nav>
	</div>
	<c:if test="${not empty sessionScope.invalidFields}">
		<c:if test="${not sessionScope.invalidFields}">
			<div class="alert alertAllPages alert-success" id="alert">
				<strong>Success!</strong> Student has been added successfully
			</div>
		</c:if>
	</c:if>
	<div id="pageWrapper">
		<div class="addStudentToGroupDiv">
			<div class="ui-widget">
				<div id="formLargeScreen">
					<form action="./addStudent" method="POST" class="form-inline"
						id="addStudentToGroupForm">
						<c:if test="${not empty sessionScope.invalidFields}">
							<c:if test="${sessionScope.invalidFields}">
								<p class="input-invalid-addStudentToGroup">You have invalid
									fields</p>
							</c:if>
						</c:if>
						<c:if test="${not empty sessionScope.emptyFields}">
							<c:if test="${sessionScope.emptyFields}">
								<p class="input-invalid-addStudentToGroup">You cannot have
									empty fields</p>
							</c:if>
						</c:if>
						<c:if test="${not empty sessionScope.doesStudentExist}">
							<c:if test="${not sessionScope.doesStudentExist}">
								<p id="studentMsg" class="input-invalid-addStudentToGroup">Student
									does not exist</p>
							</c:if>
							<c:if test="${not empty sessionScope.isStudentInGroup}">
								<c:if test="${sessionScope.doesStudentExist}">
									<c:if test="${sessionScope.isStudentInGroup}">
										<p id="studentMsg" class="input-invalid-addStudentToGroup">Student
											is already in group</p>
									</c:if>
								</c:if>
							</c:if>
						</c:if>
						<c:if test="${not empty sessionScope.validGroups}">
							<c:if test="${not sessionScope.validGroups}">
								<p id="groupsMsg" class="input-invalid-addStudentToGroup">Group
									does not exist</p>
							</c:if>
						</c:if>
						<p id="groupMsg" class="input-invalid-addStudentToGroup"></p>
						<p id="studentMsg" class="input-invalid-addStudentToGroup"></p>
						<div class="form-group">
							<label class="control-label">Choose group:</label> <select
								class="selectpicker form-control chosenGroup" name="chosenGroup">
								<option value="null">-</option>
								<c:forEach var="group" items="${applicationScope.allGroups}">
									<option value="${group.id}"><c:out
											value="${group.name}"></c:out></option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group" class="studentSearch">
							<label class="control-label">Choose student:</label> <input
								class="form-control searchStudents" maxlength="15"
								name="selectedStudent" placeholder="Enter student"
								value="${sessionScope.chosenUsernameTry}" />
						</div>
						<div class="form-group" id="addButtonDiv">
							<input type="submit" class=" form-control btn btn-default"
								value="Add">
						</div>
					</form>
				</div>
			</div>
		</div>
		<br> <br>

		<div id="listHeadingAndStudents">
			<div id="listOfStudentsOfGroupHeading"></div>
			<div id="listOfStudentsOfGroup">
				<ul class="editable list-group"></ul>
			</div>
		</div>
		<c:if test="${not empty sessionScope.invalidFields}">
			<c:remove var="invalidFields" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.emptyFields}">
			<c:remove var="emptyFields" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.doesStudentExist}">
			<c:remove var="doesStudentExist" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.isStudentInGroup}">
			<c:remove var="isStudentInGroup" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.validGroups}">
			<c:remove var="validGroups" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.chosenUsernameTry}">
			<c:remove var="chosenUsernameTry" scope="session" />
		</c:if>
	</div>
</body>
<script>
	$('#addStudentToGroupForm')
			.submit(
					function(e) {
						e.preventDefault();
						var chosenGroupId = document.forms["addStudentToGroupForm"]["chosenGroup"].value;
						var chosenStudentUsername = document.forms["addStudentToGroupForm"]["selectedStudent"].value;
						var doesUserExist = true;
						var chosenStudentUsernameAlreadyInGroup = true;
						var chosenStudentUsernameEmpty = false;
						var chosenGroupEmpty = false;
						if (chosenGroupId == 'null') {
							chosenGroupEmpty = true;
						}
						if (!$('#alert').is(':empty')) {
							$("#alert").remove();
						}
						if (!$('#groupMsg').is(':empty')) {
							$("#groupMsg").empty();
						}
						if (!$('#studentMsg').is(':empty')) {
							$("#studentMsg").empty();
						}
						if (chosenGroupEmpty === true) {
							document.getElementById("groupMsg").append(
									"Choose group first");
							return false;
						}
						if (!chosenStudentUsername) {
							chosenStudentUsernameEmpty = true;
						}
						if (chosenStudentUsernameEmpty === true) {
							document.getElementById("groupMsg").append(
									"Student is empty");
							return false;
						}
						$
								.ajax({
									url : './doesUserExist',
									type : 'GET',
									data : {
										"chosenStudentUsername" : chosenStudentUsername
									},
									success : function(response) {
										if (!$('#studentMsg').is(':empty')) {
											$("#studentMsg").empty();
											doesUserExist = true;
										}
										$
												.ajax({
													url : './isChosenStudentAlreadyInGroup',
													type : 'GET',
													data : {
														"chosenGroupId" : chosenGroupId,
														"chosenStudentUsername" : chosenStudentUsername
													},
													success : function(response) {
														if (!$('#studentMsg')
																.is(':empty')) {
															$("#studentMsg")
																	.empty();
														}
														chosenStudentUsernameAlreadyInGroup = false;
													},
													error : function(data) {
														if (!$('#studentMsg')
																.is(':empty')) {
															$("#studentMsg")
																	.empty();
														}
														chosenStudentUsernameAlreadyInGroup = true;
														document
																.getElementById(
																		"studentMsg")
																.append(
																		"Student is already in group");
													}
												});
									},
									error : function(data) {
										if (!$('#studentMsg').is(':empty')) {
											$("#studentMsg").empty();
										}
										doesUserExist = false;
										document
												.getElementById("studentMsg")
												.append(
														"Student does not exist");
									}
								});
						$(document)
								.ajaxStop(
										function() {
											if (chosenStudentUsernameAlreadyInGroup === false
													&& doesUserExist === true) {
												document
														.getElementById(
																"addStudentToGroupForm")
														.submit();
											}
										});
					});
	function areYouSureRemove(e, username, groupId) {
		if (confirm("Are you sure, that you want to remove this student from the group?")) {
			$.ajax({
				type : 'POST',
				url : "./removeStudentFromGroup",
				data : {
					"chosenStudentUsername" : username,
					"chosenGroupId" : groupId
				},
				success : function(data) {
					if (!$('#listOfStudentsOfGroup').is(':empty')) {
						$("#listOfStudentsOfGroup").empty();
					}
					if (!$('#listOfStudentsOfGroupHeading').is(':empty')) {
						$("#listOfStudentsOfGroupHeading").empty();
					}
					getStudents(groupId);
					$(".input-invalid-addStudentToGroup").empty();
					alert('Student has been removed successfully!')
				},
			});
		} else {
			return false;
		}
	}

	$(document)
			.ready(
					function() {
						$('.chosenGroup')
								.change(
										function(event) {
											if (!$('#listOfStudentsOfGroup')
													.is(':empty')) {
												$("#listOfStudentsOfGroup")
														.empty();
												document
														.getElementById('listOfStudentsOfGroup').style.visibility = 'hidden';

											}
											$(
													".input-invalid-addStudentToGroup")
													.empty();
											if (!$(
													'#listOfStudentsOfGroupHeading')
													.is(':empty')) {
												$(
														"#listOfStudentsOfGroupHeading")
														.empty();
												document
														.getElementById('listOfStudentsOfGroupHeading').style.visibility = 'hidden';

											}
											if (!$('#alert').is(':empty')) {
												$("#alert").remove();
											}
											var groupId = $(this).find(
													":selected").val();
											getStudents(groupId);
										});
						$(function() {
							var availableTags = new Array();
							<c:forEach items="${applicationScope.allStudents}" var="student">
							availableTags.push('${student.username}');
							</c:forEach>
							$(".searchStudents").autocomplete({
								source : availableTags,
								messages : {
									noResults : '',
									results : function() {
									}
								}
							});
						});
					});
	function getStudents(groupId) {
		$
				.ajax({
					url : './getAllStudentsOfGroupRemoveStudent',
					type : 'GET',
					data : {
						"chosenGroupId" : groupId
					},
					dataType : 'json',
					success : function(response) {
						for ( var i in response) {
							$('#listOfStudentsOfGroup')
									.append(
											"<li class = 'list-group-item'>"
													+ response[i].username
													+ "<button type='button'class = 'btn btn-xs'  onclick = 'areYouSureRemove(this,\""
													+ response[i].username
													+ "\",\""
													+ groupId
													+ "\")' ><span  class='badge glyphicon glyphicon-remove'>"
													+ " "
													+ "</span></button></li>");
						}
						document.getElementById('listOfStudentsOfGroup').style.visibility = 'visible';
						if ($('#listOfStudentsOfGroup').is(':empty')) {
							document.getElementById(
									'listOfStudentsOfGroupHeading').append(
									'There are no students in this group.');
						} else {
							document.getElementById(
									'listOfStudentsOfGroupHeading').append(
									'Students in chosen group:');

						}
						document.getElementById('listOfStudentsOfGroupHeading').style.visibility = 'visible';
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