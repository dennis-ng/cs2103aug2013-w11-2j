package typetodo.logic;
@SuppressWarnings("serial")

public class InvalidDateTimeException extends Exception {
	public InvalidDateTimeException(){
		super();
	}
	
	public InvalidDateTimeException(String message) {
		super(message);
	}
}
