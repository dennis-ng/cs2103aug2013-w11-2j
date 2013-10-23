package typetodo.logic;

public class HelpCommand implements Command{
	Scheduler sc;
	
	public HelpCommand(Scheduler sc) {
		this.sc = sc;
	}
	
	@Override
	public void execute() throws Exception {
		sc.help();
	}
}
