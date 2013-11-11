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

/**
 * This class is used to format Tasks with html tags such that it is ready for display to the users
 * @author A0091024U
 *
 */
public class TasksFormatter {
	
	/**
	 * Takes in a list of tasks and format them for display with the use of html tags
	 * @param tasks
	 * @return returns a String of the formated tasks, ready for display
	 */
	public static String formatTasks(ArrayList<Task> tasks) {
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
					sb.append(formatTask(task));
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
				sb.append(formatTask(task));
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
				addTaskToHashMap(timedAndDeadlineTasks, date, task);
			} else if (task instanceof TimedTask) {
				TimedTask taskToBeDisplayed = (TimedTask) task.makeCopy();
				date = taskToBeDisplayed.getStart().toLocalDate();
				LocalDate endDate = ((TimedTask)task).getEnd().toLocalDate();
				
				if (date.isEqual(endDate)){
					addTaskToHashMap(timedAndDeadlineTasks, date, task);
				} else {
					LocalTime endTime = taskToBeDisplayed.getEnd().toLocalTime();
					date = addTimedTaskToitsStartDate(timedAndDeadlineTasks, date, endDate, taskToBeDisplayed);
					date = addTimedTaskToAllDatesBetweenItsStartAndEnd(timedAndDeadlineTasks, date, endDate, taskToBeDisplayed);
					addTimedTaskToItsEndDate(timedAndDeadlineTasks, date, endDate, endTime, taskToBeDisplayed);
				}
			} else if (task instanceof FloatingTask) {
				floatingTasks.add(task);
			}
		}
	}
	
	private static String formatTask(Task task) {
		if (task instanceof FloatingTask) {
			return formatFloatingTask((FloatingTask) task);
		} else if (task instanceof DeadlineTask) {
			return formatDeadlineTask((DeadlineTask) task);
		} else if (task instanceof TimedTask) {
			return formatTimedTask((TimedTask) task);
		}
		
		return "";
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
		sb.append(formatTask(task));
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
		sb.append(formatTask(task));
		sb.append("</font>");
		sb.append("</strike>");
	}
	
	private static LocalDate addTimedTaskToitsStartDate(HashMap<LocalDate, ArrayList<Task>> tasksMap, LocalDate date, LocalDate endDate, TimedTask task) {
		if (date.isBefore(endDate)) {
			task.setEnd(date.toDateTime(LocalTime.parse("23:59"), null));
			addTaskToHashMap(tasksMap, date, task.makeCopy());
			date = date.plusDays(1);
		}
		
		return date;
	}
	
	private static LocalDate addTimedTaskToAllDatesBetweenItsStartAndEnd(HashMap<LocalDate, ArrayList<Task>> tasksMap, LocalDate date, LocalDate endDate, TimedTask task) {
		while (date.isBefore(endDate)) {
			task.setStart(date.toDateTime(LocalTime.parse("00:00"), null));
			addTaskToHashMap(tasksMap, date, task.makeCopy());

			date = date.plusDays(1);
		}
		return date;
	}
	
	private static void addTimedTaskToItsEndDate(HashMap<LocalDate, ArrayList<Task>> tasksMap, LocalDate date, LocalDate endDate, LocalTime endTime, TimedTask task) {
		task.setStart(date.toDateTime(LocalTime.parse("00:00"), null));
		task.setEnd(date.toDateTime(endTime));
		addTaskToHashMap(tasksMap, date, task.makeCopy());
	}
	
	private static void addTaskToHashMap(HashMap<LocalDate, ArrayList<Task>> tasksMap, LocalDate date, Task task) {
		if (tasksMap.containsKey(date)) {
			tasksMap.get(date).add(task);
		} else {
			tasksMap.put(date, new ArrayList<Task>());
			tasksMap.get(date).add(task);
		}
	}
	
	private static String formatFloatingTask(FloatingTask floatingTask) {
		StringBuilder sb = new StringBuilder();
		sb.append("<font face=\"century gothic\">");
		sb.append("[Id: " + floatingTask.getTaskId() + "] ");
		sb.append("<b>");
		sb.append(floatingTask.getTitle() + " ");
		sb.append("</b>");
		
		if (floatingTask.getDescription() != null && !floatingTask.getDescription().equals("")) {
			sb.append("\n");
			sb.append("<br>");
			sb.append("<i>");
			sb.append(" - " + floatingTask.getDescription().trim());
			sb.append("</i>");
		}
		sb.append("</font>");
		sb.append("<br>");
		
		return sb.toString();
	}
	
	private static String formatDeadlineTask(DeadlineTask deadlineTask) {
		StringBuilder sb = new StringBuilder();
		
		if (deadlineTask.getDeadline().isBefore(new DateTime())) {
			sb.append("<font face=\"century gothic\" color=\"#B6B6B4\">");
			sb.append("[Id: " + deadlineTask.getTaskId() + "] ");
			sb.append("<b>");
			sb.append(deadlineTask.getTitle() + " ");
			sb.append("</b>");
			
			sb.append("due at ");
			sb.append("<b>");
			sb.append(deadlineTask.getDeadline().toString("HH:mm"));
			sb.append("</b>");
		} else {
			sb.append("<font face=\"century gothic\">");
			sb.append("[Id: " + deadlineTask.getTaskId() + "] ");
			sb.append("<b>");
			sb.append(deadlineTask.getTitle() + " ");
			sb.append("</b>");

			sb.append("due at ");
			sb.append("<font face=\"century gothic\" color=\"red\">");
			sb.append("<b>");
			sb.append(deadlineTask.getDeadline().toString("HH:mm"));
			sb.append("</b>");
			sb.append("</font>");
		}

		if (deadlineTask.getDescription() != null && !deadlineTask.getDescription().equals("")) {
			sb.append("\n");
			sb.append("<br>");
			sb.append("<i>");
			sb.append(" - " + deadlineTask.getDescription().trim());
			sb.append("</i>");
		}
		sb.append("</font>");
		sb.append("<br>");
		
		return sb.toString();
	}
	
	private static String formatTimedTask(TimedTask timedTask) {
		StringBuilder sb = new StringBuilder();

		if (timedTask.getEnd().isBefore(new DateTime())) {
			sb.append("<font face=\"century gothic\" color=\"#B6B6B4\">");
			sb.append("[Id: " + timedTask.getTaskId() + "] ");
			sb.append("<b>");
			sb.append(timedTask.getTitle() + " ");
			sb.append("</b>");

			sb.append("from ");
			sb.append("<b>");
			sb.append(timedTask.getStart().toString("HH:mm"));
			sb.append(" to ");
			sb.append(timedTask.getEnd().toString("HH:mm"));
			sb.append("</b>");
		} else {
			sb.append("<font face=\"century gothic\">");
			sb.append("[Id: " + timedTask.getTaskId() + "] ");
			sb.append("<b>");
			sb.append(timedTask.getTitle() + " ");
			sb.append("</b>");

			sb.append("from ");
			sb.append("<font face=\"century gothic\" color=\"red\">");
			sb.append("<b>");
			sb.append(timedTask.getStart().toString("HH:mm"));
			sb.append(" to ");
			sb.append(timedTask.getEnd().toString("HH:mm"));
			sb.append("</b>");
			sb.append("</font>");
		}

		if (timedTask.getDescription() != null && !timedTask.getDescription().equals("")) {
			sb.append("\n");
			sb.append("<br>");
			sb.append("<i>");
			sb.append(" - " + timedTask.getDescription().trim());
			sb.append("</i>");
		}
		sb.append("</font>");
		sb.append("<br>");
		
		return sb.toString();
	}
}
