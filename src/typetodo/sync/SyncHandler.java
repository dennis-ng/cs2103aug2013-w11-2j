package typetodo.sync;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import typetodo.db.DbController;
import typetodo.model.Task;

public class SyncHandler {
	public DateTime lastSyncDate;

	private final GCalHandler gcal;
	private final DbController db;

	public SyncHandler() throws IOException {
		db = DbController.getInstance();
		gcal = new GCalHandler();
		DateTimeFormatter fmt = DateTimeFormat.forPattern("H:mm d-MMM yyyy");
		lastSyncDate = fmt.parseDateTime("17:00 26-OCT 2013"); // for testing
	}

	public void syncToGoogleCalendar() {
		ArrayList<Task> tasks = db.retrieveAll();
		for (int index = 0; index < tasks.size(); ++index) {
			Task task = tasks.get(index);
			try {
				if (!gcal.hasTask(task)) { // if task is not in google calendar
					System.out.println(task.getTitle() + " is not in google system");

					// Case 1a: task had not be sync before
					if (task.getDateModified().isAfter(lastSyncDate)) {
						gcal.addTaskToGCal(task);
						try {
							if (db.updateTask(task)) {
								System.out.println("GoogleId has been added to \"" + task.getTitle() + "\"");
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// Case 1b: task was added but later deleted. Delete from local db
					else if (task.getDateModified().isBefore(lastSyncDate)) {
						db.deleteTask(task.getTaskId());
						System.out.println("\"" + task.getTitle()
								+ "\" has been deleted from db");
					}
				}
				// case 2: Task already exists in google calendar, have to check for
				// updates
				else {
					// check for differences and take the one the lastest modified date
					System.out.println("\"" + task.getTitle()
							+ "\" exists in Google Calendar");
					System.out.println("Searching for differences in task");

					Task taskToBeUpdated;
					// Checks for update
					if ((taskToBeUpdated = gcal.getUpdate(task)) != null) {
						// Set updating parameters
						taskToBeUpdated.setTaskId(task.getTaskId());
						taskToBeUpdated.setGoogleId(task.getGoogleId());
						taskToBeUpdated.setDateCreated(task.getDateCreated());
						taskToBeUpdated.updateDateModified(); // set modded day
																															// to today.
						try {
							db.updateTask(taskToBeUpdated);
							System.out.println("\"" + taskToBeUpdated.getTitle()
									+ "\" has been updated in db");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						System.out.println("\"" + task.getTitle()
								+ "\" has been updated in Google Calendar");
					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("All Tasks have been Sync to google Calendar");
	}

}
