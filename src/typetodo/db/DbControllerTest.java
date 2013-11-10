// @author A0097968Y
package typetodo.db;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import typetodo.model.DeadlineTask;
import typetodo.model.FloatingTask;

public class DbControllerTest {
	// Always start with a clean state

	@Test
	public void test2() {
		DbController db;
		try {
			db = DbController.getInstance();
			assertEquals("Check property", "Date", db.getProperty("Test"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
