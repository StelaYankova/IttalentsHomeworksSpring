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
<link href="<c:url value="css/addStudentToGroupCss.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">

</head>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="./GetMainPageTeacher">Home</a>
					<span class="divider"> <span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li>Add or remove student<span class="divider"> <span
						class="accesshide "><span class="arrow_text"></span>&nbsp;</span>
				</span></li>
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
	<div id="addStudentToGroupDiv">
		<div class="ui-widget" >
			<form
				action="./AddStudentToGroupServlet"
				method="POST" class="form-inline" id="addStudentToGroupForm">
				<c:if test="${not empty sessionScope.invalidFields}">
					<c:if test="${sessionScope.invalidFields}">
						<p class="input-invalid-addStudentToGroup">Invalid
							fields</p>
					</c:if>
				</c:if>
				<c:if test="${not empty sessionScope.emptyFields}">
					<c:if test="${sessionScope.emptyFields}">
						<p class="input-invalid-addStudentToGroup">Empty fields</p>
					</c:if>
				</c:if>
				<c:if test="${not empty sessionScope.doesStudentExist}">
					<c:if test="${not sessionScope.doesStudentExist}">
						<p id="studentMsg" class="input-invalid">Student does not
							exist</p>
					</c:if>
					<c:if test="${not empty sessionScope.isStudentInGroup}">
						<c:if test="${sessionScope.doesStudentExist}">
							<c:if test="${sessionScope.isStudentInGroup}">
								<p id="studentMsg" class="input-invalid">Student is already
									in group</p>
							</c:if>
						</c:if>
					</c:if>
				</c:if>
				<c:if test="${not empty sessionScope.validGroups}">
					<c:if test="${not sessionScope.validGroups}">
						<p id="groupsMsg" class="input-invalid">Group does not exist</p>
					</c:if>
				</c:if>
				<p id="groupMsg" class="input-invalid"></p>
				<p id="studentMsg" class="input-invalid"></p>
				<div class="form-group" style = "padding-top: 2px; ">				
					<label class="control-label col-sm-9" style="padding-right: 30px; padding-bottom: 5px;" >Choose group:</label> <select
						id="chosenGroup" name="chosenGroup" class="selectpicker form-control" required>
						<option value="">-</option>
						<c:forEach var="group" items="${applicationScope.allGroups}">
							<option value="${group.id}"><c:out value="${group.name}"></c:out></option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group" id="studentSearch">
					<label class="control-label col-sm-13" style = "padding-bottom: 5px;">Choose student:</label> <input
						id="searchStudents" name="selectedStudent" class="form-control"
						value="${sessionScope.chosenUsernameTry}" required />
				</div>
				<div class="form-group" style = "padding-top: 23px; width:5% ">
							<input
								style="background-color: #2E71AC; color: #ffffff"
								type="submit" class=" form-control btn btn-default"
								value="Add">
					</div>
			</form>
		</div></div>
		<br> <br>
		<div id="listOfStudentsOfGroupHeading">
		</div>
		<div id="listOfStudentsOfGroup">
			<ul class="editable list-group"></ul>
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
						if (chosenGroupId == '') {
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
									url : './DoesUserExist',
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
													url : './IsChosenStudentAlreadyInGroup',
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
				url : "./RemoveStudentFromGroup",
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
					$(".input-invalid").empty();
					alert('Student has been removed successfully!')
				}
			});
		} else {
			return false;
		}
	}

	$(document).ready(function() {
		$('#chosenGroup').change(function(event) {
			document.getElementById("searchStudents").value = "";
			if (!$('#listOfStudentsOfGroup').is(':empty')) {
				$("#listOfStudentsOfGroup").empty();
				document.getElementById('listOfStudentsOfGroup').style.visibility = 'hidden';

			}
			if (!$('#listOfStudentsOfGroupHeading').is(':empty')) {
				$("#listOfStudentsOfGroupHeading").empty();
				document.getElementById('listOfStudentsOfGroupHeading').style.visibility = 'hidden';

			}
			if (!$('#alert').is(':empty')) {
				$("#alert").remove();
			}
			var groupId = $(this).find(":selected").val();
			getStudents(groupId);
		});
		$(function() {
			var availableTags = new Array();
			<c:forEach items="${applicationScope.allStudents}" var="student">
			availableTags.push('${student.username}');
			</c:forEach>
			$("#searchStudents").autocomplete({
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
						var div = document
								.getElementById("listOfStudentsOfGroup");
						for ( var i in response) {
							var currUsername = response[i].username;
							$('#listOfStudentsOfGroup')
									.append(
											"<li class = 'list-group-item'>"
													+ response[i].username
													+ "<button style = 'background:transparent; borders:none; position:relative;float:right' type = 'button' class = 'btn btn-xs'  onclick = 'areYouSureRemove(this,\""
													+ response[i].username
													+ "\",\""
													+ groupId
													+ "\")' ><span  class='badge glyphicon glyphicon-remove'>"
													+ " "
													+ "</span></button></li>");
						}
						document.getElementById('listOfStudentsOfGroup').style.visibility = 'visible';
						if ($('#listOfStudentsOfGroup').is(':empty')) {
							document.getElementById('listOfStudentsOfGroupHeading').append('There are no students in this group.');
							
						}else{
							document.getElementById('listOfStudentsOfGroupHeading').append('Students in chosen group:');

						}
						document.getElementById('listOfStudentsOfGroupHeading').style.visibility = 'visible';
					}
					
				});
	}
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