<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<style>

</style>
<body>
					<%@ include file="footer.jsp"%>

	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div class="navbar-header" style="background-color: none; z-index: 1">
				<a class="navbar-brand" href="http://localhost:8080/MyProject/index"
					style="padding-top: 30px; padding-left: 80px;"> <img
					id="img-href" src="http://ittalents.bg/images/logo-white.png"
					height="70px" width="auto"></a>
			</div>
			<ul class="nav navbar-nav navbar-right"
				style="padding-top: 25px; padding-right: 25px">
				<li><a href="http://localhost:8080/MyProject/GetMainPageStudent" style="color: #ffffff"
					class="btn-lg">Home</a></li>
				<li><a href="http://localhost:8080/MyProject/UpdateYourProfileServlet" style="color: #ffffff"
					class="btn-lg">Your profile</a></li>
				<li><a href="http://localhost:8080/MyProject/SeeScoresServlet"
					class="btn btn-primary btn-lg dropdown-toggle"
					style="background: transparent; border: none; color: #ffffff">Your
						scores</a></li>
				<li class="dropdown"><a href="#"
					class="btn btn-primary btn-lg dropdown-toggle"
					style="background: transparent; border: none; color: #ffffff"
					onclick="seeGroups()" data-toggle="dropdown"> Your groups <span
						class="caret"></span>
				</a>
					<ul class="dropdown-menu" id="groups" style='background-color:#2E71AC'>
					</ul></li>
				<li><a href="http://localhost:8080/MyProject/LogoutServlet" style="color: #ffffff"
					class="btn-lg"> Logout </a></li>
			</ul>
		</div>
	</nav>
	<div id="homeworks"></div>				
	
	<script>
function seeGroups() {
	if(!$('#groups').is(':empty') ) {
		$( "#groups" ).empty();
	}
	history.pushState('', 'New Page Title', "http://localhost:8080/MyProject/GetHomeworksOfGroupsServlet");

	$.ajax({
		url : 'http://localhost:8080/MyProject/GetGroupsOfUserServlet',
		type : 'GET',
		dataType : 'json',
		success : function(response) {
			for ( var i in response) {
				console.log(response[i].id);

				$('#groups').append(
						"<li><a href = 'http://localhost:8080/MyProject/GetHomeworksOfGroupsServlet/"+response[i].id+"' method = 'GET' style='color:#ffffff;background-color:#2E71AC'>"
						+ response[i].name + "</a></li>");
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

</script>
</body>
</html>