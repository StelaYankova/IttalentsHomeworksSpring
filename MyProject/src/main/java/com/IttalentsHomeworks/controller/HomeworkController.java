package com.IttalentsHomeworks.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.IttalentsHomeworks.DAO.GroupDAO;
import com.IttalentsHomeworks.DAO.UserDAO;
import com.IttalentsHomeworks.DAO.ValidationsDAO;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.NotUniqueUsernameException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.Exceptions.ValidationException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.Homework;
import com.IttalentsHomeworks.model.HomeworkDetails;
import com.IttalentsHomeworks.model.Student;
import com.IttalentsHomeworks.model.Task;
import com.IttalentsHomeworks.model.Teacher;
import com.IttalentsHomeworks.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
@Controller
@MultipartConfig
public class HomeworkController {
	private static final String SAVE_DIR = "/Users/Stela/Desktop/imagesIttalentsHomework";

	@RequestMapping(value = "/AddHomework", method = RequestMethod.GET)
	protected String addHomeworkGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "addHomework";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/AddHomework", method = RequestMethod.POST)
	protected String addHomeworkPost(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file1,
			HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String heading = request.getParameter("name").trim();
			String[] selectedGroups = request.getParameterValues("groups");
			String opens = request.getParameter("opens").replace("/", "-").trim();
			String closes = request.getParameter("closes").replace("/", "-").trim();
			MultipartFile filePart = file1;
			String numberOfTasksString = request.getParameter("numberOfTasks").trim();
			request.setAttribute("nameTry", heading);
			request.setAttribute("opensTry", opens.replace("-", "/"));
			request.setAttribute("closesTry", closes.replace("-", "/"));
			if (isHomeworkNumberOfTasksLengthValid(numberOfTasksString)) {
				request.setAttribute("numberOfTasksTry", Integer.parseInt(numberOfTasksString));
			}
			request.setAttribute("selectedGroupsTry", selectedGroups);
			// empty fields
			if (isThereEmptyField(heading, opens, closes, filePart, numberOfTasksString, selectedGroups)) {
				request.setAttribute("emptyFields", true);
			} else {
				// heading valid
				File file = null;
				try {
					int numberOfTasks = 0;
					boolean isHeadingValid = false;
					boolean isHeadingUnique = false;
					if (areCharactersHeadingValid(heading) && isLengthHeadingValid(heading)) {
						isHeadingValid = true;
						if (isHomeworkHeadingUnique(heading)) {
							isHeadingUnique = true;
						}
					}
					request.setAttribute("validHeading", isHeadingValid);
					request.setAttribute("uniqueHeading", isHeadingUnique);
					// heading unique
					// opening time
					boolean isOpeningTimeValid = false;
					if (isHomeworkOpeningTimeValid(opens)) {
						isOpeningTimeValid = true;
					}
					request.setAttribute("validOpeningTime", isOpeningTimeValid);
					// closing time
					boolean isClosingTimeValid = false;
					if (isHomeworkClosingTimeValid(opens, closes)) {
						isClosingTimeValid = true;
					}
					request.setAttribute("validClosingTime", isClosingTimeValid);
					// file
					boolean isFileValid = false;
					if (isHomeworkContentTypeValid(filePart) && isHomeworkSizeValid(filePart)) {
						isFileValid = true;
					}
					request.setAttribute("validFile", isFileValid);
					// numTasks
					boolean areTasksValid = false;
					if (isHomeworkNumberOfTasksLengthValid(numberOfTasksString)) {
						numberOfTasks = Integer.parseInt(request.getParameter("numberOfTasks"));
						if (isHomeworkNumberOfTasksValid(numberOfTasks)) {
							areTasksValid = true;
						}
					}
					request.setAttribute("validTasks", areTasksValid);
					// groups
					boolean areGroupsValid = false;
					if (doAllGroupsExist(selectedGroups)) {
						areGroupsValid = true;
					}
					request.setAttribute("validGroups", areGroupsValid);
					if (isHeadingValid == true && isHeadingUnique == true && isOpeningTimeValid == true
							&& isClosingTimeValid == true && isFileValid == true && areTasksValid == true
							&& areGroupsValid == true) {
						String savePath = SAVE_DIR;
						File fileSaveDir = new File(savePath);
						if (!fileSaveDir.exists()) {
							fileSaveDir.mkdir();
						}
						String fileName = " ";
						fileName = "hwName" + heading + ".pdf";
						OutputStream out = null;
						InputStream filecontent = null;
						file = new File(SAVE_DIR + File.separator + fileName);
						if (!file.exists()) {
							file.createNewFile();
						}
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
						LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
						LocalDateTime closingTime = LocalDateTime.parse(closes, formatter);
						ArrayList<Group> groupsForHw = new ArrayList<>();
						HomeworkDetails homeworkDetails = new HomeworkDetails(heading, openingTime, closingTime,
								numberOfTasks, fileName);
						for (int i = 0; i < selectedGroups.length; i++) {
							int id = Integer.parseInt(selectedGroups[i]);
							Group g = GroupDAO.getInstance().getGroupById(id);
							groupsForHw.add(g);
						}
						GroupDAO.getInstance().createHomeworkDetails(homeworkDetails, groupsForHw);
						request.setAttribute("invalidFields", false);
						// if its ok
						out = new FileOutputStream(file, true);
						filecontent = filePart.getInputStream();

						int read = 0;
						final byte[] bytes = new byte[1024];
						while ((read = filecontent.read(bytes)) != -1) {
							out.write(bytes, 0, read);
						}
						ArrayList<Group> allGroupsUpdated = GroupDAO.getInstance().getAllGroups();
						request.getServletContext().setAttribute("allGroups", allGroupsUpdated);
						ArrayList<Teacher> allTeachers = UserDAO.getInstance().getAllTeachers();
						request.getServletContext().setAttribute("allTeachers", allTeachers);
						for (Teacher t : allTeachers) {
							t.setGroups(UserDAO.getInstance().getGroupsOfUser(t.getId()));
						}
					}
				} catch (GroupException | UserException e) {
					if (file.exists()) {
						file.delete();
					}
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (ValidationException e) {
					if (file.exists()) {
						file.delete();
					}
					request.setAttribute("invalidFields", true);
				} catch (NotUniqueUsernameException e) {
					request.setAttribute("invalidFields", true);
					e.printStackTrace();
				}
			}
			return "addHomework";
		}
		return "forbiddenPage";
	}

	private boolean isThereEmptyField(String heading, String opens, String closes, MultipartFile filePart,
			String numberOfTasksString, String[] selectedGroups) {
		if (heading == null || heading.equals("") || opens == null || opens.equals("") || closes == null
				|| closes.equals("") || numberOfTasksString == null || numberOfTasksString.equals("")
				|| selectedGroups == null) {
			return true;
		}
		if (filePart.getSize() == 0) {
			return true;
		}
		return false;
	}

	private boolean isLengthHeadingValid(String heading) {
		if (heading.length() >= 5 && heading.length() <= 40) {
			return true;
		}
		return false;
	}

	private boolean areCharactersHeadingValid(String heading) {
		for (int i = 0; i < heading.length(); i++) {
			if (!(((int) heading.charAt(i) >= 32 && (int) heading.charAt(i) <= 126))) {
				return false;
			}
		}
		return true;
	}

	private boolean isHomeworkHeadingUnique(String heading) throws GroupException {
		if (ValidationsDAO.getInstance().isHomeworkHeadingUnique(heading)) {
			return true;
		}
		return false;
	}

	private boolean isHomeworkOpeningTimeValid(String opens) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
			LocalDate openingDate = openingTime.toLocalDate();
			if (openingDate.isAfter(LocalDate.now().minusDays(1))
					&& openingDate.isBefore(LocalDate.now().plusMonths(6).minusDays(1))) {
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isHomeworkClosingTimeValid(String opens, String closes) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
			LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
			long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);
			if (closingDateTime.isAfter(LocalDateTime.now()) && closingDateTime.isAfter(openingDateTime)
					&& diffInMonths < 6) {
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isHomeworkNumberOfTasksLengthValid(String numberOfTasks) {
		if (numberOfTasks.length() >= 10) {
			return false;
		}
		return true;
	}

	private boolean isHomeworkNumberOfTasksValid(int numberOfTasks) {
		if (numberOfTasks >= 1 && numberOfTasks <= 40) {
			return true;
		}
		return false;
	}

	private boolean isHomeworkContentTypeValid(MultipartFile filePart) {
		String contentType = filePart.getOriginalFilename().substring(filePart.getOriginalFilename().indexOf("."));
		if (!(contentType.equals(".pdf"))) {
			return false;
		}
		return true;
	}

	private boolean isHomeworkSizeValid(MultipartFile filePart) {
		long sizeInMb = filePart.getSize() / (1024 * 1024);
		if (sizeInMb > 20) {
			return false;
		}
		return true;
	}

	private boolean doAllGroupsExist(String[] selectedGroups) throws GroupException, UserException {
		for (String groupId : selectedGroups) {
			try {
				Group currGroup = GroupDAO.getInstance().getGroupById(Integer.parseInt(groupId));
				String groupName = currGroup.getName();
				if (ValidationsDAO.getInstance().isGroupNameUnique(groupName)) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = "/GetCurrHomeworkOfStudent", method = RequestMethod.GET)
	protected String getCurrHomeworkOfStudent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "homeworkOfStudent";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/GetHomeworkServlet", method = RequestMethod.GET)
	protected String getHomework(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User userTry = (User) request.getSession().getAttribute("user");
		if (!userTry.isTeacher()) {
			int homeworkId = 0;
			Student user = (Student) request.getSession().getAttribute("user");
			String idHomework = request.getParameter("id");
			if(idHomework != null){
				int length = request.getParameter("id").length();
				String number = request.getParameter("id");
				if (length > 0 && length < 10) {
					for (int i = 0; i < length; i++) {
						if ((char) number.charAt(i) < 48 || (char) number.charAt(i) > 57) {
							return "pageNotFound";
						}
					}
				}else{
					return "pageNotFound";
				}
				 homeworkId = Integer.parseInt(idHomework);

			}else{
				if (request.getSession().getAttribute("currHomework") != null) {
					Homework hd = (Homework) request.getSession().getAttribute("currHomework");
					homeworkId = hd.getHomeworkDetails().getId();
				} else {
					return "pageNotFound";
				}
			}
			boolean doesUserHaveHomework = false;
			Homework homework = null;
			for (Homework h : user.getHomeworks()) {
				if (h.getHomeworkDetails().getId() == homeworkId) {
					homework = new Homework(h.getTeacherGrade(), h.getTeacherComment(), h.getTasks(),
							h.getHomeworkDetails());
					doesUserHaveHomework = true;
					break;
				}
			}
			if(doesUserHaveHomework){
			request.getSession().setAttribute("currHomework", homework);
			boolean hasUploadTimePassed = true;
			if (homework.getHomeworkDetails().getClosingTime().isAfter(LocalDateTime.now())) {
				hasUploadTimePassed = false;
			}
			boolean hasUploadTimeCome = false;
			if (LocalDateTime.now().isAfter(homework.getHomeworkDetails().getOpeningTime())) {
				hasUploadTimeCome = true;
			}
			request.getSession().setAttribute("hasUploadTimePassed", hasUploadTimePassed);
			request.getSession().setAttribute("hasUploadTimeCome", hasUploadTimeCome);
			return "currHomeworkPageStudent";
		}
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/GetHomeworksOfGroupsServlet", method = RequestMethod.GET)
	protected String getHomeworksOfGroups(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User userTry = (User) request.getSession().getAttribute("user");
		if (!userTry.isTeacher()) {
			User user = (User) request.getSession().getAttribute("user");
			String groupChosen = request.getParameter("groupId");
			if(groupChosen != null){
				int length = request.getParameter("groupId").length();
				String number = request.getParameter("groupId");
				if (length > 0 && length < 10) {
					for (int i = 0; i < length; i++) {
						if ((char) number.charAt(i) < 48 || (char) number.charAt(i) > 57) {
							return "pageNotFound";
						}
					}
				}else{
					return "pageNotFound";
				}
			try {
				int groupId = Integer.parseInt(groupChosen);
				boolean doesUserHaveGroup = false;
				Group group = null;
				for (Group g : user.getGroups()) {
					if (g.getId() == groupId) {
						group = g;
						doesUserHaveGroup = true;
						break;
					}
				}
				if(doesUserHaveGroup){
				ArrayList<HomeworkDetails> homeworks = new ArrayList<>();
				for (HomeworkDetails h : group.getHomeworks()) {
					long days = LocalDateTime.now().until(h.getClosingTime(), ChronoUnit.DAYS);
					HomeworkDetails currHd = new HomeworkDetails(h.getHeading(), h.getOpeningTime(), h.getClosingTime(),
							h.getNumberOfTasks(), h.getTasksFile());
					currHd.setDaysLeft((int) days);
					currHd.setId(GroupDAO.getInstance().getHomeworkDetailsId(currHd));
					homeworks.add(currHd);
				}
				request.getSession().setAttribute("currHomeworksOfGroup", homeworks);
				}else{
					return "forbiddenPage";
				}
			} catch (GroupException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			}
			
			return "seeYourHomeworks";
		}else{
			return "pageNotFound";
		}
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/ReadHomeworkServlet", method = RequestMethod.GET)
	protected String readHomework(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		String fileName = request.getParameter("fileName").trim();
		String homeworkName = fileName.substring(6, fileName.length() - 4);
		boolean canUserAccessHomeworkTasks = false;
		if (!user.isTeacher()) {
			for (Group g : user.getGroups()) {
				for (HomeworkDetails hd : g.getHomeworks()) {
					if (hd.getHeading().equals(homeworkName)) {
						canUserAccessHomeworkTasks = true;
						break;
					}
				}
			}
		} else {
			canUserAccessHomeworkTasks = true;
		}
		if (canUserAccessHomeworkTasks) {
			File file = new File(SAVE_DIR + File.separator + fileName);
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setContentLength((int) file.length());
			FileInputStream fileInputStream = new FileInputStream(file);
			OutputStream responseOutputStream = response.getOutputStream();
			int bytes;
			while ((bytes = fileInputStream.read()) != -1) {
				responseOutputStream.write(bytes);
			}
			fileInputStream.close();
			return "currHomeworkPageStudent";
		}
		return "forbiddenPage";
	}
	
	@RequestMapping(value = "/SaveChangedSolutionText", method = RequestMethod.POST)
	protected void changeSolutionText(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		User user = (User) request.getSession().getAttribute("user");
		Homework homework = (Homework) request.getSession().getAttribute("currHomework");
		HomeworkDetails homeworkDetails = homework.getHomeworkDetails();
		String text = request.getParameter("text");
		int taskNum = Integer.parseInt(request.getParameter("taskNum")) - 1;
		String fileName = SAVE_DIR + File.separator + "hwId" + homeworkDetails.getId() + "userId" + user.getId()
		+ "taskNum" + taskNum + ".java";
		if(text.trim().length() > 0 && text.length() < 1048576){
		 PrintStream out = new PrintStream(new FileOutputStream(fileName));
		    out.print(text);
		    out.flush();
		    out.close();
		    response.setStatus(200);
		}else{
		    response.setStatus(400);
		}
		
	}
	
	@RequestMapping(value = "/ReadJavaFileServlet", method = RequestMethod.GET)
	protected void readJavaFile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int taskNum = Integer.parseInt(request.getParameter("taskNum")) - 1;
		User user = (User) request.getSession().getAttribute("user");
		Homework homework = (Homework) request.getSession().getAttribute("currHomework");
		HomeworkDetails homeworkDetails = homework.getHomeworkDetails();
		String fileName = null;
		if (!user.isTeacher()) {
			fileName = SAVE_DIR + File.separator + "hwId" + homeworkDetails.getId() + "userId" + user.getId()
					+ "taskNum" + taskNum + ".java";
		} else {
			int studentId = (int) request.getSession().getAttribute("studentId");
			fileName = SAVE_DIR + File.separator + "hwId" + homeworkDetails.getId() + "userId" + studentId + "taskNum"
					+ taskNum + ".java";
		}
		File f = new File(fileName);
		String strLine = "";
		if (f.exists()) {
				strLine = new String(Files.readAllBytes(Paths.get(fileName)));
		} else {
			strLine = "Solution is not uploaded yet.";
		}
		JsonObject obj = new JsonObject();
		Task task = homework.getTasks().get(taskNum);
		if (strLine.equals("Solution is not uploaded yet.")) {
			obj.addProperty("uploadedOn", "-");
			obj.addProperty("solution", strLine);
		} else {
			obj.addProperty("uploadedOn", task.getUploadedOn().toString());
			obj.addProperty("solution", strLine);
		}
		response.setStatus(200);
		response.getWriter().write(obj.toString());
	}

	@RequestMapping(value = "/RemoveHomeworkDetails", method = RequestMethod.POST)
	protected String removeHomeworkDetails(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			HomeworkDetails hd = (HomeworkDetails) request.getSession().getAttribute("currHomework");
			try {
				GroupDAO.getInstance().removeHomeworkDetails(hd);
				request.getSession().removeAttribute("currHomework");
			} catch (GroupException | UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			}
			request.setAttribute("invalidFields", false);
			return "seeHomeworks";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/SeeAllHomeworksOfStudentByGroupServlet", method = RequestMethod.GET)
	protected void seeAllHomeworksOfStudentByGroupServlet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			try {
				int groupId = Integer.parseInt(request.getParameter("groupId"));

				int studentId = Integer.parseInt(request.getParameter("studentId"));
				Group selectedGroup = null;
				JsonArray array = new JsonArray();
				ArrayList<HomeworkDetails> homeworkDetailsByGroup = new ArrayList<>();
				selectedGroup = GroupDAO.getInstance().getGroupById(groupId);
				homeworkDetailsByGroup.addAll(GroupDAO.getInstance().getHomeworkDetailsOfGroup(selectedGroup));

				boolean hasStudentGivenMinOneTask = false;

				for (HomeworkDetails hd : homeworkDetailsByGroup) {
					JsonObject obj = new JsonObject();
					obj.addProperty("heading", hd.getHeading());
					obj.addProperty("id", hd.getId());
					obj.addProperty("opens", hd.getOpeningTime().toString());
					obj.addProperty("closes", hd.getClosingTime().toString());
					for (Homework h : UserDAO.getInstance().getHomeworksOfStudentByGroup(studentId, selectedGroup)) {
						if (hd.getId() == h.getHomeworkDetails().getId()) {
							int grade = h.getTeacherGrade();
							String comment = h.getTeacherComment();
							for (Task t : h.getTasks()) {
								String x = t.getSolution();
								if (x != null) {
									hasStudentGivenMinOneTask = true;
									break;
								}
							}

							obj.addProperty("hasStudentGivenMinOneTask", hasStudentGivenMinOneTask);
							obj.addProperty("teacherScore", grade);
							obj.addProperty("teacherComment", comment);
							hasStudentGivenMinOneTask = false;
							break;
						}
					}
					array.add(obj);
				}
				response.setStatus(200);
				response.getWriter().write(array.toString());
			} catch (GroupException | UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(500);
			}
		} else {
			response.setStatus(403);
		}
	}

	@RequestMapping(value = "/seeHomeworksOfGroupServlet", method = RequestMethod.GET)
	protected void seeHomeworksOfGroupServlet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			try {
				ArrayList<HomeworkDetails> homeworkDetailsByGroup = new ArrayList<>();
				JsonArray array = new JsonArray();
				if (!(request.getParameter("chosenGroup").equals("null"))) {
					if (!(request.getParameter("chosenGroup").equals("allGroups"))) {
						int groupId = Integer.parseInt(request.getParameter("chosenGroup"));
						Group chosenGroup = null;
						chosenGroup = GroupDAO.getInstance().getGroupById(groupId);
						homeworkDetailsByGroup.addAll(GroupDAO.getInstance().getHomeworkDetailsOfGroup(chosenGroup));
					} else {
						homeworkDetailsByGroup = GroupDAO.getInstance().getAllHomeworksDetails();
					}
					for (HomeworkDetails hd : homeworkDetailsByGroup) {
						JsonObject obj = new JsonObject();
						obj.addProperty("heading", hd.getHeading());
						obj.addProperty("id", hd.getId());
						obj.addProperty("opens", hd.getOpeningTime().toString());
						obj.addProperty("closes", hd.getClosingTime().toString());
						array.add(obj);
					}
				}
				response.setStatus(200);
				response.getWriter().write(array.toString());
			} catch (GroupException | UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(500);
			}
		} else {
			response.setStatus(403);
		}
	}

	@RequestMapping(value = "/SeeHomeworksServlet", method = RequestMethod.GET)
	protected String seeHomeworks(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "seeHomeworks";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/SeeYourHomeworksByGroup", method = RequestMethod.GET)
	protected void seeYourHomeworksByGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User userTest = (User) request.getSession().getAttribute("user");
		if (!userTest.isTeacher()) {
			ArrayList<HomeworkDetails> homeworkDetailsByGroup = new ArrayList<>();
			Student user = (Student) request.getSession().getAttribute("user");
			Group selectedGroup = null;
			if (!request.getParameter("selectedGroupId").equals("null")) {
				try {
					JsonArray array = new JsonArray();
					if (request.getParameter("selectedGroupId").equals("allGroups")) {
						ArrayList<Integer> checkedIds = new ArrayList<>();
						for (Group g : user.getGroups()) {
							homeworkDetailsByGroup.addAll(GroupDAO.getInstance().getHomeworkDetailsOfGroup(g));
							for (HomeworkDetails hd : homeworkDetailsByGroup) {
								if (!(checkedIds.contains((Integer) hd.getId()))) {
									JsonObject obj = new JsonObject();
									obj.addProperty("id", hd.getId());
									obj.addProperty("heading", hd.getHeading());
									obj.addProperty("opens", hd.getOpeningTime().toString());
									obj.addProperty("closes", hd.getClosingTime().toString());
									for (Homework h : UserDAO.getInstance().getHomeworksOfStudent(user.getId())) {
										if (hd.getId() == h.getHomeworkDetails().getId()) {
											int grade = h.getTeacherGrade();
											String comment = h.getTeacherComment();
											obj.addProperty("teacherScore", grade);
											obj.addProperty("teacherComment", comment);
											break;
										}
									}
									checkedIds.add(hd.getId());
									array.add(obj);
								}

							}
						}
						response.setStatus(200);
						response.getWriter().write(array.toString());
					} else {
						int selectedGroupId = Integer.parseInt(request.getParameter("selectedGroupId"));
						selectedGroup = GroupDAO.getInstance().getGroupById(selectedGroupId);
						homeworkDetailsByGroup.addAll(GroupDAO.getInstance().getHomeworkDetailsOfGroup(selectedGroup));
						for (HomeworkDetails hd : homeworkDetailsByGroup) {
							JsonObject obj = new JsonObject();
							obj.addProperty("heading", hd.getHeading());
							obj.addProperty("id", hd.getId());
							obj.addProperty("opens", hd.getOpeningTime().toString());
							obj.addProperty("closes", hd.getClosingTime().toString());
							for (Homework h : UserDAO.getInstance().getHomeworksOfStudentByGroup(user.getId(),
									selectedGroup)) {
								if (hd.getId() == h.getHomeworkDetails().getId()) {
									int grade = h.getTeacherGrade();
									String comment = h.getTeacherComment();

									obj.addProperty("teacherScore", grade);
									obj.addProperty("teacherComment", comment);
									break;
								}
							}
							array.add(obj);
						}
						response.setStatus(200);
						response.getWriter().write(array.toString());
					}
				} catch (GroupException | UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					response.setStatus(500);
				}
			}
		} else {
			response.setStatus(403);
		}
	}

	@RequestMapping(value = "/UpdateHomeworkServlet", method = RequestMethod.GET)
	protected String updateHomeworkPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getParameter("chosenHomework") != null
					|| request.getSession().getAttribute("currHomework") != null) {
				int hwId = 0;
				HomeworkDetails hd;
				try {
					if (request.getParameter("chosenHomework") != null) {
						int length = request.getParameter("chosenHomework").length();
						String number = request.getParameter("chosenHomework");
						if (length > 0 && length < 10) {
							for (int i = 0; i < length; i++) {
								if ((char) number.charAt(i) < 48 || (char) number.charAt(i) > 57) {
									return "pageNotFound";
								}
							}
						}else{
							return "pageNotFound";
						}
						hwId = Integer.parseInt(request.getParameter("chosenHomework"));
						hd = GroupDAO.getInstance().getHomeworkDetailsById(hwId);
						if (hd != null) {
							request.getSession().setAttribute("currHomework", hd);
						} else {
							return "pageNotFound";
						}
					} else {
						HomeworkDetails hd1 = (HomeworkDetails) request.getSession().getAttribute("currHomework");
						hwId = hd1.getId();
					}
				} catch (GroupException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}
				return "updateHomework";
			}
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/UpdateHomeworkServlet", method = RequestMethod.POST)
	protected String updateHomework(HttpServletRequest request,
			@RequestParam(value = "file") MultipartFile fileMultiPart, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			int homeworkDetailsId = ((HomeworkDetails) request.getSession().getAttribute("currHomework")).getId();
			String heading = request.getParameter("name").trim();
			String fileName = " ";
			String[] selectedGroups = request.getParameterValues("groups");
			MultipartFile filePart = fileMultiPart;
			String opens = request.getParameter("opens").trim().replace("/", "-");
			String closes = request.getParameter("closes").trim().replace("/", "-");
			String numberOfTasksString = request.getParameter("numberOfTasks").trim();
			File file = null;
			try {
				// empty fields (except file)
				if (isThereEmptyFieldUpdateHomework(heading, opens, closes, numberOfTasksString, selectedGroups)) {
					request.getSession().setAttribute("emptyFields", true);
				} else {
					HomeworkDetails currHd = null;
					currHd = GroupDAO.getInstance().getHomeworkDetailsById(homeworkDetailsId);
					// valid heading
					int numberOfTasks = 0;
					boolean isHeadingValid = false;
					boolean isHeadingUnique = false;
					if (areHomeworkUpdateCharactersValid(heading) && isHomeworkUpdateLengthValid(heading)) {
						isHeadingValid = true;
						if (isHomeworkUpdateHeadingUnique(heading, currHd)) {
							isHeadingUnique = true;
						}
					}
					request.getSession().setAttribute("validHeading", isHeadingValid);
					request.getSession().setAttribute("uniqueHeading", isHeadingUnique); // unique
					// heading
					// opening time
					boolean isOpeningTimeValid = false;
					if (isHomeworkUpdateOpeningTimeValid(opens, currHd)) {
						isOpeningTimeValid = true;
					}
					request.getSession().setAttribute("validOpeningTime", isOpeningTimeValid);
					// closing time
					boolean isClosingTimeValid = false;
					if (isHomeworkUpdateClosingTimeValid(opens, closes, currHd)) {
						isClosingTimeValid = true;
					}
					request.getSession().setAttribute("validClosingTime", isClosingTimeValid);
					// numTasks
					boolean areTasksValid = false;

					if (isHomeworkUpdateNumberOfTasksLengthValid(numberOfTasksString)) {
						numberOfTasks = Integer.parseInt(request.getParameter("numberOfTasks"));
						if (isHomeworkUpdateNumberOfTasksValid(numberOfTasks)) {
							areTasksValid = true;
						}
					}
					request.getSession().setAttribute("validTasks", areTasksValid);
					// do all groups exist
					boolean areGroupsValid = false;
					if (doAllGroupsExistHomeworkUpdate(selectedGroups)) {
						areGroupsValid = true;
					}
					request.getSession().setAttribute("validGroups", areGroupsValid);
					boolean isFileValid = false;
					if (filePart.getSize() == 0) {
						isFileValid = true;
					} else {
						if (isFileUpdateHomeworkValid(filePart)) {
							isFileValid = true;
						}
					}
					request.getSession().setAttribute("validFile", isFileValid);

					if (isHeadingValid == true && isHeadingUnique == true && isOpeningTimeValid == true
							&& isClosingTimeValid == true && areTasksValid == true && areGroupsValid == true
							&& isFileValid == true) {
						fileName = "hwName" + heading + ".pdf";
						file = new File(SAVE_DIR + File.separator + fileName);
						File file1 = null;
						if (!file.exists()) {
							String oldNameOfFile;
							oldNameOfFile = ((HomeworkDetails) GroupDAO.getInstance()
									.getHomeworkDetailsById(homeworkDetailsId)).getHeading();
							file1 = new File(SAVE_DIR + File.separator + "hwName" + oldNameOfFile + ".pdf");
							Files.copy(file1.toPath(), file.toPath());
						}
						if (filePart.getSize() != 0) {
							isFileValid = true;
							String savePath = SAVE_DIR;
							File fileSaveDir = new File(savePath);
							if (!fileSaveDir.exists()) {
								fileSaveDir.mkdir();
							}
							// final String fileName =
							// extractFileName(filePart);
							fileName = "hwName" + heading + ".pdf";
						} else {
							fileName = "hwName" + heading + ".pdf";
						}
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
						LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
						LocalDateTime closingTime = LocalDateTime.parse(closes, formatter);
						ArrayList<Group> groupsForHw = new ArrayList<>();
						HomeworkDetails homeworkDetails = new HomeworkDetails(homeworkDetailsId, heading, openingTime,
								closingTime, numberOfTasks, fileName);
						if (request.getParameterValues("groups") != null) {
							for (int i = 0; i < selectedGroups.length; i++) {
								int id = Integer.parseInt(selectedGroups[i]);
								Group g = GroupDAO.getInstance().getGroupById(id);
								groupsForHw.add(g);

							}
						}
						GroupDAO.getInstance().updateHomeworkDetails(homeworkDetails, groupsForHw);
						// if its ok
						OutputStream out = null;
						InputStream filecontent = null;
						out = new FileOutputStream(file, true);
						filecontent = filePart.getInputStream();

						int read = 0;
						final byte[] bytes = new byte[1024];

						while ((read = filecontent.read(bytes)) != -1) {
							out.write(bytes, 0, read);
						}
						request.getSession().setAttribute("currHomework", homeworkDetails);
						request.getServletContext().removeAttribute("allGroups");
						ArrayList<Group> allGroups = GroupDAO.getInstance().getAllGroups();
						request.getServletContext().setAttribute("allGroups", allGroups);
						request.getSession().setAttribute("invalidFields", false);
						if (file1 != null) {
							file1.delete();
						}

					}
				}
			} catch (GroupException | UserException e) {
				if (file.exists()) {
					file.delete();
				}
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			} catch (ValidationException e) {
				if (file.exists()) {
					file.delete();
				}
				request.getSession().setAttribute("invalidFields", true);
			} catch (NotUniqueUsernameException e) {
				request.getSession().setAttribute("invalidFields", true);
				e.printStackTrace();
			}
			return "redirect:./UpdateHomeworkServlet";
		}
		return "forbiddenPage";
	}

	private boolean isFileUpdateHomeworkValid(MultipartFile filePart) {
		if (isHomeworkUpdateContentTypeValid(filePart) && isHomeworkUpdateSizeValid(filePart)) {
			return true;
		}
		return false;
	}

	private boolean isThereEmptyFieldUpdateHomework(String heading, String opens, String closes,
			String numberOfTasksString, String[] selectedGroups) {
		if (heading == null || heading.equals("") || opens == null || opens.equals("") || closes == null
				|| closes.equals("") || numberOfTasksString == null || numberOfTasksString.equals("")
				|| selectedGroups == null) {
			return true;
		}
		return false;

	}

	private boolean isHomeworkUpdateLengthValid(String heading) {
		if (heading.length() >= 5 && heading.length() <= 40) {
			return true;
		}
		return false;
	}

	private boolean areHomeworkUpdateCharactersValid(String heading) {
		for (int i = 0; i < heading.length(); i++) {
			if (!(((int) heading.charAt(i) >= 32 && (int) heading.charAt(i) <= 126))) {
				return false;
			}
		}
		return true;
	}

	private boolean isHomeworkUpdateHeadingUnique(String heading, HomeworkDetails currHd) throws GroupException {
		if (currHd.getHeading().equals(heading) || ValidationsDAO.getInstance().isHomeworkHeadingUnique(heading)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isHomeworkUpdateOpeningTimeValid(String opens, HomeworkDetails currHd) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

			LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
			LocalDate openingDate = openingTime.toLocalDate();
			if (openingTime.equals(currHd.getOpeningTime())) {
				return true;
			} else {
				if (openingDate.isAfter(LocalDate.now().minusDays(1))
						&& openingDate.isBefore(LocalDate.now().plusMonths(6).minusDays(1))) {
					return true;
				} else {
					return false;
				}
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isHomeworkUpdateClosingTimeValid(String opens, String closes, HomeworkDetails currHd) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
			LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
			long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);
			if (closingDateTime.equals(currHd.getClosingTime())) {
				return true;
			} else {
				if (closingDateTime.isAfter(LocalDateTime.now()) && closingDateTime.isAfter(openingDateTime)
						&& diffInMonths < 6) {
					return true;
				} else {
					return false;
				}
			}
		} catch (NumberFormatException e) {
			return false;

		}
	}

	private boolean isHomeworkUpdateNumberOfTasksLengthValid(String numberOfTasks) {
		if (numberOfTasks.length() >= 10) {
			return false;
		}
		return true;
	}

	private boolean isHomeworkUpdateNumberOfTasksValid(int numberOfTasks) {
		if (numberOfTasks >= 1 && numberOfTasks <= 40) {
			return true;
		}
		return false;
	}

	private boolean isHomeworkUpdateContentTypeValid(MultipartFile filePart) {
		String contentType = filePart.getOriginalFilename().substring(filePart.getOriginalFilename().indexOf("."));
		if (!(contentType.equals(".pdf"))) {
			return false;
		}
		return true;
	}

	private boolean isHomeworkUpdateSizeValid(MultipartFile filePart) {
		long sizeInMb = filePart.getSize() / (1024 * 1024);
		if (sizeInMb > 20) {
			return false;
		}
		return true;
	}

	private boolean doAllGroupsExistHomeworkUpdate(String[] selectedGroups) throws GroupException, UserException {
		for (String groupId : selectedGroups) {
			try {
				Group currGroup = GroupDAO.getInstance().getGroupById(Integer.parseInt(groupId));
				String groupName = currGroup.getName();
				if (ValidationsDAO.getInstance().isGroupNameUnique(groupName)) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = "/UpdateTeacherGradeAndCommentServlet", method = RequestMethod.POST)
	protected String updateTeacherGradeAndCommentServlet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			Homework homework = (Homework) request.getSession().getAttribute("currHomework");
			HomeworkDetails hdOfhomework = null;
			String teacherComment = request.getParameter("comment").trim();
			String teacherGradeString = request.getParameter("grade").trim();
			int studentId = (int) request.getSession().getAttribute("studentId");
			int teacherGrade = 0;
			// grade not empty
			if (isGradeEmpty(teacherGradeString)) {
				request.getSession().setAttribute("emptyFields", true); // success

			} else if (isGradeTooLong(teacherGradeString)) {
				request.getSession().setAttribute("GradeTooLong", true); // success

			} else {
				teacherGrade = Integer.parseInt(teacherGradeString);
				// grade >=0 <=100
				boolean isGradeValueValid = false;
				if (isGradeValueValid(teacherGrade)) {
					isGradeValueValid = true;
				}
				request.getSession().setAttribute("validGrade", isGradeValueValid); // success

				// comment max length = 150
				boolean isCommentLengthValid = false;
				if (isCommentLengthValid(teacherComment)) {
					isCommentLengthValid = true;
				}
				request.getSession().setAttribute("validComment", isCommentLengthValid); // success
				if (isGradeValueValid == true && isCommentLengthValid == true) {
					ArrayList<Homework> homeworksOfStudent;
					try {
						homeworksOfStudent = UserDAO.getInstance().getHomeworksOfStudent(studentId);
						for (Homework h : homeworksOfStudent) {
							if (h.getHomeworkDetails().getId() == homework.getHomeworkDetails().getId()) {
								hdOfhomework = h.getHomeworkDetails();
							}
						}

						UserDAO.getInstance().setTeacherComment(hdOfhomework, studentId, teacherComment);
						UserDAO.getInstance().setTeacherGrade(hdOfhomework, studentId, teacherGrade);
						homework.setTeacherComment(teacherComment);
						homework.setTeacherGrade(teacherGrade);
					} catch (UserException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						return "exception";
					} catch (ValidationException e) {
						request.getSession().setAttribute("invalidFields", true);
					}
				}
			}
			return "redirect:./GetCurrHomeworkOfStudent";
		}
		return "forbiddenPage";
	}

	private boolean isGradeEmpty(String grade) {
		if (grade == null || grade.equals("")) {
			return true;
		}
		return false;
	}

	private boolean isGradeTooLong(String grade) {
		if (grade.length() > 3) {
			return true;
		}
		return false;
	}

	private boolean isGradeValueValid(int grade) {
		if (grade >= 0 && grade <= 100) {
			return true;
		}
		return false;
	}

	private boolean isCommentLengthValid(String comment) {
		if (comment.length() <= 150) {
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/UploadSolutionServlet", method = RequestMethod.GET)
	protected String uploadSolutionPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (!user.isTeacher()) {
			return "currHomeworkPageStudent";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/UploadSolutionServlet", method = RequestMethod.POST)
	protected String uploadSolution(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file1,
			HttpServletResponse response) throws ServletException, IOException {
		int taskNum = Integer.parseInt(request.getParameter("taskNum")) - 1;
		Homework homework = (Homework) request.getSession().getAttribute("currHomework");
		HomeworkDetails homeworkDetails = homework.getHomeworkDetails();
		User user = (User) request.getSession().getAttribute("user");
		if (!user.isTeacher()) {

			String savePath = SAVE_DIR;

			// creates the save directory if it does not exists
			File fileSaveDir = new File(savePath);
			if (!fileSaveDir.exists()) {
				fileSaveDir.mkdir();
			}
			String fileName = " ";
			MultipartFile file = file1;
			if (!isFileEmptyUploadSolution(file)) {
				if (!isSizeValidUploadSolution(file)) {
					request.getSession().setAttribute("wrongSize", true);
				} else {
					if (!isContentTypeValidUploadSolution(file)) {
						request.getSession().setAttribute("wrongContentType", true);
					} else {
						fileName = "hwId" + homeworkDetails.getId() + "userId" + user.getId() + "taskNum" + taskNum
								+ ".java";
						file.transferTo(new File(savePath + File.separator + fileName));
						try {
							UserDAO.getInstance().setSolutionOfTask(homeworkDetails, (Student) user, taskNum, fileName,
									LocalDateTime.now());
							homework.getTasks().get(taskNum).setSolution(fileName);
							homework.getTasks().get(taskNum).setUploadedOn(LocalDateTime.now());
							request.getSession().setAttribute("invalidFields", true);
						} catch (UserException e) {
							File f = new File(savePath + File.separator + fileName);
							if (f.exists()) {
								f.delete();
							}
							System.out.println(e.getMessage());
							e.printStackTrace();
							return "exception";
						}
					}
				}
			}
			request.getSession().setAttribute("currTaskUpload", taskNum);
			return "redirect:/GetHomeworkPageServlet";
		} else {
			return "forbiddenPage";
		}
	}

	private boolean isContentTypeValidUploadSolution(MultipartFile file) {
		String contentType = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
		if (!(contentType.equals(".java"))) {
			return false;
		}
		return true;
	}

	private boolean isFileEmptyUploadSolution(MultipartFile file) {
		if (file.getSize() != 0) {
			return false;
		}
		return true;
	}

	private boolean isSizeValidUploadSolution(MultipartFile file) {
		long sizeInMb = file.getSize() / (1024 * 1024);
		if (sizeInMb >= 1) {
			return false;
		}
		return true;
	}
}
