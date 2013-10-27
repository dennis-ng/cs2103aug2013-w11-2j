package typetodo.logic;

public class CommandHome implements Command {
	private static final String MESSAGE_HOME = "";
	private Schedule sc;
	
	public CommandHome(Schedule sc) {
		this.sc = sc;
	}
	
	@Override
	public String execute() throws Exception {
		sc.viewTasksofToday();
		String feedback = MESSAGE_HOME;
		return feedback;
	}
}
