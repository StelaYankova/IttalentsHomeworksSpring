<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
body {
	background:
		url("http://localhost:8080/MyProject/images/exception.png")
		no-repeat center center fixed;
	-webkit-background-size: cover;
	-moz-background-size: cover;
	-o-background-size: cover;
	
    background-size:100% 87vh;
}
</style>
<body>
	<c:if test="${sessionScope.isTeacher == false}">
		<%@ include file="navBarStudent.jsp"%>
	</c:if>
	<c:if test="${sessionScope.isTeacher == true}">
		<%@ include file="navBarTeacher.jsp"%>
	</c:if>

</body>
</html>