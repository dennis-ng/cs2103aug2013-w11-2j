/**
 * 
 */
package typetodo.db;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import typetodo.logic.DeadlineTask;
import typetodo.logic.FloatingTask;

/**
 * @author DennZ
 * 
 */
public class DBHandlerTest {

	@Test
	public void test() {
		DBHandler db;
		try {
			FloatingTask floatingTask = new FloatingTask("floatingTask1", "Desc");
			FloatingTask floatingTask2 = new FloatingTask("floatingTask2", "Desc");
			Date date = new Date();
			Date deadline = new SimpleDateFormat("h:mm d-MMM yyyy", Locale.ENGLISH)
					.parse("12:00 28-OCT 2014");
			DeadlineTask deadlineTask = new DeadlineTask(1, "deadlineTask", "Desc",
					deadline);
			db = new DBHandler();
			assertEquals("Add task", 1, db.addTask(floatingTask));
			assertEquals("Add task twice", 2, db.addTask(floatingTask2));
			assertEquals("Update task", true, db.updateTask(deadlineTask));
			assertEquals("Delete", true, (db.deleteTask(1)));
			assertEquals("Delete", true, (db.deleteTask(2)));
			assertEquals("Undo deleted task", 1, (db.addTask(deadlineTask)));
			db = new DBHandler();
			// File needs to be loaded when a new DBHandler is created
			assertEquals("Retrieve", deadline, ((DeadlineTask) db.retrieveList(date)
					.get(0)).getDeadline());
			assertEquals("Delete the deadline task", true, (db.deleteTask(1)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
