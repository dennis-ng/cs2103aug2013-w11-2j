package typetodo.logic;

public class FloatingTask extends Task {
	public FloatingTask(String name, String description) {
		super(name, description);
	}
	
	public FloatingTask(int taskId, String name, String description) {
		super(name, description);
		this.setTaskId(taskId);
	}
	
	public Task makeCopy() {
		return new FloatingTask(this.getTaskId(), this.getName(), this.getDescription());
	}
}