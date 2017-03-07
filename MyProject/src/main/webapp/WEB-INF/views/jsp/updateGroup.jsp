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
/* #formAddGroup {
	position: absolute;
	left: 60px;
	top: 220px;
	background-color: #ffffff;
	width: 500px;
} */
#formAddGroup {
	margin: 0 auto;
	margin-top: 80px;
	width: 42%;
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
				<li><a
					href="http://localhost:8080/MyProject/UpdateGroupServlet">Update
						group</a> <span class="divider"> <span class="accesshide "><span
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
		
		<div id="formAddGroup" align="right">
				<legend style="text-align: left">Update Group</legend>
		
			<form action="http://localhost:8080/MyProject/UpdateGroupServlet" method="POST"
				id="updateGroupForm">
				<!-- <label
					style="position: absolute; left: 290px; text-decoration: underline;">Update
					group</label> <br> <br> <br> -->
				<c:if test="${not empty sessionScope.invalidFields}">
					<c:if test="${sessionScope.invalidFields}">
						<p class="input-invalid-or-empty">Invalid fields</p>
					</c:if>
				</c:if>
				<c:if test="${not empty sessionScope.emptyFields}">
					<c:if test="${sessionScope.emptyFields}">
						<p class="input-invalid-or-empty">Empty fields</p>
					</c:if>
				</c:if>
				<div class="form-group">
					<label class="control-label col-sm-4" style="padding-right: 30px;">Name:</label>
					<div class="col-sm-7">
						<input type="text" class="form-control" name="groupName" maxlength="20"
							placeholder="Enter name" data-toggle="popover"
							class="form-control" value="${sessionScope.currGroup.name}"
							data-placement="bottom" data-trigger="focus" maxlength="20"
							data-content="Size of name - 4 to 15 symbols. Valid inputs are numbers and letters (large and small)"
							required />
						<c:if test="${not empty sessionScope.validName}">
							<c:if test="${not sessionScope.validName}">
								<p id="nameMsg" class="input-invalid">Name is not valid</p>
							</c:if>
							<c:if test="${not empty sessionScope.uniqueName}">
								<c:if test="${sessionScope.validName}">
									<c:if test="${not sessionScope.uniqueName}">
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
					<label class="control-label col-sm-4" style="padding-right: 30px;">Teachers</label>
					<div class="col-sm-7">
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
					</div>
				</div>
				<br><br>
				<!-- <div class="form-group">
					<div class="col-sm-offset-3 col-sm-2" style="left: 290px">
						<button style="align: right" type="submit" class="btn btn-default">Save</button>
					</div>
				</div> -->
				<legend></legend>
					<div class="form-group">
						<div class="col-md-offset-4 col-sm-5">
							<input
								style="/* margin-right: 30px;  */background-color: #2E71AC; color: #ffffff"
								type="submit" class=" form-control btn btn-default"
								value="Update">
						</div>
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
		var isNameValid = true;
		if(name == ""){
			isNameValid = false;
		}
		if((isNameValid === false) || (name.length < 4 && name.length > 15)){
			if (!$('#nameMsg').is(':empty')) {
				$("#nameMsg").empty();
			}
			document.getElementById("nameMsg").append(
					"Invalid size of name");
			return false;
		}
		$.ajax({
			url : 'http://localhost:8080/MyProject/IsGroupNameUniqueUpdate',
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