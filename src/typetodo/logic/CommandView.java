package typetodo.logic;

import org.joda.time.DateTime;

import typetodo.model.TaskType;

public class CommandView implements Command {
	private static final String MESSAGE_VIEWBYDATE = "Viewing tasks of %s";
	private static final String MESSAGE_VIEWALL = "Viewing all tasks in schedule";
	private CurrentTaskListManager taskListManager;
	private DateTime dateTime;

	public CommandView(CurrentTaskListManager taskListManager, DateTime dateTime) {
		this.taskListManager = taskListManager;
		this.dateTime = dateTime;
	}

	public CommandView(CurrentTaskListManager taskListManager) {
		this.taskListManager = taskListManager;
	}

	@Override
	public String execute() throws Exception {
		String feedback;
		if (dateTime == null) {
			this.taskListManager.setByDateRange(null, null);
			feedback = String.format(MESSAGE_VIEWALL);
			return feedback;
		} else {
			this.taskListManager.setByDateRange(dateTime, dateTime);
			feedback = String.format(MESSAGE_VIEWBYDATE,
					dateTime.toString("EEE, dd MMM yyyy"));
		}

		return feedback;
	}
}
