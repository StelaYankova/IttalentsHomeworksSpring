package com.IttalentsHomeworks.model;

import java.util.ArrayList;

public abstract class User {
	
	private int id;
	private String username;
	private String password;
	private String repeatedPassword;
	private String email;
	private boolean isTeacher;
	ArrayList<Group> groups;
	

	public User(int id, String username, String password, String email, boolean isTeacher, ArrayList<Group> groups) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.isTeacher = isTeacher;
		this.groups = groups;
	}
	//by creating
	public User(String username, String password, String repeatedPassword, String email){
		this.username = username;
		this.password = password;
		this.repeatedPassword = repeatedPassword;
		this.email = email;
	}
	//if it is another user, you dont have to know his groups and password
	public User(int id, String username, String email, boolean isTeacher) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.isTeacher = isTeacher;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isTeacher() {
		return isTeacher;
	}
	public void setTeacher(boolean isTeacher) {
		this.isTeacher = isTeacher;
	}
	public ArrayList<Group> getGroups() {
		return groups;
	}
	public void setGroups(ArrayList<Group> groups) {
		this.groups = groups;
	}
	public String getRepeatedPassword() {
		return repeatedPassword;
	}
	public void setRepeatedPassword(String repeatedPassword) {
		this.repeatedPassword = repeatedPassword;
	}
	
}
