<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%-- <link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
 --%><link href="<c:url value="css/addHomeworkCss.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">

</head>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="./GetMainPageTeacher">Home</a><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li>Add
						homework<span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
			</ul>
		</nav>
	</div>
	<c:if test="${not empty invalidFields}">
			<c:if test="${not invalidFields}">
				<div class="alert alertAllPages alert-success">
					<strong>Success!</strong> Homework has been added successfully
				</div>
			</c:if>
		</c:if>
	<div id="pageWrapper">		
		<div id="formAddHomework" >
					<legend>Add homework</legend>
		
			<form action="./addHomework" method="POST"
				enctype="multipart/form-data" id="addHomeworkForm"  accept-charset="UTF-8" class = "form-horizontal">
				<c:if test="${not empty invalidFields}">
					<c:if test="${invalidFields}">
						<p class="input-invalid-or-empty">You have invalid fields</p>
					</c:if>
				</c:if>
				<c:if test="${emptyFields}">
					<p class="input-invalid-or-empty">You cannot have empty fields</p>
				</c:if>
				<div class="form-group">
					<label class="control-label" >Heading:</label>
					<div class="control-label-input">
						<input type="text" class="form-control" name="name"
							value="${nameTry}" placeholder="Enter homework heading" maxlength="40"
							data-toggle="popover" data-placement="bottom"
							data-trigger="focus"
							data-content="Valid length is from 5 to 40 symbols. Valid inputs are numbers and letters (large and small)"
							 required/>
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
					<label class="control-label">Opening
						time:</label>
					<div class='control-label-input'>
						<div class='input-group date' id='datetimepicker6'>
							<input type='text' value="${opensTry}" class="form-control"
								id="opens" name="opens" placeholder="Enter opening time"
								data-toggle="popover" data-placement="bottom"
								data-trigger="focus"
								data-content="Maximum 6 months from today" class="input-group-addon" required/><span
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
					<label class="control-label">Closing
						time:</label>
					<div class='control-label-input'>
						<div class='input-group date' id='datetimepicker7'>
							<input type='text' value="${closesTry}" data-toggle="popover"
								data-placement="bottom" data-trigger="focus"
								data-content="Maximum 6 months after opening time"
								class="form-control" id="closes" name="closes"
								placeholder="Enter closing time"  required/><span
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
					<label class="control-label">Number of
						tasks:</label>
					<div class="control-label-input">
						<input type="number" min="1" max="41" maxlength="2"
							class="form-control" name="numberOfTasks"
							placeholder="Enter number of tasks" value="${numberOfTasksTry}"
							data-toggle="popover" data-placement="bottom"
							data-trigger="focus" data-content="From 1 to 40"  required/>
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
					<label class="control-label">Groups</label>
					<div class="control-label-input">
						<select class="selectpicker form-control" data-width="101%" data-size="7" multiple name="groups" id="groups"
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
					<label class="control-label">Tasks:</label>
					<div class="control-label-input">
						<input type="file" accept="application/pdf" name="file"  required/>
						<c:if test="${not empty validFile}">
							<c:if test="${not validFile}">
								<p id="fileMsg" class="input-invalid">Valid file format -
									pdf, maximal size - 20MB</p>
							</c:if>
						</c:if>
						<p id="fileMsg" class="input-invalid"></p>
					</div>
				</div>
				<legend></legend>
				<div class="form-group" >
					<!-- <div class="col-md-offset-4 col-sm-5"> -->
						<input 
							type="submit" id = "addButton" class=" form-control btn btn-default"
							value="Add">
					<!-- </div> -->
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
			if (size > 20 || size == 0) {
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


