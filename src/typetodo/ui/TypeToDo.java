package typetodo.ui;

import java.text.ParseException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import typetodo.logic.*;

/**@author Wang Qi*/
public class TypeToDo {
	public static CommandParser parser;
	public static Scanner scanner = new Scanner(System.in);

	private static final String MESSAGE_PROMPT = "Enter command here: ";

	public static void main(String[] args) throws Exception{
		parser = new CommandParser();
		initialiseApp();
		executeProgram();
	}

	//initialization
	private static View initialiseApp() {
		return parser.initialise();
	}

	private static void executeProgram() throws ParseException {
		while (true) {
			printMessage(MESSAGE_PROMPT);
			String inputLine = scanner.nextLine();
			View returnedView = executeCommand(inputLine);
			printView(returnedView);
		}
	}

	/**print prompt feedback and task list*/
	private static void printView(View view) {
		String feedback = view.getFeedBack();
		ArrayList<Task> tasks = view.getTasks();
		int totalTask = tasks.size();
		System.out.print(feedback);
		System.out.println();
		for (int i = 0; i < totalTask; i++) {
			System.out.print((i + 1) + ". ");
			printTask(tasks.get(i));
		}
	}

	/**print all fields in one task
	 * the task can be floating, deadline or timed task.*/
	private static void printTask(Task task) {
		String name = task.getName();
		String description = task.getDescription();
		Date start = ((TimedTask) task).getStart();
		Date end = ((TimedTask) task).getEnd();
		boolean isBusy = ((TimedTask) task).isBusy();
		Date deadline = ((DeadlineTask) task).getDeadline();
		System.out.println(name + " " + description + " " + start + " " + end
				+ deadline + " " + isBusy);
	}

	/**print parameter message*/
	private static void printMessage(String message) {
		System.out.println(message);
	}

	/**parse textUI to CommandParser class
	 * @throws ParseException */
	private static View executeCommand(String userInput) throws ParseException {
		View executedResult = parser.executeCommand(userInput);
		return executedResult;
	}
}
