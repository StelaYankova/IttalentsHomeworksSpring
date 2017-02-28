<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
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
</head>
<style>

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
	
	<div class="footer">
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
	</div>

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
			<li>
					<a href = "./SeeScoresServlet" class="btn btn-primary btn-lg dropdown-toggle" style = "background:transparent; border:none; color: #9d9d9d">Your scores</a>
			</li>
			<li><a href="./LogoutServlet" style="color: #9d9d9d"
				class="btn-lg"> Logout </a></li>

			<li class="dropdown">
			
				<a href = "#" class="btn btn-primary btn-lg dropdown-toggle" style = "background:transparent; border:none; color: #9d9d9d" 
					onclick="seeGroups()" data-toggle="dropdown">
					Your groups <span class="caret"></span>
				</a>
				<ul class="dropdown-menu" id="groups">
					
				</ul>
			</li>
		</ul>
	</div>
	</nav>
	<div id="homeworks"></div>
	<script>
function seeGroups() {
	if(!$('#groups').is(':empty') ) {
		$( "#groups" ).empty();
	}
	$.ajax({
		url : './GetGroupsOfUserServlet',
		type : 'GET',
		dataType : 'json',
		success : function(response) {
			for ( var i in response) {
				$('#groups').append(
						/*"<li><a href = '#' style = 'background-color: #2E71AC;border-color: #2e6da4; color: #9d9d9d' ><form action = './GetHomeworksOfGroupsServlet' method = 'GET'><input type = 'hidden' name = 'groupId' value = "+response[i].id+"><button type = 'button' style ='padding: 0;border: none;background: none; color: #9d9d9d'>"
								+ response[i].name + "</button></form></a></li>");*/
						"<li><form action = './GetHomeworksOfGroupsServlet' method = 'GET' style='background-color:#2E71AC'><input type = 'hidden' name = 'groupId' value = "+response[i].id+"><button class='btn btn-link' type = 'submit' style ='padding: 0;border: none;background: #2E71AC; color: #9d9d9d'>"
						+ response[i].name + "</button></form></li>");
			}
		}
	});
}
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
/*function seeHomeworks(groupId) {
	if(!$('#homeworks').is(':empty') ) {
		$( "#homeworks" ).empty();
	}
	console.log(groupId);
	$.ajax({
		url : './GetHomeworksOfGroupsServlet',
		type : 'GET',
		dataType : 'json',
		data : {
			groupId : groupId
		},
		success : function(response) {
			for ( var h in response) {
				var id = response[h].id;console.log(id)
				$('#homeworks').append('<br><form action = "./GetHomeworkServlet" method = "GET"><input type = "hidden" name = "id" value = ' +id + '><button type = "submit">' + response[h].heading + '</form>');
				homeworkId = id;
				$('#homeworks').append(
						'  ' + response[h].timeLeft + ' days left');

			}
		}
	});
}*/
/*<button style = 'background:#9d9d9d; border: none;color:#9d9d9d;' id = 'response[i].id' onclick = 'seeHomeworks("
	+ response[i].id + ")'>"
	+ response[i].name + "</button></a>*/
</script>
</body>
</html>