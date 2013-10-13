package typetodo.logic;

import org.joda.time.DateTime;

/**
 * This abstract class is used to create 3 types of task,
 * namely, TimedTask, Deadline Task, Floating Task
 * @author Phan Shi Yu
 *
 */
public abstract class Task {
	public static enum Status {
		COMPLETED, INCOMPLETE, DISCARDED;
	}

	private int TaskId;
	private String googleId; //Retrive after sync
	private String title;
	private String description;
	private Status status;
	private DateTime dateCreated;
	private DateTime dateModified;

	public Task(String name, String description) {
		this.setTitle(name);
		this.setDescription(description);
		dateCreated = new DateTime();
		dateModified = new DateTime();
		this.setStatus(Status.INCOMPLETE);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String name) {
		this.title = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getTaskId() {
		return TaskId;
	}
	
	public void setTaskId(int taskId) {
		this.TaskId = taskId;
	}
	
	public String toString() {
		return (this.getTitle() + " " + this.getDescription());
	}
	
	public abstract Task makeCopy();

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
	/**
	 * @return the dateCreated
	 */
	public DateTime getDateCreated() {
		return dateCreated;
	}

	/**
	 * @return the dateModified
	 */
	public DateTime getDateModified() {
		return dateModified;
	}

	/**
	 * @param dateModified the dateModified to set
	 */
	public void setDateModified(DateTime dateModified) {
		this.dateModified = dateModified;
	}

	/**
	 * @return the googleId
	 */
	public String getGoogleId() {
		return googleId;
	}

	/**
	 * @param googleCalendarEventId the googleCalendarEventId to set
	 */
	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}
}
