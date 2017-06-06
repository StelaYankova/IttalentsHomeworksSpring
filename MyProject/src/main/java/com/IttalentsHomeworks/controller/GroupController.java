package com.IttalentsHomeworks.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.IttalentsHomeworks.DAO.GroupDAO;
import com.IttalentsHomeworks.DAO.HomeworkException;
import com.IttalentsHomeworks.DAO.IValidationsDAO;
import com.IttalentsHomeworks.DAO.UserDAO;
import com.IttalentsHomeworks.DAO.ValidationsDAO;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.Exceptions.ValidationException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.Student;
import com.IttalentsHomeworks.model.Task;
import com.IttalentsHomeworks.model.Teacher;
import com.IttalentsHomeworks.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class GroupController {

	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private GroupDAO groupDAO;
	
	@Autowired
	private ValidationsDAO validationsDAO;
	
	@RequestMapping(value = "/createGroup", method = RequestMethod.GET)
	protected String addGroupGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "addGroup";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/createGroup", method = RequestMethod.POST)
	protected String addGroupPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String groupName = (request.getParameter("groupName").trim() != null)
					? (request.getParameter("groupName").trim()) : ("");
			String[] selectedTeachersUsername = (request.getParameterValues("teachers") != null)
					? (request.getParameterValues("teachers")) : (new String[0]);
			request.setAttribute("nameTry", groupName);
			request.setAttribute("selectedTeachersUsernameTry", selectedTeachersUsername);
			// empty fields
			boolean isNameUnique = false;
			if (isThereEmptyField(request.getParameter("groupName").trim(), request.getParameterValues("teachers"))) {
				request.setAttribute("emptyFields", true);
			} else {
				try {
					// unique name
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
					ArrayList<Teacher> allTeachers = null;
					synchronized (request.getServletContext()) {
						allTeachers = (ArrayList<Teacher>) request.getServletContext()
							.getAttribute("allTeachers");
					}
					ArrayList<String> allTeacherUsernames = new ArrayList<>();
					boolean allTeachersExist = true;
					for (Teacher teacher : allTeachers) {
						allTeacherUsernames.add(teacher.getUsername());
					}
					if (!doAllTeachersExist(selectedTeachersUsername, allTeacherUsernames)) {
						allTeachersExist = false;
					}
					request.setAttribute("allTeachersExist", allTeachersExist);
					if (isNameUnique == true && isNameValid == true && allTeachersExist == true) {
						ArrayList<Teacher> allSelectedTeachers = new ArrayList<>();
						for (int i = 0; i < selectedTeachersUsername.length; i++) {
							Teacher t = null;
							t = (Teacher) userDAO.getUserByUsername(selectedTeachersUsername[i]);
							if (t != null) {
								allSelectedTeachers.add(t);
							}
						}
						Group newGroup = new Group(groupName, allSelectedTeachers);
						System.out.println("Will create group..." + newGroup.getName());
						groupDAO.createNewGroup(newGroup);
						request.setAttribute("invalidFields", false);
						synchronized (request.getServletContext()) {
							ArrayList<Group> allGroupsUpdated = (ArrayList<Group>) request.getServletContext()
									.getAttribute("allGroups");
							allGroupsUpdated.add(
									groupDAO.getGroupWithoutStudentsById(groupDAO.getGroupIdByGroupName(groupName)));
							ArrayList<Teacher> allTeachersUpdated = userDAO.getAllTeachers();
							for (Teacher t : allTeachersUpdated) {
								t.setGroups(userDAO.getGroupsOfUserWithoutStudents(t.getId()));
							}
							request.getServletContext().setAttribute("allTeachers", allTeachersUpdated);
						}
						
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
		} else {
			return "forbiddenPage";
		}
	}

	private boolean isGroupNameUnique(String groupName) throws GroupException {
		if (validationsDAO.isGroupNameUnique(groupName)) {
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
		if (groupName.length() >= IValidationsDAO.MIN_SIZE_OF_GROUP_NAME
				&& groupName.length() <= IValidationsDAO.MAX_SIZE_OF_GROUP_NAME) {
			return true;
		}
		return false;
	}

	private boolean areCharactersValid(String groupName) {
		for (int i = 0; i < groupName.length(); i++) {
			if (!(((int) groupName.charAt(i) >= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_FROM
					&& (int) groupName.charAt(i) <= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_TO))
					|| (int) groupName.charAt(i) == IValidationsDAO.ASCII_TABLE_QUOTES) {
				return false;
			}
		}
		return true;
	}

	private boolean isThereEmptyField(String groupName, String[] teachers) {
		if (groupName != null && !(groupName.equals("")) && teachers != null && teachers.length > 0) {
			return false;
		}
		return true;
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

	@RequestMapping(value = "/addOrRemoveStudent", method = RequestMethod.GET)
	protected String addStudentToGroupGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "addOrRemoveStudentByGroup";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/addStudent", method = RequestMethod.POST)
	protected String addStudentToGroupPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			// empty fields
			if (isThereEmptyField(request.getParameter("chosenGroup").trim(),
					request.getParameter("selectedStudent").trim())) {
				request.getSession().setAttribute("emptyFields", true);
			} else {
				String chosenGroupIdString = request.getParameter("chosenGroup").trim();
				String chosenStudentUsername = request.getParameter("selectedStudent").trim();
				request.getSession().setAttribute("chosenUsernameTry", chosenStudentUsername);
				try {
					// does student exist
					if (!validationsDAO.isStringValidInteger(chosenGroupIdString)) {
						request.getSession().setAttribute("invalidFields", true);
						return "redirect:./addOrRemoveStudent";
					}
					boolean doesStudentExist = false;
					boolean isStudentInGroup = false;
					int chosenGroupId = Integer.parseInt(chosenGroupIdString);
					boolean isGroupValid = false;
					// does group exist
					if (doesGroupExist(chosenGroupId)) {
						isGroupValid = true;
					}
					request.getSession().setAttribute("validGroups", isGroupValid);
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
					if (doesStudentExist == true && isStudentInGroup == false && isGroupValid == true) {
						groupDAO.addUserToGroup(chosenGroupId,
								userDAO.getUserIdByUsername(chosenStudentUsername));
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
			return "redirect:./addOrRemoveStudent";
		}
		return "forbiddenPage";
	}

	private boolean doesStudentExist(String username) throws UserException {
		if (validationsDAO.isUsernameUnique(username)) {
			return false;
		}
		return true;
	}

	public boolean isStudentAlreadyInGroup(int groupId, String username) throws GroupException, UserException {
		if (groupDAO.isUserAlreadyInGroup(groupId, username)) {
			return true;
		}
		return false;
	}

	private boolean isThereEmptyField(String groupIdString, String username) {
		if (groupIdString != null && !(groupIdString.trim().equals("")) && username != null && !(username.equals(""))) {
			return false;
		}
		return true;
	}

	private boolean doesGroupExist(int groupId) throws GroupException, UserException {
		boolean doesGroupExist = validationsDAO.doesGroupExistInDBById(groupId);
		return doesGroupExist;
	}

	@RequestMapping(value = "/getAllStudentsOfGroupServlet", method = RequestMethod.GET)
	protected void getAllStudentsOfGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getParameter("chosenGroupId") != null
					&& !(request.getParameter("chosenGroupId").trim().equals(""))) {
				String groupIdStr = request.getParameter("chosenGroupId").trim();
				if (!groupIdStr.equals("allGroups") && !groupIdStr.equals("null")) {
					if (!validationsDAO.isStringValidInteger(groupIdStr)) {
						response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
						return;
					}
					int groupId = Integer.parseInt((String) request.getParameter("chosenGroupId"));
					try {
						if (validationsDAO.doesGroupExistInDBById(groupId)) {
							request.getSession().setAttribute("chosenGroupName",
									groupDAO.getGroupNameById(groupId));
							ArrayList<Integer> allStudentsIdsOfGroup = groupDAO
									.getStudentsIdsOfGroup(groupId);
							JsonArray array = new JsonArray();
							for (Integer studentId : allStudentsIdsOfGroup) {
								boolean hasStudentGivenMinOneTask = false;
								JsonObject obj = new JsonObject();
								obj.addProperty("id", studentId);
								obj.addProperty("username", userDAO.getUserUsernameById(studentId));
								if (request.getParameter("homeworkId") != null && validationsDAO
										.isStringValidInteger(request.getParameter("homeworkId"))) {
									int chosenHomeworkId = Integer.parseInt(request.getParameter("homeworkId"));
									if (validationsDAO.doHomeworkDetailsExist(chosenHomeworkId)) {
										for (Task t : userDAO.getTasksOfHomeworkOfStudent(studentId,
												chosenHomeworkId)) {
											hasStudentGivenMinOneTask = false;
											String x = t.getSolution();
											if (x != null) {
												File f = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA
														+ File.separator + x);
												if (f.exists() && f.isFile()) {
													long length = f.length();
													if (length > 0) {
														hasStudentGivenMinOneTask = true;
														obj.addProperty("hasStudentGivenMinOneTask",
																hasStudentGivenMinOneTask);
														break;
													}
												}
											}
										}
									} else {
										response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
										return;
									}
								}
								array.add(obj);
							}
							response.setStatus(IValidationsDAO.SUCCESS_STATUS);
							response.getWriter().write(array.toString());
						} else {
							response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
						}
					} catch (GroupException | UserException | HomeworkException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
					} catch (IOException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
					}
				}
				response.setStatus(IValidationsDAO.SUCCESS_STATUS);
			} else {
				if(request.getParameter("chosenGroupId") == null){
					response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
					
				}else
				if (!request.getParameter("chosenGroupId").equals("null")) {
					response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
				}
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/getAllStudentsOfGroupRemoveStudent", method = RequestMethod.GET)
	protected void getAllStudentsOfGroupRemoveStudent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getParameter("chosenGroupId") != null && !(request.getParameter("chosenGroupId").equals(""))) {
				String groupIdStr = request.getParameter("chosenGroupId").trim();
				if (!(groupIdStr.equals("allGroups")) && !(groupIdStr.equals("null"))) {
					if (!validationsDAO.isStringValidInteger(groupIdStr)) {
						response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
						return;
					}
					int groupId = Integer.parseInt((String) request.getParameter("chosenGroupId"));
					try {
						if (validationsDAO.doesGroupExistInDBById(groupId)) {
							ArrayList<Student> allStudentsOfGroup = groupDAO.getStudentsOfGroup(groupId);
							JsonArray array = new JsonArray();
							for (Student student : allStudentsOfGroup) {
								JsonObject obj = new JsonObject();
								obj.addProperty("id", student.getId());
								obj.addProperty("username", student.getUsername());
								array.add(obj);
							}
							response.setStatus(IValidationsDAO.SUCCESS_STATUS);
							System.out.println(array.toString());
							response.getWriter().write(array.toString());
						} else {
							response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
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
				}
				response.setStatus(IValidationsDAO.SUCCESS_STATUS);
			} else {
				response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/removeGroup", method = RequestMethod.POST)
	protected String removeGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getParameter("groupId") != null && !(request.getParameter("groupId").trim().equals(""))
					&& validationsDAO.isStringValidInteger(request.getParameter("groupId").trim())) {
				int groupId = Integer.parseInt(request.getParameter("groupId").trim());
				try {
					if (validationsDAO.doesGroupExistInDBById(groupId)) {
						groupDAO.removeGroup(groupId);
						synchronized (request.getServletContext()) {
							request.getServletContext().removeAttribute("allGroups");
							ArrayList<Group> allGroupsUpdated = groupDAO.getAllGroupsWithoutStudents();
							request.getServletContext().setAttribute("allGroups", allGroupsUpdated);
						}
						request.getSession().setAttribute("invalidFields", false);
					}
				} catch (GroupException | UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}
			}
			return "redirect:./seeGroups";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/removeStudentFromGroup", method = RequestMethod.POST)
	protected void removeStudentFromGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getParameter("chosenGroupId") != null
					&& !(request.getParameter("chosenGroupId").trim().equals(""))
					&& !(request.getParameter("chosenGroupId").equals("null"))
					&& validationsDAO.isStringValidInteger(request.getParameter("chosenGroupId"))) {
				try {
					int chosenGroupId = Integer.parseInt(request.getParameter("chosenGroupId"));
					String studentUsername = request.getParameter("chosenStudentUsername").trim();
					int chosenStudentId = userDAO.getUserIdByUsername(studentUsername);
					if (validationsDAO.doesGroupExistInDBById(chosenGroupId) == true
							&& validationsDAO.doesUserExistInDBById(chosenStudentId)) {
						groupDAO.removeUserFromGroup(chosenGroupId, chosenStudentId);
						response.setStatus(IValidationsDAO.SUCCESS_STATUS);
					} else {
						response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
					}
				} catch (GroupException | UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
				}
			} else {
				response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/seeGroups", method = RequestMethod.GET)
	protected String seeGroups(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "seeAndChangeGroups";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/updateGroup", method = RequestMethod.GET)
	protected String seeUpdateGroupPage(HttpServletRequest request, HttpServletResponse resp)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			int groupId = 0;
			if (request.getParameter("groupId") != null) {
				if (validationsDAO.isStringValidInteger(request.getParameter("groupId").trim())) {
					groupId = Integer.parseInt(request.getParameter("groupId"));
				} else {
					return "pageNotFound";
				}
			} else {
				if (request.getSession().getAttribute("currGroup") != null) {
					Group group = (Group) request.getSession().getAttribute("currGroup");
					groupId = group.getId();
				} else {
					return "pageNotFound";
				}
			}
			try {
				Group group = groupDAO.getGroupWithoutStudentsById(groupId);
				if (group != null) {
					request.getSession().setAttribute("currGroup", group);
					return "updateGroup";
				} else {
					return "pageNotFound";
				}
			} catch (GroupException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			}
		} else {
			return "forbiddenPage";
		}
	}

	@RequestMapping(value = "/updateGroup", method = RequestMethod.POST)
	protected String updateGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getSession().getAttribute("currGroup") != null) {
				Group currGroup = (Group) request.getSession().getAttribute("currGroup");
				int groupId = currGroup.getId();
				try {
					// empty fields
					if (isThereEmptyFieldUpdateGroup(request.getParameter("groupName"),
							request.getParameterValues("teachers"))) {
						request.getSession().setAttribute("emptyFields", true);
						String groupName = (request.getParameter("groupName").trim() != null)
								? (request.getParameter("groupName").trim()) : ("");
						String[] selectedTeachersUsername = (request.getParameterValues("teachers") != null)
								? (request.getParameterValues("teachers")) : (new String[0]);
						request.setAttribute("nameTry", groupName);
						request.setAttribute("selectedTeachersUsernameTry", selectedTeachersUsername);
					} else {
						String newGroupName = request.getParameter("groupName").trim();
						String[] selectedTeachersUsername = request.getParameterValues("teachers");
						// unique name
						boolean isNameUnique = false;
						if (isGroupNameUnique(groupId, newGroupName)) {
							isNameUnique = true;
						}
						request.getSession().setAttribute("uniqueName", isNameUnique);
						// valid name
						boolean isNameValid = false;
						if (isGroupNameValidUpdateGroup(newGroupName)) {
							isNameValid = true;
						}
						request.getSession().setAttribute("validName", isNameValid);
						ArrayList<Teacher> allTeachers = null;
						synchronized (request.getServletContext()) {
							 allTeachers = (ArrayList<Teacher>) request.getServletContext()
								.getAttribute("allTeachers");
						}
						ArrayList<String> allTeacherUsernames = new ArrayList<>();
						boolean allTeachersExist = true;
						for (Teacher teacher : allTeachers) {
							allTeacherUsernames.add(teacher.getUsername());
						}
						if (!doAllTeachersExistUpdateGroup(selectedTeachersUsername, allTeacherUsernames)) {
							allTeachersExist = false;
						}
						request.getSession().setAttribute("allTeachersExist", allTeachersExist);
						if (isNameUnique == true && isNameValid == true && allTeachersExist == true) {
							ArrayList<Integer> allSelectedTeachers = new ArrayList<>();
							for (int i = 0; i < selectedTeachersUsername.length; i++) {
								allSelectedTeachers
										.add(userDAO.getUserIdByUsername(selectedTeachersUsername[i]));
							}
							currGroup.setName(newGroupName);
							groupDAO.updateGroup(currGroup, allSelectedTeachers);
							ArrayList<Group> allGroups = groupDAO.getAllGroupsWithoutStudents();
							synchronized (request.getServletContext()) {
								request.getServletContext().setAttribute("allGroups", allGroups);
								ArrayList<Teacher> allTeachersUpdated = userDAO.getAllTeachers();
								for (Teacher t : allTeachersUpdated) {
									t.setGroups(userDAO.getGroupsOfUserWithoutStudents(t.getId()));
								}
								request.getServletContext().setAttribute("allTeachers", allTeachersUpdated);
							}
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
				return "redirect:./updateGroup";
			} else {
				request.getSession().setAttribute("invalidFields", true);
				return "redirect:./updateGroup";
			}
		}
		return "forbiddenPage";
	}

	private boolean isGroupNameUnique(int groupId, String groupName) throws GroupException {
		int wantedGroupNameId = groupDAO.getGroupIdByGroupName(groupName);
		if (validationsDAO.isGroupNameUnique(groupName)) {
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
		if (groupName.length() >= IValidationsDAO.MIN_SIZE_OF_GROUP_NAME
				&& groupName.length() <= IValidationsDAO.MAX_SIZE_OF_GROUP_NAME) {
			return true;
		}
		return false;
	}

	private boolean areGroupNameCharactersValidUpdateGroup(String groupName) {
		for (int i = 0; i < groupName.length(); i++) {
			if (!(((int) groupName.charAt(i) >= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_FROM
					&& (int) groupName.charAt(i) <= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_TO))
					|| (int) groupName.charAt(i) == IValidationsDAO.ASCII_TABLE_QUOTES) {
				return false;
			}
		}
		return true;
	}

	private boolean isThereEmptyFieldUpdateGroup(String groupName, String[] teachers) {
		if (groupName != null && !(groupName.equals("")) && teachers != null && teachers.length > 0) {
			return false;
		}
		return true;
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
