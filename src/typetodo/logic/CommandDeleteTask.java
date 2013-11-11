package typetodo.logic;

import typetodo.model.Task;

public class CommandDeleteTask implements Command, Undoable{
	private static final String MESSAGE_DELETED = "\"%s\" has been deleted from your schedule";
	
	private Schedule schedule;
	private Task taskToBeDeleted;
	private Integer taskId;
	
	public CommandDeleteTask(Schedule sc, int taskId) {
		this.schedule = sc;
		this.taskId = taskId;
	}
	
	public String execute() throws Exception {
		this.taskToBeDeleted = schedule.getTask(taskId).makeCopy();
		
		if (taskId != null) { 
			schedule.deleteTaskById(taskId);
		}
		
		String feedback = String.format(MESSAGE_DELETED, taskToBeDeleted.getTitle());
		return feedback;
	}
	
	public void undo() throws Exception{
		schedule.addTask(taskToBeDeleted);
	}
}

