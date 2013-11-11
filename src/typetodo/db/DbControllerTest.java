// @author A0097968Y
package typetodo.db;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import typetodo.model.DeadlineTask;
import typetodo.model.FloatingTask;

public class DbControllerTest {
	/**
	 * Note: Every test assumes starting with clean state. Please make sure the
	 * savedfiles folder does not exist or the files are empty files before
	 * starting the test. This is required as the DbController needs to handle the
	 * file directly.
	 */

	@Test
	public void PropertyTest() {
		DbController db;
		try {
			db = DbController.getInstance();
			DateTime timeProperty = new DateTime("2013-01-01T00:00:00.000+08:00");
			/**
			 * Property only takes in String, max length string is not tested as
			 * Java's implementation of HashMap is assumed to be well-tested.
			 */
			// Boundary: property String length empty
			db.setProperty("Empty", new String());
			assertEquals("Empty property in memory", "", db.getProperty("Empty"));
			db.reloadAllFiles();
			assertEquals("Empty property in file", "", db.getProperty("Empty"));
			// Boundary: property String length of a datetime
			db.setProperty("DateTime", timeProperty.toString());
			assertEquals("DateTime property in memory.", timeProperty.toString(),
					db.getProperty("DateTime"));
			db.reloadAllFiles();
			assertEquals("DateTime property in file", timeProperty.toString(),
					db.getProperty("DateTime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() {
		DbController db;
		try {
			FloatingTask floatingTask = new FloatingTask("floatingTask1", "Desc");
			FloatingTask floatingTask2 = new FloatingTask("floatingTask2", "Desc");
			DateTime date = new DateTime();
			DateTimeFormatter fmt = DateTimeFormat.forPattern("h:mm d-MMM yyyy")
					.withLocale(Locale.ENGLISH);
			DateTime deadline = fmt.parseDateTime("12:00 28-OCT 2014");

			DeadlineTask deadlineTask = new DeadlineTask(1, "deadlineTask", "Desc",
					deadline);
			db = DbController.getInstance();
			assertEquals("Add task", 1, db.addTask(floatingTask));
			assertEquals("Add second task", 2, db.addTask(floatingTask2));
			// White box testing: deadlineTask with taskId 1 is used to replace the
			// same task with taskId 1. The algorithm behind the update method is
			// known.
			assertEquals("Update task", true, db.updateTask(deadlineTask));
			// Test deleting over 2 when last taskId is 2
			assertEquals("Delete a task with taskID larger than the last taskId",
					false, (db.deleteTask(3)));
			// Test deleting over 2 when last taskId is 2
			assertEquals("Delete a task with taskID larger than the last taskId",
					false, (db.deleteTask(4)));
			// Test deleting over 2 when last taskId is 2
			assertEquals("Delete a task with taskID larger than the last taskId",
					false, (db.deleteTask(5)));
			// Test deleting below 2 when last taskId is 1
			assertEquals("Delete negative number of tasks", false, (db.deleteTask(0)));
			// Test deleting below 2 when last taskId is 1
			assertEquals("Delete negative number of tasks", false,
					(db.deleteTask(-1)));
			// Test deleting below 2 when last taskId is 1
			assertEquals("Delete negative number of tasks", false,
					(db.deleteTask(-2)));
			assertEquals("Delete", true, (db.deleteTask(2)));

			// Test deleting over 1 when last taskId is 1
			assertEquals("Delete a task with taskID larger than the last taskId",
					false, (db.deleteTask(2)));
			// Test deleting over 1 when last taskId is 1
			assertEquals("Delete a task with taskID larger than the last taskId",
					false, (db.deleteTask(3)));
			// Test deleting over 1 when last taskId is 1
			assertEquals("Delete a task with taskID larger than the last taskId",
					false, (db.deleteTask(4)));

			assertEquals("Delete", true, (db.deleteTask(1)));
			assertEquals("Undo deleted task", 1, (db.addTask(deadlineTask)));
			assertEquals("Retrieve", deadline, ((DeadlineTask) db.retrieveList(date)
					.get(0)).getDeadline());
			assertEquals("Delete the deadline task", true, (db.deleteTask(1)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
