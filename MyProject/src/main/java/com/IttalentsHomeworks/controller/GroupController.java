package com.IttalentsHomeworks.controller;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.IttalentsHomeworks.DAO.GroupDAO;
import com.IttalentsHomeworks.DAO.IValidationsDAO;
import com.IttalentsHomeworks.DAO.UserDAO;
import com.IttalentsHomeworks.DAO.ValidationsDAO;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.Exceptions.ValidationException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.HomeworkDetails;
import com.IttalentsHomeworks.model.Student;
import com.IttalentsHomeworks.model.Task;
import com.IttalentsHomeworks.model.Teacher;
import com.IttalentsHomeworks.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class GroupController {

	@RequestMapping(value = "/AddGroupServlet", method = RequestMethod.GET)
	protected String addGroupGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "addGroup";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/AddGroupServlet", method = RequestMethod.POST)
	protected String addGroupPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String groupName = request.getParameter("groupName").trim();
			// empty fields
			request.setAttribute("nameTry", groupName);
			String[] selectedTeachersUsername = request.getParameterValues("teachers");

			request.setAttribute("selectedTeachersUsernameTry", selectedTeachersUsername);

			boolean isNameUnique = false;
			
			if (isThereEmptyField(groupName)) {
				request.setAttribute("emptyFields", true);
			} else {
				// unique name
				try {
					if (isGroupNameUnique(groupName)) {
						isNameUnique = true;
					}

					request.setAttribute("uniqueName", isNameUnique);
					// valid name
					boolean isNameValid = false;
					if (isGroupNameValid(groupName)) {
						isNameValid = true;
					}
					request.setAttribute("validName", isNameValid);
					ArrayList<Teacher> allTeachers = (ArrayList<Teacher>) request.getServletContext()
							.getAttribute("allTeachers");
					ArrayList<String> allTeacherUsernames = new ArrayList<>();
					boolean allTeachersExist = true;

					if (selectedTeachersUsername != null) {
						for (Teacher teacher : allTeachers) {
							allTeacherUsernames.add(teacher.getUsername());
						}
						if (!doAllTeachersExist(selectedTeachersUsername, allTeacherUsernames)) {
							allTeachersExist = false;
						}
						request.setAttribute("allTeachersExist", allTeachersExist);
					}
					if (isNameUnique == true && isNameValid == true && allTeachersExist == true) {
						ArrayList<Teacher> allSelectedTeachers = new ArrayList<>();
						if (selectedTeachersUsername != null) {

							for (int i = 0; i < selectedTeachersUsername.length; i++) {
								Teacher t = null;
								t = (Teacher) UserDAO.getInstance().getUserByUsername(selectedTeachersUsername[i]);
								allSelectedTeachers.add(t);
							}
						}
						Group newGroup = new Group(groupName, allSelectedTeachers);

						GroupDAO.getInstance().createNewGroup(newGroup);
						request.setAttribute("invalidFields", false);

						ArrayList<Group> allGroupsUpdated = GroupDAO.getInstance().getAllGroups();
						request.getServletContext().setAttribute("allGroups", allGroupsUpdated);
						ArrayList<Teacher> allTeachersUpdated = UserDAO.getInstance().getAllTeachers();
						for (Teacher t : allTeachersUpdated) {
							t.setGroups(UserDAO.getInstance().getGroupsOfUser(t.getId()));
						}
						request.getServletContext().setAttribute("allTeachers", allTeachersUpdated);

					}
				} catch (GroupException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (ValidationException e) {
					request.setAttribute("invalidFields", true);
					return "addGroup";
				}
			}

			return "addGroup";
		}
		return "forbiddenPage";
	}

	private boolean isGroupNameUnique(String groupName) throws GroupException {
		if (ValidationsDAO.getInstance().isGroupNameUnique(groupName)) {
			return true;
		}
		return false;
	}

	private boolean isGroupNameValid(String groupName) {
		if (isLengthValid(groupName) && areCharactersValid(groupName)) {
			return true;
		}
		return false;
	}

	private boolean isLengthValid(String groupName) {
		if (groupName.length() >= IValidationsDAO.MIN_SIZE_OF_GROUP_NAME && groupName.length() <= IValidationsDAO.MAX_SIZE_OF_GROUP_NAME) {
			return true;
		}
		return false;
	}

	private boolean areCharactersValid(String groupName) {
		for (int i = 0; i < groupName.length(); i++) {
			if (!(((int) groupName.charAt(i) >= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_FROM && (int) groupName.charAt(i) <= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_TO))) {
				return false;
			}
		}
		return true;
	}

	private boolean isThereEmptyField(String groupName) {
		if (groupName == null || groupName.equals("")) {
			return true;
		}
		return false;
	}

	private boolean doAllTeachersExist(String[] selectedTeachersUsername, ArrayList<String> allTeachersUsernames) {
		boolean doAllTeachersExist = true;
		for (int i = 0; i < selectedTeachersUsername.length;) {
			if (!(allTeachersUsernames.contains(selectedTeachersUsername[i++]))) {
				doAllTeachersExist = false;
				break;
			}
		}
		return doAllTeachersExist;
	}

	@RequestMapping(value = "/AddStudentToGroupServlet", method = RequestMethod.GET)
	protected String addStudentToGroupGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "addStudentToGroup";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/AddStudentToGroupServlet", method = RequestMethod.POST)
	protected String addStudentToGroupPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String chosenGroupIdString = request.getParameter("chosenGroup").trim();
			String chosenStudentUsername = request.getParameter("selectedStudent").trim();
			// empty fields
			request.getSession().setAttribute("chosenUsernameTry", chosenStudentUsername);
			if (isThereEmptyField(chosenGroupIdString, chosenStudentUsername)) {
				request.getSession().setAttribute("emptyFields", true);
			} else {
				try {
					// does student exist
					boolean doesStudentExist = false;
					boolean isStudentInGroup = false;
					int chosenGroupId = Integer.parseInt(chosenGroupIdString);
					if (doesStudentExist(chosenStudentUsername)) {
						doesStudentExist = true;
					}
					request.getSession().setAttribute("doesStudentExist", doesStudentExist);
					if (doesStudentExist == true) {
						// is student already in group
						if (isStudentAlreadyInGroup(chosenGroupId, chosenStudentUsername)) {
							isStudentInGroup = true;
						}
					}
					request.getSession().setAttribute("isStudentInGroup", isStudentInGroup);

					boolean isGroupValid = false;
					if (doesGroupExist(chosenGroupId)) {
						isGroupValid = true;
					}
					request.getSession().setAttribute("validGroups", isGroupValid);
					if (doesStudentExist == true && isStudentInGroup == false && isGroupValid == true) {
						Group group = GroupDAO.getInstance().getGroupById(chosenGroupId);
						GroupDAO.getInstance().addUserToGroup(group, UserDAO.getInstance().getUserIdByUsername(chosenStudentUsername));
						request.getSession().setAttribute("invalidFields", false);
					}
				} catch (UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (GroupException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (ValidationException e) {
					request.getSession().setAttribute("invalidFields", true);
				} 
			}
			return "redirect:./AddStudentToGroupServlet";
		}
		return "forbiddenPage";
	}

	private boolean doesStudentExist(String username) throws UserException {
		if (ValidationsDAO.getInstance().isUsernameUnique(username)) {
			return false;
		}
		return true;
	}

	public boolean isStudentAlreadyInGroup(int groupId, String username) throws GroupException, UserException {
		Group chosenGroup;
		chosenGroup = GroupDAO.getInstance().getGroupById(groupId);
		if (GroupDAO.getInstance().isUserAlreadyInGroup(chosenGroup, username)) {
			return true;
		}
		return false;
	}

	private boolean isThereEmptyField(String groupIdString, String username) {
		if (groupIdString.trim().equals("") || (username.equals("")) || username == null) {
			return true;
		}
		return false;
	}

	private boolean doesGroupExist(int groupId) throws GroupException, UserException {
		try {
			Group currGroup = GroupDAO.getInstance().getGroupById(groupId);
			if (currGroup == null) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/getAllStudentsOfGroupServlet", method = RequestMethod.GET)
	protected void getAllStudentsOfGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if(request.getParameter("chosenGroupId") != null){
			String groupIdStr = request.getParameter("chosenGroupId").trim();
			if (!groupIdStr.equals("allGroups")) {
				if (!(groupIdStr.equals(""))) {	
					if(!ValidationsDAO.getInstance().isStringValidInteger(groupIdStr)){
						response.setStatus(404);
						return;
					}
					int groupId = Integer.parseInt((String) request.getParameter("chosenGroupId"));
					try {
						Group selectedGroup = GroupDAO.getInstance().getGroupById(groupId);
						if(selectedGroup != null){
							request.getSession().setAttribute("chosenGroupName", selectedGroup.getName());

						ArrayList<Student> allStudentsOfGroup = GroupDAO.getInstance()
								.getStudentsOfGroup(selectedGroup);
						JsonArray array = new JsonArray();
						for (Student student : allStudentsOfGroup) {
							boolean hasStudentGivenMinOneTask = false;
							JsonObject obj = new JsonObject();
							obj.addProperty("id", student.getId());
							obj.addProperty("username", student.getUsername());
							if (request.getParameter("homeworkId") != null) {
								if(ValidationsDAO.getInstance().isStringValidInteger(request.getParameter("homeworkId"))){
								int chosenHomeworkId = Integer.parseInt(request.getParameter("homeworkId"));
								HomeworkDetails chosenHomework = null;
								chosenHomework = GroupDAO.getInstance().getHomeworkDetailsById(chosenHomeworkId);
								if(chosenHomework != null){
								for (Task t : UserDAO.getInstance().getTasksOfHomeworkOfStudent(student.getId(),
										chosenHomework)) {
									hasStudentGivenMinOneTask = false;
									String x = t.getSolution();
									if (x != null) {
										hasStudentGivenMinOneTask = true;
										obj.addProperty("hasStudentGivenMinOneTask", hasStudentGivenMinOneTask);
										break;
									}
									obj.addProperty("hasStudentGivenMinOneTask", hasStudentGivenMinOneTask);
								}
								
							}else{
								response.setStatus(404);return;
							}
								}else{
									response.setStatus(404);return;
								}
							}
							array.add(obj);
						}
						response.setStatus(IValidationsDAO.SUCCESS_STATUS);
						response.getWriter().write(array.toString());
						
						}else{
							response.setStatus(404);
						}
					} catch (GroupException | UserException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
					} catch (IOException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
					}
				}else{
					response.setStatus(404);
				}
			
			}
			}else{
				response.setStatus(404);
			}
		}else{
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}
	@RequestMapping(value = "/getAllStudentsOfGroupRemoveStudent", method = RequestMethod.GET)
	protected void getAllStudentsOfGroupRemoveStudent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if(request.getParameter("chosenGroupId") != null){
			String groupIdStr = request.getParameter("chosenGroupId").trim();
			if (!groupIdStr.equals("allGroups")) {
				if (!(groupIdStr.equals(""))) {
					if(!ValidationsDAO.getInstance().isStringValidInteger(groupIdStr)){
						response.setStatus(404);
						return;
					}
					int groupId = Integer.parseInt((String) request.getParameter("chosenGroupId"));
					try {
						Group selectedGroup = GroupDAO.getInstance().getGroupById(groupId);
						if(selectedGroup != null){
						ArrayList<Student> allStudentsOfGroup = GroupDAO.getInstance()
								.getStudentsOfGroup(selectedGroup);
						JsonArray array = new JsonArray();
						for (Student student : allStudentsOfGroup) {
							JsonObject obj = new JsonObject();
							obj.addProperty("id", student.getId());
							obj.addProperty("username", student.getUsername());
							array.add(obj);
						}
						response.setStatus(IValidationsDAO.SUCCESS_STATUS);
						response.getWriter().write(array.toString());
						
						}else{
							response.setStatus(404);
						}
					} catch (GroupException | UserException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
					} catch (IOException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
					}
				}else{
					response.setStatus(404);
				}
			
			}
			}else{
				response.setStatus(404);
			}
		}else{
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}
	@RequestMapping(value = "/RemoveGroupServlet", method = RequestMethod.POST)
	protected String removeGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			int groupId = Integer.parseInt(request.getParameter("groupId"));
			Group selectedGroup;
			try {
				selectedGroup = GroupDAO.getInstance().getGroupById(groupId);
				GroupDAO.getInstance().removeGroup(selectedGroup);
				request.getServletContext().removeAttribute("allGroups");
				ArrayList<Group> allGroupsUpdated = GroupDAO.getInstance().getAllGroups();
				request.getServletContext().setAttribute("allGroups", allGroupsUpdated);
				request.getSession().setAttribute("invalidFields", false);
			} catch (GroupException | UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			}
			return "redirect:./SeeGroups";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/RemoveStudentFromGroup", method = RequestMethod.POST)
	protected void removeStudentFromGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (!(request.getParameter("chosenGroupId").equals("null"))) {
				try {
					int chosenGroupId = Integer.parseInt(request.getParameter("chosenGroupId"));
					Group chosenGroup = GroupDAO.getInstance().getGroupById(chosenGroupId);
					String studentUsername = request.getParameter("chosenStudentUsername").trim();
					Student chosenStudent = (Student) UserDAO.getInstance().getUserByUsername(studentUsername);
					GroupDAO.getInstance().removeUserFromGroup(chosenGroup, chosenStudent.getId());
					response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				} catch (GroupException | UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
				}
			}
		}else{
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/SeeGroups", method = RequestMethod.GET)
	protected String seeGroups(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "seeGroupsToChange";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/UpdateGroupServlet", method = RequestMethod.GET)
	protected String seeUpdateGroupPage(HttpServletRequest request, HttpServletResponse resp)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			int groupId = 0;

			if (request.getParameter("groupId") != null) {
				int length = request.getParameter("groupId").length();
				String number = request.getParameter("groupId");
				if (length > IValidationsDAO.MIN_SIZE_OF_INTEGER && length < IValidationsDAO.MAX_SIZE_OF_INTEGER) {
					for (int i = 0; i < length; i++) {
						if ((int) number.charAt(i) < IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO || (int) number.charAt(i) > IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE) {
							return "pageNotFound";
						}
					}
				}else{
					return "pageNotFound";
				}
				groupId = Integer.parseInt(request.getParameter("groupId"));
			} else {
				if (request.getSession().getAttribute("currGroup") != null) {
					Group group = (Group) request.getSession().getAttribute("currGroup");
					groupId = group.getId();
				} else {
					return "pageNotFound";
				}
			}
			try {
				Group group = GroupDAO.getInstance().getGroupById(groupId);
				if (group != null) {
					request.getSession().setAttribute("currGroup", group);
					return "updateGroup";
				}
				return "pageNotFound";
			} catch (GroupException | UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			}
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/UpdateGroupServlet", method = RequestMethod.POST)
	protected String updateGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			Group currGroup = (Group) request.getSession().getAttribute("currGroup");
			int groupId = currGroup.getId();
			String newGroupName = request.getParameter("groupName").trim();
			String[] selectedTeachersUsername = request.getParameterValues("teachers");
			ArrayList<Integer> allSelectedTeachers = new ArrayList<>();
			
			try {
				if (selectedTeachersUsername != null) {
					for (int i = 0; i < selectedTeachersUsername.length; i++) {
						Teacher t = null;
						t = (Teacher) UserDAO.getInstance().getUserByUsername(selectedTeachersUsername[i]);

						allSelectedTeachers.add(t.getId());
					}
				}
				// empty fields
				if (isThereEmptyFieldUpdateGroup(newGroupName)) {
					request.getSession().setAttribute("emptyFields", true);
				} else {
					// invalid name
					boolean isNameUnique = false;
					if (isGroupNameUnique(groupId, newGroupName)) {
						isNameUnique = true;
					}
					request.getSession().setAttribute("uniqueName", isNameUnique);
					boolean isNameValid = false;
					if (isGroupNameValidUpdateGroup(newGroupName)) {
						isNameValid = true;
					}
					request.getSession().setAttribute("validName", isNameValid); // success
					ArrayList<Teacher> allTeachers = (ArrayList<Teacher>) request.getServletContext()
							.getAttribute("allTeachers");
					ArrayList<String> allTeacherUsernames = new ArrayList<>();
					boolean allTeachersExist = true;
					if (selectedTeachersUsername != null) {
						for (Teacher teacher : allTeachers) {
							allTeacherUsernames.add(teacher.getUsername());
						}
						if (!doAllTeachersExistUpdateGroup(selectedTeachersUsername, allTeacherUsernames)) {
							allTeachersExist = false;
						}
						request.getSession().setAttribute("allTeachersExist", allTeachersExist);
					}
					if (isNameUnique == true && isNameValid == true && allTeachersExist == true) {
						currGroup.setName(newGroupName);
						GroupDAO.getInstance().updateGroup(currGroup, allSelectedTeachers);
						ArrayList<Group> allGroups;
						allGroups = GroupDAO.getInstance().getAllGroups();
						request.getServletContext().setAttribute("allGroups", allGroups);
						ArrayList<Teacher> allTeachersUpdated = UserDAO.getInstance().getAllTeachers();
						for (Teacher t : allTeachersUpdated) {
							t.setGroups(UserDAO.getInstance().getGroupsOfUser(t.getId()));
						}
						request.getServletContext().setAttribute("allTeachers", allTeachersUpdated);
						request.getSession().setAttribute("invalidFields", false);
					}
				}
			} catch (GroupException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			} catch (ValidationException e) {
				request.getSession().setAttribute("invalidFields", true);
			} catch (UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			}
			return "redirect:./UpdateGroupServlet";
		}
		return "forbiddenPage";
	}

	private boolean isGroupNameUnique(int groupId, String groupName) throws GroupException {
		int wantedGroupNameId = GroupDAO.getInstance().getGroupIdByGroupName(groupName);
		if (ValidationsDAO.getInstance().isGroupNameUnique(groupName)) {
			return true;
		} else {
			if (wantedGroupNameId == groupId) {
				return true;
			}
		}
		return false;
	}

	private boolean isGroupNameValidUpdateGroup(String groupName) {
		if (isGroupNameLengthValidUpdateGroup(groupName) && areGroupNameCharactersValidUpdateGroup(groupName)) {
			return true;
		}
		return false;
	}

	private boolean isGroupNameLengthValidUpdateGroup(String groupName) {
		if (groupName.length() >= IValidationsDAO.MIN_SIZE_OF_GROUP_NAME && groupName.length() <= IValidationsDAO.MAX_SIZE_OF_GROUP_NAME) {
			return true;
		}
		return false;
	}

	private boolean areGroupNameCharactersValidUpdateGroup(String groupName) {
		for (int i = 0; i < groupName.length(); i++) {
			if (!(((int) groupName.charAt(i) >= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_FROM && (int) groupName.charAt(i) <= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_TO))) {
				return false;
			}
		}
		return true;
	}

	private boolean isThereEmptyFieldUpdateGroup(String groupName) {
		if (groupName == null || groupName.equals("")) {
			return true;
		}
		return false;
	}

	private boolean doAllTeachersExistUpdateGroup(String[] selectedTeachersUsername,
			ArrayList<String> allTeachersUsernames) {
		boolean doAllExist = true;
		for (int i = 0; i < selectedTeachersUsername.length;) {
			if (!(allTeachersUsernames.contains(selectedTeachersUsername[i++]))) {
				doAllExist = false;
				break;
			}
		}
		return doAllExist;
	}
}
