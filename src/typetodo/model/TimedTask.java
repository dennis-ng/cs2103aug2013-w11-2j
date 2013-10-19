package typetodo.model;

import java.util.Comparator;

import org.joda.time.DateTime;

public class TimedTask extends Task {
	private DateTime start;
	private DateTime end;
	private boolean isBusy;

	public TimedTask(String name, String description, DateTime start,
			DateTime end, boolean isBusy) {
		super(name, description);
		this.setStart(start);
		this.setEnd(end);
		this.setBusy(isBusy);
	}

	public TimedTask(int taskId, String name, String description, DateTime start,
			DateTime end, boolean isBusy) {
		super(name, description);
		this.setTaskId(taskId);
		this.setStart(start);
		this.setEnd(end);
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
		return new TimedTask(this.getTaskId(), this.getTitle(),
				this.getDescription(), this.getStart(), this.getEnd(), this.isBusy());
	}

	public static final Comparator<TimedTask> COMPARE_BY_DATE = new Comparator<TimedTask>() {
		@Override
		public int compare(TimedTask task1, TimedTask task2) {
			if (task1.getEnd().isEqual(task2.getEnd())) {
				return task1.getStart().compareTo(task2.getStart());
			} else {
				return task1.getEnd().compareTo(task2.getEnd());
			}
		}
	};
}