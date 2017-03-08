<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<!-- <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="//cdn.datatables.net/1.10.12/css/jquery.dataTables.css">
<script type="text/javascript" charset="utf8"
	src="//cdn.datatables.net/1.10.12/js/jquery.dataTables.js"></script>
	 -->
</head>
<!-- <style>

#pageWrapper{
	margin-left: 20px;
	margin-right: 20px;
	margin-bottom: 50px;
}
.input-invalid {
	color: red;
	text-align: center;
}
.alert {
	position: absolute;
	top: 81px;
	width: 100%;
}
#image {
	position: absolute;
	margin-left: 1000px;
	margin-right: 20px;
	margin-top: -60px;
}

.footer{
   position: absolute;
  position: fixed;
    bottom: 0;
    height: 60px;
 background-color:  #404040;
     width: 100%;
     overflow: hidden; /* will contain if #first is longer than #second */
     z-index:1;
}

.input-invalid-login{
position:absolute;
top:30px;
right:270px;
	color:red;
}
.footerText{
	color:white;
}

#footerTextPosition1{
	posistion:absolute;
		margin-left:15%;
		margin-top:1%;
float:left;		   
		
		
}
#footerTextPosition2{
	posistion:absolute;
		margin-left:30%;
		margin-top:1%;		
		float:left;
		    overflow: hidden; /* if you don't want #second to wrap below #first */
}
#footerTextPosition3{
	posistion:absolute;
		margin-left:45%;
		margin-top:1%;		
		
float:left;	
}
#footerTextPosition3,#footerTextPosition2,#footerTextPosition1{
  display: inline-block;

}

.navbar {
	background-color: #2E71AC;
/* 	border-color: #2e6da4;
 */		margin-bottom: 0px;
	
}
.container{
	    text-align: right;
		padding-left:150px;
		padding-right:50px;
}
.header-menu {
    float: left;
    line-height: 26px;
    margin-left:10px;
}
#formFields {
	top: 110px;
	right: 0;
	width: 500px;
}

.navbar-nav > li > a, .navbar-brand {
    padding-top:6px !important; 
    height: 20px;
}

.navbar {min-height:28px !important;}
#formContainer{
	padding-left:200px;
}
.navbar-header{
	z-index:1;
}
#img-href{
	position:absolute;
	z-index:1;
}
</style> -->
<body>
					<%@ include file="footer.jsp"%>

	<!-- <div class="footer">
		<div id="footerTextPosition1">
			<p class="footerText">
				Address:<br>bul. "Bulgaria" 69, 1404 Sofia
			</p>
		</div>
		<div id="footerTextPosition2">
			<p class="footerText">
				Email:<br> info@ittalents.bg
			</p>
		</div>
		<div id="footerTextPosition3">
			<p class="footerText">
				Phone:<br> 088xxxxxxx
			</p>
		</div> 
	</div>-->

	<nav class="navbar navbar-inverse" style = "background-color:none">
		<div class="container-fluid" style = "background-color:none">
			<div class="navbar-header" style = "background-color:none; z-index:1">
				<a class="navbar-brand" href="http://localhost:8080/MyProject/index"
					style="padding-top: 30px; padding-left:80px;"> <img id = "img-href" src = "http://ittalents.bg/images/logo-white.png" height = "70px" width="auto"></a>
			</div>
			<ul class="nav navbar-nav navbar-right header-menu"  >
			
				<li>
				
					<div class="container">
						<form class="form-inline" action="http://localhost:8080/MyProject/LoginServlet" method="POST"
							name="signInForm" id="signInForm">
							<div class="form-group"><input type="text"
									class=" form-control input-md" maxlength="15"
									value="${sessionScope.usernameTry}" placeholder = "Username" name="username"  required/>
							</div>
							<div class="form-group"><input type="password"
									class="form-control input-md" placeholder = "Password" value="${sessionScope.passwordTry}"
									maxlength="15" name="password"  required/>
							</div>
							<input style="align: right" type="submit"
								class="btn btn-xs btn-default" value="Sign in">

						</form>
						<c:if test="${not empty sessionScope.invalidField}">
							<c:if test="${sessionScope.invalidField}">
								<p id="usernamePasswordMsg" class="input-invalid-login">Wrong
									username/password</p>
							</c:if>
						</c:if>
						<p id="usernamePasswordMsg" class="input-invalid-login"></p>
						
					</div>
				</li>
				<div class="container">
					<li>
						<a href="http://localhost:8080/MyProject/RegisterServlet" style="color: #9d9d9d">
							<span class="glyphicon glyphicon-log-in btn-sm"></span>Register here
						</a>
					</li>
				</div>
			</ul>
		</div>
	</nav>
	<c:if test="${not empty sessionScope.invalidFields}">
		<c:remove var="invalidFields" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.usernameTry}">
		<c:remove var="usernameTry" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.passwordTry}">
		<c:remove var="passwordTry" scope="session" />
	</c:if>
	
</body>
<script>
	$('#signInForm')
	.submit(
			function(e) {
				e.preventDefault();

	var username = document.forms["signInForm"]["username"].value;
	var password = document.forms["signInForm"]["password"].value;

	if (!$('#usernamePasswordMsg').is(':empty')) {
		$("#usernamePasswordMsg").empty();
	}
	
	$.ajax({
		url: 'http://localhost:8080/MyProject/ValidateLogin',
		method: 'GET',
		data:{
			"username": username,
			"password": password
		},
		success: function(response){
			document.getElementById("signInForm").submit();
			return true;
		},
		error: function(response){
			document.getElementById("usernamePasswordMsg").append("Wrong username/password");
			return false;
		}
	});
});
	$(function () {
	      $.ajaxSetup({
	        statusCode: {
	          401: function () {
	            location.href = '/MyProject/index';
	          },
	          403: function () {
		            location.href = '/MyProject/forbiddenPage';
		      },
		      500: function(){
		    	  location.href = '/MyProject/exceptionPage';
		      }
	        }
	      });
	    });
</script>
</html>