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
import java.util.Date;
import java.util.List;

import typetodo.logic.DeadlineTask;
import typetodo.logic.FloatingTask;
import typetodo.logic.Task;
import typetodo.logic.TimedTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class DBHandler {

	private static final String FILENAME = "TypeToDo.txt";
	private final File savedFile;

	private List<Task> tasksCache;

	private final Gson gson;

	public DBHandler() throws IOException {
		savedFile = new File(FILENAME);
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
		gson = gsonBuilder.setPrettyPrinting().create();
		tasksCache = new ArrayList<Task>();
		if (!savedFile.exists()) {
			savedFile.createNewFile();
		}
		this.loadFile();
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
			// tasksCache = gson.fromJson(fileToTextBuffer.toString(),
			// collectionType);
			Type collectionType = new TypeToken<List<Task>>() {
			}.getType();
			tasksCache = gson.fromJson(fileToTextBuffer.toString(), collectionType);

		}
		// TODO Create a custom comparator to sort list by taskId
	}

	private void writeChangesToFile() {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(savedFile));
			// Type collectionType = new TypeToken<List<Task>>() {
			// }.getType();
			Type collectionType = new TypeToken<List<Task>>() {
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
		if (newTask.getTaskId() != 0) {
			// TODO Check for conflict in the list since the taskId is given
			tasksCache.add(newTask);
			this.writeChangesToFile();
			// TODO do a sort by taskId
			return newTask.getTaskId();
		}
		int taskId;
		if (tasksCache.isEmpty()) {
			taskId = 1;
		} else {
			taskId = tasksCache.get(tasksCache.size() - 1).getTaskId() + 1;
		}
		newTask.setTaskId(taskId);
		tasksCache.add(newTask);
		this.writeChangesToFile();
		return taskId;
	}

	/**
	 * 
	 * @param task
	 * @return true: Deleted, false: Not found
	 */
	public boolean deleteTask(int taskIdToDelete) {
		boolean isDeleted = false;
		// TODO Try to use HashMap to reduce complexity
		for (int i = 0; i < tasksCache.size(); i++) {
			if (tasksCache.get(i).getTaskId() == taskIdToDelete) {
				tasksCache.remove(i);
				this.writeChangesToFile();
				isDeleted = true;
				return isDeleted;
			}
		}
		return isDeleted;
	}

	/**
	 * 
	 * @param task
	 * @return true: Updated, false: Not found
	 * @throws Exception
	 *           : If clash with time slot
	 */
	public boolean updateTask(Task task) throws Exception {
		// TODO Try to use HashMap to reduce complexity
		boolean isUpdated = false;
		// Below will throw NullErrorException if there is no taskId
		int taskIdToUpdate = task.getTaskId();
		for (int i = 0; i < tasksCache.size(); i++) {
			if (tasksCache.get(i).getTaskId() == taskIdToUpdate) {
				tasksCache.set(i, task);
				this.writeChangesToFile();
				isUpdated = true;
				return isUpdated;
			}
		}
		return isUpdated;
	}

	/**
	 * 
	 * @param day
	 * @return An arraylist of all the tasks on this day. null will be returned if
	 *         nothing is found.
	 * @throws Exception
	 */
	public ArrayList<Task> retrieveList(Date day) {
		List<Task> deadlineTasks = new ArrayList<Task>();
		List<Task> timedTasks = new ArrayList<Task>();
		List<Task> floatingTasks = new ArrayList<Task>();
		for (Task taskInCache : tasksCache) {
			if (taskInCache instanceof DeadlineTask) {
				if (!((DeadlineTask) taskInCache).getDeadline().before(day)) {
					deadlineTasks.add(taskInCache);
				}
			} else if (taskInCache instanceof TimedTask) {
				if (!(((TimedTask) taskInCache).getEnd().before(day) && ((TimedTask) taskInCache)
						.getStart().after(day))) {
					timedTasks.add(taskInCache);
				}
			} else if (taskInCache instanceof FloatingTask) {
				floatingTasks.add(taskInCache);
			}
		}
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		filteredTasks.addAll(deadlineTasks);
		filteredTasks.addAll(timedTasks);
		filteredTasks.addAll(floatingTasks);
		return filteredTasks;
	}

	/**
	 * 
	 * @param searchCriteria
	 * @return An arraylist of all the tasks that meets the searching criteria.
	 *         null will be returned if nothing is found.
	 * @throws Exception
	 */
	public ArrayList<Task> retrieveContaining(String searchCriteria) {
		List<Task> deadlineTasks = new ArrayList<Task>();
		List<Task> timedTasks = new ArrayList<Task>();
		List<Task> floatingTasks = new ArrayList<Task>();
		for (Task taskInCache : tasksCache) {
			if (taskInCache.getName().contains(searchCriteria)
					|| taskInCache.getDescription().contains(searchCriteria)) {
				if (taskInCache instanceof DeadlineTask) {
					deadlineTasks.add(taskInCache);
				} else if (taskInCache instanceof TimedTask) {
					timedTasks.add(taskInCache);
				} else if (taskInCache instanceof FloatingTask) {
					floatingTasks.add(taskInCache);
				}
			}
		}
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		filteredTasks.addAll(deadlineTasks);
		filteredTasks.addAll(timedTasks);
		filteredTasks.addAll(floatingTasks);
		return filteredTasks;
	}

	public boolean isAvailable(Date start, Date end) {
		boolean isAvailable = true;
		for (Task taskInCache : tasksCache) {
			if (taskInCache instanceof TimedTask) {
				TimedTask timedTask = (TimedTask) taskInCache;
				if (timedTask.isBusy()
						&& !(timedTask.getStart().after(end) || timedTask.getEnd().before(
								start))) {
					isAvailable = false;
					return isAvailable;
				}
			}
		}
		return isAvailable;
	}

	public Task retrieveBusyTask(Date start, Date end) {
		Task busyTask = null;
		for (Task taskInCache : tasksCache) {
			if (taskInCache instanceof TimedTask) {
				TimedTask timedTask = (TimedTask) taskInCache;
				if (timedTask.isBusy()
						&& !(timedTask.getStart().after(end) || timedTask.getEnd().before(
								start))) {
					busyTask = timedTask;
					return busyTask;
				}
			}
		}
		return busyTask;
	}

}
