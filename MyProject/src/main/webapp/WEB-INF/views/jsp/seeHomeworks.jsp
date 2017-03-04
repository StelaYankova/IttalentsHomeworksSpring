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
#divTable {
	position: absolute;
	top: 150px;
	left: 10px;
	width: 70%;
}

#listOfStudentsOfGroup {
	position: absolute;
	top: 300px;
	right: 50px;
}
</style>
<body>
	<%@ include file="navBarTeacher.jsp"%>
	<div id="pageWrapper">
		<nav class="breadcrumb-nav">
		<ul class="breadcrumb">
			<li><a href="http://localhost:8080/MyProject/GetMainPageTeacher">Home</a>
				<span class="divider"> <span class="accesshide "><span
						class="arrow_text"></span></span>
			</span></li>
			<li><a
				href="http://localhost:8080/MyProject/SeeHomeworksServlet">See/Update
					homeworks</a> <span class="divider"> <span class="accesshide "><span
						class="arrow_text"></span>&nbsp;</span>
			</span></li>
		</ul>
		</nav>
		<div id="image">
			<img src="images/logo-black.png" class="img-rounded" width="380"
				height="236">
		</div>
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
		<div id="divTable">
			<table id="resultTable" border="1"
				class="table table-striped table-bordered table-hover"
				style="width: 60%">
				<thead class=wrapword>
					<tr>
						<td>Heading</td>
						<td>Opens</td>
						<td>Closes</td>
						<td></td>
					</tr>
				</thead>
				<tbody class=wrapword>
				</tbody>
			</table>
		</div>
		<ul id="listOfStudentsOfGroup" class="editable"
			style="visibility: hidden; z-index: 1; height: 300px; width: 18%; overflow: hidden; overflow-y: scroll; overflow-x: scroll;"></ul>
	</div>
	<script>
		$(document)
				.ready(
						function() {
							var table = $('#resultTable').DataTable({
								"aoColumnDefs" : [ {
									'bSortable' : true,
									'aTargets' : [ 0, 1, 2 ],
									'className' : "wrapword",
									"targets" : [ 0, 1, 2, 3 ]

								} ],
								"dom" : '<"top"l>rt<"bottom"ip><"clear">',
								"aoColumns" : [ {
									sWidth : '12%'
								}, {
									sWidth : '5%'
								}, {
									sWidth : '5%'
								}, {
									sWidth : '7%'
								} ],
								"lengthMenu" : [ 5 ],
								"bDestroy" : true
							});
							$('#chosenGroup')
									.change(
											function(event) {
												if (!$('#resultTable tbody')
														.is(':empty')) {
													$("#resultTable tbody")
															.empty();
												}
												if (!$('#listOfStudentsOfGroup')
														.is(':empty')) {
													$("#listOfStudentsOfGroup")
															.empty();
												}
												var groupId = $(this).find(
														":selected").val();
												$
														.ajax({
															url : './seeHomeworksOfGroupServlet',
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
																	if (response === 'null') {
																		$(
																				'#resultTable tbody')
																				.html(
																						'no data available in table');
																	} else if (groupId === 'allGroups') {
																		var rowNode = table.row
																				.add(
																						[
																								"<button type = 'submit' class='btn btn-link' onclick = 'chooseGroupFirst()'>"
																										+ response[i].heading
																										+ "</button>",
																								opensRep,
																								closesRep,
																								"<form action = './UpdateHomeworkServlet' method = 'GET'><input type = 'hidden' name = 'chosenHomework' value = "+response[i].id +"><button type = 'submit' style = 'color:black' class='btn btn-link'>Change</button></form>" ])
																				.draw()
																				.node();
																	} else {
																		var rowNode = table.row
																				.add(
																						[
																								"<button type = 'submit' class='btn btn-link' onclick = 'chooseStudent("
																										+ response[i].id
																										+ ","
																										+ groupId
																										+ ")'>"
																										+ response[i].heading
																										+ "</button>",
																								opensRep,
																								closesRep,
																								"<form action = './UpdateHomeworkServlet' method = 'GET'><input type = 'hidden' name = 'chosenHomework' value = "+response[i].id +"><button type = 'submit' class='btn btn-link' style = 'color:black'>Change</button></form>" ])
																				.draw()
																				.node();
																	}

																}
															}
														});
											});
						});
		function chooseGroupFirst() {
			alert("If you would like to see the homework of some of the students you should choose group first.")
		}
		function chooseStudent(homeworkId, groupId) {
			if (!$('#listOfStudentsOfGroup').is(':empty')) {
				$("#listOfStudentsOfGroup").empty();
			}
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

							for ( var i in response) {
								var hasStudentGivenMinOneTask = response[i].hasStudentGivenMinOneTask;
								if (hasStudentGivenMinOneTask == true) {
									$('#listOfStudentsOfGroup')
											.append(
													"<li><form action = './GetHomeworkOfStudentServlet'><input type = 'hidden' name = 'id' value = "+homeworkId +"><input type = 'hidden' name = 'studentId' value = "+response[i].id+"><button type = 'submit' class='btn btn-link'>"
															+ response[i].username
															+ "</button></form></li>");
									document
											.getElementById("listOfStudentsOfGroup").style.visibility = "visible";
								} else {
									$('#listOfStudentsOfGroup')
											.append(
													"<li><form action = './GetHomeworkOfStudentServlet'><input type = 'hidden' name = 'id' value = "+homeworkId +"><input type = 'hidden' name = 'studentId' value = "+response[i].id+"><button type = 'button' style= 'color:#620062' class='btn btn-link'>"
															+ response[i].username
															+ "</button></form></li>");
									document
											.getElementById("listOfStudentsOfGroup").style.visibility = "visible";
								}
							}
						},

					});
		};
		function selectOption(index) {
			document.getElementById("chosenGroup").options.selectedIndex = index;
		}
		$(document).ready(function(e) {
			selectOption(0);
		});
		/*for(var i in response){
		
		var row = $("<tr>");
		
		if(groupId === 'allGroups'){
			row.append($("<td><button type = 'submit' onclick = 'chooseGroupFirst()'>" +response[i].heading + "</button></td>")) 
			.append($("<td>"+ response[i].opens+"</td>"))
		     .append($("<td>" +response[i].closes+"</td>"))
		     .append($("<form action = './UpdateHomeworkServlet' method = 'GET'><input type = 'hidden' name = 'chosenHomework' value = "+response[i].id +"><button type = 'submit'>Change</button></form>"))
		  $("#resultTable").append(row);
		
		}else{
		row.append($("<td><button type = 'submit' onclick = 'chooseStudent(" + response[i].id +"," + groupId +")'>" +response[i].heading + "</button></td>")) 
			.append($("<td>"+ response[i].opens+"</td>"))
		     .append($("<td>" +response[i].closes+"</td>"))
		     .append($("<form action = './UpdateHomeworkServlet' method = 'GET'><input type = 'hidden' name = 'chosenHomework' value = "+response[i].id +"><button type = 'submit'>Change</button></form>"))
		  $("#resultTable").append(row);
		}
		}*/
		//});
		$(function() {
			$.ajaxSetup({
				statusCode : {
					401 : function() {
						location.href = '/MyProject/index';
					},
					403 : function() {
						location.href = '/MyProject/forbiddenPage';
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