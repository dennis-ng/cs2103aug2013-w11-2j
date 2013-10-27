package typetodo.sync;

import java.io.IOException;

import org.joda.time.DateTime;

import typetodo.model.DeadlineTask;
import typetodo.model.FloatingTask;
import typetodo.model.Task;
import typetodo.model.TimedTask;

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
			task.setGoogleId(result.getId()); //append GCal's id to task
		}
		else if(task instanceof DeadlineTask) {
			Event event = Converter.deadlineTaskToGoogleEvent((DeadlineTask) task);
			Event result = calendarClient.events().insert(calendarId, event).execute();
			task.setGoogleId(result.getId()); //append GCal's id to task
		}
		else if(task instanceof FloatingTask) {
			com.google.api.services.tasks.model.Task googleTask = 
					Converter.floatingTaskToGoogleTask((FloatingTask) task);
			
			com.google.api.services.tasks.model.Task result = 
					tasksClient.tasks().insert(taskListId, googleTask).execute();
			
			task.setGoogleId(result.getId());
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
		if (task.getGoogleId() == null) {
			return false;
		}

		if (task instanceof TimedTask || task instanceof DeadlineTask) { //check google calendar
			try {
				Event event = calendarClient.events().get(calendarId, task.getGoogleId()).execute();

				if ((!event.equals(null)) && (!event.getStatus().equals("cancelled"))){
					return true;
				} else {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (task instanceof FloatingTask) { //Check google task
			try {
				if (!tasksClient.tasks().get(taskListId, task.getGoogleId()).execute().equals(null)) {
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
	}


	/**
	 * Returns a task to update if task mod date is before google event/task updated mod
	 * @param task
	 * @return
	 */
	public Task getUpdate(Task task) {
		Event googleEvent;
		if (task.getGoogleId() == null) {
			return null;
		}

		try {
			if (task instanceof TimedTask || task instanceof DeadlineTask) {
				if (calendarClient.events().get(calendarId, task.getGoogleId()) != null){
					googleEvent = calendarClient.events().get(calendarId, task.getGoogleId()).execute();
					DateTime gcEventModifiedDate = Converter.toJodaDateTime(googleEvent.getUpdated());
					DateTime taskModifiedDate = task.getDateModified();

					if (gcEventModifiedDate.isAfter(taskModifiedDate)) {
						return Converter.googleEventToTask(googleEvent);
					}
					else if (gcEventModifiedDate.isBefore(taskModifiedDate)) {
						this.updateTaskInGCal(task);
						return null;
					}
				}
			}
			else if (task instanceof FloatingTask) {
				com.google.api.services.tasks.model.Task googleTask;
				googleTask = tasksClient.tasks().get(taskListId, task.getGoogleId()).execute();
				if (!googleTask.equals(null)) {
					DateTime gcTaskModifiedDate = Converter.toJodaDateTime(googleTask.getUpdated());
					DateTime taskModifiedDate = task.getDateModified();

					if (gcTaskModifiedDate.isAfter(taskModifiedDate)) {
						return Converter.googleTaskToTask(googleTask);
					}
					else if (gcTaskModifiedDate.isBefore(taskModifiedDate)) {
						this.updateTaskInGCal(task);
						return null;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}
	
	public void updateTaskInGCal(Task task) {
		Event event;
		String googleId = task.getGoogleId();
		
		if (task instanceof TimedTask) {
			event = Converter.timedTaskToEvent((TimedTask) task);
			try {
				calendarClient.events().update(calendarId, googleId, event).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (task instanceof DeadlineTask) {
			event = Converter.deadlineTaskToGoogleEvent((DeadlineTask) task);
			try {
				calendarClient.events().update(calendarId, googleId, event).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (task instanceof FloatingTask) {
			com.google.api.services.tasks.model.Task googleTask = 
					Converter.floatingTaskToGoogleTask((FloatingTask) task);
			try {
				tasksClient.tasks().update(taskListId, googleId, googleTask).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			//TODO:
		}
		
		return null;
	}
	
}
