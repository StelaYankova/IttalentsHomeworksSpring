<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
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

</head>
<link href="<c:url value="css/cssReset.css" />" rel="stylesheet">

<link href="<c:url value="css/navBarTeacherAndStudentCss.css" />"
	type="text/css" rel="stylesheet">
<body>
	<%@ include file="footer.jsp"%>
	<nav class="navbar navbar-inverse dropdownSmallScreen">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button"
					class="navbar-toggle glyphicon glyphicon-menu-hamburger"
					data-toggle="collapse" data-target="#myNavbar" id="dropdownButton">

				</button>

				<a class="navbar-brand" href="./index"
					style="padding-top: 30px; padding-left: 80px;"> <img
					id="img-href" src="http://ittalents.bg/images/logo-white.png"
					height="70px" width="auto"></a>
			</div>
			<br>
			<br>
			<br>
			<br>
			<div class="collapse" id="myNavbar">
				<ul class="nav navbar-nav">

					<li><a href="./mainPageTeacher" class="btn-md"
						style="background: transparent; border: none; color: #ffffff">Home</a></li>
					<li><a class="btn-md" href="./updateProfile"
						style="background: transparent; border: none; color: #ffffff"
						class="btn-lg">Your profile</a></li>
					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#"
						style="background: transparent; border: none; color: #ffffff">Manage
							<span class="caret"></span>
					</a>
						<ul class="dropdown-menu">
							<li class="subMenu"><a href="./addOrRemoveStudent"
								class="btn-sm"
								style="border: none; color: #ffffff; background-color: #2E71AC">Add
									or remove student</a></li>
							<li class="subMenu"><a href="./studentsScoresByTeacher"
								class="btn-sm"
								style="border: none; color: #ffffff; background-color: #2E71AC">Students
									scores</a></li>
							<li class="subMenu"><a href="./seeGroups" class="btn-sm"
								style="border: none; color: #ffffff; background-color: #2E71AC">See
									groups</a></li>
							<li class="subMenu"><a href="./addHomework" class="btn-sm"
								style="border: none; color: #ffffff; background-color: #2E71AC">Add
									homework</a></li>
						</ul></li>
					<li><a href="./seeOrUpdateHomeworks" class="btn-md"
						style="background: transparent; border: none; color: #ffffff">See/Update
							homeworks</a></li>
					<li><a href="./logout"
						style="background: transparent; border: none; color: #ffffff"
						class="btn-md"> Logout </a></li>
				</ul>
			</div>
		</div>
	</nav>

	<div class="dropdownLargeScreen">

		<nav class="navbar navbar-inverse" style="background-color: none">
			<div class="container-fluid" style="background-color: none">
				<div class="navbar-header"
					style="background-color: none; z-index: 1">
					<a class="navbar-brand" href="./index"
						style="padding-top: 30px; padding-left: 80px;"> <img
						id="img-href" src="http://ittalents.bg/images/logo-white.png"
						height="70px" width="auto"></a>
				</div>
				<ul class="nav navbar-nav navbar-right"
					style="padding-top: 25px; padding-right: 25px" style="float:none">
					<li><a href="./mainPageTeacher" style="color: #ffffff"
						class="btn-lg">Home</a></li>
					<li><a href="./updateProfile" style="color: #ffffff"
						class="btn-lg">Your profile</a></li>
					<li><a href="./seeOrUpdateHomeworks"
						class="btn btn-primary btn-lg"
						style="background: transparent; border: none; color: #ffffff">See/Update
							homeworks</a></li>
					<li class="dropdown"><a href="#"
						class="btn btn-primary btn-lg dropdown-toggle" type="button"
						data-toggle="dropdown"
						style="background: transparent; border: none; color: #ffffff">
							Manage<span class="caret"></span>
					</a>
						<ul class="dropdown-menu" style='background-color: #2E71AC'>
							<li><a href="./addOrRemoveStudent"
								style="border: none; color: #ffffff; background-color: #2E71AC;">Add
									or remove student</a></li>
							<li><a href="./studentsScoresByTeacher"
								style="border: none; color: #ffffff; background-color: #2E71AC">Students
									scores</a></li>
							<li><a href="./seeGroups"
								style="border: none; color: #ffffff; background-color: #2E71AC">See
									groups</a></li>
							<li><a href="./addHomework"
								style="border: none; color: #ffffff; background-color: #2E71AC">Add
									homework</a></li>
						</ul></li>
					<li><a href="./logout" style="color: #ffffff" class="btn-lg">
							Logout </a></li>
				</ul>
			</div>
		</nav>
	</div>
</body>
<script>
	$(document).ready(function() {
		$(".dropdown-toggle").dropdown();
	});
</script>
</html>