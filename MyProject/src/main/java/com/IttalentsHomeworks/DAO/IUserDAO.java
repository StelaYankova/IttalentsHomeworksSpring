package com.IttalentsHomeworks.DAO;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.IttalentsHomeworks.DB.DBManager;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.Exceptions.ValidationException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.Homework;
import com.IttalentsHomeworks.model.HomeworkDetails;
import com.IttalentsHomeworks.model.Student;
import com.IttalentsHomeworks.model.Task;
import com.IttalentsHomeworks.model.Teacher;
import com.IttalentsHomeworks.model.User;

public interface IUserDAO {

	
	DBManager getManager();

	void setManager(DBManager manager);

	boolean isUserATeacher(int userId) throws UserException;

	int getUserIdByUsername(String username) throws UserException;

	ArrayList<Group> getGroupsOfUser(int userId) throws UserException, GroupException;

	ArrayList<Homework> getHomeworksOfStudentByGroup(int studentId, int groupId)
			throws UserException, ValidationException, GroupException;

	ArrayList<Task> getTasksOfHomeworkOfStudent(int studentId, int homeworkDetailsId) throws UserException;

	User getUserByUsername(String username) throws UserException, GroupException;

	ArrayList<Homework> getHomeworksOfStudent(int userId) throws UserException;

	void createNewUser(User user) throws UserException, ValidationException, NoSuchAlgorithmException;

	void removeUserProfile(int userId) throws UserException;

	void setTeacherGrade(int homeworkDetailsId, int studentId, int teacherGrade)
			throws UserException, ValidationException;

	void setTeacherComment(int homeworkDetailsId, int studentId, String teacherComment)
			throws UserException, ValidationException;

	void setSolutionOfTask(int homeworkDetailsId, int studentId, int taskNumber, String solution,
			LocalDateTime timeOfUpload) throws UserException;

	void setTimeOfUploadOfTask(int homeworkDetailsId, int studentId, int taskNumber, LocalDateTime timeOfUpload)
			throws UserException;

	boolean doesTaskAlreadyExist(int homeworkDetailsId, int studentId, int taskNum) throws UserException;

	void updateUser(User user, String formerPass) throws UserException, ValidationException, NoSuchAlgorithmException;

	Student getStudentsByUsername(String username) throws UserException;

	boolean isTaskNumberValid(int studentId, int homeworkId, int taskNumber) throws UserException;

	void addHomeworkToStudent(int userId, HomeworkDetails hd) throws UserException;

	ArrayList<Teacher> getAllTeachers() throws UserException;

	ArrayList<Student> getAllStudents() throws UserException;

	User getUserById(int userId) throws UserException, GroupException;

	String getUserUsernameById(Integer studentId) throws UserException;

	ArrayList<HomeworkDetails> getActiveHomeworksOfStudent(int studentId) throws UserException;

	ArrayList<Group> getGroupsOfUserWithoutStudents(int userId) throws UserException, GroupException;

	Homework getHomeworkOfStudent(int userId, int homeworkId) throws UserException;

	void setPassedSystemTest(int userId, int homeworkDetailsId, int taskNum, boolean hasPassedTest)
			throws UserException;

}