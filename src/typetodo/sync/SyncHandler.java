package typetodo.sync;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import typetodo.db.DbHandler;
import typetodo.model.Task;

public class SyncHandler {
	public DateTime lastSyncDate;

	private final GCalHandler googleSchedule;
	private final DbHandler db;

	public SyncHandler() throws IOException {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("H:mm d-MMM yyyy");
		lastSyncDate = fmt.parseDateTime("17:00 26-OCT 2013"); // for testing
		db = DbHandler.getInstance();
		googleSchedule = new GCalHandler();
	}

	public void twoWaySync() throws Exception {
		this.syncTasksThatExistInBothSchedule();
		this.syncAdditionalTasksFromLocalSchedule();
		this.syncAdditionalTasksFromGoogleSchedule();
	}
	
	private void syncAdditionalTasksFromLocalSchedule() throws Exception {
		ArrayList<Task> localTasks = db.retrieveAll();
		for (int index = 0; index < localTasks.size(); ++index) {
			Task localTask = localTasks.get(index);
			Task googleTask = googleSchedule.retrieveTask(localTask);
			
			if (googleTask == null) { // if task is not in google calendar
				System.out.println(localTask.getTitle() + " is not in google system");
				// Case 1a: task had not be sync before
				if (localTask.getDateModified().isAfter(lastSyncDate)) {
					try {
						googleSchedule.addTask(localTask);
					} catch (IOException e) {
						//TODO: Auto-generated catch block
						e.printStackTrace();
					}

					if (db.updateTask(localTask)) {
						System.out.println("GoogleId has been added to \"" + localTask.getTitle() + "\"");
					}
					
				} else if (localTask.getDateModified().isBefore(lastSyncDate)) { // Case 1b: task was added but later deleted. Delete from local db
					db.deleteTask(localTask.getTaskId());
					System.out.println("\"" + localTask.getTitle() + "\" has been deleted from db");
				}
			} 
		}
	}

	private void syncTasksThatExistInBothSchedule() {
		ArrayList<Task> localTasks = db.retrieveAll();
		
		for (int index = 0; index < localTasks.size(); ++index) {
			Task localTask = localTasks.get(index);
			Task googleTask = googleSchedule.retrieveTask(localTask);

			if (googleTask != null) { // case 2: Task already exists in google calendar
				// check for differences and take the one the lastest modified date
				System.out.println("\"" + localTask.getTitle() + "\" exists in Google Calendar");
				this.syncTask(localTask, googleTask);	
			}
		}
	}

	private void syncAdditionalTasksFromGoogleSchedule() throws IOException {
		ArrayList<Task> googleTasks = googleSchedule.retrieveAllTasks();
		for (Task googleTask : googleTasks) {
			if (!this.hasTask(googleTask)) {	
				if (googleTask.getDateModified().isAfter(lastSyncDate)) {
					googleTask.updateDateModified();
					try {
						db.addTask(googleTask);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("\"" + googleTask.getTitle() + "\" has been added into db");
				}
				// Case 1b: task was added but later deleted. Delete from gcal.
				else if (googleTask.getDateModified().isBefore(lastSyncDate)) {
					googleSchedule.deleteTask(googleTask);
					System.out.println("\"" + googleTask.getTitle()
							+ "\" has been deleted from google calendar");
				}
			}
		}
	}

	private void syncTask(Task localTask, Task googleTask) {
		if (googleTask.getDateModified().isAfter(localTask.getDateModified())) {
			googleTask.setTaskId(localTask.getTaskId());
			googleTask.setGoogleId(localTask.getGoogleId());
			googleTask.setDateCreated(localTask.getDateCreated());
			googleTask.updateDateModified(); // set modded day																									// to today.
			try {
				db.updateTask(googleTask);
				System.out.println("\"" + googleTask.getTitle()
						+ "\" has been updated in db");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (googleTask.getDateModified().isBefore(localTask.getDateModified())) {
			googleSchedule.updateTask(localTask);
			System.out.println("\"" + localTask.getTitle()
					+ "\" has been updated in Google Calendar");
		}
	}

	private boolean hasTask(Task googleTask) {
		ArrayList<Task> allTasksInDb = db.retrieveAll();

		if (allTasksInDb == null) {
			return false;
		}

		for (Task task : allTasksInDb) {
			if (task.getGoogleId() == null) {

			} else {
				if (task.getGoogleId().equals(googleTask.getGoogleId())) {
					return true;
				}
			}
		}

		return false;
	}

}
