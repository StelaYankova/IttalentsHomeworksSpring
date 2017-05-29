package com.IttalentsHomeworks.model;

import java.util.ArrayList;

public class Teacher extends User {

	public Teacher(int id, String username, String password, String email, ArrayList<Group> groups) {
		super(id, username, password, email, true, groups);
	}

	public Teacher(int id, String username, String email) {
		super(id, username, email, true);
	}

	public Teacher(String username, String password, String repeatedPassword, String email) {
		super(username, password, repeatedPassword, email);
		super.isTeacher = true;
	}

}
