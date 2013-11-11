// @author A0097968Y
package typetodo.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import typetodo.exception.DuplicateKeyException;
import typetodo.exception.InvalidDateRangeException;
import typetodo.exception.MissingFieldException;
import typetodo.model.DeadlineTask;
import typetodo.model.FloatingTask;
import typetodo.model.Task;
import typetodo.model.TaskType;
import typetodo.model.TimedTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class DbController {

	// Constants
	private static final String DIRECTORY_NAME = "savedfiles";
	private static final String FILENAME_TASK = "tasks.txt";
	private static final String FILENAME_PROPERTIES = "properties.txt";

	// Variables
	private HashMap<String, File> allFiles;
	private HashMap<String, String> properties;
	private TreeMap<Integer, Task> tasksCache;

	// Controllers and external libraries
	private static DbController mainDbHandler;
	private final Gson gson;

	private DbController() throws IOException, JsonSyntaxException,
			FileNotFoundException {

		gson = initialiseGson();
		tasksCache = new TreeMap<Integer, Task>();
		properties = new HashMap<String, String>();
		initializeFiles();
		reloadAllFiles();
	}

	private Gson initialiseGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
		gsonBuilder.registerTypeHierarchyAdapter(DateTime.class,
				new DateTimeTypeAdapter());
		return gsonBuilder.setPrettyPrinting().create();
	}

	public void reloadAllFiles() {
		for (String fileName : allFiles.keySet()) {
			this.loadFile(fileName);
		}
	}

	public static DbController getInstance() throws IOException,
			JsonSyntaxException, FileNotFoundException {
		if (mainDbHandler == null) {
			mainDbHandler = new DbController();
		}
		return mainDbHandler;
	}

	private void initializeFiles() throws IOException {
		allFiles = new HashMap<String, File>(3);
		final File subdirectory = new File(DIRECTORY_NAME);
		File FILE_TASKS = new File(subdirectory, FILENAME_TASK);
		File FILE_PROPERTIES = new File(subdirectory, FILENAME_PROPERTIES);
		allFiles.put(FILENAME_TASK, FILE_TASKS);
		allFiles.put(FILENAME_PROPERTIES, FILE_PROPERTIES);
		if (!subdirectory.exists()) {
			subdirectory.mkdir();
			for (File file : allFiles.values()) {
				file.createNewFile();
			}
		}
	}

	/**
	 * @throws FileNotFoundException
	 *           During loadFile, if directory exist but file does not
	 * @throws JsonSyntaxException
	 *           contents of the file to load is incorrect
	 */
	private void loadFile(String fileName) throws JsonSyntaxException {
		StringBuilder fileToTextBuffer = new StringBuilder();
		try {
			File fileToLoad = allFiles.get(fileName);
			BufferedReader reader = new BufferedReader(new FileReader(fileToLoad));
			String nextLine;
			while ((nextLine = reader.readLine()) != null) {
				fileToTextBuffer.append(nextLine);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!fileToTextBuffer.toString().isEmpty()) {
			if (fileName.equals(FILENAME_TASK)) {
				Type collectionType = new TypeToken<TreeMap<Integer, Task>>() {
				}.getType();
				tasksCache = gson.fromJson(fileToTextBuffer.toString(), collectionType);
			} else if (fileName.equals(FILENAME_PROPERTIES)) {
				Type collectionType = new TypeToken<HashMap<String, Object>>() {
				}.getType();
				properties = gson.fromJson(fileToTextBuffer.toString(), collectionType);
			}
		}
	}

	private void writeChangesToFile(String fileName) {
		BufferedWriter writer;
		try {
			File fileToWrite = allFiles.get(fileName);
			writer = new BufferedWriter(new FileWriter(fileToWrite));
			if (fileName.equals(FILENAME_TASK)) {
				Type collectionType = new TypeToken<TreeMap<Integer, Task>>() {
				}.getType();
				writer.write(gson.toJson(tasksCache, collectionType));
			} else if (fileName.equals(FILENAME_PROPERTIES)) {
				Type collectionType = new TypeToken<HashMap<String, Object>>() {
				}.getType();
				writer.write(gson.toJson(properties, collectionType));
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param propertyName
	 *          the name of the property that was saved
	 * @return A string of property matched by the propertyName, or null if the
	 *         property doesn't exist
	 */
	public String getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	/**
	 * @param propertyName
	 *          The name of the property to be saved.
	 * @param property
	 *          A string to be saved as a property.
	 */
	public void setProperty(String propertyName, String property) {
		properties.put(propertyName, property);
		this.writeChangesToFile(FILENAME_PROPERTIES);
	}

	/**
	 * 
	 * @param task
	 * @return generated taskId of successfully added task
	 * @throws DuplicateKeyException
	 *           task with same id is in the database
	 */
	public int addTask(Task newTask) throws DuplicateKeyException {
		// Supports for undoing deleted task
		if (newTask.getTaskId() != 0) {
			if (tasksCache.containsKey(newTask.getTaskId())) {
				throw new DuplicateKeyException("Task with the same id already exist.");
			}
			tasksCache.put(newTask.getTaskId(), newTask);
			this.writeChangesToFile(FILENAME_TASK);
			return newTask.getTaskId();
		}

		// Generate a new taskId to add a totally new task
		int newTaskIdGenerated;
		if (tasksCache.isEmpty()) {
			newTaskIdGenerated = 1;
		} else {
			newTaskIdGenerated = tasksCache.lastKey() + 1;
		}
		newTask.setTaskId(newTaskIdGenerated);
		tasksCache.put(newTaskIdGenerated, newTask);
		this.writeChangesToFile(FILENAME_TASK);
		return newTaskIdGenerated;
	}

	/**
	 * 
	 * @param taskId
	 *          taskId of the task to be deleted from the database
	 * @return true when deleted, or false if not found
	 */
	public boolean deleteTask(int taskId) {
		if (tasksCache.containsKey(taskId)) {
			tasksCache.remove(taskId);
			this.writeChangesToFile(FILENAME_TASK);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param taskId
	 *          taskId of the task to check for existent in the database.
	 * @return returns true if the taskId is found, or false if not found
	 */
	public boolean containsTask(int taskId) {
		return tasksCache.containsKey(taskId);
	}

	/**
	 * @param taskId
	 *          taskId of the task that exist in the database.
	 * @return returns the task if the taskId is valid, or null if invalid
	 */
	public Task getTask(int taskId) {
		return tasksCache.get(taskId);
	}

	/**
	 * 
	 * @param taskToUpdate
	 *          The updated task to be written over the task in database
	 * @return true if Updated, false if not found
	 * @throws MissingFieldException
	 *           task identifier missing
	 */
	public boolean updateTask(Task taskToUpdate) throws Exception {
		int taskIdToUpdate = taskToUpdate.getTaskId();
		if (taskIdToUpdate == 0) {
			throw new MissingFieldException("The task did not contain a taskId.");
		}
		if (tasksCache.put(taskIdToUpdate, taskToUpdate) != null) {
			this.writeChangesToFile(FILENAME_TASK);
			return true;
		}
		return false;
	}

	/**
	 * @param startDay
	 *          Start of the time range of the tasks you want
	 * @param endDay
	 *          End of the time range of the tasks you want
	 * @return An arraylist of all the tasks within a given date range. An empty
	 *         arraylist will be returned if nothing is found.
	 * @throws InvalidDateRangeException
	 *           endDay cannot be strictly earlier than startDay
	 */
	public ArrayList<Task> retrieveTasks(DateTime startDay, DateTime endDay)
			throws InvalidDateRangeException {
		List<DeadlineTask> deadlineTasks = new ArrayList<DeadlineTask>();
		List<TimedTask> timedTasks = new ArrayList<TimedTask>();
		List<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
		LocalDate rangeStart = startDay.toLocalDate();
		LocalDate rangeEnd = endDay.toLocalDate();
		if (rangeEnd.isBefore(rangeStart)) {
			throw new InvalidDateRangeException(
					"End time is earlier than start time.");
		} else {
			for (Task taskInCache : tasksCache.values()) {
				if (taskInCache instanceof DeadlineTask) {
					DeadlineTask deadlineTask = (DeadlineTask) taskInCache;
					// Get the localdate only so that we can compare without time
					LocalDate deadline = deadlineTask.getDeadline().toLocalDate();
					if (isWithin(deadline, rangeStart, rangeEnd)) {
						deadlineTasks.add((DeadlineTask) taskInCache);
					}
				} else if (taskInCache instanceof TimedTask) {
					TimedTask timedTask = (TimedTask) taskInCache;
					// Get the localdate only so that we can compare without time
					LocalDate taskStart = timedTask.getStart().toLocalDate();
					LocalDate taskEnd = timedTask.getEnd().toLocalDate();

					if (isWithin(taskStart, taskEnd, rangeStart, rangeEnd)) {
						timedTasks.add(timedTask);
					}
				} else if (taskInCache instanceof FloatingTask) {
					floatingTasks.add((FloatingTask) taskInCache);
				}
			}
		}
		return combineTasksForViewing(deadlineTasks, timedTasks, floatingTasks);
	}

	/**
	 * @param startDay
	 *          Start of the time range of the tasks you want
	 * @param endDay
	 *          End of the time range of the tasks you want
	 * @param taskType
	 *          Only DeadlineTask, TimedTask and Floating task will be considered.
	 * @return An arraylist of a specific type of task within a given date range.
	 *         An empty arraylist will be returned if nothing is found or the
	 *         TaskType specified is incorrect.
	 * @throws InvalidDateRangeException
	 *           endDay cannot be strictly earlier than startDay
	 */
	public ArrayList<Task> retrieveTasks(DateTime startDay, DateTime endDay,
			TaskType taskType) throws InvalidDateRangeException {
		ArrayList<Task> selectedTasks = new ArrayList<Task>();
		LocalDate rangeStart = startDay.toLocalDate();
		LocalDate rangeEnd = endDay.toLocalDate();
		if (rangeEnd.isBefore(rangeStart)) {
			throw new InvalidDateRangeException(
					"End time is earlier than start time.");
		} else {
			for (Task taskInCache : tasksCache.values()) {
				switch (taskType) {
				// Only add the type of task that is needed
					case DEADLINE_TASK:
						if (taskInCache instanceof DeadlineTask) {
							DeadlineTask deadlineTask = (DeadlineTask) taskInCache;
							// Get the localdate only so that we can compare without time
							LocalDate deadline = deadlineTask.getDeadline().toLocalDate();
							if (isWithin(deadline, rangeStart, rangeEnd)) {
								selectedTasks.add(taskInCache);
							}
						}
						break;
					case TIMED_TASK:
						if (taskInCache instanceof TimedTask) {
							TimedTask timedTask = (TimedTask) taskInCache;
							// Get the localdate only so that we can compare without time
							LocalDate taskStart = timedTask.getStart().toLocalDate();
							LocalDate taskEnd = timedTask.getEnd().toLocalDate();

							if (isWithin(taskStart, taskEnd, rangeStart, rangeEnd)) {
								selectedTasks.add(timedTask);
							}
						}
						break;
					case FLOATING_TASK:
						if (taskInCache instanceof FloatingTask) {
							selectedTasks.add(taskInCache);
						}
				}
			}
		}
		return selectedTasks;
	}

	/**
	 * 
	 * @return An arraylist of all the tasks in the system. An empty arraylist
	 *         will be returned if nothing is found.
	 */
	public ArrayList<Task> retrieveAll() {
		List<DeadlineTask> deadlineTasks = new ArrayList<DeadlineTask>();
		List<TimedTask> timedTasks = new ArrayList<TimedTask>();
		List<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
		for (Task taskInCache : tasksCache.values()) {
			if (taskInCache instanceof DeadlineTask) {
				deadlineTasks.add((DeadlineTask) taskInCache);
			} else if (taskInCache instanceof TimedTask) {
				timedTasks.add((TimedTask) taskInCache);
			} else if (taskInCache instanceof FloatingTask) {
				floatingTasks.add((FloatingTask) taskInCache);
			}
		}
		return combineTasksForViewing(deadlineTasks, timedTasks, floatingTasks);
	}

	/**
	 * 
	 * @return An arraylist of all the specific type of tasks in the system. An
	 *         empty arraylist will be returned if nothing is found or if type of
	 *         task is incorrect.
	 */
	public ArrayList<Task> retrieveAll(TaskType taskType) {
		ArrayList<Task> selectedTasks = new ArrayList<Task>();
		for (Task taskInCache : tasksCache.values()) {
			switch (taskType) {
				case DEADLINE_TASK:
					if (taskInCache instanceof DeadlineTask) {
						selectedTasks.add(taskInCache);
					}
					break;
				case TIMED_TASK:
					if (taskInCache instanceof TimedTask) {
						selectedTasks.add(taskInCache);
					}
					break;
				case FLOATING_TASK:
					if (taskInCache instanceof FloatingTask) {
						selectedTasks.add(taskInCache);
					}
					break;

			}
		}
		return selectedTasks;
	}

	/**
	 * 
	 * @param searchCriteria
	 * @return An arraylist of all the tasks that meets the searching criteria. An
	 *         empty arraylist will be returned if nothing is found.
	 * @throws Exception
	 */
	public ArrayList<Task> retrieveContaining(String searchCriteria) {
		List<DeadlineTask> deadlineTasks = new ArrayList<DeadlineTask>();
		List<TimedTask> timedTasks = new ArrayList<TimedTask>();
		List<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
		for (Task taskInCache : tasksCache.values()) {
			if (taskInCache.getTitle().toUpperCase()
					.contains(searchCriteria.toUpperCase())
					|| taskInCache.getDescription().toUpperCase()
							.contains(searchCriteria.toUpperCase())) {
				if (taskInCache instanceof DeadlineTask) {
					deadlineTasks.add((DeadlineTask) taskInCache);
				} else if (taskInCache instanceof TimedTask) {
					timedTasks.add((TimedTask) taskInCache);
				} else if (taskInCache instanceof FloatingTask) {
					floatingTasks.add((FloatingTask) taskInCache);
				}
			}
		}
		return combineTasksForViewing(deadlineTasks, timedTasks, floatingTasks);

	}

	/**
	 * 
	 * @param searchCriteria
	 * @return An arraylist of all the tasks that meets the searching criteria. An
	 *         empty arraylist will be returned if nothing is found.
	 * @throws Exception
	 */
	public ArrayList<Task> retrieveContaining(String searchCriteria,
			TaskType taskType) {
		ArrayList<Task> selectedTasks = new ArrayList<Task>();
		for (Task taskInCache : tasksCache.values()) {
			if (foundInTask(taskInCache, searchCriteria)) {
				switch (taskType) {
					case DEADLINE_TASK:
						if (taskInCache instanceof DeadlineTask) {
							selectedTasks.add(taskInCache);
						}
						break;
					case TIMED_TASK:
						if (taskInCache instanceof TimedTask) {
							selectedTasks.add(taskInCache);
						}
						break;
					case FLOATING_TASK:
						if (taskInCache instanceof FloatingTask) {
							selectedTasks.add(taskInCache);
						}
						break;

				}
			}
		}
		return selectedTasks;

	}

	/**
	 * @return true if searchCriteria is found in the name or description of the
	 *         task.
	 */
	private boolean foundInTask(Task task, String searchCriteria) {
		return (task.getTitle().toUpperCase()
				.contains(searchCriteria.toUpperCase()) || task.getDescription()
				.toUpperCase().contains(searchCriteria.toUpperCase()));
	}

	private ArrayList<Task> combineTasksForViewing(
			List<DeadlineTask> deadlineTasks, List<TimedTask> timedTasks,
			List<FloatingTask> floatingTasks) {
		ArrayList<Task> filteredTasks = new ArrayList<Task>();

		Collections.sort(deadlineTasks, DeadlineTask.COMPARE_BY_DATE);
		Collections.sort(timedTasks, TimedTask.COMPARE_BY_DATE);

		filteredTasks.addAll(deadlineTasks);
		filteredTasks.addAll(timedTasks);
		filteredTasks.addAll(floatingTasks);
		return filteredTasks;
	}

	/**
	 * @param taskStart
	 *          Start date of the task
	 * @param taskEnd
	 *          End date of the task
	 * @param rangeStart
	 *          Start of the range to check
	 * @param rangeEnd
	 *          End of the range to check
	 * @return true if a day between taskStart and taskEnd inclusively is within
	 *         rangeStart and rangeEnd
	 */
	private boolean isWithin(LocalDate taskStart, LocalDate taskEnd,
			LocalDate rangeStart, LocalDate rangeEnd) {
		return (!(taskStart.isAfter(rangeEnd) || taskStart.isBefore(rangeStart))
				|| !(taskEnd.isAfter(rangeEnd) || taskEnd.isBefore(rangeStart)) || taskStart
				.isBefore(rangeStart) && taskEnd.isAfter(rangeEnd));
	}

	/**
	 * @param dateToCheck
	 *          The date to check if within the given range.
	 * @param rangeStart
	 *          Start of the range to check
	 * @param rangeEnd
	 *          End of the range to check
	 * @return true if the day is within rangeStart and rangeEnd
	 */
	private boolean isWithin(LocalDate dateToCheck, LocalDate rangeStart,
			LocalDate rangeEnd) {
		return (!dateToCheck.isBefore(rangeStart) && !dateToCheck.isAfter(rangeEnd));
	}

}
