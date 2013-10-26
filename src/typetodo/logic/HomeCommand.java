package typetodo.logic;

public class HomeCommand implements Command {
	private static final String MESSAGE_HOME = "";
	private Schedule sc;
	
	public HomeCommand(Schedule sc) {
		this.sc = sc;
	}
	
	@Override
	public String execute() throws Exception {
		sc.viewTasksofToday();
		String feedback = MESSAGE_HOME;
		return feedback;
	}
}
