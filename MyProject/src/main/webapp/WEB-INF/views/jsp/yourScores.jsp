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
	width: 80%;
}
</style>
<body>
	<%@ include file="navBarStudent.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="http://localhost:8080/MyProject/GetMainPageStudent">Home</a>
					<span class="divider"> <span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li><a href="http://localhost:8080/MyProject/SeeScoresServlet">Your
						scores</a> <span class="divider"> <span class="accesshide "><span
							class="arrow_text"></span>&nbsp;</span>
				</span></li>
			</ul>
		</nav>
	</div>
	<div id="pageWrapper">
		Choose a group: <select id="selectGroup" class="selectpicker">
			<option value="null">-</option>
			<option value="allGroups">All groups</option>
			<c:forEach items="${sessionScope.user.groups}" var="group">
				<option value="${group.id}">"${group.name}"</option>
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
						<td>Teacher score</td>
						<td>Teacher comment</td>
					</tr>
				</thead>
				<tbody class=wrapword>
				</tbody>
			</table>
		</div>
	</div>
	<script>
		$(document)
				.ready(
						function() {
							var table = $('#resultTable');
							if ($(table).find("#tbody").html() !== 0) {
								$('#resultTable').DataTable().clear().draw();
							}
							var table = $('#resultTable').DataTable({
								"aoColumnDefs" : [ {
									'bSortable' : false,
									'aTargets' : [ 0, 3, 4 ],
									'className' : "wrapword",
									"targets" : [ 0, 1, 2, 3, 4 ]
								} ],
								"dom" : '<"top"l>rt<"bottom"ip><"clear">',
								"aoColumns" : [ {
									sWidth : '14%'
								}, {
									sWidth : '12%'
								}, {
									sWidth : '12%'
								}, {
									sWidth : '10%'
								}, {
									sWidth : '20%'
								} ],
								"lengthMenu" : [ 5 ],
								"bDestroy" : true
							});
							$('#selectGroup')
									.on(
											'change',
											function() {
												$('#resultTable tbody')
														.html('');
												$
														.ajax({
															url : 'http://localhost:8080/MyProject/SeeYourHomeworksByGroup',
															data : {
																"selectedGroupId" : $(
																		this)
																		.find(
																				":selected")
																		.val()
															},
															type : 'GET',
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
																	var rowNode = table.row
																			.add(
																					[
																							"<form action = 'http://localhost:8080/MyProject/GetHomeworkServlet' method = 'GET'><input type = 'hidden' name = 'id' value = " + response[i].id+ "><button class='btn btn-link' type = 'submit'>"
																									+ response[i].heading
																									+ "</button></form>",
																							opensRep,
																							closesRep,
																							response[i].teacherScore
																									+ "/100",
																							response[i].teacherComment ])
																			.draw()
																			.node();
																}
																if (response === 'null') {
																	$(
																			'#resultTable tbody')
																			.html(
																					'no data available in table');
																}
															}
														});
											});
						});
		function selectOption(index) {
			document.getElementById("selectGroup").options.selectedIndex = index;
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
					500 : function() {
						location.href = '/MyProject/exceptionPage';
					}
				}
			});
		});
	</script>
</body>
</html>