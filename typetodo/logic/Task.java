package typetodo.logic;

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
	
	private int taskId;
	private String name;
	private String description;
	private Status status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
}
