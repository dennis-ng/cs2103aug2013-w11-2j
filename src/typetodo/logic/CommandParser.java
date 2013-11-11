package typetodo.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.joda.time.DateTime;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;


import typetodo.exception.InvalidCommandException;
import typetodo.exception.InvalidDateTimeException;
import typetodo.exception.InvalidFieldNameException;
import typetodo.exception.InvalidFormatException;
import typetodo.exception.MissingFieldException;
import typetodo.exception.ReservedCharacterException;
import typetodo.model.FieldName;
import typetodo.sync.SyncHandler;

public class CommandParser {
	private Schedule schedule;
	private ScheduleController sc;
	private SyncHandler sync;

	public CommandParser(ScheduleController sc, Schedule schedule) {
		this.sc = sc;
		this.schedule = schedule;
	}

	/**
	 * Extracts and returns the command from the user input.
	 * 
	 * @param userInput
	 *            A String containing the raw user input
	 * @return the command
	 * @throws InvalidCommandException
	 */
	private CommandType getCommand(String userInput)
			throws InvalidCommandException {

		Scanner scanner = new Scanner(userInput);
		// Extract the first string
		String command = scanner.next().toLowerCase();
		scanner.close();

		HashMap<CommandType, List<String>> commandSynonyms = new HashMap<CommandType, List<String>>();
		/** hard coded library of possible various user command inputs. */
		commandSynonyms.put(CommandType.ADD, Arrays.asList("add", "insert"));
		commandSynonyms.put(CommandType.DELETE,
				Arrays.asList("delete", "del", "de", "-", "remove"));
		commandSynonyms.put(CommandType.DONE, Arrays.asList("done", "finished",
				"finish", "completed", "complete"));
		commandSynonyms.put(CommandType.DISPLAY,
				Arrays.asList("display", "view", "show", "see", "list"));
		commandSynonyms.put(CommandType.HELP, Arrays.asList("help"));
		commandSynonyms.put(CommandType.HOME, Arrays.asList("home", "today"));
		commandSynonyms.put(CommandType.HOTKEY, Arrays.asList("hotkey",
				"quick", "hot key", "hotkeys", "hot", "short"));
		commandSynonyms.put(CommandType.UPDATE,
				Arrays.asList("update", "edit", "change"));
		commandSynonyms
				.put(CommandType.SEARCH, Arrays.asList("search", "find"));
		commandSynonyms.put(CommandType.UNDO, Arrays.asList("undo"));
		commandSynonyms.put(CommandType.EXIT, Arrays.asList("exit"));
		commandSynonyms.put(CommandType.SYNC, Arrays.asList("sync"));

		for (CommandType commandType : commandSynonyms.keySet()) {
			if (commandSynonyms.get(commandType).contains(command)) {
				return commandType;
			}
		}

		throw new InvalidCommandException(
				"Invalid command, please refer to catalog by entering 'help'.");
	}

	/**
	 * Extracts and returns the title from the user input.
	 * 
	 * @param userInput
	 *            A String containing the raw user input
	 * @return the title
	 * @throws InvalidFormatException
	 *             if user input does not have ';'
	 * @throws MissingFieldException
	 *             if user input does not contain a title
	 */
	private String getTitle(String userInput) throws InvalidFormatException,
			MissingFieldException, ReservedCharacterException {
		if (userInput.indexOf(';') == -1) {
			throw new InvalidFormatException("Missing ';'");
		}

		Scanner scanner = new Scanner(userInput);
		scanner.next(); // throw user command;
		scanner.useDelimiter(";"); // title of task must always end with ";"
		String title = scanner.next().trim();
		scanner.close();

		if (title.equals("")) {
			throw new MissingFieldException(
					"Title of task is missing, please refer to catalog by entering 'help'");
		} else if (title.contains("+")) {
			throw new ReservedCharacterException(
					"'+' is a reserved character and should not be found in the title");
		} else {
			assert title != "" && !title.contains("+") && !title.contains(";");
			return title.trim();
		}
	}

	/**
	 * Extracts and returns the description from the user input.
	 * 
	 * @param userInput
	 *            A String containing the raw user input
	 * @return the description
	 * @throws InvalidFormatException
	 *             if restricted char ';' is found in the description
	 */
	private String getDescription(String userInput)
			throws ReservedCharacterException {
		String description;
		int indexOfDescription = userInput.indexOf('+');

		// no description entered
		if (indexOfDescription == -1) {
			return "";
		}

		// description is empty
		else if (indexOfDescription == (userInput.length() - 1)) {
			return "";
		} else {
			description = userInput.substring(++indexOfDescription);
		}

		if (description.indexOf(';') != -1) {
			throw new ReservedCharacterException(
					"';' is a reserved character and should not be found in the description");
		}

		return description.trim();
	}

	/**
	 * Extracts and returns the index from the user input.
	 * 
	 * @param userInput
	 *            A String containing the raw user input
	 * @return the index
	 */
	private int getIndex(String userInput) {
		Scanner scanner = new Scanner(userInput);
		scanner.next(); // throw the command
		int index = scanner.nextInt(); // next expected field is the index
		scanner.close();

		return index;
	}

	/**
	 * Extracts and returns the FieldName from the user input.
	 * 
	 * @param userInput
	 *            Raw user input
	 * @return the Field Name
	 * @throws InvalidFieldNameException
	 *             if field name is invalid
	 * @throws MissingFieldException
	 *             if field name is missing
	 */
	private FieldName getFieldName(String userInput)
			throws InvalidFieldNameException, MissingFieldException {
		FieldName fieldName;
		Scanner scanner = new Scanner(userInput);
		scanner.next(); // discard the command
		scanner.nextInt(); // discard the index

		try {
			fieldName = this.convertToFieldName(scanner.next());
		} catch (NoSuchElementException e) {
			scanner.close();
			throw new MissingFieldException(
					"Field Name is missing, please refer to catalog by entering 'help edit'");
		}
		scanner.close();

		return fieldName;
	}

	/**
	 * Extracts and returns the new value from the user input. It can be an
	 * instance of either a String, DateTime or Boolean, depending on the field
	 * that is to be updated.
	 * 
	 * @param userInput
	 *            Raw user input
	 * @return the new value
	 * @throws Exception
	 */
	private Object getNewValue(String userInput) throws Exception {
		Scanner scanner = new Scanner(userInput);
		scanner.next(); // throw away command
		scanner.nextInt(); // throw away index
		scanner.next(); // throw away fieldName

		String newValue;
		try {
			newValue = scanner.nextLine().trim();
		} catch (NoSuchElementException e) {
			scanner.close();
			throw new MissingFieldException(
					"New value is missing, please refer to catalog by entering 'help edit'");
		}
		scanner.close();

		switch (this.getFieldName(userInput)) {
		case TITLE:
		case DESCRIPTION:
			return newValue;
		case START:
		case END:
		case DEADLINE:
			return new DateTime(this.getDates(userInput).get(0));
		default:
			assert this.getFieldName(userInput) != null;
		}

		return null;
	}

	private String getKeyword(String userInput) {
		String keyword = null;
		Scanner scanner = new Scanner(userInput);

		scanner.next(); // throw away command
		keyword = scanner.nextLine();// get keyword
		scanner.close();

		return keyword.trim();
	}

	private ArrayList<DateTime> getDates(String userInput) throws Exception {
		String dateField;
		Scanner scanner = new Scanner(userInput);
		scanner.next();// throw away command

		if (userInput.contains(";")) {
			scanner.useDelimiter(";");
			scanner.next();
			scanner.useDelimiter("\\+");
			dateField = scanner.next().substring(1).trim();
		} else if (this.getCommand(userInput).equals(CommandType.DISPLAY)) {
			dateField = scanner.nextLine().trim();
		} else {
			scanner.next();// throw away index
			scanner.next();// throw away fieldName
			dateField = scanner.nextLine().trim();
		}

		scanner.close();

		dateField = dateField.replaceAll("-", " to ").toLowerCase();
		List<java.util.Date> javaDates = new PrettyTimeParser()
				.parse(dateField);
		ArrayList<DateTime> jodaDates = new ArrayList<DateTime>();

		while (!javaDates.isEmpty()) {
			DateTime validDates = new DateTime(javaDates.remove(0));
			jodaDates.add(validDates);
		}

		if (dateField.contains(" to ") && jodaDates.size() == 1) {
			throw new InvalidDateTimeException(
					"Please specify both date and time in all fields. Please use 'dd/mm' format if you want to type standard date.");
		}

		return jodaDates;
	}

	private String getHelpType(String userInput) throws InvalidCommandException {
		String helpType;
		Scanner scanner = new Scanner(userInput);
		scanner.next();// throw away command;

		if (scanner.hasNext()) {
			helpType = scanner.next();
		} else {
			helpType = "";
		}
		scanner.close();

		return helpType;
	}

	/**
	 * convert from string to FieldName and return FieldName.
	 * 
	 * @throws InvalidFieldNameException
	 */
	private FieldName convertToFieldName(String fnString)
			throws InvalidFieldNameException {
		HashMap<FieldName, List<String>> fieldNameSynonyms = new HashMap<FieldName, List<String>>();
		fieldNameSynonyms.put(FieldName.TITLE, Arrays.asList("NAME", "TITLE"));
		fieldNameSynonyms.put(FieldName.DESCRIPTION,
				Arrays.asList("DESCRIPTION", "DESC"));
		fieldNameSynonyms.put(FieldName.START, Arrays.asList("START"));
		fieldNameSynonyms.put(FieldName.END, Arrays.asList("END"));
		fieldNameSynonyms.put(FieldName.DEADLINE, Arrays.asList("DEADLINE"));

		for (FieldName fieldName : fieldNameSynonyms.keySet()) {
			if (fieldNameSynonyms.get(fieldName).contains(fnString)) {
				return fieldName;
			}
		}

		throw new InvalidFieldNameException(
				"\""
						+ fnString
						+ "\" is not a valid Field Name, please refer to catalog by entering 'help edit'");
	}

	private boolean isViewAll(String userInput) {
		Scanner scanner = new Scanner(userInput);
		scanner.next(); // discard user command
		if (scanner.next().equals("all")) {
			scanner.close();
			return true;
		}
		scanner.close();

		return false;
	}

	private void checkForReservedCharacters(String userInput)
			throws ReservedCharacterException {
		if (userInput.indexOf("<") != -1) {
			throw new ReservedCharacterException(
					"'<' is a reserved character and cannot be used");
		}
		if (userInput.indexOf(">") != -1) {
			throw new ReservedCharacterException(
					"'>' is a reserved character and cannot be used");
		}
		if (userInput.indexOf("[") != -1) {
			throw new ReservedCharacterException(
					"'[' is a reserved character and cannot be used");
		}
		if (userInput.indexOf("]") != -1) {
			throw new ReservedCharacterException(
					"']' is a reserved character and cannot be used");
		}
	}

	/**
	 * 
	 * @param userInput
	 * @return
	 * @throws Exception
	 */
	public Command parse(String userInput) throws Exception {
		this.checkForReservedCharacters(userInput);

		Command command = null;
		switch (this.getCommand(userInput)) {

		case ADD:
			String title = this.getTitle(userInput);
			String description = this.getDescription(userInput);
			ArrayList<DateTime> dates = this.getDates(userInput);

			if (dates.isEmpty()) {
				command = new CommandAddTask(schedule, title, description);
			} else if (dates.size() == 1) {
				DateTime deadline = dates.get(0);
				command = new CommandAddTask(schedule, title, description,
						deadline);
			} else if (dates.size() == 2) {
				DateTime start = dates.get(0);
				DateTime end = dates.get(1);

				command = new CommandAddTask(schedule, title, description,
						start, end, false);
			} else {
				throw new InvalidFormatException(
						"INVALID FORMAT. Please refer to catalog by entering 'help add'");
			}
			break;

		case DELETE:
			try {
				int index = this.getIndex(userInput);
				command = new CommandDeleteTask(schedule, index);
			} catch (Exception e) {
				String keyword = this.getKeyword(userInput);
				if (keyword == null) {
					throw new InvalidFormatException(
							"INVALID FORMAT. Please refer to catalog by entering 'help delete'");
				}
				command = new CommandDeleteTask(schedule, keyword);
			}
			break;

		case UPDATE:
			int index = this.getIndex(userInput);
			FieldName fieldName = this.getFieldName(userInput);
			Object newValue = this.getNewValue(userInput);

			if (newValue instanceof String) {
				command = new CommandEditTask(schedule, index, fieldName,
						(String) newValue);
			} else if (newValue instanceof DateTime) {
				command = new CommandEditTask(schedule, index, fieldName,
						(DateTime) newValue);
			} else {
				throw new InvalidFormatException(
						"INVALID FORMAT. Please refer to catalog by entering 'help edit'");
			}
			break;

		case SEARCH:
			try {
				String keyword = this.getKeyword(userInput);
				command = new CommandSearch(schedule, keyword);
			} catch (Exception e) {
				throw new InvalidFormatException(
						"INVALID FORMAT. Please refer to catalog by entering 'help search'");
			}
			break;

		case DISPLAY:
			if (this.isViewAll(userInput)) {
				command = new CommandView(schedule);
				break;
			}

			DateTime dateTime;
			try {
				dateTime = this.getDates(userInput).get(0);
			} catch (IndexOutOfBoundsException e) {
				throw new InvalidFormatException(
						"INVALID FORMAT. Please refer to catalog by entering 'help display'");
			}
			command = new CommandView(schedule, dateTime);
			break;

		case DONE:
			try {
				int indexOfCompletedTask = this.getIndex(userInput);
				command = new CommandCompleted(schedule, indexOfCompletedTask);
			} catch (Exception e) {
				throw new InvalidFormatException(
						"INVALID FORMAT. Please refer to catalog by entering 'help done'");
			}
			break;

		case HOME:
			command = new CommandHome(schedule);
			break;

		case UNDO:
			command = new CommandUndo(sc);
			break;

		case HELP:
			String helpType = getHelpType(userInput);
			if (helpType != "") {
				CommandType commandType = getCommand(helpType);
				HelpController helpController = new HelpController(commandType);
				command = new CommandHelp(schedule, helpController);
			} else {
				HelpController helpController = new HelpController(helpType);
				command = new CommandHelp(schedule, helpController);
			}
			break;

		case EXIT:
			command = new CommandExit();
			break;

		case SYNC:
			if (sync == null) {
				sync = new SyncHandler();
			}
			command = new CommandSync(sync);
			break;

		default:
			// TODO:
			throw new Error();
		}
		return command;
	}

}