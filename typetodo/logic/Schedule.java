package typetodo.logic;

import java.io.IOException;
import java.util.Date;

import typetodo.db.DBHandler;

public class Schedule {
	private DBHandler db;

	public Schedule() {
		try {
			db = new DBHandler();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public abstract class Task {
		private String name;
		private String description;

		public String getName() {
			return this.name;
		}

		public String getDescription() {
			return description;
		}
	}

	public class FloatingTask extends Task {
	}

	public class TimedTask extends Task {
		private int startTime;
		private int endTime;
		private String startDate;
		private String endDate;

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public int getStartTime() {
			return startTime;
		}

		public void setStartTime(int startTime) {
			this.startTime = startTime;
		}

		public int getEndTime() {
			return endTime;
		}

		public void setEndTime(int endTime) {
			this.endTime = endTime;
		}

	}

	public class DeadlineTask extends Task {
		private int endTime;
		private String endDate;

		public int getEndTime() {
			return endTime;
		}

		public void setEndTime(int endTime) {
			this.endTime = endTime;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
	}

	public String addFloatingTask(FloatingTask floatingTask) {
		String feedBack = null;
		return feedBack;
	}

	public String addTimedTask(TimedTask timedTask) {
		String feedBack = null;
		return feedBack;
	}

	public String addDeadlineTask(DeadlineTask deadLineTask) {
		String feedBack = null;
		return feedBack;
	}

	String deleteTask(Task task) {
		String feedBack = null;
		return feedBack;
	}

	public String viewMode(String mode) {
		String feedBack = null;
		if (mode.equals("today")) {
			feedBack = db.retrieveList(new Date());
		}
		return feedBack;
	}

	String editFloatingTask(FloatingTask floatingTask, String name,
			String description) {
		String feedBack = null;
		return feedBack;
	}

	String editTimedTask(TimedTask timedTask, String name, String description,
			int startTime, String startDate, int endTime, String endDate) {
		String feedBack = null;
		return feedBack;
	}

	String editDeadineTask(DeadlineTask deadlineTask, String name,
			String description, int endTime, int endDate) {
		String feedBack = null;
		return feedBack;
	}

}
