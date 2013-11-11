package typetodo.logic;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import typetodo.db.DbController;
import typetodo.exception.InvalidAttributeException;
import typetodo.exception.InvalidDateRangeException;
import typetodo.exception.InvalidFieldNameException;
import typetodo.exception.InvalidIdException;
import typetodo.exception.MissingFieldException;
import typetodo.model.DeadlineTask;
import typetodo.model.FieldName;
import typetodo.model.Task;
import typetodo.model.Task.Status;
import typetodo.model.TaskType;
import typetodo.model.TimedTask;
/**
 * The Schedule class provides the necessary API needed to manage (CRUD) the Tasks in the Schedule.
 * @author A0091024U
 *
 */
public class Schedule {
	private static final String ERROR_MESSAGE_INVALID_ID = "ID does not exist in schedule";
	private static final String ERROR_MESSAGE_INVALID_FIELD = "Invalid field name";
	private static final String ERROR_MESSAGE_INVALID_DEADLINE_ATTRIBUTE = "This type of task does not support a deadline";
	private static final String ERROR_MESSAGE_INVALID_START_ATTRIBUTE = "This type of task does not support a start date";
	private static final String ERROR_MESSAGE_INVALID_END_ATTRIBUTE = "This type of task does not support a end date";
	private static final String ERROR_MESSAGE_INVALID_DATE_START_AFTER_END = "Start date cannot be after end date";
	private static final String ERROR_MESSAGE_INVALID_DATE_RANGE = "Invalid date range";
	private static final String ERROR_MESSAGE_MISSING_TITLE = "Task must have a title";
	private static final String ERROR_MESSAGE_MISSING_DEADLINE = "A Deadline Task must have deadline";
	private static final String ERROR_MESSAGE_MISSING_DATES = "Missing start/end date(s)";
		
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
		this.checkForTitle(task);
		
		if (task instanceof TimedTask) {
			this.checkForValidDateRange((TimedTask) task);
		} else if (task instanceof DeadlineTask) {
			this.checkForDeadline((DeadlineTask) task);
		}
		
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
			throw new InvalidIdException(ERROR_MESSAGE_INVALID_ID);
		}
	}

	/**
	 * Updates an existing task with a given task.
	 * @param task Task to replace existing task
	 * @throws Exception 
	 */
	public void updateTask(Task task) throws Exception {
		this.checkForTitle(task);
		
		if (task instanceof TimedTask) {
			this.checkForValidDateRange((TimedTask) task);
		} else if (task instanceof DeadlineTask) {
			this.checkForDeadline((DeadlineTask) task);
		}
		
		db.updateTask(task);
	}

	/**
	 * Updates the TITLE/DESCRIPTION of an existing task.
	 * @param taskId Task id of the existing task
	 * @param fieldName TITLE/DESCRIPTION
	 * @param newString new value
	 * @throws Exception if Id is invalid, if attribute to be updated is does not exist in that 
	 * particular type of task and if new value is invalid 
	 */
	public void updateTask(int taskId, FieldName fieldName, String newString) throws Exception {
		Task taskToBeUpdated = db.getTask(taskId);
		if (taskToBeUpdated == null) {
			throw new InvalidIdException(ERROR_MESSAGE_INVALID_ID);
		}

		switch (fieldName) {
		case TITLE :
			taskToBeUpdated.setTitle(newString);
			this.checkForTitle(taskToBeUpdated);
			break;
		case DESCRIPTION :
			taskToBeUpdated.setDescription(newString);
			break;
		default:
			throw new InvalidFieldNameException(ERROR_MESSAGE_INVALID_FIELD);
		}

		taskToBeUpdated.updateDateModified();
		db.updateTask(taskToBeUpdated);
	}

	/**
	 * Updates the DEADLINE/START/END of an existing task.
	 * @param taskId Task id of the existing task
	 * @param fieldName DEADLINE/START/END
	 * @param newDateTime new value
	 * @throws Exception if Id is invalid, if attribute to be updated is does not exist in that 
	 * particular type of task and if new value is invalid
	 */
	public void updateTask(int taskId, FieldName fieldName, DateTime newDateTime) throws Exception {
		Task taskToBeUpdated = db.getTask(taskId);
		if (taskToBeUpdated == null) {
			throw new InvalidAttributeException(ERROR_MESSAGE_INVALID_ID);
		}

		switch (fieldName) {
		case DEADLINE :
			if (taskToBeUpdated instanceof DeadlineTask) {
				((DeadlineTask) taskToBeUpdated).setDeadline(newDateTime);
			} else {
				throw new InvalidAttributeException(ERROR_MESSAGE_INVALID_DEADLINE_ATTRIBUTE);
			}
			break;
		case START :
			if (taskToBeUpdated instanceof TimedTask) {
				((TimedTask) taskToBeUpdated).setStart(newDateTime);
				this.checkForValidDateRange((TimedTask) taskToBeUpdated);
			} else {
				throw new InvalidAttributeException(ERROR_MESSAGE_INVALID_START_ATTRIBUTE);
			}
			break;
		case END :
			if (taskToBeUpdated instanceof TimedTask) {
				((TimedTask) taskToBeUpdated).setEnd(newDateTime);
			} else {
				throw new InvalidAttributeException(ERROR_MESSAGE_INVALID_END_ATTRIBUTE);
			}
			break;
		default :
			throw new InvalidFieldNameException(ERROR_MESSAGE_INVALID_FIELD);
		}

		taskToBeUpdated.updateDateModified();
		db.updateTask(taskToBeUpdated);
	}

	/**
	 * Updates the status of an existing task.
	 * @param taskId Task id of the existing task
	 * @param status COMPLETE/INCOMPLETE
	 * @throws Exception if id is invalid
	 */
	public void updateTaskStatus(int taskId, Status status) throws Exception {
		Task taskToBeMarked = db.getTask(taskId);

		if (taskToBeMarked == null) {
			throw new InvalidIdException(ERROR_MESSAGE_INVALID_ID);
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
		ArrayList<Task> result = new ArrayList<Task>();
		
		if (keyword == null) {
			return result;
		}
		
		result = db.retrieveContaining(keyword);
		return result;
	}

	/**
	 * Gets a list of all the tasks from Schedule that falls within the given date range. If start and end are both null,
	 * a list of all tasks from Schedule would be returned.
	 * @param start Lower date bound of range
	 * @param end Upper date bound of range
	 * @param status Status of task. COMPLETED/INCOMPLETE
	 * @return returns a list of either tasks which falls under a given date range, or all tasks in the schedule
	 * @throws InvalidDateRangeException if given date range is invalid
	 */
	public ArrayList<Task> getTasksByDateRange(DateTime start, DateTime end, Status status) throws InvalidDateRangeException {
		ArrayList<Task> tasks = new ArrayList<Task>();

		if ((start == null) && (end == null)) {
			return db.retrieveAll();
		} else if ((start == null) || (end == null)) {
			throw new InvalidDateRangeException(ERROR_MESSAGE_INVALID_DATE_RANGE);
		} else if (start.isAfter(end)) {
			throw new InvalidDateRangeException(ERROR_MESSAGE_INVALID_DATE_START_AFTER_END);
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
	 * be returned. Optionally filtered by a given Task status.
	 * @param status Status of task, COMPLETED/INCOMPLETE/null
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
	 * Returns all Floating Tasks in the Schedule, filtered by a given Task status.
	 * Optionally filtered by a given Task status.
	 * @param status Status of task, COMPLETED/INCOMPLETE/null
	 * @return returns a list of all Floating Tasks found in the Schedule.
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
	 * Returns all Deadline Tasks in the Schedule, filtered by a given Task status.
	 * Optionally filtered by a given Task status.
	 * @param status Status of task, COMPLETED/INCOMPLETE/null
	 * @return returns a list of all Deadline Tasks found in schedule
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
	 * Returns all Timed Tasks in the Schedule, filtered by a given Task status.
	 * Optionally filtered by a given Task status.
	 * @param status Status of task, COMPLETED/INCOMPLETE/null
	 * @return returns a list of all Timed Tasks found in schedule
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
			throw new InvalidIdException(ERROR_MESSAGE_INVALID_ID);
		}
		return task;
	}
	
	private void checkForTitle(Task task) throws MissingFieldException {
		if (task.getTitle() == null || task.getTitle().equals("")) {
			throw new MissingFieldException(ERROR_MESSAGE_MISSING_TITLE);
		}
	}
	
	private void checkForDeadline(DeadlineTask deadlineTask) throws MissingFieldException {
		if (deadlineTask.getDeadline() == null) {
			throw new MissingFieldException(ERROR_MESSAGE_MISSING_DEADLINE);
		}
	}
	
	private void checkForValidDateRange(TimedTask timedTask) throws InvalidDateRangeException {
		DateTime start = timedTask.getStart();
		DateTime end = timedTask.getEnd();
		
		if (start == null || end == null) {
			throw new InvalidDateRangeException(ERROR_MESSAGE_MISSING_DATES);
		}
		
		if (start.isAfter(end)) {
			throw new InvalidDateRangeException(ERROR_MESSAGE_INVALID_DATE_START_AFTER_END);
		}
	}
}
