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
<link href="<c:url value="css/currHomeworkPageStudentCss.css" />" rel="stylesheet">
</head>
<style>

</style>
<body>
	<%@ include file="navBarStudent.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="./GetMainPageStudent">Home</a>
					<span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<c:if test="${not empty sessionScope.throughtScores}">
					<c:if test="${sessionScope.throughtScores == 0}">
						<li><a
							href="./homeworksOfGroup"><c:out
						value="${sessionScope.chosenGroupName}"></c:out></a><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
					</c:if>
					<c:if test="${sessionScope.throughtScores == 1}">
						<li><a
							href="./yourScores">Your
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
							href="./SeeScoresServlet">Your
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
			<!-- <div class="form-group"> -->
			
			<div id="downloadHomeworkForm">
				<form action="./ReadHomeworkServlet" method="GET">
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
			<!-- </div> -->
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
								</button> <c:if test="${sessionScope.hasUploadTimePassed == 'false'}">
									<c:if test="${sessionScope.hasUploadTimeCome == 'true'}">
										<form action="./UploadSolutionServlet" method="POST" class = "uploadSolutionButton"
											enctype="multipart/form-data" id="uploadSolutionForm${i}"
											onchange="uploadFile('uploadSolutionForm${i}')" accept-charset="UTF-8">
											<label class="btn btn-sm btn-file" style = "text-decoration:underline"> <input
												type="hidden" value="${i}" name="taskNum">Upload<input
												type="file" accept="application/java" size="50"
												name="datafile" class="hidden"></label>
										</form>
<!-- 										<span class="invalidData">
 -->										<%-- <c:if test="${sessionScope.currTaskUpload+1 == i}">
 												
												<p class = "input-invalid wrongFile"><!-- You can upload only .java files u</p>
												You can upload only files up to 1 MB --></p>
												
											</c:if> --%>
<!-- 										</span>
 -->										<p id="fileMsguploadSolutionForm${i}" class="input-invalid"></p>
										
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
			<!-- <br> <br> -->
		</div>

		<div id="solution">
			<c:if test="${sessionScope.hasUploadTimeCome == 'true'}">
				<div id="taskUpload"></div>
				<c:if test="${sessionScope.hasUploadTimePassed == 'false'}">
					<textarea id="currTaskSolution"
						class="form-control" ><!-- cols="30" rows="27" -->
					</textarea>
				</c:if>
				<c:if test="${sessionScope.hasUploadTimePassed == 'true'}">
					<textarea id="currTaskSolution" disabled="disabled"
						class="form-control"><!--  cols="30"
						rows="27" -->
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
	
</body>
<script>

function uploadFile(e){
	
	
	 //console.log(arguments) ; return;
	/* var form = document.getElementById(e);
	var formData = new FormData(form);
	var oReq = new XMLHttpRequest();
	oReq.open("POST", './UploadSolutionServlet', true);
	oReq.onload = function(oEvent){
		if(oReq.status == 200){
			alert("OK");
		}
	}
	oReq.send(formData);return; */
	
	
	console.log("uploadSolutionForm" + e)
	var file = document.forms[e]["datafile"].value;
	

	 $('.input-invalid').empty();
		if (file == "") {
			isFileValid = false;
			return false;
		}
		var isFileValid = isFileValidCheck(e);
		if (!isFileValid) {console.log("fileMsg" + e)
			document.getElementById("fileMsg"+e).append(
					"Valid file format - java, maximal size - 1MB");
		}else{  
		var form = new FormData(document.getElementById(e));
		console.log(form.get("taskNum"))
		$.ajax({
			url:'./UploadSolutionServlet',
			type:'POST',
			data: form,
			processData: false,
			contentType:false,
			success:function(data){
				alert("The solution has been added successfully!");
				seeTaskSolution(form.get("taskNum"));
			},
			error:function(data){
				document.getElementById("fileMsg"+e).append(
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

			/* "lengthMenu" : [ 5 ], */
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
		console.log(taskNum);
		$
				.ajax({
					url : './SaveChangedSolutionText',
					type : 'POST',
					data : {
						"taskNum" : taskNum,
						"text" : text
					},
					error : function(data) {
						if (data.status == 400) {
							alert("File cannot be empty and should be smaller than 1MB");
							document.getElementById("currTaskSolution").value = sessionStorage
									.getItem("currTaskSolution");
						}
					}
				
					/* sessionStorage.setItem("currTask", taskNum);
					sessionStorage.setItem("currTaskSolution",
							response.solution); */
				})
	}
	
	function seeTaskSolution(taskNum) {
		$('.input-invalid').empty();
		console.log(taskNum + " !");
		$
				.ajax({
					url : './ReadJavaFileServlet',
					data : {
						"taskNum" : taskNum
					},
					type : 'GET',
					dataType : 'json',
					success : function(response) {
						var uploaded = response.uploadedOn;
						console.log(uploaded)
						var uploadedRep = uploaded.replace("T", " ");
						$("#taskUpload").html(
								"Task " + taskNum + " uploaded on: "
										+ uploadedRep);
						document.getElementById('currTaskSolution').value = response.solution;
						document.getElementById("taskUpload").style.display = "block";
						document.getElementById("currTaskSolution").style.display = "block";
						if (uploaded === "-") {
							document.getElementById("currTaskSolution").disabled = true;
						} else {
							document.getElementById("currTaskSolution").disabled = false;
						}
						sessionStorage.setItem("currTask", taskNum);
						sessionStorage.setItem("currTaskSolution",
								response.solution);
						console.log("willll")
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
				.toFixed(2);
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