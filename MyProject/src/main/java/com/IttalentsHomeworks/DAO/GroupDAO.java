package com.IttalentsHomeworks.DAO;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class GroupDAO implements IGroupDAO {

	private static final String DOES_STUDENT_ALREADY_HAVE_HOMEWORK = "SELECT * FROM IttalentsHomeworks.User_has_homework WHERE user_id = ? AND homework_id = ?;";
	private static final String GET_HOMEWORK_DETAILS_BY_ID = "SELECT id,heading,opens,closes,num_of_tasks,tasks_pdf,test_tasks_directory FROM IttalentsHomeworks.Homework WHERE id = ?;";
	private static final String CHANGE_GROUP_NAME = "UPDATE IttalentsHomeworks.Groups SET group_name = ? WHERE id = ?;";
	private static final String REMOVE_HOMEWORK_DETAILS = "DELETE FROM IttalentsHomeworks.Homework WHERE id = ?";
	private static final String GET_GROUP_ID_BY_GROUP_NAME = "SELECT id FROM IttalentsHomeworks.Groups WHERE BINARY group_name = ?;";
	private static final String GET_GROUP_BY_ID = "SELECT id, group_name FROM IttalentsHomeworks.Groups WHERE id = ?;";
	private static final String ADD_HOMEWORK_TO_GROUP_III = "INSERT INTO IttalentsHomeworks.Homework_task_solution VALUES(?,?,?,null,null,0);";
	private static final String ADD_HOMEWORK_TO_GROUP_II = "INSERT INTO IttalentsHomeworks.User_has_homework (user_id,homework_id) VALUES (?,?);";
	private static final String ADD_HOMEWORK_TO_GROUP_I = "INSERT INTO IttalentsHomeworks.Group_has_Homework VALUES (?,?);";
	private static final String REMOVE_HOMEWORK_FROM_GROUP_I = "DELETE FROM IttalentsHomeworks.Group_has_Homework WHERE group_id = ? AND homework_id = ?;";
	private static final String GET_IDS_OF_GROUPS_FOR_WHICH_IS_HOMEWORK = "SELECT group_id FROM IttalentsHomeworks.Group_has_Homework WHERE homework_id = ?;";
	private static final String UPDATE_HOMEWORK_DETAILS = "UPDATE IttalentsHomeworks.Homework SET heading = ?, opens = ?, closes = ?, num_of_tasks = ?, tasks_pdf = ?, test_tasks_directory = ? WHERE id = ?;";
	private static final String GET_HOMEWORK_DETAILS_ID = "SELECT id FROM IttalentsHomeworks.Homework WHERE BINARY heading = ?;";
	private static final String CREATE_HOMEWORK_DETAILS = "INSERT INTO IttalentsHomeworks.Homework VALUES (NULL, ?, ?, ?, ?, ?,?);";
	private static final String REMOVE_GROUP = "DELETE FROM IttalentsHomeworks.Groups WHERE id = ?;";
	private static final String REMOVE_USER_FROM_GROUP = "DELETE FROM IttalentsHomeworks.User_has_Group WHERE user_id = ? AND group_id = ?;";
	private static final String GET_ALL_GROUPS = "SELECT CONCAT(G.id) AS 'group_id', G.group_name FROM IttalentsHomeworks.Groups G;";
	private static final String GET_ALL_HOMEWORKS_DETAILS = "SELECT H.id, H.heading, H.opens, H.closes, H.num_of_tasks, H.tasks_pdf,H.test_tasks_directory FROM IttalentsHomeworks.Homework H;";
	private static final String CREATE_NEW_GROUP = "INSERT INTO IttalentsHomeworks.Groups (group_name) VALUE (?);";
	private static final String ADD_USER_TO_GROUP = "INSERT INTO IttalentsHomeworks.User_has_Group VALUES (?,?);";
	private static final String IS_USER_ALREADY_IN_GROUP = "SELECT * FROM IttalentsHomeworks.User_has_Group WHERE user_id = ? AND group_id = ?;";
	private static final String GET_HOMEWORK_DETAILS_OF_GROUP = "SELECT H.id, H.heading, H.opens, H.closes, H.num_of_tasks, H.tasks_pdf,H.test_tasks_directory FROM IttalentsHomeworks.Homework H JOIN IttalentsHomeworks.Group_has_Homework GH ON (GH. homework_id = H.id) WHERE GH.group_id = ?;";
	private static final String GET_STUDENTS_OF_GROUP = "SELECT U.id, U.username, U.email, U.isTeacher FROM IttalentsHomeworks.User_has_Group G JOIN IttalentsHomeworks.Users U ON (G.user_id = U.id) WHERE G.group_id = ? AND U.isTeacher = 0;";
	private static final String GET_STUDENTS_OF_GROUP_BY_ID = "SELECT U.id, U.username, U.email, U.isTeacher FROM IttalentsHomeworks.User_has_Group G JOIN IttalentsHomeworks.Users U ON (G.user_id = U.id) WHERE G.group_id = ? AND U.isTeacher = 0;";
	private static final String GET_TEACHERS_OF_GROUP = "SELECT U.id, U.username, U.email, U.isTeacher FROM IttalentsHomeworks.User_has_Group G JOIN IttalentsHomeworks.Users U ON (G.user_id = U.id) WHERE G.group_id = ? AND U.isTeacher = 1;";
	private static IGroupDAO instance;
	private DBManager manager;

	private GroupDAO() {
		setManager(DBManager.getInstance());
	}

	public static IGroupDAO getInstance() {
		if (instance == null)
			instance = new GroupDAO();
		return instance;
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getManager()
	 */
	@Override
	public DBManager getManager() {
		return manager;
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#setManager(com.IttalentsHomeworks.DB.DBManager)
	 */
	@Override
	public void setManager(DBManager manager) {
		this.manager = manager;
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getTeachersOfGroup(int)
	 */
	@Override
	public ArrayList<Teacher> getTeachersOfGroup(int groupId) throws GroupException {
		ArrayList<Teacher> teachersOfGroup = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_TEACHERS_OF_GROUP);
			ps.setInt(1, groupId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				teachersOfGroup.add(new Teacher(rs.getInt(1), rs.getString(2), rs.getString(3)));
			}

		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking the teaches of a group..");
		}
		return teachersOfGroup;
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getStudentsOfGroup(int)
	 */
	@Override
	public ArrayList<Student> getStudentsOfGroup(int groupId) throws GroupException, UserException {
		ArrayList<Student> studentsOfGroup = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_STUDENTS_OF_GROUP);
			ps.setInt(1, groupId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				studentsOfGroup.add(new Student(rs.getInt(1), rs.getString(2), rs.getString(3)));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking the students of a group..");
		}
		return studentsOfGroup;
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getHomeworkDetailsOfGroup(int)
	 */
	@Override
	public ArrayList<HomeworkDetails> getHomeworkDetailsOfGroup(int groupId) throws GroupException {
		ArrayList<HomeworkDetails> homeworksOfGroup = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_HOMEWORK_DETAILS_OF_GROUP);
			ps.setInt(1, groupId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String openingTimeString = rs.getString(3);
				String closingTimeString = rs.getString(4);
				LocalDateTime openingTime = LocalDateTime.parse(openingTimeString, formatter);
				LocalDateTime closingTime = LocalDateTime.parse(closingTimeString, formatter);

				homeworksOfGroup.add(new HomeworkDetails(rs.getInt(1), rs.getString(2), openingTime, closingTime,
						rs.getInt(5), rs.getString(6), rs.getString(7)));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking the homeworks of a group..");
		}
		return homeworksOfGroup;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#isUserAlreadyInGroup(int, java.lang.String)
	 */
	@Override
	public boolean isUserAlreadyInGroup(int groupId, String username) throws GroupException, UserException {
		Connection con = manager.getConnection();
		boolean isUserAlreadyInGroup = false;
		if (ValidationsDAO.getInstance().doesUserExistInDBByUsername(username)) {
			try {
				PreparedStatement ps = con.prepareStatement(IS_USER_ALREADY_IN_GROUP);
				ps.setInt(1, UserDAO.getInstance().getUserIdByUsername(username));
				ps.setInt(2, groupId);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					isUserAlreadyInGroup = true;
				}
			} catch (SQLException e) {
				throw new GroupException("Something went wrong with checking if user is already in a group..");
			}
		}
		return isUserAlreadyInGroup;
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#addUserToGroup(int, int)
	 */
	@Override
	public void addUserToGroup(int groupId, int userId) throws GroupException, UserException, ValidationException {
		Connection con = manager.getConnection();
		if ((ValidationsDAO.getInstance().doesGroupExistInDBById(groupId)
				&& ValidationsDAO.getInstance().doesUserExistInDBById(userId))
				&& ValidationsDAO.getInstance().doesUserExistInDBById(userId)) {
			if (!ValidationsDAO.getInstance().isStudentAlreadyInGroupAddStudent(groupId, userId)) {
				try {
					PreparedStatement ps = con.prepareStatement(ADD_USER_TO_GROUP);
					ps.setInt(1, userId);
					ps.setInt(2, groupId);
					ps.execute();
					if (!UserDAO.getInstance().isUserATeacher(userId)) {
						for (HomeworkDetails hd : GroupDAO.getInstance().getHomeworkDetailsOfGroup(groupId)) {
							if (!((IGroupDAO) GroupDAO.getInstance()).doesStudentAlreadyHaveHomework(userId, hd)) {
								UserDAO.getInstance().addHomeworkToStudent(userId, hd);
							}
						}
					}
				} catch (SQLException e) {
					throw new GroupException("Something went wrong with adding a user to a group..");
				}
			} else {
				throw new ValidationException("Add user to group --> user is already in group");
			}
		} else {
			throw new ValidationException("Add user to group --> invalid fields");
		}
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#doesStudentAlreadyHaveHomework(int, com.IttalentsHomeworks.model.HomeworkDetails)
	 */
	@Override
	public boolean doesStudentAlreadyHaveHomework(int userId, HomeworkDetails hd) throws GroupException {
		boolean doesHaveHw = false;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(DOES_STUDENT_ALREADY_HAVE_HOMEWORK);
			ps.setInt(1, userId);
			ps.setInt(2, hd.getId());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				doesHaveHw = true;
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking if user already has current homework..");
		}
		return doesHaveHw;
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#createNewGroup(com.IttalentsHomeworks.model.Group)
	 */
	@Override
	public void createNewGroup(Group group) throws GroupException, ValidationException {
		Connection con = manager.getConnection();
		if (!ValidationsDAO.getInstance().addGroupAreThereEmptyFields(group.getName())) {
			if (ValidationsDAO.getInstance().isGroupNameValid(group.getName())) {
				if (ValidationsDAO.getInstance().isGroupNameUnique(group.getName())) {
					try {
						con.setAutoCommit(false);
						PreparedStatement ps = con.prepareStatement(CREATE_NEW_GROUP);
						ps.setString(1, group.getName());
						ps.executeUpdate();
						group.setId(GroupDAO.getInstance().getGroupIdByGroupName(group.getName()));
						for (int i = 0; i < group.getTeachers().size(); i++) {
							ps = con.prepareStatement(ADD_USER_TO_GROUP);
							ps.setInt(1, group.getTeachers().get(i).getId());
							ps.setInt(2, group.getId());
							ps.executeUpdate();
						}
						con.commit();
					} catch (SQLException e1) {
						if (con != null) {
							try {
								con.rollback();
							} catch (SQLException excep) {
								throw new GroupException("Something went wrong with creating new group..");
							}
						}
					} finally {
						try {
							con.setAutoCommit(true);
						} catch (SQLException e) {
							throw new GroupException("Something went wrong with creating new group..");
						}
					}
				} else {
					throw new ValidationException("Add group --> name is not unique");
				}
			} else {
				throw new ValidationException("Add group --> name is invalid");
			}
		} else {
			throw new ValidationException("Add group --> empty fields");
		}

	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getAllHomeworksDetails()
	 */
	@Override
	public ArrayList<HomeworkDetails> getAllHomeworksDetails() throws GroupException {
		ArrayList<HomeworkDetails> homeworksOfGroup = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(GET_ALL_HOMEWORKS_DETAILS);
			while (rs.next()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String openingTimeString = rs.getString(3);
				String closingTimeString = rs.getString(4);
				LocalDateTime openingTime = LocalDateTime.parse(openingTimeString, formatter);
				LocalDateTime closingTime = LocalDateTime.parse(closingTimeString, formatter);
				homeworksOfGroup.add(new HomeworkDetails(rs.getInt(1), rs.getString(2), openingTime, closingTime,
						rs.getInt(5), rs.getString(6), rs.getString(7)));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking all homework details..");
		}

		return homeworksOfGroup;
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getAllGroups()
	 */
	@Override
	public ArrayList<Group> getAllGroups() throws UserException, GroupException {
		Connection con = manager.getConnection();
		ArrayList<Group> groups = new ArrayList<>();
		Statement st;
		try {
			st = con.createStatement();
			ResultSet rs = st.executeQuery(GET_ALL_GROUPS);
			while (rs.next()) {
				groups.add(new Group(rs.getInt(1), rs.getString(2)));
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with getting groups.." + e.getMessage());
		}
		return groups;
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#removeUserFromGroup(int, int)
	 */
	@Override
	public void removeUserFromGroup(int groupId, int userId) throws GroupException, UserException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(REMOVE_USER_FROM_GROUP);
			ps.setInt(1, userId);
			ps.setInt(2, groupId);
			ps.execute();
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with removing a student from a group..");
		}

	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#removeGroup(int)
	 */
	@Override
	public void removeGroup(int groupId) throws GroupException, UserException {
		Connection con = manager.getConnection();// add transaction
		try {
			con.setAutoCommit(false);
			try {
				// are homeworks of group just for this group
				ArrayList<HomeworkDetails> hdOfGroup = GroupDAO.getInstance().getHomeworkDetailsOfGroup(groupId);
				for (HomeworkDetails hd : hdOfGroup) {
					if (GroupDAO.getInstance().isHomeworkJustForThisGroup(hd.getId(), groupId)) {
						GroupDAO.getInstance().removeHomeworkDetails(hd);
					}
				}
				PreparedStatement ps = con.prepareStatement(REMOVE_GROUP);
				ps.setInt(1, groupId);
				ps.execute();
				con.commit();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				e.getStackTrace();
				con.rollback();
				throw new GroupException("Something went wrong with removing a group.." + e.getMessage());
			} finally {
				con.setAutoCommit(true);
			}
		} catch (SQLException e1) {
			throw new GroupException("Something went wrong with removing a group.." + e1.getMessage());
		}
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#createHomeworkDetails(com.IttalentsHomeworks.model.HomeworkDetails, java.util.ArrayList)
	 */
	@Override
	public void createHomeworkDetails(HomeworkDetails homeworkDetails, ArrayList<Integer> groupsForHomework)
			throws GroupException, UserException, ValidationException, NotUniqueHomeworkHeadingException {
		Connection con = manager.getConnection();
		if (ValidationsDAO.getInstance().isHomeworkHeadingUniqueAddHomework(homeworkDetails.getHeading())) {
			if (homeworkDetails.getTasksFile() != null
					&& !(homeworkDetails.getTasksFile().trim().equals("") && homeworkDetails.getTestTasksFile() != null
							&& !homeworkDetails.getTestTasksFile().trim().equals("") && groupsForHomework != null
							&& groupsForHomework.size() > 0)) {
				if (!ValidationsDAO.getInstance().isThereEmptyFieldAddHomework(homeworkDetails.getHeading(),
						homeworkDetails.getOpeningTime().toString(), homeworkDetails.getClosingTime().toString(),
						homeworkDetails.getNumberOfTasks())) {
					if (ValidationsDAO.getInstance().isLengthHeadingValidAddHomework(homeworkDetails.getHeading())
							&& ValidationsDAO.getInstance()
									.isHomeworkOpeningTimeValidAddHomework(homeworkDetails.getOpeningTime().toString())
							&& ValidationsDAO.getInstance().isHomeworkClosingTimeValidAddHomework(
									homeworkDetails.getOpeningTime().toString(),
									homeworkDetails.getClosingTime().toString())
							&& ValidationsDAO.getInstance()
									.isHomeworkNumberOfTasksValidAddHomework(homeworkDetails.getNumberOfTasks())) {
						try {
							con.setAutoCommit(false);
							PreparedStatement ps = con.prepareStatement(CREATE_HOMEWORK_DETAILS);
							ps.setString(1, homeworkDetails.getHeading());
							ps.setString(2, homeworkDetails.getOpeningTime().toString());
							ps.setString(3, homeworkDetails.getClosingTime().toString());
							ps.setInt(4, homeworkDetails.getNumberOfTasks());
							ps.setString(5, homeworkDetails.getTasksFile());
							ps.setString(6, homeworkDetails.getTestTasksFile());
							ps.execute();
							homeworkDetails
									.setId(GroupDAO.getInstance().getHomeworkDetailsId(homeworkDetails.getHeading()));
							for (Integer groupId : groupsForHomework) {
								GroupDAO.getInstance().addHomeworkToGroupTransaction(homeworkDetails, groupId);
							}
							con.commit();
						} catch (SQLException e) {
							if (con != null) {
								try {
									e.printStackTrace();
									System.out.println(e.getMessage());
									System.out.println(e.getStackTrace());
									con.rollback();
								} catch (SQLException e1) {
									throw new GroupException("Something went wrong with creating a homework..");
								}
							}
						} finally {
							try {
								con.setAutoCommit(true);
							} catch (SQLException e) {
								throw new GroupException("Something went wrong with creating a homework..");
							}
						}
					} else {
						throw new ValidationException("Add homework --> invalid field");
					}
				} else {
					throw new ValidationException("Add homework --> empty field");
				}
			} else {
				throw new ValidationException("Add homework --> empty field");
			}
		} else {
			throw new NotUniqueHomeworkHeadingException("Add homework --> not unique username");
		}

	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getHomeworkDetailsId(java.lang.String)
	 */
	@Override
	public int getHomeworkDetailsId(String homeworkDetailsHeading) throws GroupException {
		Connection con = manager.getConnection();
		int homeworkDetailsId = 0;
		try {
			PreparedStatement ps = con.prepareStatement(GET_HOMEWORK_DETAILS_ID);
			ps.setString(1, homeworkDetailsHeading);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				homeworkDetailsId = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking the id of a homework..");
		}
		return homeworkDetailsId;
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#updateHomeworkDetails(com.IttalentsHomeworks.model.HomeworkDetails, java.util.ArrayList)
	 */
	@Override
	public void updateHomeworkDetails(HomeworkDetails homeworkDetails, ArrayList<Integer> groupsForHomework)
			throws GroupException, UserException, ValidationException, NotUniqueHomeworkHeadingException {
		Connection con = manager.getConnection();
		HomeworkDetails currHd = GroupDAO.getInstance().getHomeworkDetailsById(homeworkDetails.getId());
		if (ValidationsDAO.getInstance().isHomeworkUpdateHeadingUnique(homeworkDetails.getHeading(), currHd)) {

			if (homeworkDetails.getTasksFile() != null
					&& !(homeworkDetails.getTasksFile().trim().equals("") && homeworkDetails.getTestTasksFile() != null
							&& !(homeworkDetails.getTestTasksFile().trim().equals("")))
					&& groupsForHomework != null && groupsForHomework.size() > 0) {
				if (!ValidationsDAO.getInstance().updateGroupAreThereEmptyFields(homeworkDetails.getHeading(),
						homeworkDetails.getOpeningTime().toString(), homeworkDetails.getClosingTime().toString(),
						homeworkDetails.getTasksFile().trim())) {
					if (ValidationsDAO.getInstance().areHomeworkUpdateCharactersValid(homeworkDetails.getHeading())
							&& ValidationsDAO.getInstance().isHomeworkUpdateLengthValid(homeworkDetails.getHeading())
							&& ValidationsDAO.getInstance().isHomeworkUpdateOpeningTimeValid(
									homeworkDetails.getOpeningTime().toString(), currHd)
							&& ValidationsDAO.getInstance().isHomeworkUpdateClosingTimeValid(
									homeworkDetails.getOpeningTime().toString(),
									homeworkDetails.getClosingTime().toString(), currHd)
							&& ValidationsDAO.getInstance()
									.isHomeworkUpdateNumberOfTasksValid(homeworkDetails.getNumberOfTasks())) {
						try {
							con.setAutoCommit(false);
							PreparedStatement ps = con.prepareStatement(UPDATE_HOMEWORK_DETAILS);
							ps.setString(1, homeworkDetails.getHeading());
							ps.setString(2, homeworkDetails.getOpeningTime().toString());
							ps.setString(3, homeworkDetails.getClosingTime().toString());
							ps.setInt(4, homeworkDetails.getNumberOfTasks());
							ps.setString(5, homeworkDetails.getTasksFile());
							ps.setString(6, homeworkDetails.getTestTasksFile());
							ps.setInt(7, homeworkDetails.getId());
							ps.executeUpdate();
							ArrayList<Integer> currGroupIds = GroupDAO.getInstance()
									.getIdsOfGroupsForWhichIsHomework(homeworkDetails.getId());
							ArrayList<Integer> wishedGroupIds = new ArrayList<>();
							for (Integer id : groupsForHomework) {
								wishedGroupIds.add(id);
							}
							for (Integer id : currGroupIds) {
								if (!(wishedGroupIds.contains(id))) {
									GroupDAO.getInstance().removeHomeworkFromGroup(homeworkDetails.getId(), id);
								}
							}
							for (Integer id : wishedGroupIds) {
								if (!(currGroupIds.contains(id))) {
									GroupDAO.getInstance().addHomeworkToGroupTransaction(homeworkDetails, id);
								}
							}
							GroupDAO.getInstance().updateNumberOfTasksForStudents(homeworkDetails,
									currHd.getNumberOfTasks());
							con.commit();
						} catch (SQLException e) {
							try {
								con.rollback();
							} catch (SQLException e1) {
								throw new GroupException("Something went wrong with updating homework details..");
							}
						} finally {
							try {
								con.setAutoCommit(true);
							} catch (SQLException e) {
								throw new GroupException("Something went wrong with updating homework details..");
							}
						}
					} else {
						throw new ValidationException("Update homework --> invalid field");
					}
				} else {

					throw new ValidationException("Update homework --> empty field");
				}

			} else {
				throw new ValidationException("Update homework --> empty field");
			}
		} else {
			throw new NotUniqueHomeworkHeadingException("Add homework --> not unique username");
		}

	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getIdsOfGroupsForWhichIsHomework(int)
	 */
	@Override
	public ArrayList<Integer> getIdsOfGroupsForWhichIsHomework(int homeworkDetailsId) throws GroupException {
		ArrayList<Integer> groupsIds = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_IDS_OF_GROUPS_FOR_WHICH_IS_HOMEWORK);
			ps.setInt(1, homeworkDetailsId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				groupsIds.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			throw new GroupException(
					"Something went wrong with getting the ids of groups, for which is the homework..");
		}
		return groupsIds;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#removeHomeworkFromGroup(int, int)
	 */
	@Override
	public void removeHomeworkFromGroup(int homeworkDetailsId, int groupId) throws GroupException, UserException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(REMOVE_HOMEWORK_FROM_GROUP_I);
			ps.setInt(1, groupId);
			ps.setInt(2, homeworkDetailsId);
			ps.execute();
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with removing a homework from group..");
		}
	}


	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#addHomeworkToGroup(com.IttalentsHomeworks.model.HomeworkDetails, int)
	 */
	@Override
	public void addHomeworkToGroup(HomeworkDetails homeworkDetails, int groupId) throws GroupException, UserException {
		Connection con = manager.getConnection();
		try {
			con.setAutoCommit(false);
			try {
				PreparedStatement ps = con.prepareStatement(ADD_HOMEWORK_TO_GROUP_I);
				ps.setInt(1, groupId);
				ps.setInt(2, homeworkDetails.getId());
				ps.execute();
				for (Student s : GroupDAO.getInstance().getStudentsOfGroup(groupId)) {
					if (!((IGroupDAO) GroupDAO.getInstance()).doesStudentAlreadyHaveHomework(s.getId(),
							homeworkDetails)) {
						ps = con.prepareStatement(ADD_HOMEWORK_TO_GROUP_II);
						ps.setInt(1, s.getId());
						ps.setInt(2, homeworkDetails.getId());
						ps.execute();
						for (int i = 0; i < homeworkDetails.getNumberOfTasks(); i++) {
							Task t = new Task(i, null, null, false);
							if (!UserDAO.getInstance().doesTaskAlreadyExist(homeworkDetails.getId(), s.getId(), i)) {
								ps = con.prepareStatement(ADD_HOMEWORK_TO_GROUP_III);
								ps.setInt(1, s.getId());
								ps.setInt(2, homeworkDetails.getId());
								ps.setInt(3, t.getTaskNumber());
								ps.execute();
							}
						}
					}
				}
				con.commit();
			} catch (SQLException e) {
				con.rollback();
				System.out.println(e.getMessage());
				throw new GroupException("Something went wrong with adding a homework to group..");
			} finally {
				con.setAutoCommit(true);
			}
		} catch (SQLException e1) {
			System.out.println(e1.getMessage());
			throw new GroupException("Something went wrong with adding a homework to group..");
		}
	}

	
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getGroupById(int)
	 */
	@Override
	public Group getGroupById(int id) throws GroupException, UserException {
		Group group = null;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_GROUP_BY_ID);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				group = new Group(rs.getInt(1), rs.getString(2));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting the group by id..");
		}
		return group;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getGroupIdByGroupName(java.lang.String)
	 */
	@Override
	public int getGroupIdByGroupName(String groupName) throws GroupException {
		int idGroup = 0;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_GROUP_ID_BY_GROUP_NAME);
			ps.setString(1, groupName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				idGroup = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting id of group..");
		}

		return idGroup;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#removeHomeworkDetails(com.IttalentsHomeworks.model.HomeworkDetails)
	 */
	@Override
	public void removeHomeworkDetails(HomeworkDetails homeworkDetails) throws GroupException, UserException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(REMOVE_HOMEWORK_DETAILS);
			ps.setInt(1, homeworkDetails.getId());
			ArrayList<Integer> currGroupIds = GroupDAO.getInstance()
					.getIdsOfGroupsForWhichIsHomework(homeworkDetails.getId());
			ArrayList<Group> currGroups = new ArrayList<>();
			for (Integer id : currGroupIds) {
				currGroups.add(GroupDAO.getInstance().getGroupById(id));
			}
			ps.execute();
			File fileTasks = new File(
					IValidationsDAO.SAVE_DIR_HOMEWORK_FILES_PDF + File.separator + homeworkDetails.getTasksFile());
			if (fileTasks.exists()) {
				fileTasks.delete();
			}
			File fileTests = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator
					+ homeworkDetails.getTestTasksFile());
			if (fileTests.exists()) {
				String[] entries = fileTests.list();
				if (entries != null) {
					for (String s : entries) {
						File currentFile = new File(fileTests.getPath(), s);
						currentFile.delete();
					}
				}
				fileTests.delete();
			}
			File fileTestsZip = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator
					+ homeworkDetails.getTestTasksFile() + ".zip");
			if (fileTestsZip.exists()) {
				fileTestsZip.delete();
			}
			for (Group group : currGroups) {
				for (Student s : GroupDAO.getInstance().getStudentsOfGroup(group.getId())) {
					for (int i = 0; i < homeworkDetails.getNumberOfTasks(); i++) {
						String fileName = "hwId" + homeworkDetails.getId() + "userId" + s.getId() + "taskNum" + i;
						File fileStudentTasks = new File(
								IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + fileName);
						if (fileStudentTasks.exists()) {
							String[] entries = fileStudentTasks.list();
							if (entries != null) {
								for (String entry : entries) {
									File currentFile = new File(fileStudentTasks.getPath(), entry);
									currentFile.delete();
								}
							}
							fileStudentTasks.delete();
						}
					}
				}
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with removing homework details rollback..");
		}
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#updateGroup(com.IttalentsHomeworks.model.Group, java.util.ArrayList)
	 */
	@Override
	public void updateGroup(Group group, ArrayList<Integer> wishedTeacherIds)
			throws GroupException, ValidationException, UserException {
		Connection con = manager.getConnection();
		if (!(ValidationsDAO.getInstance().isThereGroupEmptyFieldUpdate(group.getName()))
				&& ValidationsDAO.getInstance().isGroupNameUniqueUpdate(group.getId(), group.getName())
				&& ValidationsDAO.getInstance().isGroupNameValid(group.getName())) {
			try {
				con.setAutoCommit(false);
				PreparedStatement ps = con.prepareStatement(CHANGE_GROUP_NAME);
				ps.setString(1, group.getName());
				ps.setInt(2, group.getId());
				ps.executeUpdate();
				// curr teachers
				ArrayList<Teacher> currTeachers = GroupDAO.getInstance().getTeachersOfGroup(group.getId());
				ArrayList<Integer> currTeachersIds = new ArrayList<>();
				for (Teacher t : currTeachers) {
					currTeachersIds.add(t.getId());
				}
				// wished teachers
				ArrayList<Integer> wishedTeachersIds = new ArrayList<>();
				wishedTeachersIds.addAll(wishedTeacherIds);
				if (currTeachersIds != null) {
					for (Integer teacherId : currTeachersIds) {
						if (!(wishedTeachersIds.contains(teacherId))) {
							GroupDAO.getInstance().removeUserFromGroup(group.getId(), teacherId);
						}
					}
				}
				if (wishedTeachersIds != null) {
					for (Integer id : wishedTeachersIds) {
						if (!(currTeachersIds.contains(id))) {
							GroupDAO.getInstance().addUserToGroup(group.getId(), id);
						}
					}
				}
				con.commit();
			} catch (SQLException e) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					throw new GroupException("Something went wrong with changing the name of a group..");
				}
			} finally {
				try {
					con.setAutoCommit(true);
				} catch (SQLException e) {
					throw new GroupException("Something went wrong with changing the name of a group..");
				}
			}
		} else {
			throw new ValidationException("Update group --> invalid fields");
		}
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getHomeworkDetailsById(int)
	 */
	@Override
	public HomeworkDetails getHomeworkDetailsById(int chosenHomeworkId) throws GroupException {
		Connection con = manager.getConnection();
		HomeworkDetails homeworkDetails = null;
		try {
			PreparedStatement ps = con.prepareStatement(GET_HOMEWORK_DETAILS_BY_ID);
			ps.setInt(1, chosenHomeworkId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String openingTimeString = rs.getString(3);
				String closingTimeString = rs.getString(4);

				LocalDateTime openingTime = LocalDateTime.parse(openingTimeString, formatter);
				LocalDateTime closingTime = LocalDateTime.parse(closingTimeString, formatter);
				homeworkDetails = new HomeworkDetails(rs.getInt(1), rs.getString(2), openingTime, closingTime,
						rs.getInt(5), rs.getString(6), rs.getString(7));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting homework details..");
		}
		return homeworkDetails;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#addHomeworkToGroupTransaction(com.IttalentsHomeworks.model.HomeworkDetails, int)
	 */
	@Override
	public void addHomeworkToGroupTransaction(HomeworkDetails homeworkDetails, int groupId)
			throws GroupException, UserException, SQLException {
		Connection con = manager.getConnection();
		PreparedStatement ps = con.prepareStatement(ADD_HOMEWORK_TO_GROUP_I);
		ps.setInt(1, groupId);
		ps.setInt(2, homeworkDetails.getId());
		ps.execute();
		for (Student s : GroupDAO.getInstance().getStudentsOfGroup(groupId)) {
			if (!((IGroupDAO) GroupDAO.getInstance()).doesStudentAlreadyHaveHomework(s.getId(), homeworkDetails)) {
				ps = con.prepareStatement(ADD_HOMEWORK_TO_GROUP_II);
				ps.setInt(1, s.getId());
				ps.setInt(2, homeworkDetails.getId());
				ps.execute();
				for (int i = 0; i < homeworkDetails.getNumberOfTasks(); i++) {
					Task t = new Task(i, null, null, false);
					if (!UserDAO.getInstance().doesTaskAlreadyExist(homeworkDetails.getId(), s.getId(), i)) {
						ps = con.prepareStatement(ADD_HOMEWORK_TO_GROUP_III);
						ps.setInt(1, s.getId());
						ps.setInt(2, homeworkDetails.getId());
						ps.setInt(3, t.getTaskNumber());
						ps.execute();

					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#updateNumberOfTasksForStudents(com.IttalentsHomeworks.model.HomeworkDetails, int)
	 */
	@Override
	public void updateNumberOfTasksForStudents(HomeworkDetails homeworkDetails, int numOfTasks) throws GroupException {
		Connection con = manager.getConnection();
		PreparedStatement ps;
		ArrayList<Integer> students = GroupDAO.getInstance().getStudentsWithSearchedHomework(homeworkDetails.getId());
		if (homeworkDetails.getNumberOfTasks() > numOfTasks) {
			for (Integer studentId : students) {
				int currTaskNumberInsert = numOfTasks;
				while (currTaskNumberInsert != homeworkDetails.getNumberOfTasks()) {
					try {
						ps = con.prepareStatement(
								"INSERT INTO IttalentsHomeworks.Homework_task_solution (student_id, homework_id, task_number) VALUES (?,?,?);");
						ps.setInt(1, studentId);
						ps.setInt(2, homeworkDetails.getId());
						ps.setInt(3, currTaskNumberInsert);
						ps.execute();
						currTaskNumberInsert++;
					} catch (SQLException e) {
						throw new GroupException("Something went wrong with adding tasks to homework of student..");
					}
				}

			}

		} else if (homeworkDetails.getNumberOfTasks() < numOfTasks) {
			for (Integer studentId : students) {
				int currTaskNumberRemove = homeworkDetails.getNumberOfTasks();
				while (currTaskNumberRemove != numOfTasks) {
					try {
						ps = con.prepareStatement(
								"DELETE FROM IttalentsHomeworks.Homework_task_solution WHERE student_id = ? AND homework_id = ? AND task_number = ?;");
						ps.setInt(1, studentId);
						ps.setInt(2, homeworkDetails.getId());
						ps.setInt(3, currTaskNumberRemove);
						ps.execute();

						String fileName = "hwId" + homeworkDetails.getId() + "userId" + studentId + "taskNum"
								+ currTaskNumberRemove;
						System.out.println("Remove file " + fileName);
						File fileStudentTasks = new File(
								IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + fileName);
						if (fileStudentTasks.exists()) {
							String[] entries = fileStudentTasks.list();
							if (entries != null) {
								for (String entry : entries) {
									File currentFile = new File(fileStudentTasks.getPath(), entry);
									currentFile.delete();
								}
							}
							fileStudentTasks.delete();
						}
						currTaskNumberRemove++;

					} catch (SQLException e) {
						throw new GroupException("Something went wrong with removing tasks from homework of student..");
					}
				}

			}
		}

	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getStudentsWithSearchedHomework(int)
	 */
	@Override
	public ArrayList<Integer> getStudentsWithSearchedHomework(int homeworkDetailsId) throws GroupException {
		Connection con = manager.getConnection();
		PreparedStatement ps;
		ArrayList<Integer> students = new ArrayList<>();
		try {
			ps = con.prepareStatement(
					"SELECT user_id FROM IttalentsHomeworks.User_has_homework WHERE homework_id = ?;");
			ps.setInt(1, homeworkDetailsId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				students.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting students with searched homeworks..");
		}
		return students;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getGroupWithoutStudentsById(int)
	 */
	@Override
	public Group getGroupWithoutStudentsById(int groupId) throws GroupException {
		Group group = null;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_GROUP_BY_ID);
			ps.setInt(1, groupId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ArrayList<Teacher> teachers = GroupDAO.getInstance().getTeachersOfGroup(groupId);
				group = new Group(rs.getInt(1), rs.getString(2), teachers);
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting the group by id..");
		}
		return group;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getGroupNameById(int)
	 */
	@Override
	public String getGroupNameById(int chosenGroupId) throws GroupException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con
					.prepareStatement("SELECT group_name FROM IttalentsHomeworks.Groups WHERE id = ?;");
			ps.setInt(1, chosenGroupId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting the group name by id..");
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getStudentsIdsOfGroup(int)
	 */
	@Override
	public ArrayList<Integer> getStudentsIdsOfGroup(int groupId) throws GroupException {
		ArrayList<Integer> studentsOfGroup = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_STUDENTS_OF_GROUP_BY_ID);
			ps.setInt(1, groupId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				studentsOfGroup.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting the students(id) of a group..");
		}
		return studentsOfGroup;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getAllGroupsWithoutStudents()
	 */
	@Override
	public ArrayList<Group> getAllGroupsWithoutStudents() throws GroupException {
		Connection con = manager.getConnection();
		ArrayList<Group> groups = new ArrayList<>();
		Statement st;
		try {
			st = con.createStatement();
			ResultSet rs = st.executeQuery(GET_ALL_GROUPS);
			while (rs.next()) {
				ArrayList<HomeworkDetails> homeworkDetailsOfGroup = GroupDAO.getInstance()
						.getHomeworkDetailsOfGroup(rs.getInt(1));
				groups.add(new Group(rs.getInt(1), rs.getString(2), null, null, homeworkDetailsOfGroup));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting groups.." + e.getMessage());
		}
		return groups;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#doesPassSystemTest(java.lang.String, com.IttalentsHomeworks.model.Homework, int)
	 */
	@Override
	public boolean doesPassSystemTest(String solutionOfStudent, Homework homework, int taskNum)
			throws IOException, InterruptedException {
		// we enter the directory with the student answer
		String getNameOfTask = solutionOfStudent.split("/", solutionOfStudent.length())[1];
		String getNameOfDir = solutionOfStudent.split("/", solutionOfStudent.length())[0];

		// we compile the program
		Process proc = Runtime.getRuntime()
				.exec("javac " + IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + solutionOfStudent);
		proc.waitFor();
		String inXPath = IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator
				+ homework.getHomeworkDetails().getTestTasksFile();
		// we get args params
		File in = new File(inXPath + File.separator + "in" + taskNum + ".txt");
		if (!in.exists()) {
			return false;
		}
		Process proc1 = Runtime.getRuntime().exec("cat " + inXPath + File.separator + "in" + taskNum + ".txt");
		proc1.waitFor();
		InputStream stdout = proc1.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
		String line = "";
		StringBuilder argParams = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			argParams.append(line + " ");
		}
		// we run the program
		Process proc2 = Runtime.getRuntime()
				.exec("java -cp " + IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + getNameOfDir
						+ File.separator + " " + getNameOfTask.substring(0, getNameOfTask.length() - IValidationsDAO.java_EXTENSION_REMOVE) + " "
						+ argParams);
		proc2.waitFor();
		stdout = proc2.getInputStream();
		reader = new BufferedReader(new InputStreamReader(stdout));
		line = "";
		StringBuilder answerStudent = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			answerStudent.append(line);
		}
		String outXPath = IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator
				+ homework.getHomeworkDetails().getTestTasksFile();
		File out = new File(outXPath + File.separator + "out" + taskNum + ".txt");
		if (!out.exists()) {
			return false;
		}
		Process proc3 = Runtime.getRuntime().exec("cat " + outXPath + File.separator + "out" + taskNum + ".txt");
		proc3.waitFor();
		stdout = proc3.getInputStream();
		reader = new BufferedReader(new InputStreamReader(stdout));
		String answerTest = null;
		StringBuilder answerTruth = new StringBuilder();
		while ((answerTest = reader.readLine()) != null) {
			answerTruth.append(answerTest);
		}
		boolean doesPastSystemTest = answerStudent.toString().equals(answerTruth.toString());
		return doesPastSystemTest;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#isHomeworkJustForThisGroup(int, int)
	 */
	@Override
	public boolean isHomeworkJustForThisGroup(int homeworkId, int groupId) throws GroupException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM IttalentsHomeworks.Group_has_Homework WHERE homework_id = ?;");
			ps.setInt(1, homeworkId);
			ResultSet rs = ps.executeQuery();
			int count = 0;
			if(rs.next()){
				count = rs.getInt(1);
			}
			if(count == 1){
				return true;
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking if a homework is just for one group.." + e.getMessage());
		}
		return false;
	}
}
