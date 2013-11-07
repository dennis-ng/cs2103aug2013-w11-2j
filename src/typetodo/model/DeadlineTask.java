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
			
		if (this.getDeadline().isBefore(new DateTime())) {
			sb.append("<font face=\"century gothic\" color=\"#B6B6B4\">");
			sb.append("[Id: " + this.getTaskId() + "] ");
			sb.append("<b>");
			sb.append(this.getTitle() + " ");
			sb.append("</b>");
			
			sb.append("due at ");
			sb.append("<b>");
			sb.append(this.getDeadline().toString("HH:mm"));
			sb.append("</b>");
		} else {
			sb.append("<font face=\"century gothic\">");
			sb.append("[Id: " + this.getTaskId() + "] ");
			sb.append("<b>");
			sb.append(this.getTitle() + " ");
			sb.append("</b>");

			sb.append("due at ");
			sb.append("<font face=\"century gothic\" color=\"red\">");
			sb.append("<b>");
			sb.append(this.getDeadline().toString("HH:mm"));
			sb.append("</b>");
			sb.append("</font>");
		}

		if (this.getDescription() != null && !this.getDescription().equals("")) {
			sb.append("\n");
			sb.append("<br>");
			sb.append("<i>");
			sb.append(" - " + this.getDescription().trim());
			sb.append("</i>");
		}
		sb.append("</font>");
		sb.append("<br>");
		
		return sb.toString();
	}

	public static final Comparator<DeadlineTask> COMPARE_BY_DATE = new Comparator<DeadlineTask>() {
		@Override
		public int compare(DeadlineTask task1, DeadlineTask task2) {
			return task1.getDeadline().compareTo(task2.getDeadline());
		}
	};
}