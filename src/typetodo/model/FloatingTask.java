package typetodo.model;


public class FloatingTask extends Task {
	public FloatingTask(String name, String description) {
		super(name, description);
	}
	
	public FloatingTask(int taskId, String name, String description) {
		super(name, description);
		this.setTaskId(taskId);
	}
	
	public Task makeCopy() {
		return new FloatingTask(this.getTaskId(), this.getTitle(), this.getDescription());
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TITLE: " + this.getTitle() + " ");
		sb.append("DESCRIPTION: " + this.getDescription());
		
		return sb.toString();
	}
}