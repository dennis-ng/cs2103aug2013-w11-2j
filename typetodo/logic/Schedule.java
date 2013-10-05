package typetodo.logic;

import java.sql.Date;
import java.util.ArrayList;

import typetodo.db.DBHandler;
import typetodo.logic.Task.Status;

public class Schedule {
	private static final String WELCOME_MESSAGE = "Welcome to TypeToDo.\n";
	private static final String MESSAGE_ADDED = "%s has been added to your schedule";
	
	private static class ViewMode {
		private static String mode;
		private static Date date;
		private static String keyword;
		private static Status status;
		
		private static String getMode() {
			return mode;
		}
		
		private static void setMode(String mode) {
			ViewMode.mode = mode;
		}
		
		private static Date getDate() {
			return date;
		}
		
		private static void setDate(Date date) {
			ViewMode.date = date;
		}
		
		private static String getKeyword() {
			return keyword;
		}
		
		private static void setKeyword(String keyword) {
			ViewMode.keyword = keyword;
		}
		
		private static Status getStatus() {
			return status;
		}
		
		private static void setStatus(Status status) {
			ViewMode.status = status;
		}
	}
	
	private String feedBack;
	
	DBHandler db = new DBHandler();
	
	View addTask(String name, String description) {
		// Check if there is a duplicate entry//
		db.addTask(new FloatingTask(name, description));
		setFeedBack(String.format(MESSAGE_ADDED, name));
		return generateView();
	}
	
	View addTask(String name, String description, Date start, Date end, boolean isBusy) {
		db.addTask(new TimedTask(name, description, start, end, isBusy));
		setFeedBack(String.format(MESSAGE_ADDED, name));
		return generateView();
	}
	
	View addTask(String name, String description, Date deadline) {
		db.addTask(new DeadlineTask(name, description, deadline));
		setFeedBack(String.format(MESSAGE_ADDED, name));
		return generateView();
	}
	
	View deleteTask(String taskName) {
		db.deleteTask(taskName);
		return generateView();
	}
	
	public View generateView() {
		String mode = ViewMode.getMode();
		ArrayList<Task> tasks = null;
		View view = null;
		
		switch (mode) {
			case "date" :
				tasks = db.retrieveList(ViewMode.getDate());
				view = new View(getFeedBack(), tasks);
				break;
			
			case "keyword" :
				tasks = db.retrieveContains(ViewMode.getKeyword());
				view = new View(getFeedBack(), tasks);
				break;
				
			case "status" :
				tasks = db.retrieveContains(ViewMode.getStatus().toString());
				view = new View(getFeedBack(), tasks);
				break;
		}
		
		return view;
	}
	
	View setViewMode(Date date) {
		ViewMode.setMode("date");
		ViewMode.setDate(date);
		return generateView();
	}
	
	View setViewMode(String keyword) {
		ViewMode.setMode("keyword");
		ViewMode.setKeyword(keyword);
		return generateView();
	}
	
	View setViewMode(Status status) {
		ViewMode.setMode(status.toString());
		ViewMode.setStatus(status);
		return generateView();
	}
	
	String editFloatingTask(FloatingTask floatingTask, String name, String description) {
		String feedBack = null;
		return feedBack;
	}
	
	String editTimedTask(TimedTask timedTask, String name, String description, int startTime, String startDate, 
																				int endTime, String endDate) {
		String feedBack = null;
		return feedBack;
	}
	
	String editDeadineTask(DeadlineTask deadlineTask, String name, String description, int endTime, int endDate) {
		String feedBack = null;
		return feedBack;
	}

	private String getFeedBack() {
		return this.feedBack;
	}

	private void setFeedBack(String feedBack) {
		this.feedBack = feedBack;
	}
}

	