package typetodo.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.joda.time.DateTime;

import typetodo.db.DbController;
import typetodo.model.Task;
import typetodo.ui.View;

public class SyncController {
	private static final String MESSAGE_SYNC = "Sync as of %s";
	
	public DateTime lastSyncDate;

	private GCalHandler googleSchedule;
	private final DbController db;
	private final View view;

	public SyncController(View view) throws IOException {
		db = DbController.getInstance();
		this.view = view;

		if (db.getProperty("lastSyncDate") != null) {
			lastSyncDate = new DateTime(db.getProperty("lastSyncDate"));
		} else {
			lastSyncDate = new DateTime().minusYears(20);
		}
		this.googleSchedule = null;
	}
	
	private class SyncWorker extends SwingWorker<Void, String>{
		private View view;
		private SyncController syncController;
		
		public SyncWorker(SyncController syncController, View view) throws IOException {
			this.syncController = syncController;
			this.view = view;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			publish("Syncing additional tasks from in local schedule..");
			syncController.syncAdditionalTasksFromLocalSchedule();;
			publish("Syncing tasks that exist in both schedule..");
			syncController.syncTasksThatExistInBothSchedule();
			publish("Syncing additional tasks from in google schedule..");
			syncController.syncAdditionalTasksFromGoogleSchedule();
			syncController.updateLastSyncDate();
			publish(String.format(MESSAGE_SYNC, new DateTime().toString("EEE, dd MMM yyyy HH:mm")));
			return null;
		}
		
		@Override
		protected void process(final List<String> chunks) {
			// Updates the messages text area
			for (final String progress : chunks) {
				view.displayFeedBack(progress);
			}
		}
	}

	private void connectToGoogleSchedule() {
		googleSchedule = new GCalHandler();
	}

	public void twoWaySync() throws Exception {
		while (googleSchedule == null) {
			this.connectToGoogleSchedule();
		}

		(new SyncWorker(this, view)).execute();
	}

	private void syncAdditionalTasksFromLocalSchedule() throws Exception {
		ArrayList<Task> localTasks = db.retrieveAll();
		for (int index = 0; index < localTasks.size(); ++index) {
			Task localTask = localTasks.get(index);
			Task googleTask = googleSchedule.retrieveTask(localTask);

			if (googleTask == null) { // if task is not in google calendar
				// Case 1a: task had not be sync before
				if (localTask.getDateModified().isAfter(lastSyncDate)) {
					try {
						googleSchedule.addTask(localTask);
					} catch (IOException e) {
						// TODO: Auto-generated catch block
						e.printStackTrace();
					}

					if (db.updateTask(localTask)) {
//						System.out.println("GoogleId has been added to \""
//								+ localTask.getTitle() + "\"");
					}

				} else if (localTask.getDateModified().isBefore(lastSyncDate)) { // Case 
					db.deleteTask(localTask.getTaskId());
				}
			}
		}
	}

	private void syncTasksThatExistInBothSchedule() {
		ArrayList<Task> localTasks = db.retrieveAll();

		for (int index = 0; index < localTasks.size(); ++index) {
			Task localTask = localTasks.get(index);
			Task googleTask = googleSchedule.retrieveTask(localTask);

			if (googleTask != null) { // case 2: Task already exists in google
																// calendar
				// check for differences and take the one the lastest modified date
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
				}
				// Case 1b: task was added but later deleted. Delete from gcal.
				else if (googleTask.getDateModified().isBefore(lastSyncDate)) {
					googleSchedule.deleteTask(googleTask);
				}
			}
		}
	}

	private void syncTask(Task localTask, Task googleTask) {
		if (googleTask.getDateModified().isAfter(localTask.getDateModified())) {
			googleTask.setTaskId(localTask.getTaskId());
			googleTask.setGoogleId(localTask.getGoogleId());
			googleTask.setDateCreated(localTask.getDateCreated());
			googleTask.updateDateModified(); // set modded day // to today.
			try {
				db.updateTask(googleTask);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (googleTask.getDateModified().isBefore(
				localTask.getDateModified())) {
			googleSchedule.updateTask(localTask);
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
	
	private void updateLastSyncDate() {
		lastSyncDate = new DateTime();
		db.setProperty("lastSyncDate", lastSyncDate.toString());
	}
}
