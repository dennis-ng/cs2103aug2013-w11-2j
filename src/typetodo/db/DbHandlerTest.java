/**
 * 
 */
package typetodo.db;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import typetodo.model.DeadlineTask;
import typetodo.model.FloatingTask;

/**
 * @author DennZ
 * 
 */
public class DbHandlerTest {

	@Test
	public void test() {
		DbHandler db;
		try {
			FloatingTask floatingTask = new FloatingTask("floatingTask1", "Desc");
			FloatingTask floatingTask2 = new FloatingTask("floatingTask2", "Desc");
			DateTime date = new DateTime();
			DateTimeFormatter fmt = DateTimeFormat.forPattern("h:mm d-MMM yyyy")
					.withLocale(Locale.ENGLISH);
			DateTime deadline = fmt.parseDateTime("12:00 28-OCT 2014");

			DeadlineTask deadlineTask = new DeadlineTask(1, "deadlineTask", "Desc",
					deadline);
			db = DbHandler.getInstance();
			assertEquals("Add task", 1, db.addTask(floatingTask));
			assertEquals("Add task twice", 2, db.addTask(floatingTask2));
			assertEquals("Update task", true, db.updateTask(deadlineTask));
			assertEquals("Delete", true, (db.deleteTask(1)));
			assertEquals("Delete", true, (db.deleteTask(2)));
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
