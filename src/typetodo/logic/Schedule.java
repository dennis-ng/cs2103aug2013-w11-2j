package typetodo.logic;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import typetodo.db.DbHandler;
import typetodo.model.DeadlineTask;
import typetodo.model.FieldName;
import typetodo.model.Task;
import typetodo.model.Task.Status;
import typetodo.model.TimedTask;

public class Schedule {
	private ArrayList<Task> currentListOfTasks;
	
	public ArrayList<Task> getCurrentListOfTasks() {
		this.refreshView();
		return currentListOfTasks;
	}

	private DbHandler db;

	private static enum Mode {
		DATE, KEYWORD, STATUS;
	}
	
	private static class ViewMode {
		private static Mode mode;
		private static DateTime date;
		private static String keyword;
		private static Status status;

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
	}

	public Schedule() throws IOException {
		db = DbHandler.getInstance(); // model

		ViewMode.setDate(new DateTime());
		ViewMode.setMode(Mode.DATE);
	}

	public int addTask(Task task) throws Exception {
		int taskId = db.addTask(task);
		
		return taskId;
	}

	public Task deleteTaskByIndex(int index) {
		Task taskToBeDeleted = null;
		
		try {
			taskToBeDeleted = this.currentListOfTasks.get(index - 1);
		} catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("Given index is not in range");
		}
		
		db.deleteTask(taskToBeDeleted.getTaskId());
		
		return taskToBeDeleted;
	}

	public Task deleteTaskByKeyword(String keyword) throws Exception {
		Task taskToBeDeleted = null;

		ArrayList<Task> tasks = db.retrieveContaining(keyword);
		if (tasks == null) {
			/*
			 * there are no tasks in the system that matches the keyword, generate
			 * feedback.
			 */
			throw new Exception("There are no tasks that matches your given keyword!");
		} else if (tasks.size() > 1) { // means there is more than one task that
			// matches the keyword
			/*
			 * show user a temp view which contains the tasks that matches the keyword
			 * so that he can specify which tasks he wants to delete
			 */

			throw new Exception("Please sepcify the index of the tasks that you wish to delete");
		} else if (tasks.size() == 1) { // Only one match
			taskToBeDeleted = tasks.get(0);
			db.deleteTask(taskToBeDeleted.getTaskId());
		}

		return taskToBeDeleted;
	}

	public void deleteTaskById(int taskId) {
		db.deleteTask(taskId);
	}

	public void editTask(Task task) throws Exception {
		db.updateTask(task);
	}

	public Task editTask(int index, FieldName fieldName, String newString)
			throws Exception {
		Task taskToBeEdited = currentListOfTasks.get(index - 1);
		Task taskBeforeEdit = taskToBeEdited.makeCopy();

		switch (fieldName) {
		case TITLE :
			taskToBeEdited.setTitle(newString);
			break;
		case DESCRIPTION :
			taskToBeEdited.setDescription(newString);
			break;
		default:
			// TODO: throw exception
		}

		taskToBeEdited.setDateModified(new DateTime());
		db.updateTask(taskToBeEdited);

		return taskBeforeEdit;
	}

	public Task editTask(int index, FieldName fieldName, DateTime newDateTime)
			throws Exception {
		Task taskToBeEdited = currentListOfTasks.get(index - 1);
		Task taskBeforeEdit = taskToBeEdited.makeCopy();

		switch (fieldName) {
		case DEADLINE :
			if (taskToBeEdited instanceof DeadlineTask) {
				((DeadlineTask) taskToBeEdited).setDeadline(newDateTime);
			} else {
				// TODO: throw exception
			}
			break;
		case START :
			if (taskToBeEdited instanceof TimedTask) {
				((TimedTask) taskToBeEdited).setStart(newDateTime);
			} else {
				// TODO: throw exception for illegal field
			}
			break;
		case END :
			if (taskToBeEdited instanceof TimedTask) {
				((TimedTask) taskToBeEdited).setEnd(newDateTime);
			} else {
				// TODO: throw exception for illegal field
			}
			break;
		default :
			// TODO: throw exception
		}

		taskToBeEdited.setDateModified(new DateTime());
		db.updateTask(taskToBeEdited);

		return taskBeforeEdit;
	}

	public Task editTask(int index, FieldName fieldName, boolean newBoolean)
			throws Exception {
		Task taskToBeEdited = currentListOfTasks.get(index - 1);
		Task taskBeforeEdit = taskToBeEdited.makeCopy();

		if (taskToBeEdited instanceof TimedTask) {
			((TimedTask) taskToBeEdited).setBusy(newBoolean);
		}

		taskToBeEdited.setDateModified(new DateTime());
		db.updateTask(taskToBeEdited);

		return taskBeforeEdit;
	}

	public void search(String keyword) {
		this.setViewMode(keyword);
	}

	private void setViewMode(String keyword) {
		ViewMode.setMode(Mode.KEYWORD);
		ViewMode.setKeyword(keyword);
	}

	private void setViewMode(DateTime dateTime) {
		ViewMode.setMode(Mode.DATE);
		ViewMode.setDate(dateTime);
	}

	public void viewTasksofToday() {
		this.setViewMode(new DateTime());
	}

	public void help() {
	}

	private void refreshView() {
		switch (ViewMode.getMode()) {
		case DATE :
			currentListOfTasks = db.retrieveList(ViewMode.getDate());
			break;

		case KEYWORD :
			currentListOfTasks = db.retrieveContaining(ViewMode.getKeyword());
			break;

		case STATUS :
			currentListOfTasks = db.retrieveContaining(ViewMode.getStatus()
					.toString());
			break;
		}
	}
}
