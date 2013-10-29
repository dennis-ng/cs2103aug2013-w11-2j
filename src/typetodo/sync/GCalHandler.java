package typetodo.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private final com.google.api.services.calendar.Calendar gCalendarClient;
	private final com.google.api.services.tasks.Tasks gTasksClient;
	private String gCalendarId;
	private String gTaskListId;
	
	public GCalHandler() {
		authenticator = new GCalAuthenticator("TypeToDo");
		gCalendarClient = authenticator.getClient();
		gTasksClient = authenticator.getTasksClient();
		
		if ((gCalendarId = getTypeToDoCalendarId()) == null) { //TypeToDo calendar does not exist
			gCalendarId = addTypeToDoCalendar();
		}
		
		gTaskListId = this.getTaskListId();
	}
	
	/**
	 * Adds a task into the Google System.
	 * @param taskToBeAdded Task to be added
	 * @throws IOException 
	 */
	public void addTask(Task taskToBeAdded) throws IOException {
		if (taskToBeAdded instanceof TimedTask) {
			Event event = Converter.timedTaskToEvent((TimedTask) taskToBeAdded);
			Event result = gCalendarClient.events().insert(gCalendarId, event).execute();
			taskToBeAdded.setGoogleId(result.getId()); //append GCal's id to task
		}
		else if(taskToBeAdded instanceof DeadlineTask) {
			Event event = Converter.deadlineTaskToGoogleEvent((DeadlineTask) taskToBeAdded);
			Event result = gCalendarClient.events().insert(gCalendarId, event).execute();
			taskToBeAdded.setGoogleId(result.getId()); //append GCal's id to task
		}
		else if(taskToBeAdded instanceof FloatingTask) {
			com.google.api.services.tasks.model.Task googleTask = 
					Converter.floatingTaskToGoogleTask((FloatingTask) taskToBeAdded);

			com.google.api.services.tasks.model.Task result = 
					gTasksClient.tasks().insert(gTaskListId, googleTask).execute();

			taskToBeAdded.setGoogleId(result.getId());
		}
	}

	/**
	 * Deletes a task from the Google System.
	 * @param taskToBeDeleted Task to be deleted
	 * @throws IOException if task to be deleted does not exist in the Google System
	 */
	public void deleteTask(Task taskToBeDeleted) throws IOException {
		String googleId = taskToBeDeleted.getGoogleId();

		if (googleId == null) {
			throw new NullPointerException("GoogleId is missing, task cannot be deleted.");
		}

		//Case 1: If deadline/timed task, delete from Google Calendar
		if (taskToBeDeleted instanceof DeadlineTask || taskToBeDeleted instanceof TimedTask) { 

			try {
				gCalendarClient.events().delete(gCalendarId, googleId).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException("\"" + taskToBeDeleted.getTitle() + "\" does not exist in Google Calendar");
			}
		//Case 2: If floating task, delete from Google Tasks
		} else if (taskToBeDeleted instanceof FloatingTask) { 

			try {
				gTasksClient.tasks().delete(gTaskListId, googleId).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IOException("\"" + taskToBeDeleted.getTitle() + "\" does not exist in Google Tasks");
			}

		}
	}

	/**
	 * Returns all Tasks found in the Google System
	 * @return Returns an ArrayList of tasks found in the Google System
	 */
	public ArrayList<Task> retrieveAllTasks() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		//1. Extract Deadline/Timed Tasks from Google Calendar
		Events feed = null;
		try {
			//Get all tasks in TypeToDo Calendar
			feed = gCalendarClient.events().list(gCalendarId).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Event event : feed.getItems()) {
			if (!event.getStatus().equals("cancelled")) {
				tasks.add(Converter.googleEventToTask(event));
			}
		}

		//2. Extract floating tasks from Google Tasks
		List<com.google.api.services.tasks.model.Task> taskList = null;
		//TODO:
		try {
			taskList = gTasksClient.tasks().list(gTaskListId).execute().getItems();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (com.google.api.services.tasks.model.Task googleTask : taskList) {
				if ((googleTask.getDeleted() != null) && !googleTask.getDeleted()) {
					tasks.add(Converter.googleTaskToTask(googleTask));
				}
				if (googleTask.getDeleted() == null) {
					tasks.add(Converter.googleTaskToTask(googleTask));
				}
		}
		
		return tasks;
	}
	
	/**
	 * Retrieves and return equivalent Task from the Google System
	 * @param task Local Task
	 * @return Returns the equivalent Task from the Google System
	 */
	public Task retrieveTask(Task task) {
		if (task.getGoogleId() == null) {
			return null;
		}

		if (task instanceof TimedTask || task instanceof DeadlineTask) { //check google calendar
			try {
				Event event = gCalendarClient.events().get(gCalendarId, task.getGoogleId()).execute();

				if (event == null) {
					return null;
				} 
				
				if (event.getStatus().equals("cancelled")) {
					return null;
				}
				
				return Converter.googleEventToTask(event);	
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (task instanceof FloatingTask) { //Check google task
			
			try {
				com.google.api.services.tasks.model.Task googleTask;
				googleTask = gTasksClient.tasks().get(gTaskListId, task.getGoogleId()).execute();
				
				if (googleTask == null) {
					return null;
				}
				
				return Converter.googleTaskToTask(googleTask);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/**
	 * Updates a task in Google Calendar.
	 * @param taskToBeUpdated
	 */
	public void updateTask(Task taskToBeUpdated) {
		Event event;
		String googleId = taskToBeUpdated.getGoogleId();
		
		if (taskToBeUpdated instanceof TimedTask) {
			event = Converter.timedTaskToEvent((TimedTask) taskToBeUpdated);
			try {
				gCalendarClient.events().update(gCalendarId, googleId, event).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (taskToBeUpdated instanceof DeadlineTask) {
			event = Converter.deadlineTaskToGoogleEvent((DeadlineTask) taskToBeUpdated);
			try {
				gCalendarClient.events().update(gCalendarId, googleId, event).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (taskToBeUpdated instanceof FloatingTask) {
			com.google.api.services.tasks.model.Task googleTask = 
					Converter.floatingTaskToGoogleTask((FloatingTask) taskToBeUpdated);
			try {
				gTasksClient.tasks().update(gTaskListId, googleId, googleTask).execute();
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
			calendar = gCalendarClient.calendars().insert(calendar).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return calendar.getId();
	}
	
	private String getTypeToDoCalendarId() {
		try {
			CalendarList feed = gCalendarClient.calendarList().list().execute();
			
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

	private String getTaskListId() {
		String taskListId = null;
		try {
			taskListId = gTasksClient.tasklists().list().execute().getItems().get(0).getId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return taskListId;
	}
}

