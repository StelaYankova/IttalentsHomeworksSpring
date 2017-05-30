package com.IttalentsHomeworks.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.IttalentsHomeworks.DAO.GroupDAO;
import com.IttalentsHomeworks.DAO.IValidationsDAO;
import com.IttalentsHomeworks.DAO.UserDAO;
import com.IttalentsHomeworks.DAO.ValidationsDAO;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.Exceptions.ValidationException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.Homework;
import com.IttalentsHomeworks.model.HomeworkDetails;
import com.IttalentsHomeworks.model.Student;
import com.IttalentsHomeworks.model.Teacher;
import com.IttalentsHomeworks.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class UserController {

	@RequestMapping(value = "/getGroupsOfStudentByStudent", method = RequestMethod.GET)
	protected void getGroupsOfStudent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User userTry = (User) request.getSession().getAttribute("user");
		if (!userTry.isTeacher()) {
			User user = (User) request.getSession().getAttribute("user");
			ArrayList<Group> groupsOfUser = user.getGroups();
			JsonArray jsonGroups = new JsonArray();
			for (Group g : groupsOfUser) {
				JsonObject obj = new JsonObject();
				obj.addProperty("name", g.getName());
				obj.addProperty("id", g.getId());
				obj.add("homeworks", null);
				JsonArray homeworks = new JsonArray();
				for (HomeworkDetails h : g.getHomeworks()) {
					JsonObject obj1 = new JsonObject();
					obj1.addProperty("heading", h.getHeading());
					obj1.addProperty("id", h.getId());
					long days = h.getClosingTime().until(h.getOpeningTime(), ChronoUnit.DAYS);
					obj1.addProperty("timeLeft", days);
					homeworks.add(obj1);
				}
				obj.add("homeworks", homeworks);
				jsonGroups.add(obj);
			}
			response.setContentType("application/json");
			response.getWriter().write(jsonGroups.toString());
			response.setStatus(IValidationsDAO.SUCCESS_STATUS);
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/getHomeworkOfStudentByTeacher", method = RequestMethod.GET)
	protected String getHomeworksOfStudent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getParameter("studentId") != null && !(request.getParameter("studentId").trim().equals(""))
					&& ValidationsDAO.getInstance().isStringValidInteger(request.getParameter("studentId").trim())
					&& request.getParameter("homeworkId") != null
					&& !(request.getParameter("homeworkId").trim().equals(""))
					&& ValidationsDAO.getInstance().isStringValidInteger(request.getParameter("homeworkId").trim())) {
				int studentId = Integer.parseInt(request.getParameter("studentId").trim());
				int homeworkId = Integer.parseInt(request.getParameter("homeworkId").trim());
				request.getSession().setAttribute("studentId", studentId);
				Homework homework = null;
				try {
					Student chosenStudent = (Student) UserDAO.getInstance().getUserById(studentId);
					homework = UserDAO.getInstance().getHomeworkOfStudent(studentId, homeworkId);
					if (homework == null || chosenStudent == null) {
						return "pageNotFound";
					}
					int numberOfTasks = homework.getHomeworkDetails().getNumberOfTasks();
					int pointsPerTask = 100 / numberOfTasks;
					request.getSession().setAttribute("pointsPerTask", pointsPerTask);
					request.getSession().setAttribute("currHomework", homework);
					request.getSession().setAttribute("currStudentUsername", chosenStudent.getUsername());
					return "redirect:./seeChosenHomeworkPageOfStudentByTeacher";
				} catch (UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (GroupException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}
			} else {
				return "pageNotFound";
			}
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/mainPageStudent", method = RequestMethod.GET)
	protected String mainPageStudent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (!user.isTeacher()) {
			try {
				getActiveHomeworksOfStudent((Student) user, request);
			} catch (UserException e) {
				return "exception";
			}
			return "mainPageStudent";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/mainPageTeacher", method = RequestMethod.GET)
	protected String getMainPageTeacher(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			try {
				getRecentClosedHomeworksByGroupsOfTeacher((Teacher) user, request);
			} catch (GroupException e) {
				return "exception";
			}
			return "mainPageTeacher";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/studentsScoresByTeacher", method = RequestMethod.GET)
	protected String getStudentsScored(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		request.getSession().removeAttribute("throughtSeeOrUpdateHomeworks");
		if (user.isTeacher()) {
			return "seeStudentsScores";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	protected String indexGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return "homePage";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	protected String loginPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = null;
		String username = (request.getParameter("username") != null) ? (request.getParameter("username").trim()) : ("");
		String password = (request.getParameter("password") != null) ? (request.getParameter("password").trim()) : ("");
		request.getSession().setAttribute("usernameTry", username);
		request.getSession().setAttribute("passwordTry", password);
		if (isThereEmptyFieldLogin(request.getParameter("username").trim(), request.getParameter("password").trim())) {
			request.getSession().setAttribute("invalidField", true);
		} else {
			try {
				if (doesUserLoginExist(username, password)) {
					user = UserDAO.getInstance().getUserByUsername(username);
					request.getSession().setAttribute("user", user);
					ArrayList<Group> allGroups;
					try {
						allGroups = GroupDAO.getInstance().getAllGroupsWithoutStudents();
						if (request.getServletContext().getAttribute("allGroups") == null) {
							request.getServletContext().setAttribute("allGroups", allGroups);
						}
						ArrayList<Teacher> allTeachers = UserDAO.getInstance().getAllTeachers();
						for (Teacher t : allTeachers) {
							t.setGroups(UserDAO.getInstance().getGroupsOfUserWithoutStudents(t.getId()));
						}
						if (request.getServletContext().getAttribute("allTeachers") == null) {
							request.getServletContext().setAttribute("allTeachers", allTeachers);
						}
					} catch (UserException | GroupException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						return "exception";
					}
					if (user.isTeacher()) {
						request.getSession().setAttribute("isTeacher", true);
						ArrayList<Student> allStudents = UserDAO.getInstance().getAllStudents();
						if (request.getServletContext().getAttribute("allStudents") == null) {
							request.getServletContext().setAttribute("allStudents", allStudents);
						}
						return "redirect:./mainPageTeacher";
					} else {
						request.getSession().setAttribute("isTeacher", false);
						return "redirect:./mainPageStudent";
					}
				} else {
					request.getSession().setAttribute("invalidField", true);
				}
			} catch (UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			} catch (GroupException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			} catch (NoSuchAlgorithmException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			}
		}
		return "redirect:./index";
	}

	private void getActiveHomeworksOfStudent(Student user, HttpServletRequest request) throws UserException {
		ArrayList<HomeworkDetails> activeHomeworksOfStudent = UserDAO.getInstance()
				.getActiveHomeworksOfStudent(user.getId());
		request.getSession().setAttribute("activeHomeworksOfStudent", activeHomeworksOfStudent);
	}

	private void getRecentClosedHomeworksByGroupsOfTeacher(Teacher user, HttpServletRequest request)
			throws GroupException {
		// we check if hd is in wanted time range
		ArrayList<HomeworkDetails> mostRecentlyClosedHomeworksForTeacher = new ArrayList<>();
		HashMap<Group, HashSet<HomeworkDetails>> mostRecentlyClosedHomeworksForTeacherMap = new HashMap<>();
		for (Group g : user.getGroups()) {
			for (HomeworkDetails hd : g.getHomeworks()) {
				if (hd.getOpeningTime().isBefore(LocalDateTime.now())
						&& hd.getClosingTime().isBefore(LocalDateTime.now())) {
					mostRecentlyClosedHomeworksForTeacher.add(hd);
				}
			}
		}
		// we sort them by closing time
		mostRecentlyClosedHomeworksForTeacher.sort(new Comparator<HomeworkDetails>() {
			@Override
			public int compare(HomeworkDetails o1, HomeworkDetails o2) {
				return o2.getClosingTime().compareTo(o1.getClosingTime());
			}
		});
		// we get most recently closed hd
		ArrayList<HomeworkDetails> topMostRecentlyClosedHomeworksForTeacher = new ArrayList<>();
		for (int i = 0; i < IValidationsDAO.topMostRecentlyClosedHomeworksForTeacher; i++) {
			if (mostRecentlyClosedHomeworksForTeacher.size() > i) {
				topMostRecentlyClosedHomeworksForTeacher.add(mostRecentlyClosedHomeworksForTeacher.get(i));
			}
		}

		// we get wanted groups of top 10
		for (Group g : user.getGroups()) {
			for (HomeworkDetails hd : topMostRecentlyClosedHomeworksForTeacher) {
				ArrayList<Integer> currGroupIds = GroupDAO.getInstance().getIdsOfGroupsForWhichIsHomework(hd.getId());
				if (currGroupIds.contains(g.getId())) {
					if (!mostRecentlyClosedHomeworksForTeacherMap.containsKey(g)) {
						mostRecentlyClosedHomeworksForTeacherMap.put(g, new HashSet<>());
					}
					mostRecentlyClosedHomeworksForTeacherMap.get(g).add(hd);
				}
			}
		}
		request.getSession().setAttribute("mostRecentlyClosedHomeworks", mostRecentlyClosedHomeworksForTeacherMap);

	}

	private boolean doesUserLoginExist(String username, String password)
			throws UserException, NoSuchAlgorithmException {
		return ValidationsDAO.getInstance().doesUserExistInDB(username, password);
	}

	private boolean isThereEmptyFieldLogin(String username, String password) {
		if (username != null && !(username.equals("")) && password != null && !(password.equals(""))) {
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	protected String logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().invalidate();
		return "redirect:./index";
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	protected String goToRegister(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "registerPage";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	protected String register(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = (request.getParameter("username") != null) ? (request.getParameter("username").trim()) : ("");
		String password = (request.getParameter("password") != null) ? (request.getParameter("password").trim()) : ("");
		String repeatedPassword = (request.getParameter("repeatedPassword") != null)
				? (request.getParameter("repeatedPassword").trim()) : ("");
		String email = (request.getParameter("email") != null) ? (request.getParameter("email").trim()) : ("");
		User userTry = new Student(username, password, repeatedPassword, email);
		request.setAttribute("userTry", userTry);
		// not null
		if (isThereEmptyFieldRegister(request.getParameter("username").trim(), request.getParameter("password").trim(),
				request.getParameter("repeatedPassword").trim(), request.getParameter("email").trim())) {
			request.setAttribute("emptyFields", true);
		} else {
			request.setAttribute("userTry", userTry);
			// uniqueUsername
			try {
				boolean isUsernameUnique = false;
				if (isUsernameUniqueRegister(username)) {
					isUsernameUnique = true;
				}
				request.setAttribute("uniqueUsername", isUsernameUnique);

				// validUsername
				boolean isUsernameValid = false;
				if (isLengthValidUsernameRegister(username) && areCharactersValidUsernameRegister(username)) {
					isUsernameValid = true;
				}
				request.setAttribute("validUsername", isUsernameValid);

				// validPass
				boolean isPassValid = false;
				if (isLengthValidPassRegister(password) && areCharactersValidPassRegister(password)) {
					isPassValid = true;
				}
				request.setAttribute("validPass", isPassValid);

				// validRepeatedPass
				boolean isRepeatedPassValid = false;
				if (arePassAndRepeatedPassEqualRegister(password, repeatedPassword)) {
					isRepeatedPassValid = true;
				}
				request.setAttribute("validRepeatedPass", isRepeatedPassValid);

				// validEmail
				boolean isEmailValid = false;
				if (isEmailValidRegister(email)) {
					isEmailValid = true;
				}
				request.setAttribute("validEmail", isEmailValid);
				if (isUsernameUnique == true && isUsernameValid == true && isPassValid == true
						&& isRepeatedPassValid == true && isEmailValid == true) {
					// we create user
					User user = new Student(username, password, repeatedPassword, email);
					UserDAO.getInstance().createNewUser(user);
					request.setAttribute("invalidFields", false);
				}
			} catch (UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			} catch (ValidationException e) {
				request.setAttribute("invalidFields", true);
			} catch (NoSuchAlgorithmException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			}
		}
		return "registerPage";
	}

	private boolean isLengthValidUsernameRegister(String username) {
		if (username.length() >= IValidationsDAO.MIN_LENGTH_USERNAME
				&& username.length() <= IValidationsDAO.MAX_LENGTH_USERNAME) {
			return true;
		}
		return false;
	}

	private boolean isUsernameUniqueRegister(String username) throws UserException {
		return ValidationsDAO.getInstance().isUsernameUnique(username);
	}

	private boolean areCharactersValidUsernameRegister(String username) {
		for (int i = 0; i < username.length(); i++) {
			if (!(((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
					&& (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
					|| ((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A
							&& (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
					|| ((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a
							&& (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z)
					|| (int) username.charAt(i) == IValidationsDAO.ASCII_TABLE_VALUE_OF_DOT)) {
				return false;
			}
		}
		return true;
	}

	private boolean isLengthValidPassRegister(String password) {
		if (password.length() >= IValidationsDAO.MIN_LENGTH_OF_PASSWORD
				&& password.length() <= IValidationsDAO.MAX_LENGTH_OF_PASSWORD) {
			return true;
		}
		return false;
	}

	private boolean areCharactersValidPassRegister(String password) {
		for (int i = 0; i < password.length(); i++) {
			if (!(((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
					&& (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
					|| ((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A
							&& (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
					|| ((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a
							&& (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z))) {
				return false;
			}
		}
		return true;
	}

	private boolean isEmailValidRegister(String email) {
		String regex = IValidationsDAO.EMAIL_VALIDATION;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher((CharSequence) email);
		return matcher.matches();
	}

	private boolean arePassAndRepeatedPassEqualRegister(String pass, String repeatedPass) {
		if (pass.equals(repeatedPass)) {
			return true;
		}
		return false;
	}

	private boolean isThereEmptyFieldRegister(String username, String password, String repeatedPassword, String email) {
		if (username != null && !(username.equals("")) && password != null && !(password.equals(""))
				&& repeatedPassword != null && !(repeatedPassword.equals("")) && email != null && !(email.equals(""))) {
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/studentsScoresByStudent", method = RequestMethod.GET)
	protected String seeScoresServlet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		request.getSession().setAttribute("throughtScores", 1);
		if (!user.isTeacher()) {
			return "seeStudentsScoresByStudent";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.GET)
	protected String updateYourProfilePage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return "updateProfile";
	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	protected String updateYourProfile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getId();

		User newUser = null;

		// empty fields
		if (isThereEmptyFieldUpdateProfile(request.getParameter("password").trim(),
				request.getParameter("repeatedPassword").trim(), request.getParameter("email").trim())) {
			request.setAttribute("emptyFields", true);
		} else {
			// password valid
			String username = user.getUsername();
			String password = request.getParameter("password").trim();
			String repeatedPassword = request.getParameter("repeatedPassword").trim();
			String email = request.getParameter("email").trim();
			User userTry = new Student(username, password, repeatedPassword, email);
			request.setAttribute("userTry", userTry);
			boolean isPassValid = false;
			if (!password.equals(user.getPassword())) {
				if (isPasswordValidUpdateProfile(password)) {
					isPassValid = true;
				}
			} else {
				isPassValid = true;
				request.setAttribute("invalidFields", false);
			}
			request.setAttribute("validPass", isPassValid);
			// repeatedPass
			boolean isRepeatedPassValid = false;
			if (arePassAndRepeatedPassEqualUpdateProfile(password, repeatedPassword)) {
				isRepeatedPassValid = true;
			}
			request.setAttribute("validRepeatedPass", isRepeatedPassValid);
			// email
			boolean isEmailValid = false;
			if (isEmailValidUpdateProfile(email)) {
				isEmailValid = true;
			}
			request.setAttribute("validEmail", isEmailValid);

			if (isPassValid == true && isRepeatedPassValid == true && isEmailValid == true) {
				if (user.isTeacher()) {
					newUser = new Teacher(username, password, repeatedPassword, email);
				} else {
					newUser = new Student(username, password, repeatedPassword, email);
				}
				newUser.setId(userId);
				try {
					UserDAO.getInstance().updateUser(newUser, user.getPassword());
					newUser = UserDAO.getInstance().getUserByUsername(username);
					request.setAttribute("invalidFields", false);
					request.getSession().setAttribute("user", newUser);
				} catch (UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (ValidationException e) {
					request.setAttribute("invalidFields", true);
				} catch (NoSuchAlgorithmException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (GroupException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}
			}
		}
		return "updateProfile";
	}

	private boolean isPasswordValidUpdateProfile(String password) {
		if (isLengthValidPassUpdateProfile(password) && areCharactersValidPassUpdateProfile(password)) {
			return true;
		}
		return false;
	}

	private boolean isLengthValidPassUpdateProfile(String password) {
		if (password.length() >= IValidationsDAO.MIN_LENGTH_OF_PASSWORD
				&& password.length() <= IValidationsDAO.MAX_LENGTH_OF_PASSWORD) {
			return true;
		}
		return false;
	}

	private boolean areCharactersValidPassUpdateProfile(String password) {
		for (int i = 0; i < password.length(); i++) {
			if (!(((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
					&& (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
					|| ((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A
							&& (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
					|| ((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a
							&& (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z))) {
				return false;
			}
		}
		return true;
	}

	private boolean isEmailValidUpdateProfile(String email) {
		String regex = IValidationsDAO.EMAIL_VALIDATION;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher((CharSequence) email);
		return matcher.matches();
	}

	private boolean isThereEmptyFieldUpdateProfile(String password, String repeatedPassword, String email) {
		if (password != null && !(password.equals("")) && repeatedPassword != null && !(repeatedPassword.equals(""))
				&& email != null && !(email.equals(""))) {
			return false;
		}
		return true;
	}

	private boolean arePassAndRepeatedPassEqualUpdateProfile(String pass, String repeatedPass) {
		if (pass.equals(repeatedPass)) {
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/forbiddenPage", method = RequestMethod.GET)
	protected String getForbiddenPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return "forbiddenPage";
	}

	@RequestMapping(value = "/exceptionPage", method = RequestMethod.GET)
	protected String getExceptionPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return "exception";
	}

	@RequestMapping(value = "/pageNotFoundPage", method = RequestMethod.GET)
	protected String pageNotFoundPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return "pageNotFound";
	}
}
