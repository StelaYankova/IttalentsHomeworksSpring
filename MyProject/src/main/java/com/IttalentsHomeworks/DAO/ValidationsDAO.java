package com.IttalentsHomeworks.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Part;

import com.IttalentsHomeworks.DB.DBManager;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.HomeworkDetails;

public class ValidationsDAO implements IValidationsDAO{

	private static IValidationsDAO instance;
	private DBManager manager;
	private static final String IS_USERNAME_UNIQUE = "SELECT * FROM IttalentsHomeworks.Users WHERE username = ?;";
	private static final String IS_GROUP_NAME_UNIQUE = "SELECT id FROM IttalentsHomeworks.Groups WHERE group_name = ?";

	private ValidationsDAO() {
		setManager(DBManager.getInstance());
	}

	public static IValidationsDAO getInstance() {
		if (instance == null)
			instance = (IValidationsDAO) new ValidationsDAO();
		return instance;
	}

	
	public DBManager getManager() {
		return manager;
	}

	public void setManager(DBManager manager) {
		this.manager = manager;
	}
	
	@Override
	public boolean isUsernameUnique(String username) throws UserException{
		boolean isUsernameUnique = true;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(IS_USERNAME_UNIQUE);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				isUsernameUnique = false;
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with checking if username is unique");
		}
		return isUsernameUnique;
	}
	
	@Override
	public boolean isPasswordValid(String pass){
		boolean isPasswordValid = true;
		if(pass.length() >= 6 && pass.length() <= 15){
			for (int i = 0; i < pass.length(); i++) {
				if (!(((int) pass.charAt(i) >= 48 && (int) pass.charAt(i) <= 57)
						|| ((int) pass.charAt(i) >= 65 && (int) pass.charAt(i) <= 90)
						|| ((int) pass.charAt(i) >= 97 && (int) pass.charAt(i) <= 122))) {
					isPasswordValid = false;
					break;
				}
			}
		}
		return isPasswordValid;
	}
	
	@Override
	public boolean isUsernameValid(String username){
		boolean isUsernameValid = true;
		if(username.length() >= 6 && username.length() <= 15){
			for (int i = 0; i < username.length(); i++) {
				if (!(((int) username.charAt(i) >= 48 && (int) username.charAt(i) <= 57)
						|| ((int) username.charAt(i) >= 65 && (int) username.charAt(i) <= 90)
						|| ((int) username.charAt(i) >= 97 && (int) username.charAt(i) <= 122))) {
					isUsernameValid = false;
					break;
				}
			}
		}
		
		return isUsernameValid;
	}
	
	@Override
	public boolean isEmailValid(String email) {
		
		String regex = "^(.+)@(.+)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher((CharSequence) email);
		return matcher.matches();
	}
	
	@Override
	public boolean isRepeatedPasswordValid(String pass, String repeatedPass){
		if(pass.equals(repeatedPass)){
			return true;
		}
		return false;
	}

	@Override
	public boolean createUserAreThereEmptyFields(String username, String password, String repeatedPassword,
			String email) {
		if(username == null || username == "" ||password == null || password == "" ||repeatedPassword == null || repeatedPassword == "" ||email == null || email == ""){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isGroupNameUnique(String groupName) throws GroupException {
		Connection con = manager.getConnection();
		boolean isGroupNameUnique = true;
		try {
			PreparedStatement ps = con
					.prepareStatement(IS_GROUP_NAME_UNIQUE);
			ps.setString(1, groupName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				isGroupNameUnique = false;
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking if group's name is unique..");
		}
		return isGroupNameUnique;
	}
	
	@Override
	public boolean isGroupNameValid(String groupName) {
		if(ValidationsDAO.getInstance().isGroupNameLengthValid(groupName) && ValidationsDAO.getInstance().areGroupNameCharactersValid(groupName)){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isGroupNameLengthValid(String groupName) {
		if (groupName.length() >= 5 && groupName.length() <= 20) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean areGroupNameCharactersValid(String groupName) {
		for(int i = 0; i < groupName.length(); i++){
			if(!(((int)groupName.charAt(i) >= 32 && (int)groupName.charAt(i) <= 126))){
				return false;
			}
		}
		return true;
	}
	@Override
	public boolean addGroupAreThereEmptyFields(String name) {
		if(name == null || name == ""){
			return true;
		}
		return false;
	}

	@Override
	public boolean updateUserAreThereEmptyFields(String password, String repeatedPassword, String email) {
		if(password == null || password == "" ||repeatedPassword == null || repeatedPassword == "" ||email == null || email == ""){
			return true;
		}
		return false;	
	}
	
	@Override
	public boolean isGroupNameUniqueUpdate(int groupId, String groupName) {
		try {
			int wantedGroupNameId = GroupDAO.getInstance().getGroupIdByGroupName(groupName);

			if (ValidationsDAO.getInstance().isGroupNameUnique(groupName)) {
				return true;
			} else {
				if (wantedGroupNameId == groupId) {
					return true;
				}
			}

		} catch (GroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean isThereGroupEmptyFieldUpdate(String groupName) {
		if (groupName == null || groupName == "") {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isGradeTooLog(int grade){
		if(grade > 100){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isGradeValueValid(int grade){
		System.out.println("Grade is " + grade);
		if(grade >= 0 && grade <= 100){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isCommentLengthValid(String comment){
		if(comment.length() <= 150){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean doesStudentExist(String username) {
		try {
			if (ValidationsDAO.getInstance().isUsernameUnique(username)) {
				return false;
			}
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean isStudentAlreadyInGroupAddStudent(int groupId, String username) {
		Group chosenGroup;
		try {
			chosenGroup = GroupDAO.getInstance().getGroupById(groupId);
			com.IttalentsHomeworks.model.User chosenStudent = UserDAO.getInstance().getUserByUsername(username);
			if (GroupDAO.getInstance().isUserAlreadyInGroup(chosenGroup, chosenStudent)) {
				return true;
			}
		} catch (GroupException | UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean isThereEmptyFieldAddStudentToGroup(String username) {
		if ((username.equals("")) || username == null) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isThereEmptyFieldAddHomework(String heading, String opens, String closes, int numberOfTasksString) {
		if (heading == null || heading.equals("") || opens == null || opens.equals("") || closes == null
				|| closes.equals("")) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isLengthHeadingValidAddHomework(String heading) {
		if (heading.length() >= 5 && heading.length() <= 40) {
			return true;
		}
		return false;
	}

	@Override
	public boolean areCharactersHeadingValidAddHomework(String heading) {
		for(int i = 0; i < heading.length(); i++){
			if(!(((int)heading.charAt(i) >= 32 && (int)heading.charAt(i) <= 126))){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean isHomeworkHeadingUniqueAddHomework(String heading) {
		if (heading != null && (!heading.equals(""))) {
			if (GroupDAO.getInstance().isHomeworkHeadingUnique(heading)) {
				return true;
			}
		}else{//if its null it is unique --> it will be catched in the next try (for empty fields) and the file will be removed from the system
			return true;
		}
		return false;
	}

	@Override
	public boolean isHomeworkOpeningTimeValidAddHomework(String opens) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

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
	
	@Override
	public boolean isHomeworkClosingTimeValidAddHomework(String opens, String closes){
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
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
	
	@Override
	public boolean isHomeworkNumberOfTasksValidAddHomework(int numberOfTasks){
		if(numberOfTasks >= 1 && numberOfTasks <= 40){
			return true;
		}
		return false;
	}

	@Override
	public boolean updateGroupAreThereEmptyFields(String heading, String opens, String closes, String tasksFile) {
		if (heading == null || heading.equals("") || opens == null || opens.equals("") || closes == null
				|| closes.equals("") || tasksFile == null || tasksFile.equals("")) {
			
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isHomeworkUpdateLengthValid(String heading) {
		if (heading.length() >= 5 && heading.length() <= 40) {
			return true;
		}
		return false;
	}

	@Override
	public boolean areHomeworkUpdateCharactersValid(String heading) {
		for(int i = 0; i < heading.length(); i++){
			if(!(((int)heading.charAt(i) >= 32 && (int)heading.charAt(i) <= 126))){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean isHomeworkUpdateHeadingUnique(String heading, HomeworkDetails currHd) {
		if (heading != null && (!heading.equals(""))) {
			if (currHd.getHeading().equals(heading) || GroupDAO.getInstance().isHomeworkHeadingUnique(heading)) {
				return true;
			}
		}else{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isHomeworkUpdateOpeningTimeValid(String opens, HomeworkDetails currHd){
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

			LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
			LocalDate openingDate = openingTime.toLocalDate();
			if(openingTime.equals(currHd.getOpeningTime())){
				return true;
			}else{
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
	
	@Override
	public boolean isHomeworkUpdateClosingTimeValid(String opens, String closes, HomeworkDetails currHd){
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
			LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
			LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
			long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);
			if (closingDateTime.equals(currHd.getClosingTime())) {
				return true;
			}else{
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
	
	@Override
	public boolean isHomeworkUpdateNumberOfTasksValid(int numberOfTasks){
		if(numberOfTasks >= 1 && numberOfTasks <= 40){
			return true;
		}
		return false;
	}

}
