package com.IttalentsHomeworks.controller;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.synth.SynthSpinnerUI;
import javax.swing.text.DefaultEditorKit.CopyAction;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.IttalentsHomeworks.DAO.GroupDAO;
import com.IttalentsHomeworks.DAO.IValidationsDAO;
import com.IttalentsHomeworks.DAO.NotUniqueHomeworkHeadingException;
import com.IttalentsHomeworks.DAO.UserDAO;
import com.IttalentsHomeworks.DAO.ValidationsDAO;
import com.IttalentsHomeworks.Exceptions.GroupException;
import com.IttalentsHomeworks.Exceptions.InvalidFilesExtensionInZIP;
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
import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Controller
@MultipartConfig
public class HomeworkController {
//
//	private static final int READ_HOMEWORK_GET_NAME_TO_INDEX = 4;
//	private static final int READ_HOMEWORK_GET_NAME_FROM_INDEX = 6;

	@RequestMapping(value = "/addHomework", method = RequestMethod.GET)
	protected String addHomeworkGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			return "addHomework";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/addHomework", method = RequestMethod.POST)
	protected String addHomeworkPost(HttpServletRequest request,
			@RequestParam(value = "file") MultipartFile fileUploaded, @RequestParam(value = "testsFile") MultipartFile testsFileUploaded, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		HomeworkDetails homeworkDetails = null;
		if (user.isTeacher()) {
			String heading = (request.getParameter("name") != null) ? (request.getParameter("name").trim()) : ("");
			String[] selectedGroups = (request.getParameterValues("groups") != null) ? (request.getParameterValues("groups")) : (new String[0]);
			String opens = (request.getParameter("opens") != null) ? (request.getParameter("opens").replace("/", "-").trim()) : ("");
			String closes = (request.getParameter("closes") != null) ? (request.getParameter("closes").replace("/", "-").trim()) : ("");
			String numberOfTasksString = (request.getParameter("numberOfTasks") != null) ? (request.getParameter("numberOfTasks").trim()) : ("");
			request.setAttribute("nameTry", heading);
			request.setAttribute("opensTry", opens.replace("-", "/"));
			request.setAttribute("closesTry", closes.replace("-", "/"));
			if (isHomeworkNumberOfTasksANumber(numberOfTasksString)) {
				request.setAttribute("numberOfTasksTry", Integer.parseInt(numberOfTasksString));
			}
			request.setAttribute("selectedGroupsTry", selectedGroups);
			// empty fields
			if (isThereEmptyField(request.getParameter("name").trim(),
					request.getParameter("opens").replace("/", "-").trim(),
					request.getParameter("closes").replace("/", "-").trim(), fileUploaded, testsFileUploaded,
					request.getParameter("numberOfTasks").trim(), request.getParameterValues("groups"))) {
				request.setAttribute("emptyFields", true);
			
			} else {
				MultipartFile filePart = fileUploaded;
				MultipartFile testsFilePart = testsFileUploaded;
				Unzipper unzip = null;

				File unzippedFilesDir = null;
				File zipFile = null;
				// heading valid
				File file = null;
				File fileTests = null;
				try {
					int numberOfTasks = 0;
					boolean isHeadingValid = false;
					boolean isHeadingUnique = false;
					if (areCharactersHeadingValid(heading) && isLengthHeadingValid(heading)) {
						isHeadingValid = true;
						if (isHomeworkHeadingUnique(heading)) {
							isHeadingUnique = true;
						}
					}
					request.setAttribute("validHeading", isHeadingValid);
					request.setAttribute("uniqueHeading", isHeadingUnique);
					// heading unique
					// opening time
					boolean isOpeningTimeValid = false;
					if (isHomeworkOpeningTimeValid(opens)) {
						isOpeningTimeValid = true;
					}
					request.setAttribute("validOpeningTime", isOpeningTimeValid);
					// closing time
					boolean isClosingTimeValid = false;
					if (isHomeworkClosingTimeValid(opens, closes)) {
						isClosingTimeValid = true;
					}
					request.setAttribute("validClosingTime", isClosingTimeValid);
					// file
					boolean isFileValid = false;
					if (isHomeworkSizeValid(filePart) && isHomeworkContentTypeValid(filePart)) {
						isFileValid = true;
					}
					request.setAttribute("validFile", isFileValid);
					
					//testsFile
					boolean isTestsFileValid = false;
					if (isHomeworkTestsFileSizeValid(testsFilePart) && isHomeworkTestsFileContentTypeValid(testsFilePart)) {
						isTestsFileValid = true;
					}
					request.setAttribute("validTestsFile", isTestsFileValid);
					
					// numTasks
					boolean areTasksValid = false;
					if (isHomeworkNumberOfTasksANumber(numberOfTasksString)) {
						numberOfTasks = Integer.parseInt(request.getParameter("numberOfTasks"));
						if (isHomeworkNumberOfTasksValid(numberOfTasks)) {
							areTasksValid = true;
						}
					}
					request.setAttribute("validTasks", areTasksValid);
					// groups
					boolean areGroupsValid = false;
					if (doAllGroupsExist(selectedGroups)) {
						areGroupsValid = true;
					}
					request.setAttribute("validGroups", areGroupsValid);
					System.out.println("is clocing valid " + isClosingTimeValid);
					if (isHeadingValid == true && isHeadingUnique == true && isOpeningTimeValid == true
							&& isClosingTimeValid == true && isFileValid == true && isTestsFileValid == true && areTasksValid == true
							&& areGroupsValid == true) {
					//	String savePathFilePdf = IValidationsDAO.SAVE_DIR_HOMEWORK_FILES_PDF;
						File fileSaveDirPdf = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_FILES_PDF);
						if (!fileSaveDirPdf.exists()) {
							fileSaveDirPdf.mkdir();
						}
					//	String savePathFileZip = IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES;
						
						//upload file with hw tasks
						String fileName = " ";
						fileName = fileUploaded.getOriginalFilename().substring(0,
								fileUploaded.getOriginalFilename().length() - 4) + ".pdf";
						System.out.println("file name tasks  " + fileName);

						OutputStream out = null;
						InputStream filecontent = null;
						file = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_FILES_PDF + File.separator + fileName);
						while (file.exists()) {
							Random randomGenerator = new Random();
							int randomLength = randomGenerator.nextInt((7 - 1) + 1) + 1;
							String randomString = this.getRandomCharacters(randomLength);
							fileName = fileUploaded.getOriginalFilename().substring(0,
									fileUploaded.getOriginalFilename().length() - 4) + randomString + ".pdf";
							file = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_FILES_PDF + File.separator + fileName);
						}
						//TODO extract file (+where to download+extract)
						file.createNewFile();
						
						//upload file with hw tests
						File fileSaveDirZip = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES);
						if (!fileSaveDirZip.exists()) {
							fileSaveDirZip.mkdir();
						}
						String fileNameTests = " ";
						System.out.println("original name tests file " +  testsFileUploaded.getOriginalFilename());
						fileNameTests = testsFileUploaded.getOriginalFilename().substring(0,
								testsFileUploaded.getOriginalFilename().length() - 4) + ".zip";
						System.out.println("File name is " + testsFileUploaded.getOriginalFilename().substring(0,
								testsFileUploaded.getOriginalFilename().length() - 4));
						//OutputStream outTests = null;
						//InputStream filecontentTests = null;
						fileTests = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator + fileNameTests);
						while (fileTests.exists()) {
							Random randomGenerator = new Random();
							int randomLength = randomGenerator.nextInt((7 - 1) + 1) + 1;
							String randomString = this.getRandomCharacters(randomLength);
							fileNameTests = testsFileUploaded.getOriginalFilename().substring(0,
									testsFileUploaded.getOriginalFilename().length() - 4) + randomString + ".zip";
							fileTests = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator + fileNameTests);
							//see if unzips, check file is .txt
							// da e v papka s imeto na doma6noto
							
						}
						
						fileTests.createNewFile();
						//
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
						LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
						LocalDateTime closingTime = LocalDateTime.parse(closes, formatter);
						ArrayList<Integer> groupsForHw = new ArrayList<>();
						homeworkDetails = new HomeworkDetails(heading, openingTime, closingTime,
								numberOfTasks, fileName, null);
						for (int i = 0; i < selectedGroups.length; i++) {
							int groupId = Integer.parseInt(selectedGroups[i]);
							//Group g = GroupDAO.getInstance().getGroupById(id);
							groupsForHw.add(groupId);
						}
						// if its ok
						out = new FileOutputStream(file, true);
						filecontent = filePart.getInputStream();
						int read = 0;
						final byte[] bytes = new byte[1024];
						while ((read = filecontent.read(bytes)) != -1) {
							out.write(bytes, 0, read);
						}
						zipFile = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES  + File.separator + heading + ".zip");
						if(!zipFile.exists()){
							zipFile.mkdirs();
						}
						System.out.println("See testsFilePart " + testsFilePart.getSize());
						System.out.println("See testsFileUpload " + testsFileUploaded.getSize());
						testsFilePart.transferTo(zipFile);
						
						out.close();
						filecontent.close();
						unzippedFilesDir = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES  + File.separator + homeworkDetails.getHeading());
						if(!unzippedFilesDir.exists()){
							unzippedFilesDir.mkdir();
						}
						homeworkDetails.setTestTasksFile(unzippedFilesDir.getName());
						unzip = new Unzipper(zipFile.getAbsolutePath(), unzippedFilesDir.getAbsolutePath());
						
						unzip.unzip(unzip.zipFilePath, unzip.destDirectory);
						
						GroupDAO.getInstance().createHomeworkDetails(homeworkDetails, groupsForHw);

						ArrayList<Group> allGroupsUpdated = GroupDAO.getInstance().getAllGroupsWithoutStudents();
						request.getServletContext().setAttribute("allGroups", allGroupsUpdated);
						ArrayList<Teacher> allTeachers = UserDAO.getInstance().getAllTeachers();
						request.getServletContext().setAttribute("allTeachers", allTeachers);
						for (Teacher t : allTeachers) {
							t.setGroups(UserDAO.getInstance().getGroupsOfUserWithoutStudents(t.getId()));
						}
						//we remove zip file
//						if (zipFile.exists()) {
//							zipFile.delete();
//						}
						if(fileTests.exists()){
							fileTests.delete();
						}
						request.setAttribute("invalidFields", false);

					}
				} catch (GroupException | UserException e) {
					System.out.println("IN VALIDDDDD11");

					if (file.exists()) {
						file.delete();
					}
					if (fileTests.exists()) {
						fileTests.delete();
					}
					if (unzippedFilesDir.exists()) {
						String[]entries = unzippedFilesDir.list();
						for(String s: entries){
						    File currentFile = new File(unzippedFilesDir.getPath(),s);
						    currentFile.delete();
						}
						unzippedFilesDir.delete();
					}
					if (zipFile.exists()) {
						zipFile.delete();
					}
					System.out.println(e.getMessage());
					e.printStackTrace();
					request.setAttribute("invalidFields", true);

					return "exception";
				} catch (ValidationException e) {
					System.out.println("IN VALIDDDDD");
					if (file.exists()) {
						System.out.println("Will deleteL : " + file.getAbsolutePath());

						file.delete();
					}
					if (fileTests.exists()) {
						System.out.println("Will deleteL : " + fileTests.getAbsolutePath());

						fileTests.delete();
					}
					if (unzippedFilesDir.exists()) {
						String[]entries = unzippedFilesDir.list();
						for(String s: entries){
						    File currentFile = new File(unzippedFilesDir.getPath(),s);
						    currentFile.delete();
						}
						System.out.println("Will deleteL : " + unzippedFilesDir.getAbsolutePath());

						unzippedFilesDir.delete();
					}
					if (zipFile.exists()) {
						System.out.println("Will deleteL : " + zipFile.getAbsolutePath());

						zipFile.delete();
					}
					request.setAttribute("invalidFields", true);
				} catch (NotUniqueHomeworkHeadingException e) {
					if (file.exists()) {
						file.delete();
					}
					if (fileTests.exists()) {
						fileTests.delete();
					}
					
					if (unzippedFilesDir.exists()) {
						String[]entries = unzippedFilesDir.list();
						for(String s: entries){
						    File currentFile = new File(unzippedFilesDir.getPath(),s);
						    currentFile.delete();
						}
						
						unzippedFilesDir.delete();
					}
					if (zipFile.exists()) {
						zipFile.delete();
					}
					request.setAttribute("invalidFields", true);
					e.printStackTrace();
				} catch (InvalidFilesExtensionInZIP e) {//todo direktoriqta ne se e mahnala
					if (file.exists()) {
						System.out.println("Will deleteL : " + file.getAbsolutePath());
						file.delete();
					}
					if (fileTests.exists()) {
						System.out.println("Will deleteL : " + fileTests.getAbsolutePath());

						fileTests.delete();
					}
					System.out.println("DOES EXISTS " + unzippedFilesDir.exists());
					if (unzippedFilesDir.exists()) {
						String[]entries = unzippedFilesDir.list();
						for(String s: entries){
						    File currentFile = new File(unzippedFilesDir.getPath(),s);
						    currentFile.delete();
						}
						System.out.println("Will deleteL : " + unzippedFilesDir.getAbsolutePath());

						unzippedFilesDir.delete();
					}
					if (zipFile.exists()) {

						zipFile.delete();
					}
					request.setAttribute("invalidFields", true);

					e.printStackTrace();
				}
			}
			return "addHomework";
		}
		return "forbiddenPage";
	}

	private boolean isHomeworkTestsFileContentTypeValid(MultipartFile testsFilePart) {
		if (testsFilePart.getOriginalFilename().contains(".")) {
			String contentType = testsFilePart.getOriginalFilename().substring(testsFilePart.getOriginalFilename().indexOf("."));
			if (!(contentType.equals(".zip"))) {
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean isHomeworkTestsFileSizeValid(MultipartFile testsFilePart) {
		long sizeInMb = testsFilePart.getSize() / (1024 * 1024);
		if (sizeInMb > IValidationsDAO.MAX_SIZE_IN_MB_FOR_HOMEWORK_ASSIGNMENT) {
			return false;
		}
		return true;
	}

	private String getRandomCharacters(int length) {
		final Random random = new Random();
		final String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890!@#$";

		StringBuilder token = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			token.append(CHARS.charAt(random.nextInt(CHARS.length())));
		}
		return token.toString();

	}

	private boolean isHomeworkNumberOfTasksANumber(String numberOfTasksString) {
		if (isHomeworkNumberOfTasksLengthValid(numberOfTasksString)) {
			if (!doesHomeworkNumberOfTasksContainsInvalidSymbols(numberOfTasksString)) {
				return true;
			}
		}
		return false;
	}

	private boolean doesHomeworkNumberOfTasksContainsInvalidSymbols(String numberOfTasksString) {
		for (int i = 0; i < numberOfTasksString.length(); i++) {
			if ((int) numberOfTasksString.charAt(i) < IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
					|| (int) numberOfTasksString.charAt(i) > IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE) {
				return true;
			}
		}
		return false;
	}

	private boolean isThereEmptyField(String heading, String opens, String closes, MultipartFile filePart,
			MultipartFile testsFileUploaded, String numberOfTasksString, String[] selectedGroups) {
		boolean isThereEmptyField = true;
		if (heading != null && !(heading.trim().equals("")) && opens != null && !(opens.trim().equals(""))
				&& closes != null && !(closes.trim().equals("")) && numberOfTasksString != null
				&& !(numberOfTasksString.trim().equals("")) && selectedGroups != null && selectedGroups.length > 0) {
			isThereEmptyField = false;
			//return false;
		}
		if (!(filePart != null && filePart.getSize() != 0 && testsFileUploaded != null && testsFileUploaded.getSize() != 0 ) ) {
			isThereEmptyField = true;
			//return false;
		}
		return isThereEmptyField;
	}

	private boolean isLengthHeadingValid(String heading) {
		if (heading.length() >= IValidationsDAO.HOMEWORK_HEADING_MIN_LENGTH
				&& heading.length() <= IValidationsDAO.HOMEWORK_HEADING_MAX_LENGTH) {
			return true;
		}
		return false;
	}

	private boolean areCharactersHeadingValid(String heading) {
		for (int i = 0; i < heading.length(); i++) {
			if (!(((int) heading.charAt(i) >= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_FROM
					&& (int) heading.charAt(i) <= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_TO))  || (int) heading.charAt(i) == IValidationsDAO.ASCII_TABLE_QUOTES) {
				return false;
			}
		}
		return true;
	}

	private boolean isHomeworkHeadingUnique(String heading) throws GroupException {
		if (ValidationsDAO.getInstance().isHomeworkHeadingUnique(heading)) {
			return true;
		}
		return false;
	}

	private boolean isHomeworkOpeningTimeValid(String opens) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
			LocalDate openingDate = openingTime.toLocalDate();
			if (openingDate.isAfter(LocalDate.now().minusDays(1))
					&& openingDate.isBefore(LocalDate.now().plusMonths(6).minusDays(1))) {
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isHomeworkClosingTimeValid(String opens, String closes) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
			LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
			long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);
			if (closingDateTime.isAfter(LocalDateTime.now()) && closingDateTime.isAfter(openingDateTime)
					&& diffInMonths < IValidationsDAO.MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK) {
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isHomeworkNumberOfTasksLengthValid(String numberOfTasks) {
		if (numberOfTasks.trim().length() == 0
				|| numberOfTasks.trim().length() >= IValidationsDAO.MAX_SIZE_OF_INTEGER) {
			return false;
		}
		return true;
	}

	private boolean isHomeworkNumberOfTasksValid(int numberOfTasks) {
		if (numberOfTasks >= IValidationsDAO.MIN_NUMBER_OF_TASKS_FOR_HOMEWORK
				&& numberOfTasks <= IValidationsDAO.MAX_NUMBER_OF_TASKS_FOR_HOMEWORK) {
			return true;
		}
		return false;
	}

	private boolean isHomeworkContentTypeValid(MultipartFile filePart) {
		if (filePart.getOriginalFilename().contains(".")) {
			String contentType = filePart.getOriginalFilename().substring(filePart.getOriginalFilename().indexOf("."));
			if (!(contentType.equals(".pdf"))) {
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean isHomeworkSizeValid(MultipartFile filePart) {
		long sizeInMb = filePart.getSize() / (1024 * 1024);
		if (sizeInMb > IValidationsDAO.MAX_SIZE_IN_MB_FOR_HOMEWORK_ASSIGNMENT) {
			return false;
		}
		return true;
	}

	private boolean doAllGroupsExist(String[] selectedGroups) throws GroupException, UserException {
		for (String groupId : selectedGroups) {
//				Group currGroup = GroupDAO.getInstance().getGroupById(Integer.parseInt(groupId));
//				String groupName = currGroup.getName();
//				if (ValidationsDAO.getInstance().isGroupNameUnique(groupName)) {
//					return false;
//				}
				if (ValidationsDAO.getInstance().isStringValidInteger(groupId)) {
					if (!ValidationsDAO.getInstance().doesGroupExistInDBById(Integer.parseInt(groupId))) {
						return false;
					}
				}else{
					return false;
				}
		}
		return true;
	}

	@RequestMapping(value = "/seeChosenHomeworkPageOfStudentByTeacher", method = RequestMethod.GET)
	protected String seeChosenHomeworkPageOfStudentByTeacher(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
	if (user.isTeacher()) {
			if (request.getSession().getAttribute("currHomework") == null
					|| request.getSession().getAttribute("chosenGroupName") == null
					|| request.getSession().getAttribute("currStudentUsername") == null) {
				return "pageNotFound";
			}
			return "currentHomeworkPageOfStudentByTeacher";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/seeChosenHomeworkPageOfStudentByStudent", method = RequestMethod.GET)
	protected String getHomework(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User userTry = (User) request.getSession().getAttribute("user");
		if (!userTry.isTeacher()) {
			int homeworkId = 0;
			Student user = (Student) request.getSession().getAttribute("user");
			String idHomework = request.getParameter("homeworkId");
			if (idHomework != null) {
				String number = request.getParameter("homeworkId").trim();
				if (!ValidationsDAO.getInstance().isStringValidInteger(number)) {
					return "pageNotFound";
				}
				homeworkId = Integer.parseInt(idHomework.trim());
			} else {
				if (request.getSession().getAttribute("currHomework") != null) {
					Homework hd = (Homework) request.getSession().getAttribute("currHomework");
					homeworkId = hd.getHomeworkDetails().getId();
				} else {
					return "pageNotFound";
				}
			}
		//	boolean doesUserHaveHomework = UserDAO.getInstance();
			Homework homework = null;
			try {
				homework = UserDAO.getInstance().getHomeworkOfStudent(user.getId(), homeworkId);
			} catch (UserException e1) {
				System.out.println(e1.getMessage());
				e1.printStackTrace();
				return "exception";
			}
//			for (Homework h : user.getHomeworks()) {
//				if (h.getHomeworkDetails().getId() == homeworkId) {
//					homework = new Homework(h.getTeacherGrade(), h.getTeacherComment(), h.getTasks(),
//							h.getHomeworkDetails());
//					doesUserHaveHomework = true;
//					break;
//				}
//			}
			if (homework != null) {
				request.getSession().setAttribute("currHomework", homework);
				boolean hasUploadTimePassed = true;
				//syso
					for(Task t: homework.getTasks()){
						System.out.println("HAS PASSSSSED");
						System.out.println(t.isHasPassedSystemTest());
					}
						
				//end syso
				if (homework.getHomeworkDetails().getClosingTime().isAfter(LocalDateTime.now())) {
					hasUploadTimePassed = false;
				}
				boolean hasUploadTimeCome = false;
				if (LocalDateTime.now().isAfter(homework.getHomeworkDetails().getOpeningTime())) {
					hasUploadTimeCome = true;
				}
				request.getSession().setAttribute("hasUploadTimePassed", hasUploadTimePassed);
				request.getSession().setAttribute("hasUploadTimeCome", hasUploadTimeCome);
				System.out.println("UP TO HERE");
				int numberOfTasks = homework.getHomeworkDetails().getNumberOfTasks();
				System.out.println("Number of tasks is " + numberOfTasks);
				int pointsPerTask = 100 / numberOfTasks;
				System.out.println("Points per task is " + pointsPerTask);
				request.getSession().setAttribute("pointsPerTask", pointsPerTask);
				String gName = null;
				boolean doesCurrentGroupContainChosenHomework = false;
				if (request.getSession().getAttribute("chosenGroup") != null && (!request.getSession().getAttribute("chosenGroup").equals(""))) {
					System.out.println("Chosen group is: " + request.getSession().getAttribute("chosenGroup"));
					int chosenGroupId = (int) request.getSession().getAttribute("chosenGroup");
					//Group chosenGroup;
					try {
						//chosenGroup = GroupDAO.getInstance().getGroupById(chosenGroupId);
						ArrayList<HomeworkDetails> allHomeworkDetailsOfGroup = GroupDAO.getInstance().getHomeworkDetailsOfGroup(chosenGroupId);
						//for (HomeworkDetails h : chosenGroup.getHomeworks()) {
						for (HomeworkDetails h : allHomeworkDetailsOfGroup) {
							if (h.getId() == homeworkId) {
								doesCurrentGroupContainChosenHomework = true;
								break;
							}
						}
						if (!doesCurrentGroupContainChosenHomework) {
							for (Group g : user.getGroups()) {
								for (HomeworkDetails h : g.getHomeworks()) {
									if (h.getId() == homeworkId) {
										gName = g.getName();
										break;
									}
								}
							}
							request.getSession().setAttribute("chosenGroupName", gName);
						} else {
							gName = GroupDAO.getInstance().getGroupNameById(chosenGroupId);
							request.getSession().setAttribute("chosenGroupName", gName);
						}
						

					} catch (GroupException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						return "exception";
					}
					return "currentHomeworkPageOfStudentByStudent";
				} else {
					for (Group g : user.getGroups()) {
						for (HomeworkDetails h : g.getHomeworks()) {
							if (h.getId() == homeworkId) {
								gName = g.getName();
								break;
							}
						}
					}
					request.getSession().setAttribute("chosenGroupName", gName);
					return "currentHomeworkPageOfStudentByStudent";

				}
			}
		}
		return "forbiddenPage";
	}

	// @RequestMapping(value = { "/GetHomeworksOfGroupsServlet" }, method =
	// RequestMethod.GET)
//	protected String getHomeworksOfGroupsNoGroupIdInUrl(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		System.out.println("BABABAB");
//		User userTry = (User) request.getSession().getAttribute("user");
//		request.getSession().setAttribute("throughtScores", 0);
//		if (!userTry.isTeacher()) {
//			User user = (User) request.getSession().getAttribute("user");
//			String groupChosen = request.getSession().getAttribute("chosenGroup").toString();
//			if (groupChosen != null) {
//				int length = groupChosen.length();
//				String number = groupChosen;
//				if (length > IValidationsDAO.MIN_SIZE_OF_INTEGER && length < IValidationsDAO.MAX_SIZE_OF_INTEGER) {
//					for (int i = 0; i < length; i++) {
//						if ((int) number.charAt(i) < IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
//								|| (int) number.charAt(i) > IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE) {
//							return "pageNotFound";
//						}
//					}
//				} else {
//					return "pageNotFound";
//				}
//				try {
//					int groupId = Integer.parseInt(groupChosen);
//					boolean doesUserHaveGroup = false;
//					Group group = null;
//					for (Group g : user.getGroups()) {
//						if (g.getId() == groupId) {
//							group = g;
//							doesUserHaveGroup = true;
//							break;
//						}
//					}
//					if (doesUserHaveGroup) {
//						ArrayList<HomeworkDetails> homeworks = new ArrayList<>();
//						for (HomeworkDetails h : group.getHomeworks()) {
//							long days = LocalDateTime.now().until(h.getClosingTime(), ChronoUnit.DAYS);
//							HomeworkDetails currHd = new HomeworkDetails(h.getHeading(), h.getOpeningTime(),
//									h.getClosingTime(), h.getNumberOfTasks(), h.getTasksFile());
//							currHd.setDaysLeft((int) days);
//							currHd.setId(GroupDAO.getInstance().getHomeworkDetailsId(currHd));
//							homeworks.add(currHd);
//						}
//						Group group1 = GroupDAO.getInstance().getGroupById(groupId);
//						System.out.println(group1.getName() + " IUGYUFTYUYGUHIJL:");
//						request.getSession().setAttribute("chosenGroupName", group1.getName());
//
//						request.getSession().setAttribute("chosenGroup", groupId);
//						request.getSession().setAttribute("currHomeworksOfGroup", homeworks);
//					} else {
//
//						return "forbiddenPage";
//					}
//				} catch (GroupException e) {
//					System.out.println(e.getMessage());
//					e.printStackTrace();
//					return "exception";
//				} catch (UserException e) {
//					System.out.println(e.getMessage());
//					e.printStackTrace();
//					return "exception";
//				}
//
//				return "seeHomeworksByStudent";
//			} else {
//				if (request.getSession().getAttribute("chosenGroup") != null) {
//					return "seeHomeworksByStudent";
//				}
//				return "pageNotFound";
//			}
//		}
//		return "forbiddenPage";
//	}

	@RequestMapping(value = { "/seeHomeworksListOfStudentByGroupByStudent"}, method = RequestMethod.GET)
	protected String getHomeworksOfGroups(@RequestParam(value = "groupId", required = false) String groupIdUrl,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User userTry = (User) request.getSession().getAttribute("user");
		request.getSession().setAttribute("throughtScores", 0);
		if (!userTry.isTeacher()) {
			User user = (User) request.getSession().getAttribute("user");
			if (groupIdUrl != null && groupIdUrl.trim() != "") {
				String groupChosen = groupIdUrl;
				if(!ValidationsDAO.getInstance().isStringValidInteger(groupChosen)){
					return "pageNotFound";
				}
				try {
					int groupId = Integer.parseInt(groupChosen);
					boolean doesUserHaveGroup = false;
					Group group = null;
					for (Group g : user.getGroups()) {
						if (g.getId() == groupId) {
							group = g;
							doesUserHaveGroup = true;
							break;
						}
					}//TODO dobavih gettasksfile - trqbva li mi nqkade?
					if (doesUserHaveGroup) {
						ArrayList<HomeworkDetails> homeworks = new ArrayList<>();
						for (HomeworkDetails h : group.getHomeworks()) {
							long days = LocalDateTime.now().until(h.getClosingTime(), ChronoUnit.DAYS);
							HomeworkDetails currHd = new HomeworkDetails(h.getHeading(), h.getOpeningTime(),
									h.getClosingTime(), h.getNumberOfTasks(), h.getTasksFile(), h.getTestTasksFile());
							currHd.setDaysLeft((int) days);
							currHd.setId(GroupDAO.getInstance().getHomeworkDetailsId(currHd.getHeading()));
							homeworks.add(currHd);
						}
						request.getSession().setAttribute("chosenGroup", groupId);
						//Group g = GroupDAO.getInstance().getGroupById(groupId);
						request.getSession().setAttribute("chosenGroupName", GroupDAO.getInstance().getGroupNameById(groupId));
						request.getSession().setAttribute("currHomeworksOfGroup", homeworks);
					} else {
						return "forbiddenPage";
					}
				} catch (GroupException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}
				return "seeHomeworksByStudent";
			} else {
				if (request.getSession().getAttribute("chosenGroup") != null) {
					return "seeHomeworksByStudent";
				}
				return "pageNotFound";
			}
		}
		return "forbiddenPage";
	}
	
	@RequestMapping(value = "/readFileOfTasksForHomeworkTestsZip", method = RequestMethod.GET, produces="application/zip")
	protected void readFileOfTasksForHomeworkTestsZip(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (request.getParameter("fileName") != null && !(request.getParameter("fileName").trim().equals(""))) {
			String fileName = request.getParameter("fileName").trim();
			boolean canUserAccessHomeworkTasks = false;
			if (!user.isTeacher()) {
				canUserAccessHomeworkTasks = true;
			} else {
				canUserAccessHomeworkTasks = true;
			}
			if (canUserAccessHomeworkTasks) {
				 // get absolute path of the application
		        ServletContext context = request.getServletContext();
		        String appPath = context.getRealPath("");
		        System.out.println("appPath = " + appPath);
		 
		        // construct the complete absolute path of the file
		        String fullPath = IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES  + File.separator + fileName + ".zip";      
		        File downloadFile = new File(fullPath);
		        FileInputStream inputStream = new FileInputStream(downloadFile);
		         
		        // get MIME type of the file
		        String mimeType = context.getMimeType(fullPath);
		        if (mimeType == null) {
		            // set to binary type if MIME mapping not found
		            mimeType = "application/octet-stream";
		        }
		        System.out.println("MIME type: " + mimeType);
		 
		        // set content attributes for the response
		        response.setContentType(mimeType);
		        response.setContentLength((int) downloadFile.length());
		 
		        // set headers for the response
		        String headerKey = "Content-Disposition";
		        String headerValue = String.format("attachment; filename=\"%s\"",
		                downloadFile.getName());
		        response.setHeader(headerKey, headerValue);
		 
		        // get output stream of the response
		        OutputStream outStream = response.getOutputStream();
		 
		        byte[] buffer = new byte[1024];
		        int bytesRead = -1;
		 
		        // write bytes read from the input stream into the output stream
		        while ((bytesRead = inputStream.read(buffer)) != -1) {
		            outStream.write(buffer, 0, bytesRead);
		        }
		 
		        inputStream.close();
		        outStream.close();
			} else {
				response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
				return;
			}
		} else {
			response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
			return;
		}

	}
	
	@RequestMapping(value = "/readFileOfTasksForHomeworkPDF", method = RequestMethod.GET)
	protected void readHomework(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (request.getParameter("fileName") != null && !(request.getParameter("fileName").trim().equals(""))) {
				String fileName = request.getParameter("fileName").trim();
				String homeworkName = fileName;
					boolean canUserAccessHomeworkTasks = false;
					if (!user.isTeacher()) {
						for (Group g : user.getGroups()) {
							for (HomeworkDetails hd : g.getHomeworks()) {
								if (hd.getTasksFile().equals(homeworkName)) {
									canUserAccessHomeworkTasks = true;
									break;
								}
							}
						}
					} else {
						canUserAccessHomeworkTasks = true;
					}
					if (canUserAccessHomeworkTasks) {
						File file = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_FILES_PDF + File.separator + fileName);
						if (!file.exists()) {
							response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
							return;
						}
						response.setContentType("application/ms-excel; charset=UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setHeader("Content-Disposition",
								"attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
						FileInputStream fileInputStream = new FileInputStream(file);
						OutputStream responseOutputStream = response.getOutputStream();
						int bytes;
						while ((bytes = fileInputStream.read()) != -1) {
							responseOutputStream.write(bytes);
						}
						fileInputStream.close();
					} else {
						response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
						return;
					}
		} else {
			response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
			return;
		}

	}

	@RequestMapping(value = "/updateSolutionTextOfTaskByStudentJava", method = RequestMethod.POST)
	protected void changeSolutionText(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (request.getSession().getAttribute("currHomework") != null && request.getParameter("text") != null
				&& request.getParameter("taskNum") != null
				&& ValidationsDAO.getInstance().isStringValidInteger(request.getParameter("taskNum"))) {
			Homework homework = (Homework) request.getSession().getAttribute("currHomework");
			HomeworkDetails homeworkDetails = homework.getHomeworkDetails();
			if(homeworkDetails.getOpeningTime().isBefore(LocalDateTime.now())&&homeworkDetails.getClosingTime().isAfter(LocalDateTime.now())){

			String text = request.getParameter("text");
			int taskNum = Integer.parseInt(request.getParameter("taskNum")) - 1;
			if (doesTaskNumExist(taskNum, homeworkDetails)) {
				
				//beg previous
//				//beg
//			
//				
//				//end
//				File directory = new File((IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + "hwId" + homeworkDetails.getId()+ "userId" + user.getId() + "taskNum" + taskNum));
//				//String strLine = "";
//				
//				if (directory.exists()) {			
//					//FOUND
//					String fileName = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + "hwId" + homeworkDetails.getId()+ "userId" + user.getId() + "taskNum" + taskNum).listFiles()[0].getAbsoluteFile().getName();
//					if(fileName == null){				
//
//					//strLine = new String(Files.readAllBytes(Paths.get(fileName)), "UTF8");
//						response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
//						return;
//					}
//				} else {
//					response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
//					return;
//				}
				
				//end previous
//				String fileName = IValidationsDAO.SAVE_DIR + File.separator + "hwId" + homeworkDetails.getId()
//						+ "userId" + user.getId() + "taskNum" + taskNum + ".java";
				
				String fileDirectory = "hwId" + homeworkDetails.getId() + "userId" + user.getId() + "taskNum"
						+ taskNum;
				String savePath = IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA;
				File fileSaveDir = new File(savePath  + File.separator + fileDirectory);
				String solutionPath = homework.getTasks().get(taskNum).getSolution();

				if (solutionPath.trim().equals("") || solutionPath == null || !fileSaveDir.exists()) {
					System.out.println("Directory does not exist");
					response.setStatus(IValidationsDAO.NOT_ACCEPTABLE);
					return;
				}
				//String fileName = file.getOriginalFilename();
				File newFile = null;
				String fileName = "";
				//file.transferTo(new File(savePath +File.separator + fileDirectory+ File.separator + fileName));
				System.out.println("))))))))))");
			    System.out.println(solutionPath.substring(solutionPath.lastIndexOf("/") + 1));
				String searchedFileName = solutionPath.substring(solutionPath.lastIndexOf("/") + 1);
				boolean doesFolderContainFile = false;
				for(File f: new File(IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + "hwId"+ homeworkDetails.getId() + "userId" + user.getId() + "taskNum" + taskNum).listFiles()){
					if(f.getName().equals(searchedFileName)){
						doesFolderContainFile = true;
						fileName = f.getAbsolutePath();
						break;
					}
				}
				if(!doesFolderContainFile){
					System.out.println("File does not exist");
					response.setStatus(IValidationsDAO.NOT_ACCEPTABLE);
					return;
				}else{
						System.out.println(solutionPath.substring(solutionPath.lastIndexOf("/") + 1));
						
						System.out.println("solution oath is: " + solutionPath);
						System.out.println("searched file name is: " + searchedFileName);
						String searchedFileNameClass = searchedFileName.substring(0,
								searchedFileName.length() - 5);
						File currentFileClassToRemove = new File(savePath + File.separator
								+ fileDirectory + File.separator + searchedFileNameClass + ".class");						
						if (currentFileClassToRemove.exists()) {
							currentFileClassToRemove.delete();
						}
					
				}
				
				
				//FOUND
				File currentFile = new File(fileName);
				if (text.trim().length() > IValidationsDAO.MIN_NUMBER_OF_CHARACTERS_SOLUTION_TASK
						&& text.length() < IValidationsDAO.MAX_NUMBER_OF_CHARACTERS_SOLUTION_TASK_1_MB) {
					System.out.println("FILE " + currentFile.getAbsolutePath());
					Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentFile.getAbsolutePath()), "UTF-8"));
					out.write(text);
					System.out.println(text);
					out.flush();
					out.close();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					LocalDateTime currDateTime = LocalDateTime.now();
					  String currDateTimeString = currDateTime.format(formatter);
					  currDateTime = LocalDateTime.parse(currDateTimeString, formatter);
					try {
						UserDAO.getInstance().setTimeOfUploadOfTask(homeworkDetails.getId(), user.getId(), taskNum, currDateTime);
						homework.getTasks().get(taskNum).setUploadedOn(currDateTime);
						System.out.println("BEFORE SYSTEM TEST");
						fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
						boolean doesPastSystemTest = GroupDAO.getInstance().doesPassSystemTest(fileDirectory+ File.separator + fileName,homework, taskNum);
			//			boolean doesPastSystemTest = true;
		//				if(doesPastSystemTest){
							UserDAO.getInstance().setPassedSystemTest(user.getId(), homeworkDetails.getId(), taskNum, doesPastSystemTest);
		//				}
						System.out.println("AFTER SYSTEM TEST");

						homework.getTasks().get(taskNum).setHasPassedSystemTest(doesPastSystemTest);
						int numberOfTasks = homework.getHomeworkDetails().getNumberOfTasks();
						System.out.println("Number of tasks is " + numberOfTasks);
						int pointsPerTask = 100 / numberOfTasks;
						System.out.println("Points per task is " + pointsPerTask);
						request.getSession().setAttribute("pointsPerTask", pointsPerTask);
						
						request.getSession().setAttribute("currTaskUpload", taskNum);
						JsonObject obj = new JsonObject();
						obj.addProperty("pointsPerTask", pointsPerTask);//TODO ADD v upload
						obj.addProperty("hasPassedTest", doesPastSystemTest);
						System.out.println(obj.toString());
						response.setStatus(IValidationsDAO.SUCCESS_STATUS);

						response.getWriter().write(obj.toString());
					//	UserDAO.getInstance().getTasksOfHomeworkOfStudent(user.getId(), homeworkDetailsId);
					} catch (UserException e) {
						response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
					} catch (InterruptedException e) {
						response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
					}
						response.setStatus(IValidationsDAO.SUCCESS_STATUS);
					} else {
						response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
					}
				} else {
					response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
				}
			} else {
				response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
		}
	}

	private boolean doesTaskNumExist(int taskNum, HomeworkDetails homeworkDetails) {
		if (taskNum >= 0 && taskNum < homeworkDetails.getNumberOfTasks()) {
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/readSolutionOfTaskJava", method = RequestMethod.GET)
	protected void readJavaFile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getSession().getAttribute("currHomework") != null && request.getParameter("taskNum") != null
				&& !request.getParameter("taskNum").trim().equals("")
				&& ValidationsDAO.getInstance().isStringValidInteger(request.getParameter("taskNum").trim())) {
			int taskNum = Integer.parseInt(request.getParameter("taskNum").trim()) - 1;
			User user = (User) request.getSession().getAttribute("user");
			Homework homework = (Homework) request.getSession().getAttribute("currHomework");
			HomeworkDetails homeworkDetails = homework.getHomeworkDetails();
			String fileName = null;
			int idOfStudentForHw = -1;
			System.out.println("IN");
			if (!user.isTeacher()) {
				idOfStudentForHw = user.getId();
				// fileName = ((new File(IValidationsDAO.SAVE_DIR +
				// File.separator + "hwId" + homeworkDetails.getId()
				// + "userId" + user.getId() + "taskNum" +
				// taskNum).listFiles()[0] != null) ? (new
				// File(IValidationsDAO.SAVE_DIR + File.separator + "hwId" +
				// homeworkDetails.getId()
				// + "userId" + user.getId() + "taskNum" +
				// taskNum).listFiles()[0].getAbsoluteFile().getName()) : null);
				// fileName = currentFile.getAbsolutePath();
				// fileName = UserDAO.getInstance().
				// System.out.println("WE READ FILE " + fileName);

				// fileName = IValidationsDAO.SAVE_DIR + File.separator + "hwId"
				// + homeworkDetails.getId()
				// + "userId" + user.getId() + "taskNum" + taskNum + ".java";
			} else {
				System.out.println(request.getSession().getAttribute("studentId"));

				if (request.getSession().getAttribute("studentId") != null) {
					idOfStudentForHw = (int) request.getSession().getAttribute("studentId");
					// fileName = new File(IValidationsDAO.SAVE_DIR +
					// File.separator + "hwId" + homeworkDetails.getId()
					// + "userId" + studentId + "taskNum" +
					// taskNum).listFiles()[0].getAbsoluteFile().getName();
					// fileName = currentFile.getAbsolutePath();
					// fileName = IValidationsDAO.SAVE_DIR + File.separator +
					// "hwId" + homeworkDetails.getId()
					// + "userId" + studentId + "taskNum" + taskNum + ".java";
				} else {
					response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
					return;
				}
			}
			File directory = new File((IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + "hwId" + homeworkDetails.getId()
					+ "userId" + idOfStudentForHw + "taskNum" + taskNum));
			System.out.println(directory.getAbsolutePath());
			String strLine = "";
			if (directory.exists()) {
				if (directory.listFiles().length == 0) {
					strLine = "Solution is not uploaded yet.";
				} else {
//					fileName = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + "hwId"
//							+ homeworkDetails.getId() + "userId" + idOfStudentForHw + "taskNum" + taskNum)
//									.listFiles()[0].getAbsolutePath();
//					if (fileName != null) {
//						strLine = new String(Files.readAllBytes(Paths.get(fileName)), "UTF8");
//					} else {
//						strLine = "Solution is not uploaded yet.";
//					}
					String solutionPath = homework.getTasks().get(taskNum).getSolution();
					System.out.println("))))))))))");			
					boolean doesFolderContainFile = false;
					if(solutionPath != null){
				   // System.out.println(solutionPath.substring(solutionPath.lastIndexOf("/") + 1));
					String searchedFileName = solutionPath.substring(solutionPath.lastIndexOf("/") + 1);
					for(File file: new File(IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + "hwId"+ homeworkDetails.getId() + "userId" + idOfStudentForHw + "taskNum" + taskNum).listFiles()){
						if(file.getName().equals(searchedFileName)){
							doesFolderContainFile = true;
							fileName = file.getAbsolutePath();
							break;
						}
					}
					}
					if (doesFolderContainFile == true) {
						strLine = new String(Files.readAllBytes(Paths.get(fileName)), "UTF8");
					} else {
						strLine = "Solution is not uploaded yet.";
					}
				}
			} else {
				strLine = "Solution is not uploaded yet.";
			}
			JsonObject obj = new JsonObject();
			if (homework.getTasks().size() > taskNum && taskNum >= 0) {
				Task task = homework.getTasks().get(taskNum);
				if (strLine.equals("Solution is not uploaded yet.")) {
					obj.addProperty("uploadedOn", "-");
					obj.addProperty("solution", strLine);
				} else {
					obj.addProperty("uploadedOn", task.getUploadedOn().toString());
					obj.addProperty("solution", strLine);
				}
				response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				response.getWriter().write(obj.toString());
			} else {
				response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
				return;

			}
		} else {
			response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
			return;
		}
	}

	@RequestMapping(value = "/removeHomeworkDetails", method = RequestMethod.POST)
	protected String removeHomeworkDetails(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getSession().getAttribute("currHomework") != null) {
				HomeworkDetails hd = (HomeworkDetails) request.getSession().getAttribute("currHomework");
				try {
					GroupDAO.getInstance().removeHomeworkDetails(hd);
					request.getSession().removeAttribute("currHomework");
				} catch (GroupException | UserException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}
				request.setAttribute("invalidFields", false);
				return "seeOrUpdateHomeworks";
			} else {
				return "pageNotFound";
			}
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/seeHomeworksOfStudentByGroupForScoresByTeacher", method = RequestMethod.GET)
	protected void seeAllHomeworksOfStudentByGroupServlet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			try {
				if (request.getParameter("groupId") != null && !request.getParameter("groupId").trim().equals("")
						&& request.getParameter("studentId") != null
						&& !request.getParameter("studentId").trim().equals("")) {

					if (ValidationsDAO.getInstance().isStringValidInteger(request.getParameter("groupId").trim())
							&& ValidationsDAO.getInstance()
									.isStringValidInteger(request.getParameter("studentId").trim())) {
						
						int groupId = Integer.parseInt(request.getParameter("groupId").trim());
						int studentId = Integer.parseInt(request.getParameter("studentId").trim());
						JsonArray array = new JsonArray();
						boolean doesGroupExist = ValidationsDAO.getInstance().doesGroupExistInDBById(groupId);	
						boolean doesUserExist = ValidationsDAO.getInstance().doesUserExistInDBById(studentId);
						boolean isSelectedUserTeacher = UserDAO.getInstance().isUserATeacher(studentId);
						boolean isStudentInGroup = ValidationsDAO.getInstance().isStudentAlreadyInGroup(studentId, groupId);
						if (doesGroupExist == true && doesUserExist == true && (!isSelectedUserTeacher) && (isStudentInGroup == true)) {
							boolean hasStudentGivenMinOneTask = false;

								long startTime2 = System.currentTimeMillis();

								ArrayList<Homework> allHomeworksOfStudent = UserDAO.getInstance()
										.getHomeworksOfStudentByGroup(studentId, groupId);
								long endTime2 = System.currentTimeMillis();
								System.out.println("That part 2 function took " + (endTime2 - startTime2) + " milliseconds");
								
								for (Homework h : allHomeworksOfStudent) {
									JsonObject obj = new JsonObject();					
									int grade = h.getTeacherGrade();
									System.out.println("");
									String comment = h.getTeacherComment();
									//int avgSystemScore = 0;
									int numberOfTasks = h.getHomeworkDetails().getNumberOfTasks();
									System.out.println("Number of tasks is " + numberOfTasks);
									int pointsPerTask = 100 / numberOfTasks;
									System.out.println("Points per task is " + pointsPerTask);
									request.getSession().setAttribute("pointsPerTask", pointsPerTask);
									int systemScorePoints = 0;
									for (Task t : h.getTasks()) {
										String x = t.getSolution();
										if(t.isHasPassedSystemTest()){
											systemScorePoints += pointsPerTask;
										}
										if (x != null) {
											hasStudentGivenMinOneTask = true;
											//break;
										}
									}
									obj.addProperty("systemScore", systemScorePoints);
									obj.addProperty("heading", h.getHomeworkDetails().getHeading());
									obj.addProperty("id", h.getHomeworkDetails().getId());
									obj.addProperty("hasStudentGivenMinOneTask", hasStudentGivenMinOneTask);
									obj.addProperty("teacherScore", grade);
									obj.addProperty("teacherComment", comment);
									array.add(obj);
									hasStudentGivenMinOneTask = false;
								}
								response.setStatus(IValidationsDAO.SUCCESS_STATUS);
								response.getWriter().write(array.toString());
						} else {
							response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
							return;
						}		
					}
				} else {
					response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
					return;
				}
			} catch (GroupException | UserException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
			} catch (ValidationException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
			}
		}
	}
//			} else {
//				response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
//				return;
//			}
//		} catch (GroupException | UserException e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//			response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
////		} catch (ValidationException e) {
////			System.out.println(e.getMessage());
////			e.printStackTrace();
////		}
//		} catch (ValidationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}} else {
//		response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
//		return;
//	}
//}	
							//}
//						}
//						if (!doesUserHaveGroup) {
//							response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
//							return;
//						}
//							for (HomeworkDetails hd : homeworkDetailsByGroup) {
////								System.out.println("1");
////								System.out.println("Getting user by id");
//								if (UserDAO.getInstance().getUserById(studentId) != null) {
////									System.out.println("user is here");
//									boolean doesUserHaveGroup = false;
//									for (Group g : UserDAO.getInstance().getUserById(studentId).getGroups()) {
////										System.out.println("8");
//										if (g.getId() == selectedGroup.getId()) {
//											doesUserHaveGroup = true;
//											break;
//										}
//									}
//									if (!doesUserHaveGroup) {
//										response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
//										return;
//									}
//									JsonObject obj = new JsonObject();
//									obj.addProperty("heading", hd.getHeading());
//									obj.addProperty("id", hd.getId());
//									obj.addProperty("opens", hd.getOpeningTime().toString());
//									obj.addProperty("closes", hd.getClosingTime().toString());
//
//									// do tuk //get exact hw
////									System.out.println("will get hw of student by group..");
//									for (Homework h : UserDAO.getInstance().getHomeworksOfStudentByGroup(studentId,
//											selectedGroup)) {
////										System.out.println("2");
//										if (hd.getId() == h.getHomeworkDetails().getId()) {
//											int grade = h.getTeacherGrade();
//											String comment = h.getTeacherComment();
//											for (Task t : h.getTasks()) {
//												System.out.println("3");
//												String x = t.getSolution();
//												if (x != null) {
//													hasStudentGivenMinOneTask = true;
//													break;
//												}
//											}
//											obj.addProperty("hasStudentGivenMinOneTask", hasStudentGivenMinOneTask);
//											obj.addProperty("teacherScore", grade);
//											obj.addProperty("teacherComment", comment);
//											hasStudentGivenMinOneTask = false;
//											break;
//										}
//									}
//									array.add(obj);
//								} else {
//									response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
//									return;
//								}
//							}
//						} else {
//							response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
//							return;
//						}
//						response.setStatus(IValidationsDAO.SUCCESS_STATUS);
//						response.getWriter().write(array.toString());
//					} else {
//						response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
//						return;
//					}
//				} else {
//					response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
//					return;
//				}
//			} catch (GroupException | UserException e) {
//				System.out.println(e.getMessage());
//				e.printStackTrace();
//				response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
////			} catch (ValidationException e) {
////				System.out.println(e.getMessage());
////				e.printStackTrace();
////			}
//			} catch (ValidationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}} else {
//			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
//			return;
//		}
//	}

	
	@RequestMapping(value = "/seeHomeworksOfGroupForUpdateByTeacher", method = RequestMethod.GET)
	protected void seeHomeworksOfGroupForUpdateByTeacher(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			try {
				ArrayList<HomeworkDetails> homeworkDetailsByGroup = new ArrayList<>();
				JsonArray array = new JsonArray();
				if (request.getParameter("chosenGroup") != null && !(request.getParameter("chosenGroup").trim().equals(""))) {
					if (!(request.getParameter("chosenGroup").equals("allGroups")) && !(request.getParameter("chosenGroup").equals("null")) && ValidationsDAO.getInstance().isStringValidInteger(request.getParameter("chosenGroup").trim())) {
						int groupId = Integer.parseInt(request.getParameter("chosenGroup").trim());
						//Group chosenGroup = null;
						//chosenGroup = GroupDAO.getInstance().getGroupById(groupId);

						if(ValidationsDAO.getInstance().doesGroupExistInDBById(groupId)){
							homeworkDetailsByGroup.addAll(GroupDAO.getInstance().getHomeworkDetailsOfGroup(groupId));
						}else{
							response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
							return;
						}
						
					} else {
						if((!request.getParameter("chosenGroup").equals("allGroups") && (!request.getParameter("chosenGroup").equals("null")))){
							response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
							return;
						}

						homeworkDetailsByGroup = GroupDAO.getInstance().getAllHomeworksDetails();
						
					}
					for (HomeworkDetails hd : homeworkDetailsByGroup) {
						JsonObject obj = new JsonObject();
						obj.addProperty("heading", hd.getHeading());
						obj.addProperty("id", hd.getId());
						obj.addProperty("opens", hd.getOpeningTime().toString());
						obj.addProperty("closes", hd.getClosingTime().toString());
						array.add(obj);
					}
				}else{
					response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
					return;
				}
				response.setStatus(IValidationsDAO.SUCCESS_STATUS);
				response.getWriter().write(array.toString());
			} catch (GroupException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}

	@RequestMapping(value = "/seeOrUpdateHomeworks", method = RequestMethod.GET)
	protected String seeHomeworks(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		request.getSession().setAttribute("throughtSeeOrUpdateHomeworks", 1);
		if (user.isTeacher()) {
			return "seeOrUpdateHomeworks";
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/seeHomeworksByGroupByStudent", method = RequestMethod.GET)
	protected void seeHomeworksByGroupByStudent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User userTest = (User) request.getSession().getAttribute("user");
		long start = System.currentTimeMillis();

		if (!userTest.isTeacher()) {
			if (request.getParameter("selectedGroupId") != null
					&& !(request.getParameter("selectedGroupId").trim().equals(""))) {
				ArrayList<HomeworkDetails> homeworkDetailsByGroup = new ArrayList<>();
				Student user = (Student) request.getSession().getAttribute("user");
			//	Group selectedGroup = null;
				if (!request.getParameter("selectedGroupId").equals("null")) {
					try {
						JsonArray array = new JsonArray();
						if (request.getParameter("selectedGroupId").equals("allGroups")) {
							ArrayList<Integer> checkedIds = new ArrayList<>();
							for (Group g : user.getGroups()) {
								ArrayList<Homework> homeworksOfStudentByGroup = UserDAO.getInstance().getHomeworksOfStudentByGroup(user.getId(),
										g.getId());
								for (Homework h : homeworksOfStudentByGroup) {
									JsonObject obj = new JsonObject();
									int numberOfTasks = h.getHomeworkDetails().getNumberOfTasks();
									System.out.println("Number of tasks is " + numberOfTasks);
									int pointsPerTask = 100 / numberOfTasks;
									System.out.println("Points per task is " + pointsPerTask);
									request.getSession().setAttribute("pointsPerTask", pointsPerTask);
									int systemScorePoints = 0;
									for (Task t : h.getTasks()) {
										String x = t.getSolution();
										if(t.isHasPassedSystemTest()){
											systemScorePoints += pointsPerTask;
										}
									}
									obj.addProperty("systemScore", systemScorePoints);
									obj.addProperty("heading", h.getHomeworkDetails().getHeading());
									obj.addProperty("id", h.getHomeworkDetails().getId());
									obj.addProperty("opens", h.getHomeworkDetails().getOpeningTime().toString());
									obj.addProperty("closes", h.getHomeworkDetails().getClosingTime().toString());
									int grade = h.getTeacherGrade();
									String comment = h.getTeacherComment();
									obj.addProperty("teacherScore", grade);
									obj.addProperty("teacherComment", comment);
									System.out.println("Teacher comment: " + comment);
									array.add(obj);
								}
//								homeworkDetailsByGroup.addAll(GroupDAO.getInstance().getHomeworkDetailsOfGroup(g.getId()));
//								for (HomeworkDetails hd : homeworkDetailsByGroup) {
//									if (!(checkedIds.contains((Integer) hd.getId()))) {
//										JsonObject obj = new JsonObject();
//										obj.addProperty("id", hd.getId());
//										obj.addProperty("heading", hd.getHeading());
//										obj.addProperty("opens", hd.getOpeningTime().toString());
//										obj.addProperty("closes", hd.getClosingTime().toString());
//										for (Homework h : UserDAO.getInstance().getHomeworksOfStudent(user.getId())) {
//											if (hd.getId() == h.getHomeworkDetails().getId()) {
//												int grade = h.getTeacherGrade();
//												String comment = h.getTeacherComment();
//												obj.addProperty("teacherScore", grade);
//												obj.addProperty("teacherComment", comment);
//												break;
//											}
//										}
//										checkedIds.add(hd.getId());
//										array.add(obj);
//									}
//
//								}
							}
							response.setStatus(IValidationsDAO.SUCCESS_STATUS);
							response.getWriter().write(array.toString());
						} else {
							if (ValidationsDAO.getInstance()
									.isStringValidInteger(request.getParameter("selectedGroupId"))) {
								int selectedGroupId = Integer.parseInt(request.getParameter("selectedGroupId"));
								request.getSession().setAttribute("chosenGroup", selectedGroupId);
								//selectedGroup = GroupDAO.getInstance().getGroupById(selectedGroupId);
								if (!ValidationsDAO.getInstance().doesGroupExistInDBById(selectedGroupId)) {
									response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
									return;
								}
								boolean doesUserHaveChosenGroup = false;
								for (Group g : user.getGroups()) {
									if (g.getId() == selectedGroupId) {
										doesUserHaveChosenGroup = true;
										break;
									}
								}
								if (!doesUserHaveChosenGroup) {
									response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
									return;
								}

								homeworkDetailsByGroup
										.addAll(GroupDAO.getInstance().getHomeworkDetailsOfGroup(selectedGroupId));


								//for (HomeworkDetails hd : homeworkDetailsByGroup) {
								ArrayList<Homework> homeworksOfStudentByGroup = UserDAO.getInstance().getHomeworksOfStudentByGroup(user.getId(),
										selectedGroupId);
								for (Homework h : homeworksOfStudentByGroup) {
									JsonObject obj = new JsonObject();
									int numberOfTasks = h.getHomeworkDetails().getNumberOfTasks();
									System.out.println("Number of tasks is " + numberOfTasks);
									int pointsPerTask = 100 / numberOfTasks;
									System.out.println("Points per task is " + pointsPerTask);
									request.getSession().setAttribute("pointsPerTask", pointsPerTask);
									int systemScorePoints = 0;
									for (Task t : h.getTasks()) {
										String x = t.getSolution();
										if(t.isHasPassedSystemTest()){
											systemScorePoints += pointsPerTask;
										}
									}
									obj.addProperty("systemScore", systemScorePoints);
									obj.addProperty("heading", h.getHomeworkDetails().getHeading());
									obj.addProperty("id", h.getHomeworkDetails().getId());
									obj.addProperty("opens", h.getHomeworkDetails().getOpeningTime().toString());
									obj.addProperty("closes", h.getHomeworkDetails().getClosingTime().toString());
									int grade = h.getTeacherGrade();
									String comment = h.getTeacherComment();
									obj.addProperty("teacherScore", grade);
									obj.addProperty("teacherComment", comment);
									array.add(obj);
								}
									
									
//									for (Homework h : homeworksOfStudentByGroup) {
//										if (hd.getId() == h.getHomeworkDetails().getId()) {
//											int grade = h.getTeacherGrade();
//											String comment = h.getTeacherComment();
//											obj.addProperty("teacherScore", grade);
//											obj.addProperty("teacherComment", comment);
//											break;
//										}
//									}
									
								//}	
//								for (HomeworkDetails hd : homeworkDetailsByGroup) {
//								JsonObject obj = new JsonObject();
//								obj.addProperty("heading", hd.getHeading());
//								obj.addProperty("id", hd.getId());
//								obj.addProperty("opens", hd.getOpeningTime().toString());
//								obj.addProperty("closes", hd.getClosingTime().toString());
//								//not needed
//
//								ArrayList<Homework> homeworksOfStudentByGroup = UserDAO.getInstance().getHomeworksOfStudentByGroup(user.getId(),
//										selectedGroupId);
//
//								for (Homework h : homeworksOfStudentByGroup) {
//									if (hd.getId() == h.getHomeworkDetails().getId()) {
//										int grade = h.getTeacherGrade();
//										String comment = h.getTeacherComment();
//										obj.addProperty("teacherScore", grade);
//										obj.addProperty("teacherComment", comment);
//										break;
//									}
//								}
//								array.add(obj);
//							}		
			
								
								

								response.setStatus(IValidationsDAO.SUCCESS_STATUS);
								response.getWriter().write(array.toString());
							} else {
								response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
							}
						}
					} catch (GroupException | UserException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
					} catch (ValidationException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
				}
			} else {
				response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
		System.out.println("!!!Time passed: " + (System.currentTimeMillis()-start));
	}

	@RequestMapping(value = "/updateHomework", method = RequestMethod.GET)
	protected String updateHomeworkPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getParameter("chosenHomework") != null
					|| request.getSession().getAttribute("currHomework") != null) {
				int hwId = 0;
				HomeworkDetails hd;
				try {
					if (request.getParameter("chosenHomework") != null) {
						int length = request.getParameter("chosenHomework").length();
						String number = request.getParameter("chosenHomework");
						if (length > IValidationsDAO.MIN_SIZE_OF_INTEGER
								&& length < IValidationsDAO.MAX_SIZE_OF_INTEGER) {
							for (int i = 0; i < length; i++) {
								if ((int) number.charAt(i) < IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
										|| (int) number.charAt(i) > IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE) {
									return "pageNotFound";
								}
							}
						} else {
							return "pageNotFound";
						}
						hwId = Integer.parseInt(request.getParameter("chosenHomework"));
						hd = GroupDAO.getInstance().getHomeworkDetailsById(hwId);
						if (hd != null) {
							request.getSession().setAttribute("currHomework", hd);
						} else {
							return "pageNotFound";
						}
					} else {
						HomeworkDetails hd1 = (HomeworkDetails) request.getSession().getAttribute("currHomework");
						if (hd1 != null) {
							hwId = hd1.getId();
						} else {
							return "pageNotFound";
						}
					}
				} catch (GroupException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				}
				return "updateHomework";
			}else{
				return "pageNotFound";
			}
		}
		return "forbiddenPage";
	}

	@RequestMapping(value = "/updateHomework", method = RequestMethod.POST)
	protected String updateHomework(HttpServletRequest request,
			@RequestParam(value = "file") MultipartFile fileMultiPart,@RequestParam(value = "testsFile") MultipartFile testsFileUploaded, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user.isTeacher()) {
			if (request.getSession().getAttribute("currHomework") != null) {
				File newFilePdf = null;
				boolean isFileNameChangedPdf = false;
				boolean isFileNameTestsChanged = false;
				File newFileTests = null;
				File oldFileTests = null;
				Unzipper unzip = null;
				File unzippedFilesDir = null;
				File zipFile = null;
				Path oldFileTestsPath = null;
				Path newFileTestsPath = null;
				
				try {
					// empty fields (except file)
					boolean isFileEmptyPdf = fileMultiPart.getOriginalFilename().isEmpty();
					boolean emptyFields = false;
					//TODO proverka i za testsFileUploaded
					if (!isFileEmptyPdf) {
						emptyFields = isThereEmptyFieldUpdateHomework(request.getParameter("name").trim(),
								request.getParameter("opens").trim().replace("/", "-"),
								request.getParameter("closes").trim().replace("/", "-"), fileMultiPart,
								request.getParameter("numberOfTasks").trim(), request.getParameterValues("groups"));
					} else {
						emptyFields = isThereEmptyFieldUpdateHomeworkNoFileUploaded(request.getParameter("name").trim(),
								request.getParameter("opens").trim().replace("/", "-"),
								request.getParameter("closes").trim().replace("/", "-"),
								request.getParameter("numberOfTasks").trim(), request.getParameterValues("groups"));
					}

					if (emptyFields) {
						request.getSession().setAttribute("emptyFields", true);
					} else {
						int homeworkDetailsId = ((HomeworkDetails) request.getSession().getAttribute("currHomework"))
								.getId();
						String heading = request.getParameter("name").trim();
						String fileNamePdf = " ";
						String[] selectedGroups = request.getParameterValues("groups");
						MultipartFile filePartPdf = fileMultiPart;
						String opens = request.getParameter("opens").trim().replace("/", "-");
						String closes = request.getParameter("closes").trim().replace("/", "-");
						String numberOfTasksString = request.getParameter("numberOfTasks").trim();
						HomeworkDetails currHd = null;
						currHd = GroupDAO.getInstance().getHomeworkDetailsById(homeworkDetailsId);
						MultipartFile testsFilePart = testsFileUploaded;
						// heading valid
						//File file = null;
						File fileTests = null;
						// valid heading
						int numberOfTasks = 0;
						boolean isHeadingValid = false;
						boolean isHeadingUnique = false;
						if (areHomeworkUpdateCharactersValid(heading) && isHomeworkUpdateLengthValid(heading)) {
							isHeadingValid = true;
							if (isHomeworkUpdateHeadingUnique(heading, currHd)) {
								isHeadingUnique = true;
							}
						}
						HomeworkDetails homeworkDetailsCurrent = GroupDAO.getInstance().getHomeworkDetailsById(homeworkDetailsId);

						request.getSession().setAttribute("validHeading", isHeadingValid);
						request.getSession().setAttribute("uniqueHeading", isHeadingUnique); // unique
						// heading
						// opening time
						boolean isOpeningTimeValid = false;
						if (isHomeworkUpdateOpeningTimeValid(opens, currHd)) {
							isOpeningTimeValid = true;
						}
						request.getSession().setAttribute("validOpeningTime", isOpeningTimeValid);
						// closing time
						boolean isClosingTimeValid = false;
						if (isHomeworkUpdateClosingTimeValid(opens, closes, currHd)) {
							isClosingTimeValid = true;
						}
						request.getSession().setAttribute("validClosingTime", isClosingTimeValid);
						// numTasks
						boolean areTasksValid = false;
						if (isHomeworkNumberOfTasksANumber(numberOfTasksString)) {
							numberOfTasks = Integer.parseInt(request.getParameter("numberOfTasks"));
							if (isHomeworkUpdateNumberOfTasksValid(numberOfTasks)) {
								areTasksValid = true;
							}
						}
						request.getSession().setAttribute("validTasks", areTasksValid);
						// do all groups exist
						boolean areGroupsValid = false;
						if (doAllGroupsExistHomeworkUpdate(selectedGroups)) {
							areGroupsValid = true;
						}
						request.getSession().setAttribute("validGroups", areGroupsValid);
						boolean isFileValid = false;
						if (filePartPdf.getSize() == 0) {
							isFileValid = true;
						} else {
							if (isFileUpdateHomeworkValid(filePartPdf)) {
								isFileValid = true;
							}
						}
						
						//testsFile
						boolean isTestsFileValid = false;
						if(testsFilePart.getSize() == 0){
							isTestsFileValid = true;
						}else{
						if (isHomeworkTestsFileSizeValid(testsFilePart) && isHomeworkTestsFileContentTypeValid(testsFilePart)) {
							isTestsFileValid = true;
						}
						}
						request.setAttribute("validTestsFile", isTestsFileValid);
						request.getSession().setAttribute("validFile", isFileValid);
						System.out.println("Is closing time valid " + isClosingTimeValid);
						if (isHeadingValid == true && isHeadingUnique == true && isOpeningTimeValid == true
								&& isClosingTimeValid == true && areTasksValid == true && areGroupsValid == true
								&& isFileValid == true && isTestsFileValid == true) {
							// fileName = "hwName" + heading + ".pdf";
							
							fileNamePdf = filePartPdf.getOriginalFilename();
							newFilePdf = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_FILES_PDF + File.separator + fileNamePdf);
							File oldFilePdf = null;
							// if (!newFile.exists()) {
							//
							if (!fileNamePdf.equals(currHd.getTasksFile()) && !(fileNamePdf.trim().equals(""))) {
								String oldNameOfFile = currHd.getTasksFile();
								oldFilePdf = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_FILES_PDF + File.separator + oldNameOfFile);// remove
								isFileNameChangedPdf = true;
							
								if (filePartPdf.getSize() != 0) {
									String savePath = IValidationsDAO.SAVE_DIR_HOMEWORK_FILES_PDF;
									File fileSaveDir = new File(savePath);
									if (!fileSaveDir.exists()) {
										fileSaveDir.mkdir();
									}
									while (newFilePdf.exists()) {
										Random randomGenerator = new Random();
										int randomLength = randomGenerator.nextInt((7 - 1) + 1) + 1;
										String randomString = this.getRandomCharacters(randomLength);
										fileNamePdf = filePartPdf.getOriginalFilename().substring(0,
												filePartPdf.getOriginalFilename().length() - 4) + randomString + ".pdf";
										newFilePdf = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_FILES_PDF + File.separator + fileNamePdf);
									}
									newFilePdf.createNewFile();
								} else {
									fileNamePdf = heading;
								}
							}
							//we save the uploaded file (.pdf)
							if (filePartPdf.getSize() != 0) {
								OutputStream out = null;
								InputStream filecontent = null;
								out = new FileOutputStream(newFilePdf, true);
								filecontent = filePartPdf.getInputStream();
								int read = 0;
								final byte[] bytes = new byte[1024];

								while ((read = filecontent.read(bytes)) != -1) {
									out.write(bytes, 0, read);
								}
								
								out.flush();
								out.close();
							}
							isFileNameTestsChanged = false;
							if(!homeworkDetailsCurrent.getHeading().equals(heading)){
								isFileNameTestsChanged = true;
							}
							if(testsFilePart.getSize() != 0){
								//upload file with hw tests
								File fileSaveDirZip = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES);
								if (!fileSaveDirZip.exists()) {
									fileSaveDirZip.mkdir();
								}
								String fileNameTests = " ";
								System.out.println("original name tests file " +  testsFileUploaded.getOriginalFilename());
								fileNameTests = testsFileUploaded.getOriginalFilename().substring(0,
										testsFileUploaded.getOriginalFilename().length() - 4) + ".zip";
								System.out.println("File name is " + testsFileUploaded.getOriginalFilename().substring(0,
										testsFileUploaded.getOriginalFilename().length() - 4));
								//OutputStream outTests = null;
								//InputStream filecontentTests = null;
								fileTests = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator + fileNameTests);
								while (fileTests.exists()) {
									Random randomGenerator = new Random();
									int randomLength = randomGenerator.nextInt((7 - 1) + 1) + 1;
									String randomString = this.getRandomCharacters(randomLength);
									fileNameTests = testsFileUploaded.getOriginalFilename().substring(0,
											testsFileUploaded.getOriginalFilename().length() - 4) + randomString + ".zip";
									fileTests = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator + fileNameTests);
									//see if unzips, check file is .txt
									// da e v papka s imeto na doma6noto
									
								}
								
								fileTests.createNewFile();
								OutputStream out = null;
								InputStream filecontent = null;
								out = new FileOutputStream(fileTests, true);
								filecontent = testsFilePart.getInputStream();
								int read = 0;
								final byte[] bytes = new byte[1024];
								while ((read = filecontent.read(bytes)) != -1) {
									out.write(bytes, 0, read);
								}
								zipFile = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES  + File.separator + heading + ".zip");
								System.out.println("HERE WE GO: ");
								System.out.println(zipFile.getAbsolutePath());
								if(!zipFile.exists()){
									zipFile.mkdirs();
								}
								out.close();
								filecontent.close();
								unzippedFilesDir = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES  + File.separator + homeworkDetailsCurrent.getHeading());
								if(!unzippedFilesDir.exists()){
									unzippedFilesDir.mkdir();
								}
								//homeworkDetailsCurrent.setTestTasksFile(unzippedFilesDir.getName());
								unzip = new Unzipper(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator + fileNameTests, unzippedFilesDir.getAbsolutePath());
								
								
								if(testsFilePart.getSize() != 0){
									newFileTests = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES  + File.separator + heading);
									if(!newFileTests.exists()){
										newFileTests.createNewFile();
									}
									 oldFileTests = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES  + File.separator + homeworkDetailsCurrent.getTestTasksFile());
									 
									 oldFileTestsPath = FileSystems.getDefault().getPath(oldFileTests.getAbsolutePath());
									  newFileTestsPath = FileSystems.getDefault().getPath(newFileTests.getAbsolutePath());
									  System.out.println("are extensions valid " + unzip.areExtensionsValid(new ZipInputStream((testsFileUploaded.getInputStream()))));
									if(!unzip.areExtensionsValid(new ZipInputStream((testsFileUploaded.getInputStream())))){
										throw new InvalidFilesExtensionInZIP();
									}
								}
							} else {// ako e prazno, no imeto na homework e
									// promeneno
								if (isFileNameTestsChanged) {
									newFileTests = new File(
											IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator + heading);
									if (!newFileTests.exists()) {
										newFileTests.createNewFile();
									}
									oldFileTests = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES
											+ File.separator + homeworkDetailsCurrent.getTestTasksFile());

									oldFileTestsPath = FileSystems.getDefault().getPath(oldFileTests.getAbsolutePath());
									newFileTestsPath = FileSystems.getDefault().getPath(newFileTests.getAbsolutePath());
									System.out.println("WILL MOVE " + oldFileTestsPath.toString() + " to "
											+ newFileTestsPath.toString());
								}

							}

							
							System.out.println("Our heading  is " + heading);
							File previousZipFile = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES + File.separator + homeworkDetailsCurrent.getTestTasksFile()+".zip");
							System.out.println("Previous path: " + previousZipFile.getAbsolutePath());

							zipFile = new File(IValidationsDAO.SAVE_DIR_HOMEWORK_TESTS_FILES  + File.separator + heading + ".zip");
							if(!zipFile.exists()){
								zipFile.createNewFile();
							}
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
							LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
							LocalDateTime closingTime = LocalDateTime.parse(closes, formatter);
							ArrayList<Integer> groupsForHw = new ArrayList<>();
							HomeworkDetails homeworkDetails = null;
						
							if (isFileNameChangedPdf) {
								homeworkDetails = new HomeworkDetails(homeworkDetailsId, heading, openingTime,
										closingTime, numberOfTasks, fileNamePdf, heading);
								
							} else {
								if (fileNamePdf.equals("")) {
									fileNamePdf = currHd.getTasksFile();
								}
								homeworkDetails = new HomeworkDetails(homeworkDetailsId, heading, openingTime,
										closingTime, numberOfTasks, fileNamePdf, heading);
							}
							if (request.getParameterValues("groups") != null) {
								for (int i = 0; i < selectedGroups.length; i++) {
									int id = Integer.parseInt(selectedGroups[i]);
									groupsForHw.add(id);
								}
							}
							GroupDAO.getInstance().updateHomeworkDetails(homeworkDetails, groupsForHw);

							
							Path previousZipFilePath = FileSystems.getDefault().getPath(previousZipFile.getAbsolutePath());
							Path zipFilePath = FileSystems.getDefault().getPath(zipFile.getAbsolutePath());
							Files.move(previousZipFilePath, zipFilePath, StandardCopyOption.REPLACE_EXISTING);
							request.getSession().setAttribute("currHomework", homeworkDetails);
							request.getServletContext().removeAttribute("allGroups");
							ArrayList<Group> allGroups = GroupDAO.getInstance().getAllGroupsWithoutStudents();
							request.getServletContext().setAttribute("allGroups", allGroups);
							// ne ni trqbvat u4enicite
							user.setGroups(UserDAO.getInstance().getGroupsOfUserWithoutStudents(user.getId()));
							request.getSession().setAttribute("invalidFields", false);
							if (testsFilePart.getSize() != 0) {
								if (testsFilePart.getSize() != 0 && oldFileTests != null && newFileTests != null) {
									File currentFileToReplaceTests = new File(unzippedFilesDir.getAbsolutePath());
									if (currentFileToReplaceTests.exists()) {
										String[] entries = currentFileToReplaceTests.list();
										System.out.println(entries.length);
										for (String s : entries) {
											File currentFile = new File(currentFileToReplaceTests.getPath(), s);
											System.out.println("WE DELETE FILE : " + currentFile.getAbsolutePath());
											currentFile.delete();
										}
									}
								}
							}
							// System.out.println("WILL MOVE " + oldFileTestsPath.toString() + " to " + newFileTestsPath.toString());
								//we unzip the file and save it 
							if(testsFilePart.getSize() != 0){
									unzip.unzip(unzip.zipFilePath, unzip.destDirectory);
									testsFilePart.transferTo(zipFile);
							}
							// nulirame score- ovete na u4enicite
							System.out.println("We move " + oldFileTestsPath + " into " + newFileTestsPath);
							if (oldFileTests != null && newFileTests != null) {
								System.out.println("in");
								//reverse new with old
								Files.move(oldFileTestsPath, newFileTestsPath, StandardCopyOption.REPLACE_EXISTING);
							}
							if (testsFilePart.getSize() != 0) {
								for (Integer studentId : GroupDAO.getInstance()
										.getStudentsWithSearchedHomework(homeworkDetailsId)) {
									Homework homeworkOfStudent = UserDAO.getInstance().getHomeworkOfStudent(studentId, homeworkDetails.getId());
									ArrayList<Task> tasksOfHomeworkOfStudent = homeworkOfStudent.getTasks();
									tasksOfHomeworkOfStudent.sort((Task o1, Task o2)->o1.getTaskNumber()-o2.getTaskNumber());
									for (int taskNum = 0; taskNum < homeworkDetails.getNumberOfTasks(); taskNum++) {
										System.out.println("$$$ " + tasksOfHomeworkOfStudent.get(taskNum).getSolution());
										if(tasksOfHomeworkOfStudent.get(taskNum).getSolution() != null){
										boolean hasPassedNewSystemTest = GroupDAO.getInstance().doesPassSystemTest(tasksOfHomeworkOfStudent.get(taskNum).getSolution(), homeworkOfStudent, taskNum);
										UserDAO.getInstance().setPassedSystemTest(studentId, homeworkDetails.getId(),
												taskNum, hasPassedNewSystemTest);
										}else{
											UserDAO.getInstance().setPassedSystemTest(studentId, homeworkDetails.getId(),
													taskNum, false);
										}
									}
								}
							}
							if (oldFilePdf != null) {
								oldFilePdf.delete();
							}
						}
					}
				} catch (GroupException | UserException e) {
					if (isFileNameChangedPdf) {
						System.out.println("We remove " + newFilePdf.getAbsolutePath());
						if (newFilePdf.exists()) {
							newFilePdf.delete();
						}
					}
//					if (isFileNameTestsChanged) {
//						if (newFileTests.exists()) {
//							newFileTests.delete();
//						}
//					}
					
					if (isFileNameTestsChanged) {
						if (newFileTests.exists()) {
							System.out.println("We remove " + newFileTests.getAbsolutePath());
							String[] entries = newFileTests.list();
							if(entries != null){
							for (String s : entries) {
								File currentFile = new File(newFileTests.getPath(), s);
								currentFile.delete();
							}
							}
							newFileTests.delete();
						}
						System.out.println("We remove " + zipFile.getAbsolutePath());
						if (zipFile.exists()) {
							zipFile.delete();
						}
					}
					System.out.println(e.getMessage());
					e.printStackTrace();
					return "exception";
				} catch (ValidationException e) {
					if (isFileNameChangedPdf) {
						if (newFilePdf.exists()) {
							newFilePdf.delete();
						}
					}
					
					if (isFileNameTestsChanged) {
						if (newFileTests.exists()) {
							System.out.println("We remove " + newFileTests.getAbsolutePath());
							String[] entries = newFileTests.list();
							if(entries != null){
							for (String s : entries) {
								File currentFile = new File(newFileTests.getPath(), s);
								currentFile.delete();
							}
							}
							newFileTests.delete();
						}
						System.out.println("We remove " + zipFile.getAbsolutePath());
						if (zipFile.exists()) {
							zipFile.delete();
						}
					}
					e.printStackTrace();
					request.getSession().setAttribute("invalidFields", true);
				} catch (NotUniqueHomeworkHeadingException e) {
					if (isFileNameChangedPdf) {
						if (newFilePdf.exists()) {
							newFilePdf.delete();
						}
					}
					if (isFileNameTestsChanged) {
						if (newFileTests.exists()) {
							System.out.println("We remove " + newFileTests.getAbsolutePath());
							String[] entries = newFileTests.list();
							if(entries != null){
							for (String s : entries) {
								File currentFile = new File(newFileTests.getPath(), s);
								currentFile.delete();
							}
							}
							newFileTests.delete();
						}
						System.out.println("We remove " + zipFile.getAbsolutePath());
						if (zipFile.exists()) {
							zipFile.delete();
						}
					}
					request.getSession().setAttribute("invalidFields", true);
					e.printStackTrace();
				} catch (InvalidFilesExtensionInZIP e) {
					if (isFileNameChangedPdf) {
						System.out.println("We delete file pdf : " + newFilePdf.getAbsolutePath());

						if (newFilePdf.exists()) {
							newFilePdf.delete();
						}
					}
					if (isFileNameTestsChanged) {
						if (newFileTests.exists()) {
							System.out.println("We remove " + newFileTests.getAbsolutePath());
							String[] entries = newFileTests.list();
							if(entries != null){
							for (String s : entries) {
								File currentFile = new File(newFileTests.getPath(), s);
								currentFile.delete();
							}
							}
							newFileTests.delete();
						}
						System.out.println("We remove " + zipFile.getAbsolutePath());
						if (zipFile.exists()) {
							zipFile.delete();
						}
					}
					request.getSession().setAttribute("invalidFields", true);
					e.printStackTrace();
				} catch (InterruptedException e) {
					if (isFileNameChangedPdf) {
						System.out.println("We delete file pdf : " + newFilePdf.getAbsolutePath());

						if (newFilePdf.exists()) {
							newFilePdf.delete();
						}
					}
					if (isFileNameTestsChanged) {
						if (newFileTests.exists()) {
							System.out.println("We remove " + newFileTests.getAbsolutePath());
							String[] entries = newFileTests.list();
							if(entries != null){
							for (String s : entries) {
								File currentFile = new File(newFileTests.getPath(), s);
								currentFile.delete();
							}
							}
							newFileTests.delete();
						}
						System.out.println("We remove " + zipFile.getAbsolutePath());
						if (zipFile.exists()) {
							zipFile.delete();
						}
					}
					response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
				}
				return "redirect:./updateHomework";
			} else {
				return "pageNotFound";
			}
		}
		return "forbiddenPage";
	}

	private boolean isFileUpdateHomeworkValid(MultipartFile filePart) {
		if (isHomeworkUpdateContentTypeValid(filePart) && isHomeworkUpdateSizeValid(filePart)) {
			return true;
		}
		return false;
	}

	private boolean isThereEmptyFieldUpdateHomework(String heading, String opens, String closes, MultipartFile filePart,
			String numberOfTasksString, String[] selectedGroups) {
		boolean isThereEmptyField = true;
		if (heading != null && !(heading.trim().equals("")) && opens != null && !(opens.trim().equals(""))
				&& closes != null && !(closes.trim().equals("")) && numberOfTasksString != null
				&& !(numberOfTasksString.trim().equals("")) && selectedGroups != null && selectedGroups.length > 0) {
			isThereEmptyField = false;
			//return false;
		}
		if (!(filePart != null && filePart.getSize() != 0)) {
			isThereEmptyField = true;
			//return false;
		}
		return isThereEmptyField;

	}

	private boolean isThereEmptyFieldUpdateHomeworkNoFileUploaded(String heading, String opens, String closes,
			String numberOfTasksString, String[] selectedGroups) {
		if (heading != null && !(heading.trim().equals("")) && opens != null && !(opens.trim().equals(""))
				&& closes != null && !(closes.trim().equals("")) && numberOfTasksString != null
				&& !(numberOfTasksString.trim().equals("")) && selectedGroups != null && selectedGroups.length > 0) {
			return false;
		}
		return true;
	}

	private boolean isHomeworkUpdateLengthValid(String heading) {
		if (heading.length() >= IValidationsDAO.HOMEWORK_HEADING_MIN_LENGTH
				&& heading.length() <= IValidationsDAO.HOMEWORK_HEADING_MAX_LENGTH) {
			return true;
		}
		return false;
	}

	private boolean areHomeworkUpdateCharactersValid(String heading) {
		for (int i = 0; i < heading.length(); i++) {
			if (!(((int) heading.charAt(i) >= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_FROM
					&& (int) heading.charAt(i) <= IValidationsDAO.HOMEWORK_HEADING_VALID_CHARS_ASCII_TABLE_TO))  || (int) heading.charAt(i) == IValidationsDAO.ASCII_TABLE_QUOTES) {
				return false;
			}
		}
		return true;
	}

	private boolean isHomeworkUpdateHeadingUnique(String heading, HomeworkDetails currHd) throws GroupException {
		if (currHd.getHeading().equals(heading) || ValidationsDAO.getInstance().isHomeworkHeadingUnique(heading)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isHomeworkUpdateOpeningTimeValid(String opens, HomeworkDetails currHd) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

			LocalDateTime openingTime = LocalDateTime.parse(opens, formatter);
			LocalDate openingDate = openingTime.toLocalDate();
			if (openingTime.equals(currHd.getOpeningTime())) {
				return true;
			} else {
				if (openingDate.isAfter(LocalDate.now().minusDays(IValidationsDAO.MINUS_ONE_DAY))
						&& openingDate.isBefore(LocalDate.now()
								.plusMonths(
										IValidationsDAO.MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK)
								.minusDays(IValidationsDAO.MINUS_ONE_DAY))) {
					return true;
				} else {
					return false;
				}
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isHomeworkUpdateClosingTimeValid(String opens, String closes, HomeworkDetails currHd) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime openingDateTime = LocalDateTime.parse(opens, formatter);
			LocalDateTime closingDateTime = LocalDateTime.parse(closes, formatter);
			long diffInMonths = ChronoUnit.MONTHS.between(openingDateTime, closingDateTime);
			if (closingDateTime.equals(currHd.getClosingTime())) {
				return true;
			} else {
				if (closingDateTime.isAfter(LocalDateTime.now()) && closingDateTime.isAfter(openingDateTime)
						&& diffInMonths < IValidationsDAO.MAX_DIFFERENCE_IN_MONTHS_FROM_OPENING_TO_CLOSING_OF_HOMEWORK) {
					return true;
				} else {
					return false;
				}
			}
		} catch (NumberFormatException e) {
			return false;

		}
	}

	private boolean isHomeworkUpdateNumberOfTasksValid(int numberOfTasks) {
		if (numberOfTasks >= IValidationsDAO.MIN_NUMBER_OF_TASKS_FOR_HOMEWORK
				&& numberOfTasks <= IValidationsDAO.MAX_NUMBER_OF_TASKS_FOR_HOMEWORK) {
			return true;
		}
		return false;
	}

	private boolean isHomeworkUpdateContentTypeValid(MultipartFile filePart) {
		String contentType = filePart.getOriginalFilename().substring(filePart.getOriginalFilename().indexOf("."));
		if (!(contentType.equals(".pdf"))) {
			return false;
		}
		return true;
	}

	private boolean isHomeworkUpdateSizeValid(MultipartFile filePart) {
		long sizeInMb = filePart.getSize() / (1024 * 1024);
		if (sizeInMb > IValidationsDAO.MAX_SIZE_IN_MB_FOR_HOMEWORK_ASSIGNMENT) {
			return false;
		}
		return true;
	}

	private boolean doAllGroupsExistHomeworkUpdate(String[] selectedGroups) throws GroupException, UserException {
		for (String groupId : selectedGroups) {
			if (ValidationsDAO.getInstance().isStringValidInteger(groupId)) {
				if (!ValidationsDAO.getInstance().doesGroupExistInDBById(Integer.parseInt(groupId))) {
					return false;
				}
			}else{
				return false;
			}
//			try {
//				Group currGroup = GroupDAO.getInstance().getGroupById(Integer.parseInt(groupId));
//				String groupName = currGroup.getName();
//				if (ValidationsDAO.getInstance().isGroupNameUnique(groupName)) {
//					return false;
//				}
//			} catch (NumberFormatException e) {
//				return false;
//			}
		}
		return true;
	}

	@RequestMapping(value = "/updateTeacherGradeAndComment", method = RequestMethod.POST)
	protected String updateTeacherGradeAndComment(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		Homework homework = null;
		if (user.isTeacher()) {
			if (request.getSession().getAttribute("currHomework") != null && request.getParameter("comment") != null
					&& request.getParameter("grade") != null
					&& request.getSession().getAttribute("studentId") != null) {
				homework = (Homework) request.getSession().getAttribute("currHomework");
				String teacherComment = request.getParameter("comment");
				String teacherGradeString = request.getParameter("grade").trim();
				int studentId = (int) request.getSession().getAttribute("studentId");
				int teacherGrade = 0;
				
				// grade not empty
				if (isGradeEmpty(teacherGradeString)) {
					request.getSession().setAttribute("emptyFields", true); // success
				} else if (isGradeTooLong(teacherGradeString)) {
					request.getSession().setAttribute("GradeTooLong", true); // success
				} else {
					boolean isGradeValueValid = false;
					if (!doesGradeHaveInvalidSymbols(teacherGradeString.trim())) {
						teacherGrade = Integer.parseInt(teacherGradeString.trim());
						// grade >=0 <=100
						if (isGradeValueValid(teacherGrade)) {
							isGradeValueValid = true;
						}
					}
					request.getSession().setAttribute("validGrade", isGradeValueValid); // success
					// comment max length = 250
					boolean isCommentLengthValid = false;
					if (isCommentLengthValid(teacherComment)) {
						isCommentLengthValid = true;
					}
					request.getSession().setAttribute("validComment", isCommentLengthValid); // success
					if (isGradeValueValid == true && isCommentLengthValid == true) {
						try {
							UserDAO.getInstance().setTeacherComment(homework.getHomeworkDetails().getId(), studentId,
									teacherComment);
							UserDAO.getInstance().setTeacherGrade(homework.getHomeworkDetails().getId(), studentId,
									teacherGrade);
							homework.setTeacherComment(teacherComment);
							homework.setTeacherGrade(teacherGrade);
							request.getSession().setAttribute("invalidFields", false);
						} catch (UserException e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
							return "exception";
						} catch (ValidationException e) {
							request.getSession().setAttribute("invalidFields", true);
						}
					}
				}
				return "redirect:./seeChosenHomeworkPageOfStudentByTeacher";
			}
			return "pageNotFound";
		}
		return "forbiddenPage";
	}

	private boolean isGradeEmpty(String grade) {
		if (grade != null && !(grade.equals(""))) {
			return false;
		}
		return true;
	}

	private boolean isGradeTooLong(String grade) {
		if (grade.length() > IValidationsDAO.MAX_LENGTH_OF_GRADE) {
			return true;
		}
		return false;
	}

	private boolean isGradeValueValid(int grade) {
		if (grade >= IValidationsDAO.MIN_VALUE_OF_GRADE && grade <= IValidationsDAO.MAX_VALUE_OF_GRADE) {
			return true;
		}
		return false;
	}

	private boolean isCommentLengthValid(String comment) {
		if (comment.length() <= IValidationsDAO.MAX_LENGTH_OF_COMMENT) {
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/uploadSolutionToTaskJava", method = RequestMethod.GET)
	protected String uploadSolutionPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (!user.isTeacher()) {
			return "currentHomeworkPageOfStudentByStudent";
		}
		return "forbiddenPage";
	}
	//ckeck uploadTimeIsCorrect
	@RequestMapping(value = "/uploadSolutionToTaskJava", method = RequestMethod.POST)
	protected void uploadSolution(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("datafile") MultipartFile uploadfile) throws ServletException, IOException {

		User user = (User) request.getSession().getAttribute("user");
		if (!user.isTeacher()) {
			if (request.getSession().getAttribute("currHomework") != null && request.getParameter("taskNum") != null
					&& ValidationsDAO.getInstance().isStringValidInteger(request.getParameter("taskNum"))) {
				
				String savePath = IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA;
				int taskNum = Integer.parseInt(request.getParameter("taskNum")) - 1;
				Homework homework = (Homework) request.getSession().getAttribute("currHomework");
				HomeworkDetails homeworkDetails = homework.getHomeworkDetails();
				if(homeworkDetails.getOpeningTime().isBefore(LocalDateTime.now())&&homeworkDetails.getClosingTime().isAfter(LocalDateTime.now())){
				// creates the save directory if it does not exists
				
				String fileName = " ";
				MultipartFile file = uploadfile;
				if (!isFileEmptyUploadSolution(file)) {
					if (!isSizeValidUploadSolution(file)) {
						response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
						return;
					} else {
						if (!isContentTypeValidUploadSolution(file)) {
							response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
							return;
						} else {
								if (doesTaskNumExist(taskNum, homeworkDetails)) {
									String fileDirectory = "hwId" + homeworkDetails.getId() + "userId" + user.getId()
											+ "taskNum" + taskNum;
									File fileSaveDir = new File(savePath + File.separator + fileDirectory);
									if (!fileSaveDir.exists()) {
										fileSaveDir.mkdir();
									}
									fileName = file.getOriginalFilename();
									File newFile = new File(
											savePath + File.separator + fileDirectory + File.separator + fileName);
									if (!newFile.exists()) {
										newFile.createNewFile();
									}
									String solutionPath = homework.getTasks().get(taskNum).getSolution();
									System.out.println("))))))))))");
									if (solutionPath != null && (!solutionPath.trim().equals(""))) {
										//System.out.println(solutionPath.substring(solutionPath.lastIndexOf("/") + 1));
										String searchedFileName = solutionPath
												.substring(solutionPath.lastIndexOf("/") + 1);
										//System.out.println("solution oath is: " + solutionPath);
										//System.out.println("searched file name is: " + searchedFileName);
										String searchedFileNameClass = searchedFileName.substring(0,
												searchedFileName.length() - 5);
										File currentFileToRemove = new File(savePath + File.separator + fileDirectory
												+ File.separator + searchedFileName);
										File currentFileClassToRemove = new File(savePath + File.separator
												+ fileDirectory + File.separator + searchedFileNameClass + ".class");
										
										

										//System.out.println("Want to remove: " + currentFileToRemove.getAbsolutePath());
										// if
										// (!searchedFileName.equals(file.getOriginalFilename()))
										// {
										if (currentFileToRemove.exists()) {
											currentFileToRemove.delete();
										}

										// }
										if (currentFileClassToRemove.exists()) {
											currentFileClassToRemove.delete();
										}
										file.transferTo(newFile);
									}
								//newFile = new File(savePath +File.separator+fileDirectory+ File.separator + fileName);

								//boolean doesFolderContainFile = false;
//								for(File f: new File(IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + "hwId"+ homeworkDetails.getId() + "userId" + user.getId() + "taskNum" + taskNum).listFiles()){
//									if(f.getName().equals(searchedFileName)){
//										doesFolderContainFile = true;
//										
//										
//										//fileName = f.getAbsolutePath();
//										break;
//									}
//								}
//								if (doesFolderContainFile == true) {
//									strLine = new String(Files.readAllBytes(Paths.get(fileName)), "UTF8");
//								} else {
//									strLine = "Solution is not uploaded yet.";
//								}
//								if(!doesFolderContainFile){
//									newFile = new File(savePath +File.separator+fileDirectory+ File.separator + fileName);
//									System.out.println(newFile);
//									if(!newFile.exists()){
//										newFile.createNewFile();
//									}
//									
//								}else{
//
//									//FOUND
////									
////									String solutionPath = homework.getTasks().get(taskNum).getSolution();
////									System.out.println("))))))))))");
////								    System.out.println(solutionPath.substring(solutionPath.lastIndexOf("/") + 1));
////									String searchedFileName = solutionPath.substring(solutionPath.lastIndexOf("/") + 1);
////									boolean doesFolderContainFile = false;
////									for(File file: new File(IValidationsDAO.SAVE_DIR_HOMEWORK_SOLUTIONS_JAVA + File.separator + "hwId"+ homeworkDetails.getId() + "userId" + idOfStudentForHw + "taskNum" + taskNum).listFiles()){
////										if(file.getName().equals(searchedFileName)){
////											doesFolderContainFile = true;
////											fileName = file.getAbsolutePath();
////											break;
////										}
////									}
////									if (doesFolderContainFile == true) {
////										strLine = new String(Files.readAllBytes(Paths.get(fileName)), "UTF8");
////									} else {
////										strLine = "Solution is not uploaded yet.";
////									}
//									
//									
//										
//									}
								//file.transferTo(new File(fileName));
//								new File(fileName).renameTo(new File(savePath +File.separator+fileDirectory+ File.separator + fileName));
//								//TODO DA GO IZTRIQ MINALIQ
						//		System.out.println("Path: " + );
								try {
									DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
									LocalDateTime currDateTime = LocalDateTime.now();
									  String currDateTimeString = currDateTime.format(formatter);
									  currDateTime = LocalDateTime.parse(currDateTimeString, formatter);
										fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
										UserDAO.getInstance().setSolutionOfTask(homeworkDetails.getId(), user.getId(), taskNum,
											fileDirectory+ File.separator + fileName, currDateTime);
									homework.getTasks().get(taskNum).setSolution(fileName);
									homework.getTasks().get(taskNum).setUploadedOn(currDateTime);
									//TODO has passed system score + da dobavq dali e minalo test pri vikane na doma6noto
									System.out.println("BEFORE SYSTEM TEST");

									boolean doesPastSystemTest = GroupDAO.getInstance().doesPassSystemTest(fileDirectory+ File.separator + fileName,homework, taskNum);
						//			boolean doesPastSystemTest = true;
									//if(doesPastSystemTest){
										UserDAO.getInstance().setPassedSystemTest(user.getId(), homeworkDetails.getId(), taskNum, doesPastSystemTest);
									//}
									System.out.println("AFTER SYSTEM TEST");

									homework.getTasks().get(taskNum).setHasPassedSystemTest(doesPastSystemTest);
									int numberOfTasks = homework.getHomeworkDetails().getNumberOfTasks();
									System.out.println("Number of tasks is " + numberOfTasks);
									int pointsPerTask = 100 / numberOfTasks;
									System.out.println("Points per task is " + pointsPerTask);
									request.getSession().setAttribute("pointsPerTask", pointsPerTask);
									
									request.getSession().setAttribute("currTaskUpload", taskNum);
									JsonObject obj = new JsonObject();
									obj.addProperty("pointsPerTask", pointsPerTask);
									obj.addProperty("hasPassedTest", doesPastSystemTest);
									System.out.println(obj.toString());
									response.setStatus(IValidationsDAO.SUCCESS_STATUS);
									if(solutionPath != null && (!solutionPath.trim().equals(""))){
									File currentFile = new File(solutionPath);//TODO da sloja imeto
									if(currentFile.exists()){
										currentFile.delete();
									}
									}
									response.getWriter().write(obj.toString());
//									request.getSession().setAttribute("invalidFields", false);
								} catch (UserException e) {
									File f = new File(savePath +File.separator + fileDirectory+ File.separator + fileName);
									if (f.exists()) {
										f.delete();
									} 
									System.out.println(e.getMessage());
									e.printStackTrace();
									response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
								} catch (InterruptedException e) {
									File f = new File(savePath +File.separator + fileDirectory+ File.separator + fileName);
									if (f.exists()) {
										f.delete();
									} 
									System.out.println(e.getMessage());
									e.printStackTrace();
									response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
								}
							} else {
								response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
								return;
							}
						}
					}
				}else{
					response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
					return;
				}
				

				}else{
					response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
				}
			} else {
				response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
			}
		} else {
			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
		}
	}


//	@RequestMapping(value = "/uploadSolutionToTaskJava", method = RequestMethod.POST)
//	protected void uploadSolution(HttpServletRequest request, HttpServletResponse response,
//			@RequestParam("datafile") MultipartFile uploadfile) throws ServletException, IOException {
//
//		User user = (User) request.getSession().getAttribute("user");
//		if (!user.isTeacher()) {
//			if (request.getSession().getAttribute("currHomework") != null && request.getParameter("taskNum") != null
//					&& ValidationsDAO.getInstance().isStringValidInteger(request.getParameter("taskNum"))) {
//				
//				String savePath = IValidationsDAO.SAVE_DIR;
//				int taskNum = Integer.parseInt(request.getParameter("taskNum")) - 1;
//				Homework homework = (Homework) request.getSession().getAttribute("currHomework");
//				HomeworkDetails homeworkDetails = homework.getHomeworkDetails();
//				// creates the save directory if it does not exists
//				File fileSaveDir = new File(savePath);
//				if (!fileSaveDir.exists()) {
//					fileSaveDir.mkdir();
//				}
//				String fileName = " ";
//				MultipartFile file = uploadfile;
//				if (!isFileEmptyUploadSolution(file)) {
//					if (!isSizeValidUploadSolution(file)) {
//						response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
//						return;
//					} else {
//						if (!isContentTypeValidUploadSolution(file)) {
//							response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
//							return;
//						} else {
//							if (doesTaskNumExist(taskNum, homeworkDetails)) {
//								fileName = "hwId" + homeworkDetails.getId() + "userId" + user.getId() + "taskNum"
//										+ taskNum + ".java";
//								file.transferTo(new File(savePath + File.separator + fileName));
//								try {
//									DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//									LocalDateTime currDateTime = LocalDateTime.now();
//									  String currDateTimeString = currDateTime.format(formatter);
//									  currDateTime = LocalDateTime.parse(currDateTimeString, formatter);
//									UserDAO.getInstance().setSolutionOfTask(homeworkDetails.getId(), user.getId(), taskNum,
//											fileName, currDateTime);
//									homework.getTasks().get(taskNum).setSolution(fileName);
//									homework.getTasks().get(taskNum).setUploadedOn(currDateTime);
////									request.getSession().setAttribute("invalidFields", false);
//								} catch (UserException e) {
//									File f = new File(savePath + File.separator + fileName);
//									if (f.exists()) {
//										f.delete();
//									}
//									System.out.println(e.getMessage());
//									e.printStackTrace();
//									response.setStatus(IValidationsDAO.INTERNAL_SERVER_ERROR_STATUS);
//								}
//							} else {
//								response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
//								return;
//							}
//						}
//					}
//				}else{
//					response.setStatus(IValidationsDAO.BAD_REQUEST_STATUS);
//					return;
//				}
//				request.getSession().setAttribute("currTaskUpload", taskNum);
//				response.setStatus(IValidationsDAO.SUCCESS_STATUS);
//			} else {
//				response.setStatus(IValidationsDAO.PAGE_NOT_FOUND_STATUS);
//			}
//		} else {
//			response.setStatus(IValidationsDAO.FORBIDDEN_STATUS);
//		}
//	}

	private boolean isContentTypeValidUploadSolution(MultipartFile file) {
		String contentType = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
		if (!(contentType.equals(".java"))) {
			return false;
		}
		return true;
	}

	private boolean isFileEmptyUploadSolution(MultipartFile file) {
		if (file != null && file.getSize() != 0) {
			return false;
		}
		return true;
	}

	private boolean isSizeValidUploadSolution(MultipartFile file) {
		long sizeInMb = file.getSize() / (1024 * 1024);
		if (sizeInMb >= IValidationsDAO.MAX_SIZE_IN_MB_FOR_TASK_SOLUTION) {
			return false;
		}
		return true;
	}
	

	private boolean doesGradeHaveInvalidSymbols(String teacherGradeString) {
		if (teacherGradeString.length() < 10) {
			for (int i = 0; i < teacherGradeString.length(); i++) {
				if ((int) teacherGradeString.charAt(i) < IValidationsDAO.ASCII_TABLE_VALUE_OF_ZERO
						|| (int) teacherGradeString.charAt(i) > IValidationsDAO.ASCII_TABLE_VALUE_OF_NINE) {
					return true;
				}
			}
		}
		return false;
	}

}
