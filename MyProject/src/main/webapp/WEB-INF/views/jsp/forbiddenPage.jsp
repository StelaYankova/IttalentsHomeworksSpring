<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
<link href="<c:url value="css/forbiddenPageCss.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link rel="icon" type="image/png" href="./images/favIcon.png">
</head>
<body>
	<c:if test="${sessionScope.isTeacher == false}">
		<%@ include file="navBarStudent.jsp"%>
	</c:if>
	<c:if test="${sessionScope.isTeacher == true}">
		<%@ include file="navBarTeacher.jsp"%>
	</c:if>
</body>
</html>