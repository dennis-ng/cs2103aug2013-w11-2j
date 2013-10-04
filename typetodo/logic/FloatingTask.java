package texttodo.logic;

public class FloatingTask extends Task {
	public FloatingTask(String name, String description) {
		this.setName(name);
		this.setDescription(description);
		this.setStatus(Status.INCOMPLETE);
	}
}