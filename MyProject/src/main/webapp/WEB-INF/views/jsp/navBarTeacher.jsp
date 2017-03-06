<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title><!-- 
   <script src="https://code.jquery.com/jquery-1.12.4.js"></script>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
 
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/css/bootstrap-select.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/js/bootstrap-select.min.js"></script>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.10.6/moment.min.js"></script>   
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
 <link rel="stylesheet" type="text/css"
	href="//cdn.datatables.net/1.10.12/css/jquery.dataTables.css">
<script type="text/javascript" charset="utf8"
	src="//cdn.datatables.net/1.10.12/js/jquery.dataTables.js"></script> -->
</head>
<!-- <style>



#pageWrapper{
 	margin-top: -10px;
 	margin-left: 170px;
	margin-right: 170px;
	margin-bottom: 70px;
}
.input-invalid-or-empty {
	color: red;
	text-align: left;
}

.input-invalid {
	color: red;
	text-align: center;
}
.breadcrumb > li + li:before {
color:#333;
        content: "\3E" /* Hex code for > */
    }
.alert {
	position: absolute;
	top: 81px;
	width: 100%;
	
}

.invalidData {
	color: red;
}

.footerText {
	color: white;
}

#footerTextPosition1 {
	posistion: absolute;
	margin-left: 15%;
	margin-top: 1%;
	float: left;
}

#footerTextPosition2 {
	posistion: absolute;
	margin-left: 30%;
	margin-top: 1%;
	float: left;
	overflow: hidden; /* if you don't want #second to wrap below #first */
}

#footerTextPosition3 {
	posistion: absolute;
	margin-left: 45%;
	margin-top: 1%;
	float: left;
}

#footerTextPosition3, #footerTextPosition2, #footerTextPosition1 {
	display: inline-block;
}

.footer {
	position: absolute;
	position: fixed;
	bottom: 0;
	height: 60px;
	background-color: #404040;
	width: 100%;
	overflow: hidden; /* will contain if #first is longer than #second */
	z-index: 1;
}

.navbar {
	background-color: #2E71AC;
	margin-bottom: 0px;
/* 	border-color: /* #2e6da4 #00BFFF;
 */}

.container {
	text-align: right;
}

#formFields {
	top: 110px;
	right: 0;
	width: 500px;
}

.navbar-nav>li>a, .navbar-brand {
	padding-top: 6px !important;
	height: 20px;
}

.navbar {
	min-height: 80px !important;
}
</style> -->
<body>
				<%@ include file="footer.jsp"%>


	<nav class="navbar navbar-inverse" style = "background-color:none">
		<div class="container-fluid" style = "background-color:none">
			<div class="navbar-header" style = "background-color:none; z-index:1">
				<a class="navbar-brand" href="http://localhost:8080/MyProject/index"
					style="padding-top: 30px; padding-left:80px;"> <img id = "img-href" src = "http://ittalents.bg/images/logo-white.png" height = "70px" width="auto"></a>
			</div>
			<ul class="nav navbar-nav navbar-right"
				style="padding-top: 25px; padding-right: 25px">
					<li><a href="http://localhost:8080/MyProject/GetMainPageTeacher" style="color: #ffffff"
					class="btn-lg">Home</a></li>
				<li><a href="http://localhost:8080/MyProject/UpdateYourProfileServlet" style="color: #ffffff"
					class="btn-lg">Your profile</a></li>
				<li><a href="http://localhost:8080/MyProject/SeeHomeworksServlet"
					class="btn btn-primary btn-lg dropdown-toggle"
					style="background: transparent; border: none; color: #ffffff">See/Update
						homeworks</a></li>
				<li class="dropdown"><a href="#"
					class="btn btn-primary btn-lg dropdown-toggle" type="button"
					data-toggle="dropdown"
					style="background: transparent; border: none; color: #ffffff">
						Manage<span class="caret"></span>
				</a>
					<ul class="dropdown-menu" style='background-color:#2E71AC'>
						<li><a href="http://localhost:8080/MyProject/AddStudentToGroupServlet"
							style="border: none; color: #ffffff; background-color:#2E71AC">Add
								or remove student</a></li>
						<li><a href="http://localhost:8080/MyProject/GetStudentsScoresServlet"
							style="border: none; color: #ffffff; background-color:#2E71AC">Students
								scores</a></li>
						<li><a href="http://localhost:8080/MyProject/SeeGroups"
							style="border: none; color: #ffffff; background-color:#2E71AC">See
								groups</a></li>
						<li><a href="http://localhost:8080/MyProject/AddHomework"
							style="border: none; color: #ffffff; background-color:#2E71AC">Add
								homework</a></li>
					</ul></li>		
				<li><a href="http://localhost:8080/MyProject/LogoutServlet" style="color: #ffffff"
					class="btn-lg"> Logout </a></li>
			</ul>
		</div>
	</nav>