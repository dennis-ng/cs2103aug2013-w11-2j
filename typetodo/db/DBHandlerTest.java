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
			FloatingTask floatingTask = new FloatingTask("Name", "Desc");
			DeadlineTask deadlineTask = new DeadlineTask(1, "Name", "Desc",
					new Date());
			db = new DBHandler();
			assertEquals("Add task", 1, db.addTask(floatingTask));
			assertEquals("Update task", true, db.updateTask(deadlineTask));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
