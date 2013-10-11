package typetodo.logic;

import org.joda.time.DateTime;

import typetodo.db.DateTimeTypeConverter;
import com.google.gson.*;

public class DeadlineTask extends Task{
	private DateTime deadline;
	
	public DeadlineTask(String name, String description, DateTime deadline) {
		this.setName(name);
		this.setDescription(description);
		this.setDeadline(deadline);
		this.setStatus(Status.INCOMPLETE);
	}

	public DeadlineTask(int taskId, String name, String description, DateTime deadline) {
		this.setTaskId(taskId);
		this.setName(name);
		this.setDescription(description);
		this.setDeadline(deadline);
		this.setStatus(Status.INCOMPLETE);
	}
	
	public DateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(DateTime deadline) {
		this.deadline = deadline;
	}
	
	public Task makeCopy() {
		return new DeadlineTask(this.getTaskId(), this.getName(), this.getDescription(), this.getDeadline());
	}
}