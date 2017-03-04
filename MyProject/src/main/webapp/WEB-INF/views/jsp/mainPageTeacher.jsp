<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
</style>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div id="pageWrapper">
		<nav class="breadcrumb-nav">
		<ul class="breadcrumb">
			<li><a href="http://localhost:8080/MyProject/GetMainPageTeacher">Home</a>
				<span class="divider"> <span class="accesshide "><span
						class="arrow_text"></span></span>
			</span></li>
		</ul>
		</nav>
		<div id="image">
			<img src="images/logo-black.png" class="img-rounded" width="300"
				height="200">
		</div>
		<div class="pageContent">
			<a href="./AddStudentToGroupServlet">Add or remove student</a> <a
				href="./SeeGroups">See groups</a> <a href="./AddHomework">Add
				homework</a> <a href="./GetStudentsScoresServlet">Students homeworks</a>
			<a href="./SeeHomeworksServlet">See homeworks</a>
		</div>
	</div>
</body>
</html>