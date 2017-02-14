package com.IttalentsHomeworks.DAO;

import java.io.File;
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
import com.IttalentsHomeworks.Exceptions.NotUniqueUsernameException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.Exceptions.ValidationException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.Homework;
import com.IttalentsHomeworks.model.HomeworkDetails;
import com.IttalentsHomeworks.model.Student;
import com.IttalentsHomeworks.model.Task;
import com.IttalentsHomeworks.model.Teacher;
import com.IttalentsHomeworks.model.User;

public class GroupDAO implements IGroupDAO {

	private static final String CHANGE_GROUP_NAME = "UPDATE IttalentsHomeworks.Groups SET group_name = ? WHERE id = ?;";
	private static final String REMOVE_HOMEWORK_DETAILS = "DELETE FROM IttalentsHomeworks.Homework WHERE id = ?";
	private static final String GET_GROUP_ID_BY_GROUP_NAME = "SELECT id FROM IttalentsHomeworks.Groups WHERE group_name = ?;";
	private static final String GET_GROUP_BY_ID = "SELECT id, group_name FROM IttalentsHomeworks.Groups WHERE id = ?;";
	private static final String ADD_HOMEWORK_TO_GROUP_III = "INSERT INTO IttalentsHomeworks.Homework_task_solution VALUES(?,?,?,null,null);";
	private static final String ADD_HOMEWORK_TO_GROUP_II = "INSERT INTO IttalentsHomeworks.User_has_homework VALUES (?,?,null, null);";
	private static final String ADD_HOMEWORK_TO_GROUP_I = "INSERT INTO IttalentsHomeworks.Group_has_Homework VALUES (?,?);";
	private static final String REMOVE_HOMEWORK_FROM_GROUP_III = "DELETE FROM IttalentsHomeworks.Homework_task_solution WHERE student_id = ? AND homework_id = ?;";
	private static final String REMOVE_HOMEWORK_FROM_GROUP_II = "DELETE FROM IttalentsHomeworks.User_has_homework WHERE user_id = ? AND homework_id = ?;";
	private static final String REMOVE_HOMEWORK_FROM_GROUP_I = "DELETE FROM IttalentsHomeworks.Group_has_Homework WHERE group_id = ? AND homework_id = ?;";
	private static final String GET_IDS_OF_GROUPS_FOR_WHICH_IS_HOMEWORK = "SELECT group_id FROM IttalentsHomeworks.Group_has_Homework WHERE homework_id = ?;";
	private static final String UPDATE_HOMEWORK_DETAILS = "UPDATE IttalentsHomeworks.Homework SET heading = ?, opens = ?, closes = ?, num_of_tasks = ?, tasks_pdf = ? WHERE id = ?;";
	private static final String GET_HOMEWORK_DETAILS_ID = "SELECT id FROM IttalentsHomeworks.Homework WHERE heading = ?;";
	private static final String CREATE_HOMEWORK_DETAILS = "INSERT INTO IttalentsHomeworks.Homework VALUES (NULL, ?, ?, ?, ?, ?);";
	private static final String REMOVE_GROUP = "DELETE FROM IttalentsHomeworks.Groups WHERE id = ?;";
	private static final String REMOVE_USER_FROM_GROUP = "DELETE FROM IttalentsHomeworks.User_has_Group WHERE user_id = ? AND group_id = ?;";
	private static final String GET_ALL_GROUPS = "SELECT CONCAT(G.id) AS 'group_id', G.group_name FROM IttalentsHomeworks.Groups G;";
	private static final String GET_ALL_HOMEWORKS_DETAILS = "SELECT H.id, H.heading, H.opens, H.closes, H.num_of_tasks, H.tasks_pdf FROM IttalentsHomeworks.Homework H;";
	private static final String CREATE_NEW_GROUP = "INSERT INTO IttalentsHomeworks.Groups (group_name) VALUE (?);";
	private static final String ADD_USER_TO_GROUP = "INSERT INTO IttalentsHomeworks.User_has_Group VALUES (?,?);";
	private static final String IS_USER_ALREADY_IN_GROUP = "SELECT * FROM IttalentsHomeworks.User_has_Group WHERE user_id = ? AND group_id = ?;";
	private static final String GET_HOMEWORK_DETAILS_OF_GROUP = "SELECT H.id, H.heading, H.opens, H.closes, H.num_of_tasks, H.tasks_pdf FROM IttalentsHomeworks.Homework H JOIN IttalentsHomeworks.Group_has_Homework GH ON (GH. homework_id = H.id) WHERE GH.group_id = ?;";
	private static final String GET_STUDENTS_OF_GROUP = "SELECT U.id, U.username, U.email, U.isTeacher FROM IttalentsHomeworks.User_has_Group G JOIN IttalentsHomeworks.Users U ON (G.user_id = U.id) WHERE G.group_id = ? AND U.isTeacher = 0;";
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
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getTeachersOfGroup(com.IttalentsHomeworks.model.Group)
	 */
	@Override
	public ArrayList<Teacher> getTeachersOfGroup(Group group) throws GroupException {
		ArrayList<Teacher> teachersOfGroup = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(
					GET_TEACHERS_OF_GROUP);
			ps.setInt(1, group.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				teachersOfGroup.add(new Teacher(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4)));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking the teaches of a group..");
		}
		return teachersOfGroup;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getStudentsOfGroup(com.IttalentsHomeworks.model.Group)
	 */
	@Override
	public ArrayList<Student> getStudentsOfGroup(Group group) throws GroupException, UserException {
		ArrayList<Student> studentsOfGroup = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(
					GET_STUDENTS_OF_GROUP);
			ps.setInt(1, group.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				// all homeworks of student, not by group
				// we dont have to know their groups
				Student currStudent = (Student) UserDAO.getInstance().getStudentsByUsername(rs.getString(2));
				ArrayList<Homework> homeworksOfStudent = UserDAO.getInstance().getHomeworksOfStudent(currStudent.getId());
				
				studentsOfGroup.add(new Student(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4),
						homeworksOfStudent));
				
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking the students of a group..");
		}
		return studentsOfGroup;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getHomeworksDetailsOfGroup(com.IttalentsHomeworks.model.Group)
	 */
	@Override
	public ArrayList<HomeworkDetails> getHomeworkDetailsOfGroup(Group group) throws GroupException {
		ArrayList<HomeworkDetails> homeworksOfGroup = new ArrayList<>();
		Connection con = manager.getConnection();			

		try {
			PreparedStatement ps = con.prepareStatement(
					GET_HOMEWORK_DETAILS_OF_GROUP);
			ps.setInt(1, group.getId());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String openingTimeString = rs.getString(3);
				String closingTimeString = rs.getString(4);
				LocalDateTime openingTime = LocalDateTime.parse(openingTimeString, formatter);
				LocalDateTime closingTime = LocalDateTime.parse(closingTimeString, formatter);

				homeworksOfGroup.add(new HomeworkDetails(rs.getInt(1), rs.getString(2), openingTime, closingTime,
						rs.getInt(5), rs.getString(6)));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking the homeworks of a group..");
		}

		return homeworksOfGroup;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#isUserAlreadyInGroup(com.IttalentsHomeworks.model.Group, com.IttalentsHomeworks.model.User)
	 */
	@Override
	public boolean isUserAlreadyInGroup(Group group, User user) throws GroupException, UserException {
		Connection con = manager.getConnection();
		boolean isUserAlreadyInGroup = false;
		if (UserDAO.getInstance().doesUserExistInDB(user.getUsername(), user.getPassword())) {
			try {
				PreparedStatement ps = con.prepareStatement(IS_USER_ALREADY_IN_GROUP);
				ps.setInt(1, user.getId());
				ps.setInt(2, group.getId());
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
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#addUserToGroup(com.IttalentsHomeworks.model.Group, com.IttalentsHomeworks.model.User)
	 */
	@Override
	public void addUserToGroup(Group group, User user) throws GroupException, UserException, ValidationException {
		Connection con = manager.getConnection();		
		if ((group != null && user != null)
				&& ValidationsDAO.getInstance().doesStudentExist(user.getUsername())) {
			if (!ValidationsDAO.getInstance().isStudentAlreadyInGroupAddStudent(group.getId(), user.getUsername())) {

				try {
					PreparedStatement ps = con.prepareStatement(ADD_USER_TO_GROUP);
					ps.setInt(1, user.getId());
					ps.setInt(2, group.getId());
					ps.execute();
					if (!user.isTeacher()) {
						// for za vsi4ki doma6ni na grupata
						// da go nqma
						for (HomeworkDetails hd : GroupDAO.getInstance().getHomeworkDetailsOfGroup(group)) {
							if (!((GroupDAO) GroupDAO.getInstance()).doesStudentAlreadyHaveHomework(user, hd)) {
								UserDAO.getInstance().addHomeworkToStudent(user, hd);
							}
						}
					}
				} catch (SQLException e) {
					throw new GroupException("Something went wrong with adding a user to a group..");
				}
			}else{
				throw new ValidationException("Add user to group --> user is already in group");
			}
		}else{
			throw new ValidationException("Add user to group --> invalid fields");
		}
	}

	public boolean doesStudentAlreadyHaveHomework(User user, HomeworkDetails hd) throws GroupException{
		boolean doesHaveHw = false;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM IttalentsHomeworks.User_has_homework WHERE user_id = ? AND homework_id = ?;");
			ps.setInt(1, user.getId());
			ps.setInt(2, hd.getId());
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				doesHaveHw = true;
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking if user already has current homework..");
		}
		return doesHaveHw;
	}
	// constructor with teachers
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#createNewGroup(com.IttalentsHomeworks.model.Group)
	 */
	@Override
	public void createNewGroup(Group group) throws GroupException, ValidationException {
		Connection con = manager.getConnection();
		if(!ValidationsDAO.getInstance().addGroupAreThereEmptyFields(group.getName())){
		if(ValidationsDAO.getInstance().isGroupNameValid(group.getName())){
		if (ValidationsDAO.getInstance().isGroupNameUnique(group.getName())) {

		try {
			con.setAutoCommit(false);
				PreparedStatement ps = con
						.prepareStatement(CREATE_NEW_GROUP);
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
	            } catch(SQLException excep) {
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
		}else{
			throw new ValidationException("Add group --> name is not unique");
		}}else{
			throw new ValidationException("Add group --> name is invalid");
		}}	else{		throw new ValidationException("Add group --> empty fields");
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
			ResultSet rs = st.executeQuery(
					GET_ALL_HOMEWORKS_DETAILS);
			while (rs.next()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String openingTimeString = rs.getString(3);
				String closingTimeString = rs.getString(4);
				LocalDateTime openingTime = LocalDateTime.parse(openingTimeString, formatter);
				LocalDateTime closingTime = LocalDateTime.parse(closingTimeString, formatter);

				homeworksOfGroup.add(new HomeworkDetails(rs.getInt(1), rs.getString(2), openingTime, closingTime,
						rs.getInt(5), rs.getString(6)));
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
			ResultSet rs = st
					.executeQuery(GET_ALL_GROUPS);
			while (rs.next()) {
				Group currGroup = new Group(rs.getInt(1), rs.getString(2));
				ArrayList<Teacher> teachersOfGroup = GroupDAO.getInstance().getTeachersOfGroup(currGroup);
				ArrayList<Student> studentsOfGroup = GroupDAO.getInstance().getStudentsOfGroup(currGroup);
				ArrayList<HomeworkDetails> homeworkDetailsOfGroup = GroupDAO.getInstance()
						.getHomeworkDetailsOfGroup(currGroup);
				groups.add(new Group(rs.getInt(1), rs.getString(2), teachersOfGroup, studentsOfGroup,
						homeworkDetailsOfGroup));
			}
		} catch (SQLException e) {
			throw new UserException("Something went wrong with getting groups.." + e.getMessage());
		}

		return groups;
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#removeUserFromGroup(com.IttalentsHomeworks.model.Group, com.IttalentsHomeworks.model.Student)
	 */
	@Override
	public void removeUserFromGroup(Group group, int userId) throws GroupException, UserException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(
					REMOVE_USER_FROM_GROUP);
			ps.setInt(1, userId);
			ps.setInt(2, group.getId());
			ps.execute();
			//if we remove user from group we don't remove his homeworks and solutions,
			//if we decide to add him back to the group later it would be better if we kept them
			/*if(!user.isTeacher()){
				
				for(HomeworkDetails hd: GroupDAO.getInstance().getAllHomeworksDetails()){
					if(((GroupDAO) GroupDAO.getInstance()).doesStudentAlreadyHaveHomework(user,hd)){
						//ako doma6noto e samo kam tazi grupa
						if(GroupDAO.getInstance().get)
						UserDAO.getInstance().removeHomeworkFromStudent(user,hd);
					}
				}
			}*/
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with removing a student from a group..");
		}

	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#removeGroup(com.IttalentsHomeworks.model.Group)
	 */
	@Override
	public void removeGroup(Group group) throws GroupException {
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(REMOVE_GROUP);
			ps.setInt(1, group.getId());
			ps.execute();
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with removing a group.." + e.getMessage());
		}
	}

	@Override
	public boolean isHomeworkHeadingUnique(String heading){
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM IttalentsHomeworks.Homework WHERE heading = ?");
			ps.setString(1, heading);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}
	//TODO pri update da gledam hem dali e unikalno, no ako e kato minaloto mu nqma problem
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#createHomeworkForGroup(com.IttalentsHomeworks.model.HomeworkDetails, java.util.ArrayList)
	 */
	@Override
	public void createHomeworkDetails(HomeworkDetails homeworkDetails, ArrayList<Group> groupsForHomework)
			throws GroupException, UserException, ValidationException, NotUniqueUsernameException {
		Connection con = manager.getConnection();
		// con.setAutoCommit(false);
		if(ValidationsDAO.getInstance().isHomeworkHeadingUniqueAddHomework(homeworkDetails.getHeading())){
		if (homeworkDetails.getTasksFile() != null && !(homeworkDetails.getTasksFile().trim().equals(""))
				&& groupsForHomework != null && groupsForHomework.size() > 0) {
			if (!ValidationsDAO.getInstance().isThereEmptyFieldAddHomework(homeworkDetails.getHeading(),
					homeworkDetails.getOpeningTime().toString(), homeworkDetails.getClosingTime().toString(),
					homeworkDetails.getNumberOfTasks())) {
				if(ValidationsDAO.getInstance().isLengthHeadingValidAddHomework(homeworkDetails.getHeading()) && ValidationsDAO.getInstance().isHomeworkOpeningTimeValidAddHomework(homeworkDetails.getOpeningTime().toString()) && ValidationsDAO.getInstance().isHomeworkClosingTimeValidAddHomework(homeworkDetails.getOpeningTime().toString(), homeworkDetails.getClosingTime().toString()) && ValidationsDAO.getInstance().isHomeworkNumberOfTasksValidAddHomework(homeworkDetails.getNumberOfTasks())){
				try {
					// con.setAutoCommit(false);
					PreparedStatement ps = con.prepareStatement(CREATE_HOMEWORK_DETAILS);
					ps.setString(1, homeworkDetails.getHeading());
					ps.setString(2, homeworkDetails.getOpeningTime().toString());
					ps.setString(3, homeworkDetails.getClosingTime().toString());
					ps.setInt(4, homeworkDetails.getNumberOfTasks());
					ps.setString(5, homeworkDetails.getTasksFile());
					ps.execute();
					homeworkDetails.setId(GroupDAO.getInstance().getHomeworkDetailsId(homeworkDetails));
					// con.setAutoCommit(false);

					for (Group group : groupsForHomework) {
						// con.setAutoCommit(false);
						GroupDAO.getInstance().addHomeworkToGroup(homeworkDetails, group);

					}

					// con.commit();
				} catch (SQLException e) {
					// con.setAutoCommit(false);
					// con.rollback();
					System.out.println(e.getMessage());
					throw new GroupException("Something went wrong with creating a homework..");
				} finally {
					// con.setAutoCommit(true);
				}
				}else{
					throw new ValidationException("Add homework --> invalid field");

				}
			}else{
				throw new ValidationException("Add homework --> empty field");
			}
		}else{
			throw new ValidationException("Add homework --> empty field");
		}
		}else{
			throw new NotUniqueUsernameException("Add homework --> not unique username");
		}
		
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getHomeworkDetailsId(com.IttalentsHomeworks.model.HomeworkDetails)
	 */
	@Override
	public int getHomeworkDetailsId(HomeworkDetails homeworkDetails) throws GroupException {
		Connection con = manager.getConnection();
		int homeworkDetailsId = 0;
		try {
			PreparedStatement ps = con
					.prepareStatement(GET_HOMEWORK_DETAILS_ID);
			ps.setString(1, homeworkDetails.getHeading());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				homeworkDetailsId = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with checking the id of a homework..");
		}
		return homeworkDetailsId;
	}

	//updateHomework
	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#updateHomeworkDetails(com.IttalentsHomeworks.model.HomeworkDetails, java.util.ArrayList)
	 */
	@Override
	public void updateHomeworkDetails(HomeworkDetails homeworkDetails, ArrayList<Group> groupsForHomework)
			throws GroupException, UserException, ValidationException, NotUniqueUsernameException {
		Connection con = manager.getConnection();// get id
		//con.setAutoCommit(false);
		HomeworkDetails currHd = GroupDAO.getInstance().getHomeworkDetailsById(homeworkDetails.getId());

		if(ValidationsDAO.getInstance().isHomeworkUpdateHeadingUnique(homeworkDetails.getHeading(), currHd)){
		
		if (homeworkDetails.getTasksFile() != null && !(homeworkDetails.getTasksFile().trim().equals(""))
				&& groupsForHomework != null && groupsForHomework.size() > 0) {
		if(!ValidationsDAO.getInstance().updateGroupAreThereEmptyFields(homeworkDetails.getHeading(), homeworkDetails.getOpeningTime().toString(), homeworkDetails.getClosingTime().toString(), homeworkDetails.getTasksFile().trim())){
			if(ValidationsDAO.getInstance().areHomeworkUpdateCharactersValid(homeworkDetails.getHeading()) && ValidationsDAO.getInstance().isHomeworkUpdateLengthValid(homeworkDetails.getHeading()) && ValidationsDAO.getInstance().isHomeworkUpdateOpeningTimeValid(homeworkDetails.getOpeningTime().toString(), currHd) && ValidationsDAO.getInstance().isHomeworkUpdateClosingTimeValid(homeworkDetails.getOpeningTime().toString(), homeworkDetails.getClosingTime().toString(), currHd) && ValidationsDAO.getInstance().isHomeworkUpdateNumberOfTasksValid(homeworkDetails.getNumberOfTasks())){
			try {
			PreparedStatement ps = con.prepareStatement(
					UPDATE_HOMEWORK_DETAILS);
			ps.setString(1, homeworkDetails.getHeading());
			ps.setString(2, homeworkDetails.getOpeningTime().toString());
			ps.setString(3, homeworkDetails.getClosingTime().toString());
			ps.setInt(4, homeworkDetails.getNumberOfTasks());
			ps.setString(5, homeworkDetails.getTasksFile());
			ps.setInt(6, homeworkDetails.getId());
			ps.executeUpdate();
			ArrayList<Integer> currGroupIds = GroupDAO.getInstance().getIdsOfGroupsForWhichIsHomework(homeworkDetails);
			ArrayList<Integer> wishedGroupIds = new ArrayList<>();
			for (Group g : groupsForHomework) {
				wishedGroupIds.add(g.getId());
			}
			for (Integer id : currGroupIds) {
				if (!(wishedGroupIds.contains(id))) {
					GroupDAO.getInstance().removeHomeworkFromGroup(homeworkDetails, GroupDAO.getInstance().getGroupById(id));
				}
			}
			for (Integer id : wishedGroupIds) {
				if (!(currGroupIds.contains(id))) {
					//if (!(GroupDAO.getInstance().getIdsOfGroupsForWhichIsHomework(homeworkDetails).contains(id))) {
						GroupDAO.getInstance().addHomeworkToGroup(homeworkDetails,
								GroupDAO.getInstance().getGroupById(id));
					//}
				}
			}
			//con.commit();
		} catch (SQLException e) {
			//con.rollback();
			throw new GroupException("Something went wrong with updating homework details..");
		} finally {
					// con.setAutoCommit(true);
				}
			} else {

					throw new ValidationException("Update homework --> invalid field");
				}
			} else {

				throw new ValidationException("Update homework --> empty field");
			}
		
		} else {

			throw new ValidationException("Update homework --> empty field");

		}}else{
			throw new NotUniqueUsernameException("Add homework --> not unique username");
		}

	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getIdsOfGroupsForWhichIsHw(com.IttalentsHomeworks.model.HomeworkDetails)
	 */
	@Override
	public ArrayList<Integer> getIdsOfGroupsForWhichIsHomework(HomeworkDetails homeworkDetails) throws GroupException {
		ArrayList<Integer> groupsIds = new ArrayList<>();
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(
					GET_IDS_OF_GROUPS_FOR_WHICH_IS_HOMEWORK);
			ps.setInt(1, homeworkDetails.getId());
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
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#removeHomeworkFromGroup(com.IttalentsHomeworks.model.HomeworkDetails, com.IttalentsHomeworks.model.Group)
	 */
	@Override//TODO add transaction
	public void removeHomeworkFromGroup(HomeworkDetails homeworkDetails, Group group) throws GroupException, UserException {
		Connection con = manager.getConnection();
		//try {
			//con.setAutoCommit(false);
			try {
				PreparedStatement ps = con.prepareStatement(
						REMOVE_HOMEWORK_FROM_GROUP_I);
				ps.setInt(1, group.getId());
				ps.setInt(2, homeworkDetails.getId());
				ps.execute();
				/*for (Student s : GroupDAO.getInstance().getStudentsOfGroup(group)) {
					ps = con.prepareStatement(REMOVE_HOMEWORK_FROM_GROUP_II);
					ps.setInt(1, s.getId());
					ps.setInt(2, homeworkDetails.getId());
					ps.execute();
					
					for (int i = 0; i < homeworkDetails.getNumberOfTasks(); i++) {
						Task t = new Task(i, null, null);
						if (UserDAO.getInstance().doesTaskAlreadyExist(homeworkDetails, s, i)) {
							ps = con.prepareStatement(
									REMOVE_HOMEWORK_FROM_GROUP_III);
							ps.setInt(1, s.getId());
							ps.setInt(2, homeworkDetails.getId());
							ps.execute();
						}
					}
				}*/
				//con.commit();
			} catch (SQLException e) {
			//	con.rollback();
				throw new GroupException("Something went wrong with removing a homework from group..");
			}/* finally {
				con.setAutoCommit(true);
			}
		} catch (SQLException e1) {
			throw new GroupException("Something went wrong with removing a homework from group..");
		}*/
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#addHomeworkToGroup(com.IttalentsHomeworks.model.HomeworkDetails, com.IttalentsHomeworks.model.Group)
	 */
	@Override//TODO add transaction
	public void addHomeworkToGroup(HomeworkDetails homeworkDetails, Group group) throws GroupException, UserException {
		Connection con = manager.getConnection();
		try {
			//con.setAutoCommit(false);
			//ftry {
				PreparedStatement ps = con
						.prepareStatement(ADD_HOMEWORK_TO_GROUP_I);
				ps.setInt(1, group.getId());
				ps.setInt(2, homeworkDetails.getId());
				ps.execute();

				for (Student s : GroupDAO.getInstance().getStudentsOfGroup(group)) {
					if(!((GroupDAO) GroupDAO.getInstance()).doesStudentAlreadyHaveHomework(s,homeworkDetails)){

					ps = con.prepareStatement(ADD_HOMEWORK_TO_GROUP_II);
					ps.setInt(1, s.getId());
					ps.setInt(2, homeworkDetails.getId());
					ps.execute();

					for (int i = 0; i < homeworkDetails.getNumberOfTasks(); i++) {

						Task t = new Task(i, null, null);
						if (!UserDAO.getInstance().doesTaskAlreadyExist(homeworkDetails, s, i)) {

							ps = con.prepareStatement(
									ADD_HOMEWORK_TO_GROUP_III);

							ps.setInt(1, s.getId());
							ps.setInt(2, homeworkDetails.getId());
							ps.setInt(3, t.getTaskNumber());
							ps.execute();

						}
					}
				}
				}
				//con.commit();
			} catch (SQLException e) {
			//	con.rollback();
				System.out.println(e.getMessage());
				throw new GroupException("Something went wrong with adding a homework to group..");
			} finally {
				//con.setAutoCommit(true);
			}
		/*} catch (SQLException e1) {
			System.out.println(e1.getMessage());
			throw new GroupException("Something went wrong with adding a homework to group..");
		}*/
	}

	/* (non-Javadoc)
	 * @see com.IttalentsHomeworks.DAO.IGroupDAO#getGroupById(int)
	 */
	@Override
	public Group getGroupById(int id) throws GroupException, UserException {
		Group group = null;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con
					.prepareStatement(GET_GROUP_BY_ID);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ArrayList<Teacher> teachers = GroupDAO.getInstance().getTeachersOfGroup(new Group(id, rs.getString(2)));
				ArrayList<Student> students = GroupDAO.getInstance().getStudentsOfGroup(new Group(id, rs.getString(2)));
				ArrayList<HomeworkDetails> homeworkDetails = GroupDAO.getInstance()
						.getHomeworkDetailsOfGroup(new Group(id, rs.getString(2)));

				group = new Group(rs.getInt(1), rs.getString(2), teachers, students, homeworkDetails);
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting the group by id..");
		}
		return group;
	}

	@Override
	public int getGroupIdByGroupName(String groupName) throws GroupException {
		int idGroup = 0;
		Connection con = manager.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(GET_GROUP_ID_BY_GROUP_NAME);
			ps.setString(1, groupName);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				idGroup = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting id of group..");
		}
		
		return idGroup;
	}

	@Override
	public void removeHomeworkDetails(HomeworkDetails homeworkDetails) throws GroupException, UserException {
		Connection con = manager.getConnection();
		try {
			con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement(REMOVE_HOMEWORK_DETAILS);
			ps.setInt(1, homeworkDetails.getId());
			ArrayList<Integer> currGroupIds = GroupDAO.getInstance().getIdsOfGroupsForWhichIsHomework(homeworkDetails);
			ArrayList<Group> currGroups = new ArrayList<>();
			for (Integer id : currGroupIds) {
				currGroups.add(GroupDAO.getInstance().getGroupById(id));
			}
			ps.execute();

			File fileTasks = new File("/Users/Stela/Desktop/imagesIttalentsHomework" + File.separator + homeworkDetails.getTasksFile());
			if(fileTasks.exists()){
				System.out.println("DELETE FILE " + fileTasks.getAbsolutePath());
				fileTasks.delete();
			}
			for (Group group : currGroups) {	

				for (Student s : GroupDAO.getInstance().getStudentsOfGroup(group)) {
					for (int i = 0; i < homeworkDetails.getNumberOfTasks(); i++) {
						String fileName = "hwId" + homeworkDetails.getId() + "userId" + s.getId() + "taskNum" + i
								+ ".java";
						File fileStudentTasks = new File(
								"/Users/Stela/Desktop/imagesIttalentsHomework" + File.separator + fileName);
						if (fileStudentTasks.exists()) {
							fileStudentTasks.delete();
						}
					}
				}
			}
			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new GroupException("Something went wrong with removing homework details rollback..");

			}
			throw new GroupException("Something went wrong with removing homework details..");
		}finally{
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				throw new GroupException("Something went wrong with removing homework details rollback..");
			}
		}
	}
	
	@Override
	public void updateGroup(Group group, ArrayList<Integer> wishedTeacherIds) throws GroupException, ValidationException{
		Connection con = manager.getConnection();
		if(!(ValidationsDAO.getInstance().isThereGroupEmptyFieldUpdate(group.getName())) && ValidationsDAO.getInstance().isGroupNameUniqueUpdate(group.getId(), group.getName()) && ValidationsDAO.getInstance().isGroupNameValid(group.getName())){
		try {
			PreparedStatement ps = con.prepareStatement(CHANGE_GROUP_NAME);
			ps.setString(1, group.getName());
			ps.setInt(2, group.getId());
			ps.executeUpdate();
			//ne e u4itel
			//curr teachers
			ArrayList<Teacher> currTeachers = GroupDAO.getInstance().getTeachersOfGroup(group);
			ArrayList<Integer> currTeachersIds = new ArrayList<>();
			for (Teacher t : currTeachers) {
				currTeachersIds.add(t.getId());
			}
			//wished teachers
			ArrayList<Integer> wishedTeachersIds = new ArrayList<>();
			wishedTeachersIds.addAll(wishedTeacherIds);
			if(currTeachersIds != null){
			for (Integer teacherId : currTeachersIds) {
				if (!(wishedTeachersIds.contains(teacherId))) {
					GroupDAO.getInstance().removeUserFromGroup(group, teacherId);
				}
			}
			}
			if(wishedTeachersIds != null){

			for (Integer id : wishedTeachersIds) {
				if (!(currTeachersIds.contains(id))) {
					User currTeacher = UserDAO.getInstance().getUserById(id);
						GroupDAO.getInstance().addUserToGroup(group, currTeacher);
				}
			}
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with changing the name of a group..");
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}else{
			throw new ValidationException("Update group --> invalid fields");
		}
	}

	@Override
	public HomeworkDetails getHomeworkDetailsById(int chosenHomeworkId) throws GroupException {
		Connection con = manager.getConnection();
		HomeworkDetails homeworkDetails = null;
		try {
			PreparedStatement ps = con.prepareStatement("SELECT id,heading,opens,closes,num_of_tasks,tasks_pdf FROM IttalentsHomeworks.Homework WHERE id = ?;");
			ps.setInt(1, chosenHomeworkId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String openingTimeString = rs.getString(3);
				String closingTimeString = rs.getString(4);

				LocalDateTime openingTime = LocalDateTime.parse(openingTimeString, formatter);
				LocalDateTime closingTime = LocalDateTime.parse(closingTimeString, formatter);
				//String removeTOpens = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(openingTime);
			//	String removeTCloses = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(closingTime);
				
				//DateTimeFormatter formatterRemoveT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
				//LocalDateTime openingTimeRemoveT = LocalDateTime.parse(openingTime.toString().replace("T", " "), formatter);
				//System.out.println("ETO " + openingTimeRemoveT);
			//	LocalDateTime closingTimeRemoveT = LocalDateTime.parse(closingTime.toString(), formatterRemoveT);
				homeworkDetails = new HomeworkDetails(rs.getInt(1), rs.getString(2), openingTime, closingTime, rs.getInt(5), rs.getString(6));
			}
		} catch (SQLException e) {
			throw new GroupException("Something went wrong with getting homework details..");
		}
		return homeworkDetails;
	}
}
