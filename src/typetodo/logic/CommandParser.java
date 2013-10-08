package typetodo.logic;

public class CommandParser {

	private final Schedule sched;

	public CommandParser() {
		sched = new Schedule();
	}

	enum Command {
		ADD, DELETE, RETRIEVE, UPDATE
	}

	public String initialise() {
		return sched.viewMode("today");
	}

	public String parse(String cmd) {
		return sched.viewMode("today");
	}
}
