package hudson.plugins.im.bot;

/**
 * Is thrown when a bot command couldn't be executed.
 *
 * @author
 */
public class CommandException extends Exception {

	private static final long serialVersionUID = 1L;
	private String replyMessage;

	/**
	 * Create an Exception with specific message.
	 * 
	 * @param message
	 *            The message thrown with the Exception.
	 */
	public CommandException(String message) {
		super(message);
		this.replyMessage = message;
	}

	/**
	 * Create an Exception with specific cause
	 * 
	 * @param cause
	 *            The cause of the exception
	 */
	public CommandException(Throwable cause) {
		super(cause);
		this.replyMessage = null;
	}

	/**
	 * Create an Exception with both message and cause
	 * 
	 * @param message
	 *            The cause of the exception
	 * @param cause
	 *            The cause of the exception
	 */
	public CommandException(String message, Throwable cause) {
		super(message, cause);
		this.replyMessage = message;
	}

	/**
	 * Retrieve the message associated with the Exception
	 * 
	 * @return The message associated with the Exception
	 */
	public String getReplyMessage() {
		return replyMessage;
	}
}
