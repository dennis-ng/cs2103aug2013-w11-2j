package typetodo.logic;

import org.joda.time.DateTime;

public class DeadlineTask extends Task{
	private DateTime deadline;
	
	public DeadlineTask(String name, String description, DateTime deadline) {
		super(name, description);
		this.setDeadline(deadline);
	}

	public DeadlineTask(int taskId, String name, String description, DateTime deadline) {
		super(name, description);
		this.setTaskId(taskId);
		this.setDeadline(deadline);
	}
	
	public DateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(DateTime deadline) {
		this.deadline = deadline;
	}
	
	public Task makeCopy() {
		return new DeadlineTask(this.getTaskId(), this.getTitle(), this.getDescription(), this.getDeadline());
	}
}