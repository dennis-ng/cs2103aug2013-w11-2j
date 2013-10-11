package typetodo.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

import org.joda.time.DateTime;

import typetodo.logic.CommandParser;
import typetodo.logic.DeadlineTask;
import typetodo.logic.FloatingTask;
import typetodo.logic.Task;
import typetodo.logic.TimedTask;
import typetodo.logic.View;

/** @author Wang Qi */
public class TypeToDo {
	public static CommandParser parser;
	public static Scanner scanner = new Scanner(System.in);

	private static final String MESSAGE_PROMPT = "Enter command here: ";

	public static void main(String[] args) throws Exception {
		parser = new CommandParser();
		initialiseApp();
		executeProgram();
	}

	// initialization
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

	/** print prompt feedback and task list */
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

	/**
	 * print all fields in one task the task can be floating, deadline or timed
	 * task.
	 */
	private static void printTask(Task task) {
		String name = task.getName();
		String description = task.getDescription();
		DateTime start, end, deadline;
		boolean isBusy;

		if (task instanceof TimedTask) {
			start = ((TimedTask) task).getStart();
			end = ((TimedTask) task).getEnd();
			isBusy = ((TimedTask) task).isBusy();
			System.out.println(name + " " + description + " " + start + " "
					+ end + " " + isBusy);
		}

		else if (task instanceof FloatingTask) {
			System.out.println(name + " " + description);
		}

		else if (task instanceof DeadlineTask) {
			deadline = ((DeadlineTask) task).getDeadline();
			System.out.println(name + " " + description + " " + deadline);
		}

	}

	/** print parameter message */
	private static void printMessage(String message) {
		System.out.println(message);
	}

	/**
	 * parse textUI to CommandParser class
	 * 
	 * @throws ParseException
	 */
	private static View executeCommand(String userInput) throws ParseException {
		View executedResult = parser.executeCommand(userInput);
		return executedResult;
	}
}
