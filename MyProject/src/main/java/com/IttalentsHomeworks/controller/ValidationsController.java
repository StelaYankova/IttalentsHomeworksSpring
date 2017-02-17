package com.IttalentsHomeworks.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.IttalentsHomeworks.DAO.GroupDAO;
import com.IttalentsHomeworks.DAO.ValidationsDAO;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.HomeworkDetails;
import com.IttalentsHomeworks.model.User;

@Controller
public class ValidationsController {

	@RequestMapping(value="/DoesUserExist",method = RequestMethod.GET)
	protected void checkIfUserAlreadyExists(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String chosenStudentUsername = request.getParameter("chosenStudentUsername").trim();
		try {
			if(ValidationsDAO.getInstance().isUsernameUnique(chosenStudentUsername)){//if its unique id is not in DB
				return;
			}else{
				response.setStatus(200);
			}
		} catch (UserException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatus(500);
		}
	}
	
	@RequestMapping(value="/IsChosenStudentAlreadyInGroup",method = RequestMethod.GET)
	protected void isChosenStudentAlreadyInGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			int chosenGroupId = Integer.parseInt(request.getParameter("chosenGroupId"));
			String chosenStudentUsername = request.getParameter("chosenStudentUsername").trim();
			try {
				Group chosenGroup = GroupDAO.getInstance().getGroupById(chosenGroupId);
				if (GroupDAO.getInstance().isUserAlreadyInGroup(chosenGroup, chosenStudentUsername)) {
					response.setStatus(400);
				} else {
					response.setStatus(200);
				}
			} catch (GroupException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(500);
			} catch (UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(500);
			} 
		} else {
			response.setStatus(403);
		}
	}
	
	@RequestMapping(value="/IsGroupNameUnique",method = RequestMethod.GET)
	protected void isGroupNameUnique(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if(user.isTeacher()){
		String groupName = request.getParameter("name").trim();
		try {
			if(ValidationsDAO.getInstance().isGroupNameUnique(groupName)){
				response.setStatus(200);
			}else{
				response.setStatus(400);
			}
		} catch (GroupException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatus(500);	
		}
		}else{
			response.setStatus(403);
		}
	}
	
	@RequestMapping(value="/IsGroupNameUniqueUpdate",method = RequestMethod.GET)
	protected void isGroupNameUniqueUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if(user.isTeacher()){
		String groupName = request.getParameter("name").trim();
		Group currGroup = (Group) request.getSession().getAttribute("currGroup");
		int currGroupId = currGroup.getId();
		try {
			int wantedGroupNameId = GroupDAO.getInstance().getGroupIdByGroupName(groupName);

			if(ValidationsDAO.getInstance().isGroupNameUnique(groupName)){
				response.setStatus(200);
			}else{
				if(wantedGroupNameId == currGroupId){
					response.setStatus(200);
				}else{
					response.setStatus(400);
				}
			}
		} catch (GroupException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatus(500);	
		}}else{
			response.setStatus(403);
		}
	}
	
	
	@RequestMapping(value="/IsGroupNameValid",method = RequestMethod.GET)
	protected void isGroupNameValid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if(user.isTeacher()){
		String groupName = request.getParameter("name").trim();
		if(isLengthGroupNameValid(groupName) && areCharactersGroupNameValid(groupName)){
			response.setStatus(200);
		}else{
			response.setStatus(400);
		}
		}else{
			response.setStatus(403);
		}
	}
	private boolean isLengthGroupNameValid(String groupName) {
		if (groupName.length() >= 5 && groupName.length() <= 20) {
			return true;
		}
		return false;
	}

	private boolean areCharactersGroupNameValid(String groupName) {
		for(int i = 0; i < groupName.length(); i++){
			if(!(((int)groupName.charAt(i) >= 32 && (int)groupName.charAt(i) <= 126))){
				return false;
			}
		}
		return true;
	}
	
	@RequestMapping(value="/IsHomeworkClosingTimeValid",method = RequestMethod.GET)
	protected void isHomeworkClosingTimeValid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if(user.isTeacher()){
		String opens = request.getParameter("opens").replace("/", "-");
		String closes = request.getParameter("closes").replace("/", "-");
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
			LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
			long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);
			if (closingDateTime.isAfter(LocalDateTime.now()) && closingDateTime.isAfter(openingDateTime)
					&& diffInMonths < 6) {
				response.setStatus(200);
			} else {
				response.setStatus(400);
			}
		} catch (NumberFormatException e) {
			response.setStatus(400);
		}}else{
			response.setStatus(403);
		}
	}
	
	@RequestMapping(value = "/IsHomeworkHeadingUnique", method = RequestMethod.GET)
	protected void isHomeworkHeadingValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String heading = request.getParameter("heading").trim();
			try {
				if (ValidationsDAO.getInstance().isHomeworkHeadingUnique(heading)) {
					response.setStatus(200);
				} else {
					response.setStatus(400);
				}
			} catch (GroupException e) {
				response.setStatus(500);
			}
		} else {
			response.setStatus(403);
		}
	}
	
	@RequestMapping(value="/IsHomeworkHeadingValid",method = RequestMethod.GET)
	protected void isHomeworkHeadingUnique(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if(user.isTeacher()){
		String heading = request.getParameter("heading").trim();
		if(isLengthHomeworkHeadingValid(heading) && areCharactersHomeworkHeadingValid(heading)){
			response.setStatus(200);
		}else{
			response.setStatus(400);
		}}else{
			response.setStatus(403);
		}
	}
	private boolean isLengthHomeworkHeadingValid(String heading) {
		if (heading.length() >= 5 && heading.length() <= 40) {
			return true;
		}
		return false;
	}

	private boolean areCharactersHomeworkHeadingValid(String heading) {
		for(int i = 0; i < heading.length(); i++){
			if(!(((int)heading.charAt(i) >= 32 && (int)heading.charAt(i) <= 126))){
				return false;
			}
		}
		return true;
	}
	
	@RequestMapping(value="/IsHomeworkOpeningTimeValid",method = RequestMethod.GET)
	protected void isHomeworkOpeningTimeValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if(user.isTeacher()){
		String opens = request.getParameter("opens").replace("/", "-");
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

			LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
			LocalDate openingDate = openingTime.toLocalDate();
			if (openingDate.isAfter(LocalDate.now().minusDays(1))
					&& openingDate.isBefore(LocalDate.now().plusMonths(6).minusDays(1))) {
				response.setStatus(200);
			} else {
				response.setStatus(400);
			}
		} catch (NumberFormatException e) {
			response.setStatus(400);
		}}else{
			response.setStatus(403);
		}
	}
	
	@RequestMapping(value="/IsHomeworkUpdateClosingTimeValid",method = RequestMethod.GET)
	protected void isHomeworkUpdateClosingTimeValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String opens = request.getParameter("opens").trim().replace("/", "-");
			String closes = request.getParameter("closes").trim().replace("/", "-");
			HomeworkDetails currHd = (HomeworkDetails) request.getSession().getAttribute("currHomework");
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
				LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
				long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);
				if (closingDateTime.equals(currHd.getClosingTime())) {
					response.setStatus(200);
				} else {
					if (closingDateTime.isAfter(LocalDateTime.now()) && closingDateTime.isAfter(openingDateTime)
							&& diffInMonths < 6) {
						response.setStatus(200);
					} else {
						response.setStatus(400);
					}
				}
			} catch (NumberFormatException e) {
				response.setStatus(400);
			}
		}else{
			response.setStatus(403);
		}
	}

	@RequestMapping(value = "/IsHomeworkUpdateHeadingIsRepeated", method = RequestMethod.GET)
	protected void isHomeworkUpdateHeadingIsRepeated(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String heading = request.getParameter("heading").trim();
			HomeworkDetails currHd = (HomeworkDetails) request.getSession().getAttribute("currHomework");
			try {
				if (currHd.getHeading().equals(heading) || ValidationsDAO.getInstance().isHomeworkHeadingUnique(heading)) {
					response.setStatus(200);
				} else {
					response.setStatus(400);
				}
			} catch (GroupException e) {
				response.setStatus(500);
			}
		} else {
			response.setStatus(403);
		}
	}
	@RequestMapping(value="/IsHomeworkUpdateHeadingValid",method = RequestMethod.GET)
	protected void isHomeworkUpdateHeadingValid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String heading = request.getParameter("heading").trim();
			if (isLengthHomeworkUpdateHeadingValid(heading) && areCharactersHomeworkUpdateHeadingValid(heading)) {
				response.setStatus(200);
			} else {
				response.setStatus(400);
			}
		} else {
			response.setStatus(403);
		}
	}

	private boolean isLengthHomeworkUpdateHeadingValid(String heading) {
		if (heading.length() >= 5 && heading.length() <= 40) {
			return true;
		}
		return false;
	}

	private boolean areCharactersHomeworkUpdateHeadingValid(String heading) {
		for(int i = 0; i < heading.length(); i++){
			if(!(((int)heading.charAt(i) >= 32 && (int)heading.charAt(i) <= 126))){
				return false;
			}
		}
		return true;
	}
	
	
	@RequestMapping(value="/IsHomeworkUpdateOpeningTimeValid",method = RequestMethod.GET)
	protected void isHomeworkUpdateOpeningTimeValid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			String opens = request.getParameter("opens").replace("/", "-");
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			HomeworkDetails currHd = (HomeworkDetails) request.getSession().getAttribute("currHomework");
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
				LocalDate openingDate = openingTime.toLocalDate();
				if (openingTime.equals(currHd.getOpeningTime())) {
					response.setStatus(200);
				} else {
					if (openingDate.isAfter(LocalDate.now().minusDays(1))
							&& openingDate.isBefore(LocalDate.now().plusMonths(6).minusDays(1))) {
						response.setStatus(200);
					} else {
						response.setStatus(400);
					}
				}
			} catch (NumberFormatException e) {
				response.setStatus(400);
			}
		} else {
			response.setStatus(403);
		}
	}
	
	@RequestMapping(value="/IsPasswordValid",method = RequestMethod.GET)
	protected void isPasswordValid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String password = request.getParameter("password").trim();
		if(isLengthPasswordValid(password) && areCharactersPasswordValid(password)){
			response.setStatus(200);
		}else{
			response.setStatus(400);
		}
	}

	private boolean isLengthPasswordValid(String password) {
		if (password.length() >= 6 && password.length() <= 15) {
			return true;
		}
		return false;
	}

	private boolean areCharactersPasswordValid(String password) {
		for(int i = 0; i < password.length(); i++){
			if(!(((int)password.charAt(i) >= 48 && (int)password.charAt(i) <= 57) || ((int)password.charAt(i) >= 65 && (int)password.charAt(i) <= 90) || ((int)password.charAt(i) >= 97 && (int)password.charAt(i) <= 122))){
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = "/IsUsernameUniqueServlet", method = RequestMethod.GET)
	protected void isUsernameUnique(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username").trim();
		boolean isUnique = false;
		try {
			isUnique = ValidationsDAO.getInstance().isUsernameUnique(username);
		} catch (UserException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatus(500);
		}
		if (isUnique) {
			response.setStatus(200);
		} else {
			response.setStatus(400);
		}
	}
	
	@RequestMapping(value="/IsUsernameValid",method = RequestMethod.GET)
	protected void isUsernameValid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username").trim();
		if(isLengthUsernameValid(username) && areCharactersUsernameValid(username)){
			response.setStatus(200);
		}else{
			response.setStatus(400);
		}
	}

	private boolean isLengthUsernameValid(String username) {
		if (username.length() >= 6 && username.length() <= 15) {
			return true;
		}
		return false;
	}

	private boolean areCharactersUsernameValid(String username) {
		for(int i = 0; i < username.length(); i++){
			if(!(((int)username.charAt(i) >= 48 && (int)username.charAt(i) <= 57) || ((int)username.charAt(i) >= 65 && (int)username.charAt(i) <= 90) || ((int)username.charAt(i) >= 97 && (int)username.charAt(i) <= 122))){
				return false;
			}
		}
		return true;
	}
	
	@RequestMapping(value="/ValidateLogin",method = RequestMethod.GET)
	protected void validateLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();
		boolean areUsernamePasswordValid;
		try {
			areUsernamePasswordValid = ValidationsDAO.getInstance().doesUserExistInDB(username, password);
			if(areUsernamePasswordValid){
				response.setStatus(200);
			}else{
				response.setStatus(400);
			}
		} catch (UserException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatus(500);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatus(500);
		}	
	}

}
