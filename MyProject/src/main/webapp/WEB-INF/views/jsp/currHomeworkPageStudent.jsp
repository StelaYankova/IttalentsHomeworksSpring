<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
#textareaComment {
    max-width: 30%;
}
#currTaskSolution {
    max-width: 50%;
}
#invalidData{
	color:red;
}
#image {
	position: absolute;
	left: 850px;
/* } */
#pageContent{
position: absolute;
	left: 50px;
	padding:10px;
   padding-bottom:60px; 

}

</style>
<body>

	<%@ include file="navBarStudent.jsp"%>
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
		<div class="form-group">
			<br> <b>Teacher grade:</b>
			<c:out value="${sessionScope.currHomework.teacherGrade }" />
			<br> <br> <b>Teacher comment:</b>
			<textarea style="display: inline" disabled="disabled"
				class="form-control field span12" id="textareaComment" rows="3"><c:out
					value="${sessionScope.currHomework.teacherComment }" /></textarea>
			<br> <br> <br>
			<c:forEach var="i" begin="1"
				end="${sessionScope.currHomework.homeworkDetails.numberOfTasks}">
				<c:if test="${i == 5}">
					<br>
					<br>
					<br>
					<br>
					<br>
				</c:if>
				<div style='float: left'>
					<button class="btn btn-primary btn-sm"
						style="color: #fff; background-color: #0086b3" type="submit"
						onclick="seeTaskSolution('${i}')">
						<c:out value="Task ${i}" />
					</button>
					<c:if test="${sessionScope.hasUploadTimePassed == 'false'}">
						<c:if test="${sessionScope.hasUploadTimeCome == 'true'}">
							<form action="./UploadSolutionServlet" method="POST"
								enctype="multipart/form-data" id="uploadSolutionForm">

								<input type="hidden" value="${i}" name="taskNum"><input
									type="file" accept="application/java" size="50" name="file"
									id="file">
								<!--   <button class="btn btn-default btn-xs" type="Submit">Upload
									solution</button>-->
								<input type="submit" class="btn btn-default btn-xs"
									value="Upload
									solution">


							</form>
							<div id="invalidData">
								<c:if test="${sessionScope.currTaskUpload+1 == i}">
									<p id="fileMsg" class="input-invalid"></p>

									<c:if test="${sessionScope.wrongContentType == true}">You can upload only .java files</c:if>
									<c:if test="${sessionScope.wrongSize == true}">You can upload only files up to 1 MB</c:if>
								</c:if>
							</div>
						</c:if>
					</c:if>
				</div>
			</c:forEach>

			<br> <br>
			<c:if test="${sessionScope.hasUploadTimeCome == 'true'}">
				<div id="taskUpload" style="visibility: hidden"></div>
				<c:if test="${sessionScope.hasUploadTimePassed == 'false'}">
					<textarea id="currTaskSolution" style="visibility: hidden;"
						class="form-control" cols="150" rows="25">
					</textarea>
				</c:if>
				<c:if test="${sessionScope.hasUploadTimePassed == 'true'}">
					<textarea id="currTaskSolution" disabled="disabled"
						style="visibility: hidden;" class="form-control" cols="150"
						rows="25">
					</textarea>
				</c:if>
			</c:if>
		</div>
	</div>
	<c:if test="${not empty sessionScope.currTaskUpload}">
		<c:remove var="currTaskUpload" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.wrongContentType}">
		<c:remove var="wrongContentType" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.wrongSize}">
		<c:remove var="wrongSize" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.currTask}">
		<c:remove var="currTask" scope="session" />
	</c:if>
	<c:if test="${not empty sessionScope.currTaskSolution}">
		<c:remove var="currTaskSolution" scope="session" />
	</c:if>
	
	<script>
	document.getElementById("currTaskSolution").addEventListener("change", saveChangedText);
	
	function saveChangedText(){
		var taskNum = sessionStorage.getItem("currTask");
		console.log('currTask is ' + taskNum)
		var text = document.getElementById("currTaskSolution").value;
		$.ajax({
			url: './SaveChangedSolutionText',
			type : 'POST',
			data : {
				"taskNum" : taskNum,
				"text" : text
			},
			success : function(response){
				console.log("YES");
			},
			error : function(data){
				console.log("NO");
				if(data.status == 400){
					alert("File cannot be empty and should be smaller than 1 MB");
					document.getElementById("currTaskSolution").value = sessionStorage.getItem("currTaskSolution");
				}
			}
		})
	}
	
	function seeTaskSolution(taskNum){
		$.ajax({
			url : './ReadJavaFileServlet',
			data : {
				"taskNum" : taskNum
			},
			type : 'GET',
			dataType: 'json',
			success : function(response) {
				var uploaded = response.uploadedOn;
				var uploadedRep = uploaded.replace("T", " ");
				$("#taskUpload").html("<br><br><br><br>Task " + taskNum + " uploaded on: " + uploadedRep);
				$("#currTaskSolution").html(response.solution);
				document.getElementById("taskUpload").style.visibility = "visible";
				document.getElementById("currTaskSolution").style.visibility = "visible";
				console.log(uploaded)
				if(uploaded === "-"){
					console.log(uploaded)
					document.getElementById("currTaskSolution").disabled = true;
				}
				sessionStorage.setItem("currTask", taskNum);
				sessionStorage.setItem("currTaskSolution", response.solution);
			}
		});
	}
	 function isFileValidCheck() {
			var file = document.forms["uploadSolutionForm"]["file"].value;
			var val = file.toLowerCase();
			var regex = new RegExp("(.*?)\.(java)$");
			if (!(regex.test(val))) {
				return false;
			}
			
			 var size = (document.forms["uploadSolutionForm"]["file"].files[0].size/1024/1024).toFixed(2);
			console.log(size)
			if(size > 1){
				console.log(false)
				return false;
			}
			return true;
		}
	 
	$('#uploadSolutionForm').submit(function(e) {
		e.preventDefault();
		var file = document.forms["uploadSolutionForm"]["file"].value;
		if(file == ""){
			isFileValid = false;
			return false;
		}
		var isFileValid = isFileValidCheck();
		if(!isFileValid){
			if (!$('#fileMsg').is(':empty')) {
				$("#fileMsg").empty();
			}
			document.getElementById("fileMsg").append(
			"File format-java, maxSize - 1MB");
			console.log("invalid file")
		}
		$( document ).ajaxStop(function() {

		if(isFileValid === true){
			document.getElementById("uploadSolutionForm").submit();
		}
		});
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