package com.IttalentsHomeworks.jUnitTests;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.IttalentsHomeworks.DAO.ConfigAutowire;
import com.IttalentsHomeworks.DAO.GroupDAO;
import com.IttalentsHomeworks.DAO.NotUniqueHomeworkHeadingException;
import com.IttalentsHomeworks.DAO.UserDAO;
import com.IttalentsHomeworks.DAO.ValidationsDAO;
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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConfigAutowire.class})
public class TestDAO {

	//AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigAutowire.class);
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private GroupDAO groupDAO;
	
	
	@Autowired
	private ValidationsDAO validationsDAO;
	
	
	
	
	
	
	
	static User user1 = new Student("user22221", "1222234", "1222234", "email@user1");
	static Teacher user2 = new Teacher("user2", "1222234", "1222234", "email@user1");;
	static Teacher user2ToAddToGroupUpdate1 = new Teacher("user2ToAddToGroupUpdate1", "1222234", "1222234", "email@user1");
	static Teacher user2ToAddToGroupUpdate2 = new Teacher("user2ToAddToGroupUpdate2", "1222234", "1222234", "email@user1");;
	ArrayList<Teacher> teachersForGroup = new ArrayList<>();
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	static String openingString = LocalDateTime.now().plusDays(1)
			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	static LocalDateTime opening = LocalDateTime.parse(openingString, formatter);
	static String closingString = LocalDateTime.now().plusDays(3)
			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

	static LocalDateTime closing = LocalDateTime.parse(closingString, formatter);
	static HomeworkDetails hd = new HomeworkDetails("test_homework", opening, closing, 5, "testTasks.pdf", "");
	static Group group1 = null;

	@Test
	public void test01isUsernameUnique() throws UserException, GroupException, NoSuchAlgorithmException, ValidationException {
		String username = user1.getUsername();
		boolean validAnser = true;
		boolean answer = validationsDAO.isUsernameUnique(username);
		assertEquals(validAnser, answer);
	}

	@Test
	public void test02createUser() throws UserException, GroupException, NoSuchAlgorithmException, ValidationException {
		userDAO.createNewUser(user1);
		user1.setId(userDAO.getUserIdByUsername(user1.getUsername()));	
		User userReturned = userDAO.getUserByUsername(user1.getUsername());
		String username = user1.getUsername();
		String password = user1.getPassword();
		String email = user1.getEmail();
		String usernameReturned = userReturned.getUsername();
		String passwordReturned = userReturned.getPassword();
		String emailReturned = userReturned.getEmail();
		userDAO.createNewUser(user2);
		userDAO.createNewUser(user2ToAddToGroupUpdate1);
		userDAO.createNewUser(user2ToAddToGroupUpdate2);
		user2.setId(userDAO.getUserIdByUsername(user2.getUsername()));
		user2ToAddToGroupUpdate1.setId(userDAO.getUserIdByUsername(user2ToAddToGroupUpdate1.getUsername()));
		user2ToAddToGroupUpdate2.setId(userDAO.getUserIdByUsername(user2ToAddToGroupUpdate2.getUsername()));
		assertEquals(username, usernameReturned);
		assertEquals(validationsDAO.encryptPass(password), passwordReturned);
		assertEquals(email, emailReturned);
	}

	@Test
	public void test03getUserIdByUsername() throws UserException, GroupException {
		int userId = userDAO.getUserIdByUsername(user1.getUsername());
		user1.setId(userId);
		User userReturned = userDAO.getUserByUsername(user1.getUsername());
		assertEquals(userId, userReturned.getId());
	}

	@Test
	public void test04isUserATeacher() throws UserException {
		boolean validAnswer = false;
		boolean answer = userDAO.isUserATeacher(user1.getId());
		assertEquals(validAnswer, answer);
	}

	@Test
	public void test05addTeacher() throws UserException, GroupException {
		user2 = (Teacher) userDAO.getUserByUsername("user2");
		user2ToAddToGroupUpdate1 = (Teacher) userDAO.getUserByUsername("user2ToAddToGroupUpdate1");
		user2ToAddToGroupUpdate2 = (Teacher) userDAO.getUserByUsername("user2ToAddToGroupUpdate2");
		boolean answer = userDAO.isUserATeacher(user2.getId());
		assertEquals(true, answer);
	}

	@Test
	public void test06createGroup() throws GroupException, UserException, ValidationException {
		teachersForGroup.add(user2);
		group1 = new Group("groupTest", teachersForGroup);
		groupDAO.createNewGroup(group1);
		group1.setId(groupDAO.getGroupIdByGroupName(group1.getName()));
		boolean contains = false;
		for (Group g : groupDAO.getAllGroups()) {
			if (g.getId() == group1.getId()) {
				contains = true;
			}
		}
		assertEquals(true, contains);
	}

	@Test
	public void test07removeTeacherFromGroup() throws GroupException, UserException {
		boolean isTeacherReturned = false;
		groupDAO.removeUserFromGroup(group1.getId(), user2.getId());
		for (Teacher t : groupDAO.getTeachersOfGroup(group1.getId())) {
			if (t.getId() == user2.getId()) {
				isTeacherReturned = true;
				break;
			}
		}
		assertEquals(false, isTeacherReturned);
	}

	@Test
	public void test08addTeacherToGroup() throws GroupException, UserException, ValidationException {
		boolean isTeacherReturned = false;
		groupDAO.addUserToGroup(group1.getId(), user2.getId());
		for (Teacher t : groupDAO.getTeachersOfGroup(group1.getId())) {
			if (t.getId() == user2.getId()) {
				isTeacherReturned = true;
				break;
			}
		}
		assertEquals(true, isTeacherReturned);
	}

	@Test
	public void test09isUserAlreadyInGroup() throws GroupException, UserException {
		boolean isStudentReturned = groupDAO.isUserAlreadyInGroup(group1.getId(), user1.getUsername());
		assertEquals(false, isStudentReturned);
		boolean isTeacherReturned = groupDAO.isUserAlreadyInGroup(group1.getId(), user2.getUsername());
		assertEquals(true, isTeacherReturned);
	}

	@Test
	public void test10addStudentToGroup() throws GroupException, UserException, ValidationException {
		boolean isStudentReturned = false;
		groupDAO.addUserToGroup(group1.getId(), user1.getId());
		for (Student s : groupDAO.getStudentsOfGroup(group1.getId())) {
			if (s.getId() == user1.getId()) {
				isStudentReturned = true;
				break;
			}
		}
		assertEquals(true, isStudentReturned);
	}

	@Test
	public void test11removeStudentFromGroup() throws GroupException, UserException, ValidationException {
		boolean isStudentReturned = false;
		groupDAO.removeUserFromGroup(group1.getId(), user1.getId());
		for (Student s : groupDAO.getStudentsOfGroup(group1.getId())) {
			if (s.getId() == user1.getId()) {
				isStudentReturned = true;
				break;
			}
		}
		// we add the student again
		groupDAO.addUserToGroup(group1.getId(), user1.getId());
		assertEquals(false, isStudentReturned);
	}

	@Test
	public void test12isGroupNameUnique() throws GroupException {
		boolean isUnique = validationsDAO.isGroupNameUnique(group1.getName());
		assertEquals(false, isUnique);
	}

	@Test
	public void test13getGroupsOfUser() throws UserException, GroupException {
		ArrayList<Group> groupsOfUser = userDAO.getGroupsOfUser(user1.getId());
		assertEquals(1, groupsOfUser.size());
		assertEquals(group1.getId(), groupsOfUser.get(0).getId());
	}

	@Test
	public void test14createHomeworkForGroup() throws GroupException, UserException, ValidationException,
			NotUniqueUsernameException, NotUniqueHomeworkHeadingException {
		ArrayList<Integer> groupsForHw = new ArrayList<>();
		groupsForHw.add(group1.getId());
		groupDAO.createHomeworkDetails(hd, groupsForHw);
		int hwId = groupDAO.getHomeworkDetailsId(hd.getHeading());
		hd.setId(hwId);
		boolean isInHomeworksTable = false;
		for (HomeworkDetails hd1 : groupDAO.getAllHomeworksDetails()) {
			if (hd1.getHeading().equals(hd.getHeading())) {
				isInHomeworksTable = true;
				break;
			}
		}
		assertEquals(true, isInHomeworksTable);
		boolean isInGroupHomeworksTable = false;
		for (HomeworkDetails hd1 : groupDAO.getHomeworkDetailsOfGroup(group1.getId())) {
			if (hd1.getHeading().equals(hd.getHeading())) {
				isInGroupHomeworksTable = true;
				break;
			}
		}
		assertEquals(true, isInGroupHomeworksTable);
	}

	@Test
	public void test15removeHomeworkFromGroup() throws GroupException, UserException {
		groupDAO.removeHomeworkFromGroup(hd.getId(), group1.getId());
		boolean isInGroupHomeworksTable = false;
		for (HomeworkDetails hd1 : groupDAO.getHomeworkDetailsOfGroup(group1.getId())) {
			if (hd1.getHeading().equals(hd.getHeading())) {
				isInGroupHomeworksTable = true;
				break;
			}
		}
		assertEquals(false, isInGroupHomeworksTable);
	}

	@Test
	public void test16addHomeworkToGroup() throws GroupException, UserException {
		groupDAO.addHomeworkToGroup(hd, group1.getId());
		boolean isInGroupHomeworksTable = false;
		for (HomeworkDetails hd1 : groupDAO.getHomeworkDetailsOfGroup(group1.getId())) {
			if (hd1.getHeading().equals(hd.getHeading())) {
				isInGroupHomeworksTable = true;
				break;
			}
		}
		assertEquals(true, isInGroupHomeworksTable);
	}

	@Test
	public void test18addSolutionToTask() throws UserException {
		ArrayList<Task> tasksOfHw = userDAO.getTasksOfHomeworkOfStudent(user1.getId(), hd.getId());
		assertEquals(null, tasksOfHw.get(0).getSolution());
		userDAO.setSolutionOfTask(hd.getId(), user1.getId(), 0, "my_solution.java",
				LocalDateTime.of(2016, 12, 12, 18, 22, 13));
		tasksOfHw = userDAO.getTasksOfHomeworkOfStudent(user1.getId(), hd.getId());
		assertEquals("my_solution.java", tasksOfHw.get(0).getSolution());
	}

	@Test
	public void test19addUploadTomeTotask() throws UserException {
		ArrayList<Task> tasksOfHw = userDAO.getTasksOfHomeworkOfStudent(user1.getId(), hd.getId());
		assertEquals(LocalDateTime.of(2016, 12, 12, 18, 22, 13), tasksOfHw.get(0).getUploadedOn());

	}

	@Test
	public void test20addTeacherComment() throws UserException, ValidationException {
		ArrayList<Homework> homeworksOfStudent = userDAO.getHomeworksOfStudent(user1.getId());
		assertEquals("", homeworksOfStudent.get(0).getTeacherComment());
		userDAO.setTeacherComment(hd.getId(), user1.getId(), "not bad");
		homeworksOfStudent = userDAO.getHomeworksOfStudent(user1.getId());
		assertEquals("not bad", homeworksOfStudent.get(0).getTeacherComment());
	}

	@Test
	public void test21addTeacherGrade() throws UserException, ValidationException, GroupException {
		ArrayList<Homework> homeworksOfStudentByGroup = userDAO
				.getHomeworksOfStudentByGroup(user1.getId(), group1.getId());
		assertEquals(0, homeworksOfStudentByGroup.get(0).getTeacherGrade());
		userDAO.setTeacherGrade(hd.getId(), user1.getId(), 12);
		homeworksOfStudentByGroup = userDAO.getHomeworksOfStudent(user1.getId());
		assertEquals(12, homeworksOfStudentByGroup.get(0).getTeacherGrade());
	}

	@Test
	public void test21updateHomework() throws GroupException, UserException, ValidationException,
			NotUniqueUsernameException, NotUniqueHomeworkHeadingException {
		String openingStringUpdated = LocalDateTime.now().plusDays(3)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		LocalDateTime openingUpdated = LocalDateTime.parse(openingStringUpdated, formatter);
		String closingStringUpdated = LocalDateTime.now().plusDays(4)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		LocalDateTime closingUpdated = LocalDateTime.parse(closingStringUpdated, formatter);

		for (HomeworkDetails hd1 : groupDAO.getAllHomeworksDetails()) {
			if (hd1.getId() == hd.getId()) {
				assertEquals("test_homework", hd.getHeading());
				assertEquals(opening, hd.getOpeningTime());
				assertEquals(closing, hd.getClosingTime());
				assertEquals(5, hd.getNumberOfTasks());
				assertEquals("testTasks.pdf", hd.getTasksFile());
			}
		}
		ArrayList<Integer> groupsforHomework = new ArrayList<>();
		groupsforHomework.add(group1.getId());
		HomeworkDetails updatedHomework = new HomeworkDetails(hd.getId(), "new heading1221135", openingUpdated,
				closingUpdated, 5, "newFile.pdf", "");
		groupDAO.updateHomeworkDetails(updatedHomework, groupsforHomework);
		ArrayList<HomeworkDetails> newHd = groupDAO.getHomeworkDetailsOfGroup(group1.getId());
		for (HomeworkDetails hd1 : groupDAO.getAllHomeworksDetails()) {
			if (hd1.getId() == updatedHomework.getId()) {
				assertEquals("new heading1221135", newHd.get(0).getHeading());
				assertEquals(openingUpdated, newHd.get(0).getOpeningTime());
				assertEquals(closingUpdated, newHd.get(0).getClosingTime());
				assertEquals(5, newHd.get(0).getNumberOfTasks());
				assertEquals("newFile.pdf", newHd.get(0).getTasksFile());
			}
		}
		hd = updatedHomework;
	}

	@Test
	public void test22removeHomeworkDetails() throws GroupException, UserException {
		ArrayList<Group> groupsForHw = new ArrayList<>();
		groupsForHw.add(group1);
		int hwId = groupDAO.getHomeworkDetailsId(hd.getHeading());
		hd.setId(hwId);
		groupDAO.removeHomeworkDetails(hd);
		boolean isInHomeworksTable = false;
		for (HomeworkDetails hd1 : groupDAO.getAllHomeworksDetails()) {
			if (hd1.getHeading().equals(hd.getHeading())) {
				isInHomeworksTable = true;
				break;
			}
		}
		assertEquals(false, isInHomeworksTable);
		boolean isInGroupHomeworksTable = false;
		for (HomeworkDetails hd1 : groupDAO.getHomeworkDetailsOfGroup(group1.getId())) {
			if (hd1.getHeading().equals(hd.getHeading())) {
				isInGroupHomeworksTable = true;
				break;
			}
		}
		assertEquals(false, isInGroupHomeworksTable);
	}

	@Test
	public void test23updateUser() throws UserException, GroupException, NoSuchAlgorithmException, ValidationException {
		User updatedUser = new Student(user1.getUsername(), "newPass123", "newPass123", "newEmail@abv.bg");
		userDAO.updateUser(updatedUser, user1.getPassword());
		User newUser = userDAO.getUserByUsername(user1.getUsername());
		assertEquals(user1.getUsername(), newUser.getUsername());
		assertEquals(validationsDAO.encryptPass(updatedUser.getPassword()), newUser.getPassword());
		assertEquals(updatedUser.getEmail(), newUser.getEmail());
		user1 = newUser;
	}

	@Test
	public void test24updateGroup() throws GroupException, UserException, ValidationException {
		String name = "newName";
		Group updatedGroup = new Group(group1.getId(), name);
		// curr t - user2
		ArrayList<Teacher> currTeachers = groupDAO.getTeachersOfGroup(group1.getId());
		updatedGroup.setTeachers(currTeachers);
		ArrayList<Integer> currTeachersIds = new ArrayList<>();
		for (Teacher t : currTeachers) {
			currTeachersIds.add(t.getId());
		}
		// wished t
		ArrayList<Integer> wishedTeachersIds = new ArrayList<>();
		wishedTeachersIds.add(user2ToAddToGroupUpdate1.getId());
		wishedTeachersIds.add(user2ToAddToGroupUpdate2.getId());
		// set the wished teachers
		groupDAO.updateGroup(updatedGroup, wishedTeachersIds);
		Group getUpdatedGroup = groupDAO.getGroupById(updatedGroup.getId());
		// we return the curr teachers now and we check if group contains them
		// all
		boolean areWishedTeachersInGroupAndCurrNot = false;
		ArrayList<Teacher> currTeachersInUpdatedGroup = groupDAO
				.getTeachersOfGroup(getUpdatedGroup.getId());
		ArrayList<Integer> currTeachersIdsInUpdatedGroup = new ArrayList<>();

		for (Teacher t : currTeachersInUpdatedGroup) {
			currTeachersIdsInUpdatedGroup.add(t.getId());
		}
		if (currTeachersIdsInUpdatedGroup.contains(user2ToAddToGroupUpdate1.getId())
				&& currTeachersIdsInUpdatedGroup.contains(user2ToAddToGroupUpdate2.getId())
				&& (!(currTeachersIdsInUpdatedGroup.contains(user2.getId())))) {
			areWishedTeachersInGroupAndCurrNot = true;
		}
		assertEquals(true, areWishedTeachersInGroupAndCurrNot);

		groupDAO.updateGroup(updatedGroup, currTeachersIds);
		Group getUpdatedGroup1 = groupDAO.getGroupById(updatedGroup.getId());
		// we return the curr teachers now and we check if group contains them
		// all
		boolean areWishedTeachersInGroupAndCurrNot1 = false;

		ArrayList<Teacher> currTeachersInUpdatedGroup1 = groupDAO
				.getTeachersOfGroup(getUpdatedGroup1.getId());
		ArrayList<Integer> currTeachersIdsInUpdatedGroup1 = new ArrayList<>();
		for (Teacher t : currTeachersInUpdatedGroup1) {
			currTeachersIdsInUpdatedGroup1.add(t.getId());
		}
		if (!(currTeachersIdsInUpdatedGroup1.contains(user2ToAddToGroupUpdate1.getId()))
				&& (!(currTeachersIdsInUpdatedGroup1.contains(user2ToAddToGroupUpdate2.getId())))
				&& (currTeachersIdsInUpdatedGroup1.contains(user2.getId()))) {
			areWishedTeachersInGroupAndCurrNot1 = true;
		}
		assertEquals(true, areWishedTeachersInGroupAndCurrNot1);
		Group returnGroup = groupDAO.getGroupById(group1.getId());
		assertEquals("newName", returnGroup.getName());
	}

	@Test
	public void test25doesUserExistInDB() throws UserException, NoSuchAlgorithmException {
		boolean doesExists = validationsDAO.doesUserExistInDB(user1.getUsername(), "newPass123");
		assertEquals(true, doesExists);
	}

	@Test
	public void test26removeGroup() throws UserException, GroupException {
		groupDAO.removeGroup(group1.getId());
		Group g = groupDAO.getGroupById(group1.getId());
		assertEquals(null, g);
	}

	@Test
	public void test27removeUserProfile() throws UserException, GroupException {
		userDAO.removeUserProfile(user1.getId());
		userDAO.removeUserProfile(user2.getId());
		userDAO.removeUserProfile(user2ToAddToGroupUpdate1.getId());
		userDAO.removeUserProfile(user2ToAddToGroupUpdate2.getId());
		User userIdReturned = userDAO.getUserByUsername(user1.getUsername());
		assertEquals(null, userIdReturned);
	}

}
