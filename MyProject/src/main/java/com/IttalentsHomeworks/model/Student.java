package com.IttalentsHomeworks.model;

import java.util.ArrayList;

public class Student extends User {

	private ArrayList<Homework> homeworks;

	public Student(int id, String username, String password, String email, ArrayList<Group> groups,
			ArrayList<Homework> homeworks) {
		super(id, username, password, email, false, groups);
		this.homeworks = homeworks;
	}

	public Student(String username, String password, String repeatedPassword, String email) {
		super(username, password, repeatedPassword, email);
		super.isTeacher = false;
	}

	public Student(int id, String username, String email, ArrayList<Homework> homeworks) {
		super(id, username, email, false);
		this.homeworks = homeworks;
		super.isTeacher = false;
	}

	public Student(int id, String username, String email) {
		super(id, username, email, false);
		super.isTeacher = false;
	}

	public ArrayList<Homework> getHomeworks() {
		return homeworks;
	}

	public void setHomeworks(ArrayList<Homework> homeworks) {
		this.homeworks = homeworks;
	}

}
