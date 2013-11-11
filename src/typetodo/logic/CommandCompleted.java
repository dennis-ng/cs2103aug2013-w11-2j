package typetodo.logic;

import typetodo.model.Task;
import typetodo.model.Task.Status;

/**
 * Concrete command class to mark task as completed.
 * @author A0091024U
 *
 */
public class CommandCompleted implements Command, Undoable{
	private static final String MESSAGE_COMPLETED = "\"%s\" has been marked as completed";
	private int taskId;
	private Task taskBeforeMarking;
	private Schedule schedule;
	public CommandCompleted(Schedule schedule, int taskId) {
		this.schedule = schedule;
		this.taskId = taskId;
	}
	
	@Override
	public String execute() throws Exception {
		taskBeforeMarking = schedule.getTask(taskId).makeCopy();
		schedule.updateTaskStatus(taskId, Status.COMPLETED);
		String feedback = String.format(MESSAGE_COMPLETED, taskBeforeMarking.getTitle());
		return feedback;
	}

	@Override
	public void undo() throws Exception {
		schedule.updateTask(taskBeforeMarking);
	}
}
