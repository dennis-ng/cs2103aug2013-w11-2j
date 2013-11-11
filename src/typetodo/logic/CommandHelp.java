package typetodo.logic;

public class CommandHelp implements Command {
	private HelpController hc;

	public CommandHelp(HelpController hc) {
		this.hc = hc;
	}

	@Override
	public String execute() throws Exception {
		String feedback;
		feedback=hc.getFeedback();
		return feedback;
	}
}
