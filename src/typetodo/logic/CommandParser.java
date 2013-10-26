package typetodo.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.joda.time.DateTime;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import typetodo.model.FieldName;

public class CommandParser {
	private Schedule scheduler;
	private ScheduleController sc;
	
	public CommandParser(ScheduleController sc, Schedule schedule) {
		this.sc = sc;
		this.scheduler = schedule;
	}
	
	/**
	 * Extracts and returns the command from the user input.
	 * @param userInput A String containing the raw user input
	 * @return the command
	 * @throws InvalidCommandException
	 */
	private CommandType getCommand(String userInput) throws InvalidCommandException {
		Scanner scanner = new Scanner(userInput);
		String command = scanner.next().toLowerCase(); //Extract the first string
		scanner.close();

		HashMap<CommandType, List<String>> commandSynonyms = new HashMap<CommandType, List<String>>();
		/** hard coded library of possible various user command inputs. */
		commandSynonyms.put(CommandType.ADD, Arrays.asList("add", "insert"));
		commandSynonyms.put(CommandType.DELETE, Arrays.asList("delete", "del", "de", "-", "remove"));
		commandSynonyms.put(CommandType.DONE, Arrays.asList("done", "finished", "finish", "completed", "complete"));
		commandSynonyms.put(CommandType.DISPLAY, Arrays.asList("display", "view", "show", "see", "list"));
		commandSynonyms.put(CommandType.HELP, Arrays.asList("help"));
		commandSynonyms.put(CommandType.HOME, Arrays.asList("home", "today"));
		commandSynonyms.put(CommandType.UPDATE, Arrays.asList("update", "edit", "change"));;
		commandSynonyms.put(CommandType.SEARCH, Arrays.asList("search", "find"));
		commandSynonyms.put(CommandType.UNDO, Arrays.asList("undo"));
		commandSynonyms.put(CommandType.EXIT, Arrays.asList("exit", "terminate"));

		for (CommandType commandType : commandSynonyms.keySet()) {
			if (commandSynonyms.get(commandType).contains(command)) {
				return commandType;
			}
		}
		throw new InvalidCommandException("Invalid command");
	}

	/**
	 * Extracts and returns the title from the user input.
	 * @param userInput A String containing the raw user input
	 * @return the title
	 * @throws InvalidFormatException if user input does not have ';'
	 * @throws MissingFieldException if user input does not contain a title
	 */
	private String getTitle(String userInput) throws InvalidFormatException, MissingFieldException {
		if (userInput.indexOf(';') == -1) {
			throw new InvalidFormatException("Missing ';'");
		}

		Scanner scanner = new Scanner(userInput);
		scanner.next(); //discard user command;
		scanner.useDelimiter(";"); //title of task must always end with a ;
		String title = scanner.next().trim(); //extract title and remove all leading/trailing spaces
		scanner.close();
		if (title.equals(null)) {
			throw new MissingFieldException("Title of task is missing");
		}
		return title;
	}
	
	/**
	 * Extracts and returns the description from the user input.
	 * @param userInput A String containing the raw user input
	 * @return the description
	 * @throws InvalidFormatException if restricted char ';' is found in the description
	 */
	private String getDescription(String userInput) throws InvalidFormatException {
		int indexOfDescription = userInput.indexOf('+');
		if (indexOfDescription == -1) { //if user input does not contain a description
			return "";
		}
		
		if (indexOfDescription == (userInput.length()-1)) { //if description is blank
			return ""; 
		}
		
		//Description will always be at the end of the userinput
		String description = userInput.substring(++indexOfDescription);
		if (description.indexOf(';') == -1) {
			throw new InvalidFormatException("';' is a reserved character and should not be found in the description");
		}
		return description;
	}

	/**
	 * Extracts and returns the index from the user input.
	 * @param userInput A String containing the raw user input
	 * @return the index
	 */
	private int getIndex(String userInput) {
		Scanner scanner = new Scanner(userInput);
		scanner.next(); //discard the command
		int index = scanner.nextInt(); //next expected field should be the index
		scanner.close();

		return index;
	}

	/**
	 * Extracts and returns the FieldName from the user input.
	 * @param userInput Raw user input
	 * @return the Field Name
	 * @throws InvalidFieldNameException if field name is invalid
	 * @throws MissingFieldException if field name is missing 
	 */
	private FieldName getFieldName(String userInput) throws InvalidFieldNameException, MissingFieldException {
		FieldName fieldName;
		Scanner scanner = new Scanner(userInput);
		scanner.next(); //discard the command
		scanner.nextInt(); //discard the index
		try {
			fieldName = this.convertToFieldName(scanner.next());
		} catch (NoSuchElementException e) {
			scanner.close();
			throw new MissingFieldException("Field Name is missing");
		}
		scanner.close();
		return fieldName;
	}
	
	/**
	 * Extracts and returns the new value from the user input. It can be an instance of either a String,
	 * DateTime or Boolean, depending on the field that is to be updated.
	 * @param userInput Raw user input
	 * @return the new value
	 * @throws MissingFieldException if new value is missing
	 * @throws InvalidFieldNameException if field name is invalid
	 */
	private Object getNewValue(String userInput) throws MissingFieldException, InvalidFieldNameException {
		Scanner scanner = new Scanner(userInput);
		scanner.next(); //throw away command
		scanner.nextInt(); //throw away index
		scanner.next(); //throw away fieldName
		
		String newValue;
		try {
			newValue = scanner.nextLine();
		} catch (NoSuchElementException e) {
			scanner.close();
			throw new MissingFieldException("New value is missing");
		}
		scanner.close();
		
		switch (this.getFieldName(userInput)) {
			case TITLE :
			case DESCRIPTION :
				return newValue;
			case START :
			case END :
			case DEADLINE :
				return new DateTime(this.getDates(newValue).get(0));
			case BUSYFIELD :
				return (this.getIsBusy(userInput));
			default:
				//TODO: assert?
		}
		return null;
	}
	
	private boolean getIsBusy(String userInput) {
		if (userInput.contains("BUSY")) {
			return true;
		}
		return false;
	}

	private String getKeyword(String userInput) {
		String keyword = null;
		Scanner scanner = new Scanner(userInput);
		scanner.next(); //throw away command
		keyword = scanner.nextLine();//get keyword
		scanner.close();
		return keyword.trim();
	}

	private ArrayList<DateTime> getDates(String userInput) {
		String dateField;
		Scanner scanner = new Scanner(userInput);
		scanner.next();//throw away command
		if (userInput.contains(";")) {
			scanner.useDelimiter(";");
			scanner.next();
			scanner.useDelimiter("\\+");
			dateField = scanner.next().substring(1).trim();
			System.out.println(dateField);
		}
		else {
			scanner.next();//throw away index
			scanner.next();//throw away fieldName
			dateField = scanner.nextLine().trim();
			System.out.println(dateField);
		}
		scanner.close();
		List<java.util.Date> javaDates = new PrettyTimeParser().parse(dateField);
		ArrayList<DateTime> jodaDates = new ArrayList<DateTime>();
		
		while(!javaDates.isEmpty()) {
			jodaDates.add(new DateTime(javaDates.remove(0)));
		}
		
		return jodaDates;
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

	/** convert from string to FieldName and return FieldName. 
	 * @throws InvalidFieldNameException */
	private FieldName convertToFieldName(String fnString) throws InvalidFieldNameException {
		HashMap<FieldName, List<String>> fieldNameSynonyms = new HashMap<FieldName, List<String>>();
		fieldNameSynonyms.put(FieldName.TITLE, Arrays.asList("NAME", "TITLE"));
		fieldNameSynonyms.put(FieldName.DESCRIPTION, Arrays.asList("DESCRIPTION", "DESC"));
		fieldNameSynonyms.put(FieldName.START, Arrays.asList("START"));
		fieldNameSynonyms.put(FieldName.END, Arrays.asList("END"));
		fieldNameSynonyms.put(FieldName.DEADLINE, Arrays.asList("DEADLINE"));
		fieldNameSynonyms.put(FieldName.BUSYFIELD, Arrays.asList("BUSYFIELD"));

		for (FieldName fieldName : fieldNameSynonyms.keySet()) {
			if (fieldNameSynonyms.get(fieldName).contains(fnString)) {
				return fieldName;
			}
		}
		throw new InvalidFieldNameException("\"" + fnString + "\" is not a valid Field Name");
	}

	public Command parse(String userInput) throws Exception {
		Command command = null;
		switch (this.getCommand(userInput)) {
		
			case ADD :
				String title = this.getTitle(userInput);
				String description = this.getDescription(userInput);
				ArrayList<DateTime> dates = this.getDates(userInput);

			if (dates.isEmpty()) {
				command = new AddTaskCommand(scheduler, title, description);
			} else if (dates.size() == 1) {
				DateTime deadline = dates.get(0);
				command = new AddTaskCommand(scheduler, title, description, deadline);
			} else if (dates.size() == 2) {
				DateTime start = dates.get(0);
				DateTime end = dates.get(1);

				//Boolean isBusy = this.isBusy();

				command = new AddTaskCommand(scheduler, title, description, start, end, false);
			} else {
				//TODO: invalid new view
			}
			break;

		case DELETE :
			try {
				int index = this.getIndex(userInput);
				command = new DeleteTaskCommand(scheduler, index);
			} catch (Exception e) {
				String keyword = this.getKeyword(userInput);
				if (keyword == null) {
					throw new Exception("INVALID FORMAT");
				}
				command = new DeleteTaskCommand(scheduler, keyword);
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
			int index = this.getIndex(userInput);
			FieldName fieldName = this.getFieldName(userInput);
			Object newValue = this.getNewValue(userInput);
			if (newValue instanceof String) {
				command = new EditTaskCommand(scheduler, index, fieldName, (String) newValue);
			} else if (newValue instanceof DateTime) {
				command = new EditTaskCommand(scheduler, index, fieldName, (DateTime) newValue);
			} else if (newValue instanceof Boolean) {
				command = new EditTaskCommand(scheduler, index, fieldName,(Boolean) newValue);
			} else {
				//TODO: invalid view
			}
			break;

		case SEARCH :
			String keyword = this.getKeyword(userInput);
			command = new SearchCommand(scheduler, keyword);
			break;

		case DONE :
			//TODO
			break;

		case HOME :
			command = new HomeCommand(scheduler);
			break;

		case UNDO :
			command = new UndoCommand(sc);
			break;

		case HELP:
			command = new HelpCommand(scheduler);
			break;

		case INVALID :
			//TODO: view invalid
			break;

		case EXIT :
			command = new ExitCommand();
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

}