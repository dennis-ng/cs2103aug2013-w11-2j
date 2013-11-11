/**
 * 
 */
package typetodo.model;

import java.util.ArrayList;

/**
 * @author A0091024U
 * 
 */
public class View {
	private String feedBack;
	private ArrayList<Task> tasks;
	
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
		return tasks;
	}

	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int index;
		
		sb.append("FEEDBACK: " + this.feedBack + "\n");
		sb.append("TASKS:\n");
		
		for (int i = 0; i < this.tasks.size(); ++i) {
			index = i+1;
			sb.append(index + ". " + tasks.get(i));
		}
		
		return sb.toString();
	}
}
