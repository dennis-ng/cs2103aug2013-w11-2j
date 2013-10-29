package typetodo.logic;

import typetodo.model.Task;
import typetodo.model.Task.Status;

public class CommandCompleted implements Command, Undoable{
	private static final String MESSAGE_COMPLETED = "\"%s\" has been marked as completed";
	private int index;
	private Task taskBeforeMarking;
	private Schedule schedule;
	public CommandCompleted(Schedule schedule, int index) {
		this.schedule = schedule;
		this.index = index;
	}
	
	@Override
	public String execute() throws Exception {
		taskBeforeMarking = schedule.updateTaskStatus(index, Status.COMPLETED);
		String feedback = String.format(MESSAGE_COMPLETED, taskBeforeMarking.getTitle());
		return feedback;
	}

	@Override
	public void undo() throws Exception {
		schedule.editTask(taskBeforeMarking);
	}
}
