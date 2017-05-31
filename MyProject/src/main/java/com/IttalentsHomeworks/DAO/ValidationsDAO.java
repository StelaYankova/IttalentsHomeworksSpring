package com.IttalentsHomeworks.DAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.IttalentsHomeworks.DB.DBManager;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.model.HomeworkDetails;

public class ValidationsDAO implements IValidationsDAO {

	private static final int MD5_PART_4 = 1;
	private static final int MD5_PART_3 = 16;
	private static final int MD5_PART_2 = 0x100;
	private static final int MD5_PART_1 = 0xff;
	private static final String PASSWORD_MD5 = "MD5";
	private static IValidationsDAO instance;
	private DBManager manager;
	private static final String DOES_USER_HAVE_GROUP = "SELECT count(*) FROM IttalentsHomeworks.User_has_Group WHERE user_id=? AND group_id=?;";
	private static final String IS_USERNAME_UNIQUE = "SELECT * FROM IttalentsHomeworks.Users WHERE BINARY username = ?;";
	private static final String IS_GROUP_NAME_UNIQUE = "SELECT id FROM IttalentsHomeworks.Groups WHERE BINARY group_name = ?";
	private static final String IS_HOMEWORK_HEADING_UNIQUE = "SELECT * FROM IttalentsHomeworks.Homework WHERE BINARY heading = ?";
	private static final String DOES_USER_EXIST = "SELECT * FROM IttalentsHomeworks.Users WHERE BINARY username = ? AND BINARY pass = ?;";
	private static final String DOES_USER_EXIST_BY_USERNAME = "SELECT * FROM IttalentsHomeworks.Users WHERE BINARY username = ?";

	private ValidationsDAO() {
		setManager(DBManager.getInstance());
	}

	public static IValidationsDAO getInstance() {
		if (instance == null)
			instance = (IValidationsDAO) new ValidationsDAO();
		return instance;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#getManager()
	 */
	@Override
	public DBManager getManager() {
		return manager;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#setManager(com.IttalentsHomeworks.DB.DBManager)
	 */
	@Override
	public void setManager(DBManager manager) {
		this.manager = manager;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isUsernameUnique(java.lang.String)
	 */
	@Override
	public boolean isUsernameUnique(String username) throws UserException {
		boolean isUsernameUnique = true;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(IS_USERNAME_UNIQUE);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				isUsernameUnique = false;
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with checking if username is unique");
		}
		return isUsernameUnique;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isPasswordValid(java.lang.String)
	 */
	@Override
	public boolean isPasswordValid(String pass) {
		boolean isPasswordValid = true;
		if (pass.length() >= IValidationsDAO.MIN_LENGTH_OF_PASSWORD
				&& pass.length() <= IValidationsDAO.MAX_LENGTH_OF_PASSWORD) {
			for (int i = 0; i < pass.length(); i++) {
				if (!(((int) pass.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
						&& (int) pass.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
						|| ((int) pass.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A
								&& (int) pass.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
						|| ((int) pass.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a
								&& (int) pass.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z))) {
					isPasswordValid = false;
					break;
				}
			}
		} else {
			isPasswordValid = false;
		}
		return isPasswordValid;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isUsernameValid(java.lang.String)
	 */
	@Override
	public boolean isUsernameValid(String username) {
		boolean isUsernameValid = true;
		if (username.length() >= IValidationsDAO.MIN_LENGTH_USERNAME
				&& username.length() <= IValidationsDAO.MAX_LENGTH_USERNAME) {
			for (int i = 0; i < username.length(); i++) {
				if (!(((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
						&& (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
						|| ((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A
								&& (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
						|| ((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a
								&& (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z)
						|| (int) username.charAt(i) == IValidationsDAO.ASCII_TABLE_VALUE_OF_DOT)) {
					isUsernameValid = false;
					break;
				}
			}
		}

		return isUsernameValid;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isEmailValid(java.lang.String)
	 */
	@Override
	public boolean isEmailValid(String email) {

		String regex = IValidationsDAO.EMAIL_VALIDATION;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher((CharSequence) email);
		return matcher.matches();
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isRepeatedPasswordValid(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isRepeatedPasswordValid(String pass, String repeatedPass) {
		if (pass.equals(repeatedPass)) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#createUserAreThereEmptyFields(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean createUserAreThereEmptyFields(String username, String password, String repeatedPassword,
			String email) {
		if (username != null && !(username.trim().equals("")) && password != null && !(password.trim().equals(""))
				&& repeatedPassword != null && !(repeatedPassword.trim().equals("")) && email != null
				&& !(email.trim().equals(""))) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isGroupNameUnique(java.lang.String)
	 */
	@Override
	public boolean isGroupNameUnique(String groupName) throws GroupException {
		Connection con = manager.getConnection();
		boolean isGroupNameUnique = true;
		try {
			PreparedStatement ps = con.prepareStatement(IS_GROUP_NAME_UNIQUE);
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

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isGroupNameValid(java.lang.String)
	 */
	@Override
	public boolean isGroupNameValid(String groupName) {
		if (ValidationsDAO.getInstance().isGroupNameLengthValid(groupName)
				&& ValidationsDAO.getInstance().areGroupNameCharactersValid(groupName)) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isGroupNameLengthValid(java.lang.String)
	 */
	@Override
	public boolean isGroupNameLengthValid(String groupName) {
		if (groupName.length() >= IValidationsDAO.MIN_SIZE_OF_GROUP_NAME
				&& groupName.length() <= IValidationsDAO.MAX_SIZE_OF_GROUP_NAME) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#areGroupNameCharactersValid(java.lang.String)
	 */
	@Override
	public boolean areGroupNameCharactersValid(String groupName) {
		for (int i = 0; i < groupName.length(); i++) {
			if (!(((int) groupName.charAt(i) >= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_FROM
					&& (int) groupName.charAt(i) <= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_TO))
					|| (int) groupName.charAt(i) == IValidationsDAO.ASCII_TABLE_QUOTES) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#addGroupAreThereEmptyFields(java.lang.String)
	 */
	@Override
	public boolean addGroupAreThereEmptyFields(String name) {
		if (name != null && !(name.trim().equals(""))) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#updateUserAreThereEmptyFields(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean updateUserAreThereEmptyFields(String password, String repeatedPassword, String email) {
		if (password != null && !(password.trim().equals("")) && repeatedPassword != null
				&& !(repeatedPassword.trim().equals("")) && email != null && !(email.trim().equals(""))) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isGroupNameUniqueUpdate(int, java.lang.String)
	 */
	@Override
	public boolean isGroupNameUniqueUpdate(int groupId, String groupName) throws GroupException {
		int wantedGroupNameId = GroupDAO.getInstance().getGroupIdByGroupName(groupName);
		if (ValidationsDAO.getInstance().isGroupNameUnique(groupName)) {
			return true;
		} else {
			if (wantedGroupNameId == groupId) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isThereGroupEmptyFieldUpdate(java.lang.String)
	 */
	@Override
	public boolean isThereGroupEmptyFieldUpdate(String groupName) {
		if (groupName != null && !(groupName.trim().equals(""))) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isGradeValueValid(int)
	 */
	@Override
	public boolean isGradeValueValid(int grade) {
		if (grade >= IValidationsDAO.MIN_VALUE_OF_GRADE && grade <= IValidationsDAO.MAX_VALUE_OF_GRADE) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isCommentLengthValid(java.lang.String)
	 */
	@Override
	public boolean isCommentLengthValid(String comment) {
		if (comment.length() <= IValidationsDAO.MAX_LENGTH_OF_COMMENT) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#doesStudentExist(java.lang.String)
	 */
	@Override
	public boolean doesStudentExist(String username) throws UserException {
		if (ValidationsDAO.getInstance().isUsernameUnique(username)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#encryptPass(java.lang.String)
	 */
	@Override
	public String encryptPass(String pass) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(PASSWORD_MD5);
		md.update(pass.getBytes());
		byte byteData[] = md.digest();
		// convert the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & MD5_PART_1) + MD5_PART_2, MD5_PART_3).substring(MD5_PART_4));
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#doesUserExistInDB(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean doesUserExistInDB(String username, String password) throws UserException, NoSuchAlgorithmException {
		Connection con = manager.getConnection();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(DOES_USER_EXIST);
			ps.setString(1, username);
			ps.setString(2, encryptPass(password));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserException("Something went wrong with checking if the user is valid..");
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isStudentAlreadyInGroupAddStudent(int, int)
	 */
	@Override
	public boolean isStudentAlreadyInGroupAddStudent(int groupId, int userId) throws GroupException, UserException {
		if (ValidationsDAO.getInstance().isStudentAlreadyInGroup(groupId, userId)) {
			return true;
		}
		return false;

	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isThereEmptyFieldAddStudentToGroup(java.lang.String)
	 */
	@Override
	public boolean isThereEmptyFieldAddStudentToGroup(String username) {
		if (username != null && !(username.trim().equals(""))) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isThereEmptyFieldAddHomework(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public boolean isThereEmptyFieldAddHomework(String heading, String opens, String closes, int numberOfTasksString) {
		if (heading != null && !(heading.trim().equals("")) && opens != null && !(opens.trim().equals(""))
				&& closes != null && !(closes.trim().equals(""))) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isLengthHeadingValidAddHomework(java.lang.String)
	 */
	@Override
	public boolean isLengthHeadingValidAddHomework(String heading) {
		if (heading.length() >= IValidationsDAO.HOMEWORK_HEADING_MIN_LENGTH
				&& heading.length() <= IValidationsDAO.HOMEWORK_HEADING_MAX_LENGTH) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#areCharactersHeadingValidAddHomework(java.lang.String)
	 */
	@Override
	public boolean areCharactersHeadingValidAddHomework(String heading) {
		for (int i = 0; i < heading.length(); i++) {
			if (!(((int) heading.charAt(i) >= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_FROM
					&& (int) heading.charAt(i) <= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_TO))
					|| (int) heading.charAt(i) == IValidationsDAO.ASCII_TABLE_QUOTES) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isHomeworkHeadingUniqueAddHomework(java.lang.String)
	 */
	@Override
	public boolean isHomeworkHeadingUniqueAddHomework(String heading) throws GroupException {
		if (heading != null && (!heading.trim().equals(""))) {
			if (ValidationsDAO.getInstance().isHomeworkHeadingUnique(heading)) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isHomeworkOpeningTimeValidAddHomework(java.lang.String)
	 */
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
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isHomeworkClosingTimeValidAddHomework(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isHomeworkClosingTimeValidAddHomework(String opens, String closes) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
			LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
			LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
			long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);

			if (closingDateTime.isAfter(LocalDateTime.now()) && closingDateTime.isAfter(openingDateTime)
					&& diffInMonths < IValidationsDAO.MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK) {
				return true;
			} else {
				return false;
			}
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isHomeworkNumberOfTasksValidAddHomework(int)
	 */
	@Override
	public boolean isHomeworkNumberOfTasksValidAddHomework(int numberOfTasks) {
		if (numberOfTasks >= IValidationsDAO.MIN_NUMBER_OF_TASKS_FOR_HOMEWORK
				&& numberOfTasks <= IValidationsDAO.MAX_NUMBER_OF_TASKS_FOR_HOMEWORK) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#updateGroupAreThereEmptyFields(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean updateGroupAreThereEmptyFields(String heading, String opens, String closes, String tasksFile) {
		if (heading != null && !(heading.trim().equals("")) && opens != null && !(opens.trim().equals(""))
				&& closes != null && !(closes.trim().equals("")) && tasksFile != null && !(tasksFile.equals(""))) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isHomeworkUpdateLengthValid(java.lang.String)
	 */
	@Override
	public boolean isHomeworkUpdateLengthValid(String heading) {
		if (heading.length() >= IValidationsDAO.HOMEWORK_HEADING_MIN_LENGTH
				&& heading.length() <= IValidationsDAO.HOMEWORK_HEADING_MAX_LENGTH) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#areHomeworkUpdateCharactersValid(java.lang.String)
	 */
	@Override
	public boolean areHomeworkUpdateCharactersValid(String heading) {
		for (int i = 0; i < heading.length(); i++) {
			if (!(((int) heading.charAt(i) >= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_FROM
					&& (int) heading.charAt(i) <= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_TO))
					|| (int) heading.charAt(i) == IValidationsDAO.ASCII_TABLE_QUOTES) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isHomeworkUpdateHeadingUnique(java.lang.String, com.IttalentsHomeworks.model.HomeworkDetails)
	 */
	@Override
	public boolean isHomeworkUpdateHeadingUnique(String heading, HomeworkDetails currHd) throws GroupException {
		if (heading != null && (!heading.equals(""))) {
			if (currHd.getHeading().equals(heading) || ValidationsDAO.getInstance().isHomeworkHeadingUnique(heading)) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isHomeworkUpdateOpeningTimeValid(java.lang.String, com.IttalentsHomeworks.model.HomeworkDetails)
	 */
	@Override
	public boolean isHomeworkUpdateOpeningTimeValid(String opens, HomeworkDetails currHd) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

			LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
			LocalDate openingDate = openingTime.toLocalDate();
			if (openingTime.equals(currHd.getOpeningTime())) {
				return true;
			} else {
				if (openingDate.isAfter(LocalDate.now().minusDays(IValidationsDAO.MINUS_ONE_DAY))
						&& openingDate.isBefore(LocalDate.now()
								.plusMonths(IValidationsDAO.MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK)
								.minusDays(IValidationsDAO.MINUS_ONE_DAY))) {
					return true;
				} else {
					return false;
				}
			}
		} catch (NumberFormatException e) {
			return false;

		}
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isHomeworkUpdateClosingTimeValid(java.lang.String, java.lang.String, com.IttalentsHomeworks.model.HomeworkDetails)
	 */
	@Override
	public boolean isHomeworkUpdateClosingTimeValid(String opens, String closes, HomeworkDetails currHd) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
			LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
			LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
			long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);
			if (closingDateTime.equals(currHd.getClosingTime())) {
				return true;
			} else {
				if (closingDateTime.isAfter(LocalDateTime.now()) && closingDateTime.isAfter(openingDateTime)
						&& diffInMonths < IValidationsDAO.MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK) {
					return true;
				} else {
					return false;
				}
			}
		} catch (NumberFormatException e) {
			return false;

		}
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isHomeworkUpdateNumberOfTasksValid(int)
	 */
	@Override
	public boolean isHomeworkUpdateNumberOfTasksValid(int numberOfTasks) {
		if (numberOfTasks >= IValidationsDAO.MIN_NUMBER_OF_TASKS_FOR_HOMEWORK
				&& numberOfTasks <= IValidationsDAO.MAX_NUMBER_OF_TASKS_FOR_HOMEWORK) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isHomeworkHeadingUnique(java.lang.String)
	 */
	@Override
	public boolean isHomeworkHeadingUnique(String heading) throws GroupException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(IS_HOMEWORK_HEADING_UNIQUE);
			ps.setString(1, heading);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return false;
			}
		} catch (SQLException e) {
			throw new GroupException(
					"Something went wrong with checking if group heading is unique.." + e.getMessage());
		}
		return true;

	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#doesUserExistInDBByUsername(java.lang.String)
	 */
	@Override
	public boolean doesUserExistInDBByUsername(String username) throws UserException {
		Connection con = manager.getConnection();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(DOES_USER_EXIST_BY_USERNAME);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with checking if the user is valid..");
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isPasswordUpdateValid(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isPasswordUpdateValid(String pass, String formerPass) {
		boolean isPasswordValid = true;
		if (!pass.equals(formerPass)) {
			if (pass.length() >= IValidationsDAO.MIN_LENGTH_OF_PASSWORD
					&& pass.length() <= IValidationsDAO.MAX_LENGTH_OF_PASSWORD) {
				for (int i = 0; i < pass.length(); i++) {
					if (!(((int) pass.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
							&& (int) pass.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
							|| ((int) pass.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A
									&& (int) pass.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
							|| ((int) pass.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a
									&& (int) pass.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z))) {
						isPasswordValid = false;
						break;
					}
				}
			} else {
				isPasswordValid = false;
			}
		}
		return isPasswordValid;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isStringValidInteger(java.lang.String)
	 */
	@Override
	public boolean isStringValidInteger(String string) {
		if (string.length() > IValidationsDAO.MIN_SIZE_OF_INTEGER
				&& string.length() < IValidationsDAO.MAX_SIZE_OF_INTEGER) {
			for (int i = 0; i < string.length(); i++) {
				if ((int) string.charAt(i) < IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
						|| (int) string.charAt(i) > IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#doesUserExistInDBById(int)
	 */
	@Override
	public boolean doesUserExistInDBById(int studentId) throws UserException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT username FROM IttalentsHomeworks.Users WHERE id = ?;");
			ps.setInt(1, studentId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with getting username of user..");
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#doesGroupExistInDBById(int)
	 */
	@Override
	public boolean doesGroupExistInDBById(int groupId) throws GroupException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con
					.prepareStatement("SELECT group_name FROM IttalentsHomeworks.Groups WHERE id = ?;");
			ps.setInt(1, groupId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting name of group..");
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#isStudentAlreadyInGroup(int, int)
	 */
	@Override
	public boolean isStudentAlreadyInGroup(int userId, int groupId) throws UserException, GroupException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(DOES_USER_HAVE_GROUP);
			ps.setInt(1, userId);
			ps.setInt(2, groupId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) != 0) {
					return true;
				}
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with checking if user has group..");
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IValidationsDAO#doHomeworkDetailsExist(int)
	 */
	@Override
	public boolean doHomeworkDetailsExist(int chosenHomeworkId) throws HomeworkException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con
					.prepareStatement("SELECT COUNT(*) FROM IttalentsHomeworks.Homework WHERE id = ?;");
			ps.setInt(1, chosenHomeworkId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) != 0) {
					return true;
				}
			}
		} catch (SQLException e) {
			throw new HomeworkException("Something went wrong with checking if homework details exist..");
		}
		return false;
	}

}
