package typetodo.logic;

public class HelpCommand implements Command{
	private static final String MESSAGE_HELP = "";
	private Schedule sc;
	
	public HelpCommand(Schedule sc) {
		this.sc = sc;
	}
	
	@Override
	public String execute() throws Exception {
		sc.help();
		
		String feedback = MESSAGE_HELP;
		return feedback;
	}
}
