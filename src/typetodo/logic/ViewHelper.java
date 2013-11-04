package typetodo.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
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
		ArrayList<Task> floatingTasks = new ArrayList<Task>();
		HashMap<LocalDate, ArrayList<Task>> timedAndDeadlineTasks = new HashMap<LocalDate, ArrayList<Task>>();
	
		groupTasksByDates(tasks, timedAndDeadlineTasks, floatingTasks);
		
		ArrayList<LocalDate> sortedKeysInAscendingDate = sortKeySetByAscendingDate(timedAndDeadlineTasks.keySet());
		
		for (LocalDate date : sortedKeysInAscendingDate) {
			printDateHeading(date, sb);
			
			for (Task task : timedAndDeadlineTasks.get(date)) {
				if (isNewlyCreated(task) || isRecentlyModified(task)) {
					highlightTask(task, sb);
				} else if (task.getStatus().equals(Status.COMPLETED)) {
					strikeOutTask(task, sb);
				} else {
					sb.append(task);
				}
			}
			sb.append("<hr>");
		}

		if (!floatingTasks.isEmpty()) {
			printFloatingTaskHeading(sb);
		}

		for (Task task : floatingTasks) {
			if (isNewlyCreated(task) || isRecentlyModified(task)) {
				highlightTask(task, sb);
			} else if (task.getStatus().equals(Status.COMPLETED)) {
				strikeOutTask(task, sb);
			} else { 
				sb.append(task);
			}
		}

		return sb.toString();
	}

	private static void groupTasksByDates(ArrayList<Task> tasksToBeGrouped, 
			HashMap<LocalDate, ArrayList<Task>> timedAndDeadlineTasks, ArrayList<Task> floatingTasks) {

		for (Task task : tasksToBeGrouped) {
			LocalDate date = null;

			if (task instanceof DeadlineTask) {
				date = ((DeadlineTask) task).getDeadline().toLocalDate();
				if (timedAndDeadlineTasks.containsKey(date)) {
					timedAndDeadlineTasks.get(date).add(task);
				} else {
					timedAndDeadlineTasks.put(date, new ArrayList<Task>());
					timedAndDeadlineTasks.get(date).add(task);
				}
			} else if (task instanceof TimedTask) {
				TimedTask taskToBeDisplayed = (TimedTask) task.makeCopy();
				date = taskToBeDisplayed.getStart().toLocalDate();
				LocalDate endDate = ((TimedTask)task).getEnd().toLocalDate();
				
				if (date.isEqual(endDate)){
					if (timedAndDeadlineTasks.containsKey(date)) {
						timedAndDeadlineTasks.get(date).add(task);
					} else {
						timedAndDeadlineTasks.put(date, new ArrayList<Task>());
						timedAndDeadlineTasks.get(date).add(task);
					}
				} else {
					LocalTime endTime = taskToBeDisplayed.getEnd().toLocalTime();

					if (date.isBefore(endDate)) {
						taskToBeDisplayed.setEnd(date.toDateTime(LocalTime.parse("23:59"), null));
						if (timedAndDeadlineTasks.containsKey(date)) {
							timedAndDeadlineTasks.get(date).add(taskToBeDisplayed.makeCopy());

						} else {
							timedAndDeadlineTasks.put(date, new ArrayList<Task>());
							timedAndDeadlineTasks.get(date).add(taskToBeDisplayed.makeCopy());
						}
						
						date = date.plusDays(1);
					} else {
							
					}

					while (date.isBefore(endDate)) {
						taskToBeDisplayed.setStart(date.toDateTime(LocalTime.parse("00:00"), null));

						if (timedAndDeadlineTasks.containsKey(date)) {
							timedAndDeadlineTasks.get(date).add(taskToBeDisplayed.makeCopy());
						} else {
							timedAndDeadlineTasks.put(date, new ArrayList<Task>());
							timedAndDeadlineTasks.get(date).add(taskToBeDisplayed.makeCopy());
						}

						date = date.plusDays(1);
					}

					taskToBeDisplayed.setStart(date.toDateTime(LocalTime.parse("00:00"), null));
					taskToBeDisplayed.setEnd(date.toDateTime(endTime));

					if (timedAndDeadlineTasks.containsKey(date)) {
						timedAndDeadlineTasks.get(date).add(taskToBeDisplayed.makeCopy());
					} else {
						timedAndDeadlineTasks.put(date, new ArrayList<Task>());
						timedAndDeadlineTasks.get(date).add(taskToBeDisplayed.makeCopy());
					}
				}
			} else if (task instanceof FloatingTask) {
				floatingTasks.add(task);
			}
		}
	}
	
	private static ArrayList<LocalDate> sortKeySetByAscendingDate(Set<LocalDate> keySet) {
		ArrayList<LocalDate> sortedDates = new ArrayList<LocalDate>(keySet);
		Collections.sort(sortedDates, new LocalDateComparator());
		
		return sortedDates;
	}
	
	private static boolean isNewlyCreated(Task task) {
		DateTime dateCreated = task.getDateCreated();
		DateTime currentDateTime = new DateTime();

		return Seconds.secondsBetween(dateCreated, currentDateTime).isLessThan(Seconds.parseSeconds("PT5S"));
	}

	private static boolean isRecentlyModified(Task task) {
		DateTime dateModified = task.getDateModified();
		DateTime currentDateTime = new DateTime();

		return Seconds.secondsBetween(dateModified, currentDateTime).isLessThan(Seconds.parseSeconds("PT5S"));
	}

	private static void highlightTask(Task task, StringBuilder sb) {
		sb.append("<span style=\"background-color: #FFFF00\">");
		sb.append("<marker>");
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
		sb.append(task);
		sb.append("</font>");
		sb.append("</strike>");
	}
}
