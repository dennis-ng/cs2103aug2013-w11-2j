package typetodo.logic;

public class SearchCommand implements Command{
	ScheduleController sc;
	String keyword;
	
	public SearchCommand(ScheduleController sc, String keyword) {
		this.sc = sc;
		this.keyword = keyword;
	}
	public void execute() throws Exception {
		sc.search(keyword);
	}
}
