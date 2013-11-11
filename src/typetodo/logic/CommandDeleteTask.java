package typetodo.logic;

import typetodo.model.Task;

public class CommandDeleteTask implements Command, Undoable{
	private static final String MESSAGE_DELETED = "\"%s\" has been deleted from your schedule";
	
	private Schedule sc;
	private Task taskToBeDeleted;
	private Integer taskId;
	
	public CommandDeleteTask(Schedule sc, int taskId) {
		this.sc = sc;
		this.taskId = taskId;
	}
	
	public String execute() throws Exception {
		this.taskToBeDeleted = sc.getTask(taskId).makeCopy();
		
		if (taskId != null) { 
			sc.deleteTaskById(taskId);
		}
		
		String feedback = String.format(MESSAGE_DELETED, taskToBeDeleted.getTitle());
		return feedback;
	}
	
	public void undo() throws Exception{
		sc.addTask(taskToBeDeleted);
	}
}

