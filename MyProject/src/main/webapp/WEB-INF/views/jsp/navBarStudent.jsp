<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value="css/navBarCss.css" />" rel="stylesheet">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script> 
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="//cdn.datatables.net/1.10.12/css/jquery.dataTables.css">
<script type="text/javascript" charset="utf8"
	src="//cdn.datatables.net/1.10.12/js/jquery.dataTables.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/css/bootstrap-select.min.css">
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.1/js/bootstrap-select.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css" />
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.10.6/moment.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
<script
	src="https://cdn.datatables.net/fixedcolumns/3.2.2/js/dataTables.fixedColumns.min.js"></script>

<style>

</style>
 <link href="<c:url value="css/navBarCss.css" />" type="text/css" rel="stylesheet">

<body>
					 <%@ include file="footer.jsp"%> 

	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div class="navbar-header" style="background-color: none; z-index: 1">
				<a class="navbar-brand" href="./index"
					style="padding-top: 30px; padding-left: 80px;"> <img
					id="img-href" src="http://ittalents.bg/images/logo-white.png"
					height="70px" width="auto"></a>
			</div>
			<ul class="nav navbar-nav navbar-right"
				style="padding-top: 25px; padding-right: 25px">
				<li><a href="./GetMainPageStudent" style="color: #ffffff"
					class="btn-lg">Home</a></li>
				<li><a href="./UpdateYourProfileServlet" style="color: #ffffff"
					class="btn-lg">Your profile</a></li>
				<li><a href="./SeeScoresServlet"
					class="btn btn-primary btn-lg dropdown-toggle"
					style="background: transparent; border: none; color: #ffffff">Your
						scores</a></li>
				<li class="dropdown" id = "dropdown"><a
					class="btn btn-primary btn-lg dropdown-toggle"
					style="background: transparent; border: none; color: #ffffff"
					onclick="seeGroups()" data-toggle="dropdown" aria-expanded="true"> Your groups <span
						class="caret"></span>
				</a>
					<ul class="dropdown-menu" id="groups"
						style='background-color: #2E71AC;z-index:1'>
					</ul></li>
				<li><a href="./LogoutServlet" style="color: #ffffff"
					class="btn-lg"> Logout </a></li>
			</ul>
		</div>
	</nav>
	<div id="homeworks"></div>				
	
	<script>
/*  	$(document).ready(
 */ function seeGroups() {
	 if(!$('#groups').is(':empty') ) {
		$( "#groups" ).empty();
	} 
// document.getElementById('dropdown').className-='open';
			// document.getElementById('dropdown').className -= ' open';


	$.ajax({
		url : './GetGroupsOfUserServlet',
		type : 'GET',
		dataType : 'json',
		success : function(response) {
			
			for ( var i in response) {
				console.log(response[i].id);
						 $('ul .dropdown-menu').append(
									"<li><a href = './GetHomeworksOfGroupsServlet?id="+response[i].id+"' method = 'GET' style='color:#ffffff;background-color:#2E71AC'>"
									+ response[i].name + "</a></li>");
						 
			}
			if (!$('#dropdown').hasClass("open")) {
				 document.getElementById('dropdown').className += ' open';
			}
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
	      },404 : function(){
				location.href = '/MyProject/pageNotFoundPage';
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