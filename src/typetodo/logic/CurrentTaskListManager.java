package typetodo.logic;

import java.util.ArrayList;

import org.joda.time.DateTime;

import typetodo.model.Task;
import typetodo.model.Task.Status;
import typetodo.model.TaskType;

/**
 * The CurrentTaskListManager is a class used to manage and sustain the current list of tasks that 
 * will be displayed to the user. 
 * @author A0091024U
 *
 */
public class CurrentTaskListManager {
	Schedule schedule;
	private ArrayList<Task> currentTaskList;
	private DateTime start = null;
	private DateTime end = null;
	private Status status = null;
	private String keyword = null;
	private TaskType type = null;
	
	public CurrentTaskListManager(Schedule schedule) {
		this.schedule = schedule;
		setCurrentTaskList(new ArrayList<Task>());
		this.setByDefault();
	}

	/**
	 * Gets the current list of task based on the current set of attributes
	 * @return returns a the current list of tasks
	 * @throws Exception 
	 */
	public ArrayList<Task> getCurrentTaskList() throws Exception {
		if (keyword != null) {
			currentTaskList = schedule.search(keyword);
		} else if (type != null) {
			switch (type) {
				case FLOATING_TASK :
					currentTaskList = schedule.getFloatingTasks(status);
					break;	
				case DEADLINE_TASK :
					currentTaskList = schedule.getDeadlineTasks(status);
					break;		
				case TIMED_TASK :
					currentTaskList = schedule.getTimedTasks(status);
					break;
			}
		} else {
			currentTaskList = schedule.getTasksByDateRange(start, end, status);
		}
		
		return currentTaskList;
	}
	
	public void setCurrentTaskList(ArrayList<Task> currentListOfTasks) {
		this.currentTaskList = currentListOfTasks;
	}
	
	public void setBySearchResult(String keyword) {
		if (keyword == null) {
			this.keyword = "";
		} else {
			this.keyword = keyword;
		}
		
		this.start = null;
		this.end = null;
		this.type = null;
	}
	
	public void setByDefault() {
		DateTime now = new DateTime();
		this.start = now;
		this.end = now.plusWeeks(1);
	}
	
	public void setByDateRange(DateTime start, DateTime end) {
		this.start = start;
		this.end = end;
		this.keyword = null;
		this.type = null;
	}
	
	public void setByType(TaskType type) {
		this.type = type;
		this.start = null;
		this.end = null;
		this.keyword = null;
		this.type = null;
	}
}
