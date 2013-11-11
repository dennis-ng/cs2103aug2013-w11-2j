package typetodo.logic;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Test;

import typetodo.model.TimedTask;

/**
 * 
 * @author A0091024U
 *
 */
public class ScheduleATD {
	private static final String ERROR_MESSAGE_INVALID_ID = "ID does not exist in schedule";
	private static final String ERROR_MESSAGE_INVALID_FIELD = "Invalid field name";
	private static final String ERROR_MESSAGE_INVALID_DEADLINE_ATTRIBUTE = "This type of task does not support a deadline";
	private static final String ERROR_MESSAGE_INVALID_START_ATTRIBUTE = "This type of task does not support a start date";
	private static final String ERROR_MESSAGE_INVALID_END_ATTRIBUTE = "This type of task does not support a end date";
	private static final String ERROR_MESSAGE_INVALID_DATE_START_AFTER_END = "Start date cannot be after end date";
	private static final String ERROR_MESSAGE_INVALID_DATE_RANGE = "Invalid date range";
	private static final String ERROR_MESSAGE_MISSING_TITLE = "Task must have a title";
	private static final String ERROR_MESSAGE_MISSING_DEADLINE = "A Deadline Task must have deadline";
	private static final String ERROR_MESSAGE_MISSING_DATES = "Missing start/end date(s)";
	
	//Boundary cases for adding of timed tasks with 'either start/end date is null' partition//
	@Test
	public void AddingTimedTaskWithMissingDates() {
		Schedule schedule = null;
		TimedTask timeTask1 = new TimedTask("Timed tasks with null dates", "description", null, new DateTime());
		TimedTask timeTask2 = new TimedTask("Timed tasks with null dates", "description", new DateTime(), null);
		TimedTask timeTask3 = new TimedTask("Timed tasks with null dates", "description", null, null);

		try {
			schedule = new Schedule();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//adding timed task with start date as null
		try {
			schedule.addTask(timeTask1);
		} catch (Exception expected) {
			assertEquals(expected.getMessage(), ERROR_MESSAGE_MISSING_DATES);
		}
		
		//adding timed task with end date as null
		try {
			schedule.addTask(timeTask2);
		} catch (Exception expected) {
			assertEquals(expected.getMessage(), ERROR_MESSAGE_MISSING_DATES);
		}
		
		//adding timed task with both start and end as null
		try {
			schedule.addTask(timeTask3);
		} catch (Exception expected) {
			assertEquals(expected.getMessage(), ERROR_MESSAGE_MISSING_DATES);
		}
	}
	
	//Boundary cases for adding of timed tasks with 'start after end date' partition//
	@Test
	public void AddingATimedTaskWithStartAfterEnd() {
		Schedule schedule = null;
		TimedTask timeTask1 = new TimedTask("Timed task", "description", new DateTime().plusDays(10), new DateTime());

		try {
			schedule = new Schedule();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//adding a timetask with end date after start date
		try {
			schedule.addTask(timeTask1);
		} catch (Exception expected) {
			assertEquals(expected.getMessage(), ERROR_MESSAGE_INVALID_DATE_START_AFTER_END);
		}
	}

	//Boundary cases for adding of timed tasks with 'start is before or equal end date' partition//
	public void AddingATimedTaskWithStartBeforeOrEqualEnd() {
		DateTime now = new DateTime();
		Schedule schedule = null;
		TimedTask timeTask1 = new TimedTask("Timed task", "description", now.minusDays(10), now);
		TimedTask timeTask2 = new TimedTask("Timed task", "description", now, now);

		try {
			schedule = new Schedule();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//adding timed task with start before end date
		try {
			assertEquals(1, schedule.addTask(timeTask1));
		} catch (Exception expected) {
			;
		}
		
		//adding timed task with start and end as the same date
		try {
			assertEquals(2, schedule.addTask(timeTask2));
		} catch (Exception expected) {
			;
		}
	}
}