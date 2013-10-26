package typetodo.logic;

import org.joda.time.DateTime;

import typetodo.model.FieldName;
import typetodo.model.Task;

public class EditTaskCommand implements Command, Undoable{
	private static final String MESSAGE_EDITED = "Edit successful";
	private Schedule sc;
	private Task taskBeforeEdit;
	private String keyword;
	private int index;
	private FieldName fieldName;
	private String newString;
	private DateTime newDateTime;
	private Boolean newBoolean;
	
	public EditTaskCommand(Schedule sc, int index, FieldName fieldName, String newValue) {
		this.sc = sc;
		this.index = index;
		this.fieldName = fieldName;
		this.newString = newValue;
	}
	
	public EditTaskCommand(Schedule sc, int index, FieldName fieldName, DateTime newDate) {
		this.sc = sc;
		this.index = index;
		this.fieldName = fieldName;
		this.newDateTime = newDate;
	}
	
	public EditTaskCommand(Schedule sc, int index, FieldName fieldName, boolean newBoolean) {
		this.sc = sc;
		this.index = index;
		this.fieldName = fieldName;
		this.newBoolean = newBoolean;
	}
	
	public String execute() throws Exception {
		if (newString != null) {
			this.taskBeforeEdit = sc.editTask(index, fieldName, newString);
		}
		else if(newDateTime != null) {
			this.taskBeforeEdit = sc.editTask(index, fieldName, newDateTime);
		}
		else if(newBoolean != null) {
			this.taskBeforeEdit = sc.editTask(index, fieldName, newBoolean);
		}
		
		String feedback = String.format(MESSAGE_EDITED);
		return feedback;
	}
	
	public void undo() throws Exception {
		sc.editTask(taskBeforeEdit);
	}
}
