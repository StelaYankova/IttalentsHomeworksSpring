package com.IttalentsHomeworks.model;

import java.util.ArrayList;

public class Student extends User{
	
	private ArrayList<Homework> homeworks;

	public Student(int id, String username, String password, String email, boolean isTeacher, ArrayList<Group> groups, ArrayList<Homework> homeworks) {
		super(id, username, password, email, isTeacher, groups);
		this.homeworks = homeworks;
	}
	public Student(String username, String password, String repeatedPassword, String email) {
		super(username, password, repeatedPassword, email);
	}
	public Student(int id, String username, String email, boolean isTeacher, ArrayList<Homework> homeworks) {
		super(id, username, email, isTeacher);
		this.homeworks = homeworks;

	}
	public Student(int id, String username, String email, boolean isTeacher) {
		super(id, username, email, isTeacher);
	}
	
	public ArrayList<Homework> getHomeworks() {
		return homeworks;
	}

	public void setHomeworks(ArrayList<Homework> homeworks) {
		this.homeworks = homeworks;
	}
	
	
}
