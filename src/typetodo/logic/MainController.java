package typetodo.logic;

import java.io.IOException;
import java.util.Stack;

import typetodo.sync.SyncController;
import typetodo.ui.View;

/**
 * The MainController class controls the flow of TypeToDo and how its different components interact with
 * each other.
 * @author A0091024U
 * 
 */
public class MainController {
	private static final String MESSAGE_WELCOME = "Welcome to TypeToDo! Please type 'help' for instructions.";
	private static final String ERROR_MESSAGE_NOTHING_TO_UNDO = "Nothing to undo";
	
	private View view;
	private Stack<Command> historyOfCommands;
	private CommandParser commandParser;
	private CurrentTaskListManager taskListManager;
	private SyncController syncController;
	private HelpController helpController;
	
	public MainController(View view, Schedule schedule) throws IOException {
		this.view = view;
		this.syncController = new SyncController(view);
		this.taskListManager = new CurrentTaskListManager(schedule);
		this.commandParser = new CommandParser(this, schedule, taskListManager, syncController,helpController);
		this.historyOfCommands = new Stack<Command>();
		
		String htmlDisplayContent = "";
		
		try {
			htmlDisplayContent = TasksFormatter.formatTasks(taskListManager.getCurrentTaskList());
		} catch (Exception e) {
			view.displayErrorMessage(e.getMessage());
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
			view.displayErrorMessage(e.getMessage());
		}

		String htmlDisplayContent = "";
		try {
			htmlDisplayContent = TasksFormatter.formatTasks(taskListManager.getCurrentTaskList());
		} catch (Exception e) {
			view.displayErrorMessage(e.getMessage());
		}
		
		view.displayTasks(htmlDisplayContent);
	}

	public void undo() throws Exception {
		if (!historyOfCommands.isEmpty()) {
			try {
				((Undoable) this.historyOfCommands.pop()).undo();
			} catch (Exception e) {
				view.displayErrorMessage(e.getMessage());
			}
		} else {
			throw new Exception(ERROR_MESSAGE_NOTHING_TO_UNDO);
		}
	}
	
}
