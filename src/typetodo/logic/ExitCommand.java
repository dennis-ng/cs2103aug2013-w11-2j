package typetodo.logic;

public class ExitCommand implements Command{
	private Scheduler sc;
	public ExitCommand(Scheduler sc) {
		this.sc = sc;
	}
	@Override
	public void execute() throws Exception {
		sc.exit();
	}
}
