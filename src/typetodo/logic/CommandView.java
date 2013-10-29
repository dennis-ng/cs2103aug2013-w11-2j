package typetodo.logic;

import org.joda.time.DateTime;

public class CommandView implements Command{
	private static final String MESSAGE_VIEWBYDATE = "Viewing tasks of %s";
	private static final String MESSAGE_VIEWALL = "Viewing all tasks in schedule";
	private Schedule schedule;
	private DateTime dateTime;
	
	public CommandView(Schedule schedule, DateTime dateTime) {
		this.schedule = schedule;
		this.dateTime = dateTime;
	}
	
	public CommandView(Schedule schedule) {
		this.schedule = schedule;
	}
	
	@Override
	public String execute() throws Exception {
		String feedback;
		if (dateTime == null) {
			schedule.viewAllTasks();
			feedback = String.format(MESSAGE_VIEWALL);
			return feedback;
		}
		schedule.viewTasksByDate(dateTime);
		feedback = String.format(MESSAGE_VIEWBYDATE, dateTime.toString("EEE, dd MMM yyyy HH:mm"));
		return feedback;
	}
}
