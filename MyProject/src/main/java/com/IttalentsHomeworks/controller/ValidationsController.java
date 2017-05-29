package com.IttalentsHomeworks.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.IttalentsHomeworks.DAO.GroupDAO;
import com.IttalentsHomeworks.DAO.IValidationsDAO;
import com.IttalentsHomeworks.DAO.ValidationsDAO;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.UserException;
import com.IttalentsHomeworks.model.Group;
import com.IttalentsHomeworks.model.HomeworkDetails;
import com.IttalentsHomeworks.model.User;

@Controller
@MultipartConfig

public class ValidationsController {

	@RequestMapping(value = "/doesUserExist", method = RequestMethod.GET)
	protected void checkIfUserAlreadyExists(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String chosenStudentUsername = request.getParameter("chosenStudentUsername").trim();
		try {
			if (ValidationsDAO.getInstance().isUsernameUnique(chosenStudentUsername)) {
				response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
			} else {
				response.setStatus(IValidationsDAO.SUCCESS_STATUS);
			}
		} catch (UserException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
		}
	}

	@RequestMapping(value = "/isChosenStudentAlreadyInGroup", method = RequestMethod.GET)
	protected void isChosenStudentAlreadyInGroup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			int chosenGroupId = Integer.parseInt(request.getParameter("chosenGroupId"));
			String chosenStudentUsername = request.getParameter("chosenStudentUsername").trim();
			try {
				Group chosenGroup = GroupDAO.getInstance().getGroupById(chosenGroupId);
				if (GroupDAO.getInstance().isUserAlreadyInGroup(chosenGroup.getId(), chosenStudentUsername)) {
					response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
				} else {
					response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				}
			} catch (GroupException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
			} catch (UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/isGroupNameUnique", method = RequestMethod.GET)
	protected void isGroupNameUnique(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String groupName = request.getParameter("name").trim();
			try {
				if (ValidationsDAO.getInstance().isGroupNameUnique(groupName)) {
					response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				} else {
					response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
				}
			} catch (GroupException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/isGroupNameUniqueUpdate", method = RequestMethod.GET)
	protected void isGroupNameUniqueUpdate(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String groupName = request.getParameter("name").trim();
			Group currGroup = (Group) request.getSession().getAttribute("currGroup");
			int currGroupId = currGroup.getId();
			try {
				int wantedGroupNameId = GroupDAO.getInstance().getGroupIdByGroupName(groupName);

				if (ValidationsDAO.getInstance().isGroupNameUnique(groupName)) {
					response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				} else {
					if (wantedGroupNameId == currGroupId) {
						response.setStatus(IValidationsDAO.SUCCESS_STATUS);
					} else {
						response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
					}
				}
			} catch (GroupException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/isGroupNameValid", method = RequestMethod.GET)
	protected void isGroupNameValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String groupName = request.getParameter("name").trim();
			if (isLengthGroupNameValid(groupName) && areCharactersGroupNameValid(groupName)) {
				response.setStatus(IValidationsDAO.SUCCESS_STATUS);
			} else {
				response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	private boolean isLengthGroupNameValid(String groupName) {
		if (groupName.length() >= IValidationsDAO.MIN_SIZE_OF_GROUP_NAME
				&& groupName.length() <= IValidationsDAO.MAX_SIZE_OF_GROUP_NAME) {
			return true;
		}
		return false;
	}

	private boolean areCharactersGroupNameValid(String groupName) {
		for (int i = 0; i < groupName.length(); i++) {
			if (!(((int) groupName.charAt(i) >= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_FROM
					&& (int) groupName.charAt(i) <= IValidationsDAO.GROUP_NAME_VALID_CHARS_ASCII_TABLE_TO))
					|| (int) groupName.charAt(i) == IValidationsDAO.ASCII_TABLE_QUOTES) {
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = "/isHomeworkClosingTimeValid", method = RequestMethod.GET)
	protected void isHomeworkClosingTimeValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String opens = request.getParameter("opens").replace("/", "-").trim();
			String closes = request.getParameter("closes").replace("/", "-").trim();
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
				LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
				long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);
				if (closingDateTime.isAfter(LocalDateTime.now()) && closingDateTime.isAfter(openingDateTime)
						&& diffInMonths < IValidationsDAO.MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK) {
					response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				} else {
					response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
				}
			} catch (NumberFormatException e) {
				response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/isHomeworkHeadingUnique", method = RequestMethod.GET)
	protected void isHomeworkHeadingValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String heading = request.getParameter("heading").trim();
			try {
				if (ValidationsDAO.getInstance().isHomeworkHeadingUnique(heading)) {
					response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				} else {
					response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
				}
			} catch (GroupException e) {
				response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/isHomeworkHeadingValid", method = RequestMethod.GET)
	protected void isHomeworkHeadingUnique(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String heading = request.getParameter("heading").trim();
			if (isLengthHomeworkHeadingValid(heading) && areCharactersHomeworkHeadingValid(heading)) {
				response.setStatus(IValidationsDAO.SUCCESS_STATUS);
			} else {
				response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	private boolean isLengthHomeworkHeadingValid(String heading) {
		if (heading.length() >= IValidationsDAO.HOMEWORK_HEADING_MIN_LENGTH
				&& heading.length() <= IValidationsDAO.HOMEWORK_HEADING_MAX_LENGTH) {
			return true;
		}
		return false;
	}

	private boolean areCharactersHomeworkHeadingValid(String heading) {
		for (int i = 0; i < heading.length(); i++) {
			if (!(((int) heading.charAt(i) >= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_FROM
					&& (int) heading.charAt(i) <= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_TO))
					|| (int) heading.charAt(i) == IValidationsDAO.ASCII_TABLE_QUOTES) {
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = "/isHomeworkOpeningTimeValid", method = RequestMethod.GET)
	protected void isHomeworkOpeningTimeValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String opens = request.getParameter("opens").replace("/", "-").trim();
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

				LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
				LocalDate openingDate = openingTime.toLocalDate();
				if (openingDate.isAfter(LocalDate.now().minusDays(IValidationsDAO.MINUS_ONE_DAY))
						&& openingDate.isBefore(LocalDate.now()
								.plusMonths(
										IValidationsDAO.MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK)
								.minusDays(IValidationsDAO.MINUS_ONE_DAY))) {
					response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				} else {
					response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
				}
			} catch (NumberFormatException e) {
				response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/isHomeworkUpdateClosingTimeValid", method = RequestMethod.GET)
	protected void isHomeworkUpdateClosingTimeValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String opens = request.getParameter("opens").trim().replace("/", "-").trim();
			String closes = request.getParameter("closes").trim().replace("/", "-").trim();
			HomeworkDetails currHd = (HomeworkDetails) request.getSession().getAttribute("currHomework");
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
				LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
				long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);
				if (closingDateTime.equals(currHd.getClosingTime())) {
					response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				} else {
					if (closingDateTime.isAfter(LocalDateTime.now()) && closingDateTime.isAfter(openingDateTime)
							&& diffInMonths < IValidationsDAO.MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK) {
						response.setStatus(IValidationsDAO.SUCCESS_STATUS);
					} else {
						response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
					}
				}
			} catch (NumberFormatException e) {
				response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/isHomeworkUpdateHeadingIsRepeated", method = RequestMethod.GET)
	protected void isHomeworkUpdateHeadingIsRepeated(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String heading = request.getParameter("heading").trim();
			HomeworkDetails currHd = (HomeworkDetails) request.getSession().getAttribute("currHomework");
			try {
				if (currHd.getHeading().equals(heading)
						|| ValidationsDAO.getInstance().isHomeworkHeadingUnique(heading)) {
					response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				} else {
					response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
				}
			} catch (GroupException e) {
				response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/isHomeworkUpdateHeadingValid", method = RequestMethod.GET)
	protected void isHomeworkUpdateHeadingValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			String heading = request.getParameter("heading").trim();
			if (isLengthHomeworkUpdateHeadingValid(heading) && areCharactersHomeworkUpdateHeadingValid(heading)) {
				response.setStatus(IValidationsDAO.SUCCESS_STATUS);
			} else {
				response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	private boolean isLengthHomeworkUpdateHeadingValid(String heading) {
		if (heading.length() >= IValidationsDAO.HOMEWORK_HEADING_MIN_LENGTH
				&& heading.length() <= IValidationsDAO.HOMEWORK_HEADING_MAX_LENGTH) {
			return true;
		}
		return false;
	}

	private boolean areCharactersHomeworkUpdateHeadingValid(String heading) {
		for (int i = 0; i < heading.length(); i++) {
			if (!(((int) heading.charAt(i) >= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_FROM
					&& (int) heading.charAt(i) <= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_TO))
					|| (int) heading.charAt(i) == IValidationsDAO.ASCII_TABLE_QUOTES) {
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = "/isHomeworkUpdateOpeningTimeValid", method = RequestMethod.GET)
	protected void isHomeworkUpdateOpeningTimeValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String opens = request.getParameter("opens").replace("/", "-").trim();
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			HomeworkDetails currHd = (HomeworkDetails) request.getSession().getAttribute("currHomework");
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
				LocalDate openingDate = openingTime.toLocalDate();
				if (openingTime.equals(currHd.getOpeningTime())) {
					response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				} else {
					if (openingDate.isAfter(LocalDate.now().minusDays(IValidationsDAO.MINUS_ONE_DAY))
							&& openingDate.isBefore(LocalDate.now()
									.plusMonths(
											IValidationsDAO.MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK)
									.minusDays(IValidationsDAO.MINUS_ONE_DAY))) {
						response.setStatus(IValidationsDAO.SUCCESS_STATUS);
					} else {
						response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
					}
				}
			} catch (NumberFormatException e) {
				response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/isPasswordValid", method = RequestMethod.GET)
	protected void isPasswordValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String password = request.getParameter("password").trim();
		if (isLengthPasswordValid(password) && areCharactersPasswordValid(password)) {
			response.setStatus(IValidationsDAO.SUCCESS_STATUS);
		} else {
			response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
		}
	}

	private boolean isLengthPasswordValid(String password) {
		if (password.length() >= IValidationsDAO.MIN_LENGTH_OF_PASSWORD
				&& password.length() <= IValidationsDAO.MAX_LENGTH_OF_PASSWORD) {
			return true;
		}
		return false;
	}

	private boolean areCharactersPasswordValid(String password) {
		for (int i = 0; i < password.length(); i++) {
			if (!(((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
					&& (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
					|| ((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A
							&& (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
					|| ((int) password.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a
							&& (int) password.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z))) {
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = "/isUsernameUnique", method = RequestMethod.GET)
	protected void isUsernameUnique(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username").trim();
		boolean isUnique = false;
		try {
			isUnique = ValidationsDAO.getInstance().isUsernameUnique(username);
		} catch (UserException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
		}
		if (isUnique) {
			response.setStatus(IValidationsDAO.SUCCESS_STATUS);
		} else {
			response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
		}
	}

	@RequestMapping(value = "/isUsernameValid", method = RequestMethod.GET)
	protected void isUsernameValid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username").trim();
		if (isLengthUsernameValid(username) && areCharactersUsernameValid(username)) {
			response.setStatus(IValidationsDAO.SUCCESS_STATUS);
		} else {
			response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
		}
	}

	private boolean isLengthUsernameValid(String username) {
		if (username.length() >= IValidationsDAO.MIN_LENGTH_USERNAME
				&& username.length() <= IValidationsDAO.MAX_LENGTH_USERNAME) {
			return true;
		}
		return false;
	}

	private boolean areCharactersUsernameValid(String username) {
		for (int i = 0; i < username.length(); i++) {
			if (!(((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
					&& (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE)
					|| ((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_A
							&& (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_Z)
					|| ((int) username.charAt(i) >= IValidationsDAO.ASCII_TABLE_VALUE_OF_a
							&& (int) username.charAt(i) <= IValidationsDAO.ASCII_TABLE_VALUE_OF_z)
					|| (int) username.charAt(i) == IValidationsDAO.ASCII_TABLE_VALUE_OF_DOT)) {
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = "/validateLogin", method = RequestMethod.GET)
	protected void validateLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username").trim();
		String password = request.getParameter("password").trim();
		boolean areUsernamePasswordValid;
		try {
			areUsernamePasswordValid = ValidationsDAO.getInstance().doesUserExistInDB(username, password);
			if (areUsernamePasswordValid) {
				response.setStatus(IValidationsDAO.SUCCESS_STATUS);
			} else {
				response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
			}
		} catch (UserException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
		}
	}

	@RequestMapping(value = "/isHomeworkZipFileValid", method = RequestMethod.POST)
	protected void isHomeworkZipFileValid(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("testsFile") MultipartFile uploadfile) throws ServletException, IOException {
		Unzipper unzipper = new Unzipper(null, null);
		if (unzipper.areExtensionsValid(new ZipInputStream(uploadfile.getInputStream()))) {
			response.setStatus(IValidationsDAO.SUCCESS_STATUS);
		} else {
			response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
		}

	}
}
