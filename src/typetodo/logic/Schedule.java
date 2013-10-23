package typetodo.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import org.joda.time.DateTime;

import typetodo.db.DbHandler;
import typetodo.model.DeadlineTask;
import typetodo.model.FieldName;
import typetodo.model.FloatingTask;
import typetodo.model.Task;
import typetodo.model.Task.Status;
import typetodo.model.TimedTask;
import typetodo.model.TypeOfOperation;
import typetodo.model.View;
import typetodo.sync.SyncHandler;

/**
 * The Schedule class.
 * 
 * @author Phan Shi Yu
 * 
 */
public class Schedule {
	private static final String WELCOME_MESSAGE = "Welcome to TypeToDo.\n";
	private static final String MESSAGE_ADDED = "\"%s\" has been added to your schedule";
	private static final String MESSAGE_DELETED = "\"%s\" has been deleted from your schedule";
	private static final String MESSAGE_SEARCH = "%d Tasks have been found";
	private static final String MESSAGE_EDITED = "Edit successful";
	private static final String MESSAGE_MARK = "";
	private static final String MESSAGE_SYNC = "Sync successful";
	private static final String MESSAGE_VIEW = "Showing all tasks of %s";

	private static enum Mode {
		DATE, KEYWORD, STATUS;
	}

	/**
	 * The ViewMode class is used to store values used to construct the current
	 * View Object.
	 * 
	 * @author Phan Shi YU
	 * 
	 */
	private static class ViewMode {
		private static Mode mode;
		private static DateTime date;
		private static String keyword;
		private static Status status;
		private static boolean isTemp;

		private static Mode getMode() {
			return mode;
		}

		private static void setMode(Mode mode) {
			ViewMode.mode = mode;
		}

		private static DateTime getDate() {
			return date;
		}

		private static void setDate(DateTime date) {
			ViewMode.date = date;
		}

		private static String getKeyword() {
			return keyword;
		}

		private static void setKeyword(String keyword) {
			ViewMode.keyword = keyword;
		}

		private static Status getStatus() {
			return status;
		}

		private static void setStatus(Status status) {
			ViewMode.status = status;
		}

		/**
		 * @return the isTemp
		 */
		public static boolean isTemp() {
			return isTemp;
		}

		/**
		 * @param isTemp
		 *          the isTemp to set
		 */
		public static void setTemp(boolean isTemp) {
			ViewMode.isTemp = isTemp;
		}
	}

	/**
	 * The Operation class is used to store the necessary information of a used
	 * operation such that it could be undone.
	 * 
	 * @author Phan Shi Yu
	 * 
	 */
	private class Operation {
		private final TypeOfOperation type;
		private final Task taskAffected;

		public Operation(TypeOfOperation type, Task taskAffected) {
			this.type = type;
			this.taskAffected = taskAffected;
		}

		/**
		 * Executes the reverse of the operation to mimic an undo.
		 */
		public void undo() {
			switch (type) {
				case ADD:
					taskAffected.setDateModified(new DateTime());
					db.deleteTask(taskAffected.getTaskId());
					break;

				case DELETE:
					try {
						if (taskAffected instanceof TimedTask) {
							db.addTask(taskAffected);
						} else if (taskAffected instanceof DeadlineTask) {
							db.addTask(taskAffected);
						} else if (taskAffected instanceof FloatingTask) {
							db.addTask(taskAffected);
						}
					} catch (Exception e) {
						// handle possible exception
					}
					break;

				case EDIT:
					try {
						taskAffected.setDateModified(new DateTime());
						db.updateTask(taskAffected);
					} catch (Exception e) {
						// Handle possible exception
					}
					break;
			}

			setFeedBack("Undo is successful");
			refreshCurrentView();
		}

	}

	private String feedBack;
	private final DbHandler db;
	private View currentView;
	private final Stack<Operation> historyOfOperations;

	public Schedule() throws Exception {
		this.feedBack = WELCOME_MESSAGE;
		this.db = DbHandler.getInstance();
		this.setViewMode(new DateTime()); // setting to today's date
		this.refreshCurrentView();
		this.historyOfOperations = new Stack<Operation>();
	}

	/**
	 * Adds a floating task into the schedule.
	 * 
	 * @param title
	 *          Title of floating task.
	 * @param description
	 *          Description of floating task
	 * @return Returns true if add was successful and false if unsuccessful.
	 */
	public boolean addTask(String title, String description) {
		FloatingTask taskToBeAdded = new FloatingTask(title, description);
		int taskId;

		try {
			taskId = db.addTask(taskToBeAdded);

			taskToBeAdded.setTaskId(taskId);

			this.historyOfOperations.push(new Operation(TypeOfOperation.ADD,
					taskToBeAdded));

			setFeedBack(String.format(MESSAGE_ADDED, title));
			this.refreshCurrentView();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Adds a timed task into the schedule.
	 * 
	 * @param title
	 *          Title of timed task.
	 * @param description
	 *          Description of timed task.
	 * @param start
	 *          Starting date and time of timed task.
	 * @param end
	 *          Ending date and time of timed task.
	 * @param isBusy
	 *          Boolean value to flag if given period(start to end) is a busy one.
	 *          i.e no other tasks can be scheduled concurrently.
	 * @return Returns true if add was successful and false if unsuccessful.
	 */
	public boolean addTask(String title, String description, DateTime start,
			DateTime end, boolean isBusy) {
		TimedTask taskToBeAdded = new TimedTask(title, description, start, end,
				isBusy);
		int taskId;

		try {
			taskId = db.addTask(taskToBeAdded);
			taskToBeAdded.setTaskId(taskId);

			this.historyOfOperations.push(new Operation(TypeOfOperation.ADD,
					taskToBeAdded));

			setFeedBack(String.format(MESSAGE_ADDED, title));
			this.refreshCurrentView();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Adds a deadline task into the schedule.
	 * 
	 * @param title
	 *          Title of deadline task.
	 * @param description
	 *          Description of deadline task.
	 * @param deadline
	 *          Deadline of deadline task.
	 * @return Returns true if add was successful and false if unsuccessful.
	 */
	public boolean addTask(String title, String description, DateTime deadline) {
		DeadlineTask taskToBeAdded = new DeadlineTask(title, description, deadline);
		int taskId;

		try {
			taskId = db.addTask(taskToBeAdded);
			taskToBeAdded.setTaskId(taskId);

			this.historyOfOperations.push(new Operation(TypeOfOperation.ADD,
					taskToBeAdded));

			setFeedBack(String.format(MESSAGE_ADDED, title));
			this.refreshCurrentView();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Deletes a task in current view.
	 * 
	 * @param index
	 *          Index of task as displayed in current view.
	 * @return returns true if task is successfully deleted.
	 */
	public boolean deleteTask(int index) {
		// TODO: must throw out of bound exception!
		Task taskToBeDeleted = currentView.getTasks().get(index - 1);

		if (db.deleteTask(taskToBeDeleted.getTaskId())) {
			this.historyOfOperations.push(new Operation(TypeOfOperation.DELETE,
					taskToBeDeleted));
			setFeedBack(String.format(MESSAGE_DELETED, taskToBeDeleted.getTitle()));
			this.refreshCurrentView();
		} else {
			this.setFeedBack("DELETE FAILED");
			return false;
		}

		return true;
	}

	/**
	 * Deletes a task in database with a given keyword. If there are more than one
	 * task that matches the keyword, a View containing the tasks that matched
	 * would be generated.
	 * 
	 * @param keyword
	 * @return
	 */
	public boolean deleteTask(String keyword) {
		ArrayList<Task> tasks = db.retrieveContaining(keyword);
		Task taskToBeDeleted = null;

		if (tasks == null) {
			/*
			 * there are no tasks in the system that matches the keyword, generate
			 * feedback.
			 */
			this.setFeedBack("There are no tasks that matches your given keyword!");
			this.refreshCurrentView();
			return true;
		} else if (tasks.size() > 1) { // means there is more than one task that
																		// matches the keyword
			/*
			 * show user a temp view which contains the tasks that matches the keyword
			 * so that he can specify which tasks he wants to delete
			 */
			this.setFeedBack("Please specify the index of the tasks that you wish to delete");
			// TODO
			this.currentView = generateView(tasks); // return view of results
			return true;
		} else if (tasks.size() == 1) {
			/*
			 * there is only one task that matches, delete that task and update view
			 */
			taskToBeDeleted = tasks.get(0);
			this.historyOfOperations.push(new Operation(TypeOfOperation.DELETE,
					taskToBeDeleted)); // add operation to history for possible undo
			db.deleteTask(taskToBeDeleted.getTaskId()); // delete task from database

			setFeedBack(String.format(MESSAGE_DELETED, taskToBeDeleted.getTitle())); // set
																																								// feedback
			this.refreshCurrentView(); // generate current view base on view mode and
																	// feedback
			return true;
		}

		return false;
	}

	/**
	 * returns a View object of the current View Mode
	 * 
	 * @return returns a View object of the current View Mode
	 */
	private View generateView() {
		Mode mode = ViewMode.getMode();
		ArrayList<Task> tasks = null;
		View view = null;

		switch (mode) {
			case DATE:
				tasks = db.retrieveList(ViewMode.getDate());
				view = new View(getFeedBack(), tasks);
				break;

			case KEYWORD:
				tasks = db.retrieveContaining(ViewMode.getKeyword());
				view = new View(getFeedBack(), tasks);
				break;

			case STATUS:
				tasks = db.retrieveContaining(ViewMode.getStatus().toString());
				view = new View(getFeedBack(), tasks);
				break;
		}

		return view;
	}

	private View generateView(ArrayList<Task> tasks) {
		return (new View(getFeedBack(), tasks));
	}

	/**
	 * Searches the schedule for tasks that contains a given keyword
	 * 
	 * @param keyword
	 * @return returns a view object that displays the search results
	 */
	public void search(String keyword) {
		int numberOfResults;

		setViewMode(keyword);
		numberOfResults = this.currentView.getTasks().size();
		setFeedBack(String.format(MESSAGE_SEARCH, numberOfResults));
		this.refreshCurrentView();
	}

	/**
	 * Sets the current view mode into date mode
	 * 
	 * @param date
	 */
	public void setViewMode(DateTime date) {
		ViewMode.setMode(Mode.DATE);
		ViewMode.setDate(date);
		setFeedBack(String.format(MESSAGE_VIEW, date));
		this.refreshCurrentView();
	}

	/**
	 * Sets the current view mode into keyword mode.
	 * 
	 * @param keyword
	 */
	public void setViewMode(String keyword) {
		ViewMode.setMode(Mode.KEYWORD);
		ViewMode.setKeyword(keyword);
		this.refreshCurrentView();
	}

	/**
	 * Sets the current view mode into status mode, e.g view all COMPLETED,
	 * DELETED etc.
	 * 
	 * @param status
	 */
	public void setViewMode(Status status) {
		ViewMode.setMode(Mode.STATUS);
		ViewMode.setStatus(status);
		refreshCurrentView();
	}

	/**
	 * Edits and updates the title/description of an existing task.
	 * 
	 * @param index
	 *          Index of task as displayed in current view.
	 * @param fieldName
	 *          Name of field to be edited. Eg. TITLE, DESCRIPTION
	 * @param newValue
	 *          New value to replace the existing value.
	 * @return Returns the current View object.
	 */
	public boolean editTask(int index, FieldName fieldName, String newValue) {
		Task taskToBeEdited = currentView.getTasks().get(index - 1);
		Task taskBeforeEdit = taskToBeEdited.makeCopy();

		switch (fieldName) {
			case TITLE:
				taskToBeEdited.setTitle(newValue);
				break;

			case DESCRIPTION:
				taskToBeEdited.setDescription(newValue);
				break;

			default:
				break;
		}

		try {
			taskToBeEdited.setDateModified(new DateTime());
			db.updateTask(taskToBeEdited);
			this.historyOfOperations.push(new Operation(TypeOfOperation.EDIT,
					taskBeforeEdit));
		} catch (Exception e) {
			// TODO
			return false;
		}

		this.setFeedBack(MESSAGE_EDITED);
		this.refreshCurrentView();
		return true;
	}

	/**
	 * Edits the START/END/DEADLINE field
	 * 
	 * @param Index
	 *          of task as displayed in current view.
	 * @param fieldName
	 *          Name of field that is to be changed
	 * @param newValue
	 *          to replace old value in desired field
	 * @return returns a View object of the current View Mode
	 */
	public boolean editTask(int index, FieldName fieldName, DateTime newValue) {
		Task taskToBeEdited = currentView.getTasks().get(index - 1);
		Task taskBeforeEdit = taskToBeEdited.makeCopy();

		switch (fieldName) {
			case START:
				if (taskToBeEdited instanceof TimedTask) {
					((TimedTask) taskToBeEdited).setStart(newValue);
				}
				break;

			case END:
				if (taskToBeEdited instanceof TimedTask) {
					((TimedTask) taskToBeEdited).setEnd(newValue);
				}
				break;

			case DEADLINE:
				if (taskToBeEdited instanceof DeadlineTask) {
					((DeadlineTask) taskToBeEdited).setDeadline(newValue);
				}
				break;
			default:
				break;
		}

		try {
			taskToBeEdited.setDateModified(new DateTime());
			db.updateTask(taskToBeEdited);
			this.historyOfOperations.push(new Operation(TypeOfOperation.EDIT,
					taskBeforeEdit));
		} catch (Exception e) {
			// TODO
			return false;
		}

		this.setFeedBack(MESSAGE_EDITED);
		this.refreshCurrentView();
		return true;
	}

	/**
	 * Edits ISBUSY field
	 * 
	 * @param Index
	 *          of task as displayed in current view.
	 * @param fieldName
	 *          Name of field that is to be changed
	 * @param isBusy
	 *          Boolean value to replace the isBusy field
	 * @return returns a View object of the current View Mode
	 */
	public boolean editTask(int index, FieldName fieldName, boolean isBusy) {
		Task taskToBeEdited = currentView.getTasks().get(index - 1);
		Task taskBeforeEdit = taskToBeEdited.makeCopy();

		if (taskToBeEdited instanceof TimedTask) {
			((TimedTask) taskToBeEdited).setBusy(isBusy);
		}

		try {
			taskToBeEdited.setDateModified(new DateTime());
			db.updateTask(taskToBeEdited);
			this.historyOfOperations.push(new Operation(TypeOfOperation.EDIT,
					taskBeforeEdit));
		} catch (Exception e) {
			// TODO
			return false;
		}

		this.setFeedBack(MESSAGE_EDITED);
		this.refreshCurrentView();
		return true;
	}

	/**
	 * marks a certain task as completed
	 * 
	 * @param Index
	 *          of task as displayed in current view.
	 * @return returns a View object of the current View Mode
	 */
	public boolean markTaskAsCompleted(int index) {
		Task taskToMark = currentView.getTasks().get(index - 1);

		taskToMark.setStatus(Status.COMPLETED);

		try {
			taskToMark.setDateModified(new DateTime());
			db.updateTask(taskToMark);
			this.historyOfOperations.push(new Operation(TypeOfOperation.EDIT,
					taskToMark));
		} catch (Exception e) {
			// TODO
			return false;
		}

		this.setFeedBack(MESSAGE_MARK);
		this.refreshCurrentView();

		return true;
	}

	/**
	 * Undo the last operation
	 * 
	 * @return returns a View object of the current View Mode
	 */
	public View undoLastOperation() {
		this.historyOfOperations.pop().undo();
		return currentView;
	}

	public View sync() {
		try {
			SyncHandler sh = new SyncHandler();
			sh.syncToGoogleCalendar();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.setFeedBack(MESSAGE_SYNC);
		this.refreshCurrentView();
		return currentView;
	}

	private String getFeedBack() {
		return this.feedBack;
	}

	private void setFeedBack(String feedBack) {
		this.feedBack = feedBack;
	}

	public View getCurrentView() {
		return this.currentView;
	}

	private void refreshCurrentView() {
		this.currentView = generateView();
	}
}
