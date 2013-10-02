package typetodo.db;

import java.io.IOException;
import java.util.Date;

import typetodo.logic.Schedule.DeadlineTask;
import typetodo.logic.Schedule.FloatingTask;
import typetodo.logic.Schedule.Task;
import typetodo.logic.Schedule.TimedTask;

public class DBHandler {

	public static final String FILENAME = "file";

	public DBHandler() throws IOException {
		// BufferedReader reader = new BufferedReader(new FileReader(FILENAME));
		// PrintWriter writer = new PrintWriter(new FileWriter(FILENAME));
	}

	private static final String MESSAGE_ADD = "Task successfully added.";
	private static final String MESSAGE_DELETE = "Task successfully deleted.";
	private static final String MESSAGE_UPDATE = "Task successfully updated.";

	public String addTask(FloatingTask task) {
		return MESSAGE_ADD;
	}

	public String addTask(TimedTask task) {
		return MESSAGE_ADD;
	}

	public String addTask(DeadlineTask task) {
		return MESSAGE_ADD;
	}

	public String deleteTask(Task task) {
		return MESSAGE_DELETE;
	}

	public String updateTask(Task task) {
		return MESSAGE_UPDATE;
	}

	public String retrieveList(Date day) {
		return "Tasks for " + day.toString();
	}

	public String retrieveContains(String searchCriteria) {
		return "Search results";
	}

}
