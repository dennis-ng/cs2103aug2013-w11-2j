package typetodo.sync;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import typetodo.model.DeadlineTask;
import typetodo.model.FloatingTask;
import typetodo.model.Task;
import typetodo.model.TimedTask;
import typetodo.model.Task.Status;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

/**
 * The syncHelper is to act as a translator between the Google and Local Schedule. It provides methods to convert 
 * Google DateTime to Joda DateTime, Events to local tasks and vice versa.
 * @author A0091024U
 *
 */
public class SyncHelper {
	private static final String DATE_FORMAT_RFC3339 = "yyyy-MM-dd'T'H:mm:ss.SSSZZ";
	
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_FORMAT_RFC3339);
	
	/**
	 * Converts Google's DateTime format into Joda's DateTime format.
	 * @param googleDateTime Google's DateTime format
	 * @return returns the equivalent Joda's DateTime format
	 */
	public static org.joda.time.DateTime toJodaDateTime(DateTime googleDateTime) {
		org.joda.time.DateTime jodaDateTime = fmt.parseDateTime(googleDateTime.toStringRfc3339());
		return jodaDateTime;
	}
	
	/**
	 * Converts Joda's DateTime format into Google's DateTime format.
	 * @param jodaDateTime Joda's DateTime format
	 * @return returns the equivalent Google's DateTime format
	 */
	public static DateTime toGoogleDateTime(org.joda.time.DateTime jodaDateTime) {
		//System.out.println(jodaDateTime.toString());
		DateTime googleDateTime = DateTime.parseRfc3339(jodaDateTime.toString());
		return googleDateTime;
	}

	/**
	 * Converts the given local FloatingTask into a Google Task.
	 * @param task FloatingTask
	 * @return returns the equivalent Google Task of the Floating Task
	 */
	public static com.google.api.services.tasks.model.Task floatingTaskToGoogleTask(FloatingTask task) {
		com.google.api.services.tasks.model.Task googleTask = 
				new com.google.api.services.tasks.model.Task();
		
		String name = task.getTitle();
		String description = task.getDescription();
		String googleId = task.getGoogleId();
		
		googleTask.setTitle(name);
		googleTask.setNotes(description);
		
		if(googleId != null) {
			googleTask.setId(googleId);
		}
			
		return googleTask;
	}
	
	/**
	 * Converts the given local DeadlineTask into a Google event.
	 * @param task DeadlineTask
	 * @return returns the equivalent Google Event of the DeadlineTask
	 */
	public static Event deadlineTaskToGoogleEvent(DeadlineTask task) {
		Event googleEvent = new Event();
		String name = "";
		String description = "";
		
		name = task.getTitle();
		description = task.getDescription();
		DateTime deadline = toGoogleDateTime(((DeadlineTask) task).getDeadline());
		
		googleEvent.setSummary(name);
		googleEvent.setDescription(description);
		googleEvent.setStart(new EventDateTime().setDateTime(deadline));
		googleEvent.setEnd(new EventDateTime().setDateTime(deadline));

		return googleEvent;
	}
	
	/**
	 * Converts the given local TimedTask into a Google Event.
	 * @param task TimedTask
	 * @return returns the equivalent Google Event of the TimedTask
	 */
	public static Event timedTaskToGoogleEvent(TimedTask task) {
		Event event = new Event();
		event.setSummary(task.getTitle());
		event.setDescription(task.getDescription());

		DateTime start = toGoogleDateTime(((TimedTask) task).getStart());
		DateTime end = toGoogleDateTime(((TimedTask) task).getEnd());

		event.setStart(new EventDateTime().setDateTime(start));
		event.setEnd(new EventDateTime().setDateTime(end));
		
		return event;
	}
	
	/**
	 * Converts the given Google event to either a DeadlineTask or a TimedTask.
	 * @param event Google Event
	 * @return returns the equivalent local Task of the Google Event
	 */
	public static Task googleEventToTask(Event event) {
		Task task = null;
		String name = "";
		String description = "";
		String googleId = "";
		
		name = event.getSummary();
		description = event.getDescription();
		googleId = event.getId();
		
		org.joda.time.DateTime dateModified = toJodaDateTime(event.getUpdated());
		org.joda.time.DateTime start = toJodaDateTime(event.getStart().getDateTime());
		org.joda.time.DateTime end = toJodaDateTime(event.getEnd().getDateTime());
		
		if (description ==  null) {
			description = "";
		}
		
		if (start.isEqual(end)) {
			task = new DeadlineTask(name, description, end);
		}
		if (!start.isEqual(end)) {
			task = new TimedTask(name, description, start, end);
		}
		
		task.setGoogleId(googleId);
		task.setDateModified(dateModified);
		
		return task;
	}
	
	/**
	 * Converts the given Google Task into a local FloatingTask
	 * @param googleTask Google Task
	 * @return returns the equivalent local FloatingTask of the Google Task
	 */
	public static Task googleTaskToFloatingTask(com.google.api.services.tasks.model.Task googleTask) {
		Task task = null;
		String name = "";
		String description = "";
		String googleId = "";
		
		name = googleTask.getTitle();
		description = googleTask.getNotes();
		googleId = googleTask.getId();
		
		if (description == null) {
			description = "";
		}
		
		org.joda.time.DateTime dateModified = toJodaDateTime(googleTask.getUpdated());
		
		task = new FloatingTask(name, description);
		task.setGoogleId(googleId);
		task.setDateModified(dateModified);
		
		if (googleTask.getCompleted() != null) {
			task.setStatus(Status.COMPLETED);
		}
		
		return task;
	}
}
