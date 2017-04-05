package com.IttalentsHomeworks.DAO;
import java.sql.SQLException;
import java.util.ArrayList;
import com.IttalentsHomeworks.DB.DBManager;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.NotUniqueUsernameException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.Exceptions.ValidationException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.HomeworkDetails;
import com.IttalentsHomeworks.model.Student;
import com.IttalentsHomeworks.model.Teacher;

public interface IGroupDAO {

	DBManager getManager();

	void setManager(DBManager manager);

	ArrayList<Teacher> getTeachersOfGroup(Group g) throws GroupException;

	ArrayList<Student> getStudentsOfGroup(Group g) throws GroupException, UserException;

	ArrayList<HomeworkDetails> getHomeworkDetailsOfGroup(Group g) throws GroupException;

	boolean isUserAlreadyInGroup(Group g, String username) throws GroupException, UserException;

	void addUserToGroup(Group g, int idUser) throws GroupException, UserException, ValidationException;

	void createNewGroup(Group g) throws GroupException, ValidationException;

	ArrayList<HomeworkDetails> getAllHomeworksDetails() throws GroupException;

	ArrayList<Group> getAllGroups() throws UserException, GroupException;

	void removeUserFromGroup(Group group, int i) throws GroupException, UserException;

	void removeGroup(Group g) throws GroupException;

	void createHomeworkDetails(HomeworkDetails hd, ArrayList<Group> groupsForHw) throws GroupException, UserException, ValidationException, NotUniqueUsernameException;

	int getHomeworkDetailsId(HomeworkDetails hd) throws GroupException;

	void updateHomeworkDetails(HomeworkDetails hd, ArrayList<Group> groupsforHw) throws GroupException, UserException, ValidationException, NotUniqueUsernameException;

	void removeHomeworkFromGroup(HomeworkDetails hd, Group g) throws GroupException, UserException;

	void addHomeworkToGroup(HomeworkDetails hd, Group g) throws GroupException, UserException;

	Group getGroupById(int id) throws GroupException, UserException;

	int getGroupIdByGroupName(String groupName) throws GroupException;

	void removeHomeworkDetails(HomeworkDetails hd) throws GroupException, UserException;

	void updateGroup(Group group, ArrayList<Integer> wishedTeacherIds) throws GroupException, ValidationException, UserException;

	ArrayList<Integer> getIdsOfGroupsForWhichIsHomework(HomeworkDetails homeworkDetails) throws GroupException;

	HomeworkDetails getHomeworkDetailsById(int chosenHomeworkId) throws GroupException;

	void addHomeworkToGroupTransaction(HomeworkDetails homeworkDetails, Group group) throws GroupException, UserException, SQLException;

	void updateNumberOfTasksForStudents(HomeworkDetails homeworkDetails, int numOfTasks) throws GroupException;

	ArrayList<Integer> getStudentsWithSearchedHomework(HomeworkDetails homeworkDetails) throws GroupException;

}