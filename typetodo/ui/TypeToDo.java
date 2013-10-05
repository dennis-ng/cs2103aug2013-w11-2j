package typetodo.ui;

import typetodo.logic.CommandParser;

public class TypeToDo {

	public static CommandParser parser;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		parser = new CommandParser();
		System.out.println(initialise());
		System.out.println(parser.parse("View today"));
	}

	public static String initialise() {
		return parser.initialise();
	}
}
