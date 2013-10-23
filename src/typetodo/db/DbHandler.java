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
import java.util.List;
import java.util.TreeMap;

import org.joda.time.DateTime;

import typetodo.model.DeadlineTask;
import typetodo.model.FloatingTask;
import typetodo.model.Task;
import typetodo.model.TimedTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class DbHandler {

	// Constants
	private static final String FILENAME = "TypeToDo.txt";
	private final File savedFile;

	// Variables
	private TreeMap<Integer, Task> tasksCache;

	// Controllers and external libraries
	private static DbHandler mainDbHandler;
	private final Gson gson;

	private DbHandler() throws IOException {
		savedFile = new File(FILENAME);
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
		gsonBuilder.registerTypeHierarchyAdapter(DateTime.class,
				new DateTimeTypeConverter());
		gson = gsonBuilder.setPrettyPrinting().create();
		// Create a comparator to sort by type of tasks and end datetime
		tasksCache = new TreeMap<Integer, Task>();
		if (!savedFile.exists()) {
			savedFile.createNewFile();
		}
		this.loadFile();
	}

	public static DbHandler getInstance() throws IOException {
		if (mainDbHandler == null) {
			mainDbHandler = new DbHandler();
		}
		return mainDbHandler;
	}

	private void loadFile() {
		StringBuilder fileToTextBuffer = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(savedFile));
			String nextLine;
			while ((nextLine = reader.readLine()) != null) {
				fileToTextBuffer.append(nextLine);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!fileToTextBuffer.toString().isEmpty()) {
			Type collectionType = new TypeToken<TreeMap<Integer, Task>>() {
			}.getType();
			tasksCache = gson.fromJson(fileToTextBuffer.toString(), collectionType);
		}
	}

	private void writeChangesToFile() {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(savedFile));
			Type collectionType = new TypeToken<TreeMap<Integer, Task>>() {
			}.getType();
			writer.write(gson.toJson(tasksCache, collectionType));
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
			this.writeChangesToFile();
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
		this.writeChangesToFile();
		return newTaskIdGenerated;
	}

	/**
	 * 
	 * @param task
	 * @return Will return true when deleted, and false if not found
	 */
	public boolean deleteTask(int taskIdToDelete) {
		if (tasksCache.containsKey(taskIdToDelete)) {
			tasksCache.remove(taskIdToDelete);
			this.writeChangesToFile();
			return true;
		} else {
			return false;
		}
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
			this.writeChangesToFile();
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param day
	 * @return An arraylist of all the tasks on this day. null will be returned if
	 *         nothing is found.
	 * @throws Exception
	 */
	public ArrayList<Task> retrieveList(DateTime day) {
		List<DeadlineTask> deadlineTasks = new ArrayList<DeadlineTask>();
		List<TimedTask> timedTasks = new ArrayList<TimedTask>();
		List<FloatingTask> floatingTasks = new ArrayList<FloatingTask>();
		for (Task taskInCache : tasksCache.values()) {
			if (taskInCache instanceof DeadlineTask) {
				if (!((DeadlineTask) taskInCache).getDeadline().isBefore(day)) {
					deadlineTasks.add((DeadlineTask) taskInCache);
				}
			} else if (taskInCache instanceof TimedTask) {
				if (!(((TimedTask) taskInCache).getEnd().isBefore(day) && ((TimedTask) taskInCache)
						.getStart().isAfter(day))) {
					timedTasks.add((TimedTask) taskInCache);
				}
			} else if (taskInCache instanceof FloatingTask) {
				floatingTasks.add((FloatingTask) taskInCache);
			}
		}
		return combineTasksForViewing(deadlineTasks, timedTasks, floatingTasks);
	}

	/**
	 * 
	 * @param day
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
			if (taskInCache.getTitle().contains(searchCriteria)
					|| taskInCache.getDescription().contains(searchCriteria)) {
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

	public boolean isAvailable(DateTime start, DateTime end) {
		boolean isAvailable = true;
		for (Task taskInCache : tasksCache.values()) {
			if (taskInCache instanceof TimedTask) {
				TimedTask timedTask = (TimedTask) taskInCache;
				if (timedTask.isBusy()
						&& !(timedTask.getStart().isAfter(end) || timedTask.getEnd()
								.isBefore(start))) {
					isAvailable = false;
					return isAvailable;
				}
			}
		}
		return isAvailable;
	}

	public Task retrieveBusyTask(DateTime start, DateTime end) {
		Task busyTask = null;
		for (Task taskInCache : tasksCache.values()) {
			if (taskInCache instanceof TimedTask) {
				TimedTask timedTask = (TimedTask) taskInCache;
				if (timedTask.isBusy()
						&& !(timedTask.getStart().isAfter(end) || timedTask.getEnd()
								.isBefore(start))) {
					busyTask = timedTask;
					return busyTask;
				}
			}
		}
		return busyTask;
	}

}
