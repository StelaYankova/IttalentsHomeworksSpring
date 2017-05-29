package com.IttalentsHomeworks.model;

import java.util.ArrayList;

public class Homework {

	private int teacherGrade;
	private String teacherComment;
	private ArrayList<Task> tasks;
	private HomeworkDetails homeworkDetails;

	public Homework(int teacherGrade, String teacherComment, ArrayList<Task> tasks, HomeworkDetails homeworkDetails) {
		this.teacherGrade = teacherGrade;
		this.teacherComment = teacherComment;
		this.tasks = tasks;
		this.homeworkDetails = homeworkDetails;
	}

	public int getTeacherGrade() {
		return teacherGrade;
	}

	public void setTeacherGrade(int teacherGrade) {
		this.teacherGrade = teacherGrade;
	}

	public String getTeacherComment() {
		return teacherComment;
	}

	public void setTeacherComment(String comment) {
		this.teacherComment = comment;
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}

	public HomeworkDetails getHomeworkDetails() {
		return homeworkDetails;
	}

	public void setHomeworkDetails(HomeworkDetails homeworkDetails) {
		this.homeworkDetails = homeworkDetails;
	}

}
