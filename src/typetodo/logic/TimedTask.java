package typetodo.logic;

import java.util.Date;

public class TimedTask extends Task {
	private Date start;
	private Date end;
	private boolean isBusy;
	
	public TimedTask(String name, String description, Date start, Date end, boolean isBusy) {
		this.setName(name);
		this.setDescription(description);
		this.setStart(start);
		this.setEnd(end);
		this.setStatus(Status.INCOMPLETE);
		this.setBusy(isBusy);
	}

	public TimedTask(int taskId, String name, String description, Date start, Date end, boolean isBusy) {
		this.setTaskId(taskId);
		this.setName(name);
		this.setDescription(description);
		this.setStart(start);
		this.setEnd(end);
		this.setStatus(Status.INCOMPLETE);
		this.setBusy(isBusy);
	}
	
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
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