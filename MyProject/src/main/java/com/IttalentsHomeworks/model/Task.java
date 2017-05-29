package com.IttalentsHomeworks.model;

import java.time.LocalDateTime;

public class Task {

	private int taskNumber;
	private String solution;
	private LocalDateTime uploadedOn;
	private boolean hasPassedSystemTest;

	public Task(int taskNumber, String solution, LocalDateTime uploadedOn, boolean hasPassedSystemTest) {
		this.taskNumber = taskNumber;
		this.solution = solution;
		this.uploadedOn = uploadedOn;
		this.hasPassedSystemTest = hasPassedSystemTest;
	}

	public int getTaskNumber() {
		return taskNumber;
	}

	public void setTaskNumber(int taskNumber) {
		this.taskNumber = taskNumber;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public LocalDateTime getUploadedOn() {
		return uploadedOn;
	}

	public void setUploadedOn(LocalDateTime uploadedOn) {
		this.uploadedOn = uploadedOn;
	}

	public boolean isHasPassedSystemTest() {
		return hasPassedSystemTest;
	}

	public void setHasPassedSystemTest(boolean hasPassedSystemTest) {
		this.hasPassedSystemTest = hasPassedSystemTest;
	}

}
