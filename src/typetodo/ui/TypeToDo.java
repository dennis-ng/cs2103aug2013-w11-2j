package typetodo.ui;

import java.io.IOException;

import typetodo.logic.Scheduler;

public class TypeToDo {
	public static void main(String[] args) throws IOException {
		Scheduler sc = new Scheduler(new CommandLineView());
		// sc.listenForCommands();
	}
}
