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
.input-invalid{
	color:red;
}
#image {
	position: relative;
	left: 850px;
}

#formUpdateProfile {
	position: absolute;
	left: 60px;
	top: 220px;
	background-color: #ffffff;
	width: 500px;
}
.alert {
	position: absolute;
	top: 81px;
	width: 100%;
}
</style>
<body>
	<c:if test="${sessionScope.isTeacher == false}"><%@ include
			file="navBarStudent.jsp"%></c:if>
	<c:if test="${sessionScope.isTeacher == true}"><%@ include
			file="navBarTeacher.jsp"%></c:if>
<c:if test="${not empty invalidFields}">

	<c:if test="${not invalidFields}">
		<div class="alert alert-success">
			<strong>Success!</strong> Indicates a successful or positive action.
		</div>
	</c:if></c:if>

	<div id="image">
		<img src="images/logo-black.png" class="img-rounded" width="380" height="236">
	</div>
	<div id="formUpdateProfile" align="center">
		<label
			style="position: absolute; left: 290px; text-decoration: underline;">Update
			profile</label> <br> <br> <br>
			<c:if test="${not empty invalidFields}">
		
		<c:if test="${invalidFields}">
			<p style="text-align: center" class="input-invalid">Invalid
				fields</p>
		</c:if></c:if>


		<c:if test="${emptyFields}">
			<p class="input-invalid" style="width: 250px">You cannot have
				empty fields</p>
		</c:if>
		<form action="./UpdateYourProfileServlet" method="POST"
			id="updateForm" name="updateForm" class="form-horizontal">
			<div class="form-group">
				<label class="control-label col-sm-6">Username</label>
				<div class="col-sm-6">
					<c:out value="${sessionScope.user.username}"></c:out>
				</div>
			</div>
			<div class="form-group">
				<label class="control-label col-sm-6">Password</label>
				<div class="col-sm-6">
				
					<input type="password" class="form-control"
						value="${sessionScope.user.password}" name="password"
						placeholder="Enter password" maxlength = "15" data-toggle="popover"
						data-placement="bottom" data-trigger="focus"
						data-content="Size of password - 6 to 15 symbols. Valid inputs are numbers and letters (large and small)"
						required/>
						<c:if test="${not empty validPass}">

						<c:if test="${not validPass}">
							<p id="passwordMsg" class="input-invalid">Invalid password</p>
						</c:if>
					</c:if>
					<p id="passwordMsg" class="input-invalid"></p>
				</div>
			</div>
			<div class="form-group">
				<label class="control-label col-sm-6">Repeat new password</label>
				<div class="col-sm-6">
					<input type="password" class="form-control" placeholder="Repeat password"
						value="${sessionScope.user.password}" maxlength = "15" name="repeatedPassword"
						required/>
							<c:if test="${not empty validRepeatedPass}">
						<c:if test="${not validRepeatedPass}">
							<p id="repeatedPasswordMsg" class="input-invalid">Passwords
								are different</p>
						</c:if>
					</c:if>
					<p id="repeatedPasswordMsg" class="input-invalid"></p>
				</div>
			</div>

			<div class="form-group">
				<label class="control-label col-sm-6">Email</label>
				<div class="col-sm-6">
					<input type="email" class="form-control" placeholder="Enter email"
						value="${sessionScope.user.email}" name="email" required/>
						<c:if test="${not empty validEmail}">
						<c:if test="${not validEmail}">
							<p id="emailMsg" class="input-invalid">Invalid email</p>
						</c:if>
					</c:if>
					<p id="emailMsg" class="input-invalid"></p>
				</div>
			</div>
			<br>
			<div class="form-group">
				<div class="col-sm-offset-3 col-sm-2" style="left: 290px">
					<input style="align: right" type="submit" class="btn btn-default"
						value="Register">
				</div>
			</div>
		</form>
	</div>
	
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

							document.getElementById("passwordMsg").append("Fill password");
						isPasswordValid = false;
					}
						if (repeatedPassword == "") {

							document.getElementById("repeatedPasswordMsg").append("Fill password");
							isRepeatedPasswordValid = false;
					}
						if (email == "") {
								document.getElementById("emailMsg").append("Fill email");
							isEmailValid = false;
						}

						
	if ((isPasswordValid === true
								&& isRepeatedPasswordValid === true && isEmailValid === true)) {
							if (password !== repeatedPassword) {
								document.getElementById("repeatedPasswordMsg")
										.append("Repeated is not valid");
								isRepeatedPasswordValid = false;

							} else {
								isRepeatedPasswordValid = true;

							}
							console.log(password.length)
							if (password.length !== 32) {
								console.log(000)
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
												console.log("invalid pass")
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

							console.log(isPasswordValid)
							console.log(isEmailValid)
						
	console.log(isRepeatedPasswordValid)

							if (password.length !== 32) {
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
							} else {
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
				},
				500 : function() {
					location.href = '/MyProject/exceptionPage';
				}
			}
		});
	});
</script>

</html>