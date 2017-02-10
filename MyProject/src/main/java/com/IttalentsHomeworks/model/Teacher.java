package com.IttalentsHomeworks.model;

import java.util.ArrayList;

public class Teacher extends User{

	public Teacher(int id, String username, String password, String email, boolean isTeacher, ArrayList<Group> groups) {
		super(id, username, password, email, isTeacher, groups);
	}
	public Teacher(int id, String username, String email, boolean isTeacher) {
		super(id, username, email, isTeacher);
	}
	public Teacher(String username, String password, String repeatedPassword, String email) {
		super(username, password, repeatedPassword, email);
	}
}
