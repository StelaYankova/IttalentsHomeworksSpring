package com.IttalentsHomeworks.controller;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.IttalentsHomeworks.DAO.GroupDAO;
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

	@RequestMapping(value="/GetGroupsOfUserServlet",method = RequestMethod.GET)
	protected void getGroupsOfStudent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User userTry = (User) request.getSession().getAttribute("user");
		if(!userTry.isTeacher()){
		User user = (User) request.getSession().getAttribute("user");
		ArrayList<Group> groupsOfUser = user.getGroups();
		JsonArray jsonGroups = new JsonArray();
				for (Group g: groupsOfUser) {
					JsonObject obj = new JsonObject();
					obj.addProperty("name", g.getName());
					obj.addProperty("id", g.getId());
					obj.add("homeworks", null);
					JsonArray homeworks = new JsonArray();
					for(HomeworkDetails h: g.getHomeworks()){
						JsonObject obj1 = new JsonObject();
						obj1.addProperty("heading", h.getHeading());
						obj1.addProperty("id", h.getId());
						
						long days = h.getClosingTime().until( h.getOpeningTime(), ChronoUnit.DAYS);
						obj1.addProperty("timeLeft", days);
						homeworks.add(obj1);
					}
					obj.add("homeworks", homeworks);
					jsonGroups.add(obj);
				}
				response.setContentType("application/json");
				response.getWriter().write(jsonGroups.toString());
			
		}
	}
	
	@RequestMapping(value="/GetHomeworkOfStudentServlet",method = RequestMethod.GET)
	protected String getHomeworksOfStudent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if(user.isTeacher()){
		int studentId = Integer.parseInt(request.getParameter("studentId"));
		int homeworkId = Integer.parseInt(request.getParameter("id"));
		request.getSession().setAttribute("studentId", studentId);
		Homework homework = null;
		ArrayList<Homework> homeworks;
		try {
			homeworks = UserDAO.getInstance().getHomeworksOfStudent(studentId);
			for(Homework h: homeworks){
				if(h.getHomeworkDetails().getId() == homeworkId){
					homework = new Homework(h.getTeacherGrade(), h.getTeacherComment(), h.getTasks(), h.getHomeworkDetails());
					break;
				}
			}
			//request.getSession().setAttribute("currHomework", homework);
			request.getSession().setAttribute("currHomework", homework);
			return "redirect:./GetCurrHomeworkOfStudent";
		//	response.sendRedirect("./GetCurrHomeworkOfStudent");
		} catch (UserException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return "exception";
		}
		//response.sendRedirect("homeworkOfStudent.jsp");
	}
		return "error";
	}
	
	@RequestMapping(value="/GetHomeworkPageServlet",method = RequestMethod.GET)
	protected String getHomeworkPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if(!user.isTeacher()){
			//request.getRequestDispatcher("currHomeworkPageStudent.jsp").forward(request, response);
			return "currHomeworkPageStudent";
		}
		return "error";
	}
	
	@RequestMapping(value="/GetMainPageStudent",method = RequestMethod.GET)
	protected String getMainPageStudent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//request.getRequestDispatcher("mainPageStudent.jsp").forward(request, response);
		User user = (User) request.getSession().getAttribute("user");
		if(!user.isTeacher()){
			return "mainPageStudent";
		}
		return "error";
	}
	
	@RequestMapping(value="/GetMainPageTeacher",method = RequestMethod.GET)
	protected String getMainPageTeacher(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//request.getRequestDispatcher("mainPageTeacher.jsp").forward(request, response);
		User user = (User) request.getSession().getAttribute("user");
		if(user.isTeacher()){
			return "mainPageTeacher";
		}
		return "error";
	}
	
	@RequestMapping(value="/GetStudentsScoresServlet",method = RequestMethod.GET)
	protected String getStudentsScored(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO throw exception
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			//request.getRequestDispatcher("seeStudentsScores.jsp").forward(request, response);
			return "seeStudentsScores";
		}
		return "error";
	}
	
	@RequestMapping(value="/index",method = RequestMethod.GET)
	protected String loginGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//request.getRequestDispatcher("homePage.jsp").forward(request, response);
		return "homePage";
	}
	/*@RequestMapping(value="/**",method = RequestMethod.GET)
	protected String goToDefaultPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//request.getRequestDispatcher("homePage.jsp").forward(request, response);
		return "homePage";
	}*/
	
	@RequestMapping(value="/LoginServlet",method = RequestMethod.POST)
	protected String loginPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = null;
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();
		
		request.getSession().setMaxInactiveInterval(100000);
		if(isThereEmptyFieldLogin(username, password)){
			request.getSession().setAttribute("invalidField", true);
		}
		else{
		try {
			if(doesUserLoginExist(username,password)){
				user = UserDAO.getInstance().getUserByUsername(username);
				request.getSession().setAttribute("user", user);
				ArrayList<Group> allGroups;
				try {
					allGroups = GroupDAO.getInstance().getAllGroups();
					request.getServletContext().setAttribute("allGroups", allGroups);
					ArrayList<Teacher> allTeachers = UserDAO.getInstance().getAllTeachers();
					for(Teacher t : allTeachers){
						t.setGroups(UserDAO.getInstance().getGroupsOfUser(t.getId()));
					}
					request.getServletContext().setAttribute("allTeachers", allTeachers);
					
				} catch (UserException | GroupException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}
				if(user.isTeacher()){
					request.getSession().setAttribute("isTeacher", true);
					
					ArrayList<Student> allStudents = UserDAO.getInstance().getAllStudents();
					request.getServletContext().setAttribute("allStudents", allStudents);
					//response.sendRedirect("./GetMainPageTeacher");
					return "redirect:./GetMainPageTeacher";

				}else{
					request.getSession().setAttribute("isTeacher", false);
					return "redirect:./GetMainPageStudent";

					//response.sendRedirect("./GetMainPageStudent");
				}
				
			}else{
				request.setAttribute("invalidField", true);
				request.setAttribute("usernameTry", username);
				request.setAttribute("passwordTry", password);
			//	response.sendRedirect("./LoginServlet");
			}
		} catch (UserException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return "exception";
		} catch (GroupException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return "exception";
		}
	}
		return "redirect:./LoginServlet";
	}
	private boolean doesUserLoginExist(String username, String password) throws UserException{
			return UserDAO.getInstance().doesUserExistInDB(username, password);
		
	}
	private boolean isThereEmptyFieldLogin(String username, String password){
		if(username == null || username == "" ||password == null || password == ""){
			return true;
		}
		return false;
	}
	
	@RequestMapping(value="/LogoutServlet",method = RequestMethod.GET)
	protected String logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().removeAttribute("user");
		request.getSession().invalidate();
		//response.sendRedirect("./LoginServlet");
		return "redirect:./LoginServlet";
	}
	
	
	
	
	@RequestMapping(value="/RegisterServlet",method = RequestMethod.GET)
	protected String goToRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//req.getRequestDispatcher("registerPage.jsp").forward(req, resp);
		return "registerPage";
	}
	
	@RequestMapping(value="/RegisterServlet",method = RequestMethod.POST)
	protected String register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();
		String repeatedPassword = request.getParameter("repeatedPassword").trim();
		String email = request.getParameter("email").trim();
		User userTry = new Student(username, password, repeatedPassword, email);// by default
		request.setAttribute("userTry", userTry);
		//not null
		if (isThereEmptyFieldRegister(username, password, repeatedPassword, email)) {
			request.setAttribute("emptyFields", true);
		} else {
			// uniqueUsername
			boolean isUsernameUnique = false;
			try {
				if (isUsernameUniqueRegister(username)) {
					isUsernameUnique = true;
				}
			} catch (UserException e1) {
				System.out.println(e1.getMessage());
				e1.printStackTrace();
				return "exception";
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
				User user = new Student(username, password, repeatedPassword, email);// by default
				try {
					UserDAO.getInstance().createNewUser(user);
					request.setAttribute("invalidFields", false);
				} catch (UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (ValidationException e) {
					request.setAttribute("invalidFields", true);
				}

			}
		}
		return "registerPage";
//		request.getRequestDispatcher("registerPage.jsp").forward(request, response);
		//return email;
	}
	private boolean isLengthValidUsernameRegister(String username) {
		if (username.length() >= 6 && username.length() <= 15) {
			return true;
		}
		return false;
	}

	private boolean isUsernameUniqueRegister(String username) throws UserException{
		boolean isUnique = false;
		
			return isUnique = ValidationsDAO.getInstance().isUsernameUnique(username);
		
	}
	private boolean areCharactersValidUsernameRegister(String username) {
		for(int i = 0; i < username.length(); i++){
			if(!(((int)username.charAt(i) >= 48 && (int)username.charAt(i) <= 57) || ((int)username.charAt(i) >= 65 && (int)username.charAt(i) <= 90) || ((int)username.charAt(i) >= 97 && (int)username.charAt(i) <= 122))){
				return false;
			}
		}
		return true;
	}
	private boolean isLengthValidPassRegister(String password) {
		if (password.length() >= 6 && password.length() <= 15) {
			return true;
		}
		return false;
	}

	private boolean areCharactersValidPassRegister(String password) {
		for(int i = 0; i < password.length(); i++){
			if(!(((int)password.charAt(i) >= 48 && (int)password.charAt(i) <= 57) || ((int)password.charAt(i) >= 65 && (int)password.charAt(i) <= 90) || ((int)password.charAt(i) >= 97 && (int)password.charAt(i) <= 122))){
				return false;
			}
		}
		return true;
	}
	
	private boolean isEmailValidRegister(String email){
		 String regex = "^(.+)@(.+)$";
	      Pattern pattern = Pattern.compile(regex);
	      Matcher matcher = pattern.matcher((CharSequence) email);	         
			return matcher.matches();
	}
	
	private boolean arePassAndRepeatedPassEqualRegister(String pass, String repeatedPass){
		if(pass.equals(repeatedPass)){
			return true;
		}
		return false;
	}
	private boolean isThereEmptyFieldRegister(String username, String password, String repeatedPassword, String email){
		if(username == null || username.equals("") ||password == null || password.equals("") ||repeatedPassword == null || repeatedPassword.equals("") ||email == null || email.equals("")){
			return true;
		}
		return false;
	}
	
	@RequestMapping(value="/SeeScoresServlet",method = RequestMethod.GET)
	protected String seeScoresServlet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//TODO throw exception
				User user = (User) request.getSession().getAttribute("user");
				if(!user.isTeacher()){
					//request.getRequestDispatcher("yourScores.jsp").forward(request, response);
					return "yourScores";
				}
				return "error";
	}
	
	@RequestMapping(value="/UpdateYourProfileServlet",method = RequestMethod.GET)
	protected String updateYourProfilePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return "updateProfile";
		//request.getRequestDispatcher("updateProfile.jsp").forward(request, response);
	}
	
	@RequestMapping(value="/UpdateYourProfileServlet",method = RequestMethod.POST)
	protected String updateYourProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getId();
		String username = user.getUsername();
		String password = request.getParameter("password").trim();
		String repeatedPassword = request.getParameter("repeatedPassword").trim();
		String email = request.getParameter("email").trim();
		User userTry = new Student(username, password, repeatedPassword, email);// by default
		request.setAttribute("userTry", userTry);
		User newUser = null;

		//empty fields
		if(isThereEmptyFieldUpdateProfile(password, repeatedPassword, email)){
			request.setAttribute("emptyFields", true);
		}else{
		//password valid
			boolean isPassValid = false;
			if(isPasswordValidUpdateProfile(password)){
				isPassValid = true;
			}
			request.setAttribute("validPass", isPassValid);
		//repeatedPass
			boolean isRepeatedPassValid = false;
			if(arePassAndRepeatedPassEqualUpdateProfile(password, repeatedPassword)){
				isRepeatedPassValid = true;
			}
			request.setAttribute("validRepeatedPass", isRepeatedPassValid);
		//email
			boolean isEmailValid = false;
			if(isEmailValidUpdateProfile(email)){
				isEmailValid = true;
			}
			request.setAttribute("validEmail", isEmailValid);
			
			if(isPassValid==true && isRepeatedPassValid==true && isEmailValid==true){
				if(user.isTeacher()){
					newUser = new Teacher(username, password, repeatedPassword, email);
				}else{
					newUser = new Student(username, password, repeatedPassword, email);
				}
				newUser.setId(userId);

				try {
					UserDAO.getInstance().updateUser(newUser);
					request.setAttribute("invalidFields", false);
					request.getSession().setAttribute("user", newUser);
				} catch (UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (ValidationException e) {
					request.setAttribute("invalidFields", true);

				}
			}
		}
		//request.getRequestDispatcher("updateProfile.jsp").forward(request, response);
		return "updateProfile";
	}
		
	private boolean isPasswordValidUpdateProfile(String password){
		if(isLengthValidPassUpdateProfile(password) && areCharactersValidPassUpdateProfile(password)){
			return true;
		}
		return false;
	}
		private boolean isLengthValidPassUpdateProfile(String password) {
			if (password.length() >= 6 && password.length() <= 15) {
				return true;
			}
			return false;
		}

		private boolean areCharactersValidPassUpdateProfile(String password) {
			for(int i = 0; i < password.length(); i++){
				if(!(((int)password.charAt(i) >= 48 && (int)password.charAt(i) <= 57) || ((int)password.charAt(i) >= 65 && (int)password.charAt(i) <= 90) || ((int)password.charAt(i) >= 97 && (int)password.charAt(i) <= 122))){
					return false;
				}
			}
			return true;
		}
		
		private boolean isEmailValidUpdateProfile(String email){
			 String regex = "^(.+)@(.+)$";
		      Pattern pattern = Pattern.compile(regex);
		      Matcher matcher = pattern.matcher((CharSequence) email);		         
				return matcher.matches();
		}
		private boolean isThereEmptyFieldUpdateProfile(String password, String repeatedPassword, String email){
			if(password == null || password.equals("") ||repeatedPassword == null || repeatedPassword.equals("") ||email == null || email.equals("")){
				return true;
			}
			return false;
		}
		private boolean arePassAndRepeatedPassEqualUpdateProfile(String pass, String repeatedPass){
			if(pass.equals(repeatedPass)){
				return true;
			}
			return false;
		}
}
