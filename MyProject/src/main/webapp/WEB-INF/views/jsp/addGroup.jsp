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
#formAddGroup {
	position: absolute;
	left: 60px;
	top: 220px;
	background-color: #ffffff;
	width: 500px;
	margin: 0 auto;
}
</style>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="http://localhost:8080/MyProject/GetMainPageTeacher">Home</a>
					<span class="divider"> <span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li><a href="http://localhost:8080/MyProject/SeeGroups">See
						groups</a> <span class="divider"> <span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li><a href="http://localhost:8080/MyProject/AddGroupServlet">Create
						group</a> <span class="divider"> <span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
			</ul>
		</nav>
	</div>
	<div id="pageWrapper">
		<c:if test="${not empty invalidFields}">
			<c:if test="${not invalidFields}">
				<div class="alert alert-success">
					<strong>Success!</strong> Group has been added successfully
				</div>
			</c:if>
		</c:if>
		<div id="formAddGroup" align="right">
			<form action="http://localhost:8080/MyProject/AddGroupServlet" method="POST" id="addGroupForm">
				<!-- <label
					style="position: absolute; left: 290px; text-decoration: underline;">New
					group</label> <br> <br> <br> -->
								<legend style="text-align: left">Add Group</legend>
					
				<c:if test="${not empty invalidFields}">
					<c:if test="${invalidFields}">
						<p class="input-invalid-or-empty">Invalid fields</p>
					</c:if>
				</c:if>
				<c:if test="${not empty emptyFields}">
					<c:if test="${emptyFields}">
						<p class="input-invalid-or-empty">Empty fields</p>
					</c:if>
				</c:if>
				<div class="form-group">
					<label class="control-label col-sm-4">Name:</label>
					<div class="col-sm-7">
						<input type="text" name="groupName" class="form-control"
							placeholder="Enter name" data-toggle="popover" value="${nameTry}"
							data-placement="bottom" data-trigger="focus" maxlength="20"
							data-content="Size of name - 4 to 15 symbols. Valid inputs are numbers and letters (large and small)."
							required />
						<c:if test="${not empty validName}">
							<c:if test="${not validName}">
								<p id="nameMsg" class="input-invalid">Name is not valid</p>
							</c:if>
							<c:if test="${not empty uniqueName}">
								<c:if test="${validName}">
									<c:if test="${not uniqueName}">
										<p id="nameMsg" class="input-invalid">Name already exists</p>
									</c:if>
								</c:if>
							</c:if>
						</c:if>
						<p id="nameMsg" class="input-invalid"></p>
					</div>
				</div>
				<br>
				<div class="form-group">
					<label class="control-label col-sm-4">Teachers:</label>
					<div class="col-sm-7">
						<select class="selectpicker" multiple name="teachers">
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
					</div>
				</div>
				<br> <br> <br> <br>
				<!-- <div class="form-group">
					<div class="col-sm-offset-3 col-sm-2" style="left: 290px">
						<input style="align: right" type="submit" class="btn btn-default"
							value="Save">
					</div>
				</div> -->
				<div class="form-group">
					<div class="col-md-offset-7 col-sm-5">
						<input
							style="margin-right: 30px; background-color: #2E71AC; color: #ffffff"
							type="submit" class=" form-control btn btn-default"
							value="Register">
					</div>
				</div>
			</form>
		</div>
	</div>
	<script>
	$('#addGroupForm').submit(function(e) {
		e.preventDefault();
		var name = document.forms["addGroupForm"]["groupName"].value;
		var isNameValid = true;
		if(name == ""){
			isNameValid = false;
		}
		if((isNameValid === false) || (name.length < 4 || name.length > 15)){
			if (!$('#nameMsg').is(':empty')) {
				$("#nameMsg").empty();
			}
			document.getElementById("nameMsg").append(
					"Invalid size of name");
			return false;
		}
		$.ajax({
			url : 'http://localhost:8080/MyProject/IsGroupNameUnique',
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
					url : 'http://localhost:8080/MyProject/IsGroupNameValid',
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
								"Name is not valid");
					}
				});
				},
			error : function(data) {				
				if (!$('#nameMsg').is(':empty')) {
					$("#nameMsg").empty();
				}
				isNameValid = false;
				document.getElementById("nameMsg").append(
						"Name already exists");
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
		      500: function(){
		    	  location.href = '/MyProject/exceptionPage';
		      }
	        }
	      });
	    });
</script>
</body>
</html>