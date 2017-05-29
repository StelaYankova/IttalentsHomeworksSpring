<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="//cdn.datatables.net/1.10.12/css/jquery.dataTables.css">
<script type="text/javascript" charset="utf8"
	src="//cdn.datatables.net/1.10.12/js/jquery.dataTables.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/css/bootstrap-select.min.css">
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/js/bootstrap-select.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css" />
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.10.6/moment.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
<script
	src="https://cdn.datatables.net/fixedcolumns/3.2.2/js/dataTables.fixedColumns.min.js"></script>
</head>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
<link href="<c:url value="css/navBarCss.css" />" type="text/css"
	rel="stylesheet">

<body>
	<%@ include file="footer.jsp"%>

	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="./index"> <img id="img-href"
					src="http://ittalents.bg/images/logo-white.png"></a>
			</div>

			<div class="collapse navbar-collapse">

				<ul class="nav navbar-nav navbar-right">
					<li>

						<div class="">
							<form class="form-inline" action="./login" method="POST"
								name="signInForm" id="signInForm">
								<div class="form-group">
									<input type="text" class=" form-control input-md"
										maxlength="25" value="${sessionScope.usernameTry}"
										placeholder="Username" name="username" />
								</div>
								<div class="form-group">
									<input type="password" class="form-control input-md"
										placeholder="Password" value="${sessionScope.passwordTry}"
										maxlength="15" name="password" />
								</div>
								<input id="signInButton" type="submit"
									class="btn btn-xs btn-default" value="Sign in">

							</form>
							<c:if test="${not empty sessionScope.invalidField}">
								<c:if test="${sessionScope.invalidField}">
									<p id="usernamePasswordMsg" class="input-invalid-login">Wrong
										username or password</p>
								</c:if>
							</c:if>
							<p id="usernamePasswordMsg" class="input-invalid-login"></p>
						</div>
					</li>
				</ul>
			</div>
			<a class="registerPageButton" href="./register"><span
				class="glyphicon glyphicon-log-in btn-xs"></span> Register here</a>
		</div>
	</nav>
	<c:if test="${not empty sessionScope.invalidField}">
		<c:remove var="invalidField" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.usernameTry}">
		<c:remove var="usernameTry" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.passwordTry}">
		<c:remove var="passwordTry" scope="session" />
	</c:if>

</body>
<script>
	$('#signInForm').submit(
			function(e) {
				e.preventDefault();

				var username = document.forms["signInForm"]["username"].value;
				var password = document.forms["signInForm"]["password"].value;

				if (!$('#usernamePasswordMsg').is(':empty')) {
					$("#usernamePasswordMsg").empty();
				}

				$.ajax({
					url : './validateLogin',
					method : 'GET',
					data : {
						"username" : username,
						"password" : password
					},
					success : function(response) {
						document.getElementById("signInForm").submit();
						return true;
					},
					error : function(response) {
						document.getElementById("usernamePasswordMsg").append(
								"Wrong username or password");
						return false;
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