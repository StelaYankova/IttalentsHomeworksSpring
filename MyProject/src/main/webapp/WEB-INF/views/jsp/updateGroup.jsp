<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
<link href="<c:url value="css/updateGroupCss.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">

</head>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="./mainPageTeacher">Home</a><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li><a href="./seeGroups">See groups</a><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li><c:out value="${sessionScope.currGroup.name}"></c:out><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
			</ul>
		</nav>
	</div>
	<c:if test="${not empty sessionScope.invalidFields}">
			<c:if test="${not sessionScope.invalidFields}">
				<div class="alert alertAllPages alert-success">
					<strong>Success!</strong> Group has been updated successfully
				</div>
			</c:if>
		</c:if>
	<div id="pageWrapper">
		
		<div id="formUpdateGroup"><!-- align="right" -->
				<legend>Update Group</legend>
		
			<form action="./updateGroup" method="POST"
				id="updateGroupForm" class = "form-horizontal">
				<c:if test="${not empty sessionScope.invalidFields}">
					<c:if test="${sessionScope.invalidFields}">
						<p class="input-invalid-or-empty">You have invalid fields</p>
					</c:if>
				</c:if>
				<c:if test="${not empty sessionScope.emptyFields}">
					<c:if test="${sessionScope.emptyFields}">
						<p class="input-invalid-or-empty">You cannot have empty fields</p>
					</c:if>
				</c:if>
				<div class="form-group">
					<label class="control-label">Name:</label>
					<div class="control-label-input">
						<input type="text" class="form-control" name="groupName" maxlength="15"
							placeholder="Enter group name" data-toggle="popover"
							class="form-control" value="${sessionScope.currGroup.name}"
							data-placement="bottom" data-trigger="focus" 
							data-content="Valid length is from 4 to 15 symbols. Valid inputs are numbers and letters (large and small)"
							required />
						<c:if test="${not empty sessionScope.validName}">
							<c:if test="${not sessionScope.validName}">
								<p id="nameMsg" class="input-invalid">Group name is not valid</p>
							</c:if>
							<c:if test="${not empty sessionScope.uniqueName}">
								<c:if test="${sessionScope.validName}">
									<c:if test="${not sessionScope.uniqueName}">
										<p id="nameMsg" class="input-invalid">Group name already exists</p>
									</c:if>
								</c:if>
							</c:if>
						</c:if>
						<p id="nameMsg" class="input-invalid"></p>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label">Teachers</label>
					<div class="control-label-input">
						<select class="selectpicker form-control" multiple name="teachers">
							<c:forEach items="${applicationScope.allTeachers}" var="teacher">
								<c:set var="isTeacherInGroup" value="false"></c:set>
								<c:forEach items="${teacher.groups}" var="group">
									<c:if test="${group.id==sessionScope.currGroup.id}">
										<c:set var="isTeacherInGroup" value="true"></c:set>
									</c:if>
								</c:forEach>
								<c:if test="${isTeacherInGroup}">
									<option value="${teacher.username}" selected>
										<c:out value="${teacher.username}"></c:out></option>
								</c:if>
								<c:if test="${not isTeacherInGroup}">
									<option value="${teacher.username}">
										<c:out value="${teacher.username}"></c:out></option>
								</c:if>
							</c:forEach>
						</select>
						<c:if test="${not empty sessionScope.allTeachersExist}">
							<c:if test="${not sessionScope.allTeachersExist}">
								<p id="allTeachersExistMsg" class="input-invalid">Not all
									teachers exist</p>
							</c:if>
						</c:if>
						<p id="allTeachersExistMsg" class="input-invalid"></p>
					</div>
				</div>
				<legend></legend>
					<div class="form-group">
 						<input id = "updateGroupButton"
								type="submit" class=" form-control btn btn-default"
								value="Update">
						</div>
			</form>
		</div>
		<c:if test="${not empty sessionScope.invalidFields}">
			<c:remove var="invalidFields" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.emptyFields}">
			<c:remove var="emptyFields" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.validName}">
			<c:remove var="validName" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.uniqueName}">
			<c:remove var="uniqueName" scope="session" />
		</c:if>
		<c:if test="${not empty sessionScope.allTeachersExist}">
			<c:remove var="allTeachersExist" scope="session" />
		</c:if>
	</div>
	<script>
	$('#updateGroupForm').submit(function(e) {
		e.preventDefault();
		var name = document.forms["updateGroupForm"]["groupName"].value;
		var teachers = document.forms["updateGroupForm"]["teachers"].value;
		var areTeachersValid = true;
		var isNameValid = true;
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
			url : './isGroupNameUniqueUpdate',
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
					url : './isGroupNameValid',
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
		document.getElementById("updateGroupForm").submit();
	}else{
		return false;
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
		      },404 : function(){
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