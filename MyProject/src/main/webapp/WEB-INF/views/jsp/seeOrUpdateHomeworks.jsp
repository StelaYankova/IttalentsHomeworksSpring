<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">
<link href="<c:url value="css/generalCss.css" />" rel="stylesheet">
<link href="<c:url value="css/seeOrUpdateHomeworksCss.css" />" rel="stylesheet">

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
				<li>See/Update homeworks<span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
			</ul>
		</nav>
	</div>
	<div id="pageWrapper">
		<h4 id = "pageTitle">
			<b><u>See/ Update homeworks</u></b>
		</h4>
		<div id="select">
			<input type="hidden" id="refresh" value="no">
			<c:if test="${not empty invalidFields}">
				<c:if test="${not invalidFields}">
					<script>
					alert('Homework has been removed successfully!');
				</script>
				</c:if>
			</c:if>
			Choose group: <select id="chosenGroup" class="selectpicker">
				<option value="null">-</option>
				<option value="allGroups">All Groups</option>
				<c:forEach var="group" items="${applicationScope.allGroups}">
					<option value="${group.id}"><c:out value="${group.name}"></c:out></option>
				</c:forEach>
			</select>
		</div>
		<div id = "tableAndStudents" >
		<div id="currTable" style = "">
				<div id="divTable" style = "width:100%">
					<table id="resultTable" border="1"
						class="table table-striped table-bordered table-hover">
						<thead class="wrapword">
							<tr>
								<th>Heading</th>
								<th>Opens</th>
								<th>Closes</th>
								<th></th>
							</tr>
						</thead>
						<tbody class="wrapword">
						</tbody>
					</table>
				</div>
			</div>

			<!-- <div class="list">
				<div id="listHeading" >
					<h>Students in chosen group:</h>
					<ul id="listOfStudentsOfGroup" class="editable">
					
					</ul>
				</div>
			</div> -->
			<div class="list">
		<h id = "listHeading">Students in chosen group:</h>
			<ul id="listOfStudentsOfGroup" class="editable"></ul>
		</div>
		</div>
		</div>
		<script>
		$(document)
				.ready(
						function() {
							var table = $('#resultTable').DataTable({
								"aoColumnDefs" : [ {
									'className' : "wrapword",
									"targets" : [ 0, 1, 2, 3 ]

								} ],
								
								"dom" : '<"top"lp>rt<"clear">',
								"aoColumns" : [ {
									sWidth : '12%'
								}, {
									sWidth : '12%'
								}, {
									sWidth : '12%'
								}, {
									sWidth : '7%'
						
			      				} ],
									"aoColumns" : [ {
										"bSortable" : true
									}, {
										"bSortable" : true
									}, {
										"bSortable" : true
									}, {
										"bSortable" : false
									} ],
									"lengthMenu" : [ 5, 10, 15 ],
									"bDestroy" : true
								});
								$('#chosenGroup')
										.change(
												function(event) {

													document
															.getElementById('divTable').style.display = 'none';
													document
															.getElementById('listOfStudentsOfGroup').style.display = 'none';
													document
															.getElementById('listHeading').style.display = 'none';

													if (!$('#resultTable tbody')
															.is(':empty')) {
														$("#resultTable tbody")
																.empty();
													}
													if (!$(
															'#listOfStudentsOfGroup')
															.is(':empty')) {
														$(
																"#listOfStudentsOfGroup")
																.empty();
													}

													var groupId = $(this).find(
															":selected").val();

													if (groupId != 'null') {
														if (!$(
																'li#chosenGroupName')
																.is(':empty')) {

															$(
																	"li#chosenGroupName")
																	.remove();
															var groupName = $(
																	this)
																	.find(
																			":selected")
																	.text();
															$('.breadcrumb')
																	.append(
																			'<li id = "chosenGroupName">'
																					+ groupName
																					+ '<span class="divider"><span class="accesshide "><span class="arrow_text"></span></span></span></li>');
														}

													} else {
														if (!$(
																'li#chosenGroupName')
																.is(':empty')) {

															$(
																	"li#chosenGroupName")
																	.remove();
														}
													}
													if (!$(
															'li#chosenHomeworkName')
															.is(':empty')) {

														$(
																"li#chosenHomeworkName")
																.remove();
													}
													$
															.ajax({
																url : './seeHomeworksOfGroupForUpdateByTeacher',
																type : 'GET',
																cache : false,
																data : {
																	"chosenGroup" : groupId
																},
																dataType : 'json',
																success : function(
																		response) {
																	if ($(table)
																			.find(
																					"#tbody")
																			.html() !== 0) {
																		$(
																				'#resultTable')
																				.DataTable()
																				.clear()
																				.draw();
																	}
																	if (!$
																			.trim(response)
																			&& groupId !== "null") {
																		document
																				.getElementById('divTable').style.display = 'none';
																		alert("There are no homeworks in this group.")
																	}
																	for ( var i in response) {
																		var opens = response[i].opens;
																		var opensRep = opens
																				.replace(
																						"T",
																						" ");
																		var closes = response[i].closes;
																		var closesRep = closes
																				.replace(
																						"T",
																						" ");
																		/* if (response === 'null') {
																			$(
																					'#resultTable tbody')
																					.html(
																							'no data available in table');
																		} else  */if (groupId === 'allGroups') {
																			var rowNode = table.row
																					.add(
																							[
																									"<button type = 'submit' class='btn btn-link' onclick = 'chooseGroupFirst()'>"
																											+ response[i].heading
																											+ "</button>",
																									opensRep,
																									closesRep,
																									"<form action = './updateHomework' method = 'GET'><input type = 'hidden' name = 'chosenHomework' value = "+response[i].id +"><button type = 'submit' style = 'color:black' class='btn btn-link'>Change</button></form>" ])
																					.draw()
																					.node();
																		} else {
																			var rowNode = table.row
																					.add(
																							[
																									"<button type = 'submit' class='btn btn-link' onclick = 'chooseStudent("
																											+ "\""
																											+ response[i].heading
																											+ "\""
																											+ ","
																											+ response[i].id
																											+ ","
																											+ groupId
																											+ ")'>"
																											+ response[i].heading
																											+ "</button>",
																									opensRep,
																									closesRep,
																									"<form action = './updateHomework' method = 'GET'><input type = 'hidden' name = 'chosenHomework' value = "+response[i].id +"><button type = 'submit' class='btn btn-link' style = 'color:black'>Change</button></form>" ])
																					.draw()
																					.node();

																		}
																		if (groupId !== "null") {
																			document
																					.getElementById('divTable').style.display = 'block';

																		}
																	}
																}
															});

												});
							});
			function chooseGroupFirst() {
				alert("If you would like to see the homework of some of the students you should choose group first.")
			}
			function chooseStudent(homeworkName, homeworkId, groupId) {
				console.log(09)
				if (!$('#listOfStudentsOfGroup').is(':empty')) {
					$("#listOfStudentsOfGroup").empty();
				}
				if (!$('li#chosenHomeworkName').is(':empty')) {

					$("li#chosenHomeworkName").remove();
				}
				var homeworkName = homeworkName;
				$('.breadcrumb')
						.append(
								'<li id = "chosenHomeworkName">'
										+ homeworkName
										+ '<span class="divider"><span class="accesshide "><span class="arrow_text"></span></span></span></li>');

				$
						.ajax({
							url : './getAllStudentsOfGroupServlet',
							type : 'GET',
							data : {
								"chosenGroupId" : groupId,
								"homeworkId" : homeworkId
							},
							dataType : 'json',
							success : function(response) {
								var div = document
										.getElementById("listOfStudentsOfGroup");
								if (!$.trim(response)) {
									alert("There are no students in this group.");
								}
								for ( var i in response) {
									var hasStudentGivenMinOneTask = response[i].hasStudentGivenMinOneTask;
									if (hasStudentGivenMinOneTask == true) {
										$('#listOfStudentsOfGroup')
												.append(
														"<li type='square'><form action = './getHomeworkOfStudentByTeacher'><input type = 'hidden' name = 'homeworkId' value = "+homeworkId +"><input type = 'hidden' name = 'studentId' value = "+response[i].id+"><button type = 'submit' class='btn btn-link'>"
																+ response[i].username
																+ "</button></form></li>");

										document
												.getElementById("listOfStudentsOfGroup").style.display = "block";
										document.getElementById('listHeading').style.display = 'block';

									} else {
										$('#listOfStudentsOfGroup')
												.append(
														"<li type='square'><form action = './getHomeworkOfStudentByTeacher'><input type = 'hidden' name = 'homeworkId' value = "+homeworkId +"><input type = 'hidden' name = 'studentId' value = "+response[i].id+"><button type = 'button' title = 'Student has not uploaded the chosen homework' style= 'color:#620062' class='btn btn-link'>"
																+ response[i].username
																+ "</button></form></li>");
										document
												.getElementById("listOfStudentsOfGroup").style.display = "block";
										document.getElementById('listHeading').style.display = 'block';

									}
								}
							}
						});
			};
			function selectOption(index) {
				document.getElementById("chosenGroup").options.selectedIndex = index;
			}
			$(document).ready(function(e) {
				selectOption(0);
			});
			$(function() {
				$.ajaxSetup({
					statusCode : {
						401 : function() {
							location.href = '/MyProject/index';
						},
						403 : function() {
							location.href = '/MyProject/forbiddenPage';
						},
						404 : function() {
							location.href = '/MyProject/pageNotFoundPage';
						},
						500 : function() {
							location.href = '/MyProject/exceptionPage';
						}
					}
				});
			});
		</script>
</body>
</html>