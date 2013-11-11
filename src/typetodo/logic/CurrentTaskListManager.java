package typetodo.logic;

import java.util.ArrayList;

import org.joda.time.DateTime;

import typetodo.model.Task;
import typetodo.model.Task.Status;
import typetodo.model.TaskType;

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

	public ArrayList<Task> getCurrentTaskList() throws Exception {
		if (keyword != null) {
			currentTaskList = schedule.search(keyword);
		} else if (type != null) {
			if (type.equals(TaskType.FLOATING_TASK)) {
				currentTaskList = schedule.getFloatingTasks(status);
			} else if (type.equals(TaskType.DEADLINE_TASK)) {
				currentTaskList = schedule.getDeadlineTasks(status);
			} else if (type.equals(TaskType.TIMED_TASK)) {
				currentTaskList = schedule.getTimedTasks(status);
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
		this.keyword = keyword;
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
