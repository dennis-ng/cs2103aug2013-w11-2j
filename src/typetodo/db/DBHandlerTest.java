/**
 * 
 */
package typetodo.db;

import static org.junit.Assert.assertEquals;

import java.util.Date;

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
			DeadlineTask deadlineTask = new DeadlineTask(1, "deadlineTask", "Desc",
					date);
			db = new DBHandler();
			assertEquals("Add task", 1, db.addTask(floatingTask));
			assertEquals("Add task twice", 2, db.addTask(floatingTask2));
			assertEquals("Update task", true, db.updateTask(deadlineTask));
			assertEquals("Retreive", date,
					((DeadlineTask) db.retrieveList(date).get(0)).getDeadline());
			assertEquals("Delete", true, (db.deleteTask(1)));
			assertEquals("Delete", true, (db.deleteTask(2)));
			assertEquals("Undo deleted task", 1, (db.addTask(deadlineTask)));
			assertEquals("Delete", true, (db.deleteTask(1)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
