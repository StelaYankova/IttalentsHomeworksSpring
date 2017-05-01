package com.IttalentsHomeworks.model;

import java.time.LocalDateTime;

public class HomeworkDetails {

	private int id;
	private String heading;
	private LocalDateTime openingTime;
	private LocalDateTime closingTime;
	private int numberOfTasks;
	private String tasksFile;
	private int daysLeft;
	
	public HomeworkDetails(int id, String heading, LocalDateTime openingTime, LocalDateTime closingTime,
			int numberOfTasks, String tasksFile) {
		this.id = id;
		this.heading = heading;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
		this.numberOfTasks = numberOfTasks;
		this.tasksFile = tasksFile;
	}
	public HomeworkDetails(String heading, LocalDateTime openingTime, LocalDateTime closingTime, int numberOfTasks,
			String tasksFile) {
		this.heading = heading;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
		this.numberOfTasks = numberOfTasks;
		this.tasksFile = tasksFile;	
	}
	public HomeworkDetails(int id, String heading, LocalDateTime openingTime, LocalDateTime closingTime) {
		this.id = id;
		this.heading = heading;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public LocalDateTime getOpeningTime() {
		return openingTime;
	}
	public void setOpeningTime(LocalDateTime openingTime) {
		this.openingTime = openingTime;
	}
	public LocalDateTime getClosingTime() {
		return closingTime;
	}
	public void setClosingTime(LocalDateTime closingTime) {
		this.closingTime = closingTime;
	}
	public int getNumberOfTasks() {
		return numberOfTasks;
	}
	public void setNumberOfTasks(int numberOfTasks) {
		this.numberOfTasks = numberOfTasks;
	}
	public String getTasksFile() {
		return tasksFile;
	}
	public void setTasksFile(String tasksFile) {
		this.tasksFile = tasksFile;
	}
	public int getDaysLeft() {
		return daysLeft;
	}
	public void setDaysLeft(int daysLeft) {
		this.daysLeft = daysLeft;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		System.out.println("hash is " + result);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HomeworkDetails other = (HomeworkDetails) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
