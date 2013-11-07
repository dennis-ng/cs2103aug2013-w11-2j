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
//RFC3339 format:
public class Converter {
	//RFC3339 format:
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'H:mm:ss.SSSZZ");
	
	public static org.joda.time.DateTime toJodaDateTime(DateTime googleDateTime) {
		org.joda.time.DateTime jodaDateTime = fmt.parseDateTime(googleDateTime.toStringRfc3339());
		return jodaDateTime;
	}
	
	public static DateTime toGoogleDateTime(org.joda.time.DateTime jodaDateTime) {
		//System.out.println(jodaDateTime.toString());
		DateTime googleDateTime = DateTime.parseRfc3339(jodaDateTime.toString());
		return googleDateTime;
	}

	public static Event timedTaskToEvent(TimedTask task) {
		Event event = new Event();
		event.setSummary(task.getTitle());
		event.setDescription(task.getDescription());

		DateTime start = toGoogleDateTime(((TimedTask) task).getStart());
		DateTime end = toGoogleDateTime(((TimedTask) task).getEnd());

		event.setStart(new EventDateTime().setDateTime(start));
		event.setEnd(new EventDateTime().setDateTime(end));

		if(((TimedTask) task).isBusy()) {
			event.setTransparency("opaque");
		}
		else {
			event.setTransparency("transparent");
		}
		
		return event;
	}

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

		//TODO: set completed
		return googleEvent;
	}
	
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
		
		boolean isBusy = false;
		
		if (description ==  null) {
			description = "";
		}
		
		if (event.getTransparency() != null) {
			if (event.getTransparency().equals("opaque")) {
				isBusy = true;
			}
			/*else if (event.getTransparency().equals("transparent")) {
				isBusy = false
			}*/
		}
		
		if (start.isEqual(end)) {
			task = new DeadlineTask(name, description, end);
		}
		if (!start.isEqual(end)) {
			task = new TimedTask(name, description, start, end, isBusy);
		}
		
		task.setGoogleId(googleId);
		task.setDateModified(dateModified);
		
		return task;
	}
	
	public static Task googleTaskToTask(com.google.api.services.tasks.model.Task googleTask) {
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
