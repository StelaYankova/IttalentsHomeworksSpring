<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/updateProfileCss.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
</head>

<body>
	<c:if test="${not empty invalidFields}">
		<c:if test="${not invalidFields}">
			<div class="alert alertAllPages alert-success">
				<strong>Success!</strong> Your profile has been updated successfully
			</div>
		</c:if>
	</c:if>
	<c:if test="${sessionScope.isTeacher == false}">
		<%@ include file="navBarStudent.jsp"%>
	</c:if>
	<c:if test="${sessionScope.isTeacher == true}">
		<%@ include file="navBarTeacher.jsp"%>
	</c:if>
	<c:if test="${sessionScope.isTeacher == false}">
		<div class="navPath">
			<nav class="breadcrumb-nav">
				<ul class="breadcrumb">
					<li><a
						href="./GetMainPageStudent">Home</a>
						<span class="divider"> <span class="accesshide "><span
								class="arrow_text"></span></span>
					</span></li>
					<li>Your
							profile<span class="divider"> <span class="accesshide "><span
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
						href="./GetMainPageTeacher">Home</a>
						<span class="divider"><span class="accesshide "><span
								class="arrow_text"></span></span> </span></li>
					<li>Your
							profile<span class="divider"><span class="accesshide "><span
								class="arrow_text"></span></span> </span></li>
				</ul>
			</nav>
		</div>
	</c:if>
	<div id="pageWrapper">
		<div id="formUpdateProfile" align="center">
			<legend>Update profile</legend>
			<c:if test="${not empty invalidFields}">
				<c:if test="${invalidFields}">
					<p class="input-invalid-or-empty">Invalid fields</p>
				</c:if>
			</c:if>
			<c:if test="${emptyFields}">
				<p class="input-invalid-or-empty">Empty fields</p>
			</c:if>
			<form action="./UpdateYourProfileServlet" method="POST"
				id="updateForm" name="updateForm" class="form-horizontal">
				<div id="inputFields">
					<div class="form-group">
						<label class="control-label col-sm-4">Username:</label>
						<div class="col-sm-3">
							<c:out value="${sessionScope.user.username}"></c:out>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-4">Password:</label>
						<div class="col-sm-7">
							<input type="password" class="form-control"
								value="${sessionScope.user.password}" name="password"
								placeholder="Enter password" maxlength="15"
								data-toggle="popover" data-placement="bottom"
								data-trigger="focus"
								data-content="Size of password - 6 to 15 symbols. Valid inputs are numbers and letters (large and small)"
								required />
							<c:if test="${not empty validPass}">
								<c:if test="${not validPass}">
									<p id="passwordMsg" class="input-invalid">Password is not
										valid</p>
								</c:if>
							</c:if>
							<p id="passwordMsg" class="input-invalid"></p>
						</div>
					</div>
					<div class="form-group ">
						<label class="control-label col-sm-4">Repeat
							password:</label>
						<div class="col-sm-7">
							<input type="password" class="form-control"
								placeholder="Repeat password"
								value="${sessionScope.user.password}" maxlength="15"
								name="repeatedPassword" required />
							<c:if test="${not empty validRepeatedPass}">
								<c:if test="${not validRepeatedPass}">
									<p id="repeatedPasswordMsg" class="input-invalid">Repeated
										password is not valid</p>
								</c:if>
							</c:if>
							<p id="repeatedPasswordMsg" class="input-invalid"></p>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-4">Email:</label>
						<div class="col-sm-7">
							<input type="email" class="form-control"
								placeholder="Enter email" value="${sessionScope.user.email}"
								name="email" required />
							<c:if test="${not empty validEmail}">
								<c:if test="${not validEmail}">
									<p id="emailMsg" class="input-invalid">Email is not valid</p>
								</c:if>
							</c:if>
							<p id="emailMsg" class="input-invalid"></p>
						</div>
					</div>
					<br><legend></legend>
					<div class="form-group">
						<div class="col-md-offset-4 col-sm-5">
							<input id = "updateProfileButton"
								type="submit" class=" form-control btn btn-default"
								value="Update">
						</div>
					</div>
				</div>
				
			</form>
		</div>
	</div>
	<c:if test="${not empty invalidFields}">
		<c:remove var="invalidFields" scope="session" />
	</c:if>
	<c:if test="${not empty emptyFields}">
		<c:remove var="emptyFields" scope="session" />
	</c:if>
</body>
<script>
	function checkIsEmailValid() {
		var email = document.forms["updateForm"]["email"].value;
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(email)) {
			return false;
		}
		return true;
	}
	$('#updateForm')
			.submit(
					function(e) {
						e.preventDefault();
						var currPassword = '${sessionScope.user.password}';
						var password = document.forms["updateForm"]["password"].value;
						var repeatedPassword = document.forms["updateForm"]["repeatedPassword"].value;
						var email = document.forms["updateForm"]["email"].value;
						if (!$('#passwordMsg').is(':empty')) {
							$("#passwordMsg").empty();
						}
						if (!$('#emailMsg').is(':empty')) {
							$("#emailMsg").empty();
						}
						if (!$('#repeatedPasswordMsg').is(':empty')) {
							$("#repeatedPasswordMsg").empty();
						}
						var isPasswordValid = true;
						var isRepeatedPasswordValid = true;
						var isEmailValid = true;
						if (password == "") {
							document.getElementById("passwordMsg").append(
									"Fill password");
							isPasswordValid = false;
						}
						if (repeatedPassword == "") {
							document.getElementById("repeatedPasswordMsg")
									.append("Fill password");
							isRepeatedPasswordValid = false;
						}
						if (email == "") {
							document.getElementById("emailMsg").append(
									"Fill email");
							isEmailValid = false;
						}
						if ((isPasswordValid === true
								&& isRepeatedPasswordValid === true && isEmailValid === true)) {
							if (password !== repeatedPassword) {
								document
										.getElementById("repeatedPasswordMsg")
										.append(
												"Repeated password is not valid");
								isRepeatedPasswordValid = false;
							} else {
								isRepeatedPasswordValid = true;
							}
							if (password !== currPassword) {
								$
										.ajax({
											url : './IsPasswordValid',
											type : 'GET',
											data : {
												"password" : password
											},
											success : function(response) {
												if (!$('#passwordMsg').is(
														':empty')) {
													$("#passwordMsg").empty();
													isPasswordValid = true;
												}
											},
											error : function(data) {
												if (!$('#passwordMsg').is(
														':empty')) {
													$("#passwordMsg").empty();
												}
												isPasswordValid = false;
												document
														.getElementById(
																"passwordMsg")
														.append(
																"Password is not valid");
											}
										});
							}
							if (!isEmailValid) {
								if (!$('#emailMsg').is(':empty')) {
									$("#emailMsg").empty();
								}
								document.getElementById("emailMsg").append(
										"Email is not valid");
							} else {
								if (!$('#emailMsg').is(':empty')) {
									$("#emailMsg").empty();
								}
							}
							if (password !== currPassword) {console.log(1)
								$(document)
										.ajaxStop(
												function() {
													if ((isPasswordValid === true
															&& isRepeatedPasswordValid === true && isEmailValid === true)) {
														document
																.getElementById(
																		"updateForm")
																.submit();
													}
												});
							} else {console.log(3)
								if ((isPasswordValid === true
										&& isRepeatedPasswordValid === true && isEmailValid === true)) {
									document.getElementById("updateForm")
											.submit();
								}
							}
						}
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

</html>