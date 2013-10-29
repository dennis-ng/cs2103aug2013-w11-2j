package typetodo.logic;

import org.joda.time.DateTime;

import typetodo.sync.SyncHandler;

public class CommandSync implements Command{
	private static final String MESSAGE_SYNC = "Sync as of " + new DateTime();
	private SyncHandler sh;
	
	public CommandSync (SyncHandler sh) {
		this.sh = sh;
	}
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		sh.twoWaySync();
		return MESSAGE_SYNC;
	}
	

}
