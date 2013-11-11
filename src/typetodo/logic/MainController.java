package typetodo.logic;

import java.io.IOException;
import java.util.Stack;

import typetodo.sync.SyncHandler;
import typetodo.ui.View;

public class MainController {
	private static final String MESSAGE_WELCOME = "Welcome to TypeToDo! Please enter 'help' for instructions.";
	private View view;
	private Stack<Command> historyOfCommands;
	private CommandParser commandParser;
	private CurrentTaskListManager taskListManager;
	private SyncHandler syncController;
	
	public MainController(View view, Schedule schedule) throws IOException {
		this.view = view;
		this.syncController = new SyncHandler(view);
		this.taskListManager = new CurrentTaskListManager(schedule);
		this.commandParser = new CommandParser(this, schedule, taskListManager, syncController);
		this.historyOfCommands = new Stack<Command>();

		
		String htmlDisplayContent = "";
		
		try {
			htmlDisplayContent = ViewHelper.generateHTMLDisplayContent(taskListManager.getCurrentTaskList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		view.displayTasks(htmlDisplayContent);
		view.displayFeedBack(MESSAGE_WELCOME);
	}

	public void parseAndExecute(String userInput) {
		Command command;
		try {
			command = commandParser.parse(userInput);
			String feedback = command.execute();
			view.displayFeedBack(feedback);

			if (command instanceof Undoable) {
				historyOfCommands.add(command);
			}

		} catch (Exception e) {
			// TODO;
			e.printStackTrace();
			view.displayErrorMessage(e.getMessage());
		}

		String htmlDisplayContent = "";
		try {
			htmlDisplayContent = ViewHelper.generateHTMLDisplayContent(taskListManager.getCurrentTaskList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		view.displayTasks(htmlDisplayContent);
		// view.displayTasks(schedule.getCurrentListOfTasks());
	}

	public void undo() throws Exception {
		if (!historyOfCommands.isEmpty()) {
			try {
				((Undoable) this.historyOfCommands.pop()).undo();
			} catch (Exception e) {

			}
		} else {
			throw new Exception("Nothing to undo");
		}
	}
	
}
