<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/css/bootstrap-select.min.css">

<!-- Latest compiled and minified JavaScript -->
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/js/bootstrap-select.min.js"></script>
<title>Insert title here</title>
</head>
<style>
#image{
	position:absolute;
	   left: 850px;
	
}
</style>
<body>
	<%@ include file="navBarStudent.jsp"%>
<div id = "image">
     <img src="images/logo-black.png" class="img-rounded" width="380" height="236"> 
	</div>
	<!--<br>
	<br>
	<a href = "./LogoutServlet"> Logout </a>
	<a href = "./UpdateYourProfileServlet">Your profile</a>
	<button onclick="seeGroups()">Your groups:</button>
	<form action = "./SeeScoresServlet" method = "GET">
		<button type = "submit">Your scores</button>
	</form>
	<div id="groups">
	</div>
	<div id = "homeworks">
	</div>
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
								"<button id = 'response[i].id' onclick = 'seeHomeworks("
										+ response[i].id + ")'>"
										+ response[i].name + "</button>");
					}
				}
			});
		}

		function seeHomeworks(groupId) {
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
		}
	</script>-->
</body>
</html>