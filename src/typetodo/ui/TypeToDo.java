package typetodo.ui;

import java.io.IOException;

import typetodo.logic.ScheduleController;

public class TypeToDo {
	public static void main(String[] args) throws IOException {
		ScheduleController sc = new ScheduleController(new CommandLineView());
		sc.listenForCommands();
	}
}
