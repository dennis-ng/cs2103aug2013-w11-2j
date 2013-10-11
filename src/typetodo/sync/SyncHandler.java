package typetodo.sync;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import typetodo.db.DBHandler;
import typetodo.logic.Task;

public class SyncHandler {
	public DateTime lastSyncDate;
	
	private GCalHandler gcal;
	private DBHandler db;
	
	public SyncHandler() throws IOException {
		db = new DBHandler();
		gcal = new GCalHandler();
	}
	
	public void syncToGoogleCalendar() {
		ArrayList<Task> tasks = db.retrieveAll();
		for (int index = 0; index < tasks.size(); ++index) {
			Task task = tasks.get(index);
			try {
				if(!gcal.hasTask(task)) {
					gcal.addTaskToGCal(task);
					try {
						db.updateTask(task);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else { //task already exist
					System.out.println(task.getName() + " has already been added to gcal before");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("All Task Has Been Sync");
	}
	
}
