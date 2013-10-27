package typetodo.model;

import java.util.Comparator;

import org.joda.time.DateTime;

public class DeadlineTask extends Task {
	private DateTime deadline;

	public DeadlineTask(String name, String description, DateTime deadline) {
		super(name, description);
		this.setDeadline(deadline);
	}

	public DeadlineTask(int taskId, String name, String description,
			DateTime deadline) {
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
		return new DeadlineTask(this.getTaskId(), this.getTitle(),
				this.getDescription(), this.getDeadline());
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TITLE: " + this.getTitle() + " ");
		sb.append("DESCRIPTION: " + this.getDescription() + " ");
		sb.append("DEADLINE: " + this.getDeadline());

		return sb.toString();
	}

	public static final Comparator<DeadlineTask> COMPARE_BY_DATE = new Comparator<DeadlineTask>() {
		@Override
		public int compare(DeadlineTask task1, DeadlineTask task2) {
			return task1.getDeadline().compareTo(task2.getDeadline());
		}
	};
}