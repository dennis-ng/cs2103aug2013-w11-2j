/**
 * Author : Dennis Ng
 * Email	: a0097968@nus.edu.sg
 */
package typetodo.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

import typetodo.model.DeadlineTask;
import typetodo.model.FloatingTask;
import typetodo.model.Task;
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

	private DbController() throws IOException, JsonSyntaxException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
		gsonBuilder.registerTypeHierarchyAdapter(DateTime.class,
				new DateTimeTypeAdapter());
		gson = gsonBuilder.setPrettyPrinting().create();
		// Create a comparator to sort by type of tasks and end datetime
		tasksCache = new TreeMap<Integer, Task>();
		properties = new HashMap<String, String>();
		initializeFiles();
		for (String fileName : allFiles.keySet()) {
			this.loadFile(fileName);
		}
	}

	public static DbController getInstance() throws IOException {
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
	 * @throws JsonSyntaxException
	 *           when contents of the file to load is incorrect
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!fileToTextBuffer.toString().isEmpty()) {
			if (fileName.equals(FILENAME_TASK)) {
				Type collectionType = new TypeToken<TreeMap<Integer, Task>>() {
				}.getType();
				// The following statement throws JsonSyntaxException when contents of
				// the file to load is incorrect
				tasksCache = gson.fromJson(fileToTextBuffer.toString(), collectionType);
			} else if (fileName.equals(FILENAME_PROPERTIES)) {
				Type collectionType = new TypeToken<HashMap<String, Object>>() {
				}.getType();
				// The following statement throws JsonSyntaxException when contents of
				// the file to load is incorrect
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
	 * 
	 * @param task
	 * @return If successful: taskId of successfully edited task. This allows
	 *         caller to know the taskId if undo is required.
	 * @throws
	 */
	public int addTask(Task newTask) throws Exception {
		// Supports for undoing deleted task
		if (newTask.getTaskId() != 0) {
			if (tasksCache.containsKey(newTask.getTaskId())) {
				// TODO create a specific exception
				throw new Exception("Task with the same id already exist.");
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
	 *          The taskId of the task that exists in the database.
	 * @return This method returns true when deleted, and false if not found
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
	 *          The taskId of the task that exist in the database.
	 * @return This method returns the task if the taskId is valid, else it will
	 *         return null.
	 */
	public Task getTask(int taskId) {
		return tasksCache.get(taskId);
	}

	/**
	 * 
	 * @param taskToUpdate
	 * @return true: Updated, false: Not found
	 * @throws Exception
	 *           : If clash with time slot
	 */
	public boolean updateTask(Task taskToUpdate) throws Exception {
		int taskIdToUpdate = taskToUpdate.getTaskId();
		if (taskIdToUpdate == 0) {
			// TODO create specific exception
			throw new Exception("The task did not contain a taskId");
		}
		if (tasksCache.put(taskIdToUpdate, taskToUpdate) != null) {
			this.writeChangesToFile(FILENAME_TASK);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param
	 * @return An arraylist of all the tasks within a given date range. null will
	 *         be returned if nothing is found.
	 * @throws Exception
	 */
	public ArrayList<Task> retrieveTasks(DateTime startDay, DateTime endDay) {
		List<DeadlineTask> deadlineTasks = new ArrayList<DeadlineTask>();
		List<TimedTask> timedTasks = new ArrayList<TimedTask>();
		List<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
		for (Task taskInCache : tasksCache.values()) {
			if (taskInCache instanceof DeadlineTask) {
				if (!((DeadlineTask) taskInCache).getDeadline().toLocalDate()
						.isBefore(startDay.toLocalDate())
						&& !((DeadlineTask) taskInCache).getDeadline().toLocalDate()
								.isAfter(endDay.toLocalDate())) {
					deadlineTasks.add((DeadlineTask) taskInCache);
				}
			} else if (taskInCache instanceof TimedTask) {
				TimedTask timedTask = (TimedTask) taskInCache;
				// Get the localdate only so that we can compare without time
				LocalDate taskStart = timedTask.getStart().toLocalDate();
				LocalDate taskEnd = timedTask.getEnd().toLocalDate();
				LocalDate rangeStart = startDay.toLocalDate();
				LocalDate rangeEnd = endDay.toLocalDate();

				if (!(taskStart.isAfter(rangeEnd) || taskStart.isBefore(rangeStart))
						|| !(taskEnd.isAfter(rangeEnd) || taskEnd.isBefore(rangeStart))
						|| taskStart.isBefore(rangeStart) && taskEnd.isAfter(rangeEnd)) {
					timedTasks.add(timedTask);
				}
			} else if (taskInCache instanceof FloatingTask) {
				floatingTasks.add((FloatingTask) taskInCache);
			}
		}
		return combineTasksForViewing(deadlineTasks, timedTasks, floatingTasks);
	}

	/**
	 * 
	 * @return An arraylist of all the tasks in the system. null will be returned
	 *         if nothing is found.
	 * @throws Exception
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
	 * @param searchCriteria
	 * @return An arraylist of all the tasks that meets the searching criteria.
	 *         null will be returned if nothing is found.
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
	 * @param propertyName
	 *          is the name of the property that was saved.
	 * @return Returns null when the property doesn't exist.
	 */
	public String getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	/**
	 * @param propertyName
	 *          The name of the property you want to save.
	 * @param property
	 *          A string that you want to save as a property.
	 */
	public void setProperty(String propertyName, String property) {
		properties.put(propertyName, property);
		this.writeChangesToFile(FILENAME_PROPERTIES);
	}
}
