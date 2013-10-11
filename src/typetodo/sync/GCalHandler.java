package typetodo.sync;

import java.io.IOException;

import typetodo.logic.DeadlineTask;
import typetodo.logic.FloatingTask;
import typetodo.logic.Task;
import typetodo.logic.TimedTask;

import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class GCalHandler {
	private GCalAuthenticator authenticator;
	private final com.google.api.services.calendar.Calendar calendarClient;
	private final com.google.api.services.tasks.Tasks tasksClient;
	private String calendarId;
	private String taskListId;
	
	public GCalHandler() {
		authenticator = new GCalAuthenticator("TypeToDo");
		calendarClient = authenticator.getClient();
		tasksClient = authenticator.getTasksClient();
		
		if ((calendarId = hasTypeToDoCalendar()) == null) { //TypeToDo calendar does not exist
			calendarId = addTypeToDoCalendar();
		}
		
		try {
			System.out.println("Accessing Your Tasklist..");
			taskListId = tasksClient.tasklists().list().execute().getItems().get(0).getId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addTaskToGCal(Task task) throws IOException {
		if (task instanceof TimedTask) {
			Event event = Converter.timedTaskToEvent((TimedTask) task);
			Event result = calendarClient.events().insert(calendarId, event).execute();
			task.setGoogleCalendarEventId(result.getId()); //append GCal's id to task
		}
		else if(task instanceof DeadlineTask) {
			com.google.api.services.tasks.model.Task googleTask = 
					Converter.DeadlineTaskToGoogleTask((DeadlineTask) task);
			
			com.google.api.services.tasks.model.Task result = 
					tasksClient.tasks().insert(taskListId, googleTask).execute();
			
			task.setGoogleCalendarEventId(result.getId());
		}
		else if(task instanceof FloatingTask) {
			com.google.api.services.tasks.model.Task googleTask = 
					Converter.floatingTaskToGoogleTask((FloatingTask) task);
			
			com.google.api.services.tasks.model.Task result = 
					tasksClient.tasks().insert(taskListId, googleTask).execute();
			
			task.setGoogleCalendarEventId(result.getId());
		}
	}
	
	public Events getAllTasksFromGCal() {
		try {
			//Get all tasks in TypeToDo Calendar
			Events feed = calendarClient.events().list(calendarId).execute();
			return feed;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean hasTask(Task task) {
		if (task instanceof TimedTask) {
			if (task.getGoogleId() != null) {
				try {
					if(task.getGoogleId() != null) {
						if (calendarClient.events().get(calendarId, task.getGoogleId()).execute() != null){
							return true;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			try {
				if(task.getGoogleId() != null) {
					if (!tasksClient.tasks().get(taskListId, task.getGoogleId()).execute().equals(null)) {
						return true;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	
	public boolean hasDifferences(Task task) {
		Event event;
		if (task.getGoogleId() != null) {
			try {
				if ((event = calendarClient.events().get(calendarId, task.getGoogleId()).execute()) != null){
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	private String addTypeToDoCalendar() {
		com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
		calendar.setSummary("TypeToDo");
		try {
			calendar = calendarClient.calendars().insert(calendar).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return calendar.getId();
	}
	
	private String hasTypeToDoCalendar() {
		try {
			CalendarList feed = calendarClient.calendarList().list().execute();
			
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
