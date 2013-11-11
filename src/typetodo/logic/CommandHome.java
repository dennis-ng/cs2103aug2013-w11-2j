package typetodo.logic;

public class CommandHome implements Command {
	private static final String MESSAGE_HOME = "Displaying Upcoming Tasks";
	private CurrentTaskListManager taskListManager;
	
	public CommandHome(CurrentTaskListManager taskListManager) {
		this.taskListManager = taskListManager;
	}
	
	@Override
	public String execute() throws Exception {
		this.taskListManager.setByDefault();
		String feedback = MESSAGE_HOME;
		return feedback;
	}
}
