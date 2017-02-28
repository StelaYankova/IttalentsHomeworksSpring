<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
#image{
	position:absolute;
	   left: 850px;
	
}
</style>
<body>

<%@ include file="navBarTeacher.jsp"%>
<nav class="breadcrumb-nav">
	<ul class="breadcrumb">
<li><a href="http://localhost:8080/MyProject/GetMainPageTeacher">Home</a>
			<span class="divider"> <span class="accesshide "><span
					class="arrow_text"></span></span>
		</span></li></ul></nav>
<div id = "image">
     <img src="images/logo-black.png" class="img-rounded" width="380" height="236"> 
	</div>
<a href = "./AddStudentToGroupServlet">Add or remove student</a>
<a href = "./SeeGroups">See groups</a>
<a href = "./AddHomework">Add homework</a>

<a href = "./GetStudentsScoresServlet">Students homeworks</a>
<a href = "./SeeHomeworksServlet">See homeworks</a>
</body>
</html>