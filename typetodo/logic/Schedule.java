package texttodo.logic;

public class Schedule {
	
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
	
	String addFloatingTask(FloatingTask floatingTask) {
		String feedBack = null;
		return feedBack;
	}
	
	String addTimedTask(TimedTask timedTask) {
		String feedBack = null;
		return feedBack;
	}
	
	String addDeadlineTask(DeadlineTask deadLineTask) {
		String feedBack = null;
		return feedBack;
	}
	
	String deleteTask(Task task) {
		String feedBack = null;
		return feedBack;
	}
	
	String viewMode(String mode) {
		String feedBack = null;
		switch (mode) {
			case "today":
				break;
		}
		return feedBack;
	}
	
	String editFloatingTask(FloatingTask floatingTask, String name, String description) {
		String feedBack = null;
		return feedBack;
	}
	
	String editTimedTask(TimedTask timedTask, String name, String description, int startTime, String startDate, 
																				int endTime, String endDate) {
		String feedBack = null;
		return feedBack;
	}
	
	String editDeadineTask(DeadlineTask deadlineTask, String name, String description, int endTime, int endDate) {
		String feedBack = null;
		return feedBack;
	}
	
	
}

	