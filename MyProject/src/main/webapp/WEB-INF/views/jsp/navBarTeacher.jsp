<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
  <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
 
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/css/bootstrap-select.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/js/bootstrap-select.min.js"></script>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.10.6/moment.min.js"></script>   
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
 <link rel="stylesheet" type="text/css"
	href="//cdn.datatables.net/1.10.12/css/jquery.dataTables.css">
<script type="text/javascript" charset="utf8"
	src="//cdn.datatables.net/1.10.12/js/jquery.dataTables.js"></script>
</head>
<style>
 .input-invalid-or-empty{
 	color: red;
	text-align: left;
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
.invalidData {
	color: red;
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
  .footer {
  position: absolute;
  position: fixed;
    bottom: 0;
    height: 60px;
 background-color:  #404040;
     width: 100%;
     overflow: hidden; /* will contain if #first is longer than #second */
     z-index:1;
  } 
.navbar {
	background-color: #2E71AC;
	border-color: #2e6da4;
}
.container{
	    text-align: right;
	
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

.navbar {min-height:80px !important;}
</style>
<body>
	
	<footer class="footer">
	
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
		
	</footer>

	<nav class="navbar navbar-inverse">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" href="homePage.jsp" style="padding-top: 30px">
				ITTalents Homework System</a>
		</div>
		<ul class="nav navbar-nav navbar-right"
			style="padding-top: 25px; padding-right: 25px">
			<li><a href="./UpdateYourProfileServlet" style="color: #9d9d9d"
				class="btn-lg">Your profile</a></li>
			<li><a href = "./SeeHomeworksServlet" class="btn btn-primary btn-lg dropdown-toggle"
				style="background: transparent; border: none; color: #9d9d9d">See/Update homeworks</a></li>
					
			<li><a href="./LogoutServlet" style="color: #9d9d9d"
				class="btn-lg"> Logout </a></li>

			<li class="dropdown">
				<a href = "#" class="btn btn-primary btn-lg dropdown-toggle" type="button"
					data-toggle="dropdown" style="background: transparent; border: none; color: #9d9d9d">
					Manage<span class="caret"></span>
				</a>
				  <ul class="dropdown-menu" style='background-color:#2E71AC'>

					<li><a href="./AddStudentToGroupServlet" style="background: transparent; border: none; color: #9d9d9d">Add or remove
							student</a></li>
					<li><a href="./GetStudentsScoresServlet" style="background: transparent; border: none; color: #9d9d9d">Students
							scores</a></li>
					<li><a href = "./SeeGroups" style="background: transparent; border: none; color: #9d9d9d">See groups</a></li>
					<li><a href = "./AddHomework" style="background: transparent; border: none; color: #9d9d9d">Add homework</a></li>
				</ul>
				</li>
		</ul>
	</div>
	</nav>

