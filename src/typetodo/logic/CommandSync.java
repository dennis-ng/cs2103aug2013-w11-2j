package typetodo.logic;

import typetodo.sync.SyncHandler;

public class CommandSync implements Command{
	private SyncHandler sh;
	
	public CommandSync (SyncHandler sh) {
		this.sh = sh;
	}
	
	@Override
	public String execute() throws Exception {
		sh.twoWaySync();
		return "null";
	}
}
