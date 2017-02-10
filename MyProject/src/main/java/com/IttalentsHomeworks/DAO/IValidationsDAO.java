package com.IttalentsHomeworks.DAO;

import java.util.ArrayList;

import javax.servlet.http.Part;

import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.HomeworkDetails;

public interface IValidationsDAO {

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
	
	boolean isGroupNameUniqueUpdate(int groupId, String groupName);
		
	public boolean isGradeTooLog(int grade);
	
	public boolean isGradeValueValid(int grade);
	
	public boolean isCommentLengthValid(String comment);
	
	public boolean doesStudentExist(String username);
	
	public boolean isStudentAlreadyInGroupAddStudent(int groupId, String username);
	
	public boolean isThereEmptyFieldAddStudentToGroup(String username);
	
	public boolean isThereEmptyFieldAddHomework(String heading, String opens, String closes, int numberOfTasksString);
	
	public boolean isLengthHeadingValidAddHomework(String heading);
	
	public boolean areCharactersHeadingValidAddHomework(String heading);
	
	public boolean isHomeworkHeadingUniqueAddHomework(String heading);
	
	public boolean isHomeworkOpeningTimeValidAddHomework(String opens);
	
	public boolean isHomeworkClosingTimeValidAddHomework(String opens, String closes);
	
	public boolean isHomeworkNumberOfTasksValidAddHomework(int numberOfTasks);

	public boolean updateGroupAreThereEmptyFields(String heading, String string, String string2, String tasksFile);
	
	public boolean isHomeworkUpdateLengthValid(String heading);
	
	public boolean areHomeworkUpdateCharactersValid(String heading);
	
	public boolean isHomeworkUpdateHeadingUnique(String heading, HomeworkDetails currHd);
	
	public boolean isHomeworkUpdateOpeningTimeValid(String opens, HomeworkDetails currHd);
	
	public boolean isHomeworkUpdateClosingTimeValid(String opens, String closes, HomeworkDetails currHd);
	
	public boolean isHomeworkUpdateNumberOfTasksValid(int numberOfTasks);
		
}
