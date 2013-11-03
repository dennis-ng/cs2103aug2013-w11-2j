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

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getTitle() + " ");
		if (this.getStart().toLocalDate().isEqual(this.getEnd().toLocalDate())) {
			sb.append("from " + this.getStart().toString("HH:mm"));
			sb.append(" - ");
			sb.append(this.getEnd().toString("HH:mm"));
			sb.append(" on ");
			sb.append(this.getStart().toString("EEE, dd MMM yyyy"));
			sb.append(" ");
		} else {
			sb.append("from " + this.getStart().toString("EEE, dd MMM yyyy HH:mm"));
			sb.append(" - ");
			sb.append(this.getEnd().toString("EEE, dd MMM yyyy HH:mm"));
		}
		if (!this.getDescription().equals("")) {
			sb.append("\n");
			sb.append("    Notes: " + this.getDescription().trim());
		}
		
		return sb.toString();
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