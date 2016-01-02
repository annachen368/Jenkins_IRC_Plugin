package hudson.plugins.im.bot;

import java.util.logging.Logger;

import hudson.plugins.im.IMChat;
import hudson.plugins.im.IMException;
import hudson.plugins.im.IMMessage;
import hudson.plugins.im.Sender;
import hudson.plugins.im.tools.ExceptionHelper;

/**
 * Abstract command for sending a reply back to the sender.
 * 
 * @author kutzi
 */
public abstract class AbstractTextSendingCommand extends BotCommand {

	private static final Logger LOGGER = Logger.getLogger(AbstractTextSendingCommand.class.getName());

	private JobProvider jobProvider = new DefaultJobProvider();

	/**
	 * Get the JobProvider associated with the command object.
	 * 
	 * @return The JobProvider associated with the command object.
	 */
	protected JobProvider getJobProvider() {
		return this.jobProvider;
	}

	/**
	 * Change the JobProvider associated with the command object.
	 * 
	 * @param jobProvider
	 *            An instance of JobProcider that will be used by this command
	 *            object.
	 */
	void setJobProvider(JobProvider jobProvider) {
		this.jobProvider = jobProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void executeCommand(Bot bot, IMChat chat, IMMessage message, Sender sender, String[] args)
			throws IMException {
		String reply;
		try {
			reply = getReply(bot, sender, args);
		} catch (RuntimeException e) {
			LOGGER.warning(ExceptionHelper.dump(e));
			reply = sender.getNickname() + ": Error " + e.toString();
		}

		chat.sendMessage(reply);
	}

	/**
	 * Gets the text reply
	 * 
	 * @param bot
	 *            The bot for which this command is currently operating. Never
	 *            be null.
	 * @param sender
	 *            the command sender
	 * @param args
	 *            arguments passed to the command, where <code>args[0]</code> is
	 *            the command name itself @throws RuntimeException in case of
	 *            invalid arguments. This is automatically caught and reported
	 *            to the sender
	 */
	protected abstract String getReply(Bot bot, Sender sender, String args[]);

	/**
	 * Get the error text reply
	 * 
	 * @param sender
	 *            The command sender
	 * @param e
	 *            The Exception thrown during the execution
	 * @return The error string that will be sent back to chatting chanel
	 */
	protected String getErrorReply(Sender sender, CommandException e) {
		final StringBuilder reply;
		if (e.getReplyMessage() != null) {
			reply = new StringBuilder(e.getReplyMessage()).append("\n");
		} else {
			reply = new StringBuilder(sender.getNickname()).append(": command couldn't be executed. Error:\n");
		}
		if (e.getCause() != null) {
			reply.append("Cause: ").append(ExceptionHelper.dump(e.getCause()));
		}
		return reply.toString();
	}

}
