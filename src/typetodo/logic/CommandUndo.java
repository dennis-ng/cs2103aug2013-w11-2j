package typetodo.logic;

public class CommandUndo implements Command{
	private static final String MESSAGE_UNDO = "Undo is successful";
	private MainController mainController;
	
	public CommandUndo(MainController mainController) {
		this.mainController = mainController;
	}
	
	public String execute() throws Exception {
		mainController.undo();
		String feedback = MESSAGE_UNDO;
		return feedback;
	}
}
