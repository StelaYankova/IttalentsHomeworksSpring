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

#formAddHomework {
	position: absolute;
	left: 60px;
	top: 220px;
	background-color: #ffffff;
	width: 600px;
}
</style>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div id="pageWrapper">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="http://localhost:8080/MyProject/GetMainPageTeacher">Home</a>
					<span class="divider"> <span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li><a href="http://localhost:8080/MyProject/AddHomework">Add
						homework</a> <span class="divider"> <span class="accesshide "><span
							class="arrow_text"></span>&nbsp;</span>
				</span></li>
			</ul>
		</nav>
		<c:if test="${not empty invalidFields}">
			<c:if test="${not invalidFields}">
				<div class="alert alert-success">
					<strong>Success!</strong> Homework has been added successfully
				</div>
			</c:if>
		</c:if>
		<div id="image">
			<img src="images/logo-black.png" class="img-rounded" width="380"
				height="236">
		</div>
		<div id="formAddHomework" align="right">
			<form action="./AddHomework" method="POST"
				enctype="multipart/form-data" id="addHomeworkForm">
				<label
					style="position: absolute; left: 290px; text-decoration: underline;">Add
					homework</label> <br> <br> <br>
				<c:if test="${not empty invalidFields}">
					<c:if test="${invalidFields}">
						<p class="input-invalid-or-empty">Invalid fields</p>
					</c:if>
				</c:if>
				<c:if test="${emptyFields}">
					<p class="input-invalid-or-empty">Empty fields</p>
				</c:if>
				<div class="form-group">
					<label class="control-label col-sm-6">Name</label>
					<div class="col-sm-6">
						<input type="text" class="form-control" name="name"
							value="${nameTry}" placeholder="Enter heading" maxlength="40"
							data-toggle="popover" data-placement="bottom"
							data-trigger="focus"
							data-content="Size of heading - 5 to 40 symbols. Valid inputs are numbers and letters (large and small)"
							required />
						<c:if test="${not empty validHeading}">
							<c:if test="${not validHeading}">
								<p id="nameMsg" class="input-invalid">Heading is not valid</p>
							</c:if>
							<c:if test="${not empty uniqueHeading}">
								<c:if test="${validHeading}">
									<c:if test="${not uniqueHeading}">
										<p id="nameMsg" class="input-invalid">Heading already
											exists</p>
									</c:if>
								</c:if>
							</c:if>
						</c:if>
						<p id="nameMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<br> <label class="control-label col-sm-6">Opening
						time</label>
					<div class='col-sm-6'>
						<div class='input-group date' id='datetimepicker6'>
							<input type='text' value="${opensTry}" class="form-control"
								id="opens" name="opens" placeholder="Enter opening time"
								data-toggle="popover" data-placement="bottom"
								data-trigger="focus"
								data-content="From today max 6 months from now" required /> <span
								class="input-group-addon"> <span
								class="glyphicon glyphicon-calendar"></span>
							</span>
						</div>
						<c:if test="${not empty validOpeningTime}">
							<c:if test="${not validOpeningTime}">
								<p id="opensMsg" class="input-invalid">Opening time is not
									valid</p>
							</c:if>
						</c:if>
						<p id="opensMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<br> <label class="control-label col-sm-6">Closing
						time</label>
					<div class='col-sm-6'>
						<div class='input-group date' id='datetimepicker7'>
							<input type='text' value="${closesTry}" data-toggle="popover"
								data-placement="bottom" data-trigger="focus"
								data-content="Max 6 months after opening time"
								class="form-control" id="closes" name="closes"
								placeholder="Enter closing time" required /> <span
								class="input-group-addon"> <span
								class="glyphicon glyphicon-calendar"></span>
							</span>
						</div>
						<c:if test="${not empty validClosingTime}">
							<c:if test="${not validClosingTime}">
								<p id="closesMsg" class="input-invalid">Closing time is not
									valid</p>
							</c:if>
						</c:if>
						<p id="closesMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<br> <label class="control-label col-sm-6">Number of
						tasks</label>
					<div class="col-sm-6">
						<input type="number" min="0" max="41" maxlength="2"
							class="form-control" name="numberOfTasks"
							placeholder="Enter number of tasks" value="${numberOfTasksTry}"
							data-toggle="popover" data-placement="bottom"
							data-trigger="focus" data-content="From 1 to 40" required />
						<c:if test="${not empty validTasks}">
							<c:if test="${not validTasks}">
								<p id="numberOfTasksMsg" class="input-invalid">Number of
									tasks is not valid</p>
							</c:if>
						</c:if>
						<p id="numberOfTasksMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<br> <label class="control-label col-sm-6">Tasks</label>
					<div class="col-sm-6">
						<input type="file" accept="application/pdf" name="file" required />
						<c:if test="${not empty validFile}">
							<c:if test="${not validFile}">
								<p id="fileMsg" class="input-invalid">Valid file format -
									pdf, maximal size - 20MB</p>
							</c:if>
						</c:if>
						<p id="fileMsg" class="input-invalid"></p>
					</div>
				</div>
				<br>
				<div class="form-group">
					<label class="control-label col-sm-6">Groups</label>
					<div class="col-sm-6">
						<select class="selectpicker" multiple name="groups" id="groups"
							required>
							<c:forEach items="${applicationScope.allGroups}" var="group">
								<c:set var="isGroupSelected" value="false"></c:set>
								<c:forEach items="${selectedGroupsTry}" var="selectedGroup">
									<c:if test="${group.id == selectedGroup}">
										<c:set var="isGroupSelected" value="true"></c:set>
									</c:if>
								</c:forEach>
								<c:if test="${isGroupSelected}">
									<option value="${group.id}" selected>
										<c:out value="${group.name}"></c:out></option>
								</c:if>
								<c:if test="${not isGroupSelected}">
									<option value="${group.id}">
										<c:out value="${group.name}"></c:out></option>
								</c:if>
							</c:forEach>
						</select>
						<c:if test="${not empty validGroups}">
							<c:if test="${not validGroups}">
								<p id="groupsMsg" class="input-invalid">Not all groups exist</p>
							</c:if>
						</c:if>
						<p id="groupsMsg" class="input-invalid"></p>
					</div>
				</div>
				
				<div class="form-group">
					<div class="col-sm-offset-3 col-sm-2" style="left: 360px">
						<br> <input style="align: right" type="submit"
							class="btn btn-default" value="Save">
					</div>
				</div>
			</form>
		</div>		
	</div>
	<script type="text/javascript">

		$(document).ready(function() {
			$(function() {
				$('#datetimepicker6').datetimepicker({
					format : 'YYYY/MM/DD HH:mm'
				});
				$('#datetimepicker7').datetimepicker({
					format : 'YYYY/MM/DD HH:mm'
				});
			});
		});

		$(document).ready(function() {
			$('[data-toggle="popover"]').popover();
		});

		function isFileValidCheck() {
			var file = document.forms["addHomeworkForm"]["file"].value;
			var val = file.toLowerCase();
			var regex = new RegExp("(.*?)\.(pdf)$");
			if (!(regex.test(val))) {
				return false;
			}
			var size = (document.forms["addHomeworkForm"]["file"].files[0].size / 1024 / 1024)
					.toFixed(2);
			console.log(size)
			if (size > 20) {
				console.log(false)
				return false;
			}
			return true;
		}
		$('#addHomeworkForm')
				.submit(
						function(e) {
							e.preventDefault();
							var name = document.forms["addHomeworkForm"]["name"].value
									.trim();
							var opens = document.forms["addHomeworkForm"]["opens"].value;
							var closes = document.forms["addHomeworkForm"]["closes"].value;
							var numberOfTasks = document.forms["addHomeworkForm"]["numberOfTasks"].value;
							var file = document.forms["addHomeworkForm"]["file"].value;
							var groups = document.forms["addHomeworkForm"]["groups"].value;
							var isNameValid = true;
							var isNameUnique = true;
							var isOpensValid = true;
							var isClosesValid = true;
							var isNumberOfTasksValid = true;
							var isFileValid = true;
							var isGroupsValid = true;

							if (!$('#nameMsg').is(':empty')) {
								$("#nameMsg").empty();
							}
							if (!$('#opensMsg').is(':empty')) {
								$("#opensMsg").empty();
							}
							if (!$('#closesMsg').is(':empty')) {
								$("#closesMsg").empty();
							}
							if (!$('#numberOfTasksMsg').is(':empty')) {
								$("#numberOfTasksMsg").empty();
							}
							if (!$('#fileMsg').is(':empty')) {
								$("#fileMsg").empty();
							}
							if (!$('#groupsMsg').is(':empty')) {
								$("#groupsMsg").empty();
							}
							if (name == "") {
								document.getElementById("nameMsg").append(
										"Fill heading");
								isNameValid = false;
							}
							if (opens == "") {
								document.getElementById("opensMsg").append(
										"Fill opening time");
								isOpensValid = false;
							}
							if (closes == "") {
								document.getElementById("closesMsg").append(
										"Fill closing time");
								isClosesValid = false;
							}
							if (numberOfTasks == "") {
								document.getElementById("numberOfTasksMsg")
										.append("Fill number of tasks");
								isNumberOfTasksValid = false;
							} else {
								if ((numberOfTasks < 1) || (numberOfTasks > 40)) {
									document
											.getElementById("numberOfTasksMsg")
											.append(
													"Number of tasks - between 1 and 40");
									isNumberOfTasksValid = false;
								}
							}
							if (file == "") {
								document.getElementById("fileMsg").append(
										"Fill file");
								isFileValid = false;
							}
							if (groups.length == 0) {
								document.getElementById("groupsMsg").append(
										"Fill groups");
								isGroupsValid = false;
							}

							if (!(isNameValid === true && isOpensValid === true
									&& isClosesValid === true
									&& isNumberOfTasksValid === true
									&& isFileValid === true && isGroupsValid === true)) {
								return false;
							}

							$
									.ajax({
										url : './IsHomeworkHeadingValid',
										type : 'GET',
										data : {
											"heading" : name
										},
										success : function(response) {
											console.log(99)
											if (!$('#nameMsg').is(':empty')) {
												$("#nameMsg").empty();
												isNameValid = true;
											}
											$
													.ajax({
														url : './IsHomeworkHeadingUnique',
														type : 'GET',
														data : {
															"heading" : name
														},

														success : function(
																response) {
															console.log(99)
															if (!$('#nameMsg')
																	.is(
																			':empty')) {
																$("#nameMsg")
																		.empty();
																isNameUnique = true;
															}
														},
														error : function(data) {
															if (!$('#nameMsg')
																	.is(
																			':empty')) {
																$("#nameMsg")
																		.empty();
															}
															isNameUnique = false;
															document
																	.getElementById(
																			"nameMsg")
																	.append(
																			"Heading already exists");
														}
													});
										},
										error : function(data) {
											if (!$('#nameMsg').is(':empty')) {
												$("#nameMsg").empty();
											}
											isNameValid = false;
											document
													.getElementById("nameMsg")
													.append(
															"Heading is not valid");
										}
									});
							$.ajax({
								url : './IsHomeworkOpeningTimeValid',
								type : 'GET',
								data : {
									"opens" : opens
								},
								success : function(response) {
									if (!$('#opensMsg').is(':empty')) {
										$("#opensMsg").empty();
										isOpensValid = true;
									}
								},
								error : function(data) {
									if (!$('#opensMsg').is(':empty')) {
										$("#opensMsg").empty();
									}
									isOpensValid = false;
									document.getElementById("opensMsg").append(
											"Opening time is not valid");
									console.log("invalid opens")
								}
							});
							$
									.ajax({
										url : './IsHomeworkClosingTimeValid',
										type : 'GET',
										data : {
											"opens" : opens,
											"closes" : closes
										},
										success : function(response) {
											if (!$('#closesMsg').is(':empty')) {
												$("#closesMsg").empty();
												isClosesValid = true;
											}
										},
										error : function(data) {
											if (!$('#closesMsg').is(':empty')) {
												$("#closesMsg").empty();
											}
											isClosesValid = false;
											document
													.getElementById("closesMsg")
													.append(
															"Closing time is not valid");
										}
									});
							if (numberOfTasks >= 1 && numberOfTasks <= 40) {
								if (!$('#numberOfTasksMsg').is(':empty')) {
									$("#numberOfTasksMsg").empty();
									isNumberOfTasksValid = true;
								}
							} else {
								if (!$('#numberOfTasksMsg').is(':empty')) {
									$("#numberOfTasksMsg").empty();
								}
								isNumberOfTasksValid = false;
								document
										.getElementById("numberOfTasksMsg")
										.append(
												"Number of tasks - between 1 and 40");
							}
							isFileValid = isFileValidCheck();
							if (!isFileValid) {
								if (!$('#fileMsg').is(':empty')) {
									$("#fileMsg").empty();
								}
								document
										.getElementById("fileMsg")
										.append(
												"Valid file format - pdf, maximal size - 20MB");
							}
							$(document)
									.ajaxStop(
											function() {
												if ((isNameUnique === true
														&& isNameValid === true
														&& isOpensValid === true
														&& isClosesValid === true
														&& isNumberOfTasksValid === true && isFileValid === true)) {

													document.getElementById(
															"addHomeworkForm")
															.submit();
												}
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
</body>
</html>
