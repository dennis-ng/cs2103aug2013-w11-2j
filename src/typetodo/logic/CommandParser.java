package typetodo.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
			+ "     add <name> desc <description> <deadline in h:mm d-MMM yyyy format>\n"
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

	public View executeCommand(String userInput) throws IllegalArgumentException {
		//To determine which command to execute:
		String commandString = getFirstWord(userInput);
		COMMAND command = getCommand(commandString);
		
		String contentString = getContent(userInput);
		String[] contentArray = partitionString(contentString);

		switch (command) {

		case ADD:
			String name = contentArray[0];

			if (dateOccurrence(contentArray) == 0) {
				return schedule.addTask(name, contentArray[1]);
			}

			else if (dateOccurrence(contentArray) == 1) {
				String description = contentArray[1];
				DateTime deadline = convertToDate(contentArray[2]);
				return schedule.addTask(name, description, deadline);
			}

			else if (dateOccurrence(contentArray) == 2) {
				String description = contentArray[1];
				DateTime start = convertToDate(contentArray[2]);
				DateTime end = convertToDate(contentArray[3]);
				
				Boolean isBusy = false;
				try {
					if (contentArray[4] == "busy") {
						isBusy = true;
					} else {
						isBusy = false;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					//move on
				}
				
				return schedule.addTask(name, description, start, end, isBusy);
			} else {
				return VIEW_INVALID;
			}

		case DELETE :
			if (isNumeric(contentArray[0])) {
				return schedule.deleteTask(Integer.parseInt(contentString));
			} else {
				return schedule.deleteTask(contentString);
			}

		case DISPLAY :
			if (isDate(contentString)) {
				DateTime date = convertToDate(contentString);
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
			String field_name = fieldName.toString();
			String newValue = contentArray[2];

			if (field_name.equals("NAME") || field_name.equals("DESCRIPTION")) {
				System.out.println("HELLO");
				return schedule.editTask(index, fieldName, newValue);
			} else if (field_name.equals("START") || field_name.equals("END")
					|| field_name.equals("DEADLINE")) {
				DateTime date = convertToDate(newValue);
				return schedule.editTask(index, fieldName, date);
			} else if (field_name.equals("BUSYFIELD")) {
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
	private static COMMAND getCommand(String command) {
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

		if (temp.length < 2) {
			return null;
		}

		return temp[1];
	}

	/** return string array that each element is a word from a text. */
	private static String[] partitionString(String bodyOfUserInput) {
		StringBuilder sb;
		
		if (bodyOfUserInput == null) {
			return null;
		}

		ArrayList<String> listOfFields = new ArrayList<String>();
		
		String[] temp = bodyOfUserInput.split(" ");
		
		int indexOfLastDate = temp.length;
		int indexOfDesc = temp.length;
		int indexOfFieldName = temp.length;
		
		boolean hasDesc = false;
		Stack<String> dates = new Stack<String>();
		sb = new StringBuilder();
		
		//Finds all dates in body
		for (int index = temp.length-3; index >= 0; index--) {
			sb.append(temp[index] + " " + temp[index+1]+ " " + temp[index+2]);
			if (isDate(sb.toString())) {
				dates.push(sb.toString());
				indexOfLastDate = index;
			}
			sb = new StringBuilder();
		}
		
		//find index of 'desc'
		for (int index = 0; index < indexOfLastDate; ++index) {
			if (temp[index].equals("desc")) {
				indexOfDesc = index;
				hasDesc = true;
				break;
			}
		}
		
		if (hasDesc) {
			//extract name
			sb = new StringBuilder();
			for (int index = 0; index < indexOfDesc; ++index) {
				sb.append(temp[index]);

				if(index != indexOfDesc-1) {
					sb.append(" ");
				}
			}
			
			listOfFields.add(sb.toString());
			sb = new StringBuilder();
			
			//extract description
			for (int index = indexOfDesc+1; index < indexOfLastDate; ++index) {
				sb.append(temp[index]);
				if(index != indexOfLastDate-1) {
					sb.append(" ");
				}
			}
			
			listOfFields.add(sb.toString());
			sb = new StringBuilder();
		}
		else  {
			//find index of FIELDNAME
			//boolean isUpperCase = true;
			boolean hasFieldName = false;
			for (int index = 0; index < indexOfLastDate; ++index) {
				if (Character.isUpperCase(temp[index].charAt(0))) {
					indexOfFieldName = index; //need to modify//
					hasFieldName = true;
					break;
				}
			}
			
			if (hasFieldName) {
				//extract name/number
				sb = new StringBuilder();
				for (int index = 0; index < indexOfFieldName; ++index) {
					sb.append(temp[index]);

					if(index != indexOfFieldName-1) {
						sb.append(" ");
					}
				}
				
				listOfFields.add(sb.toString());
				sb = new StringBuilder();
				
				// add field name to array list:
				listOfFields.add(temp[indexOfFieldName]);
				
				// extract new value
				for (int index = indexOfFieldName+1; index < indexOfLastDate; ++index) {
					sb.append(temp[index]);
					if(index != indexOfLastDate-1) {
						sb.append(" ");
					}
				}
				if (!sb.toString().equals("")) {
					listOfFields.add(sb.toString());
				}
				
				sb = new StringBuilder();
			}
			else {
				for (int index = 0; index < indexOfLastDate; ++index) {
				listOfFields.add(temp[index]);
				}
			}
		}
		
		//insert dates into the back of arraylist
		while (!dates.isEmpty()) {
			listOfFields.add(dates.pop());
		}
		
		//lastly check if the busy flag is raised, if yes, insert 'busy' to back of arraylist
		if (temp[temp.length-1].equals("busy")) {
			listOfFields.add("busy");
		}
		
		return listOfFields.toArray(new String[listOfFields.size()]);
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
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("H:mm d-MMM yyyy");
			DateTime date = fmt.parseDateTime(dateString);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

	/** return number of occurrence of Date in an array. */
	private static int dateOccurrence(String[] array) {
		int count = 0;
		for (String element : array) {
			//System.out.println(element);
			if (isDate(element)) {
				count++;
			}
		}
		return count;
	}

	/** convert from string to date, and return date */
	private static DateTime convertToDate(String dateString) throws IllegalArgumentException {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("H:mm d-MMM yyyy");
		DateTime date = fmt.parseDateTime(dateString);
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
