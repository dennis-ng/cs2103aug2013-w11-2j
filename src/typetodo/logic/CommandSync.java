package typetodo.logic;

import typetodo.sync.SyncController;

public class CommandSync implements Command{
	private SyncController syncController;
	
	public CommandSync (SyncController syncController) {
		this.syncController = syncController;
	}
	
	@Override
	public String execute() throws Exception {
		syncController.twoWaySync();
		return "null";
	}
}
