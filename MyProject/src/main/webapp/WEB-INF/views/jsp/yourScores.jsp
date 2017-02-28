<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
#divTable{
	position:absolute;
	top:150px;
	left:10px;
	width:80%;
}

.wrapword {
	white-space: -moz-pre-wrap !important; /* Mozilla, since 1999 */
	white-space: -webkit-pre-wrap; /*Chrome & Safari */
	white-space: -pre-wrap; /* Opera 4-6 */
	white-space: -o-pre-wrap; /* Opera 7 */1
	white-space: pre-wrap; /* css-3 */
	word-wrap: break-word; /* Internet Explorer 5.5+ */
	word-break: break-all;
	white-space: normal;
}

#image {
	position: absolute;
	left: 850px;
}


</style>
<body>
	<%@ include file="navBarStudent.jsp"%>
	<nav class="breadcrumb-nav">
	<ul class="breadcrumb">
<li><a href="http://localhost:8080/MyProject/GetMainPageStudent">Home</a>
			<span class="divider"> <span class="accesshide "><span
					class="arrow_text"></span></span>
		</span></li>
		<li><a href="http://localhost:8080/MyProject/SeeScoresServlet">Your scores</a>
			<span class="divider"> <span class="accesshide "><span
					class="arrow_text"></span>&nbsp;</span>
		</span></li>
		</ul></nav>
	<div id="image">
		<img src="images/logo-black.png" class="img-rounded" width="380" height="236">
	</div>
	Choose a group:
	<select id="selectGroup" class="selectpicker">
		<option value="null">-</option>
		<option value="allGroups">All groups</option>
		<c:forEach items="${sessionScope.user.groups}" var="group">
			<option value="${group.id}">"${group.name}"</option>
		</c:forEach>
	</select>
	<div id = "divTable">
		<table id="resultTable" border="1"
			class="table table-striped table-bordered table-hover" style="width:60%">
			<thead  class = wrapword>
				<tr>
					<td>Heading</td>
					<td>Opens</td>
					<td>Closes</td>
					<td>Teacher score</td>
					<td>Teacher comment</td>
				</tr>
			</thead>
			<tbody class = wrapword>
			</tbody>
		</table>
	</div>
	<script>
	$(document).ready(function() {
		var table = $('#resultTable');

		if ($(table).find("#tbody").html() !== 0) {
			$('#resultTable').DataTable().clear().draw();
		}
		var table = $('#resultTable').DataTable({
			"aoColumnDefs" : [ {
				'bSortable' : false,
				'aTargets' : [ 0, 3, 4 ],
				'className': "wrapword", "targets": [ 0,1,2,3,4]
				
			} ],
		      "dom":'<"top"l>rt<"bottom"ip><"clear">',
			 "aoColumns": [
			               { sWidth: '14%' },
			              { sWidth: '12%' },
			               { sWidth: '12%' },
			               { sWidth: '10%' }, { sWidth: '20%' }],
			"lengthMenu" : [ 5],
			"bDestroy" : true
		});
				
		$('#selectGroup').on('change', function() {
			$('#resultTable tbody').html('');

			$.ajax({
				url : './SeeYourHomeworksByGroup',
				data : {
					"selectedGroupId" : $(this).find(":selected").val()
				},

				type : 'GET',
				dataType : 'json',
				success : function(response) {
						if ($(table).find("#tbody").html() !== 0) {
						$('#resultTable').DataTable().clear().draw();
					}
	
					for ( var i in response) {
						var opens = response[i].opens;
						var opensRep = opens.replace("T", " ");
						var closes = response[i].closes;
						var closesRep = closes.replace("T", " ");
						var rowNode = table.row
												.add(
														["<form action = './GetHomeworkServlet' method = 'GET'><input type = 'hidden' name = 'id' value = " + response[i].id+ "><button class='btn btn-link' type = 'submit'>" + response[i].heading +"</button></form>",
														opensRep,closesRep,response[i].teacherScore + "/100",response[i].teacherComment]).draw().node();
					/*var row = $("<tr>");

						  row.append($("<td class = 'wrapword'><form action = './GetHomeworkServlet' method = 'GET'><input type = 'hidden' name = 'id' value = " + response[i].id+ "><button type = 'submit'>" + response[i].heading +"</button></form></td>"))
						     .append($("<td class = 'wrapword'>"+ response[i].opens+"</td>"))
						     .append($("<td class = 'wrapword'>" +response[i].closes+"</td>"))
						     .append($("<td class = 'wrapword'>"+ response[i].teacherScore+"/100</td>"))
						     .append($("<td class = 'wrapword'>" +response[i].teacherComment+"</td>"));
						     
						 
						  $("#resultTable").append(row);
						*/
					}
					if(response === 'null'){
						
						$('#resultTable tbody').html('no data available in table');

					}
				}
			});
		});
	});

	function selectOption(index){ 
		  document.getElementById("selectGroup").options.selectedIndex = index;
		}
	$(document).ready(function(e) {
		selectOption(0);
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
	    });</script>
</body>
</html>