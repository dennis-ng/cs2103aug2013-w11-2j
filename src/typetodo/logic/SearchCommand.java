package typetodo.logic;

public class SearchCommand implements Command{
	Scheduler sc;
	String keyword;
	
	public SearchCommand(Scheduler sc, String keyword) {
		this.sc = sc;
		this.keyword = keyword;
	}
	public void execute() throws Exception {
		sc.search(keyword);
	}
}
