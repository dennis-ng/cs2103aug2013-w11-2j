package typetodo.logic;

public class CommandHelp implements Command {
	private static final String MESSAGE_HELP = "Command catalog is shown below: \n";
	private Schedule sc;
	private CommandType helpType;

	private static final String HELP_INSTRUCTION = "Please enter 'help <command>'.\n"
			+ "i.e.help hotkey, add, del, edit, undo, complete, search, sync, home, etc\n";
	private static final String HELP_ADD = "ADD:(add new task into typetodo)\n"
			+ "(FLOATING TASK) add <task title>;\n"
			+ "(DEADLINE TASK) add <task title>; <deadline date and time>\n"
			+ "(TIMED TASK)         add <task title>; <time span>  busy (only if you want to block this slot)\n"
			+ "\n"
			+ "*If you want to add description, you may type '+<description>' at the end of task\n";
	private static final String HELP_DELETE = "DELETE:(delete exist task from typetodo)\n"
			+ "(BY INDEX) del <index of task on current list>\n"
			+ "(BY KEYWORD) del <keyword of the task title>\n";
	private static final String HELP_DONE = "COMPLETE: (mark certain task is completed and remove from task list)\n"
			+ "(SYNTAX) done <index of task>\n";
	private static final String HELP_DISPLAY = "DISPLAY: (the default view is today's tasks)\n"
			+ "(SYNTAX) view <date>\n";
	private static final String HELP_EXIT = "EXIT:(save and quit TypeToDo)\n"
			+ "(SYNTAX) exit\n";
	private static final String HELP_HOME = "HOME:(display default task list)\n"
			+ "(SYNTAX) home\n";
	private static final String HELP_SEARCH = "SEARCH:(find tasks that contain given keyword)\n"
			+ "(SYNTAX) search <keyword>\n";
	private static final String HELP_SYNC = "SYNC:(synchronize with google calendar. Either export typetodo tasks into GCal, or import editted GCal tasks)\n"
			+ "(SYNTAX) sync\n";
	private static final String HELP_UPDATE = "UPDATE:(modify exist task from typetodo. Note field name must be in capital letters,i.e.TITLE,DESCRIPTION,DEADLINE,START,END,BUSY)\n"
			+ "(SYNTAX) edit <index of task on current list> <field name> <new value>\n";
	private static final String HELP_UNDO = "UNDO:(undo last operation. you may undo as many times as you want until the first operation you have committed)"
			+ "(SYNTAX) undo\n" + "(HOTKEY) <ctrl> + <backspace>\n";
	private static final String HELP_HOTKEY = "HOT-KEYS:(simple view application window and simple undo)\n"
			+ "(HIDE/RESTORE WINDOW) <ctrl> + <space>\n"
			+ "(UNDO) <ctrl> + <backspace>\n"
			+ "\n"
			+ "NAVIGATION MODE:\n"
			+ "(switch into navigation mode) <Esc>\n"
			+ "(scroll up) <UP>\n"
			+ "(scroll down) <DOWN>\n"
			+ "(previous page) <LEFT>"
			+ "(next page) <RIGHT>\n"
			+ "\n"
			+ "INPUT MODE:\n"
			+ "(switch into input mode) <i>\n"
			+ "(last operation) <UP>\n"
			+ "(next operation) <DOWN>\n";

	public CommandHelp(Schedule sc, CommandType helpType) {
		this.sc = sc;
		this.helpType = helpType;
	}

	public CommandHelp(Schedule sc) {
		this.sc = sc;
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

	@Override
	public String execute() throws Exception {
		String feedback;
		sc.help();
		if (helpType != null) {
			String TYPE_CATALOG = help(helpType);
			feedback = MESSAGE_HELP + TYPE_CATALOG;
		} else {
			feedback = HELP_INSTRUCTION;
		}
		return feedback;
	}
}
