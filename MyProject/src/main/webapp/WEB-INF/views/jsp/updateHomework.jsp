<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
<link href="<c:url value="css/updateHomeworkCss.css" />"
	rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link rel="icon" type="image/png" href="./images/favIcon.png">

</head>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a href="./mainPageTeacher">Home</a> <span class="divider"><span
						class="accesshide "><span class="arrow_text"></span></span> </span></li>
				<li><a href="./seeOrUpdateHomeworks">See/Update homeworks</a><span
					class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span> </span></li>
				<li><c:out value="${sessionScope.currHomework.heading}"></c:out><span
					class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span> </span></li>
			</ul>
		</nav>
	</div>
	<c:if test="${not empty sessionScope.invalidFields}">
		<c:if test="${not sessionScope.invalidFields}">
			<div class="alert alertAllPages alert-success">
				<strong>Success!</strong> Homework has been updated successfully
			</div>
		</c:if>
	</c:if>
	<div id="pageWrapper">
		<div id="readAndRemoveHomework">
			<form action="./readFileOfTasksForHomeworkPDF" method="GET"
				id="downloadHomeworkForm">
				<input type='hidden' value='${sessionScope.currHomework.tasksFile}'
					name='fileName'>
				<button class='btn btn-link btn-xs' type='submit'>
					<u>download tasks</u>
				</button>
			</form>
			<form action="./readFileOfTasksForHomeworkTestsZip" method="GET"
				id="downloadHomeworkTestsForm">
				<input type='hidden'
					value='${sessionScope.currHomework.testTasksFile}' name='fileName'>
				<button class='btn btn-link btn-xs' type='submit'>
					<u>download tests</u>
				</button>
			</form>
			<form action="./removeHomeworkDetails" method="POST"
				id="removeHomeworkForm">
				<button type="submit"
					class="glyphicon glyphicon-remove btn btn-default btn-xs"
					onclick="javascript:return confirm('Are you sure you want to remove this homework permanently?')"></button>
			</form>
		</div>
		<div id="formUpdate" align="right" class="form-horizontal">
			<legend>Update homework</legend>
			<form action="./updateHomework" method="POST"
				enctype="multipart/form-data" id="updateHomeworkForm"
				accept-charset="UTF-8">
				<c:if test="${not empty sessionScope.invalidFields}">
					<c:if test="${sessionScope.invalidFields}">
						<p class="input-invalid-or-empty">You have invalid fields</p>
					</c:if>
				</c:if>
				<c:if test="${not empty sessionScope.emptyFields}">
					<c:if test="${sessionScope.emptyFields}">
						<p class="input-invalid-or-empty">You cannot have empty fields</p>
					</c:if>
				</c:if>
				<div class="form-group">
					<label class="control-label">Heading:</label>
					<div class="control-label-input">
						<input type="text" class="form-control" name="name"
							value='${sessionScope.currHomework.heading}'
							placeholder="Enter homework heading" maxlength="40"
							data-toggle="popover" data-placement="bottom"
							data-trigger="focus"
							data-content="Valid length is from 5 to 40 symbols. Valid inputs are numbers and letters (large and small)"
							required />
						<c:if test="${not empty sessionScope.validHeading}">
							<c:if test="${not sessionScope.validHeading}">
								<p id="nameMsg" class="input-invalid">Heading is not valid</p>
							</c:if>
							<c:if test="${not empty sessionScope.uniqueHeading}">
								<c:if test="${sessionScope.validHeading}">
									<c:if test="${not sessionScope.uniqueHeading}">
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
					<label class="control-label">Opening time:</label>
					<div class='control-label-input'>
						<div class='input-group date' id='datetimepicker6'>
							<input type='text'
								value="${sessionScope.currHomework.openingTime}"
								class="form-control" id="opens" name="opens"
								placeholder="Enter opening time" data-toggle="popover"
								data-placement="bottom" data-trigger="focus"
								data-content="Maximum 6 months from today" required /><span
								class="input-group-addon"> <span
								class="glyphicon glyphicon-calendar"></span>
							</span>
						</div>
						<c:if test="${not empty sessionScope.validOpeningTime}">
							<c:if test="${not sessionScope.validOpeningTime}">
								<p id="opensMsg" class="input-invalid">Opening time is not
									valid</p>
							</c:if>
						</c:if>
						<p id="opensMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label">Closing time:</label>
					<div class='control-label-input'>
						<div class='input-group date' id='datetimepicker7'>
							<input type='text'
								value="${sessionScope.currHomework.closingTime}"
								class="form-control" id="closes" name="closes"
								placeholder="Enter closing time" data-toggle="popover"
								data-placement="bottom" data-trigger="focus"
								data-content="Maximum 6 months after opening time" required /><span
								class="input-group-addon"> <span
								class="glyphicon glyphicon-calendar"></span>
							</span>
						</div>
						<c:if test="${not empty sessionScope.validClosingTime}">
							<c:if test="${not sessionScope.validClosingTime}">
								<p id="closesMsg" class="input-invalid">Closing time is not
									valid</p>
							</c:if>
						</c:if>
						<p id="closesMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label">Number of tasks:</label>
					<div class="control-label-input">
						<input type="number" min="1" max="40" class="form-control"
							name="numberOfTasks"
							value='${sessionScope.currHomework.numberOfTasks}' maxlength="2"
							data-toggle="popover" placeholder="Enter number of tasks"
							data-placement="bottom" data-trigger="focus"
							data-content="From 1 to 40" required />
						<c:if test="${not empty sessionScope.validTasks}">
							<c:if test="${not sessionScope.validTasks}">
								<p id="numberOfTasksMsg" class="input-invalid">Number of
									tasks is not valid</p>
							</c:if>
						</c:if>
						<p id="numberOfTasksMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label">Groups:</label>
					<div class="control-label-input">
						<select class="selectpicker form-control" data-size="7"
							data-width="101%" multiple name="groups" id="groups"
							class="form-control" required>
							<c:forEach items="${applicationScope.allGroups}" var="group">
								<c:set var="isHwInGroup" value="false"></c:set>
								<c:forEach items="${group.homeworks}" var="homework">
									<c:if test="${homework.id==sessionScope.currHomework.id}">
										<c:set var="isHwInGroup" value="true"></c:set>
									</c:if>
								</c:forEach>
								<c:if test="${isHwInGroup}">
									<option value="${group.id}" selected>
										<c:out value="${group.name}"></c:out></option>
								</c:if>
								<c:if test="${not isHwInGroup}">
									<option value="${group.id}">
										<c:out value="${group.name}"></c:out></option>
								</c:if>
							</c:forEach>
						</select>
						<c:if test="${not empty sessionScope.validGroups}">
							<c:if test="${not sessionScope.validGroups}">
								<p id="groupsMsg" class="input-invalid">Not all groups exist</p>
							</c:if>
						</c:if>
						<p id="groupsMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label">Tasks:</label>
					<div class="control-label-input">
						<input type="file" accept="application/pdf" name="file">
						<c:if test="${not empty sessionScope.validFile}">
							<c:if test="${not sessionScope.validFile}">
								<p id="fileMsg" class="input-invalid">Valid file format -
									pdf, maximal size - 20MB</p>
							</c:if>
						</c:if>
						<p id="fileMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label">Tests:</label>
					<div class="control-label-input">
						<input type="file" accept="application/zip" name="testsFile" /><span
							id="testsFileConstraint">(Files in ZIP must be ".txt")</span> <br />
						<c:if test="${not empty validTestsFile}">
							<c:if test="${not validTestsFile}">
								<p id="testsFileMsg" class="input-invalid">Valid file format
									- zip, maximal size - 20MB</p>
							</c:if>
						</c:if>
						<p id="testsFileMsg" class="input-invalid"></p>
					</div>
				</div>
				<legend></legend>
				<div class="form-group">
					<input type="submit" id="updateHomeworkButton"
						class=" form-control btn btn-default" value="Update">
				</div>
			</form>
		</div>
		<c:if test="${not empty sessionScope.invalidFields}">
			<c:remove var="invalidFields" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.emptyFields}">
			<c:remove var="emptyFields" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.validHeading}">
			<c:remove var="validHeading" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.uniqueHeading}">
			<c:remove var="uniqueHeading" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.validOpeningTime}">
			<c:remove var="validOpeningTime" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.validClosingTime}">
			<c:remove var="validClosingTime" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.validTasks}">
			<c:remove var="validTasks" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.validGroups}">
			<c:remove var="validGroups" scope="session" />
		</c:if>
	</div>
	<script>
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
		function isFileValidCheck() {
			var file = document.forms["updateHomeworkForm"]["file"].value;
			var val = file.toLowerCase();
			var regex = new RegExp("(.*?)\.(pdf)$");
			if (!(regex.test(val))) {
				return false;
			}
			var size = (document.forms["updateHomeworkForm"]["file"].files[0].size / 1024 / 1024)
					.toFixed(10);
			if (size > 20 || size == 0) {
				return false;
			}
			return true;
		}
		function isTestsFileValidCheck() {
			var file = document.forms["updateHomeworkForm"]["testsFile"].value;
			var val = file.toLowerCase();
			var regex = new RegExp("(.*?)\.(zip)$");
			if (!(regex.test(val))) {
				return false;
			}
			var size = (document.forms["updateHomeworkForm"]["testsFile"].files[0].size / 1024 / 1024)
					.toFixed(10);
			if (size > 20 || size == 0) {
				return false;
			}
			return true;
		}
		 $('#updateHomeworkForm')
				.submit(
						function(e) {
							e.preventDefault();
							var name = document.forms["updateHomeworkForm"]["name"].value;
							var opens = document.forms["updateHomeworkForm"]["opens"].value;
							var closes = document.forms["updateHomeworkForm"]["closes"].value;
							var numberOfTasks = document.forms["updateHomeworkForm"]["numberOfTasks"].value;
							var file = document.forms["updateHomeworkForm"]["file"].value;
							var groups = document.forms["updateHomeworkForm"]["groups"].value;
							var testsFile = document.forms["updateHomeworkForm"]["testsFile"].value;
							var isNameValid = true;
							var isNameUnique = true;
							var isOpensValid = true;
							var isClosesValid = true;
							var isNumberOfTasksValid = true;
							var isFileValid = true;
							var isGroupsValid = true;
							var isTestsFileValid = true;
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
							if (!$('#groupsMsg').is(':empty')) {
								$("#groupsMsg").empty();
							}
							if (!$('#fileMsg').is(':empty')) {
								$("#fileMsg").empty();
							}
							if (!$('#testsFileMsg').is(':empty')) {
								$("#testsFileMsg").empty();
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
									document.getElementById("numberOfTasksMsg")
											.append("From 1 to 40");
									isNumberOfTasksValid = false;
								}
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
										url : './isHomeworkUpdateHeadingValid',
										type : 'GET',
										data : {
											"heading" : name
										},
										success : function(response) {
											if (!$('#nameMsg').is(':empty')) {
												$("#nameMsg").empty();
												isNameValid = true;
											}
											$
													.ajax({
														url : './isHomeworkUpdateHeadingIsRepeated',
														type : 'GET',
														data : {
															"heading" : name
														},
														success : function(
																response) {
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
																			"Heading already exist");
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
								url : './isHomeworkUpdateOpeningTimeValid',
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
								}
							});
							$
									.ajax({
										url : './isHomeworkUpdateClosingTimeValid',
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
												"From 1 to 40");
							}
							if (file != "") {
								isFileValid = false;
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
							}
							var isTestsFileValid = true;
							if (testsFile != "") {
							var file = document.forms["updateHomeworkForm"]["testsFile"].value;
			var val = file.toLowerCase();
			var regex = new RegExp("(.*?)\.(zip)$");
			if (!(regex.test(val))) {
				isTestsFileValid = false;
			}
			var size = (document.forms["updateHomeworkForm"]["testsFile"].files[0].size / 1024 / 1024)
					.toFixed(10);
			if (size > 20 || size == 0) {
				isTestsFileValid =  false;
			}
			
			var form = new FormData(document.forms["updateHomeworkForm"]);
			//see types
			if(isTestsFileValid === true){
					$.ajax({
								url : './isHomeworkZipFileValid',
								type : 'POST',
							 	data: form, 
							 	processData: false,
								contentType:false,
								 dataType: false,
								success : function(response) {
									if (!$('#testsFileMsg').is(':empty')) {
										$("#testsFileMsg").empty();
										isTestsFileValid = true;
									}
								},
								error : function(data) {
									if (!$('#testsFileMsg').is(':empty')) {
										$("#testsFileMsg").empty();
									}
									isTestsFileValid = false;
								}
			
						});

							
			}
							}
							$(document)
									.ajaxStop(
											function() {
												if (!$('#testsFileMsg').is(':empty')) {
													$("#testsFileMsg").empty();
												}
												if (isTestsFileValid === false) {
													document
															.getElementById("testsFileMsg")
															.append(
																	"Valid file format - zip, maximal size - 20MB, valid extensions in zip - .txt");
												}
												if ((isNameUnique === true
														&& isNameValid === true
														&& isOpensValid === true
														&& isClosesValid === true
														&& isNumberOfTasksValid === true && isFileValid === true && isTestsFileValid == true)) {
													document
															.getElementById(
																	"updateHomeworkForm")
															.submit();
												}
											});
							});
		$(document).ready(function() {
			$('[data-toggle="popover"]').popover();
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