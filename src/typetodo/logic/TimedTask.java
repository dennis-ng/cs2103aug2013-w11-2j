package typetodo.logic;

import java.util.Date;

import org.joda.time.DateTime;

public class TimedTask extends Task {
	private DateTime start;
	private DateTime end;
	private boolean isBusy;
	
	public TimedTask(String name, String description, DateTime start, DateTime end, boolean isBusy) {
		this.setName(name);
		this.setDescription(description);
		this.setStart(start);
		this.setEnd(end);
		this.setStatus(Status.INCOMPLETE);
		this.setBusy(isBusy);
	}

	public TimedTask(int taskId, String name, String description, DateTime start, DateTime end, boolean isBusy) {
		this.setTaskId(taskId);
		this.setName(name);
		this.setDescription(description);
		this.setStart(start);
		this.setEnd(end);
		this.setStatus(Status.INCOMPLETE);
		this.setBusy(isBusy);
	}
	
	public DateTime getStart() {
		return start;
	}

	public void setStart(DateTime start) {
		this.start = start;
	}

	public DateTime getEnd() {
		return end;
	}

	public void setEnd(DateTime end) {
		this.end = end;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}	
	
	public Task makeCopy() {
		return new TimedTask(this.getTaskId(), this.getName(), this.getDescription(), this.getStart(),
				this.getEnd(), this.isBusy());
	}
}