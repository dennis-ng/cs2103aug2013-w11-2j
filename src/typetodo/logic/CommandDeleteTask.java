package typetodo.logic;

import typetodo.model.Task;

public class CommandDeleteTask implements Command, Undoable{
	private static final String MESSAGE_DELETED = "\"%s\" has been deleted from your schedule";
	
	private Schedule sc;
	private Task taskToBeDeleted;
	private String keyword;
	private Integer index;
	
	public CommandDeleteTask(Schedule sc, String keyword){
		this.sc = sc;
		this.keyword = keyword;
	}
	
	public CommandDeleteTask(Schedule sc, int index) {
		this.sc = sc;
		this.index = index;
	}
	
	public String execute() throws Exception {
		if (index != null) { //delete by index
			taskToBeDeleted = sc.deleteTaskByIndex(index);
		}
		else {
			taskToBeDeleted = sc.deleteTaskByKeyword(keyword);
		}
		
		String feedback = String.format(MESSAGE_DELETED, taskToBeDeleted.getTitle());
		return feedback;
	}
	
	public void undo() throws Exception{
		sc.addTask(taskToBeDeleted);
	}
}
