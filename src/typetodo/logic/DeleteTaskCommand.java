package typetodo.logic;

import typetodo.model.Task;

public class DeleteTaskCommand implements Command, Undoable{
	Scheduler sc;
	Task taskToBeDeleted;
	String keyword;
	Integer index;
	
	public DeleteTaskCommand(Scheduler sc, String keyword){
		this.sc = sc;
		this.keyword = keyword;
	}
	
	public DeleteTaskCommand(Scheduler sc, int index) {
		this.sc = sc;
		this.index = index;
	}
	
	public void execute() throws Exception {
		if (index != null) { //delete by index
			taskToBeDeleted = sc.deleteTaskByIndex(index);
		}
		else {
			taskToBeDeleted = sc.deleteTaskByKeyword(keyword);
		}
	}
	
	public void undo() throws Exception{
		sc.addTask(taskToBeDeleted);
	}
}
