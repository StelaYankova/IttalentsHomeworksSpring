package com.IttalentsHomeworks.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.mysql.cj.api.jdbc.Statement;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;

public class UserDAO implements IUserDAO {
	private static final String GET_ALL_STUDENTS = "SELECT * FROM IttalentsHomeworks.Users WHERE isTeacher = 0;";
	private static final String GET_ALL_TEACHERS = "SELECT * FROM IttalentsHomeworks.Users WHERE isTeacher = 1;";
	private static final String ADD_HOMEWORK_TO_STUDENT_I = "INSERT INTO IttalentsHomeworks.User_has_homework (user_id,homework_id) VALUES (?,?);";
	private static final String ADD_HOMEWORK_TO_STUDENT_II = "INSERT INTO IttalentsHomeworks.Homework_task_solution (student_id,homework_id,task_number) VALUES (?,?,?);";
	private static final String IS_TASK_NUMBER_VALID = "SELECT COUNT(*) FROM IttalentsHomeworks.Homework_task_solution WHERE student_id = ? AND homework_id = ?;";
	private static final String UPDATE_USER_PROFILE = "UPDATE IttalentsHomeworks.Users SET pass = ?, email = ? WHERE id = ?";
	private static final String DOES_TASK_ALREADY_EXIST = "SELECT * FROM IttalentsHomeworks.Homework_task_solution WHERE student_id = ? AND homework_id = ? AND task_number = ?;";
	private static final String SET_TIME_OF_UPLOAD_OF_TASK = "UPDATE IttalentsHomeworks.Homework_task_solution SET uploaded_on = ? WHERE student_id = ? AND homework_id = ? AND task_number = ?;";
	private static final String SET_SOLUTION_OF_TASK = "UPDATE IttalentsHomeworks.Homework_task_solution SET solution_java = ? WHERE student_id = ? AND homework_id = ? AND task_number = ?;";
	private static final String SET_TEACHER_COMMENT_TO_HOMEWORK = "UPDATE IttalentsHomeworks.User_has_homework SET teacher_comment = ? WHERE user_id = ? AND homework_id = ?;";
	private static final String SET_TEACHER_GRADE_TO_HOMEWORK = "UPDATE IttalentsHomeworks.User_has_homework SET teacher_grade = ? WHERE user_id = ? AND homework_id = ?;";
	private static final String REMOVE_USER_PROFILE = "DELETE FROM IttalentsHomeworks.Users WHERE id = ?;";
	private static final String CREATE_NEW_USER = "INSERT INTO IttalentsHomeworks.Users (username, pass, email) VALUES (?,?,?);";
	private static final String GET_HOMEWORKS_OF_STUDENT = "SELECT H.id, H.heading, H.num_of_tasks, H.tasks_pdf, H.opens, H.closes, UH.teacher_grade, UH.teacher_comment FROM IttalentsHomeworks.User_has_homework UH JOIN IttalentsHomeworks.Homework H ON (H.id = UH.homework_id) WHERE UH.user_id = ?;";
	private static final String GET_TASKS_OF_HOMEWORK_OF_STUDENT = "SELECT homework_id,task_number,uploaded_on,solution_java FROM IttalentsHomeworks.Homework_task_solution WHERE student_id = ? AND homework_id = ?;";
	private static final String GET_HOMEWORKS_OF_STUDENT_BY_GROUP = "SELECT H.id, H.heading, H.num_of_tasks, H.tasks_pdf, H.opens, H.closes, UH.teacher_grade, UH.teacher_comment FROM IttalentsHomeworks.User_has_homework UH JOIN IttalentsHomeworks.Homework H ON (H.id = UH.homework_id) JOIN IttalentsHomeworks.Group_has_Homework GH ON (H.id = GH.homework_id) WHERE UH.user_id = ? AND GH.group_id = ?;";
	private static final String GET_GROUPS_OF_USER = "SELECT CONCAT(G.id) AS 'group_id', G.group_name FROM IttalentsHomeworks.User_has_Group UG JOIN IttalentsHomeworks.Groups G ON (G.id = UG.group_id) WHERE UG.user_id = ?";
	private static final String GET_USER_ID_BY_USERNAME = "SELECT id FROM IttalentsHomeworks.Users WHERE BINARY username = ?;";
	private static final String IS_USER_A_TEACHER = "SELECT isTeacher FROM IttalentsHomeworks.Users WHERE id = ?;";
	private static final String GET_USER_BY_ID = "SELECT * FROM IttalentsHomeworks.Users WHERE id = ?;";
	private static IUserDAO instance;
	private DBManager manager;

	private UserDAO() {
		setManager(DBManager.getInstance());
	}

	public static IUserDAO getInstance() {
		if (instance == null)
			instance = new UserDAO();
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#getManager()
	 */
	@Override
	public DBManager getManager() {
		return manager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.IttalentsHomeworks.DAO.IUserDAO#setManager(com.IttalentsHomeworks.DB.
	 * DBManager)
	 */
	@Override
	public void setManager(DBManager manager) {
		this.manager = manager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#isUserATeacher(int)
	 */
	@Override
	public boolean isUserATeacher(int userId) throws UserException {
		Connection con = manager.getConnection();
		boolean isTeacher = false;
		try {
			PreparedStatement ps = con.prepareStatement(IS_USER_A_TEACHER);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				isTeacher = rs.getBoolean(1);
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with checking if user is a teacher..");
		}
		return isTeacher;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.IttalentsHomeworks.DAO.IUserDAO#getUserIdByUsername(java.lang.String)
	 */
	@Override
	public int getUserIdByUsername(String username) throws UserException {
		Connection con = manager.getConnection();
		int userId = 0;
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(GET_USER_ID_BY_USERNAME);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				userId = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with getting user's id by username..");
		}
		return userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#getGroupsOfUser(int)
	 */
	@Override
	public ArrayList<Group> getGroupsOfUser(int userId) throws UserException, GroupException {
		Connection con = manager.getConnection();
		ArrayList<Group> groupsOfUser = new ArrayList<>();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(GET_GROUPS_OF_USER);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Group currGroup = new Group(rs.getInt(1), rs.getString(2));
				ArrayList<Teacher> teachersOfGroup = GroupDAO.getInstance().getTeachersOfGroup(currGroup);
				ArrayList<Student> studentsOfGroup = GroupDAO.getInstance().getStudentsOfGroup(currGroup);
				ArrayList<HomeworkDetails> homeworkDetailsOfGroup = GroupDAO.getInstance()
						.getHomeworkDetailsOfGroup(currGroup);
				groupsOfUser.add(new Group(rs.getInt(1), rs.getString(2), teachersOfGroup, studentsOfGroup,
						homeworkDetailsOfGroup));
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with getting user's groups..");
		}
		return groupsOfUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.IttalentsHomeworks.DAO.IUserDAO#getHomeworksOfStudentByGroup(com.
	 * IttalentsHomeworks.model.Student, com.IttalentsHomeworks.model.Group)
	 */
	@Override
	public ArrayList<Homework> getHomeworksOfStudentByGroup(int studentId, Group group) throws UserException {
		ArrayList<Homework> homeworksOfStudentByGroup = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_HOMEWORKS_OF_STUDENT_BY_GROUP);
			ps.setInt(1, studentId);
			ps.setInt(2, group.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String openingTimeString = rs.getString(5);
				String closingTimeString = rs.getString(6);
				LocalDateTime openingTime = LocalDateTime.parse(openingTimeString, formatter);
				LocalDateTime closingTime = LocalDateTime.parse(closingTimeString, formatter);

				HomeworkDetails hd = new HomeworkDetails(rs.getInt(1), rs.getString(2), openingTime, closingTime,
						rs.getInt(3), rs.getString(4));
				ArrayList<Task> tasksOfHomeworkOfStudent = UserDAO.getInstance().getTasksOfHomeworkOfStudent(studentId,
						hd);
				homeworksOfStudentByGroup
						.add(new Homework(rs.getInt(7), rs.getString(8), tasksOfHomeworkOfStudent, hd));
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with checking the homeworks of a student by group..");
		}
		return homeworksOfStudentByGroup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#getTasksOfHomeworkOfStudent(com.
	 * IttalentsHomeworks.model.Student,
	 * com.IttalentsHomeworks.model.HomeworkDetails)
	 */
	@Override
	public ArrayList<Task> getTasksOfHomeworkOfStudent(int studentId, HomeworkDetails homeworkDetails)
			throws UserException {
		ArrayList<Task> tasksOfHomeworkOfStudent = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_TASKS_OF_HOMEWORK_OF_STUDENT);
			ps.setInt(1, studentId);
			ps.setInt(2, homeworkDetails.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String uploadedOnString = null;
				if (rs.getString(4) == null) {
					uploadedOnString = "";
					tasksOfHomeworkOfStudent.add(new Task(rs.getInt(2), rs.getString(4), null));
				} else {
					uploadedOnString = rs.getString(3);
					LocalDateTime uploadedOn = LocalDateTime.parse(uploadedOnString, formatter);
					tasksOfHomeworkOfStudent.add(new Task(rs.getInt(2), rs.getString(4), uploadedOn));
				}
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with checking the tasks of the homework of a student..");
		}
		return tasksOfHomeworkOfStudent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.IttalentsHomeworks.DAO.IUserDAO#getUserByUsername(java.lang.String)
	 */
	@Override
	public User getUserByUsername(String username) throws UserException, GroupException {
		User u = null;
		Connection con = manager.getConnection();
		int userId = UserDAO.getInstance().getUserIdByUsername(username);
		if (userId != 0) {
			try {
				PreparedStatement ps = con.prepareStatement(GET_USER_BY_ID);
				ps.setInt(1, userId);

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					ArrayList<Group> groupsOfUser = UserDAO.getInstance()
							.getGroupsOfUser(UserDAO.getInstance().getUserIdByUsername(username));
					if (UserDAO.getInstance().isUserATeacher(userId)) {
						u = new Teacher(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
								rs.getBoolean(5), groupsOfUser);
					} else {
						int id = UserDAO.getInstance().getUserIdByUsername(username);
						ArrayList<Homework> homeworksOfStudent = UserDAO.getInstance().getHomeworksOfStudent(id);
						u = new Student(rs.getInt(1), rs.getString(2)
								, rs.getString(3), rs.getString(4),
								rs.getBoolean(5), groupsOfUser, homeworksOfStudent);
					}
				}
			} catch (SQLException e) {
				throw new UserException("Something went wrong with getting user by username..");
			}
		}

		return u;
	}

	// all homeworks
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#getHomeworksOfStudent(com.
	 * IttalentsHomeworks.model.Student)
	 */
	@Override
	public ArrayList<Homework> getHomeworksOfStudent(int userId) throws UserException {
		ArrayList<Homework> homeworksOfStudent = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_HOMEWORKS_OF_STUDENT);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String openingTimeString = rs.getString(5);
				String closingTimeString = rs.getString(6);
				LocalDateTime openingTime = LocalDateTime.parse(openingTimeString, formatter);
				LocalDateTime closingTime = LocalDateTime.parse(closingTimeString, formatter);
				HomeworkDetails hd = new HomeworkDetails(rs.getInt(1), rs.getString(2), openingTime, closingTime,
						rs.getInt(3), rs.getString(4));
				ArrayList<Task> tasksOfHomeworkOfStudent = UserDAO.getInstance().getTasksOfHomeworkOfStudent(userId,
						hd);
				int teacherScore = 0;
				if (rs.getInt(7) != 0) {
					teacherScore = rs.getInt(7);
				}
				String teacherComment = " ";
				if (rs.getString(8) != null) {
					teacherComment = rs.getString(8);
				}

				homeworksOfStudent.add(new Homework(teacherScore, teacherComment, tasksOfHomeworkOfStudent, hd));
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with checking the homeworks of a student by group..");
		}
		return homeworksOfStudent;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.IttalentsHomeworks.DAO.IUserDAO#createNewUser(com.IttalentsHomeworks.
	 * model.User)
	 */
	@Override
	public void createNewUser(User user) throws UserException, ValidationException, NoSuchAlgorithmException {
		Connection con = manager.getConnection();
		if (!(ValidationsDAO.getInstance().createUserAreThereEmptyFields(user.getUsername().trim(), user.getPassword().trim(),
				user.getRepeatedPassword().trim(), user.getEmail().trim()))) {
			if (ValidationsDAO.getInstance().isUsernameUnique(user.getUsername())
					&& ValidationsDAO.getInstance().isEmailValid(user.getEmail())
					&& ValidationsDAO.getInstance().isPasswordValid(user.getPassword()) && ValidationsDAO.getInstance().isRepeatedPasswordValid(user.getPassword(), user.getRepeatedPassword())
					&& ValidationsDAO.getInstance().isUsernameValid(user.getUsername())) {
				try {
					PreparedStatement ps = con.prepareStatement(CREATE_NEW_USER);
					ps.setString(1, user.getUsername());
					String passe = ValidationsDAO.getInstance().encryptPass(user.getPassword());
					ps.setString(2, passe);
					ps.setString(3, user.getEmail().trim());
					ps.execute();

				} catch (SQLException e) {
					System.out.println(e.getMessage());
					throw new UserException("Something went wrong with adding new user to DB..");
				}
			} else {
				throw new ValidationException("Create new user -- > invalid fields");
			}
		} else {
			throw new ValidationException("Create new user -- > empty fields");

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#removeUserProfile(com.
	 * IttalentsHomeworks.model.User)
	 */
	@Override
	public void removeUserProfile(User user) throws UserException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(REMOVE_USER_PROFILE);
			ps.setInt(1, user.getId());
			ps.execute();
		} catch (SQLException e) {
			throw new UserException("Something went wrong with removing the profile of a user.." + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#setTeacherGrade(com.
	 * IttalentsHomeworks.model.HomeworkDetails,
	 * com.IttalentsHomeworks.model.Student, int)
	 */
	@Override
	public void setTeacherGrade(HomeworkDetails homeworkDetails, int studentId, int teacherGrade)
			throws UserException, ValidationException {
		Connection con = manager.getConnection();

		if (ValidationsDAO.getInstance().isGradeValueValid(teacherGrade)) {
			try {
				PreparedStatement ps = con.prepareStatement(SET_TEACHER_GRADE_TO_HOMEWORK);
				ps.setInt(1, teacherGrade);
				ps.setInt(2, studentId);
				ps.setInt(3, homeworkDetails.getId());
				ps.executeUpdate();
			} catch (SQLException e) {
				throw new UserException("Something went wrong with setting the teacher's grade of homework..");
			}
		} else {
			throw new ValidationException("Teacher grade --> invalid");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#setTeacherComment(com.
	 * IttalentsHomeworks.model.HomeworkDetails,
	 * com.IttalentsHomeworks.model.Student, java.lang.String)
	 */
	@Override
	public void setTeacherComment(HomeworkDetails homeworkDetails, int studentId, String teacherComment)
			throws UserException, ValidationException {
		Connection con = manager.getConnection();
		if (ValidationsDAO.getInstance().isCommentLengthValid(teacherComment)) {
			try {
				PreparedStatement ps = con.prepareStatement(SET_TEACHER_COMMENT_TO_HOMEWORK);
				ps.setString(1, teacherComment);
				ps.setInt(2, studentId);
				ps.setInt(3, homeworkDetails.getId());
				ps.executeUpdate();
			} catch (SQLException e) {
				throw new UserException("Something went wrong with setting the teacher's comment of homework..");
			}
		} else {
			throw new ValidationException("Teacher comment --> invalid");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#setSolutionOfTask(com.
	 * IttalentsHomeworks.model.HomeworkDetails,
	 * com.IttalentsHomeworks.model.Student, com.IttalentsHomeworks.model.Task,
	 * java.lang.String, java.time.LocalDateTime)
	 */
	@Override
	public void setSolutionOfTask(HomeworkDetails homeworkDetails, Student student, int taskNumber, String solution,
			LocalDateTime timeOfUpload) throws UserException {
		Connection con = manager.getConnection();
		if (solution != null
				&& UserDAO.getInstance().isTaskNumberValid(student.getId(), homeworkDetails.getId(), taskNumber)) {
			try {
				con.setAutoCommit(false);
				try {
					PreparedStatement ps = con.prepareStatement(SET_SOLUTION_OF_TASK);
					ps.setString(1, solution);
					ps.setInt(2, student.getId());
					ps.setInt(3, homeworkDetails.getId());
					ps.setInt(4, taskNumber);
					ps.executeUpdate();
					UserDAO.getInstance().setTimeOfUploadOfTask(homeworkDetails, student, taskNumber, timeOfUpload);
					con.commit();
				} catch (SQLException e) {
					con.rollback();
					throw new UserException("Something went wrong with setting student's solution of task..");
				} finally {
					con.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				throw new UserException("Something went wrong with setting student's solution of task..");
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#setTimeOfUploadOfTask(com.
	 * IttalentsHomeworks.model.HomeworkDetails,
	 * com.IttalentsHomeworks.model.Student, com.IttalentsHomeworks.model.Task,
	 * java.time.LocalDateTime)
	 */
	@Override
	public void setTimeOfUploadOfTask(HomeworkDetails homeworkDetails, Student student, int taskNumber,
			LocalDateTime timeOfUpload) throws UserException {
		Connection con = manager.getConnection();
		if (UserDAO.getInstance().isTaskNumberValid(student.getId(), homeworkDetails.getId(), taskNumber)) {
			try {
				PreparedStatement ps = con.prepareStatement(SET_TIME_OF_UPLOAD_OF_TASK);
				ps.setString(1, timeOfUpload.toString());
				ps.setInt(2, student.getId());
				ps.setInt(3, homeworkDetails.getId());
				ps.setInt(4, taskNumber);
				ps.executeUpdate();
			} catch (SQLException e) {
				throw new UserException(
						"Something went wrong with setting the upload time of student's solution of task..");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.IttalentsHomeworks.DAO.IUserDAO#doesTaskAlreadyExist(com.
	 * IttalentsHomeworks.model.HomeworkDetails,
	 * com.IttalentsHomeworks.model.Student, com.IttalentsHomeworks.model.Task)
	 */
	@Override
	public boolean doesTaskAlreadyExist(HomeworkDetails homeworkDetails, Student student, int taskNum)
			throws UserException {
		boolean doesExist = false;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(DOES_TASK_ALREADY_EXIST);
			ps.setInt(1, student.getId());
			ps.setInt(2, homeworkDetails.getId());
			ps.setInt(3, taskNum);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				doesExist = true;
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with checking if task in table already exists..");
		}
		return doesExist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.IttalentsHomeworks.DAO.IUserDAO#updateUser(com.IttalentsHomeworks.
	 * model.User)
	 */
	@Override
	public void updateUser(User user) throws UserException, ValidationException, NoSuchAlgorithmException {
		int id = UserDAO.getInstance().getUserIdByUsername(user.getUsername());
		Connection con = manager.getConnection();
		if (!(ValidationsDAO.getInstance().updateUserAreThereEmptyFields(user.getPassword(), user.getRepeatedPassword(),
				user.getEmail())) && ValidationsDAO.getInstance().isEmailValid(user.getEmail())
				&& ValidationsDAO.getInstance().isPasswordUpdateValid(user.getPassword()) && ValidationsDAO.getInstance()
						.isRepeatedPasswordValid(user.getPassword(), user.getRepeatedPassword())) {
			try {
				PreparedStatement ps = con.prepareStatement(UPDATE_USER_PROFILE);
				if(user.getPassword().length() == 32){//it is already encryptet
					ps.setString(1, user.getPassword());
				}else{
					ps.setString(1, ValidationsDAO.getInstance().encryptPass(user.getPassword().trim()));
				}
				ps.setString(2, user.getEmail().trim());
				ps.setInt(3, id);
				ps.executeUpdate();
			} catch (SQLException e) {
				throw new UserException("Something went wrong with updating user..");
			}
		} else {
			throw new ValidationException("update user --> invalid fields");
		}
	}

	@Override
	public Student getStudentsByUsername(String username) throws UserException {
		User u = null;
		Connection con = manager.getConnection();
		int userId = UserDAO.getInstance().getUserIdByUsername(username);
		try {
			PreparedStatement ps = con.prepareStatement(GET_USER_BY_ID);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int id = UserDAO.getInstance().getUserIdByUsername(username);
				ArrayList<Homework> homeworksOfStudent = UserDAO.getInstance().getHomeworksOfStudent(id);
				u = new Student(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5), null,
						homeworksOfStudent);
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with getting user by username..");
		}
		return (Student) u;
	}

	@Override
	public boolean isTaskNumberValid(int studentId, int homeworkId, int taskNumber) throws UserException {
		boolean isValid = false;
		Connection con = manager.getConnection();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(IS_TASK_NUMBER_VALID);
			ps.setInt(1, studentId);
			ps.setInt(2, homeworkId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (taskNumber < rs.getInt(1)) {
					isValid = true;
				}
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with checking if the task number is valid..");
		}
		return isValid;
	}



	@Override
	public void addHomeworkToStudent(User user, HomeworkDetails hd) throws UserException {
		Connection con = manager.getConnection();
		try {
			con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement(ADD_HOMEWORK_TO_STUDENT_I);
			ps.setInt(1, user.getId());
			ps.setInt(2, hd.getId());
			ps.execute();
			for (int i = 0; i < hd.getNumberOfTasks(); i++) {
				if (!UserDAO.getInstance().doesTaskAlreadyExist(hd, (Student) user, i)) {
					ps = con.prepareStatement(ADD_HOMEWORK_TO_STUDENT_II);
					ps.setInt(1, user.getId());
					ps.setInt(2, hd.getId());
					ps.setInt(3, i);
					ps.execute();
				}
			}
			con.commit();
		} catch (SQLException e) {
			if (con != null) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					throw new UserException("Something went wrong with adding homework to student..");
				}
			}
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				throw new UserException("Something went wrong with adding homework to student..");
			}
		}
	}

	@Override
	public ArrayList<Teacher> getAllTeachers() throws UserException {
		ArrayList<Teacher> allTeachers = new ArrayList<>();
		Connection con = manager.getConnection();
		Statement st;
		try {
			st = (Statement) con.createStatement();
			ResultSet rs = st.executeQuery(GET_ALL_TEACHERS);
			while (rs.next()) {
				allTeachers.add(new Teacher(rs.getInt(1), rs.getString(2), rs.getString(4), rs.getBoolean(5)));
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with getting all teachers..");
		}
		return allTeachers;
	}

	@Override
	public ArrayList<Student> getAllStudents() throws UserException {
		ArrayList<Student> allStudents = new ArrayList<>();
		Connection con = manager.getConnection();
		Statement st;
		try {
			st = (Statement) con.createStatement();
			ResultSet rs = st.executeQuery(GET_ALL_STUDENTS);
			while (rs.next()) {
				allStudents.add(new Student(rs.getInt(1), rs.getString(2), rs.getString(4), rs.getBoolean(5)));
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with getting all students..");
		}
		return allStudents;
	}

	@Override
	public User getUserById(int userId) throws UserException, GroupException {
		User u = null;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_USER_BY_ID);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ArrayList<Group> groupsOfUser = UserDAO.getInstance().getGroupsOfUser(userId);
				if (UserDAO.getInstance().isUserATeacher(userId)) {
					u = new Teacher(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5),
							groupsOfUser);
				} else {
					ArrayList<Homework> homeworksOfStudent = UserDAO.getInstance().getHomeworksOfStudent(userId);
					u = new Student(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5),
							groupsOfUser, homeworksOfStudent);
				}
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with getting user by username..");
		}
		return u;
	}

}
