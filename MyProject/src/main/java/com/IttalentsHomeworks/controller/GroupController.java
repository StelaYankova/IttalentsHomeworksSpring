package com.IttalentsHomeworks.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.IttalentsHomeworks.DAO.GroupDAO;
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

	
//	@WebServlet("/AddGroupServlet")
	//public class AddGroupServlet extends HttpServlet {

	@RequestMapping(value="/AddGroupServlet",method = RequestMethod.GET)
		protected String addGroupGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			//TODO throw exception
			User user = (User) request.getSession().getAttribute("user");
			if(user.isTeacher()){
				//request.getRequestDispatcher("addGroup.jsp").forward(request, response);
				return "addGroup";

			}
			return "error";
		}

	@RequestMapping(value="/AddGroupServlet",method = RequestMethod.POST)
		protected String addGroupPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			User user = (User) request.getSession().getAttribute("user");
			if(user.isTeacher()){
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
				} catch (GroupException e1) {
					System.out.println(e1.getMessage());
					e1.printStackTrace();
					return "exception";
				}
				request.setAttribute("uniqueName", isNameUnique);
				// valid name
				boolean isNameValid = false;
				if (isGroupNameValid(groupName)) {
					isNameValid = true;
				}
				request.setAttribute("validName", isNameValid);
				ArrayList<Teacher> allTeachers = (ArrayList<Teacher>) request.getServletContext().getAttribute("allTeachers");
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
							try {
								t = (Teacher) UserDAO.getInstance().getUserByUsername(selectedTeachersUsername[i]);
								allSelectedTeachers.add(t);
							} catch (UserException | GroupException e) {
								System.out.println(e.getMessage());
								e.printStackTrace();
								return "exception";
							}
						}
					}
					Group newGroup = new Group(groupName, allSelectedTeachers);

					try {
						try {
							GroupDAO.getInstance().createNewGroup(newGroup);
							request.setAttribute("invalidFields", false);

						} catch (ValidationException e) {
							request.setAttribute("invalidFields", true);
							//request.getRequestDispatcher("addGroup.jsp").forward(request, response);
							return "addGroup";
						}
						ArrayList<Group> allGroupsUpdated = GroupDAO.getInstance().getAllGroups();
						request.getServletContext().setAttribute("allGroups", allGroupsUpdated);
							ArrayList<Teacher> allTeachersUpdated = UserDAO.getInstance().getAllTeachers();
							for(Teacher t : allTeachersUpdated){
								t.setGroups(UserDAO.getInstance().getGroupsOfUser(t.getId()));
							}
							request.getServletContext().setAttribute("allTeachers", allTeachersUpdated);
					} catch (GroupException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						return "exception";
					} catch (UserException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						return "exception";
					}
				}
			}
			//response.sendRedirect("./AddGroupServlet");
			//request.getRequestDispatcher("addGroup.jsp").forward(request, response);
			return "addGroup";
			}
			return "error";
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
			if (groupName.length() >= 5 && groupName.length() <= 20) {
				return true;
			}
			return false;
		}

		private boolean areCharactersValid(String groupName) {
			for (int i = 0; i < groupName.length(); i++) {
				if (!(((int) groupName.charAt(i) >= 32 && (int) groupName.charAt(i) <= 126))) {
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
		
		private boolean doAllTeachersExist(String[] selectedTeachersUsername, ArrayList<String> allTeachersUsernames){
			boolean doAllExist = true;
			for(int i = 0; i < selectedTeachersUsername.length;){
				if(!(allTeachersUsernames.contains(selectedTeachersUsername[i++]))){
					doAllExist = false;
					break;
				}
			}
			return doAllExist;
		}
	//}
		
		@RequestMapping(value="/AddStudentToGroupServlet",method = RequestMethod.GET)
		protected String addStudentToGroupGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			//TODO throw exception
					User user = (User) request.getSession().getAttribute("user");
					if(user.isTeacher()){
						return "addStudentToGroup";
			//request.getRequestDispatcher("addStudentToGroup.jsp").forward(request, response);
					}
					return "error";
		}

		@RequestMapping(value="/AddStudentToGroupServlet",method = RequestMethod.POST)
		protected String addStudentToGroupPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			//TODO throw exception
					User user = (User) request.getSession().getAttribute("user");
					if(user.isTeacher()){
			String chosenGroupIdString = request.getParameter("chosenGroup").trim();
			String chosenStudentUsername = request.getParameter("selectedStudent").trim();

			// empty fields
			request.getSession().setAttribute("chosenUsernameTry", chosenStudentUsername);
			if (isThereEmptyField(chosenGroupIdString, chosenStudentUsername)) {
				request.getSession().setAttribute("emptyFields", true);
			} else {
				// does student exist
				boolean doesStudentExist = false;
				boolean isStudentInGroup = false;
				int chosenGroupId = Integer.parseInt(chosenGroupIdString);
				try {
					if (doesStudentExist(chosenStudentUsername)) {
						doesStudentExist = true;
					}
				} catch (UserException e1) {
					System.out.println(e1.getMessage());
					e1.printStackTrace();
					return "exception";
				}
				request.getSession().setAttribute("doesStudentExist", doesStudentExist);

				if (doesStudentExist == true) {
					// is student already in group
					try {
						if (isStudentAlreadyInGroup(chosenGroupId, chosenStudentUsername)) {
							isStudentInGroup = true;
						}
					} catch (GroupException | UserException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						return "exception";
					}
				}
				request.getSession().setAttribute("isStudentInGroup", isStudentInGroup);

				boolean isGroupValid = false;
				try {
					if(doesGroupExist(chosenGroupId)){
						isGroupValid = true;
					}
				} catch ( GroupException | UserException e1) {
					System.out.println(e1.getMessage());
					e1.printStackTrace();
					return "exception";
				}
				request.getSession().setAttribute("validGroups", isGroupValid);
				
				if (doesStudentExist == true && isStudentInGroup == false && isGroupValid == true) {

					try {
						Group group = GroupDAO.getInstance().getGroupById(chosenGroupId);
						Student student = UserDAO.getInstance().getStudentsByUsername(chosenStudentUsername);
						GroupDAO.getInstance().addUserToGroup(group, student);
						request.getSession().setAttribute("invalidFields", false);
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
			}
		//	response.sendRedirect("./AddStudentToGroupServlet");
			return "redirect:./AddStudentToGroupServlet";
					}
					return "error";
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
				com.IttalentsHomeworks.model.User chosenStudent = UserDAO.getInstance().getUserByUsername(username);
				if (GroupDAO.getInstance().isUserAlreadyInGroup(chosenGroup, chosenStudent)) {
					return true;
				}
			
			return false;

		}

		private boolean isThereEmptyField(String groupIdString, String username) {
			if (groupIdString.equals("null") || (groupIdString.equals("")) || (username.equals("")) || username == null) {
				return true;
			}
			return false;
		}
		
		private boolean doesGroupExist(int groupId) throws GroupException, UserException{
			try{		
			Group currGroup = GroupDAO.getInstance().getGroupById(groupId);
					if(currGroup == null){
						return false;
					}
			}catch(NumberFormatException e){
				return false;
			}
			
			return true;
		}
		
		@RequestMapping(value="/getAllStudentsOfGroupServlet",method = RequestMethod.GET)
		protected String getAllStudentsOfGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			//TODO throw exception
			HttpSession session = request.getSession(true);
			System.out.println(session.getCreationTime()-session.getLastAccessedTime() + "kkkkkkk");
			System.out.println(session.getLastAccessedTime() + ")))))");
			if (session.isNew() || session.getAttribute("user") == null) {
				System.out.println("EXPIREDDDDDDDD");

				response.setStatus(401);
			   // return "redirect:./index";
				return null;
			}else{
					User user = (User) request.getSession().getAttribute("user");
					if(user.isTeacher()){
			String groupIdStr = request.getParameter("chosenGroupId");
			
			if (groupIdStr.equals("allGroups")) {
				//response.sendRedirect("chooseGroupForHomework.jsp");
				return "chooseGroupForHomework";
			} else if (!(groupIdStr.equals("null"))) {
				int groupId = Integer.parseInt((String) request.getParameter("chosenGroupId"));
				try {
					Group selectedGroup = GroupDAO.getInstance().getGroupById(groupId);
					ArrayList<Student> allStudentsOfGroup = GroupDAO.getInstance().getStudentsOfGroup(selectedGroup);
					JsonArray array = new JsonArray();
					for (Student student : allStudentsOfGroup) {
						boolean hasStudentGivenMinOneTask = false;
						JsonObject obj = new JsonObject();
						obj.addProperty("id", student.getId());
						obj.addProperty("username", student.getUsername());
						if (request.getParameter("homeworkId") != null) {
							int chosenHomeworkId = Integer.parseInt(request.getParameter("homeworkId"));
							HomeworkDetails chosenHomework = null;
							try {
								chosenHomework = GroupDAO.getInstance().getHomeworkDetailsById(chosenHomeworkId);
							} catch (GroupException e1) {
								System.out.println(e1.getMessage());
								e1.printStackTrace();
								return "exception";
							}
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
						}
						array.add(obj);
					}
					response.getWriter().write(array.toString());

				} catch (GroupException | UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}
			}
			}
					return null;}
		}
		
		@RequestMapping(value="/RemoveGroupServlet",method = RequestMethod.POST)
		protected String removeGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
			//TODO throw exception
					User user = (User) request.getSession().getAttribute("user");
					if(user.isTeacher()){
			int groupId = Integer.parseInt(request.getParameter("groupId"));
			Group selectedGroup;
			try {
				selectedGroup = GroupDAO.getInstance().getGroupById(groupId);
				GroupDAO.getInstance().removeGroup(selectedGroup);
				request.getServletContext().removeAttribute("allGroups");
				ArrayList<Group> allGroupsUpdated = GroupDAO.getInstance().getAllGroups();
				request.getServletContext().setAttribute("allGroups", allGroupsUpdated);
			} catch (GroupException | UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return "exception";
			}
			//response.sendRedirect("./SeeGroups");
			return "redirect:./SeeGroups";
			
					}
					return "error";
		}
		
		@RequestMapping(value="/RemoveStudentFromGroup",method = RequestMethod.POST)
		protected String removeStudentFromGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
				 {
			//TODO throw exception
			if (request.getRequestedSessionId() == null
			        && !request.isRequestedSessionIdValid()) {
				System.out.println("SESUSUSUSSUSUUSSU");
			    return "redirect:./index";
			}else{
					User user = (User) request.getSession().getAttribute("user");
					if(user.isTeacher()){
			if (!(request.getParameter("chosenGroupId").equals("null"))) {

				try {
					int chosenGroupId = Integer.parseInt(request.getParameter("chosenGroupId"));
					Group chosenGroup = GroupDAO.getInstance().getGroupById(chosenGroupId);
					String studentUsername = request.getParameter("chosenStudentUsername").trim();
					Student chosenStudent = (Student) UserDAO.getInstance().getUserByUsername(studentUsername);

					GroupDAO.getInstance().removeUserFromGroup(chosenGroup, chosenStudent.getId());
					response.setStatus(200);
					response.getWriter().write("");
					//request.getSession().setAttribute("invalidFields", false);

				} catch (GroupException | UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}

			}
			//response.sendRedirect("./AddStudentToGroupServlet");
			//request.getRequestDispatcher("addStudentToGroup.jsp").forward(request, response);
		}
					return null;}}

		@RequestMapping(value="/SeeGroups",method = RequestMethod.GET)
		protected String seeGroups(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
			//TODO throw exception
					User user = (User) request.getSession().getAttribute("user");
					if(user.isTeacher()){
						return "seeGroupsToChange";
			//request.getRequestDispatcher("seeGroupsToChange.jsp").forward(request, response);
					}
					return "error";
		}
		
		@RequestMapping(value="/UpdateGroupServlet",method = RequestMethod.GET)
		protected String seeUpdateGroupPage(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException{
			//TODO throw exception
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getParameter("groupId") != null) {// TODO check url (all updates too)
				int groupId = Integer.parseInt(request.getParameter("groupId"));
				try {
					Group group = GroupDAO.getInstance().getGroupById(groupId);
					if (group != null) {
						request.getSession().setAttribute("currGroup", group);
						return "updateGroup";

					}
				} catch (GroupException | UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}
			}
			// request.getRequestDispatcher("updateGroup.jsp").forward(request,
			// resp);
		}
		return "error";
	}
		
		@RequestMapping(value="/UpdateGroupServlet",method = RequestMethod.POST)
		protected String updateGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
				{
			//TODO throw exception
					User user = (User) request.getSession().getAttribute("user");
					if(user.isTeacher()){
			Group currGroup = (Group) request.getSession().getAttribute("currGroup");
			int groupId = currGroup.getId();
			String newGroupName = request.getParameter("groupName").trim();
			String[] selectedTeachersUsername = request.getParameterValues("teachers");

			ArrayList<Integer> allSelectedTeachers = new ArrayList<>();
			if (selectedTeachersUsername != null) {
				for (int i = 0; i < selectedTeachersUsername.length; i++) {
					Teacher t = null;
					try {
						t = (Teacher) UserDAO.getInstance().getUserByUsername(selectedTeachersUsername[i]);
						allSelectedTeachers.add(t.getId());
					} catch (UserException | GroupException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						return "exception";
					}
				}
			}
			// empty fields
			if (isThereEmptyFieldUpdateGroup(newGroupName)) {
				request.getSession().setAttribute("emptyFields", true);
			} else {
				// invalid name
				boolean isNameUnique = false;
				try {
					if (isGroupNameUnique(groupId, newGroupName)) {
						isNameUnique = true;
					}
				} catch (GroupException e1) {
					System.out.println(e1.getMessage());
					e1.printStackTrace();
					return "exception";
				}
				request.getSession().setAttribute("uniqueName", isNameUnique);

				boolean isNameValid = false;
				if (isGroupNameValidUpdateGroup(newGroupName)) {
					isNameValid = true;
				}
				request.getSession().setAttribute("validName", isNameValid); // success
				ArrayList<Teacher> allTeachers = (ArrayList<Teacher>) request.getServletContext().getAttribute("allTeachers");
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

					try {
						currGroup.setName(newGroupName);
						GroupDAO.getInstance().updateGroup(currGroup, allSelectedTeachers);
						
						ArrayList<Group> allGroups;
						try {
							allGroups = GroupDAO.getInstance().getAllGroups();
							request.getServletContext().setAttribute("allGroups", allGroups);
							ArrayList<Teacher> allTeachersUpdated = UserDAO.getInstance().getAllTeachers();
							for(Teacher t : allTeachersUpdated){
								t.setGroups(UserDAO.getInstance().getGroupsOfUser(t.getId()));
							}
							request.getServletContext().setAttribute("allTeachers", allTeachersUpdated);
							
						} catch (UserException | GroupException e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
							return "exception";
						}
						
						
						request.getSession().setAttribute("invalidFields", false);

					} catch (GroupException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						return "exception";
					} catch (ValidationException e) {
						request.getSession().setAttribute("invalidFields", true);
					}
				}
			}
			//response.sendRedirect("./UpdateGroupServlet");
			return "redirect:./UpdateGroupServlet";
					}
					return "error";
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
		private boolean isGroupNameValidUpdateGroup(String groupName){
			if(isGroupNameLengthValidUpdateGroup(groupName) && areGroupNameCharactersValidUpdateGroup(groupName)){
				return true;
			}
			return false;
		}
		private boolean isGroupNameLengthValidUpdateGroup(String groupName) {
			if (groupName.length() >= 5 && groupName.length() <= 20) {
				return true;
			}
			return false;
		}

		private boolean areGroupNameCharactersValidUpdateGroup(String groupName) {
			for(int i = 0; i < groupName.length(); i++){
				if(!(((int)groupName.charAt(i) >= 32 && (int)groupName.charAt(i) <= 126))){
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
		
		private boolean doAllTeachersExistUpdateGroup(String[] selectedTeachersUsername, ArrayList<String> allTeachersUsernames){
			boolean doAllExist = true;
			for(int i = 0; i < selectedTeachersUsername.length;){
				if(!(allTeachersUsernames.contains(selectedTeachersUsername[i++]))){
					doAllExist = false;
					break;
				}
			}
			return doAllExist;
		}
	
		
		
}
