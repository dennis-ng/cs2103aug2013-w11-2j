package typetodo.logic;

import org.joda.time.DateTime;

import typetodo.model.DeadlineTask;
import typetodo.model.FloatingTask;
import typetodo.model.Task;
import typetodo.model.TimedTask;

public class CommandAddTask implements Command, Undoable {
	private static final String MESSAGE_ADDED = "\"%s\" has been added to your schedule";
	private Schedule sc;
	private int taskId;
	private Task taskToBeAdded;
	
	public CommandAddTask(Schedule sc, String title, String description) {
		this.sc = sc;
		this.taskToBeAdded = new FloatingTask(title, description);
	}
	
	public CommandAddTask(Schedule sc, String title, String description, DateTime deadline) {	
		this.sc = sc;
		this.taskToBeAdded = new DeadlineTask(title, description, deadline);
	}
	
	public CommandAddTask(Schedule sc, String title, String description, DateTime start, DateTime end, boolean isBusy) {
		this.sc = sc;
		this.taskToBeAdded = new TimedTask(title, description, start, end, isBusy);
	}
	
	public String execute() throws Exception {
		taskId = sc.addTask(taskToBeAdded);
		
		String feedback;
		feedback = String.format(MESSAGE_ADDED, taskToBeAdded.getTitle());
		return feedback;
	}
	
	public void undo() throws Exception{
		sc.deleteTaskById(taskId);
	}
}
