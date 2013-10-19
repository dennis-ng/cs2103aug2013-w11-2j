package typetodo.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import org.joda.time.DateTime;

import typetodo.db.DbHandler;
import typetodo.model.DeadlineTask;
import typetodo.model.FieldName;
import typetodo.model.Task;
import typetodo.model.TimedTask;
import typetodo.model.Task.Status;
import typetodo.ui.View;

public class ScheduleController {
	private static final String WELCOME_MESSAGE = "Welcome to TypeToDo.\n";
	private static final String MESSAGE_ADDED = "\"%s\" has been added to your schedule";
	private static final String MESSAGE_DELETED = "\"%s\" has been deleted from your schedule";
	private static final String MESSAGE_SEARCH = "%d Tasks have been found";
	private static final String MESSAGE_EDITED = "Edit successful";
	private static final String MESSAGE_MARK = "";
	private static final String MESSAGE_SYNC = "Sync successful";
	private static final String MESSAGE_VIEW = "Showing all tasks of %s";

	Stack<Command> historyOfCommands;
	ArrayList<Task> tasksToBeDisplayed;
	View view;
	DbHandler db;
	CommandParser commandParser;
	String userInput;

	private static enum Mode {
		DATE, KEYWORD, STATUS;
	}

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

	public ScheduleController(View view) throws IOException {
		historyOfCommands = new Stack<Command>();
		db = DbHandler.getInstance(); //model

		ViewMode.setDate(new DateTime());
		ViewMode.setMode(Mode.DATE);

		this.refreshView(); 
		this.view = view; //view
		view.displayFeedBack("Welcome to TypeToDo");
		view.displayTasks(tasksToBeDisplayed);
	}

	public void listenForCommands() {
		Command command;
	
		while(true) {
			commandParser = new CommandParser(this);
			command = commandParser.parse(view.getUserInput());
			System.out.println(command);
			try {
					command.execute();
				if (command instanceof Undoable) {
					historyOfCommands.add(command);
				}
			} catch (Exception e) {
				//TODO;
				view.displayErrorMessage(e.getMessage());
			}
			this.refreshView();
			view.displayTasks(tasksToBeDisplayed);
		}
	}
	
	public int addTask(Task task) throws Exception{
		return db.addTask(task);
	}
	
	public Task deleteTaskByIndex(int index) {
		Task taskToBeDeleted = this.tasksToBeDisplayed.get(index - 1);
		db.deleteTask(taskToBeDeleted.getTaskId());
		
		return taskToBeDeleted;
	}
	
	public Task deleteTaskByKeyword (String keyword) throws Exception {
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
			view.displayFeedBack("Please specify the index of the tasks that you wish to delete");
			view.displayTasks(tasks);
			
			int index;
			do {
				index = view.getIndex();
				view.displayErrorMessage("index doesnt not exist");
			} while ((index > 0) && (index <= tasks.size()+1));
			
			taskToBeDeleted = tasks.get(index - 1);
			db.deleteTask(taskToBeDeleted.getTaskId());
		}
		else if (tasks.size() == 1) { //Only one match
			taskToBeDeleted = tasks.get(0);
			db.deleteTask(taskToBeDeleted.getTaskId());
			view.displayFeedBack("Task have been deleted");
		}
		
		return taskToBeDeleted;
	}

	public void deleteTaskById(int taskId) {
		db.deleteTask(taskId);
	}
	
	public void editTask(Task task) throws Exception {
		db.updateTask(task);
	}
	
	public Task editTask(int index, FieldName fieldName, String newString) throws Exception{
		Task taskToBeEdited = tasksToBeDisplayed.get(index - 1);
		Task taskBeforeEdit = taskToBeEdited.makeCopy();
		
		switch (fieldName) {
			case TITLE :
				taskToBeEdited.setTitle(newString);
				break;
			case DESCRIPTION :
				taskToBeEdited.setDescription(newString);
				break;
			default :
				//TODO: throw exception
		}
		
		taskToBeEdited.setDateModified(new DateTime());
		db.updateTask(taskToBeEdited);
		view.displayFeedBack(MESSAGE_EDITED);
		
		return taskBeforeEdit;
	}
	
	public Task editTask(int index, FieldName fieldName, DateTime newDateTime) throws Exception{
		Task taskToBeEdited = tasksToBeDisplayed.get(index - 1);
		Task taskBeforeEdit = taskToBeEdited.makeCopy();
		
		switch (fieldName) {
			case DEADLINE :
				if (taskToBeEdited instanceof DeadlineTask) {
					((DeadlineTask) taskToBeEdited).setDeadline(newDateTime);
				}
				else {
					//TODO: throw exception
				}
				break;		
			case START :
				if (taskToBeEdited instanceof TimedTask) {
					((TimedTask) taskToBeEdited).setStart(newDateTime);
				}
				else {
					//TODO: throw exception for illegal field
				}
				break;	
			case END :
				if (taskToBeEdited instanceof TimedTask) {
					((TimedTask) taskToBeEdited).setEnd(newDateTime);
				}
				else {
					//TODO: throw exception for illegal field
				}
				break;
			default :
				//TODO: throw exception
		}
		
		taskToBeEdited.setDateModified(new DateTime());
		db.updateTask(taskToBeEdited);
		view.displayFeedBack(MESSAGE_EDITED);
		
		return taskBeforeEdit;
	}
	
	public Task editTask(int index, FieldName fieldName, boolean newBoolean) throws Exception{
		Task taskToBeEdited = tasksToBeDisplayed.get(index - 1);
		Task taskBeforeEdit = taskToBeEdited.makeCopy();
		
		if (taskToBeEdited instanceof TimedTask) {
			((TimedTask) taskToBeEdited).setBusy(newBoolean);
		}
		
		taskToBeEdited.setDateModified(new DateTime());
		db.updateTask(taskToBeEdited);
		view.displayFeedBack(MESSAGE_EDITED);
		
		return taskBeforeEdit;
	}
	
	public void search(String keyword) {
		this.tasksToBeDisplayed = db.retrieveContaining(keyword);
		view.displayFeedBack("Search Results: ");
	}
	
	public void undo() {
		if(!historyOfCommands.isEmpty()) {
			try {
				((Undoable)this.historyOfCommands.pop()).undo();
				view.displayFeedBack("UNDO SUCCESS");
			}
			catch (Exception e) {
				view.displayErrorMessage("undo failed");
			}
		}
		else {
			view.displayErrorMessage("Nothing to undo");
		}
	}
	
	
	private void refreshView() {
		Mode mode = ViewMode.getMode();

		switch (mode) {
		case DATE:
			tasksToBeDisplayed = db.retrieveList(ViewMode.getDate());
			break;

		case KEYWORD:
			tasksToBeDisplayed = db.retrieveContaining(ViewMode.getKeyword());
			break;

		case STATUS:
			tasksToBeDisplayed = db.retrieveContaining(ViewMode.getStatus().toString());
			break;
		}
	}
}
