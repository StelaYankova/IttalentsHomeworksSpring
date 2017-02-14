<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!--  <link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/css/bootstrap-select.min.css">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/js/bootstrap-select.min.js"></script>-->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
.ui-helper-hidden-accessible {
	display: none;
}
.alert {
	position: absolute;
	top: 81px;
	width: 100%;
}
.input-invalid{
	color:red;
}
ul.ui-autocomplete {
	list-style-type: none;
	text-decoration: none;
}

#image {
	position: relative;
	left: 850px;
}

#pageContent {
	position: absolute;
	top: 150px;
	left: 30px;
	width: 60%;
}

#addStudentButton{
position: relative;
	top: 10px;
}
.form-group {
	width: 30%
}
</style>
<body>
	<%@ include file="navBarTeacher.jsp"%>
<c:if test="${not empty sessionScope.invalidFields}">
		<c:if test="${not sessionScope.invalidFields}">
			<div class="alert alert-success"  id = "alert">
				<strong>Success!</strong> Indicates a successful or positive action.
			</div>
		</c:if>
	</c:if>
	<div id="image">
		<img src="images/logo-black.png" class="img-rounded" width="380" height="236">
	</div>
	<div id="pageContent">
												

		<div class="ui-widget">
			<form action="./AddStudentToGroupServlet" method="POST"
				class="form-inline" id = "addStudentToGroupForm">
				<c:if test="${not empty sessionScope.invalidFields}">
			<c:if test="${sessionScope.invalidFields}">
			<p style = "text-align:left" class="input-invalid">Invalid fields</p>
			</c:if>
		</c:if>
			<c:if test="${not empty sessionScope.emptyFields}">
				<c:if test="${sessionScope.emptyFields}">
					<p style="text-align: left" class="input-invalid">You cannot
						have empty fields</p>
				</c:if>
			</c:if>
			<c:if test="${not empty sessionScope.doesStudentExist}">
						<c:if test="${not sessionScope.doesStudentExist}">
							<p id="studentMsg" class="input-invalid">Student does not exist</p>
						</c:if>
						<c:if test="${not empty sessionScope.isStudentInGroup}">
							<c:if test="${sessionScope.doesStudentExist}">
								<c:if test="${sessionScope.isStudentInGroup}">
									<p id="studentMsg" class="input-invalid">Student is already in group</p>
								</c:if>
							</c:if>
						</c:if>
					</c:if>
															<p id = "groupMsg" class = "input-invalid"></p>
																					<p id = "studentMsg" class = "input-invalid"></p>
															
					<c:if test="${not empty sessionScope.validGroups}">

						<c:if test="${not sessionScope.validGroups}">
							<p id="groupsMsg" class="input-invalid">Invalid group</p>
						</c:if>
					</c:if>
				<div class="form-group">
				
					<label class="control-label col-sm-8">Choose group:</label> <select
						id="chosenGroup" name="chosenGroup" class="selectpicker">
						<option value="null">-</option>
						<c:forEach var="group" items="${applicationScope.allGroups}">
							<option value="${group.id}"><c:out value="${group.name}"></c:out></option>
						</c:forEach>
					</select>
					
				</div>
				<div class="form-group" id = "studentSearch">
					<label class="control-label col-sm-8">Choose student:</label> <input
						id="searchStudents" name="selectedStudent" class="form-control" value = "${sessionScope.chosenUsernameTry}" required/>
										</div>
						
					<button type="submit" id = "addStudentButton" class="btn btn-default btn-md">
						<span class="glyphicon glyphicon-plus">Add</span>
					</button>
			</form>
		</div>
		<br>
		<br>
		<ul id="listOfStudentsOfGroup" class="editable list-group"
			style="visibility: hidden; z-index: 1; height: 300px; width: 35%; overflow: hidden; overflow-y: scroll; overflow-x: scroll;"></ul>
	</div>
		<c:if test="${not empty sessionScope.invalidFields}">
		<c:remove var="invalidFields" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.emptyFields}">
		<c:remove var="emptyFields" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.doesStudentExist}">
		<c:remove var="doesStudentExist" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.isStudentInGroup}">
		<c:remove var="isStudentInGroup" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.validGroups}">
		<c:remove var="validGroups" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.chosenUsernameTry}">
		<c:remove var="chosenUsernameTry" scope="session" />
	</c:if>
	
	
</body>
<script>
$('#addStudentToGroupForm').submit(function(e) {
	e.preventDefault();
	var chosenGroupId = document.forms["addStudentToGroupForm"]["chosenGroup"].value;
	var chosenStudentUsername = document.forms["addStudentToGroupForm"]["selectedStudent"].value;
		
	var doesUserExist = true;
	var chosenStudentUsernameAlreadyInGroup = true;
	var chosenStudentUsernameEmpty = false;
	var chosenGroupEmpty = false;

	if(chosenGroupId == 'null'){	
		chosenGroupEmpty = true;
	}

	if (!$('#alert').is(':empty')) {
		$("#alert").remove();
	}
	if (!$('#groupMsg').is(':empty')) {
		$("#groupMsg").empty();
	}
	if (!$('#studentMsg').is(':empty')) {
		$("#studentMsg").empty();
	}
	if(chosenGroupEmpty === true){
		document.getElementById("groupMsg").append(
				"Choose group first");
		return false;
	}
	if(!chosenStudentUsername){
		chosenStudentUsernameEmpty = true;
	}
	
	if(chosenStudentUsernameEmpty === true){
		document.getElementById("studentMsg").append(
				"Student is empty");
		return false;
	}
	$.ajax({
		url : './DoesUserExist',
		type : 'GET',
		data : {
			"chosenStudentUsername":chosenStudentUsername
		},
		success : function(response) {
			if (!$('#studentMsg').is(':empty')) {
				$("#studentMsg").empty();
				doesUserExist = true;
			}
			$.ajax({
				url : './IsChosenStudentAlreadyInGroup',
				type : 'GET',
				data : {
					"chosenGroupId" : chosenGroupId,
					"chosenStudentUsername":chosenStudentUsername
				},
				success : function(response) {
					
					if (!$('#studentMsg').is(':empty')) {
						$("#studentMsg").empty();
					}
						chosenStudentUsernameAlreadyInGroup = false;
				},
				error : function(data) {
					if (!$('#studentMsg').is(':empty')) {
						$("#studentMsg").empty();
					}
					chosenStudentUsernameAlreadyInGroup = true;
					document.getElementById("studentMsg").append(
							"Student is already in group");
				}
			});
		},
		error : function(data) {				
			if (!$('#studentMsg').is(':empty')) {
				$("#studentMsg").empty();
			}
	doesUserExist = false;
			document.getElementById("studentMsg").append(
					"Student does not exist");
		}
	});

	$( document ).ajaxStop(function() {
if(chosenStudentUsernameAlreadyInGroup === false && doesUserExist === true){
	
	document.getElementById("addStudentToGroupForm").submit();
}});
});
function areYouSureRemove(e, username,groupId){		 
	if(confirm("Do you really want to do this?") ){
		//document.getElementById("removeStudent"+idEnd).submit();
	
	$.ajax({
				type : 'POST',
				url : "./RemoveStudentFromGroup",
				data: {
					"chosenStudentUsername": username,
					"chosenGroupId": groupId
				},
				success : function(data) {
					if (!$('#listOfStudentsOfGroup')
							.is(':empty')) {
						$("#listOfStudentsOfGroup")
								.empty();
					}
					getStudents(groupId);
					alert('Student is removed successfully')

				},
				error: function (error) {
			       // alert(error.);
			    }
			});
		} else {
			return false;
		}
	}

	$(document)
			.ready(
					function() {

						$('#chosenGroup')
								.change(
										function(event) {
											document.getElementById("searchStudents").value = "";

											if (!$('#listOfStudentsOfGroup')
													.is(':empty')) {
												$("#listOfStudentsOfGroup")
														.empty();
											}
											if (!$('#alert').is(':empty')) {
												$("#alert").remove();
											}
											var groupId = $(this).find(
													":selected").val();
											getStudents(groupId);
										});
						$(function() {
							var availableTags = new Array();
							<c:forEach items="${applicationScope.allStudents}" var="student">
							availableTags.push('${student.username}');
							</c:forEach>

							$("#searchStudents").autocomplete({
								source : availableTags,
								messages : {
									noResults : '',
									results : function() {
									}
								}
							});
						});
					});
	function getStudents(groupId){
		$
		.ajax({
			url : './getAllStudentsOfGroupServlet',
			type : 'GET',
			data : {
				"chosenGroupId" : groupId
			},
			dataType : 'json',
			success : function(
					response) {
				var div = document
						.getElementById("listOfStudentsOfGroup");
				//var areYouSureMsg = "Are you sure, that you want to remove the student from this group?";

				for ( var i in response) {
					console
							.log(response[i].username)
					var currUsername = response[i].username;
					/*	$('#listOfStudentsOfGroup').append("<li class='list-group-item'><form action = './RemoveStudentFromGroup' id = 'removeStudent"+response[i].username+"'  method = 'POST'>"+response[i].username+"<input type = 'hidden' name = 'chosenStudentUsername' value = "+response[i].username+"><input type = 'hidden' name = 'chosenGroupId' value = "+groupId+"><button style = 'background:transparent; borders:none; position:relative;float:right' type = 'button' class = 'btn btn-xs'  onclick = 'areYouSureRemove(this,\"" + response[i].username + "\")' ><span  class='badge glyphicon glyphicon-remove'>"+" "+"</span></button></form></li>");*/
					//	$('#listOfStudentsOfGroup').append("<li class='list-group-item'><form action = './RemoveStudentFromGroup' id = 'removeStudent"+response[i].username+"'  method = 'POST'>"+response[i].username+"<input type = 'hidden' name = 'chosenStudentUsername' value = "+response[i].username+"><input type = 'hidden' name = 'chosenGroupId' value = "+groupId+"><button style = 'background:transparent; borders:none; position:relative;float:right' type = 'button' class = 'btn btn-xs'  onclick = 'areYouSureRemove(this,\"" + response[i].username + "\")' ><span  class='badge glyphicon glyphicon-remove'>"+" "+"</span></button></form></li>");
					$(
							'#listOfStudentsOfGroup')
							.append(
									"<li class = 'list-group-item'>"
											+ response[i].username
											+ "<button style = 'background:transparent; borders:none; position:relative;float:right' type = 'button' class = 'btn btn-xs'  onclick = 'areYouSureRemove(this,\""
											+ response[i].username
											+ "\",\""
											+ groupId
											+ "\")' ><span  class='badge glyphicon glyphicon-remove'>"
											+ " "
											+ "</span></button></li>")
					document
							.getElementById('listOfStudentsOfGroup').style.visibility = 'visible';
					console
							.log(response[i].username)
				}
			}
		});
		
	}
	$(function () {
	      $.ajaxSetup({
	        statusCode: {
	          401: function () {
	            location.href = '/MyProject/index';
	          }
	        }
	      });
	    });
	function selectOption(index){ 
		  document.getElementById("chosenGroup").options.selectedIndex = index;
		}
	$(document).ready(function(e) {
		selectOption(0);
	});
</script>
</html>