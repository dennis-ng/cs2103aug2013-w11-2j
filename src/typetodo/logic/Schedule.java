package typetodo.logic;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import typetodo.db.DbController;
import typetodo.model.DeadlineTask;
import typetodo.model.FieldName;
import typetodo.model.Task;
import typetodo.model.Task.Status;
import typetodo.model.TimedTask;
/**
 * 
 * @author Shiyu
 *
 */
public class Schedule {
	private ArrayList<Task> currentListOfTasks;
	private DbController db;
	private Object keyItem;

	public Schedule() throws IOException {
		db = DbController.getInstance();
		this.viewTasksofToday();
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Task> getCurrentListOfTasks() {
		this.refreshList();
		return currentListOfTasks;
	}
	
	/**
	 * Adds a task into the Schedule.
	 * @param task Task to be added
	 * @return Returns the ID of the added task
	 * @throws Exception 
	 */
	public int addTask(Task task) throws Exception {
		int taskId = -1;
		if (task instanceof TimedTask) {
			if (((TimedTask) task).getStart().isAfter(((TimedTask) task).getEnd())) {
				throw new Exception("Time of start cannot be after time of end");
			}
			if(db.isAvailable(((TimedTask) task).getStart(), ((TimedTask) task).getEnd())) {
				taskId = db.addTask(task);
			} else {
				//TODO:
				throw new Exception("Task cannot be added as current time frame is busy");
			}
			
		} else {
			taskId = db.addTask(task);
		}
		
		return taskId;
	}

	/**
	 * Deletes a task from the schedule.
	 * @param index Index of the task that is to be deleted
	 * @return Returns the deleted task
	 */
	public Task deleteTaskByIndex(int index) {
		Task taskToBeDeleted = null;
	
		try {
			//taskToBeDeleted = this.currentListOfTasks.get(index - 1);
			taskToBeDeleted = this.getTaskByIdFromCurrentListOfTasks(index);
		} catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("Given index is not in range");
		}
		
		db.deleteTask(taskToBeDeleted.getTaskId());
		
		return taskToBeDeleted;
	}

	/**
	 * 
	 * @param keyword
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * 
	 * @param taskId
	 */
	public void deleteTaskById(int taskId) {
		db.deleteTask(taskId);
	}

	/**
	 * 
	 * @param task
	 * @throws Exception
	 */
	public void editTask(Task task) throws Exception {
		db.updateTask(task);
	}

	public Task editTask(int index, FieldName fieldName, String newString)
			throws Exception {
		Task taskToBeEdited = null;
		try {
			//taskToBeEdited = currentListOfTasks.get(index - 1);
			taskToBeEdited = this.getTaskByIdFromCurrentListOfTasks(index);
		}
		catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("Given index is not in range");
		}
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

		taskToBeEdited.updateDateModified();
		db.updateTask(taskToBeEdited);

		return taskBeforeEdit;
	}

	public Task editTask(int index, FieldName fieldName, DateTime newDateTime)
			throws Exception {
		Task taskToBeEdited = null;
		try {
			//taskToBeEdited = currentListOfTasks.get(index - 1);
			taskToBeEdited = this.getTaskByIdFromCurrentListOfTasks(index);
		} catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("Given index is not in range");
		}
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

		taskToBeEdited.updateDateModified();
		db.updateTask(taskToBeEdited);

		return taskBeforeEdit;
	}

	public Task editTask(int index, FieldName fieldName, boolean newBoolean)
			throws Exception {
		Task taskToBeEdited = null;
		try {
			//taskToBeEdited = currentListOfTasks.get(index - 1);
			taskToBeEdited = this.getTaskByIdFromCurrentListOfTasks(index);
		} catch (IndexOutOfBoundsException e) {
			throw new IndexOutOfBoundsException("Given index is not in range");
		}
		Task taskBeforeEdit = taskToBeEdited.makeCopy();

		if (taskToBeEdited instanceof TimedTask) {
			((TimedTask) taskToBeEdited).setBusy(newBoolean);
		}

		taskToBeEdited.updateDateModified();
		db.updateTask(taskToBeEdited);

		return taskBeforeEdit;
	}

	public Task updateTaskStatus(int index, Status status) 
			throws Exception {
		Task taskToBeMarked = null;
		try {
			//taskToBeMarked = currentListOfTasks.get(index - 1);
			taskToBeMarked = this.getTaskByIdFromCurrentListOfTasks(index);
		} catch (IndexOutOfBoundsException e) {
			//TODO: failed test
			throw new IndexOutOfBoundsException("Given index is not in range");
		}
		
		Task taskBeforeMarking = taskToBeMarked.makeCopy();
		taskToBeMarked.setStatus(status);
		db.updateTask(taskToBeMarked);
		
		return taskBeforeMarking;
	}
	
	public void search(String keyword) {
		this.setKeyItem(keyword);
	}	
	
	public void viewAllTasks() {
		this.keyItem = null;
	}
	
	public void viewTasksofToday() {
		this.setKeyItem(new DateTime());
	}
	
	public void viewTasksByDate(DateTime dateTime) {
		setKeyItem(dateTime);
	}
	
	public void help(){
	}

	private void refreshList() {
		if (keyItem == null) {
			this.currentListOfTasks = db.retrieveAll();
		}
		if (keyItem instanceof String) {
			this.currentListOfTasks = db.retrieveContaining((String) keyItem);
		} else if (keyItem instanceof DateTime) {
			this.currentListOfTasks = db.retrieveList((DateTime) keyItem);
		} 
	}
	
	private void setKeyItem(DateTime dateTime) {
		keyItem = dateTime;
	}

	private void setKeyItem(String keyword) {
		keyItem = keyword;
	}
	
	private Task getTaskByIdFromCurrentListOfTasks(int id) {
		for (Task task : this.currentListOfTasks) {
			if (task.getTaskId() == id) {
				return task;
			}
		}
		
		return null;
	}

}
