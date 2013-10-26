package typetodo.logic;

public class UndoCommand implements Command{
	private static final String MESSAGE_UNDO = "Undo is successful";
	private ScheduleController sc;
	
	public UndoCommand(ScheduleController sc) {
		this.sc = sc;
	}
	
	public String execute() throws Exception {
		sc.undo();
		String feedback = MESSAGE_UNDO;
		return feedback;
	}
}
