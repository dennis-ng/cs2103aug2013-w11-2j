package typetodo.ui;

public interface View {

	public void displayFeedBack(String feedBack);

	public void displayErrorMessage(String errorMessage);

	public void displayTasks(String tasks);

	public void displayHelp(String helpMessage);
}
