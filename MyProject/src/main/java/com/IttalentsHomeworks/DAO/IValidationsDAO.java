package com.IttalentsHomeworks.DAO;

import java.security.NoSuchAlgorithmException;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.model.HomeworkDetails;

public interface IValidationsDAO {


	static final int ASCII_TABLE_VALUE_OF_NINE = 57;
	static final int ASCII_TABLE_VALUE_OF_ZERO = 48;
	static final int ASCII_TABLE_QUOTES = 34;
	static final int MAX_SIZE_OF_INTEGER = 10;
	static final int MIN_SIZE_OF_INTEGER = 0;
	 static final int FORBIDDEN_STATUS = 403;
	 static final int PAGE_NOT_FOUND_STATUS = 404;
	 static final int INTERNAL_SERVER_ERROR_STATUS = 500;
	 static final int SUCCESS_STATUS = 200;
	 static final int GROUP_NAME_VALID_CHARS_ASCII_TABLE_TO = 126;
	 static final int GROUP_NAME_VALID_CHARS_ASCII_TABLE_FROM = 32;
	 static final int MAX_SIZE_OF_GROUP_NAME = 15;
	 static final int MIN_SIZE_OF_GROUP_NAME = 5;
	 static final int MAX_SIZE_IN_MB_FOR_TASK_SOLUTION = 1;
	 static final int MAX_LENGTH_OF_COMMENT = 250;
	 static final int MAX_VALUE_OF_GRADE = 100;
	 static final int MIN_VALUE_OF_GRADE = 0;
	 static final int MAX_LENGTH_OF_GRADE = 3;
	 static final int MINUS_ONE_DAY = 1;
	 static final int MAX_NUMBER_OF_CHARACTERS_SOLUTION_TASK_1_MB = 1048576;
	 static final int MIN_NUMBER_OF_CHARACTERS_SOLUTION_TASK = 0;
	 static final int BAD_REQUEST_STATUS = 400;
	 static final int MAX_SIZE_IN_MB_FOR_HOMEWORK_ASSIGNMENT = 20;
	 static final int MAX_NUMBER_OF_TASKS_FOR_HOMEWORK = 40;
	 static final int MIN_NUMBER_OF_TASKS_FOR_HOMEWORK = 1;
	 static final int MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK = 6;
	 static final int HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_TO = 126;
	 static final int HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_FROM = 32;
	 static final int HOMEWORK_HEADING_MAX_LENGTH = 40;
	 static final int HOMEWORK_HEADING_MIN_LENGTH = 5;
	 static final String EMAIL_VALIDATION = "^(.+)@(.+)$";
	 static final int MAX_LENGTH_OF_PASSWORD = 15;
	 static final int MIN_LENGTH_OF_PASSWORD = 6;
	 static final int ASCII_TABLE_VALUE_OF_z = 122;
	 static final int ASCII_TABLE_VALUE_OF_a = 97;
	 static final int ASCII_TABLE_VALUE_OF_DOT = 46;
	 static final int ASCII_TABLE_VALUE_OF_Z = 90;
	 static final int ASCII_TABLE_VALUE_OF_A = 65;
	 static final int MAX_LENGTH_USERNAME = 25;
	 static final int MIN_LENGTH_USERNAME = 6;
	 static final int topMostRecentlyClosedHomeworksForTeacher = 10;

	static final String SAVE_DIR = "/Users/Stela/Desktop/imagesIttalentsHomework";

	boolean isUsernameUnique(String username) throws UserException;

	boolean isPasswordValid(String password);

	boolean isUsernameValid(String username);

	boolean isEmailValid(String email);

	boolean isRepeatedPasswordValid(String pass, String repeatedPass);

	boolean createUserAreThereEmptyFields(String username, String password, String repeatedPassword, String email);

	boolean isGroupNameUnique(String groupName) throws GroupException;

	boolean isGroupNameValid(String groupName);
	
	boolean isGroupNameLengthValid(String groupName);
	
	boolean areGroupNameCharactersValid(String groupName);
	
	public boolean addGroupAreThereEmptyFields(String name);

	boolean updateUserAreThereEmptyFields(String password, String repeatedPassword, String email);
	
	boolean isThereGroupEmptyFieldUpdate(String groupName);
	
	boolean isGroupNameUniqueUpdate(int groupId, String groupName) throws GroupException;
			
	public boolean isGradeValueValid(int grade);
	
	public boolean isCommentLengthValid(String comment);
	
	public boolean doesStudentExist(String username) throws UserException;
	
	public boolean isStudentAlreadyInGroupAddStudent(int groupId, int userId) throws GroupException, UserException;
	
	public boolean isThereEmptyFieldAddStudentToGroup(String username);
	
	public boolean isThereEmptyFieldAddHomework(String heading, String opens, String closes, int numberOfTasksString);
	
	public boolean isLengthHeadingValidAddHomework(String heading);
	
	public boolean areCharactersHeadingValidAddHomework(String heading);
	
	public boolean isHomeworkHeadingUniqueAddHomework(String heading) throws GroupException;
	
	public boolean isHomeworkOpeningTimeValidAddHomework(String opens);
	
	public boolean isHomeworkClosingTimeValidAddHomework(String opens, String closes);
	
	public boolean isHomeworkNumberOfTasksValidAddHomework(int numberOfTasks);

	public boolean updateGroupAreThereEmptyFields(String heading, String string, String string2, String tasksFile);
	
	public boolean isHomeworkUpdateLengthValid(String heading);
	
	public boolean areHomeworkUpdateCharactersValid(String heading);
	
	public boolean isHomeworkUpdateHeadingUnique(String heading, HomeworkDetails currHd) throws GroupException;
	
	public boolean isHomeworkUpdateOpeningTimeValid(String opens, HomeworkDetails currHd);
	
	public boolean isHomeworkUpdateClosingTimeValid(String opens, String closes, HomeworkDetails currHd);
	
	public boolean isHomeworkUpdateNumberOfTasksValid(int numberOfTasks);

	boolean isHomeworkHeadingUnique(String heading) throws GroupException;
		
	boolean doesUserExistInDB(String username, String password) throws UserException, NoSuchAlgorithmException;

	boolean doesUserExistInDBByUsername(String username) throws UserException;
	
	public String encryptPass(String pass) throws NoSuchAlgorithmException;

	boolean isPasswordUpdateValid(String password, String formerPass);

	boolean isStringValidInteger(String string);

	boolean doesUserExistInDBById(int studentId) throws UserException;

	boolean doesGroupExistInDBById(int groupId) throws GroupException;

	boolean isStudentAlreadyInGroup(int userId, int groupId) throws UserException, GroupException;

	boolean doHomeworkDetailsExist(int chosenHomeworkId) throws HomeworkException;;

}
