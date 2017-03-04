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
</style>
<body>
	<%@ include file="navBarStudent.jsp"%>
	<div id="pageWrapper">
		<nav class="breadcrumb-nav">
		<ul class="breadcrumb">
			<li><a href="http://localhost:8080/MyProject/GetMainPageStudent">Home</a>
				<span class="divider"> <span class="accesshide "><span
						class="arrow_text"></span>&nbsp;</span>
			</span></li>
		</ul>
		</nav>
		<div id="image">
			<img src="images/logo-black.png" class="img-rounded" width="380"
				height="236">
		</div>
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