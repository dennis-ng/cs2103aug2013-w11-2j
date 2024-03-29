package typetodo.exception;

@SuppressWarnings("serial")
/**
 * Exception thrown if user input does not contain a mandatory field
 * @author A0091024U
 *
 */
public class MissingFieldException extends Exception{
    public MissingFieldException()
    {
    	super();
    }
    
    public MissingFieldException(String message)
    {
       super(message);
    }
}
