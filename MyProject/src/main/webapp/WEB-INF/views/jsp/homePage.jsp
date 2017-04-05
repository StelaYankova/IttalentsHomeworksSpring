<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%-- <link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
 --%><link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link href="<c:url value="css/homePageCss.css" />" rel="stylesheet">

<title>Insert title here</title>
</head>
<body>
<div class = "wrapMain">
	<%@ include file="navBarHomePage.jsp"%>
	<!-- <div style = "margin-left:auto;margin-right:auto;"> -->
	<div id="home" class="ng-scope">
		<div class="video">
			<video ng-if="!mobile" autoplay="" loop="" class="ng-scope"
				style="width: 100%;">
				<source type="video/mp4"
					src="http://ittalents.bg/video_sequence/IT_Talents_Web.mp4">
			</video>
		</div>
	</div><!-- </div> -->
	</div>
</body>
</html>