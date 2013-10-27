package typetodo.logic;

public class CommandHelp implements Command{
	private static final String MESSAGE_HELP = "";
	private Schedule sc;
	
	public CommandHelp(Schedule sc) {
		this.sc = sc;
	}
	
	@Override
	public String execute() throws Exception {
		sc.help();
		
		String feedback = MESSAGE_HELP;
		return feedback;
	}
}
