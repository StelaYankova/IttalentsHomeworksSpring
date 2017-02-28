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
.input-invalid {
	color: red;
}

#textareaComment {
	max-width: 40%;
}

#currTaskSolution {
	max-width: 50%;
}

#image {
	position: absolute;
	left: 850px;
}

#pageContent {
	position: absolute;
	left: 50px;
	padding: 10px;
	padding-bottom: 75px;
}

label {
	display: inline-block;
	*display: inline; /* for IE7*/
	zoom: 1; /* for IE7*/
	float: left;
	padding-top: 5px;
	text-align: left;
	width: 140px;
}
​
</style>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<nav class="breadcrumb-nav">
	<ul class="breadcrumb">
<li><a href="http://localhost:8080/MyProject/GetMainPageTeacher">Home</a>
			<span class="divider"> <span class="accesshide "><span
					class="arrow_text"></span></span>
		</span></li>
		<li><a href="http://localhost:8080/MyProject/GetStudentsScoresServlet">See student's scores</a>
			<span class="divider"> <span class="accesshide "><span
					class="arrow_text"></span></span>
		</span></li>
		<li><a href="http://localhost:8080/MyProject/GetCurrHomeworkOfStudent">Current chosen homework</a>
			<span class="divider"> <span class="accesshide "><span
					class="arrow_text"></span>&nbsp;</span>
		</span></li>
		</ul></nav>
	<div id="image">
		<img src="images/logo-black.png" class="img-rounded" width="380" height="236">
	</div>
	<div id="pageContent">

		<br>
		<form style="display: inline" action="./ReadHomeworkServlet"
			method="GET">
			<input type='hidden'
				value='${sessionScope.currHomework.homeworkDetails.tasksFile}'
				name='fileName'>
			<button class='btn btn-link' style='text-decoration: none'
				type='submit'>
				<b><c:out
						value="${sessionScope.currHomework.homeworkDetails.heading }" /></b>
			</button>
		</form>
		<b><u> - until <c:out
					value="${sessionScope.currHomework.homeworkDetails.closingTime }" /></u></b>


		<br> <br>
		<c:if test="${not empty sessionScope.invalidFields}">

			<c:if test="${sessionScope.invalidFields}">
				<p style="text-align: left" class="input-invalid">Invalid fields</p>
			</c:if>
		</c:if>
		

		<c:if test="${sessionScope.emptyFields}">
			<p class="input-invalid" style="width: 250px">You cannot have
				empty fields and max value for grade is 100</p>
		</c:if>
		<br>
		<form action="./UpdateTeacherGradeAndCommentServlet" method="POST"
			id="UpdateTeacherGradeAndCommentForm">
			<div class="block">
				<label><b>Teacher grade:</b></label>
				<div class="col-xs-2">
					<input type="number" class="form-control" min=0 max=100 id="grade"
						value="${sessionScope.currHomework.teacherGrade}" name="grade" />
				</div>
			</div>
			<br> <br>
			<c:if test="${not empty sessionScope.GradeTooLong}">

				<c:if test="${sessionScope.GradeTooLong}">
					<p id="gradeMsg" class="input-invalid">Max length of grade - 3</p>
				</c:if>
			</c:if>
			<c:if test="${not empty sessionScope.validGrade}">

				<c:if test="${not sessionScope.validGrade}">
					<p id="gradeMsg" class="input-invalid">Grade [0;100]</p>
				</c:if>
			</c:if>
			<p id="gradeMsg" class="input-invalid"></p>
			<div class="block">
				<br> <label><b>Teacher comment:</b></label>&nbsp;
				<textarea class="form-control" id="textareaComment" rows="3"
					maxlength="150" name="comment"><c:out
						value="${sessionScope.currHomework.teacherComment}"></c:out></textarea>
				<c:if test="${not empty sessionScope.validComment}">

					<c:if test="${not sessionScope.validComment}">
						<p id="textareaCommentMsg" class="input-invalid">Invalid
							comment</p>
					</c:if>
				</c:if>
				<p id="textareaCommentMsg" class="input-invalid"></p>
			</div>


			<br>
			<div class="col-sm-offset-3 col-sm-2" style="left: 230px">
				<input style="align: right" type="submit" class="btn btn-default"
					value="Save">
			</div>
		</form>
		<br> <br> <br>
		<c:forEach var="i" begin="1"
			end="${sessionScope.currHomework.homeworkDetails.numberOfTasks}">
			<c:if test="${i == 5}">
				<br>
				<br>

			</c:if>
			<div style='float: left'>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

				<button type="submit" onclick="seeTaskSolution('${i}')"
					class="btn btn-primary btn-sm"
					style="color: #fff; background-color: #0086b3">
					<c:out value="Task ${i}"></c:out>
				</button>
			</div>
		</c:forEach>
		<br> <br> <br>
		<div id="taskUpload" style="visibility: hidden"></div>
		<br>
		<textarea id="currTaskSolution" disabled="disabled"
			style="visibility: hidden" class="form-control" cols="150" rows="25">
	</textarea>
	</div>
	<c:if test="${not empty sessionScope.invalidFields}">
			<c:remove var="salary" scope="session" />
		</c:if><c:if test="${not empty sessionScope.invalidFields}">
			<c:remove var="invalidFields" scope="session" />
		</c:if><c:if test="${not empty sessionScope.emptyFields}">
			<c:remove var="emptyFields" scope="session" />
		</c:if><c:if test="${not empty sessionScope.GradeTooLong}">
			<c:remove var="GradeTooLong" scope="session" />
		</c:if><c:if test="${not empty sessionScope.validGrade}">
			<c:remove var="validGrade" scope="session" />
		</c:if><c:if test="${not empty sessionScope.validComment}">
			<c:remove var="validComment" scope="session" />
		</c:if>
	<%-- 
		<c:if test="${not empty sessionScope.currHomework}">
			<c:remove var="currHomework" scope="session" />
		</c:if>
		 --%>
	<script>
	$('#UpdateTeacherGradeAndCommentForm').submit(function(e) {
		e.preventDefault();

		var grade = document.forms["UpdateTeacherGradeAndCommentForm"]["grade"].value;
		var textareaComment = document.forms["UpdateTeacherGradeAndCommentForm"]["textareaComment"].value;
		
		var isGradeValid = true;
		var isCommentValid = true;
		if (!$('#gradeMsg').is(':empty')) {
			$("#gradeMsg").empty();
		}
		if (!$('#textareaCommentMsg').is(':empty')) {
			$("#textareaCommentMsg").empty();
		}
		if(grade == ""){
			document.getElementById("grade").value = 0;
		}else{
			if((grade < 0) || (grade > 100)){
				document.getElementById("gradeMsg").append(
						"grade - between 0 and 100");
				isGradeValid = false;
			}
		}
		
		if(textareaComment.length > 150){
			
			document.getElementById("textareaCommentMsg").append(
					"comment size - max 150 symbols");
			isCommentValid = false;
		}
		
		if(isGradeValid === true && isCommentValid === true){
			document.getElementById("UpdateTeacherGradeAndCommentForm").submit();
		}else{
			return false;
		}
		
		

	});
	function seeTaskSolution(taskNum){
		$.ajax({
			url : './ReadJavaFileServlet',
			data : {
				"taskNum" : taskNum,
			},
			type : 'GET',
			dataType: 'json',
			success : function(response) {
				var uploaded = response.uploadedOn;
				var uploadedRep = uploaded.replace("T", " ");	
				$("#taskUpload").html("Task " + taskNum + " uploaded on: " + uploadedRep);
				$("#currTaskSolution").html(response.solution);
				document.getElementById("taskUpload").style.visibility = "visible";
				document.getElementById("currTaskSolution").style.visibility = "visible";
			}
		});
	}
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