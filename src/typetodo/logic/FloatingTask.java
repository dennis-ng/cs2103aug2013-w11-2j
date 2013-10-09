package typetodo.logic;

public class FloatingTask extends Task {
	public FloatingTask(String name, String description) {
		this.setName(name);
		this.setDescription(description);
		this.setStatus(Status.INCOMPLETE);
	}
	
	public FloatingTask(int taskId, String name, String description) {
		this.setTaskId(taskId);
		this.setName(name);
		this.setDescription(description);
		this.setStatus(Status.INCOMPLETE);
	}
	
	public Task makeCopy() {
		return new FloatingTask(this.getTaskId(), this.getName(), this.getDescription());
	}
}