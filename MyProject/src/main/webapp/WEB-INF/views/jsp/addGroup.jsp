<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%-- <link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
 --%><link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link href="<c:url value="css/addGroupCss.css" />" rel="stylesheet">
</head>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="./GetMainPageTeacher">Home</a>
					<span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li><a href="./seeGroups">See
						groups</a><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li>Create
						group<span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
			</ul>
		</nav>
	</div>
	<c:if test="${not empty invalidFields}">
			<c:if test="${not invalidFields}">
				<div class="alert alertAllPages alert-success">
					<strong>Success!</strong> Group has been added successfully
				</div>
			</c:if>
		</c:if>
	<div id="pageWrapper">
		
		<div id="formAddGroup">
		<legend>Add Group</legend>
		<c:if test="${not empty invalidFields}">
					<c:if test="${invalidFields}">
						<p class="input-invalid-or-empty">You have invalid fields</p>
					</c:if>
				</c:if>
				<c:if test="${not empty emptyFields}">
					<c:if test="${emptyFields}">
						<p class="input-invalid-or-empty">You cannot have empty fields</p>
					</c:if>
				</c:if>
			<form action="./createGroup" method="POST" id="addGroupForm">
				<div class="form-group">
					<label class="control-label col-sm-4">Name:</label>
					<div class="col-sm-7">
						<input type="text" name="groupName" class="form-control"
							placeholder="Enter group name" data-toggle="popover" value="${nameTry}"
							data-placement="bottom" data-trigger="focus" maxlength="15"
							data-content="Valid length is from 4 to 15 symbols. Valid inputs are numbers, letters (large and small) and main punctual symbols."
							required />
						<c:if test="${not empty validName}">
							<c:if test="${not validName}">
								<p id="nameMsg" class="input-invalid">Group name is not valid</p>
							</c:if>
							<c:if test="${not empty uniqueName}">
								<c:if test="${validName}">
									<c:if test="${not uniqueName}">
										<p id="nameMsg" class="input-invalid">Group name already exists</p>
									</c:if>
								</c:if>
							</c:if>
						</c:if>
						<p id="nameMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-sm-4">Teachers:</label>
					<div class="col-sm-7" style = "padding-bottom:15px;">
						<select class="selectpicker form-control" data-width="101%" data-size="7" multiple name="teachers" >
							<c:forEach items="${applicationScope.allTeachers}" var="teacher">
								<c:set var="isTeacherInGroupTry" value="false"></c:set>
								<c:forEach items="${selectedTeachersUsernameTry}"
									var="teacherUsername">
									<c:if test="${teacher.username eq teacherUsername}">
										<c:set var="isTeacherInGroupTry" value="true"></c:set>
									</c:if>
								</c:forEach>
								<c:if test="${isTeacherInGroupTry}">
									<option value="${teacher.username}" selected>
										<c:out value="${teacher.username}"></c:out></option>
								</c:if>
								<c:if test="${not isTeacherInGroupTry}">
									<option value="${teacher.username}">
										<c:out value="${teacher.username}"></c:out></option>
								</c:if>
							</c:forEach>
						</select>
						<c:if test="${not empty allTeachersExist}">
							<c:if test="${not allTeachersExist}">
								<p id="allTeachersExistMsg" class="input-invalid">Not all
									teachers exist</p>
							</c:if>
						</c:if>
						<p id="allTeachersExistMsg" class="input-invalid"></p>
					</div> 
				</div>
				 
				<legend></legend>
					<div class="form-group">
						<div class="col-md-offset-4 col-sm-5">
							<input
								id = "addButton"
								type="submit" class=" form-control btn btn-default"
								value="Add">
						</div>
					</div>
			</form>
		</div>
	</div>
	<script>
	$('#addGroupForm').submit(function(e) {
		e.preventDefault();
		var name = document.forms["addGroupForm"]["groupName"].value;
		var teachers = document.forms["addGroupForm"]["teachers"].value;
		var isNameValid = true;
		var areTeachersValid = true;
		if(name == ""){
			isNameValid = false;
		}
		if(teachers == ""){
			areTeachersValid = false;
		}
		if((isNameValid === false)){
			if (!$('#nameMsg').is(':empty')) {
				$("#nameMsg").empty();
			}
			document.getElementById("nameMsg").append(
					"Fill group name");
			return false;
		}
		if(areTeachersValid === false){
			if (!$('#allTeachersExistMsg').is(':empty')) {
				$("#allTeachersExistMsg").empty();
			}
			document.getElementById("allTeachersExistMsg").append(
					"Fill teachers");
			return false;
		}
		$.ajax({
			url : './IsGroupNameUnique',
			type : 'GET',
			data : {
				"name" : name
			},
			success : function(response) {
				if (!$('#nameMsg').is(':empty')) {
					$("#nameMsg").empty();
					isNameValid = true;
				}
				$.ajax({
					url : './IsGroupNameValid',
					type : 'GET',
					data : {
						"name" : name
					},
					success : function(response) {
						if (!$('#nameMsg').is(':empty')) {
							$("#nameMsg").empty();
							isNameValid = true;
						}
					},
					error : function(data) {
						if (!$('#nameMsg').is(':empty')) {
							$("#nameMsg").empty();
						}
						isNameValid = false;
						document.getElementById("nameMsg").append(
								"Group name is not valid");
					}
				});
				},
			error : function(data) {				
				if (!$('#nameMsg').is(':empty')) {
					$("#nameMsg").empty();
				}
				isNameValid = false;
				document.getElementById("nameMsg").append(
						"Group name already exists");
			}
		});
		$( document ).ajaxStop(function() {	
			if((isNameValid === true)){
				document.getElementById("addGroupForm").submit();
			}
		});
	});
	$(document).ready(function() {
		$('[data-toggle="popover"]').popover();
	});
	
	$(function () {
	      $.ajaxSetup({
	        statusCode: {
	          401: function () {
	            location.href = '/MyProject/index';
	          },
	          403: function () {
		            location.href = '/MyProject/forbiddenPage';
		      },
		      404 : function(){
					location.href = '/MyProject/pageNotFoundPage';
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