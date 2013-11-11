package typetodo.logic;

/**
 * 
 * @author A0091024U
 *
 */
public class CommandExit implements Command{	
	@Override
	public String execute() throws Exception {
		System.exit(0);
		return null;
	}
}
