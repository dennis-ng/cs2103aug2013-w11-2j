package typetodo.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import typetodo.model.FieldName;
import typetodo.model.Task.Status;

/** @author Wang Qi */
public class CommandParser {
	private final static String MESSAGE_TYPE_ERROR = "Error: wrong command type.";
	private final static String MESSAGE_ERROR_COMMAND = "Error: wrong command key.";
	private final static String MESSAGE_INVALID_COMMAND = "Error: the command is invalid.";

	private CommandType command;
	private ArrayList<DateTime> dates;
	private String title;
	private String description;
	private boolean isBusy;
	private FieldName fieldName;
	private String newValue;
	private int index;
	private String keyWord;
	private Scheduler sc;
	public CommandParser(Scheduler sc) {
		this.sc = sc;
	}

	/**
	 * parse users' command lines to the programme, according to different
	 * command types.
	 */
	private CommandType getCommand(String command) {
		String commandLowerCase = command.toLowerCase();
		if (commandLowerCase == null) {
			throw new Error(MESSAGE_ERROR_COMMAND);
		} else if (addSynonym.contains(commandLowerCase)) {
			return CommandType.ADD;
		} else if (deleteSynonym.contains(commandLowerCase)) {
			return CommandType.DELETE;
		} else if (doneSynonym.contains(commandLowerCase)) {
			return CommandType.DONE;
		} else if (displaySynonym.contains(commandLowerCase)) {
			return CommandType.DISPLAY;
		} else if (updateSynonym.contains(commandLowerCase)) {
			return CommandType.UPDATE;
		} else if (helpSynonym.contains(commandLowerCase)) {
			return CommandType.HELP;
		} else if (homeSynonym.contains(commandLowerCase)) {
			return CommandType.HOME;
		} else if (searchSynonym.contains(commandLowerCase)) {
			return CommandType.SEARCH;
		} else if (undoSynonym.contains(commandLowerCase)) {
			return CommandType.UNDO;
		} else if (exitSynonym.contains(commandLowerCase)) {
			return CommandType.EXIT;
		} else if (commandLowerCase.equals("sync")) {
			return CommandType.SYNC;
		}
		else {
			return CommandType.INVALID;
		}
	}

	/** return the first word (before whitespace) of a sentence. */
	private String getFirstWord(String userInput) {
		return userInput.trim().split("\\s+")[0];
	}

	/** return substring of a sentence that removed the first word. */
	private String getContent(String userInput) {
		String[] temp = userInput.split(" ", 2);

		if (temp.length < 2) {
			return null;
		}

		return temp[1];
	}

	/** return string array that each element is a word from a text. */
	private void partitionString(String bodyOfUserInput) {
		
		StringBuilder sb;

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
			//extract title
			sb = new StringBuilder();
			for (int index = 0; index < indexOfDesc; ++index) {
				sb.append(temp[index]);

				if(index != indexOfDesc-1) {
					sb.append(" ");
				}
			}
			
			this.setTitle(sb.toString());
			sb = new StringBuilder();
			
			//extract description
			for (int index = indexOfDesc+1; index < indexOfLastDate; ++index) {
				sb.append(temp[index]);
				if(index != indexOfLastDate-1) {
					sb.append(" ");
				}
			}
			
			this.setDescription(sb.toString());
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
				
				if (isNumeric(sb.toString())) {
					this.index = Integer.parseInt(sb.toString());
				}
				else {
					this.keyWord = sb.toString();
				}
				sb = new StringBuilder();
				
				// add field name to array list:
				this.fieldName = convertToFieldName(temp[indexOfFieldName]);
				
				// extract new value
				for (int index = indexOfFieldName+1; index < indexOfLastDate; ++index) {
					sb.append(temp[index]);
					if(index != indexOfLastDate-1) {
						sb.append(" ");
					}
				}
				if (!sb.toString().equals("")) {
					this.newValue = sb.toString();
				}
				
				sb = new StringBuilder();
			}
			else {// delete with index
				for (int index = 0; index < indexOfLastDate; ++index) {
					try {
						this.index = Integer.parseInt(temp[index]);
					} catch (Exception e) {
						this.keyWord = temp[index];
					}
				}
			}
		}
		
		//insert dates into the back of arraylist
		while (!dates.isEmpty()) {
			this.dates.add(convertToDate(dates.pop()));
		}
		
		//lastly check if the busy flag is raised, if yes, insert 'busy' to back of arraylist
		if (temp[temp.length-1].equals("busy")) {
			this.isBusy = true;
		}
		else {
			this.isBusy = false;
		}
	}

	/**
	 * return boolean value of whether a string can be parsed into an integer
	 * value.
	 */
	private boolean isNumeric(String intString) {
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
	private boolean isDate(String dateString) {
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("H:mm d-MMM yyyy");
			fmt.parseDateTime(dateString);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

	/** convert from string to date, and return date */
	private DateTime convertToDate(String dateString) throws IllegalArgumentException {
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
	private FieldName convertToFieldName(String fnString) {
		if (fnString.equals("NAME") || fnString.equals("TITLE")) {
			return FieldName.TITLE;
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

	private boolean convertToBoolean(String booleanString) {
		if (booleanString.equals("busy")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the command
	 */
	public CommandType getCommand() {
		return command;
	}

	public ArrayList<DateTime> getDates() {
		return dates;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the isBusy
	 */
	public boolean isBusy() {
		return isBusy;
	}

	/**
	 * @return the fieldName
	 */
	public FieldName getFieldName() {
		return fieldName;
	}

	/**
	 * @return the newValue
	 */
	public String getNewValue() {
		return newValue;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the keyWord
	 */
	public String getKeyWord() {
		return keyWord;
	}

	public Command parse(String userInput) throws IllegalArgumentException {
		String commandString = getFirstWord(userInput);
		this.command = getCommand(commandString);
		
		this.dates = new ArrayList<DateTime>();
		
		String contentString = getContent(userInput);
		
		if (contentString != null) {
			this.partitionString(contentString);
		}
		
		Command command = null;

		switch (this.getCommand()) {

		case ADD:
			String title = this.getTitle();
			String description = this.getDescription();
			ArrayList<DateTime> dates = this.getDates();

			if (dates == null || dates.isEmpty()) {
				command = new AddTaskCommand(sc, title, description);
			}
			else if (dates.size() == 1) {
				DateTime deadline = dates.get(0);
				command = new AddTaskCommand(sc, title, description, deadline);
			}
			else if (dates.size() == 2) {
				DateTime start = dates.get(0);
				DateTime end = dates.get(1);

				Boolean isBusy = this.isBusy();

				command = new AddTaskCommand(sc, title, description, start, end, isBusy);
			} else {
				//TODO: invalid new view
			}
			break;

		case DELETE :
			String keyWord = this.getKeyWord();
			if (keyWord == null) {
				int index = this.getIndex();
				command = new DeleteTaskCommand(sc, index);
			} else {
				command = new DeleteTaskCommand(sc, keyWord);
			}
			break;

		case DISPLAY :
			/*
			if (isDate(contentString)) {
				DateTime date = convertToDate(contentString);
				schedule.setViewMode(date);
			} else if (isStatus(contentString)) {
				Status status = convertToStatus(contentString);
				schedule.setViewMode(status);
			} else {
				schedule.setViewMode(contentString);
			}
			 */
			break;

		case UPDATE:
			int index = this.getIndex();
			FieldName fieldName = this.getFieldName();
			String field_name = fieldName.toString();

			if (field_name.equals("TITLE") || field_name.equals("DESCRIPTION")) {
				String newValue = this.getNewValue();
				command = new EditTaskCommand(sc, index, fieldName, newValue);
			} else if (field_name.equals("START") || field_name.equals("END")
					|| field_name.equals("DEADLINE")) {
				DateTime newValue = this.getDates().get(0);
				command = new EditTaskCommand(sc, index, fieldName, newValue);
			} else if (field_name.equals("BUSYFIELD")) {
				boolean newValue = this.isBusy();
				command = new EditTaskCommand(sc, index, fieldName, newValue);
			} else {
				//TODO: invalid view
			}
			break;

		case SEARCH :
			String keyword = this.getKeyWord();
			command = new SearchCommand(sc, keyword);
			break;

		case DONE :
			//TODO
			break;

		case HOME :
			command = new HomeCommand(sc);
			break;

		case UNDO :
			command = new UndoCommand(sc);
			break;

		case HELP:
			command = new HelpCommand(sc);
			break;

		case INVALID :
			//TODO: view invalid
			break;

		case EXIT :
			command = new ExitCommand(sc);
			break;

		case SYNC :
			//TODO:
			break;

		default:
			//TODO: 
			throw new Error();
		}

		return command;
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