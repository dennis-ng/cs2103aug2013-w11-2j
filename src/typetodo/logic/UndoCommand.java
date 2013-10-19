package typetodo.logic;

public class UndoCommand implements Command{
	ScheduleController sc;
	public UndoCommand(ScheduleController sc) {
		this.sc = sc;
	}
	
	public void execute() throws Exception {
		sc.undo();
	}
}
