package weboapps.crashsense;

/**
 * @author webonise
 *	This class is the model representation of the response getting from the server.
 *	Class has two variables status(boolean value) , message(string value).
 *	Class contains the getter and setter method to get and set the object values.
 */
public class ResponseModel {
	boolean status;
	String message;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
