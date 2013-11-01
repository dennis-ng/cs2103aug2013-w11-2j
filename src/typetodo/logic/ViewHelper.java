package typetodo.logic;

import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Seconds;
import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import typetodo.model.DeadlineTask;
import typetodo.model.FloatingTask;
import typetodo.model.Task;
import typetodo.model.Task.Status;
import typetodo.model.TimedTask;

public class ViewHelper {
	
	public static String generateHTMLDisplayContent(ArrayList<Task> tasks) {
		StringBuilder sb = new StringBuilder();
		ArrayList<Task> floatingTask = new ArrayList<Task>();
		
		HashMap<LocalDate, ArrayList<Task>> tasksSortedByDate = new HashMap<LocalDate, ArrayList<Task>>();
	
		for (Task task : tasks) {
			LocalDate date = null;
			
			if (task instanceof DeadlineTask) {
				date = ((DeadlineTask) task).getDeadline().toLocalDate();
				
				if (tasksSortedByDate.containsKey(date)) {
					tasksSortedByDate.get(date).add(task);
				} else {
					tasksSortedByDate.put(date, new ArrayList<Task>());
					tasksSortedByDate.get(date).add(task);
				}
			} else if (task instanceof TimedTask) {
				date = ((TimedTask) task).getStart().toLocalDate();
				while (!date.isAfter(((TimedTask) task).getEnd().toLocalDate())) {
					if (tasksSortedByDate.containsKey(date)) {
						tasksSortedByDate.get(date).add(task);
					} else {
						tasksSortedByDate.put(date, new ArrayList<Task>());
						tasksSortedByDate.get(date).add(task);
					}
					date = date.plusDays(1);
				}
			} else if (task instanceof FloatingTask) {
				floatingTask.add(task);
			}
		}
	
		ArrayList<LocalDate> sortedDates = new ArrayList<LocalDate>(tasksSortedByDate.keySet());
		Collections.sort(sortedDates, new LocalDateComparator());
		
		for (LocalDate date : sortedDates) {
			printDateHeading(date, sb);
			
			for (Task task : tasksSortedByDate.get(date)) {
				if (isNewlyCreated(task)) {
					highlightTask(task, sb);
				} else if (task.getStatus().equals(Status.COMPLETED)) {
					strikeOutTask(task, sb);
				} else {
					sb.append("[Id: " + task.getTaskId() + "] ");
					sb.append(task);
				}
			}
			sb.append("<hr>");
		}

		if (!floatingTask.isEmpty()) {
			printFloatingTaskHeading(sb);
		}

		for (Task task : floatingTask) {
			if (isNewlyCreated(task)) {
				highlightTask(task, sb);
			} else if (task.getStatus().equals(Status.COMPLETED)) {
				strikeOutTask(task, sb);
			} else { 
				sb.append("[Id: " + task.getTaskId() + "] ");
				sb.append(task);
			}
		}

		return sb.toString();
	}

	private static boolean isNewlyCreated(Task task) {
		DateTime dateCreated = task.getDateCreated();
		DateTime currentDateTime = new DateTime();

		return Seconds.secondsBetween(dateCreated, currentDateTime).isLessThan(Seconds.parseSeconds("PT5S"));
	}
	
	private static void highlightTask(Task task, StringBuilder sb) {
		sb.append("<span style=\"background-color: #FFFF00\">");
		sb.append("<marker>");
		sb.append("[Id: " + task.getTaskId() + "] ");
		sb.append(task);
		sb.append("</span>");
	}
	
	private static void printDateHeading(LocalDate date, StringBuilder sb) {
		sb.append("<font face=\"century gothic\" size=\"6\" color=\"#4863A0\">");
		sb.append(date.toString("EEE, dd MMM yyyy"));
		sb.append("</font>");
		sb.append("<br>");
	}
	
	private static void printFloatingTaskHeading(StringBuilder sb) {
		sb.append("<font face=\"century gothic\" size=\"6\" color=\"#4863A0\">");
		sb.append("Floating Task");
		sb.append("</font>");
		sb.append("<br>");
	}
	
	private static void strikeOutTask(Task task, StringBuilder sb) {
		sb.append("<strike>");
		sb.append("[Id: " + task.getTaskId() + "] ");
		sb.append(task);
		sb.append("</strike>");
	}
}
