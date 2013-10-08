package typetodo.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import typetodo.logic.Task.Status;

/** @author Wang Qi */
public class CommandParser {
	public final Schedule schedule;
	private final static String MESSAGE_TYPE_ERROR = "Error: wrong command type.";
	private final static String MESSAGE_ERROR_COMMAND = "Error: wrong command key.";
	private final static String MESSAGE_INVALID_COMMAND = "Error: the command is invalid.";
	private final static View VIEW_INVALID = new View(MESSAGE_INVALID_COMMAND,
			null);
	private final static String catalog = "Here is catalog of standard inputs: \n"
			+ "ADD: add <name> desc <description>\n"
			+ "     add <name> desc <description> <deadline in ddMMMhhmm format>\n"
			+ "     add <name> desc <description> <start date> <end date> <isBusy>(type 'busy' if busy)\n"
			+ "DELETE: delete <name>\n"
			+ "        delete <index shown in list>\n"
			+ "DISPLAY: view <date>\n"
			+ "         view <keyword>\n"
			+ "         view <status>(COMPLETED, DISCARDED, INCOMPLETE)\n"
			+ "UPDATE: edit <index> <field name> <new value>\n"
			+ "SEARCH: search <keyword>\n"
			+ "MARK COMPLETED: done <index>\n"
			+ "HOME: home\n" + "UNDO: undo\n" + "HELP: help\n" + "EXIT: exit";
	private final static View VIEW_CATALOG = new View(catalog, null);

	// initialize
	public CommandParser() throws Exception {
		schedule = new Schedule();
	}

	public enum COMMAND {
		ADD, DELETE, DISPLAY, UPDATE, SEARCH, DONE, HOME, UNDO, HELP, INVALID, EXIT
	}

	// default view
	public View initialise() {
		return schedule.generateView();
	}

	public View executeCommand(String userInput) throws ParseException {
		String commandString = getFirstWord(userInput);
		String contentString = getContent(userInput);
		COMMAND command = getCommand(commandString, contentString);
		String[] contentArray = partitionString(contentString);

		switch (command) {

		case ADD:
			String name = contentString.split(" desc ")[0];
			String contentExcludeName = contentString.split(" desc ")[1];
			int length = contentExcludeName.length();

			if (dateOccurrence(contentArray) == 0) {
				return schedule.addTask(name, contentExcludeName);
			}

			else if (dateOccurrence(contentArray) == 1) {
				String description = contentExcludeName.substring(0,
						(length - 10));
				Date deadline = convertToDate(contentExcludeName
						.substring(length - 9));
				return schedule.addTask(name, description, deadline);
			}

			else if (dateOccurrence(contentArray) == 2) {
				String description = contentExcludeName.substring(0,
						(length - 25));
				Date start = convertToDate(contentExcludeName.substring(0, 9));
				Date end = convertToDate(contentExcludeName.substring(10, 19));
				Boolean isBusy = false;
				if (contentExcludeName.substring(20) == "busy") {
					isBusy = true;
				} else {
					isBusy = false;
				}
				return schedule.addTask(name, description, start, end, isBusy);
			} else {
				return VIEW_INVALID;
			}

		case DELETE:
			if (isNumeric(contentArray[0])) {
				return schedule.deleteTask(Integer.parseInt(contentString));
			} else {
				return schedule.deleteTask(contentString);
			}

		case DISPLAY:
			if (isDate(contentString)) {
				Date date = convertToDate(contentString);
				schedule.setViewMode(date);
			} else if (isStatus(contentString)) {
				Status status = convertToStatus(contentString);
				schedule.setViewMode(status);
			} else {
				schedule.setViewMode(contentString);
			}
			return schedule.generateView();

		case UPDATE:
			int index = Integer.parseInt(contentArray[0]);
			FieldName fieldName = convertToFieldName(contentArray[1]);
			String newValue = contentString.split(fieldName + " ")[1];

			if (fieldName.equals("NAME") || fieldName.equals("DESCRIPTION")) {
				return schedule.editTask(index, fieldName, newValue);
			} else if (fieldName.equals("START") || fieldName.equals("END")
					|| fieldName.equals("DEADLINE")) {
				Date date = convertToDate(newValue);
				return schedule.editTask(index, fieldName, date);
			} else if (fieldName.equals("BUSYFIELD")) {
				boolean isBusy = convertToBoolean(newValue);
				return schedule.editTask(index, fieldName, isBusy);
			} else {
				return VIEW_INVALID;
			}

		case SEARCH:
			return schedule.search(contentString);

		case DONE:
			return schedule
					.markTaskAsCompleted(Integer.parseInt(contentString));

		case HOME:
			schedule.setViewMode("today");
			return schedule.generateView();

		case UNDO:
			return schedule.undoLastOperation();

		case HELP:
			return VIEW_CATALOG;

		case INVALID:
			return VIEW_INVALID;

		case EXIT:
			System.exit(0);

		default:
			throw new Error(MESSAGE_TYPE_ERROR);
		}
	}

	/**
	 * parse users' command lines to the programme, according to different
	 * command types.
	 */
	private static COMMAND getCommand(String command, String content) {
		String commandLowerCase = command.toLowerCase();
		if (commandLowerCase == null) {
			throw new Error(MESSAGE_ERROR_COMMAND);
		} else if (addSynonym.contains(commandLowerCase)) {
			return COMMAND.ADD;
		} else if (deleteSynonym.contains(commandLowerCase)) {
			return COMMAND.DELETE;
		} else if (doneSynonym.contains(commandLowerCase)) {
			return COMMAND.DONE;
		} else if (displaySynonym.contains(commandLowerCase)) {
			return COMMAND.DISPLAY;
		} else if (updateSynonym.contains(commandLowerCase)) {
			return COMMAND.UPDATE;
		} else if (helpSynonym.contains(commandLowerCase)) {
			return COMMAND.HELP;
		} else if (homeSynonym.contains(commandLowerCase)) {
			return COMMAND.HOME;
		} else if (searchSynonym.contains(commandLowerCase)) {
			return COMMAND.SEARCH;
		} else if (undoSynonym.contains(commandLowerCase)) {
			return COMMAND.UNDO;
		} else if (exitSynonym.contains(commandLowerCase)) {
			return COMMAND.EXIT;
		} else {
			return COMMAND.INVALID;
		}
	}

	/** return the first word (before whitespace) of a sentence. */
	private static String getFirstWord(String userInput) {
		return userInput.trim().split("\\s+")[0];
	}

	/** return substring of a sentence that removed the first word. */
	private static String getContent(String userInput) {
		String[] temp = userInput.split(" ", 2);
		return temp[1];
	}

	/** return string array that each element is a word from a text. */
	private static String[] partitionString(String text) {
		String[] temp = text.split(" ");
		return temp;
	}

	/**
	 * return boolean value of whether a string can be parsed into an integer
	 * value.
	 */
	private static boolean isNumeric(String intString) {
		try {
			Integer.parseInt(intString);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * return boolean value of whether a string can be parsed into a date value.
	 */
	private static boolean isDate(String dateString) {
		DateFormat df = new SimpleDateFormat("ddMMMHHmm");
		try {
			df.parse(dateString);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/** return number of occurrence of Date in an array. */
	private static int dateOccurrence(String[] array) {
		int count = 0;
		for (String element : array) {
			if (isDate(element)) {
				count++;
			}
		}
		return count;
	}

	/** convert from string to date, and return date */
	private static Date convertToDate(String dateString) throws ParseException {
		Date date = new SimpleDateFormat("ddMMMhhmm", Locale.ENGLISH)
				.parse(dateString);
		return date;
	}

	/**
	 * return boolean value of whether a string can be parsed to a status value.
	 */
	private static boolean isStatus(String statusString) {
		if (statusString.equals("COMPLETED")
				|| statusString.equals("INCOMPLETE")
				|| statusString.equals("DISCARDED"))
			return true;
		else {
			return false;
		}
	}

	/** convert from string to status and return status */
	private static Status convertToStatus(String statusString) {
		if (statusString.equals("COMPLETED")) {
			return Status.COMPLETED;
		} else if (statusString.equals("DISCARDED")) {
			return Status.DISCARDED;
		} else {
			return Status.INCOMPLETE;
		}
	}

	/** convert from string to FieldName and return FieldName. */
	private static FieldName convertToFieldName(String fnString) {
		if (fnString.equals("NAME")) {
			return FieldName.NAME;
		} else if (fnString.equals("DESCRIPTION")) {
			return FieldName.DESCRIPTION;
		} else if (fnString.equals("START")) {
			return FieldName.START;
		} else if (fnString.equals("END")) {
			return FieldName.END;
		} else if (fnString.equals("DEADLINE")) {
			return FieldName.DEADLINE;
		} else {
			return FieldName.BUSYFIELD;
		}
	}

	private static boolean convertToBoolean(String booleanString) {
		if (booleanString.equals("busy")) {
			return true;
		} else {
			return false;
		}
	}

	/** hard coded library of possible various user command inputs. */
	private static List<String> addSynonym = Arrays.asList("add", "ad", "a",
			"+", "insert", "i");
	private static List<String> deleteSynonym = Arrays.asList("delete", "del",
			"de", "-", "remove", "rem");
	private static List<String> doneSynonym = Arrays.asList("done", "finished",
			"finish", "completed", "complete", "fixed", "terminated",
			"checked", "check");
	private static List<String> displaySynonym = Arrays.asList("display",
			"dis", "view", "show", "see", "expand", "spect", "program", "list");
	private static List<String> helpSynonym = Arrays.asList("help");
	private static List<String> homeSynonym = Arrays.asList("home", "start");
	private static List<String> updateSynonym = Arrays.asList("update", "up",
			"edit", "ed", "change", "ch");
	private static List<String> searchSynonym = Arrays.asList("search", "find",
			"request");
	private static List<String> undoSynonym = Arrays.asList("undo", "back",
			"reverse", "re");
	private static List<String> exitSynonym = Arrays.asList("exit", "ex", "gg",
			"shut");
}
