package typetodo.logic;

public class UndoCommand implements Command{
	Scheduler sc;
	public UndoCommand(Scheduler sc) {
		this.sc = sc;
	}
	
	public void execute() throws Exception {
		sc.undo();
	}
}
