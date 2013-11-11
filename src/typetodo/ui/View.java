package typetodo.ui;

public interface View {

	public boolean disableInput();

	public boolean enableInput();

	public void displayFeedBack(String feedBack);

	public void displayErrorMessage(String errorMessage);

	public void displayTasks(String tasks);

	public void displayHelp(String helpMessage);
}
