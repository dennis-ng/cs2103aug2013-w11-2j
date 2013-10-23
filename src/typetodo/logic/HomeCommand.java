package typetodo.logic;

public class HomeCommand implements Command {
	private Scheduler sc;
	
	public HomeCommand(Scheduler sc) {
		this.sc = sc;
	}
	
	@Override
	public void execute() throws Exception {
		sc.viewTasksofToday();
	}

}
