package typetodo.logic;

import org.joda.time.DateTime;

import typetodo.model.FieldName;
import typetodo.model.Task;

public class EditTaskCommand implements Command, Undoable{
	Scheduler sc;
	Task taskBeforeEdit;
	String keyword;
	int index;
	FieldName fieldName;
	String newString;
	DateTime newDateTime;
	Boolean newBoolean;
	
	public EditTaskCommand(Scheduler sc, int index, FieldName fieldName, String newValue) {
		this.sc = sc;
		this.index = index;
		this.fieldName = fieldName;
		this.newString = newValue;
	}
	
	public EditTaskCommand(Scheduler sc, int index, FieldName fieldName, DateTime newDate) {
		this.sc = sc;
		this.index = index;
		this.fieldName = fieldName;
		this.newDateTime = newDate;
	}
	
	public EditTaskCommand(Scheduler sc, int index, FieldName fieldName, boolean newBoolean) {
		this.sc = sc;
		this.index = index;
		this.fieldName = fieldName;
		this.newBoolean = newBoolean;
	}
	
	public void execute() throws Exception {
		if (newString != null) {
			this.taskBeforeEdit = sc.editTask(index, fieldName, newString);
		}
		else if(newDateTime != null) {
			this.taskBeforeEdit = sc.editTask(index, fieldName, newDateTime);
		}
		else if(newBoolean != null) {
			this.taskBeforeEdit = sc.editTask(index, fieldName, newBoolean);
		}
	}
	
	public void undo() throws Exception {
		sc.editTask(taskBeforeEdit);
	}
}
