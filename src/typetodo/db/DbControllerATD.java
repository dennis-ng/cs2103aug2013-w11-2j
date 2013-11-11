// @author A0097968Y
package typetodo.db;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import typetodo.model.FloatingTask;

public class DbControllerATD {
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
			/**
			 * Test for property name: Method is not tested extensively and max length
			 * string is not tested as Java's implementation of HashMap is assumed to
			 * be well-tested.
			 * 
			 */
			final String dummyProperty = "PROPERTY";
			// Boundary case for 'empty' property name partition
			db.setProperty("", dummyProperty);
			assertEquals("Empty property name in memory", dummyProperty,
					db.getProperty(""));
			db.reloadAllFiles();
			assertEquals("Empty property name in file", dummyProperty,
					db.getProperty(""));

			// Boundary cases for property name of 'positive length' partition
			// property name of length 1
			db.setProperty("1", dummyProperty);
			assertEquals("property name of size 1 in memory", dummyProperty,
					db.getProperty("1"));
			db.reloadAllFiles();
			assertEquals("property name of size 1 in file", dummyProperty,
					db.getProperty("1"));
			// property name of length 2
			db.setProperty("02", dummyProperty);
			assertEquals("property name of size 2 in memory", dummyProperty,
					db.getProperty("02"));
			db.reloadAllFiles();
			assertEquals("property name of size 2 in file", dummyProperty,
					db.getProperty("02"));

			/**
			 * Test for property string: Method is not tested extensively and max
			 * length string is not tested as Java's implementation of HashMap is
			 * assumed to be well-tested.
			 */
			// Boundary case for 'empty' property String partition
			final String nameEmpty = "EMPTY";
			db.setProperty(nameEmpty, new String());
			assertEquals("Empty property in memory", "", db.getProperty(nameEmpty));
			db.reloadAllFiles();
			assertEquals("Empty property in file", "", db.getProperty(nameEmpty));

			// Boundary case for property String of 'positive length' partition
			// property String of length of a datetime
			final String nameDateTime = "DATETIME";
			final DateTime timeProperty = new DateTime(
					"2013-01-01T00:00:00.000+08:00");
			db.setProperty(nameDateTime, timeProperty.toString());
			assertEquals("DateTime property in memory.", timeProperty.toString(),
					db.getProperty(nameDateTime));
			db.reloadAllFiles();
			assertEquals("DateTime property in file", timeProperty.toString(),
					db.getProperty(nameDateTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void TaskHandlingTest() {
		DbController db;
		try {
			FloatingTask floatingTask1 = new FloatingTask("floatingTask1", "Desc");
			FloatingTask floatingTask2 = new FloatingTask("floatingTask2", "Desc");
			FloatingTask floatingTask3 = new FloatingTask("floatingTask3", "Desc");
			db = DbController.getInstance();
			/**
			 * Test for adding first 3 tasks and the generation of taskId
			 */
			// There is only 1 partition
			assertEquals("taskId 1 not generated", 1, db.addTask(floatingTask1));
			assertEquals("taskId 2 not generated", 2, db.addTask(floatingTask2));
			assertEquals("taskId 3 not generated", 3, db.addTask(floatingTask3));

			/**
			 * Test for retrievals and deletion when there are 3 tasks in the database
			 */
			// Boundary cases for retrieval of 'taskId larger than largest taskId in
			// database' partition
			assertEquals("retrieve of id greater by 1", null, db.getTask(4));
			assertEquals("retrieve of id greater by 2", null, db.getTask(5));
			assertEquals("retrieve of id of value max int", null,
					db.getTask(Integer.MAX_VALUE));
			// Boundary case for retrieval of 'taskId smaller than smallest' partition
			assertEquals("retrieve of id 0", null, db.getTask(0));
			// Boundary cases for retrieval of 'negative taskId' partition
			assertEquals("retrieve of id -1", null, db.getTask(-1));
			assertEquals("retrieve of id -2", null, db.getTask(-2));
			assertEquals("retrieve of id -3", null, db.getTask(-3));
			// Boundary cases for retrieval of 'taskId in database' partition
			assertEquals("retrieve of id 1", floatingTask1, db.getTask(1));
			assertEquals("retrieve of id 2", floatingTask2, db.getTask(2));
			assertEquals("retrieve of id 3", floatingTask3, db.getTask(3));
			// Boundary cases for deletion of 'taskId larger than the largest taskId
			// in database' partition
			assertEquals("deletion of id greater by 1", false, db.deleteTask(4));
			assertEquals("deletion of id greater by 2", false, db.deleteTask(5));
			assertEquals("deletion of id of value max int", false,
					db.deleteTask(Integer.MAX_VALUE));
			// Boundary case for deletion of 'taskId smaller than smallest' partition
			assertEquals("deletion of id 0", false, db.deleteTask(0));
			// Boundary cases for deletion of 'negative taskId' partition
			assertEquals("deletion of id -1", false, db.deleteTask(-1));
			assertEquals("deletion of id -2", false, db.deleteTask(-2));
			assertEquals("deletion of id -3", false, db.deleteTask(-3));
			// Boundary case for deletion of 'taskId in database' partition
			assertEquals("deletion of id 1", true, db.deleteTask(1));

			/**
			 * Test for retrievals and deletion when there are 2 tasks in the database
			 */
			// Boundary cases for retrieval of 'taskId larger than largest taskId in
			// database' partition
			assertEquals("retrieve of id greater by 1", null, db.getTask(4));
			assertEquals("retrieve of id greater by 2", null, db.getTask(5));
			assertEquals("retrieve of id of value max int", null,
					db.getTask(Integer.MAX_VALUE));
			// Boundary case for retrieval of 'taskId smaller than smallest' partition
			assertEquals("retrieve of id 1", null, db.getTask(1));
			assertEquals("retrieve of id 0", null, db.getTask(0));
			// Boundary cases for retrieval of 'negative taskId' partition
			assertEquals("retrieve of id -1", null, db.getTask(-1));
			assertEquals("retrieve of id -2", null, db.getTask(-2));
			assertEquals("retrieve of id -3", null, db.getTask(-3));
			// Boundary cases for retrieval of 'taskId in database' partition
			assertEquals("retrieve of id 2", floatingTask2, db.getTask(2));
			assertEquals("retrieve of id 3", floatingTask3, db.getTask(3));
			// Boundary cases for deletion of 'taskId larger than the largest taskId
			// in database' partition
			assertEquals("deletion of id greater by 1", false, db.deleteTask(4));
			assertEquals("deletion of id greater by 2", false, db.deleteTask(5));
			assertEquals("deletion of id of value max int", false,
					db.deleteTask(Integer.MAX_VALUE));
			// Boundary case for deletion of 'taskId smaller than smallest' partition
			assertEquals("deletion of id 1", false, db.deleteTask(1));
			assertEquals("deletion of id 0", false, db.deleteTask(0));
			// Boundary cases for deletion of 'negative taskId' partition
			assertEquals("deletion of id -1", false, db.deleteTask(-1));
			assertEquals("deletion of id -2", false, db.deleteTask(-2));
			assertEquals("deletion of id -3", false, db.deleteTask(-3));
			// Boundary case for deletion of 'taskId in database' partition
			assertEquals("deletion of id 3", true, db.deleteTask(3));

			/**
			 * Test for retrievals and deletion when there is only taskId 2 in the
			 * database
			 */
			// Boundary cases for retrieval of 'taskId larger than largest taskId in
			// database' partition
			assertEquals("retrieve of id greater by 1", null, db.getTask(3));
			assertEquals("retrieve of id greater by 2", null, db.getTask(4));
			assertEquals("retrieve of id of value max int", null,
					db.getTask(Integer.MAX_VALUE));
			// Boundary case for retrieval of 'taskId smaller than smallest' partition
			assertEquals("retrieve of id 1", null, db.getTask(1));
			assertEquals("retrieve of id 0", null, db.getTask(0));
			// Boundary cases for retrieval of 'negative taskId' partition
			assertEquals("retrieve of id -1", null, db.getTask(-1));
			assertEquals("retrieve of id -2", null, db.getTask(-2));
			assertEquals("retrieve of id -3", null, db.getTask(-3));
			// Boundary cases for retrieval of 'taskId in database' partition
			assertEquals("retrieve of id 2", floatingTask2, db.getTask(2));
			// Boundary cases for deletion of 'taskId larger than the largest taskId
			// in database' partition
			assertEquals("deletion of id greater by 1", false, db.deleteTask(3));
			assertEquals("deletion of id greater by 2", false, db.deleteTask(4));
			assertEquals("deletion of id of value max int", false,
					db.deleteTask(Integer.MAX_VALUE));
			// Boundary case for deletion of 'taskId smaller than smallest' partition
			assertEquals("deletion of id 1", false, db.deleteTask(1));
			assertEquals("deletion of id 0", false, db.deleteTask(0));
			// Boundary cases for deletion of 'negative taskId' partition
			assertEquals("deletion of id -1", false, db.deleteTask(-1));
			assertEquals("deletion of id -2", false, db.deleteTask(-2));
			assertEquals("deletion of id -3", false, db.deleteTask(-3));
			// Boundary case for deletion of 'taskId in database' partition
			assertEquals("deletion of id 2", true, db.deleteTask(2));

			/**
			 * Test for retrievals and deletion when all tasks are deleted from the
			 * database
			 */
			// Boundary cases for retrieval of 'taskId of positive value' partition
			assertEquals("retrieve of id 1", null, db.getTask(1));
			assertEquals("retrieve of id 2", null, db.getTask(2));
			assertEquals("retrieve of id of value max int", null,
					db.getTask(Integer.MAX_VALUE));
			// Boundary case for retrieval of 'taskId == 0' partition
			assertEquals("retrieve of id 0", null, db.getTask(0));
			// Boundary cases for retrieval of 'negative taskId' partition
			assertEquals("retrieve of id -1", null, db.getTask(-1));
			assertEquals("retrieve of id -2", null, db.getTask(-2));
			assertEquals("retrieve of id -3", null, db.getTask(-3));
			// Boundary cases for deletion of 'taskId of positive value' partition
			assertEquals("deletion of id 1", false, db.deleteTask(1));
			assertEquals("deletion of id 2", false, db.deleteTask(2));
			assertEquals("deletion of id of value max int", false,
					db.deleteTask(Integer.MAX_VALUE));
			// Boundary case for deletion of 'taskId == 0' partition
			assertEquals("deletion of id 0", false, db.deleteTask(0));
			// Boundary cases for deletion of 'negative taskId' partition
			assertEquals("deletion of id -1", false, db.deleteTask(-1));
			assertEquals("deletion of id -2", false, db.deleteTask(-2));
			assertEquals("deletion of id -3", false, db.deleteTask(-3));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
