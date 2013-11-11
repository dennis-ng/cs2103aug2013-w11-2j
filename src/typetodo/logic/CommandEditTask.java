package typetodo.logic;

import org.joda.time.DateTime;

import typetodo.model.FieldName;
import typetodo.model.Task;

/**
 * 
 * @author A0091024U
 *
 */
public class CommandEditTask implements Command, Undoable{
	private static final String MESSAGE_EDITED = "Edit successful";
	private Schedule schedule;
	private Task taskBeforeUpdate;
	private int taskId;
	private FieldName fieldName;
	private String newString;
	private DateTime newDateTime;
	
	public CommandEditTask(Schedule schedule, int taskId, FieldName fieldName, String newValue) {
		this.schedule = schedule;
		this.taskId = taskId;
		this.fieldName = fieldName;
		this.newString = newValue;
	}
	
	public CommandEditTask(Schedule schedule, int taskId, FieldName fieldName, DateTime newDate) {
		this.schedule = schedule;
		this.taskId = taskId;
		this.fieldName = fieldName;
		this.newDateTime = newDate;
	}
	
	public String execute() throws Exception {
		taskBeforeUpdate = schedule.getTask(taskId).makeCopy();
		
		if (newString != null) {
			schedule.updateTask(taskId, fieldName, newString);
		} else if(newDateTime != null) {
			schedule.updateTask(taskId, fieldName, newDateTime);
		}
		
		String feedback = String.format(MESSAGE_EDITED);
		return feedback;
	}
	
	public void undo() throws Exception {
		schedule.updateTask(taskBeforeUpdate);
	}
}

