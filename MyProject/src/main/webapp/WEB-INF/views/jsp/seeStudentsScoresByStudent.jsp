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
<link href="<c:url value="css/seeStudentsScoresByStudentCss.css" />" rel="stylesheet">

</head>
<body>
	<%@ include file="navBarStudent.jsp"%>
	<div class="navPath">
		<nav class="breadcrumb-nav">
			<ul class="breadcrumb">
				<li><a
					href="./mainPageStudent">Home</a><span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
				<li>Your
						scores<span class="divider"><span class="accesshide "><span
							class="arrow_text"></span></span>
				</span></li>
			</ul>
		</nav>
	</div>
	<div id="pageWrapper">
		<h4 id = "pageTitle" ><b><u>Your scores</u></b></h4>
		<div id = "select" >
		Choose a group: <select id="selectGroup" class="selectpicker" data-size="5"> 
			<option value="null">-</option>
			<option value="allGroups">All groups</option>
			<c:forEach items="${sessionScope.user.groups}" var="group">
				<option value="${group.id}">${group.name}</option>
			</c:forEach>
		</select></div>
		<div id="divTable">
			<table id="resultTable" border="1"
				class="table table-striped table-bordered table-hover">
				<thead class="wrapword">
					<tr>
						<th>Heading</th>
						<!-- <td>Opens</td>
						<td>Closes</td> -->
						<th>Teacher score</th>
						<th>Teacher comment</th>
					</tr>
				</thead>
				<tbody class=wrapword>
				</tbody>
			</table>
			<div id = "studentAverageScore">
				<strong><u>Average score: <span id = "score"></span>/100</u></strong>
			</div>
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
									'className' : "wrapword",
									"targets" : [ 0, 1, 2],
									"bSort" : false,
									
								} ],
								"aoColumns": [
{ "bSortable": true },
{ "bSortable": true },
{ "bSortable": false }
],
								"dom" : '<"top"l>rt<"bottom"ip><"clear">',
								/* "aoColumns" : [ {
									sWidth : '14%'
								}, {
									sWidth : '12%'
								}, {
									sWidth : '12%'
								}], */
								/* "lengthMenu" : [ 5 ], */
								"bDestroy" : true,
								"bPaginate" : false,
								/* "ordering" : false,	 */
								"bInfo": false,
								/* "bSort" : false, */
								
							});
							$('#selectGroup')
									.on(
											'change',
											function() {
												document.getElementById('divTable').style.visibility = 'hidden';
												document.getElementById('studentAverageScore').style.display = 'none';
												var selected = $(this).find(":selected").val()
												$('#resultTable tbody')
												.html('');
												if (selected != 'null') {
													if (!$('li#chosenGroupName')
															.is(':empty')) {

														$("li#chosenGroupName")
																.remove();
														var groupName = $(this)
																.find(
																		":selected")
																.text();
														$('.breadcrumb')
																.append(
																		'<li id = "chosenGroupName">'
																				+groupName+'<span class="divider"><span class="accesshide "><span class="arrow_text"></span></span></span></li>');
													}
													
												} else {
													if (!$('li#chosenGroupName')
															.is(':empty')) {

														$("li#chosenGroupName")
																.remove();
													}
												}
												$
														.ajax({
															url : './seeHomeworksByGroupByStudent',
															data : {
																"selectedGroupId" : selected
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
																var averageScore = 0;
																var numberHomeworks = 0;
																for ( var i in response) {
																	averageScore += response[i].teacherScore;
																	numberHomeworks += 1;
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
																							"<form action = './seeChosenHomeworkPageOfStudentByStudent' method = 'GET'><input type = 'hidden' name = 'homeworkId' value = " + response[i].id+ "><button class='btn btn-link' type = 'submit'>"
																									+ response[i].heading
																									+ "</button></form>",
																							/* opensRep,
																							closesRep, */
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
																if(selected == "null"){
																	document.getElementById('divTable').style.visibility = 'hidden';

																}else{
																	document.getElementById('divTable').style.visibility = 'visible';

																}
																var answer = (averageScore/numberHomeworks).toFixed(1);
																document.getElementById("studentAverageScore").style.display = "block";
															
											if (!$('#score').is(':empty')) {
																	$("#score").empty();
																}
																console.log(answer)
																if(answer === 'NaN'){
																	document.getElementById("score").append(0);
																}else{
																	document.getElementById("score").append(answer);
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
</body>
</html>