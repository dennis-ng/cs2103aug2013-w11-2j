package typetodo.logic;

public class SearchCommand implements Command{
	private static final String MESSAGE_SEARCH = "Displaying all tasks containing \"%s\"";
	private Schedule sc;
	private String keyword;
	
	public SearchCommand(Schedule sc, String keyword) {
		this.sc = sc;
		this.keyword = keyword;
	}
	public String execute() throws Exception {
		String feedback = MESSAGE_SEARCH;
		sc.search(keyword);
		return feedback;
	}
}
