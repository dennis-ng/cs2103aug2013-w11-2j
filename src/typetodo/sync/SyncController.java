package typetodo.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.joda.time.DateTime;

import typetodo.db.DbController;
import typetodo.model.Task;
import typetodo.ui.View;

/**
 * The SyncController class handles the two way sync between TypeToDo's and Google's schedule.
 * @author A0091024U
 *
 */
public class SyncController {
	private static final String MESSAGE_SYNC = "Sync as of %s. Please type 'view all' to refresh";
	private static final String MESSAGE_SYNC_FROM_LOCAL = "Syncing additional tasks from local schedule..";
	private static final String MESSAGE_SYNC_EXISTING = "Syncing tasks that exist in both schedules..";
	private static final String MESSAGE_SYNC_FROM_GOOGLE = "Syncing additional tasks from google schedule..";
	private static final String DATE_FORMAT_FOR_LAST_SYNC = "EEE, dd MMM yyyy HH:mm";
	private static final String PROPERTY_NAME_LAST_SYNC = "lastSyncDate";
	
	public DateTime lastSyncDate;

	private GoogleSchedule googleSchedule;
	private final DbController dataBase;
	private final View view;

	public SyncController(View view) throws IOException {
		dataBase = DbController.getInstance();
		this.view = view;
		this.initializeLastSyncDate();
		this.googleSchedule = null;
	}

	/**
	 * Syncs the local schedule with the Google schedule
	 * @throws Exception
	 */
	public void twoWaySync() throws Exception {
		while (googleSchedule == null) {
			this.connectToGoogleSchedule();
		}
		
		(new SyncWorker(this, view)).execute(); //executes the sync on a separate thread
	}

	private void connectToGoogleSchedule() {
		googleSchedule = new GoogleSchedule();
	}
	
	private void syncAdditionalTasksFromLocalSchedule() throws Exception {
		ArrayList<Task> localTasks = dataBase.retrieveAll();
		for (int index = 0; index < localTasks.size(); ++index) {
			Task localTask = localTasks.get(index);
			Task googleTask = googleSchedule.retrieveTask(localTask);

			if (googleTask == null) { // if task is not in google calendar
				// Case 1a: task had not be sync before
				if (localTask.getDateModified().isAfter(lastSyncDate)) {
					try {
						googleSchedule.addTask(localTask);
					} catch (IOException e) {
						throw new Exception("Failed to add task into google schedule");
					}

				dataBase.updateTask(localTask);

				//task was added but later deleted. delete from local Schedule
				} else if (localTask.getDateModified().isBefore(lastSyncDate)) { 
					dataBase.deleteTask(localTask.getTaskId());
				}
			}
		}
	}

	private void syncTasksThatExistInBothSchedule() {
		ArrayList<Task> localTasks = dataBase.retrieveAll();

		for (int index = 0; index < localTasks.size(); ++index) {
			Task localTask = localTasks.get(index);
			Task googleTask = googleSchedule.retrieveTask(localTask);
			
			//Task already exists in google schedule
			if (googleTask != null) { 
				//check for differences and take the one the latest modified date
				this.syncTask(localTask, googleTask);
			}
		}
	}

	private void syncAdditionalTasksFromGoogleSchedule() throws Exception {
		ArrayList<Task> googleTasks = googleSchedule.retrieveAllTasks();
		for (Task googleTask : googleTasks) {

			if (!this.hasTask(googleTask)) {
				if (googleTask.getDateModified().isAfter(lastSyncDate)) {
					googleTask.updateDateModified();
					try {
						dataBase.addTask(googleTask);
					} catch (Exception e) {
						throw new Exception("Failed to add task into local schedule");
					}
				}
				//task was added but later deleted. Delete from gcal.
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
			googleTask.setDateModified(localTask.getDateModified());
			try {
				dataBase.updateTask(googleTask);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (googleTask.getDateModified().isBefore(
				localTask.getDateModified())) {
			googleSchedule.updateTask(localTask);
		}
	}

	private boolean hasTask(Task googleTask) {
		ArrayList<Task> allTasksInDb = dataBase.retrieveAll();

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
	
	private void initializeLastSyncDate() {
		if (dataBase.getProperty(PROPERTY_NAME_LAST_SYNC) != null) {
			lastSyncDate = new DateTime(dataBase.getProperty(PROPERTY_NAME_LAST_SYNC));
		} else {
			lastSyncDate = new DateTime().minusYears(20);
		}
	}
	
	private void updateLastSyncDate() {
		lastSyncDate = new DateTime();
		dataBase.setProperty(PROPERTY_NAME_LAST_SYNC, lastSyncDate.toString());
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
			view.disableInput();
			try {
			publish(MESSAGE_SYNC_FROM_LOCAL);
			syncController.syncAdditionalTasksFromLocalSchedule();
			
			publish(MESSAGE_SYNC_EXISTING);
			syncController.syncTasksThatExistInBothSchedule();
			
			publish(MESSAGE_SYNC_FROM_GOOGLE);
			syncController.syncAdditionalTasksFromGoogleSchedule();
			
			syncController.updateLastSyncDate();
			publish(String.format(MESSAGE_SYNC, new DateTime().toString(DATE_FORMAT_FOR_LAST_SYNC)));
			} catch (Exception e) {
				publish("Sync is unsuccessful, please try again");
			}
			view.enableInput();
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
}
