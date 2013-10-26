package typetodo.logic;

public class ExitCommand implements Command{	
	@Override
	public String execute() throws Exception {
		System.exit(0);
		return null;
	}
}
