package typetodo.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import typetodo.db.DBHandler;
import typetodo.logic.Task.Status;

public class Schedule {
	private static final String WELCOME_MESSAGE = "Welcome to TypeToDo.\n";
	private static final String MESSAGE_ADDED = "%s has been added to your schedule";
	private static final String MESSAGE_DELETED = "%s has been deleted from your schedule";
	private static final String MESSAGE_SEARCH = "%d Tasks have been found";
	private static final String MESSAGE_EDITED = "";
	private static final String MESSAGE_MARK = "";
	
	public static enum FieldName {
		NAME, DESCRIPTION, START, END, DEADLINE, BUSYFIELD;
	}
	
	public static enum TypeOfOperation {
		ADD, DELETE, EDIT;
	}
	
	private static class ViewMode {
		private static String mode;
		private static Date date;
		private static String keyword;
		private static Status status;
		
		private static String getMode() {
			return mode;
		}
		
		private static void setMode(String mode) {
			ViewMode.mode = mode;
		}
		
		private static Date getDate() {
			return date;
		}
		
		private static void setDate(Date date) {
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
	}
	
	private class Operation {
		private final TypeOfOperation type;
		private final Task taskAffected;
		
		public Operation(TypeOfOperation type, Task taskAffected) {
			this.type = type;
			this.taskAffected = taskAffected;
		}
		
		public void undo() {
			switch (type) {
				case ADD :
					db.deleteTask(taskAffected);
					break;
				
				case DELETE :
					try {
						if (taskAffected instanceof TimedTask) {
							db.addTask((TimedTask) taskAffected);
						}
						else if (taskAffected instanceof DeadlineTask) {
							db.addTask((DeadlineTask) taskAffected);
						}
					else if (taskAffected instanceof FloatingTask) {
							db.addTask((FloatingTask) taskAffected);
						}
					} catch (Exception e) {
						//handle possible exception
					}
					break;
					
				case EDIT :
					try {
						db.updateTask(taskAffected);
					} catch (Exception e) {
						//Handle possible exception
					}
					break;
			}
			
			setFeedBack("Undo is successful");
			currentView = generateView();
		}
	
	}
	
	private String feedBack;
	private DBHandler db;
	private View currentView;
	private Stack<Operation> historyOfOperations;
	
	public Schedule() throws Exception {
		this.feedBack = WELCOME_MESSAGE;
		this.db = new DBHandler();
		this.setViewMode(new Date());	//setting to today's date
		this.currentView = generateView();
		this.historyOfOperations = new Stack<Operation>();
	}

	/**
	 * Adds a floating task into the schedule
	 * @param name
	 * @param description
	 * @return returns a View object of the current View Mode
	 */
	public View addTask(String name, String description) {
		FloatingTask taskToBeAdded = new FloatingTask(name, description);
		
		try {
			db.addTask(taskToBeAdded);
			this.historyOfOperations.push(new Operation(TypeOfOperation.ADD, taskToBeAdded));
		} catch (Exception e) {
			//Handle
		}
		
		setFeedBack(String.format(MESSAGE_ADDED, name));
		this.currentView = generateView();
			
		return currentView;
	}
	
	/**
	 * Adds a timed task into the schedule
	 * @param name
	 * @param description
	 * @param start
	 * @param end
	 * @param isBusy
	 * @return returns a View object of the current View Mode
	 */
	public View addTask(String name, String description, Date start, Date end, boolean isBusy) {
		TimedTask taskToBeAdded = new TimedTask(name, description, start, end, isBusy);
		
		try {
			db.addTask(taskToBeAdded);
			this.historyOfOperations.push(new Operation(TypeOfOperation.ADD, taskToBeAdded));
		} catch (Exception e) {
			//Handle
		}
		
		setFeedBack(String.format(MESSAGE_ADDED, name));
		this.currentView = generateView();
		return currentView;
	}
	
	/**
	 * Adds a deadline task into the schedule
	 * @param name
	 * @param description
	 * @param deadline
	 * @return returns a View object of the current View Mode
	 */
	public View addTask(String name, String description, Date deadline) {
		DeadlineTask taskToBeAdded = new DeadlineTask(name, description, deadline);
		
		try {
			db.addTask(taskToBeAdded);
			this.historyOfOperations.push(new Operation(TypeOfOperation.ADD, taskToBeAdded));
		} catch (Exception e) {
			//Handle
		}
		
		setFeedBack(String.format(MESSAGE_ADDED, name));
		this.currentView = generateView();
		return currentView;
	}
	
	/**
	 * Deletes a task base on its index of the current view
	 * @param index
	 * @return returns a View object of the current View Mode
	 */
	public View deleteTask(int index) {
		Task taskToBeDeleted = currentView.getTasks().get(index-1);
		
		if(db.deleteTask(taskToBeDeleted)) {
			this.historyOfOperations.push(new Operation(TypeOfOperation.DELETE, taskToBeDeleted));
			this.setFeedBack(MESSAGE_DELETED);
			this.currentView = generateView();
		}
		
		return currentView;
	}
	
	/**
	 * Deletes a task base on its name shown in the current view
	 * @param taskName
	 * @return returns a View object of the current View Mode
	 */
	public View deleteTask(String taskName) {
		ArrayList<Task> tasks = currentView.getTasks();
		
		//what if there is a task with same name//
		for (Task task : tasks) {
			if (task.getName().equals(taskName)) {
				db.deleteTask(task);
				this.historyOfOperations.push(new Operation(TypeOfOperation.DELETE, task));
				this.currentView = generateView();
				break;
			}
		}
		
		return currentView;
	}
	
	/**
	 * returns a View object of the current View Mode
	 * @return returns a View object of the current View Mode
	 */
	public View generateView() {
		String mode = ViewMode.getMode();
		ArrayList<Task> tasks = null;
		View view = null;
		
		switch (mode) {
			case "date" :
				tasks = db.retrieveList(ViewMode.getDate());
				view = new View(getFeedBack(), tasks);
				break;
			
			case "keyword" :
				tasks = db.retrieveContaining(ViewMode.getKeyword());
				view = new View(getFeedBack(), tasks);
				break;
				
			case "status" :
				tasks = db.retrieveContaining(ViewMode.getStatus().toString());
				view = new View(getFeedBack(), tasks);
				break;
		}
		
		return view;
	}

	/**
	 * Searches the schedule for tasks that contains a given keyword
	 * @param keyword
	 * @return returns a view object that displays the search results
	 */
	public View search(String keyword) {
		int numberOfResults;
		
		this.currentView = setViewMode(keyword);
		numberOfResults = this.currentView.getTasks().size();
		setFeedBack(String.format(MESSAGE_SEARCH, numberOfResults));
		return currentView;
	}
	
	/**
	 * Sets the current view mode into date mode
	 * @param date
	 * @return returns a View object of the new View Mode
	 */
	public View setViewMode(Date date) {
		ViewMode.setMode("date");
		ViewMode.setDate(date);
		return generateView();
	}
	
	/**
	 * Sets the current view mode into keyword mode.
	 * @param keyword
	 * @return returns a View object of the new View Mode
	 */
	public View setViewMode(String keyword) {
		ViewMode.setMode("keyword");
		ViewMode.setKeyword(keyword);
		return generateView();
	}
	
	/**
	 * Sets the current view mode into status mode, e.g view all COMPLETED, DELETED etc.
	 * @param status
	 * @return
	 */
	public View setViewMode(Status status) {
		ViewMode.setMode("statusout");
		ViewMode.setStatus(status);
		return generateView();
	}
	
	/**
	 * Edits the NAME/DESCRIPTION field
	 * @param index
	 * @param fieldName
	 * @param newValue
	 * @return returns a View object of the current View Mode
	 */
	public View editTask(int index, FieldName fieldName, String newValue) {
		Task taskToBeEdited = currentView.getTasks().get(index-1);
		
		switch (fieldName) {
			case NAME :
				taskToBeEdited.setName((String) newValue);
				break;
			
			case DESCRIPTION :
				taskToBeEdited.setDescription((String) newValue);
				break;

			default :
				break;
		}
		
		try {
			db.updateTask(taskToBeEdited);
			this.historyOfOperations.push(new Operation(TypeOfOperation.EDIT, taskToBeEdited));
		} catch (Exception e) {
			//Handle
		}
		
		this.setFeedBack(MESSAGE_EDITED);
		this.currentView = generateView();
		return this.currentView;
	}

	/**
	 * Edits the START/END/DEADLINE field
	 * @param index
	 * @param fieldName
	 * @param newValue
	 * @return returns a View object of the current View Mode
	 */
	public View editTask(int index, FieldName fieldName, Date newValue) {
		Task taskToBeEdited = currentView.getTasks().get(index-1);
		
		switch (fieldName) {
			case START :
				if (taskToBeEdited instanceof TimedTask) {
					((TimedTask) taskToBeEdited).setStart(newValue);
				}
				break;
			
			case END :
				if (taskToBeEdited instanceof TimedTask) {
					((TimedTask) taskToBeEdited).setEnd(newValue);
				}
				break;

			case DEADLINE :
				if (taskToBeEdited instanceof DeadlineTask) {
					((DeadlineTask) taskToBeEdited).setDeadline(newValue);
				}
				
			default :
				break;
		}
		
		try {
			db.updateTask(taskToBeEdited);
			this.historyOfOperations.push(new Operation(TypeOfOperation.EDIT, taskToBeEdited));
		} catch (Exception e) {
			//Handle
		}
		
		this.setFeedBack(MESSAGE_EDITED);
		this.currentView = generateView();
		return this.currentView;
	}

	/**
	 * Edits ISBUSY field
	 * @param index
	 * @param fieldName
	 * @param isBusy
	 * @return returns a View object of the current View Mode
	 */
	public View editTask(int index, FieldName fieldName, boolean isBusy) {
		Task taskToBeEdited = currentView.getTasks().get(index-1);
		
		if (taskToBeEdited instanceof TimedTask) {
			((TimedTask) taskToBeEdited).setBusy(isBusy);
		}
		
		try {
			db.updateTask(taskToBeEdited);
			this.historyOfOperations.push(new Operation(TypeOfOperation.EDIT, taskToBeEdited));
		} catch (Exception e) {
			//Handle
		}
		
		this.setFeedBack(MESSAGE_EDITED);
		this.currentView = generateView();
		return this.currentView;
	}
	
	/**
	 * marks a certain task as completed
	 * @param index
	 * @return
	 */
	public View markTaskAsCompleted(int index) {
		Task taskToMark = currentView.getTasks().get(index-1);
		
		taskToMark.setStatus(Status.COMPLETED);
		
		try {
			db.updateTask(taskToMark);
			this.historyOfOperations.push(new Operation(TypeOfOperation.EDIT, taskToMark));
		} catch (Exception e) {
			//Handle
		}
		
		this.setFeedBack(MESSAGE_MARK);
		this.currentView = generateView();
		return this.currentView;
	}
	
	/**
	 * Undo the last operation
	 * @return returns a View object of the current View Mode
	 */
	public View undoLastOperation() {
		this.historyOfOperations.pop().undo();
		return currentView;
	}
	
	private String getFeedBack() {
		return this.feedBack;
	}

	private void setFeedBack(String feedBack) {
		this.feedBack = feedBack;
	}
}

	