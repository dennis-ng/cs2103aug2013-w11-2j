//@author: A0090941E
package typetodo.logic;

public class HelpController {
	private String typeString;
	private CommandType helpType;

	//constructor for help with no specific helpType
	public HelpController(String typeString) {
		this.typeString = typeString;
	}

	//constructor for help with helpType
	public HelpController(CommandType helpType) {
		typeString = helpType.toString();
		this.helpType = helpType;
	}

	public String help(CommandType helpType) {
		switch (helpType) {
		case ADD:
			return HELP_ADD;
		case DELETE:
			return HELP_DELETE;
		case DONE:
			return HELP_DONE;
		case DISPLAY:
			return HELP_DISPLAY;
		case EXIT:
			return HELP_EXIT;
		case HOME:
			return HELP_HOME;
		case HOTKEY:
			return HELP_HOTKEY;
		case SEARCH:
			return HELP_SEARCH;
		case SYNC:
			return HELP_SYNC;
		case UNDO:
			return HELP_UNDO;
		case UPDATE:
			return HELP_UPDATE;
		default:
			return HELP_INSTRUCTION;
		}
	}

	public String getFeedback() {
		String feedback;
		if (typeString != "") {
			String thisHelp = help(helpType);
			feedback = thisHelp;
		} else {
			assert typeString == "";
			feedback = HELP_INSTRUCTION;
		}
		return feedback;
	}

	/** hard coded catalog*/
	private static final String HELP_INSTRUCTION = "Please enter 'help <command>'\n"
			+ "i.e.help hotkey, add, del, edit, undo, complete, search, sync, home, etc";
	private static final String HELP_ADD = "ADD:\n"
			+ "add <task title>; (optional) <time and date>\n"
			+ "*If you want to add description, you may type '+<description>' at the end of task";
	private static final String HELP_DELETE = "DELETE:\n"
			+ "(BY INDEX) del <index of task on current list>\n"
			+ "(BY KEYWORD) del <keyword of the task title>";
	private static final String HELP_DONE = "COMPLETE: (mark certain task is completed and remove from task list)\n"
			+ "(SYNTAX) done <index of task>";
	private static final String HELP_DISPLAY = "DISPLAY: (the default view is today's tasks)\n"
			+ "(SYNTAX) view <date> OR view <type> (Please do no use abbreviation.)";
	private static final String HELP_EXIT = "EXIT:(save and quit TypeToDo)\n"
			+ "(SYNTAX) exit";
	private static final String HELP_HOME = "HOME:(display default task list)\n"
			+ "(SYNTAX) home";
	private static final String HELP_SEARCH = "SEARCH:(find tasks that contain given keyword)\n"
			+ "(SYNTAX) search <keyword>";
	private static final String HELP_SYNC = "SYNC:(synchronize with google calendar. Either export typetodo tasks into GCal, or import editted GCal tasks)\n"
			+ "(SYNTAX) sync";
	private static final String HELP_UPDATE = "UPDATE:(modify exist task from typetodo. Note field name must be in capital letters,i.e.TITLE,DESCRIPTION,DEADLINE,START,END)\n"
			+ "(SYNTAX) edit <index of task on current list> <field name> <new value>";
	private static final String HELP_UNDO = "UNDO:\n" + "(SYNTAX) undo\n"
			+ "(HOTKEY) <ctrl> + <backspace>";
	private static final String HELP_HOTKEY = "HOT-KEYS:\n"
			+ "HIDE/RESTORE WINDOW: <ctrl> + <space>\n"
			+ "NAVIGATION MODE: <Esc>\n" + "INPUT MODE: <i> or <Enter>";
}
