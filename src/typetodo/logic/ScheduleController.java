package typetodo.logic;

import java.util.Stack;

import typetodo.ui.View;

public class ScheduleController {
	private View view;
	private Stack<Command> historyOfCommands;
	private CommandParser commandParser;
	private Schedule schedule;
	
	public ScheduleController(View view, Schedule schedule) {
		this.view = view;
		this.commandParser = new CommandParser(this, schedule);
		this.historyOfCommands = new Stack<Command>();
		this.schedule = schedule;
		
		view.displayTasks(schedule.getCurrentListOfTasks());
		view.displayFeedBack("Welcome to TypeTodo");
	}
	
	public void inputCommand(String input) {
		Command command;
		try {
			command = commandParser.parse(input);
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
		
		view.displayTasks(schedule.getCurrentListOfTasks());
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
