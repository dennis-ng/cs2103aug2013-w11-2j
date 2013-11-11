package typetodo.logic;

public class CommandSearch implements Command{
	private static final String MESSAGE_SEARCH = "Displaying all tasks containing \"%s\"";
	private CurrentTaskListManager taskListManager;
	private String keyword;
	
	public CommandSearch(CurrentTaskListManager taskListManager, String keyword) {
		this.taskListManager = taskListManager;
		this.keyword = keyword;
	}
	public String execute() throws Exception {
		String feedback = String.format(MESSAGE_SEARCH, keyword);
		taskListManager.setBySearchResult(keyword);
		return feedback;
	}
}
