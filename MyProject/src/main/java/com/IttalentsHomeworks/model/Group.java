package com.IttalentsHomeworks.model;

import java.util.ArrayList;

import com.IttalentsHomeworks.DAO.GroupDAO;

public class Group {
	
	private int id;
	private String name;
	private ArrayList<Teacher> teachers;
	private ArrayList<Student> students;
	private ArrayList<HomeworkDetails> homeworks;
	
	public Group(int id, String name, ArrayList<Teacher> teachers, ArrayList<Student> students,
			ArrayList<HomeworkDetails> homeworks) {
		this.id = id;
		this.name = name;
		this.teachers = teachers;
		this.students = students;
		this.homeworks = homeworks;
	}
	
	public Group(int id, String name, ArrayList<Teacher> teachers) {
		this.id = id;
		this.name = name;
		this.teachers = teachers;
	}
	
	public Group(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public Group(String name, ArrayList<Teacher> teachers) {
		this.name = name;
		this.teachers = teachers;	
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Teacher> getTeachers() {
		return teachers;
	}
	public void setTeachers(ArrayList<Teacher> teachers) {
		this.teachers = teachers;
	}
	public ArrayList<Student> getStudents() {
		return students;
	}
	public void setStudents(ArrayList<Student> students) {
		this.students = students;
	}
	public ArrayList<HomeworkDetails> getHomeworks() {
		return homeworks;
	}
	public void setHomeworks(ArrayList<HomeworkDetails> homeworks) {
		this.homeworks = homeworks;
	}
	
	
}
