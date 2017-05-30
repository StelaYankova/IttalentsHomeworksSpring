<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link href="<c:url value="css/currentHomeworkPageOfStudentByStudentCss.css" />" rel="stylesheet">
<link rel="icon" type="image/png" href="./images/favIcon.png">

</head>
<style>

</style>
<body>
	<%@ include file="navBarStudent.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="./mainPageStudent">Home</a>
					<span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<c:if test="${not empty sessionScope.throughtScores}">
					<c:if test="${sessionScope.throughtScores == 0}">
						<li><a
							href="./seeHomeworksListOfStudentByGroupByStudent"><c:out
						value="${sessionScope.chosenGroupName}"></c:out></a><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
					</c:if>
					<c:if test="${sessionScope.throughtScores == 1}">
						<li><a
							href="./studentsScoresByStudent">Your
								scores</a><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
						<li><c:out
						value="${sessionScope.chosenGroupName}"></c:out><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
					</c:if>
				</c:if>
				<c:if test="${ empty sessionScope.throughtScores}">
					<c:if test="${ empty sessionScope.throughtGroups}">
						<li><a
							href="./studentsScoresByStudent">Your
								scores</a><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
					</c:if>
				</c:if>
				<li><c:out
						value="${sessionScope.currHomework.homeworkDetails.heading}"/><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
			</ul>
		</nav>
	</div>
	<div id="pageWrapper">
		<div id="currHomework">
			<br>
			<div id="downloadHomeworkForm">
				<form action="./readFileOfTasksForHomeworkPDF" method="GET">
					<input type='hidden'
						value='${sessionScope.currHomework.homeworkDetails.tasksFile}'
						name='fileName'> <strong>You can download tasks
						<button class='btn btn-link btn-xs' type='submit'>
							<b>here</b>
						</button>
					</strong>
				</form>
			</div>
			<c:set var="opens" value="${fn:replace(sessionScope.currHomework.homeworkDetails.openingTime,'T', ' ')}" />
			<c:set var="closes" value="${fn:replace(sessionScope.currHomework.homeworkDetails.closingTime,'T', ' ')}" />
			<b class = "homeworkInfo">Heading: </b>
			<c:out value="${sessionScope.currHomework.homeworkDetails.heading}" /><br>
			<b class = "homeworkInfo">Opening time: </b>
			<c:out value="${opens}" /><br>
			<b class = "homeworkInfo">Closing time:  </b>
			<c:out value="${closes}" />
 			<div id="divTable">
 			<table id="tasksTable" 
				class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<td>Tasks</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="i" begin="1"
						end="${sessionScope.currHomework.homeworkDetails.numberOfTasks}">
						<tr>
							<td>
								<button id="seeTaskSolutionButton"
									class="btn btn-primary btn-sm" type="submit"
									onclick="seeTaskSolution('${i}')">
									<c:out value="Task ${i}" />
								</button> 
								<span class = "systemScore" id = "systemScore${i}">
								<c:if test="${sessionScope.currHomework.tasks[i-1].hasPassedSystemTest eq true}"><c:out value = "${sessionScope.pointsPerTask}%"></c:out></c:if>
 										<c:if test="${sessionScope.currHomework.tasks[i-1].hasPassedSystemTest ne true}">0%</c:if></span>
 										<c:if test="${sessionScope.hasUploadTimePassed == 'false'}">
									<c:if test="${sessionScope.hasUploadTimeCome == 'true'}">
										<form action="./uploadSolutionToTaskJava" method="POST" class = "uploadSolutionButton"
											enctype="multipart/form-data" id="uploadSolutionForm${i}"
											onchange="uploadFile('uploadSolutionForm${i}')" accept-charset="UTF-8">
											<label class="btn btn-sm btn-file" style = "text-decoration:underline"> <input
												type="hidden" value="${i}" name="taskNum">Upload<input
												type="file" accept="application/java" size="50"
												name="datafile" class="hidden"></label>
										</form>
										<p id="fileMsguploadSolutionForm${i}" class="input-invalid"></p>
									</c:if>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table></div>
			<br> <b id="teacherGrade">Teacher grade:</b>
			<c:out value="${sessionScope.currHomework.teacherGrade }" /><br>
			<b>Teacher comment:</b><br>
			<br> <label id="teacherComment" class = "wrapword"><c:out
					value="${sessionScope.currHomework.teacherComment}" /></label> <br>
		</div>

		<div id="solution">
			<c:if test="${sessionScope.hasUploadTimeCome == 'true'}">
				<div id="taskUpload"></div>
				<c:if test="${sessionScope.hasUploadTimePassed == 'false'}">
					<textarea id="currTaskSolution"
						class="form-control" >
					</textarea>
				</c:if>
				<c:if test="${sessionScope.hasUploadTimePassed == 'true'}">
					<textarea id="currTaskSolution" disabled="disabled"
						class="form-control">
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
	<c:if test="${not empty sessionScope.pointsPerTask}">
		<c:remove var="pointsPerTask" scope="session" />
	</c:if>
</body>
<script>

function uploadFile(e){
	var file = document.forms[e]["datafile"].value;
	$('.input-invalid').empty();
		if (file == "") {
			isFileValid = false;
			return false;
		}
		var isFileValid = isFileValidCheck(e);
		if (!isFileValid) {
			$("#fileMsg"+e).append(
					"Valid file format - java, maximal size - 1MB");
		}else{  
		var form = new FormData(document.getElementById(e));
		console.log(e)
		var taskNum = e.slice(-1);
		$.ajax({
			url:'./uploadSolutionToTaskJava',
			type:'POST',
			data: form,
			processData: false,
			contentType:false,
			 dataType: 'json',
			success:function(response){
				alert("The solution has been added successfully!");
				if(response.hasPassedTest){
					document.getElementById("systemScore" + taskNum).innerHTML = response.pointsPerTask + "%";
				}else{
/* 					document.getElementById("systemScore" + form.get("taskNum")).innerHTML = "0%";
 */				
					document.getElementById("systemScore" + taskNum).innerHTML = "0%";
				}
				/* seeTaskSolution(form.get("taskNum")); */
				seeTaskSolution(taskNum); 
			},
			error:function(data){
				$("#fileMsg"+e).append(
				"Valid file format - java, maximal size - 1MB");
			}
		});
	}
}

	$(document).ready(function() {
		var table = $('#tasksTable').DataTable({
			"autoWidth" : false,

			"aoColumnDefs" : [ {
				'bSortable' : false,
				'aTargets' : [ 0 ],
				'className' : "wrapword",

				"targets" : [ 0 ]
			} ],
			"dom" : '<"top"l>rt<"bottom"ip><"clear">',
			"aoColumns" : [ {
				'sWidth' : '12%'
			} ],
			"scrollY" : '41vh',
			"scrollCollapse" : true,

			"bDestroy" : true,
			"ordering": false,
			"bPaginate" : false,
			"bInfo" : false
		});
	});
	document.getElementById("currTaskSolution").addEventListener("change",
			saveChangedText);
	function saveChangedText() {
		$('.input-invalid').empty();
		var taskNum = sessionStorage.getItem("currTask");
		var text = document.getElementById("currTaskSolution").value;
		$
				.ajax({
					url : './updateSolutionTextOfTaskByStudentJava',
					type : 'POST',
					dataType:'json',
					data : {
						"taskNum" : taskNum,
						"text" : text
					},
					success: function(data){
						if(data.hasPassedTest){
							document.getElementById("systemScore" + taskNum).innerHTML = data.pointsPerTask + "%";
						}else{
							document.getElementById("systemScore" + taskNum).innerHTML = "0%";
						}
					},
					error : function(data) {
						if (data.status == 406) {
						      alert('You should upload file before you update its text!');
						    }
						
						if (data.status == 400) {
							alert("File cannot be empty and should be smaller than 1MB (you have to upload it before you edit it)");
							document.getElementById("currTaskSolution").value = sessionStorage
									.getItem("currTaskSolution");
						}
					}
				})
	}
	
	function seeTaskSolution(taskNum) {
		$('.input-invalid').empty();
		$
				.ajax({
					url : './readSolutionOfTaskJava',
					data : {
						"taskNum" : taskNum
					},
					type : 'GET',
					dataType : 'json',
					success : function(response) {
						var uploaded = response.uploadedOn;
						var uploadedRep = uploaded.replace("T", " ");
						$("#taskUpload").html(
								"Task " + taskNum + " last changed on: "
										+ uploadedRep);
						document.getElementById('currTaskSolution').value = response.solution;
						document.getElementById("taskUpload").style.display = "block";
						document.getElementById("currTaskSolution").style.display = "block";
						var hasUploadTimePassed = ${sessionScope.hasUploadTimePassed};
						if(!hasUploadTimePassed){
							if (uploaded === "-") {
								document.getElementById("currTaskSolution").disabled = true;
							} else{
								document.getElementById("currTaskSolution").disabled = false;
							}
						}
						sessionStorage.setItem("currTask", taskNum);
						sessionStorage.setItem("currTaskSolution",
								response.solution);
						document.getElementById("solution").style.display = "block";

					}
				});
	}
	function isFileValidCheck(e) {
		var file = document.forms[e]["datafile"].value;
		var val = file.toLowerCase();
		var regex = new RegExp("(.*?)\.(java)$");
		if (!(regex.test(val))) {
			return false;
		}
		var size = (document.forms[e]["datafile"].files[0].size / 1024 / 1024)
				.toFixed(10);
		if (size > 1 || size === 0) {
			return false;
		}
		return true;
	}

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