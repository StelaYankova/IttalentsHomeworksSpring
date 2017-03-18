package com.IttalentsHomeworks.jUnitTests;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.IttalentsHomeworks.DAO.GroupDAO;
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

public class TestDAO {

	static User user1 = new Student("user22221", "1222234", "1222234", "email@user1");
	static Teacher user2 = null;
	static Teacher user2ToAddToGroupUpdate1 = null;
	static Teacher user2ToAddToGroupUpdate2 = null;
	ArrayList<Teacher> teachersForGroup = new ArrayList<>();
	 static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	 static String openingString = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	static LocalDateTime opening =  LocalDateTime.parse(openingString, formatter);
	 static String closingString = LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

	static LocalDateTime closing = LocalDateTime.parse(closingString, formatter);
	static HomeworkDetails hd = new HomeworkDetails("test_homework", opening, closing, 5, "testTasks.pdf");
	static Group group1 = null;
	@Test 
	public void test01isUsernameUnique() throws UserException, GroupException{		
		user1.setId(UserDAO.getInstance().getUserIdByUsername(user1.getUsername()));

		//UserDAO.getInstance().removeUserProfile(user1);

		String username = user1.getUsername();
		boolean validAnser = true;
		boolean answer = ValidationsDAO.getInstance().isUsernameUnique(username);
		assertEquals(validAnser, answer);
	}
	
	@Test 
	public void test02createUser() throws UserException, GroupException, NoSuchAlgorithmException, ValidationException{
		UserDAO.getInstance().createNewUser(user1);
		User userReturned = UserDAO.getInstance().getUserByUsername(user1.getUsername());
		String username = user1.getUsername();
		String password = user1.getPassword();
		String email = user1.getEmail();
		String usernameReturned = userReturned.getUsername();
		String passwordReturned = userReturned.getPassword();
		String emailReturned = userReturned.getEmail();
		assertEquals(username,usernameReturned);
		assertEquals(ValidationsDAO.getInstance().encryptPass(password), passwordReturned);
		assertEquals(email, emailReturned);
	}
	
	@Test 
	public void test03getUserIdByUsername() throws UserException, GroupException{
		int userId = UserDAO.getInstance().getUserIdByUsername(user1.getUsername());
		user1.setId(userId);
		User userReturned = UserDAO.getInstance().getUserByUsername(user1.getUsername());
		assertEquals(userId, userReturned.getId());
	}
	
	@Test
	public void test04isUserATeacher() throws UserException{
		boolean validAnswer = false;
		boolean answer = UserDAO.getInstance().isUserATeacher(user1.getId());
		assertEquals(validAnswer, answer);
	}
	
	@Test
	public void test05addTeacher() throws UserException, GroupException{
		user2 = (Teacher) UserDAO.getInstance().getUserByUsername("user.2");
		user2ToAddToGroupUpdate1 = (Teacher) UserDAO.getInstance().getUserByUsername("user2ToAddToGroupUpdate1");
		user2ToAddToGroupUpdate2 = (Teacher) UserDAO.getInstance().getUserByUsername("user2ToAddToGroupUpdate2");
		boolean answer = UserDAO.getInstance().isUserATeacher(user2.getId());
		assertEquals(true, answer);
	}
	
	@Test
	public void test06createGroup() throws GroupException, UserException, ValidationException{
		teachersForGroup.add(user2);
		group1 = new Group("groupTest", teachersForGroup);
		GroupDAO.getInstance().createNewGroup(group1);
		group1.setId(GroupDAO.getInstance().getGroupIdByGroupName(group1.getName()));
	
		boolean contains = false;
		for(Group g: GroupDAO.getInstance().getAllGroups()){
			if(g.getId() == group1.getId()){
				contains = true;
			}
		}
		assertEquals(true, contains);
	}
	
	@Test
	public void test07removeTeacherFromGroup() throws GroupException, UserException{		
		boolean isTeacherReturned = false;
		GroupDAO.getInstance().removeUserFromGroup(group1,user2.getId());
		for(Teacher t: GroupDAO.getInstance().getTeachersOfGroup(group1)){
			if(t.getId() == user2.getId()){
				isTeacherReturned = true;
				break;
			}
		}
		assertEquals(false, isTeacherReturned);
	}
	
	@Test 
	public void test08addTeacherToGroup() throws GroupException, UserException, ValidationException{
		boolean isTeacherReturned = false;
		GroupDAO.getInstance().addUserToGroup(group1, user2.getId());
		for(Teacher t: GroupDAO.getInstance().getTeachersOfGroup(group1)){
			if(t.getId() == user2.getId()){
				isTeacherReturned = true;
				break;
			}
		}
		assertEquals(true, isTeacherReturned);
	}
	
	@Test
	public void test09isUserAlreadyInGroup() throws GroupException, UserException{
		boolean isStudentReturned = GroupDAO.getInstance().isUserAlreadyInGroup(group1, user1.getUsername());
		assertEquals(false, isStudentReturned);
		boolean isTeacherReturned = GroupDAO.getInstance().isUserAlreadyInGroup(group1, user2.getUsername());
		assertEquals(true, isTeacherReturned);
	}
	
	@Test 
	public void test10addStudentToGroup() throws GroupException, UserException, ValidationException{
		boolean isStudentReturned = false;
		GroupDAO.getInstance().addUserToGroup(group1, user1.getId());
		for(Student s: GroupDAO.getInstance().getStudentsOfGroup(group1)){
			if(s.getId() == user1.getId()){
				isStudentReturned = true;
				break;
			}
		}
		
		assertEquals(true, isStudentReturned);
	}
	
	@Test
	public void test11removeStudentFromGroup() throws GroupException, UserException, ValidationException{		
		boolean isStudentReturned = false;
		GroupDAO.getInstance().removeUserFromGroup(group1,user1.getId());
		for(Student s: GroupDAO.getInstance().getStudentsOfGroup(group1)){
			if(s.getId() == user1.getId()){
				isStudentReturned = true;
				break;
			}
		}
		//we add the student again
		GroupDAO.getInstance().addUserToGroup(group1, user1.getId());
		assertEquals(false, isStudentReturned);
	}
	
	@Test
	public void test12isGroupNameUnique() throws GroupException{
		boolean isUnique = ValidationsDAO.getInstance().isGroupNameUnique(group1.getName());
		assertEquals(false, isUnique);
	}
	
	@Test 
	public void test13getGroupsOfUser() throws UserException, GroupException{
		ArrayList<Group> groupsOfUser = UserDAO.getInstance().getGroupsOfUser(user1.getId());
		assertEquals(1, groupsOfUser.size());
		assertEquals(group1.getId(), groupsOfUser.get(0).getId());
	}
	
	@Test
	public void test14createHomeworkForGroup() throws GroupException, UserException, ValidationException, NotUniqueUsernameException{
		ArrayList<Group> groupsForHw = new ArrayList<>();
		groupsForHw.add(group1);
		GroupDAO.getInstance().createHomeworkDetails(hd, groupsForHw);
		int hwId = GroupDAO.getInstance().getHomeworkDetailsId(hd);
		hd.setId(hwId);

		boolean isInHomeworksTable = false;
		for(HomeworkDetails hd1: GroupDAO.getInstance().getAllHomeworksDetails()){
			if(hd1.getHeading().equals(hd.getHeading())){
				isInHomeworksTable = true;
				break;
			}
		}
		assertEquals(true, isInHomeworksTable);
		boolean isInGroupHomeworksTable = false;
		for(HomeworkDetails hd1: GroupDAO.getInstance().getHomeworkDetailsOfGroup(group1)){
			if(hd1.getHeading().equals(hd.getHeading())){
				isInGroupHomeworksTable = true;
				break;
			}
		}
		assertEquals(true, isInGroupHomeworksTable);
	}
	@Test
	public void test15removeHomeworkFromGroup() throws GroupException, UserException{
		GroupDAO.getInstance().removeHomeworkFromGroup(hd, group1);
		boolean isInGroupHomeworksTable = false;
		for(HomeworkDetails hd1: GroupDAO.getInstance().getHomeworkDetailsOfGroup(group1)){
			if(hd1.getHeading().equals(hd.getHeading())){
				isInGroupHomeworksTable = true;
				break;
			}
		}
		assertEquals(false, isInGroupHomeworksTable);
	}
	@Test
	public void test16addHomeworkToGroup() throws GroupException, UserException{
		GroupDAO.getInstance().addHomeworkToGroup(hd, group1);
		boolean isInGroupHomeworksTable = false;
		for(HomeworkDetails hd1: GroupDAO.getInstance().getHomeworkDetailsOfGroup(group1)){
			if(hd1.getHeading().equals(hd.getHeading())){
				isInGroupHomeworksTable = true;
				break;
			}
		}
		assertEquals(true, isInGroupHomeworksTable);
	}
	
	@Test
	public void test18addSolutionToTask() throws UserException{
		ArrayList<Task> tasksOfHw = UserDAO.getInstance().getTasksOfHomeworkOfStudent(user1.getId(), hd);
		assertEquals(null, tasksOfHw.get(0).getSolution());
		UserDAO.getInstance().setSolutionOfTask(hd, (Student)user1, 0, "my_solution.java", LocalDateTime.of(2016, 12, 12, 18, 22, 13));
		tasksOfHw = UserDAO.getInstance().getTasksOfHomeworkOfStudent(user1.getId(), hd);
		assertEquals("my_solution.java", tasksOfHw.get(0).getSolution());
	}
	
	@Test
	public void test19addUploadTomeTotask() throws UserException{
		ArrayList<Task> tasksOfHw = UserDAO.getInstance().getTasksOfHomeworkOfStudent(user1.getId(), hd);
		assertEquals(LocalDateTime.of(2016, 12, 12, 18, 22, 13), tasksOfHw.get(0).getUploadedOn());
		
	}
	
	@Test
	public void test20addTeacherComment() throws UserException, ValidationException{
		ArrayList<Homework> homeworksOfStudent = UserDAO.getInstance().getHomeworksOfStudent(user1.getId());
		assertEquals(" ", homeworksOfStudent.get(0).getTeacherComment());
		UserDAO.getInstance().setTeacherComment(hd, user1.getId(), "not bad");
		homeworksOfStudent = UserDAO.getInstance().getHomeworksOfStudent(user1.getId());
		assertEquals("not bad", homeworksOfStudent.get(0).getTeacherComment());
	}
	
	@Test
	public void test21addTeacherGrade() throws UserException, ValidationException, GroupException{
		ArrayList<Homework> homeworksOfStudentByGroup = UserDAO.getInstance().getHomeworksOfStudentByGroup(user1.getId(), group1);
		assertEquals(0, homeworksOfStudentByGroup.get(0).getTeacherGrade());
		UserDAO.getInstance().setTeacherGrade(hd, user1.getId(), 12);
		homeworksOfStudentByGroup = UserDAO.getInstance().getHomeworksOfStudent(user1.getId());
		assertEquals(12, homeworksOfStudentByGroup.get(0).getTeacherGrade());
	}
	@Test
	public void test21updateHomework() throws GroupException, UserException, ValidationException, NotUniqueUsernameException{
		String openingStringUpdated = LocalDateTime.now().plusDays(3)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		LocalDateTime openingUpdated = LocalDateTime.parse(openingStringUpdated, formatter);
		String closingStringUpdated = LocalDateTime.now().plusDays(4)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		LocalDateTime closingUpdated = LocalDateTime.parse(closingStringUpdated, formatter);

		for(HomeworkDetails hd1: GroupDAO.getInstance().getAllHomeworksDetails()){
			if(hd1.getId() == hd.getId()){
				assertEquals("test_homework", hd.getHeading());
				assertEquals(opening, hd.getOpeningTime());
				assertEquals(closing, hd.getClosingTime());
				assertEquals(5, hd.getNumberOfTasks());
				assertEquals("testTasks.pdf", hd.getTasksFile());
			}
		}
		ArrayList<Group> groupsforHomework = new ArrayList<>();
		groupsforHomework.add(group1);
		HomeworkDetails updatedHomework = new HomeworkDetails(hd.getId(), "new heading1221135", openingUpdated, closingUpdated, 5, "newFile.pdf");
		GroupDAO.getInstance().updateHomeworkDetails(updatedHomework, groupsforHomework);
		ArrayList<HomeworkDetails> newHd = GroupDAO.getInstance().getHomeworkDetailsOfGroup(group1);
		for(HomeworkDetails hd1: GroupDAO.getInstance().getAllHomeworksDetails()){
			if(hd1.getId() == updatedHomework.getId()){
				assertEquals( "new heading1221135", newHd.get(0).getHeading());
				assertEquals(openingUpdated, newHd.get(0).getOpeningTime());
				assertEquals(closingUpdated, newHd.get(0).getClosingTime());
				assertEquals(5, newHd.get(0).getNumberOfTasks());
				assertEquals("newFile.pdf", newHd.get(0).getTasksFile());
			}
		}
		hd = updatedHomework;
	}
	
	@Test
	public void test22removeHomeworkDetails() throws GroupException, UserException{
		ArrayList<Group> groupsForHw = new ArrayList<>();
		groupsForHw.add(group1);
		
		int hwId = GroupDAO.getInstance().getHomeworkDetailsId(hd);
		hd.setId(hwId);
		GroupDAO.getInstance().removeHomeworkDetails(hd);
		boolean isInHomeworksTable = false;
		for(HomeworkDetails hd1: GroupDAO.getInstance().getAllHomeworksDetails()){
			if(hd1.getHeading().equals(hd.getHeading())){
				isInHomeworksTable = true;
				break;
			}
		}
		assertEquals(false, isInHomeworksTable);
		boolean isInGroupHomeworksTable = false;
		for(HomeworkDetails hd1: GroupDAO.getInstance().getHomeworkDetailsOfGroup(group1)){
			if(hd1.getHeading().equals(hd.getHeading())){
				isInGroupHomeworksTable = true;
				break;
			}
		}
		assertEquals(false, isInGroupHomeworksTable);
	}
	
	@Test
	public void test23updateUser() throws UserException, GroupException, NoSuchAlgorithmException, ValidationException{
		User updatedUser = new Student(user1.getUsername(), "newPass123", "newPass123","newEmail@abv.bg");
		UserDAO.getInstance().updateUser(updatedUser, user1.getPassword());
		
		User newUser = UserDAO.getInstance().getUserByUsername(user1.getUsername());

		assertEquals(user1.getUsername(), newUser.getUsername());
		assertEquals(ValidationsDAO.getInstance().encryptPass(updatedUser.getPassword()), newUser.getPassword());
		assertEquals(updatedUser.getEmail(), newUser.getEmail());
		user1 = newUser;
	}
	
	@Test
	public void test24updateGroup() throws GroupException, UserException, ValidationException{
		String name = "newName";
		Group updatedGroup = new Group(group1.getId(), name);
		//curr t - user2
		ArrayList<Teacher> currTeachers = GroupDAO.getInstance().getTeachersOfGroup(group1);
		updatedGroup.setTeachers(currTeachers);
		ArrayList<Integer> currTeachersIds = new ArrayList<>();
		for(Teacher t : currTeachers){
			currTeachersIds.add(t.getId());
		}
		//wished t
		ArrayList<Integer> wishedTeachersIds = new ArrayList<>();
		wishedTeachersIds.add(user2ToAddToGroupUpdate1.getId());
		wishedTeachersIds.add(user2ToAddToGroupUpdate2.getId());
		
		//set the wished teachers
		GroupDAO.getInstance().updateGroup(updatedGroup, wishedTeachersIds);
		Group getUpdatedGroup = GroupDAO.getInstance().getGroupById(updatedGroup.getId());
		//we return the curr teachers now and we check if group contains them all
		boolean areWishedTeachersInGroupAndCurrNot = false;
		ArrayList<Teacher> currTeachersInUpdatedGroup = GroupDAO.getInstance().getTeachersOfGroup(getUpdatedGroup);
		ArrayList<Integer> currTeachersIdsInUpdatedGroup = new ArrayList<>();

		for(Teacher t: currTeachersInUpdatedGroup){
			currTeachersIdsInUpdatedGroup.add(t.getId());
		}
		if(currTeachersIdsInUpdatedGroup.contains(user2ToAddToGroupUpdate1.getId()) && currTeachersIdsInUpdatedGroup.contains(user2ToAddToGroupUpdate2.getId()) &&(!(currTeachersIdsInUpdatedGroup.contains(user2.getId()) ))){
			areWishedTeachersInGroupAndCurrNot = true;
		}
		assertEquals(true, areWishedTeachersInGroupAndCurrNot);
		
		GroupDAO.getInstance().updateGroup(updatedGroup, currTeachersIds);
		Group getUpdatedGroup1 = GroupDAO.getInstance().getGroupById(updatedGroup.getId());
		//we return the curr teachers now and we check if group contains them all
		boolean areWishedTeachersInGroupAndCurrNot1 = false;
		
		ArrayList<Teacher> currTeachersInUpdatedGroup1 = GroupDAO.getInstance().getTeachersOfGroup(getUpdatedGroup1);
		ArrayList<Integer> currTeachersIdsInUpdatedGroup1 = new ArrayList<>();
		for(Teacher t: currTeachersInUpdatedGroup1){
			currTeachersIdsInUpdatedGroup1.add(t.getId());
		}
		if(!(currTeachersIdsInUpdatedGroup1.contains(user2ToAddToGroupUpdate1.getId())) && (!(currTeachersIdsInUpdatedGroup1.contains(user2ToAddToGroupUpdate2.getId()))) && (currTeachersIdsInUpdatedGroup1.contains(user2.getId()))){
			areWishedTeachersInGroupAndCurrNot1 = true;
		}
		assertEquals(true, areWishedTeachersInGroupAndCurrNot1);
		
		Group returnGroup = GroupDAO.getInstance().getGroupById(group1.getId());
		assertEquals("newName", returnGroup.getName());
	}
	
	@Test
	public void test25doesUserExistInDB() throws UserException, NoSuchAlgorithmException{
		boolean doesExists = ValidationsDAO.getInstance().doesUserExistInDB(user1.getUsername(), "newPass123");
		assertEquals(true, doesExists);
	}
	@Test
	public void test26removeGroup() throws UserException, GroupException{
		GroupDAO.getInstance().removeGroup(group1);	
		Group g = GroupDAO.getInstance().getGroupById(group1.getId());
		assertEquals(null, g);
	}
	
	
	@Test
	public void test27removeUserProfile() throws UserException, GroupException{
		UserDAO.getInstance().removeUserProfile(user1);
		User userIdReturned = UserDAO.getInstance().getUserByUsername(user1.getUsername());
		assertEquals(null, userIdReturned);
	}
	
}
