package typetodo.logic;

import org.joda.time.DateTime;

public class CommandView implements Command{
	private static final String MESSAGE_VIEWBYDATE = "Viewing tasks of %s";
	private Schedule schedule;
	private DateTime dateTime;
	
	public CommandView (Schedule schedule, DateTime dateTime) {
		this.schedule = schedule;
		this.dateTime = dateTime;
	}
	@Override
	public String execute() throws Exception {
		schedule.viewTasksByDate(dateTime);
		String feedback = String.format(MESSAGE_VIEWBYDATE, dateTime);
		return feedback;
	}
}
