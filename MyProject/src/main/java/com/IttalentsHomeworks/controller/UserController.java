package com.IttalentsHomeworks.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Stack;
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

	@RequestMapping(value = "/GetGroupsOfUserServlet", method = RequestMethod.GET)
	protected void getGroupsOfStudent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("TARAM1");
		User userTry = (User) request.getSession().getAttribute("user");
		if (!userTry.isTeacher()) {		System.out.println("TARAM2");

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
				System.out.println("TARAM3");

				jsonGroups.add(obj);
			}
			response.setContentType("application/json");
			response.getWriter().write(jsonGroups.toString());
			response.setStatus(IValidationsDAO.SUCCESS_STATUS);
		} else {
			System.out.println("TARAM4");

			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/GetHomeworkOfStudentServlet", method = RequestMethod.GET)
	protected String getHomeworksOfStudent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			int studentId = Integer.parseInt(request.getParameter("studentId"));
			int homeworkId = Integer.parseInt(request.getParameter("id"));
			request.getSession().setAttribute("studentId", studentId);
			Homework homework = null;
			ArrayList<Homework> homeworks;
			try {
				homeworks = UserDAO.getInstance().getHomeworksOfStudent(studentId);
				for (Homework h : homeworks) {
					if (h.getHomeworkDetails().getId() == homeworkId) {
						homework = new Homework(h.getTeacherGrade(), h.getTeacherComment(), h.getTasks(),
								h.getHomeworkDetails());
						break;
					}
				}
				
				
				request.getSession().setAttribute("currHomework", homework);
				Student chosenStudent = (Student) UserDAO.getInstance().getUserById(studentId);
				request.getSession().setAttribute("currStudentUsername", chosenStudent.getUsername());
				return "redirect:./GetCurrHomeworkOfStudent";
			} catch (UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			} catch (GroupException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/GetHomeworkPageServlet", method = RequestMethod.GET)
	protected String getHomeworkPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (!user.isTeacher()) {
			return "currHomeworkPageStudent";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/GetMainPageStudent", method = RequestMethod.GET)
	protected String getMainPageStudent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (!user.isTeacher()) {
			return "mainPageStudent";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/GetMainPageTeacher", method = RequestMethod.GET)
	protected String getMainPageTeacher(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "mainPageTeacher";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/GetStudentsScoresServlet", method = RequestMethod.GET)
	protected String getStudentsScored(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "seeStudentsScores";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	protected String loginGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return "homePage";
	}

	@RequestMapping(value = "/LoginServlet", method = RequestMethod.GET)
	protected String loginServletGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return "homePage";
	}
	@RequestMapping(value = "/LoginServlet", method = RequestMethod.POST)
	protected String loginPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = null;
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();
		Stack<String> navPath = new Stack<>();
		navPath.push("Home");
		request.getSession().setAttribute("navPath", navPath);
		if (isThereEmptyFieldLogin(username, password)) {
			request.getSession().setAttribute("invalidField", true);
		} else {
			try {
				if (doesUserLoginExist(username, password)) {
					user = UserDAO.getInstance().getUserByUsername(username);
					request.getSession().setAttribute("user", user);
					ArrayList<Group> allGroups;
					try {
						allGroups = GroupDAO.getInstance().getAllGroups();
						if(request.getServletContext().getAttribute("allGroups") == null){
							request.getServletContext().setAttribute("allGroups", allGroups);
						}
						ArrayList<Teacher> allTeachers = UserDAO.getInstance().getAllTeachers();
						for (Teacher t : allTeachers) {
							t.setGroups(UserDAO.getInstance().getGroupsOfUser(t.getId()));
						}
						if(request.getServletContext().getAttribute("allTeachers") == null){
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
						if(request.getServletContext().getAttribute("allStudents") == null){
							request.getServletContext().setAttribute("allStudents", allStudents);
						}
						return "redirect:./GetMainPageTeacher";
					} else {
						request.getSession().setAttribute("isTeacher", false);
						return "redirect:./GetMainPageStudent";
					}
				} else {
					request.getSession().setAttribute("invalidField", true);
					request.getSession().setAttribute("usernameTry", username);
					request.getSession().setAttribute("passwordTry", password);
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

	private boolean doesUserLoginExist(String username, String password) throws UserException, NoSuchAlgorithmException {
		return ValidationsDAO.getInstance().doesUserExistInDB(username, password);
	}

	private boolean isThereEmptyFieldLogin(String username, String password) {
		if (username == null || username == "" || password == null || password == "") {
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/LogoutServlet", method = RequestMethod.GET)
	protected String logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*Connection con = DBManager.getInstance().getConnection();
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return "exception";
		}*/
		request.getSession().invalidate();
		return "redirect:./index";
	}

	@RequestMapping(value = "/RegisterServlet", method = RequestMethod.GET)
	protected String goToRegister(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "registerPage";
	}

	@RequestMapping(value = "/RegisterServlet", method = RequestMethod.POST)
	protected String register(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();
		String repeatedPassword = request.getParameter("repeatedPassword").trim();
		String email = request.getParameter("email").trim();
		User userTry = new Student(username, password, repeatedPassword, email);
		request.setAttribute("userTry", userTry);
		// not null
		if (isThereEmptyFieldRegister(username, password, repeatedPassword, email)) {
			request.setAttribute("emptyFields", true);
		} else {
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
		if (username.length() >= IValidationsDAO.MIN_LENGTH_USERNAME && username.length() <= IValidationsDAO.MAX_LENGTH_USERNAME) {
			return true;
		}
		return false;
	}

	private boolean isUsernameUniqueRegister(String username) throws UserException {
		return ValidationsDAO.getInstance().isUsernameUnique(username);
	}

	private boolean areCharactersValidUsernameRegister(String username) {
		for (int i = 0; i < username.length(); i++) {
			if (!(((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO && (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
					|| ((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A && (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
					|| ((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a && (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z)) || ((int) username.charAt(i) == IValidationsDAO.ASCII_TABLE_VALUE_OF_DOT)) {
				return false;
			}
		}
		return true;
	}

	private boolean isLengthValidPassRegister(String password) {
		if (password.length() >= IValidationsDAO.MIN_LENGTH_OF_PASSWORD && password.length() <= IValidationsDAO.MAX_LENGTH_OF_PASSWORD) {
			return true;
		}
		return false;
	}

	private boolean areCharactersValidPassRegister(String password) {
		for (int i = 0; i < password.length(); i++) {
			if (!(((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO && (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
					|| ((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A && (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
					|| ((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a && (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z))) {
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
		if (username == null || username.equals("") || password == null || password.equals("")
				|| repeatedPassword == null || repeatedPassword.equals("") || email == null || email.equals("")) {
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/SeeScoresServlet", method = RequestMethod.GET)
	protected String seeScoresServlet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		request.getSession().setAttribute("throughtScores", 1);
		if (!user.isTeacher()) {
			return "yourScores";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/UpdateYourProfileServlet", method = RequestMethod.GET)
	protected String updateYourProfilePage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return "updateProfile";
	}

	@RequestMapping(value = "/UpdateYourProfileServlet", method = RequestMethod.POST)
	protected String updateYourProfile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getId();
		String username = user.getUsername();
		String password = request.getParameter("password").trim();
		String repeatedPassword = request.getParameter("repeatedPassword").trim();
		String email = request.getParameter("email").trim();
		User userTry = new Student(username, password, repeatedPassword, email);
		request.setAttribute("userTry", userTry);
		User newUser = null;

		// empty fields
		if (isThereEmptyFieldUpdateProfile(password, repeatedPassword, email)) {
			request.setAttribute("emptyFields", true);
		} else {
			// password valid
			boolean isPassValid = false;
			if(!password.equals(user.getPassword())){
				if (isPasswordValidUpdateProfile(password)) {
					isPassValid = true;
				}
			} else {
				isPassValid = true;
				request.setAttribute("invalidFields", false);
			}System.out.println(3);
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
			if (isPassValid == true && isRepeatedPassValid == true && isEmailValid == true) {System.out.println(5);
				
				if (user.isTeacher()) {
					newUser = new Teacher(username, password, repeatedPassword, email);
				} else {
					newUser = new Student(username, password, repeatedPassword, email);
				}
				newUser.setId(userId);
				try {
					UserDAO.getInstance().updateUser(newUser, user.getPassword());
					request.setAttribute("invalidFields", false);
					if(!password.equals(user.getPassword())){
						String encryptedPassword = ValidationsDAO.getInstance().encryptPass(password);
						newUser.setPassword(encryptedPassword);
					}
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
		if (password.length() >= IValidationsDAO.MIN_LENGTH_OF_PASSWORD && password.length() <= IValidationsDAO.MAX_LENGTH_OF_PASSWORD) {
			return true;
		}
		return false;
	}

	private boolean areCharactersValidPassUpdateProfile(String password) {
		for (int i = 0; i < password.length(); i++) {
			if (!(((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO && (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
					|| ((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A && (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
					|| ((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a && (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z))) {
				return false;
			}
		}
		return true;
	}

	private boolean isEmailValidUpdateProfile(String email) {
		String regex = "^(.+)@(.+)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher((CharSequence) email);
		return matcher.matches();
	}

	private boolean isThereEmptyFieldUpdateProfile(String password, String repeatedPassword, String email) {
		if (password == null || password.equals("") || repeatedPassword == null || repeatedPassword.equals("")
				|| email == null || email.equals("")) {
			return true;
		}
		return false;
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
