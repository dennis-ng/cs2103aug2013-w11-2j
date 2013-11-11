package typetodo.logic;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import typetodo.db.DbController;
import typetodo.model.DeadlineTask;
import typetodo.model.FieldName;
import typetodo.model.Task;
import typetodo.model.Task.Status;
import typetodo.model.TaskType;
import typetodo.model.TimedTask;
/**
 * 
 * @author Shiyu
 *
 */
public class Schedule {
	private DbController db;

	public Schedule() throws IOException {
		db = DbController.getInstance();
	}

	/**
	 * Adds a task into the Schedule.
	 * @param task Task to be added
	 * @return Returns the ID of the added task
	 */
	public int addTask(Task task) throws Exception {

		int taskId = db.addTask(task);

		return taskId;
	}

	/**
	 * Deletes a task from the schedule.
	 * @param taskId Task id of the task that is to be deleted
	 * @throws Exception 
	 */
	public void deleteTaskById(int taskId) throws Exception {
		if (!db.deleteTask(taskId)) {
			throw new Exception("ID does not exist in schedule");
		}
	}

	/**
	 * Updates an existing task with a given task.
	 * @param task Task to replace existing task
	 * @throws Exception 
	 */
	public void updateTask(Task task) throws Exception {
		db.updateTask(task);
	}

	/**
	 * Updates the TITLE/DESCRIPTION of an existing task.
	 * @param taskId Task id of the existing task
	 * @param fieldName TITLE/DESCRIPTION
	 * @param newString new value
	 * @throws Exception 
	 */
	public void updateTask(int taskId, FieldName fieldName, String newString)
			throws Exception {
		Task taskToBeUpdated = db.getTask(taskId);
		if (taskToBeUpdated == null) {
			throw new Exception("ID does not exist in schedule");
		}

		switch (fieldName) {
		case TITLE :
			taskToBeUpdated.setTitle(newString);
			break;
		case DESCRIPTION :
			taskToBeUpdated.setDescription(newString);
			break;
		default:
			// TODO: throw exception
		}

		taskToBeUpdated.updateDateModified();
		db.updateTask(taskToBeUpdated);
	}

	/**
	 * Updates the DEADLINE/START/END of an existing task.
	 * @param taskId Task id of the existing task
	 * @param fieldName DEADLINE/START/END
	 * @param newDateTime new value
	 * @throws Exception 
	 */
	public void updateTask(int taskId, FieldName fieldName, DateTime newDateTime)
			throws Exception {
		Task taskToBeUpdated = db.getTask(taskId);
		if (taskToBeUpdated == null) {
			throw new Exception("ID does not exist in schedule");
		}

		switch (fieldName) {
		case DEADLINE :
			if (taskToBeUpdated instanceof DeadlineTask) {
				((DeadlineTask) taskToBeUpdated).setDeadline(newDateTime);
			} else {
				throw new Exception("This type of task does not support a deadline");
			}
			break;
		case START :
			if (taskToBeUpdated instanceof TimedTask) {
				((TimedTask) taskToBeUpdated).setStart(newDateTime);
			} else {
				throw new Exception("This type of task does not support a start time");
			}
			break;
		case END :
			if (taskToBeUpdated instanceof TimedTask) {
				((TimedTask) taskToBeUpdated).setEnd(newDateTime);
			} else {
				throw new Exception("This type of task does not support a end time");
			}
			break;
		default :
			throw new Exception("Illegal field type");
		}

		taskToBeUpdated.updateDateModified();
		db.updateTask(taskToBeUpdated);
	}

	/**
	 * Updates the status of an existing task.
	 * @param taskId Task id of the existing task
	 * @param status COMPLETE/INCOMPLETE
	 * @throws Exception 
	 */
	public void updateTaskStatus(int taskId, Status status) 
			throws Exception {
		Task taskToBeMarked = db.getTask(taskId);

		if (taskToBeMarked == null) {
			throw new Exception("ID does not exist in schedule");
		}

		taskToBeMarked.setStatus(status);
		db.updateTask(taskToBeMarked);
	}

	/**
	 * Searches the schedule and returns a list of Tasks which contain the given keyword in their 
	 * TITLE/DESCRIPTION.
	 * @param keyword Keyword to search
	 * @return Returns a list of Tasks which contain the given keyword in their TITLE/DESCRIPTION.
	 */
	public ArrayList<Task> search(String keyword) {
		return db.retrieveContaining(keyword);
	}

	/**
	 * 
	 * @param start
	 * @param end
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Task> getTasksByDateRange(DateTime start, DateTime end, Status status) throws Exception {
		ArrayList<Task> tasks = new ArrayList<Task>();

		if ((start == null) && (end == null)) {
			return db.retrieveAll();
		} else if ((start == null) || (end == null)) {
			throw new Exception("Invalid date range");
		} else if (start.isAfter(end)) {
			throw new Exception("Invalid date range, start date cannot be after end date");
		}
		else if (status == null) {
			return db.retrieveTasks(start, end);
		} else {
			for (Task task : db.retrieveTasks(start, end)) {
				if (task.getStatus().equals(status)) {
					tasks.add(task);
				}
			}
		}
		return tasks;
	}

	/**
	 * Returns a list of all the tasks in the schedule, filtered by the given status.
	 * E.g, if status is COMPLETED, a list of all completed tasks in the schedule would
	 * be returned.
	 * @param status Status of task, COMPLETED or INCOMPLETE
	 * @return returns an ArrayList of tasks
	 */
	public ArrayList<Task> getAllTasks(Status status) {
		if (status == null) {
			return db.retrieveAll();
		}

		ArrayList<Task> tasks = new ArrayList<Task>();
		for (Task task : db.retrieveAll()) {
			if (task.getStatus().equals(status)) {
				tasks.add(task);
			}
		}	
		return tasks;
	}

	/**
	 * Returns 
	 * @param status
	 * @return
	 */
	public ArrayList<Task> getFloatingTasks(Status status) {
		ArrayList<Task> listOfFloatingTasks = new ArrayList<Task>();

		for (Task task : db.retrieveAll(TaskType.FLOATING_TASK)) {
			if (status == null) {
				listOfFloatingTasks.add(task);
			} else {
				if (task.getStatus().equals(status)) {
					listOfFloatingTasks.add(task);
				}
			}
		}

		return listOfFloatingTasks;
	}

	/**
	 * 
	 * @param status
	 * @return
	 */
	public ArrayList<Task> getDeadlineTasks(Status status) {
		ArrayList<Task> listOfDeadlineTasks = new ArrayList<Task>();

		for (Task task : db.retrieveAll(TaskType.DEADLINE_TASK)) {
			if (status == null) {
				listOfDeadlineTasks.add(task);
			} else {
				if (task.getStatus().equals(status)) {
					listOfDeadlineTasks.add(task);
				}
			}
		}

		return listOfDeadlineTasks;
	}

	/**
	 * 
	 * @param status
	 * @return
	 */
	public ArrayList<Task> getTimedTasks(Status status) {
		ArrayList<Task> listOfTimedTasks = new ArrayList<Task>();

		for (Task task : db.retrieveAll(TaskType.TIMED_TASK)) {
			if (status == null) {
				listOfTimedTasks.add(task);
			} else {
				if (task.getStatus().equals(status)) {
					listOfTimedTasks.add(task);
				}
			}
		}

		return listOfTimedTasks;
	}

	/**
	 * Returns the task with the given task id
	 * @param taskId task id of task
	 * @return returns the tasks with the give task id
	 * @throws Exception if No task with the given task id is found
	 */
	public Task getTask(int taskId) throws Exception {
		Task task = db.getTask(taskId);
		if (task == null) {
			throw new Exception("ID does not exist in schedule");
		}
		return task;
	}

}
