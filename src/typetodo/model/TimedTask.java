package typetodo.model;

import java.util.Comparator;

import org.joda.time.DateTime;

/**
 * 
 * @author A0091024U
 *
 */
public class TimedTask extends Task {
	private DateTime start;
	private DateTime end;

	public TimedTask(String name, String description, DateTime start, DateTime end) {
		super(name, description);
		this.setStart(start);
		this.setEnd(end);
	}

	public TimedTask(int taskId, String name, String description, DateTime start, DateTime end) {
		super(name, description);
		this.setTaskId(taskId);
		this.setStart(start);
		this.setEnd(end);
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

	public Task makeCopy() {
		TimedTask copy = new TimedTask(this.getTaskId(), this.getTitle(),
				this.getDescription(), this.getStart(), this.getEnd());
		copy.setStatus(this.getStatus());
		copy.setDateCreated(this.getDateCreated());
		copy.setDateModified(this.getDateModified());
		
		return copy;
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