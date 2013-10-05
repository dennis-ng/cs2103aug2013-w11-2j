package typetodo.logic;

import java.util.Date;

public class DeadlineTask extends Task{
	private Date deadline;
	
	public DeadlineTask(String name, String description, Date deadline) {
		this.setName(name);
		this.setDescription(description);
		this.setDeadline(deadline);
		this.setStatus(Status.INCOMPLETE);
	}

	public DeadlineTask(int taskId, String name, String description, Date deadline) {
		this.setTaskId(taskId);
		this.setName(name);
		this.setDescription(description);
		this.setDeadline(deadline);
		this.setStatus(Status.INCOMPLETE);
	}
	
	Date getDeadline() {
		return deadline;
	}

	void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
}