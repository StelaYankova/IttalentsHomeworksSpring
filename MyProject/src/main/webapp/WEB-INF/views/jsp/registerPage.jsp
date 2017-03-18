<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/registerPageCss.css" />" type="text/css" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" type="text/css" rel="stylesheet">

</head>
<style>
/*  .form-group {
	margin-left: 60px auto;
} */
/* #formRegister {
	margin: 0 auto;
 	padding-top: 100px;
 	width: 40%;
 	padding-bottom:150px;
} */ 
</style>
<body>
	<%@ include file="navBarHomePage.jsp"%>
	<c:if test="${not empty invalidFields}">
		<c:if test="${not invalidFields}">
			<div class="alert alertRegisterPage alert-success">
				<strong>Success!</strong> Your have registered successfully
			</div>
		</c:if>
	</c:if>
	<div id="pageWrapperRegister">
		<div id="formRegister" align="center">
			<legend>Registration</legend>
			<c:if test="${not empty invalidFields}">
				<c:if test="${invalidFields}">
					<p class="input-invalid-or-empty">Invalid fields</p>
				</c:if>
			</c:if>
			<c:if test="${emptyFields}">
				<p class="input-invalid-or-empty">Empty fields</p>
			</c:if>
			<form class="form-horizontal" name="registerForm" id="registerForm"
				action="./RegisterServlet" method="POST">
				<div class="form-group">
					<label for="username" class="control-label col-sm-4">Username:</label>
					<div class="col-sm-7">
						<input type="text" class="form-control"
							placeholder="Enter username" name="username" maxlength="15"
							value="${userTry.username}" data-toggle="popover"
							data-placement="bottom" data-trigger="focus"
							data-content="Size of username - 6 to 15 symbols. Valid inputs are numbers and letters (large and small)"
							required />
						<c:if test="${not empty validUsername}">
							<c:if test="${not validUsername}">
								<p id="usernameMsg" class="input-invalid">Username is not
									valid</p>
							</c:if>
						</c:if>
						<c:if test="${not empty validUsername}">
							<c:if test="${not empty uniqueUsername}">
								<c:if test="${validUsername}">
									<c:if test="${not uniqueUsername}">
										<p id="usernameMsg" class="input-invalid">Username already
											exists</p>
									</c:if>
								</c:if>
							</c:if>
						</c:if>
						<p id="usernameMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<label for="password" class="control-label col-sm-4">Password:</label>
					<div class="col-sm-7">
						<input type="password" class="form-control" maxlength="15"
							name="password" value="${userTry.password }"
							placeholder="Enter password" data-toggle="popover"
							data-placement="bottom" data-trigger="focus"
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
				<div class="form-group">
					<label for="repeatedPassword" class="control-label col-sm-4">Repeat password:</label>
					<div class="col-sm-7">
						<input type="password" class="form-control" maxlength="15"
							name="repeatedPassword" placeholder="Repeat password"
							value="${userTry.repeatedPassword }" />
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
					<label for="email" class="control-label col-sm-4">Email:</label>
					<div class="col-sm-7">
						<input type="email" class="form-control" name="email" id="email"
							placeholder="Enter email" maxlength="40" value="${userTry.email}"
							required />
						<c:if test="${not empty validEmail}">
							<c:if test="${not validEmail}">
								<p id="emailMsg" class="input-invalid">Email is not valid</p>
							</c:if>
						</c:if>
						<p id="emailMsg" class="input-invalid"></p>
					</div>
				</div>
				<br>
				<legend></legend>
				<div class="form-group">
						<div class="col-md-offset-4 col-sm-5">
						<input id = "registerButton"
							type="submit" class=" form-control btn btn-default"
							value="Register">
					</div>
				</div>
			</form>
		</div>
	</div>
</body>

<script>
	function checkIsEmailValid() {
		var email = document.forms["registerForm"]["email"].value;
		var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if (!filter.test(email)) {
			return false;
		}
		return true;
	}
	$('#registerForm')
			.submit(
					function(e) {
						e.preventDefault();
						var username = document.forms["registerForm"]["username"].value;
						var password = document.forms["registerForm"]["password"].value;
						var repeatedPassword = document.forms["registerForm"]["repeatedPassword"].value;
						var email = document.forms["registerForm"]["email"].value;
						if (!$('#usernameMsg').is(':empty')) {
							$("#usernameMsg").empty();
						}
						if (!$('#passwordMsg').is(':empty')) {
							$("#passwordMsg").empty();
						}
						if (!$('#emailMsg').is(':empty')) {
							$("#emailMsg").empty();
						}
						if (!$('#repeatedPasswordMsg').is(':empty')) {
							$("#repeatedPasswordMsg").empty();
						}
						var isUsernameUnique = true;
						var isUsernameValid = true;
						var isPasswordValid = true;
						var isRepeatedPasswordValid = true;
						var isEmailValid = true;
						if (username == "") {
							document.getElementById("usernameMsg").append(
									"Fill username");
							isUsernameValid = false;

						}
						if (password == "") {
							document.getElementById("passwordMsg").append(
									"Fill password");
							isPasswordValid = false;
						}
						if (repeatedPassword == "") {

							document.getElementById("repeatedPasswordMsg")
									.append("Fill repeated password");
							isRepeatedPasswordValid = false;
						}
						if (email == "") {
							document.getElementById("emailMsg").append(
									"Fill email");
							isEmailValid = false;
						}
						if (!(isUsernameValid === true
								&& isPasswordValid === true
								&& isRepeatedPasswordValid === true && isEmailValid === true)) {
							return false;
						}
						if (password !== repeatedPassword) {
							document.getElementById("repeatedPasswordMsg")
									.append("Repeated password is not valid");
							isRepeatedPasswordValid = false;

						} else {
							isRepeatedPasswordValid = true;

						}
						$
								.ajax({
									url : './IsUsernameUniqueServlet',
									type : 'GET',
									data : {
										"username" : username
									},
									success : function(response) {
										if (!$('#usernameMsg').is(':empty')) {
											$("#usernameMsg").empty();
											isUsernameUnique = true;
										}
										$
												.ajax({
													url : './IsUsernameValid',
													type : 'GET',
													data : {
														"username" : username
													},
													success : function(response) {
														if (!$('#usernameMsg')
																.is(':empty')) {
															$("#usernameMsg")
																	.empty();
															isUsernameValid = true;
														}
													},
													error : function(data) {
														if (!$('#usernameMsg')
																.is(':empty')) {
															$("#usernameMsg")
																	.empty();
														}
														isUsernameValid = false;
														document
																.getElementById(
																		"usernameMsg")
																.append(
																		"Username is not valid");
													}
												});
									},
									error : function(data) {
										if (!$('#usernameMsg').is(':empty')) {
											$("#usernameMsg").empty();
										}
										isUsernameUnique = false;
										document
												.getElementById("usernameMsg")
												.append(
														"Username already exists");
									}
								});
						$.ajax({
							url : './IsPasswordValid',
							type : 'GET',
							data : {
								"password" : password
							},
							success : function(response) {
								if (!$('#passwordMsg').is(':empty')) {
									$("#passwordMsg").empty();
									isPasswordValid = true;
								}
							},
							error : function(data) {
								if (!$('#passwordMsg').is(':empty')) {
									$("#passwordMsg").empty();
								}
								isPasswordValid = false;
								document.getElementById("passwordMsg").append(
										"Password is not valid");
							}
						});
						isEmailValid = checkIsEmailValid();
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
						$(document)
								.ajaxStop(
										function() {
											if ((isUsernameValid === true
													&& isUsernameUnique === true
													&& isPasswordValid === true
													&& isRepeatedPasswordValid === true && isEmailValid === true)) {
												document.getElementById(
														"registerForm")
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
</html>