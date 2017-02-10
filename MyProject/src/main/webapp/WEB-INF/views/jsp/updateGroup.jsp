<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

<!--<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/css/bootstrap-select.min.css">

<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/js/bootstrap-select.min.js"></script>
  -->
<title>Insert title here</title>
</head>
<style>
.alert {
	position: absolute;
	top: 81px;
	width: 100%;
}
#image {
	position: relative;
	left: 850px;
}
.input-invalid{
	color:red;
}
#formAddGroup {
	position: absolute;
	left: 60px;
	top: 220px;
	background-color: #ffffff;
	width: 500px;
}

</style>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<c:if test="${not empty sessionScope.invalidFields}">
		<c:if test="${not sessionScope.invalidFields}">
			<div class="alert alert-success">
				<strong>Success!</strong> Indicates a successful or positive action.
			</div>
		</c:if>
	</c:if>
	<div id="image">
		<img src="images/logo-black.png" class="img-rounded" width="380" height="236">
	</div>

	<div id="formAddGroup" align="right">
		<form action="./UpdateGroupServlet" method="POST" id = "updateGroupForm">
			<label
				style="position: absolute; left: 290px; text-decoration: underline;">Update
				group</label> <br> <br> <br>
				<c:if test="${not empty sessionScope.invalidFields}">
			<c:if test="${sessionScope.invalidFields}">
			<p style = "text-align:center" class="input-invalid">Invalid fields</p>
			</c:if>
				
		</c:if>
			<c:if test="${not empty sessionScope.emptyFields}">
				<c:if test="${sessionScope.emptyFields}">
					<p style="text-align: center" class="input-invalid">You cannot
						have empty fields</p>
				</c:if>
			</c:if>
			<div class="form-group">
				<label class="control-label col-sm-6">Name</label>
				<div class="col-sm-6">
					<input type="text" name="groupName" maxlength="20" placeholder="Enter name" data-toggle="popover" class="form-control" value = "${sessionScope.currGroup.name}" data-placement="bottom" data-trigger="focus" maxlength="20"
						data-content="Size of name - 4 to 15 symbols. Valid inputs are numbers and letters (large and small)" />
					<c:if test="${not empty sessionScope.validName}">
						<c:if test="${not sessionScope.validName}">
							<p id="nameMsg" class="input-invalid">Invalid name</p>
						</c:if>
						<c:if test="${not empty sessionScope.uniqueName}">
							<c:if test="${sessionScope.validName}">
								<c:if test="${not sessionScope.uniqueName}">
									<p id="nameMsg" class="input-invalid">Name already exists</p>
								</c:if>
							</c:if>
						</c:if>
					</c:if>
					<p id = "nameMsg" class = "input-invalid"></p>
				</div>
			</div>
			<br>
			<div class="form-group">
				<label class="control-label col-sm-6">Teachers</label>
				<div class="col-sm-6">
					<select class="selectpicker" multiple name="teachers">

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
			<br>
			<br>
			<div class="form-group">
				<div class="col-sm-offset-3 col-sm-2" style="left: 290px">

					<button style="align: right" type="submit" class="btn btn-default">Save</button>
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
	<script>
	/*$('#updateGroupForm').submit(function(e) {
		e.preventDefault();
		var name = document.forms["updateGroupForm"]["groupName"].value;

		var isNameValid = true;
		if(name == ""){
			isNameValid = false;
		}
		console.log(name)
		if((isNameValid === false) || (name.length < 4 && name.length > 15)){

			if (!$('#nameMsg').is(':empty')) {
				$("#nameMsg").empty();
			}
			document.getElementById("nameMsg").append(
					"Invalid symbols or length");
			
			return false;
		}
		$.ajax({

			url : './IsGroupNameUniqueUpdate',
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
								"name is not valid");
						console.log("invalid name")

					}
				});
				},
			error : function(data) {				
				console.log(5)

				if (!$('#nameMsg').is(':empty')) {
					$("#nameMsg").empty();
				}
				isNameValid = false;
				document.getElementById("nameMsg").append(
						"Group with this name already exists");
				console.log("invalid heading")
			}
		});
		$( document ).ajaxStop(function() {
			
	if((isNameValid === true)){
		console.log(6)
		document.getElementById("updateGroupForm").submit();
	}else{
		return false;
	}
	});
	});
	$(document).ready(function() {
		$('[data-toggle="popover"]').popover();
	});*/
</script>
</body>
</html>