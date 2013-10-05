/**
 * Author : Dennis Ng
 * Email	: a0097968@nus.edu.sg
 */
package typetodo.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import typetodo.logic.Schedule.DeadlineTask;
import typetodo.logic.Schedule.FloatingTask;
import typetodo.logic.Schedule.Task;
import typetodo.logic.Schedule.TimedTask;

public class DBHandler {

	private static final String FILENAME = "file";

	public DBHandler() throws IOException {
		// TODO
		BufferedReader reader = new BufferedReader(new FileReader(FILENAME));
		PrintWriter writer = new PrintWriter(new FileWriter(FILENAME));
	}

	/**
	 * 
	 * @param task
	 * @return If successful: taskId of successfully edited task. This allows
	 *         caller to know the taskId if undo is required.
	 * @throws
	 */
	public int addTask(FloatingTask task) throws Exception {
		// TODO
		int taskId = 0;
		return taskId;
	}

	public int addTask(TimedTask task) throws Exception {
		// TODO
		int taskId = 0;
		return taskId;
	}

	public int addTask(DeadlineTask task) throws Exception {
		// TODO
		int taskId = 0;
		return taskId;
	}

	/**
	 * 
	 * @param task
	 * @return true: Deleted, false: Not found
	 */
	public boolean deleteTask(Task task) {
		return false;
		// TODO
	}

	/**
	 * 
	 * @param task
	 * @return true: Updated, false: Not found
	 * @throws Exception
	 *           : If clash with time slot
	 */
	public boolean updateTask(Task task) throws Exception {
		boolean updated = false;
		// TODO
		return updated;
	}

	/**
	 * 
	 * @param day
	 * @return An arraylist of all the tasks on this day. null will be returned if
	 *         nothing is found.
	 * @throws Exception
	 */
	public ArrayList<Task> retrieveList(Date day) {
		// TODO
		return null;
	}

	/**
	 * 
	 * @param day
	 * @return An arraylist of all the tasks that meets the searching criteria.
	 *         null will be returned if nothing is found.
	 * @throws Exception
	 */
	public ArrayList<Task> retrieveContaining(String searchCriteria) {
		// TODO
		return null;
	}

	public boolean isAvailable(Date start, Date end) {
		// TODO
		return true;
	}

	public ArrayList<Task> retrieveBusyTasks(Date start, Date end) {
		// TODO
		return null;
	}
}
