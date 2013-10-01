package typetodo.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import com.sun.jmx.snmp.tasks.Task;

public class DBHandler {

	public DBHandler(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		PrintWriter writer = new PrintWriter(new FileWriter(fileName));
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

	public String addTask(DeadLineTask task) {
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
