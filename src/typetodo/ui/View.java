package typetodo.ui;

import java.util.ArrayList;

import typetodo.model.Task;

public interface View {
	public String getUserInput();
	
	public int getIndex();
	
	public void displayFeedBack(String feedBack);
	
	public void displayErrorMessage(String errorMessage);
	
	public void displayTasks(ArrayList<Task> tasks);
}
