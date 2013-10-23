package typetodo.ui;

import java.util.ArrayList;
import java.util.Scanner;

import typetodo.model.Task;

public class CommandLineView implements View{
	Scanner sc;
	
	public CommandLineView() {
		sc = new Scanner(System.in);
	}
	
	public String getUserInput() {
		return sc.nextLine();
	}
	
	public int getIndex() {
		return sc.nextInt();
	}
	
	public void displayFeedBack(String feedBack) {
		System.out.println("FEEDBACK: " + feedBack);
	}
	
	public void displayErrorMessage(String errorMessage) {
		System.out.println(errorMessage);
	}
	
	public void displayTasks(ArrayList<Task> tasks) {
		int index = 1;
		for(Task task : tasks) {
			System.out.println(index + ". " + task);
			index++;
		}
	}
	@Override
	public void displayHelp(String helpMessage) {
		System.out.println(helpMessage);
	}
	
}
