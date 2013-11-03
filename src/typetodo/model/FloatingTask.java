package typetodo.model;

public class FloatingTask extends Task {
	public FloatingTask(String name, String description) {
		super(name, description);
	}
	
	public FloatingTask(int taskId, String name, String description) {
		super(name, description);
		this.setTaskId(taskId);
	}
	
	public Task makeCopy() {
		return new FloatingTask(this.getTaskId(), this.getTitle(), this.getDescription());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<font face=\"century gothic\">");
		sb.append("<b>");
		sb.append(this.getTitle() + " ");
		sb.append("</b>");
		
		if (!this.getDescription().equals("")) {
			sb.append("\n");
			sb.append("<br>");
			sb.append("<i>");
			sb.append(" - " + this.getDescription().trim());
			sb.append("</i>");
		}
		sb.append("</font>");
		sb.append("<br>");
		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) {
	    	return false;
	    }
	    
	    if (other == this) {
	    	return true;
	    }
	    
	    if (!(other instanceof FloatingTask)) {
	    	return false;
	    	
	    } else if (other instanceof FloatingTask) {
	    	if (!((FloatingTask) other).getTitle().equals(this.getTitle())) {
	    		return false;
	    	}
	    	if (!((FloatingTask) other).getDescription().equals(this.getDescription())) {
	    		return false;
	    	}
	    	if (!((FloatingTask) other).getGoogleId().equals(this.getGoogleId())) {
	    		return false;
	    	}
	    	if (!((FloatingTask) other).getStatus().equals(this.getStatus())) {
	    		return false;
	    	}
	    	
	    	return true;
	    }
	  
	    return false;
	}
}