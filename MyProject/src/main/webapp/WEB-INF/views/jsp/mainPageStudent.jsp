<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en"
	class=" js flexbox canvas canvastext webgl no-touch geolocation postmessage websqldatabase indexeddb hashchange history draganddrop websockets rgba hsla multiplebgs backgroundsize borderimage borderradius boxshadow textshadow opacity cssanimations csscolumns cssgradients cssreflections csstransforms csstransforms3d csstransitions fontface generatedcontent video audio localstorage sessionstorage webworkers applicationcache svg inlinesvg smil svgclippaths">
<!--<![endif]-->
<head>
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">

<!-- <base href="http://ittalents.bg/">
 --><meta charset="utf-8">
<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge">
 --><title>IT Talents</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
 <!--  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
 --></head>

<body>
				<%@ include file="navBarStudent.jsp"%>
			
				
</body>
<script>
$(function() {
	$.ajaxSetup({
		statusCode : {
			401 : function() {
				location.href = '/MyProject/index';
			},
			403 : function() {
				location.href = '/MyProject/forbiddenPage';
			},404 : function(){
				location.href = '/MyProject/pageNotFoundPage';
			},
			500 : function() {
				location.href = '/MyProject/exceptionPage';
			}
		}
	});
});
</script>
</html>