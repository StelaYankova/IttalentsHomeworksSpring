package com.IttalentsHomeworks.DAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import com.IttalentsHomeworks.DB.DBManager;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.Exceptions.ValidationException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.Homework;
import com.IttalentsHomeworks.model.HomeworkDetails;
import com.IttalentsHomeworks.model.Student;
import com.IttalentsHomeworks.model.Teacher;

public interface IGroupDAO {

	DBManager getManager();

	void setManager(DBManager manager);

	ArrayList<Teacher> getTeachersOfGroup(int groupId) throws GroupException;

	ArrayList<Student> getStudentsOfGroup(int groupId) throws GroupException, UserException;

	ArrayList<HomeworkDetails> getHomeworkDetailsOfGroup(int groupId) throws GroupException;

	boolean isUserAlreadyInGroup(int groupId, String username) throws GroupException, UserException;

	void addUserToGroup(int chosenGroupId, int idUser) throws GroupException, UserException, ValidationException;

	void createNewGroup(Group g) throws GroupException, ValidationException;

	ArrayList<HomeworkDetails> getAllHomeworksDetails() throws GroupException;

	ArrayList<Group> getAllGroups() throws UserException, GroupException;

	void removeUserFromGroup(int groupId, int i) throws GroupException, UserException;

	void removeGroup(int groupId) throws GroupException;

	void createHomeworkDetails(HomeworkDetails hd, ArrayList<Integer> groupsForHw) throws GroupException, UserException, ValidationException, NotUniqueHomeworkHeadingException;

	int getHomeworkDetailsId(String heading) throws GroupException;

	void updateHomeworkDetails(HomeworkDetails hd, ArrayList<Integer> groupsForHw) throws GroupException, UserException, ValidationException, NotUniqueHomeworkHeadingException;

	void removeHomeworkFromGroup(int homeworkDetailsId, int groupId) throws GroupException, UserException;

	void addHomeworkToGroup(HomeworkDetails hd, int groupId) throws GroupException, UserException;

	Group getGroupById(int id) throws GroupException, UserException;

	int getGroupIdByGroupName(String groupName) throws GroupException;

	void removeHomeworkDetails(HomeworkDetails hd) throws GroupException, UserException;

	void updateGroup(Group group, ArrayList<Integer> wishedTeacherIds) throws GroupException, ValidationException, UserException;

	ArrayList<Integer> getIdsOfGroupsForWhichIsHomework(int homeworkDetailsId) throws GroupException;

	HomeworkDetails getHomeworkDetailsById(int chosenHomeworkId) throws GroupException;

	void addHomeworkToGroupTransaction(HomeworkDetails homeworkDetails, int groupId) throws GroupException, UserException, SQLException;

	void updateNumberOfTasksForStudents(HomeworkDetails homeworkDetails, int numOfTasks) throws GroupException;

	ArrayList<Integer> getStudentsWithSearchedHomework(int homeworkDetailsId) throws GroupException;

	Group getGroupWithoutStudentsById(int groupId) throws GroupException;

	String getGroupNameById(int chosenGroupId) throws GroupException;

	ArrayList<Integer> getStudentsIdsOfGroup(int groupId) throws GroupException;

	ArrayList<Group> getAllGroupsWithoutStudents() throws GroupException;

	boolean doesPassSystemTest(String solutionOfStudent, Homework homework, int taskNum) throws IOException, InterruptedException;

}