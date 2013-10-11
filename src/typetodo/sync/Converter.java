package typetodo.sync;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import typetodo.logic.DeadlineTask;
import typetodo.logic.FloatingTask;
import typetodo.logic.Task;
import typetodo.logic.TimedTask;

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
		System.out.println(jodaDateTime.toString());
		DateTime googleDateTime = DateTime.parseRfc3339(jodaDateTime.toString());
		return googleDateTime;
	}

	public static Event timedTaskToEvent(TimedTask task) {
		Event event = new Event();
		event.setSummary(task.getName());
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
		
		String name = task.getName();
		String description = task.getDescription();
		
		googleTask.setTitle(name);
		googleTask.setNotes(description);
			
		return googleTask;
	}
	
	public static com.google.api.services.tasks.model.Task DeadlineTaskToGoogleTask(DeadlineTask task) {
		com.google.api.services.tasks.model.Task googleTask = 
				new com.google.api.services.tasks.model.Task();
		
		String name = task.getName();
		String description = task.getDescription();
		DateTime deadline = toGoogleDateTime(((DeadlineTask) task).getDeadline());
		
		googleTask.setTitle(name);
		googleTask.setNotes(description);
		googleTask.setDue(deadline);
			
		return googleTask;
	}
	
	public static Task EventToTask(Event event) {
		Task task = null;
		
		String name = event.getSummary();
		String description = event.getDescription();
		
		org.joda.time.DateTime start = toJodaDateTime(event.getStart().getDateTime());
		org.joda.time.DateTime end = toJodaDateTime(event.getEnd().getDateTime());
		
		boolean isBusy = false;
		if (event.getTransparency().equals("opaque")) {
			isBusy = true;
		}
		else if (event.getTransparency().equals("transparent")) {
			isBusy = false;
		}
		
		if (start.isEqual(end)) {
			task = new DeadlineTask(name, description, end);
		}
		if (!start.isEqual(end)) {
			task = new TimedTask(name, description, start, end, isBusy);
		}
		
		return task;
	}
}
