package hudson.plugins.im.bot;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.plugins.im.Sender;
import hudson.plugins.im.tools.MessageHelper;
import hudson.security.Permission;

/**
 * Comment Command for the instant messaging bot.
 *
 */
@Extension
public class CommentCommand extends AbstractSingleJobCommand {

	/**
	 * Create a new instance of CommentCommand.
	 */
	public CommentCommand() {
		super(2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getCommandNames() {
		return Collections.singleton("comment");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CharSequence getMessageForJob(AbstractProject<?, ?> job, Sender sender, String[] args)
			throws CommandException {

		try {
			int buildNumber = Integer.parseInt(args[0]);
			Run<?, ?> build = job.getBuildByNumber(buildNumber);
			if (build == null) {
				throw new CommandException("sender: there is no build with number " + args[0] + "!");
			}

			build.setDescription(MessageHelper.join(args, 1));
			return "Ok";
		} catch (NumberFormatException e) {
			throw new CommandException("sender: " + args[0] + " is no valid build number!");
		} catch (IOException e) {
			throw new CommandException("Error setting comment: ", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Permission getRequiredPermission() {
		return Item.CONFIGURE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHelp() {
		return " <job> <build-#> <comment> - adds a description to a build";
	}
}
