package typetodo.sync;

import java.io.IOException;

import typetodo.logic.DeadlineTask;
import typetodo.logic.FloatingTask;
import typetodo.logic.Task;
import typetodo.logic.TimedTask;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class GCalHandler {
	private GCalAuthenticator authenticator;
	private final com.google.api.services.calendar.Calendar client;
	private String calendarId;
	
	public GCalHandler() {
		authenticator = new GCalAuthenticator("TypeToDo");
		client = authenticator.getClient();
		
		if ((calendarId = hasTypeToDoCalendar()) == null) { //TypeToDo calendar does not exist
			calendarId = addTypeToDoCalendar();
		}
	}
	
	public void addTaskToGCal(Task task) {
		Event event = new Event();
		
		event.setSummary(task.getName()); //sets the title
		event.setDescription(task.getDescription());
		
		if (task instanceof FloatingTask) {
			
		}
		else if (task instanceof TimedTask) {
			
		}
		else if (task instanceof DeadlineTask) {
			
		}	
	}
	
	public void getAllTasksFromGCal() {
		try {
			//Get all tasks in TypeToDo Calendar
			Events feed = client.events().list(calendarId).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Task convertToTask(Event event) {
		String title = event.getSummary();
		String description = event.getDescription();
		
		DateTime start = event.getStart().getDateTime();
		DateTime end = event.getEnd().getDateTime();
		
		boolean isBusy;
		// PLS COMPLETE
		return null;
	}
	
	
	private String addTypeToDoCalendar() {
		com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
		calendar.setSummary("TypeToDo");
		try {
			calendar = client.calendars().insert(calendar).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return calendar.getId();
	}
	
	private String hasTypeToDoCalendar() {
		try {
			CalendarList feed = client.calendarList().list().execute();
			
			if (feed.isEmpty()) {
				return null;
			}
			
			for (CalendarListEntry entry : feed.getItems()) { //go through all calendars
				if (entry.getSummary().equals("TypeToDo")) {
					return entry.getId();
				}
				
			}
			
		} catch (Exception e) {
			//handle
		}
		
		return null;
	}
	
}
