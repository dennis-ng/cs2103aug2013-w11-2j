/**
 * 
 */
package texttodo.logic;

import java.util.ArrayList;

/**
 * This class is to encapsulate the relevant information that is to be
 * displayed to the user. I.e the feedback and tasks.
 * @author Phan Shi Yu
 * 
 */
public class View {
	private String feedBack;
	private ArrayList<Task> Tasks;
	
	public View(String feedBack, ArrayList<Task> tasks) {
		this.setFeedBack(feedBack);
		this.setTasks(tasks);
	}
	
	public String getFeedBack() {
		return feedBack;
	}
	
	public void setFeedBack(String feedBack) {
		this.feedBack = feedBack;
	}

	public ArrayList<Task> getTasks() {
		return Tasks;
	}

	public void setTasks(ArrayList<Task> tasks) {
		Tasks = tasks;
	}
}
