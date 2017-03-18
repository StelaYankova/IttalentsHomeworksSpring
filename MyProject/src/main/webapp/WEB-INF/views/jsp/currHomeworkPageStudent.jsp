<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/currHomeworkPageStudentCss.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
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
					<span class="divider"> <span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<c:if test="${not empty sessionScope.throughtScores}">
					<c:if test="${sessionScope.throughtScores == 0}">
						<li><a
							href="./GetHomeworksOfGroupsServlet"><c:out
						value="${sessionScope.chosenGroupName}" ></c:out></a> <span class="divider"> <span
								class="accesshide "><span class="arrow_text"></span>&nbsp;</span>
						</span></li>
					</c:if>
					<c:if test="${sessionScope.throughtScores == 1}">
						<li><a
							href="./SeeScoresServlet">Your
								scores</a><span class="divider"> <span class="accesshide "><span
									class="arrow_text"></span></span>
						</span></li>
						<li><c:out
						value="${sessionScope.chosenGroupName}"></c:out><span class="divider"> <span
								class="accesshide "><span class="arrow_text"></span>&nbsp;</span>
						</span></li>
					</c:if>
				</c:if>
				<c:if test="${ empty sessionScope.throughtScores}">
					<c:if test="${ empty sessionScope.throughtGroups}">
						<li><a
							href="./SeeScoresServlet">Your
								scores</a> <span class="divider"> <span class="accesshide "><span
									class="arrow_text"></span></span>
						</span></li>
					</c:if>
				</c:if>
				<li><c:out
						value="${sessionScope.currHomework.homeworkDetails.heading}"/><span class="divider"> <span
						class="accesshide "><span class="arrow_text"></span>&nbsp;</span>
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
			<br> <b>Teacher grade:</b>
			<c:out value="${sessionScope.currHomework.teacherGrade }" />

			<br> <br> <b>Teacher comment:</b> <br>
			<br> <label id="teacherComment"><c:out
					value="${sessionScope.currHomework.teacherComment }" /></label> <br>
			<br> <br>
			<!-- </div> -->
			<div id="id"></div>
			<table id="tasksTable" border="1"
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
										<form action="./UploadSolutionServlet" method="POST"
											enctype="multipart/form-data" id="uploadSolutionForm${i}"
											onchange="uploadFile('uploadSolutionForm${i}')">
											<label class="btn btn-default btn-file"> <input
												type="hidden" value="${i}" name="taskNum">Upload<input
												type="file" accept="application/java" size="50"
												name="datafile" class="hidden"></label>
										</form>
										<span class="invalidData">
											<p id="fileMsguploadSolutionForm${i}" class="input-invalid"></p>
											<c:if test="${sessionScope.currTaskUpload+1 == i}">
												<c:if test="${sessionScope.wrongContentType == true}">You can upload only .java files</c:if>
												<c:if test="${sessionScope.wrongSize == true}">You can upload only files up to 1 MB</c:if>
											</c:if>
										</span>
									</c:if>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<div class="solution">
			<c:if test="${sessionScope.hasUploadTimeCome == 'true'}">
				<div id="taskUpload"></div>
				<c:if test="${sessionScope.hasUploadTimePassed == 'false'}">
					<textarea id="currTaskSolution"
						class="form-control" cols="30" rows="30">
					</textarea>
				</c:if>
				<c:if test="${sessionScope.hasUploadTimePassed == 'true'}">
					<textarea id="currTaskSolution" disabled="disabled"
						class="form-control" cols="30"
						rows="30">
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
					"File format-java, maxSize - 1MB");
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
				'sWidth' : '140%'
			} ],

			/* "lengthMenu" : [ 5 ], */
			"scrollY" : '100vh',
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
		var taskNum = sessionStorage.getItem("currTask");
		var text = document.getElementById("currTaskSolution").value;
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
				})
	}
	function seeTaskSolution(taskNum) {
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
						var uploadedRep = uploaded.replace("T", " ");
						$("#taskUpload").html(
								"Task " + taskNum + " uploaded on: "
										+ uploadedRep);
						$("#currTaskSolution").html(response.solution);
						document.getElementById("taskUpload").style.visibility = "visible";
						document.getElementById("currTaskSolution").style.visibility = "visible";
						if (uploaded === "-") {
							document.getElementById("currTaskSolution").disabled = true;
						} else {
							document.getElementById("currTaskSolution").disabled = false;
						}
						sessionStorage.setItem("currTask", taskNum);
						sessionStorage.setItem("currTaskSolution",
								response.solution);
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
		if (size > 1) {
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