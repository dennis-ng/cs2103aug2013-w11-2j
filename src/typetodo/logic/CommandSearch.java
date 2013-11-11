package typetodo.logic;

public class CommandSearch implements Command{
	private static final String MESSAGE_SEARCH = "Displaying all tasks containing \"%s\"";
	private Schedule schedule;
	private String keyword;
	
	public CommandSearch(Schedule schedule, String keyword) {
		this.schedule = schedule;
		this.keyword = keyword;
	}
	public String execute() throws Exception {
		String feedback = String.format(MESSAGE_SEARCH, keyword);
		schedule.search(keyword);
		return feedback;
	}
}
